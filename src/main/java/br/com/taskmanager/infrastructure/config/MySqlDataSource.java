package br.com.taskmanager.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

public class MySqlDataSource extends HikariDataSource {

    public MySqlDataSource(Dotenv dotenv) {
        super(config(dotenv));
    }

    private static HikariConfig config(Dotenv dotenv) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dotenv.get("DB_URL"));
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));
        return config;
    }
}
