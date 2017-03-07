package DBAccess;

/**
 * Created by Jimmy on 2017/3/4.
 */
public class ParticipleBean {
    int q1_id;
    int q2_id;
    int distance;

    /**
     *
     * @param db
     * @param q_id
     * @param question
     * @param number
     */
    public void add(DBAccess db, int q_id, String question, int number) {
        String sql = "insert into chinese_question_after(q_id,question,number) values("
                + q_id + ",'" + question + "'," + number + ")";
        db.update(sql);
    }
}
