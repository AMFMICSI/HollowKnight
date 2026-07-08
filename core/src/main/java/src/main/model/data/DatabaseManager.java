package src.main.model.data;

import com.badlogic.gdx.Gdx;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_PATH = "config/save_data.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void connect() {
        if (connection != null) return;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + Gdx.files.local(DB_PATH).path());
            initTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS save_data (" +
                "slot INT PRIMARY KEY, " +
                "data_json TEXT NOT NULL" +
            ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveGame(int slot, String dataJson) {
        connect();
        String sql = "INSERT OR REPLACE INTO save_data (slot, data_json) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, slot);
            ps.setString(2, dataJson);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String loadGame(int slot) {
        connect();
        String sql = "SELECT data_json FROM save_data WHERE slot = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, slot);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("data_json");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean slotExists(int slot) {
        connect();
        String sql = "SELECT COUNT(*) FROM save_data WHERE slot = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, slot);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteSlot(int slot) {
        connect();
        String sql = "DELETE FROM save_data WHERE slot = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, slot);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
