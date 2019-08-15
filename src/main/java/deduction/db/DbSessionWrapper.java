package deduction.db;

import deduction.SerializerException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class DbSessionWrapper {

    private SqlSession sqlSession;

    DbSessionWrapper(SqlSessionFactory ssf) {
        sqlSession = ssf.openSession(false);
    }

    public <T> T getMapper(Class<T> c) throws SerializerException {
        if (sqlSession == null)
            throw new SerializerException("Improper call of Serializer method");
        return sqlSession.getMapper(c);
    }

    public void commit() throws SerializerException {
        if (sqlSession == null)
            throw new SerializerException("Improper call of Serializer method");
        sqlSession.commit();
    }

    public void close() {
        sqlSession.close();
        sqlSession = null;
    }
}
