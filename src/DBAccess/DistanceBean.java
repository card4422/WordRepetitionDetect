package DBAccess;

/**
 * Created by Jimmy on 2017/1/25.
 */
public class DistanceBean {
    int q1_id;
    int q2_id;
    int distance;
/*
    public boolean isExist(String wordname,String characteristic){
        boolean isExist = false;
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "select * from word where wordname='"
                    + wordname + "' and chatacteristic='" + characteristic + "'";
            db.query(sql);
            if(db.next()){
                isExist = true;
            }
            db.closeRs();
            db.closeStm();
            db.closeConn();
        }
        return isExist;
    }
*/
    public boolean isExist(int qd_id){
        boolean isExist = false;
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "select * from ques_dist where qd_id="
                    +qd_id;
            db.query(sql);
            if(db.next()){
                isExist = true;
            }
            db.closeRs();
            db.closeStm();
            db.closeConn();
        }
        return isExist;
    }
/*
    public int getDistance(String wordname,String characteristic){
        DBAccess db = new DBAccess();
        String result = new String();
        if(db.createConn()){
            String sql = "select * from word where wordname='"
                    + wordname + "' and chatacteristic='" + characteristic + "'";
            db.query(sql);
            result = db.getValue("frequency");

            db.closeRs();
            db.closeStm();
            db.closeConn();
        }
        return Integer.parseInt(result);
    }

    */

    //Create

    /**
     *
     * @param q1_id
     * @param q2_id
     * @param distance
     */
    public void add(int q1_id,int q2_id,int distance){
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "insert into ques_dist(q1_id,q2_id,distance) values("
                    + q1_id + "," + q2_id + ","+ distance +")";
            db.update(sql);
            db.closeStm();
            db.closeConn();
        }
    }

    public void add(int q1_id,int q2_id,int distance,double similarity){
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "insert into ques_dist(q1_id,q2_id,distance,similarity) values("
                    + q1_id + "," + q2_id + ","+ distance +"," + similarity + ")";
            db.update(sql);
            db.closeStm();
            db.closeConn();
        }
    }

    //Update
    /*
    public void updateFreq(String wordname,String characteristic,int frequency){
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "update word set frequency='" + frequency
                    + "' where wordname='" + wordname + "' and characteristic ='" + characteristic + "'";
            db.update(sql);
            db.closeStm();
            db.closeConn();
        }
    }
    */
}
