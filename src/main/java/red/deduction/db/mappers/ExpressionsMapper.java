package red.deduction.db.mappers;

import org.apache.ibatis.annotations.Mapper;
import red.deduction.db.dto.ExpressionsDTO;
import java.util.List;

@Mapper
public interface ExpressionsMapper {
    void insert(ExpressionsDTO expressionDTO);
    void deleteExpression(int id);
    List<ExpressionsDTO> getChildExpressions(int expression_id);
}
