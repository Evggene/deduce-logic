package deduction.db;

import deduction.db.domains.RulesDB;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

import java.util.List;

public interface RulesDBMapper {
    @Results(value = {
    @Result(property = "id", column = "id")})
    int insertRulesDB(RulesDB rulesDB);
    List<RulesDB> getRulesDBByRefModel(int ref_model);

}
