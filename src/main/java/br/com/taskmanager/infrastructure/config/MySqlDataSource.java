package br.com.taskmanager.infrastructure.config;

import br.com.taskmanager.infrastructure.exceptions.RequiredEnvFieldException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

public class MySqlDataSource extends HikariDataSource {

    public MySqlDataSource(Dotenv dotenv) {
        super(config(dotenv));
    }

    private static HikariConfig config(Dotenv dotenv) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(required(dotenv.get("DB_URL"), "DB_URL"));
        config.setUsername(required(dotenv.get("DB_USER"), "DB_USER"));
        config.setPassword(required(dotenv.get("DB_PASSWORD"), "DB_PASSWORD"));
        return config;
    }

    private static String required(String value, String name){
        if (value == null){
            throw new RequiredEnvFieldException(name);
        }
        return value;
    }
}
