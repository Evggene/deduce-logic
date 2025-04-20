package infrastructure;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Collections;

@TestConfiguration
public class PostgreSqlTestConfiguration {

    @Value("${test.datasource.postgres.version:13.5}")
    private String postgresVersion;

    @Value("${test.datasource.timezone:Asia/Novosibirsk}")
    private String timezone;

    @Value("${test.datasource.embedded:false}")
    private boolean embedded;

    @Bean(destroyMethod = "stop")
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> postgreSQLContainer =
                new PostgreSQLContainer<>("postgres:" + postgresVersion)
                        .withDatabaseName("db")
                        .withExposedPorts(5432)
                        .withUsername("user")
                        .withPassword("pass")
                        .withEnv("TZ", timezone);
        if (embedded) {
            postgreSQLContainer = postgreSQLContainer
                    .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));
        }
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    @Primary
    @Bean(destroyMethod = "close")
    public DataSource dataSource(PostgreSQLContainer<?> postgreSQLContainer) {
        return DataSourceBuilder.create().type(HikariDataSource.class)
                .url(postgreSQLContainer.getJdbcUrl() + "&stringtype=unspecified")
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .build();
    }
}
