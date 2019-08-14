package deduction.db;

import deduction.SerializerException;
import deduction.db.dto.DTO;
import deduction.db.mappers.Mapper;
import deduction.db.mappers.ModelMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class DbWriterWrapper {

    private SqlSession sqlSession;

    DbWriterWrapper(SqlSessionFactory ssf) {
        sqlSession = ssf.openSession(false);
    }

    public <T> T getMapper(Class<T> c) throws SerializerException {
        if (sqlSession == null)
            throw new SerializerException("Improper call of Serializer method");

        return sqlSession.getMapper(c);
    }

    public void insert(Mapper mapper, DTO dto) throws SerializerException {
        if (sqlSession == null)
            throw new SerializerException("Improper call of Serializer method");

        mapper.insert(dto);
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
