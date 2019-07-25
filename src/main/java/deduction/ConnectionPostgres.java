package deduction;

import deduction.db.ModelDBMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPostgres {

    private static SqlSessionFactory sqlSessionFactory;

    private ConnectionPostgres() {
    }

    private static Connection connection = null;
    private static final String url = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "postgres";
    private static final String pwd = "password";

    public static Connection connect() throws SQLException {
        if (connection == null) {
            return DriverManager.getConnection(url, user, pwd);
        } else {
            return connection;
        }
    }


    public static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            InputStream inputStream;
            try {
                inputStream = Resources.
                        getResourceAsStream("mybatis-config.xml");
                sqlSessionFactory = new
                        SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return sqlSessionFactory;
    }

    public static SqlSession openSession() {
        return getSqlSessionFactory().openSession();
    }


}
