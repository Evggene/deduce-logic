package deduction.writer;

import deduction.ConnectionPostgres;
import deduction.db.KnownfactsMapper;
import deduction.db.ModelDBMapper;
import deduction.db.RuleDBMapper;
import deduction.db.RulesDBMapper;
import deduction.db.domains.Knownfacts;
import deduction.db.domains.RuleDB;
import deduction.db.domains.RulesDB;
import deduction.model.Model;
import deduction.model.Rule;
import deduction.model.expression.Expression;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class WriterDB implements Writer {

    private SqlSessionFactory ssf = ConnectionPostgres.getSqlSessionFactory();
    private SqlSession session = ssf.openSession();

    @Override
    public void convert(String modelName, Model model) throws Exception {
        Connection connection = ConnectionPostgres.connect();
        Statement statement = connection.createStatement();


        ModelDBMapper modelSession = session.getMapper(ModelDBMapper.class);


        modelSession.insertNameInModel(modelName);
        session.commit();

        int modelId = modelSession.getModelByName(modelName).getId();


        //statement.execute("INSERT INTO model (name) VALUES('" + modelName + "')");
        // ResultSet modelIdSet = statement.executeQuery("SELECT id FROM model where name = '" + modelName + "'");


        //while (modelIdSet.next()) {
        //    modelId = modelIdSet.getInt(1);
        //}

        KnownfactsMapper knownFactsSession = session.getMapper(KnownfactsMapper.class);
        for (String fact : model.getKnownFactsList()) {
            knownFactsSession.insertKnownFacts(new Knownfacts(modelId, fact));
            session.commit();

            // statement.execute("INSERT INTO knownfacts (ref_model, fact) VALUES('" + modelId + "', '" + fact + "')");
        }


        RulesDBMapper rulesDBSession = session.getMapper(RulesDBMapper.class);
        for (Rule rule : model.getRulesList()) {

            RulesDB rulesDB = new RulesDB(rule.getResultFact(), modelId);
            rulesDBSession.insertRulesDB(rulesDB);
            session.commit();

            //System.out.println(rulesDB.getId());

            //System.out.println(i);
            //List<RulesDB> rulesDBList = rulesDBSession.getRulesDBByRefModel(modelId);

            //System.out.println();

//            statement.execute("INSERT INTO public.rules (result_fact, ref_model) " +
//                    "VALUES('" + rule.getResultFact() + "', '" + modelId + "')");
//
//            ResultSet rulesIdSet = statement.executeQuery("SELECT id FROM rules where ref_model = '" + modelId + "'");
//
//            int rulesId = 0;
//            while (rulesIdSet.next()) {
//                rulesId = rulesIdSet.getInt(1);
//            }
//            System.out.println(rulesId);
            serializeExpression(rule, statement, rulesDB.getId());
        }
    }


    private void serializeExpression(Rule rule, Statement statement, int ruleId) throws SQLException {

        if (rule.getExpression().getStringPresentation().equals("Fact")) {
            RuleDBMapper ruleDBSession = session.getMapper(RuleDBMapper.class);
            ruleDBSession.insertRootFact(new RuleDB(ruleId, 1, null, rule.getExpression().toString(), null));
            session.commit();
//            statement.execute("INSERT INTO rule (ref_rules, element_num, parent_id, fact, node) " +
//                    "VALUES('" + ruleId + "', 1, null, '" + rule.getExpression() + "', null)");
            return;

        }
        serializeExpression(rule.getExpression(), statement, ruleId, 1, null);
    }


    private void serializeExpression(Expression ex, Statement statement, int id_, int elementNumber_, Integer parentId_) throws SQLException {
        RuleDBMapper ruleDBSession = session.getMapper(RuleDBMapper.class);
        int elementNumber = elementNumber_;
        Integer parentId = parentId_;

        if (ex.getStringPresentation().equals("Fact")) {

            ruleDBSession.insertFact(new RuleDB(id_, elementNumber, parentId, ex.getStringPresentation(), null));
            session.commit();
//            statement.execute("INSERT INTO rule (ref_rules, element_num, parent_id, fact, node) " +
//                    "VALUES(" + id_ + ", " + elementNumber + ", " + parentId + ", '" + ex.getStringPresentation() + "', null)");
        } else {
            ruleDBSession.insertNode(new RuleDB(id_, elementNumber, parentId, null, ex.getStringPresentation()));
            session.commit();
//            statement.execute("INSERT INTO rule (ref_rules, element_num, parent_id, fact, node) " +
//                    "VALUES(" + id_ + ", " + elementNumber + ", " + parentId + ",  null , '" + ex.getStringPresentation() + "')");
        }


        parentId = elementNumber++;
        for (Iterator<Expression> iterator = ex.getExpressions().iterator(); iterator.hasNext(); ) {
            Expression expression = iterator.next();
            if (!(expression.getStringPresentation().equals("Fact"))) {
                serializeExpression(expression, statement, id_, elementNumber++, parentId);
                elementNumber = elementNumber + 2;
                continue;
            } else {
      //          String s = expression.toString();

                ruleDBSession.insertFact(new RuleDB(id_, elementNumber++, parentId, expression.toString(), null));
                session.commit();
//                statement.execute("INSERT INTO rule (ref_rules, element_num, parent_id, fact, node) " +
//                        "VALUES(" + id_ + " , " + elementNumber++ + ", " + parentId + ", '" + s + "', null)");
            }
            if (!iterator.hasNext()) {
                break;
            }
        }
    }


}
