package fr.timeothms.liteskins;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MojangAPI {

    private static final String MOJANG_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%username%";
    private static final String MOJANG_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%uuid%?unsigned=false";

    private static final ExecutorService executorService = Executors.newCachedThreadPool(); // Utilisation d'un pool de threads

    public static void fetchUUIDFromUsernameAsync(String username, Callback<String> callback) {
        String urlString = MOJANG_UUID_URL.replace("%username%", username);

        executorService.submit(() -> {
            try {
                String json = fetchJson(urlString);
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                String uuid = (String) jsonObject.get("id");
                callback.onSuccess(uuid);
            } catch (IOException | ParseException | InterruptedException e) {
                callback.onFailure(e);
            }
        });
    }

    public static void fetchSkinFromUuidAsync(String uuid, String key, Callback<String> callback) {
        String urlString = MOJANG_PROFILE_URL.replace("%uuid%", uuid);

        executorService.submit(() -> {
            try {
                String json = fetchJson(urlString);
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                JSONArray propertiesArray = (JSONArray) jsonObject.get("properties");
                JSONObject propertiesObject = (JSONObject) propertiesArray.get(0);
                String value = (String) propertiesObject.get(key);
                callback.onSuccess(value);
            } catch (IOException | ParseException | InterruptedException e) {
                callback.onFailure(e);
            }
        });
    }

    public static String fetchJson(String urlString) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else if (response.statusCode() == 404) {
            throw new IOException("Le joueur n'a pas été trouvé (404 Not Found).");
        } else {
            throw new IOException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
        }
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}
