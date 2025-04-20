package red;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import red.deduction.db.mappers.ExpressionsMapper;

@SpringBootTest
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

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("postgres")
            .withUsername("myuser")
            .withPassword("mypass");

}
