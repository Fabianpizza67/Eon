package com.Fabian.eon.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private HikariDataSource dataSource;

    public void connect(String host, String port, String database, String user, String password) {
        try {
            // Force the JVM to load the MariaDB driver
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB Driver not found in the JAR!");
            e.printStackTrace();
            return;
        }

        HikariConfig config = new HikariConfig();
        // Use the "mariadb" specific driver string
        config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);

        // NUC Optimization: Connection Pool Settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);

        dataSource = new HikariDataSource(config);
        System.out.println(">> Eon Database Connected via HikariCP");

        initTables();
    }

    private void initTables() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            // 1. The Ship Registry
            stmt.execute("CREATE TABLE IF NOT EXISTS eon_ships (" +
                    "ship_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "owner_uuid VARCHAR(36), " +
                    "ship_name VARCHAR(64), " +
                    "integrity DOUBLE DEFAULT 100.0, " +
                    "shield_hp DOUBLE DEFAULT 0.0, " +
                    "is_in_warp BOOLEAN DEFAULT FALSE" +
                    ");");

            // 2. The Black Box Logs
            stmt.execute("CREATE TABLE IF NOT EXISTS eon_blackbox (" +
                    "log_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "ship_id INT, " +
                    "destroyed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "cause TEXT, " +
                    "attacker_name VARCHAR(64)" +
                    ");");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}