package DBAccess;

/**
 * Created by Jimmy on 2017/1/26.
 */
public class QuestionBean {
    //Create
    public void add(int q_id, String question) {
        if (q_id == 30000)
            System.out.println();
        DBAccess db = new DBAccess();
        if (db.createConn()) {
            String sql = "insert into chinese_question_copy(q_id,question) values("
                    + q_id + ",'" + question + "')";
            db.update(sql);
            db.closeStm();
            db.closeConn();
        }

    }
}
