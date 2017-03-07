package DBAccess;

/**
 * Created by Jimmy on 2017/1/26.
 */
public class QuestionBean {
    //Create
    public void add(DBAccess db, int q_id, String question) {
        String sql = "insert into chinese_question_copy(q_id,question) values("
                + q_id + ",'" + question + "')";
        db.update(sql);
    }
}
