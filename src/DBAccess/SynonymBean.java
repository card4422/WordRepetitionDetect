package DBAccess;

/**
 * Created by zhuzheng on 17/2/24.
 */
public class SynonymBean {
    String key_word;
    String similar_word;

    /**
     * 检测给定词是否可以在数据库中找到
     * @param word 搜索的词
     * @return true:关键词在数据库中可以被找到;false:关键词无法在数据中中找到
     */
    public boolean isExist(String word){
        boolean isExist = false;
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "select * from synonym where similar_word='"
                    + word + "'";
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


    public int getFrequency(String wordname,String characteristic){
        DBAccess db = new DBAccess();
        String result = new String();
        if(db.createConn()){
            String sql = "select * from synonym where wordname='"
                    + wordname + "' and characteristic='" + characteristic + "'";
            db.query(sql);
            if(db.next()) {
                result = db.getValue("frequency");
                if(result.equals(""))
                    System.out.println(1234);
            }
            db.closeRs();
            db.closeStm();
            db.closeConn();
        }
        if(result.equals(""))
            System.out.println(1234);
        return Integer.parseInt(result);
    }

    //Create
    public void add(String code,String name){
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "insert into synonym(code,name) values('"
                    + code + "','" + name + "')";
            db.update(sql);
            db.closeStm();
            db.closeConn();
        }
    }


}
