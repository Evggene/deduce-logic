package red.infrastructure;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {"red"},
        annotationClass = Mapper.class,
        sqlSessionFactoryRef = "sqlSessionFactory")
public class MyBatisConfiguration {

    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        var factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
        Properties properties = new Properties();
        factory.setConfigurationProperties(properties);
        return factory.getObject();
    }

}

