package fr.timeothms.liteskins;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static fr.timeothms.liteskins.LiteSkins.config;

public class MineskinClient {
    private static final String API_KEY = config.getString("mineskin.api-key");
    private static final String BASE_URL = "https://api.mineskin.org/generate/url";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static Future<Map<String, String>> getSkinValueAndSignatureAsync(String skinUrl) {
        return executorService.submit(new Callable<Map<String, String>>() {
            @Override
            public Map<String, String> call() {
                Map<String, String> result = new HashMap<>();

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("User-Agent", "MyMineSkinApp/v1.0")
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"url\": \"" + skinUrl + "\", \"name\": \"My Skin\", \"visibility\": 0}"))
                        .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    int responseCode = response.statusCode();

                    if (responseCode == 200) {
                        JSONParser parser = new JSONParser();
                        JSONObject responseJson = (JSONObject) parser.parse(new StringReader(response.body()));
                        JSONObject dataNode = (JSONObject) responseJson.get("data");
                        if (dataNode != null) {
                            JSONObject textureNode = (JSONObject) dataNode.get("texture");
                            if (textureNode != null) {
                                String value = (String) textureNode.getOrDefault("value", "");
                                String signature = (String) textureNode.getOrDefault("signature", "");
                                result.put("value", value);
                                result.put("signature", signature);
                            }
                        }
                    } else {
                        System.err.println("Error: " + responseCode);
                        System.err.println(response.body());
                    }
                } catch (IOException | InterruptedException | ParseException e) {
                    e.printStackTrace();
                }

                return result;
            }
        });
    }
}
