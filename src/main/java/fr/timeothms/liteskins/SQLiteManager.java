package fr.timeothms.liteskins;

import com.destroystokyo.paper.profile.ProfileProperty;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLiteManager {
    private static final String DATABASE_URL = "jdbc:sqlite:plugins/LiteSkins/skins.db";
    private Connection connection;

    public SQLiteManager() {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(DATABASE_URL);


            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to initialize SQLite database", e);
        }
    }

    public void createTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS skins (" +
                "player_uuid TEXT PRIMARY KEY, " +
                "source_type TEXT NOT NULL," +
                "source_value TEXT NOT NULL," +
                "skin_value TEXT NOT NULL, " +
                "skin_signature TEXT NOT NULL)";
        executeUpdate(createTableSQL);
    }

    public void executeUpdate(String sql) {
        try (Statement stmt = connection.createStatement()) {
            // Exécuter la requête SQL
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'exécution de la requête SQL", e);
        }
    }

    public void saveSkinToDatabase(String playerUuid, String sourceType, String sourceValue, String skinValue, String skinSignature) {
        String insertSQL = "INSERT OR REPLACE INTO skins (player_uuid, source_type, source_value, skin_value, skin_signature) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, playerUuid);
            pstmt.setString(2, sourceType);
            pstmt.setString(3, sourceValue);
            pstmt.setString(4, skinValue);
            pstmt.setString(5, skinSignature);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving skin to database", e);
        }
    }

    public ProfileProperty getSkinFromDatabase(String playerUuid) {
        String selectSQL = "SELECT skin_value, skin_signature FROM skins WHERE player_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, playerUuid);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                String value = rs.getString("skin_value");
                String signature = rs.getString("skin_signature");
                return new ProfileProperty("textures", value, signature);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving skin from database", e);
        }
        return null;
    }

    public Map<String, String> getSourceDetails(String playerUuid) {
        Map<String, String> sourceDetails = new HashMap<>();

        String selectSQL = "SELECT source_type, source_value FROM skins WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, playerUuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String sourceType = rs.getString("source_type");
                    String sourceValue = rs.getString("source_value");
                    sourceDetails.put("source_type", sourceType);
                    sourceDetails.put("source_value", sourceValue);
                } else {
                    // Si le joueur n'existe pas dans la base de données
                    sourceDetails.put("source_type", "unknown");
                    sourceDetails.put("source_value", "unknown");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching source details from database", e);
        }

        return sourceDetails;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la fermeture de la connexion à la base de données", e);
        }
    }
}
