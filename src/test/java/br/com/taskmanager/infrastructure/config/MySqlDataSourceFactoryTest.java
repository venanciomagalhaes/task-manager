package br.com.taskmanager.infrastructure.config;

import br.com.taskmanager.infrastructure.exceptions.RequiredEnvFieldException;
import com.zaxxer.hikari.HikariConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySqlDataSourceFactoryTest {

    @Mock
    private Dotenv dotenv;

    @Test
    @DisplayName("lança RequiredEnvFieldException quando DB_URL não está definida")
    void lancaExcecaoQuandoDbUrlAusente() {
        when(dotenv.get("DB_URL")).thenReturn(null);

        RequiredEnvFieldException exception = assertThrows(RequiredEnvFieldException.class,
                () -> MySqlDataSourceFactory.create(dotenv));

        assertEquals("A variável de ambiente DB_URL é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("lança RequiredEnvFieldException quando DB_USER não está definida")
    void lancaExcecaoQuandoDbUserAusente() {
        when(dotenv.get("DB_URL")).thenReturn("jdbc:mysql://localhost:3306/taskmanager");
        when(dotenv.get("DB_USER")).thenReturn(null);

        RequiredEnvFieldException exception = assertThrows(RequiredEnvFieldException.class,
                () -> MySqlDataSourceFactory.create(dotenv));

        assertEquals("A variável de ambiente DB_USER é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("lança RequiredEnvFieldException quando DB_PASSWORD não está definida")
    void lancaExcecaoQuandoDbPasswordAusente() {
        when(dotenv.get("DB_URL")).thenReturn("jdbc:mysql://localhost:3306/taskmanager");
        when(dotenv.get("DB_USER")).thenReturn("taskmanager");
        when(dotenv.get("DB_PASSWORD")).thenReturn(null);

        RequiredEnvFieldException exception = assertThrows(RequiredEnvFieldException.class,
                () -> MySqlDataSourceFactory.create(dotenv));

        assertEquals("A variável de ambiente DB_PASSWORD é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("monta a configuração quando todas as variáveis estão definidas")
    void montaConfiguracaoComVariaveisDefinidas() {
        when(dotenv.get("DB_URL")).thenReturn("jdbc:mysql://localhost:3306/taskmanager");
        when(dotenv.get("DB_USER")).thenReturn("taskmanager");
        when(dotenv.get("DB_PASSWORD")).thenReturn("secret");

        HikariConfig config = MySqlDataSourceFactory.config(dotenv);

        assertEquals("jdbc:mysql://localhost:3306/taskmanager", config.getJdbcUrl());
        assertEquals("taskmanager", config.getUsername());
        assertEquals("secret", config.getPassword());
    }
}
