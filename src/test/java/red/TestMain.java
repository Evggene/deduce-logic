package red;

import infrastructure.PostgreSqlTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import red.deduction.db.mappers.ExpressionsMapper;
import red.infrastructure.DataSourceConfiguration;
import red.infrastructure.FlywayConfig;
import red.infrastructure.FlywayRunner;
import red.infrastructure.MyBatisConfiguration;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = { "classpath:application-test.properties" })
@SpringBootTest(classes = {
        Main.class,
        FlywayConfig.class,
        MyBatisConfiguration.class,
        PostgreSqlTestConfiguration.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TestMain {

    @Autowired
    private ExpressionsMapper expressionsMapper;
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void test_startApplication_contextLoads() {
        Assertions.assertNotNull(applicationContext);
    }

}
