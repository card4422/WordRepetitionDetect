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

    public String getString(String word){
        String result = new String();
        DBAccess db = new DBAccess();
        if(db.createConn()){
            String sql = "select * from synonym where similar_word='"
                    + word + "'";
            db.query(sql);
            if(db.next())
              result = db.getValue("key_word");

            db.closeRs();
            db.closeStm();
            db.closeConn();
        }
        return result;
    }


    //Create
    public void add(DBAccess db,String code,String name) {
        String sql = "insert into synonym(key_word,similar_word) values('"
                + code + "','" + name + "')";
        db.update(sql);
        db.closeStm();
        db.closeConn();
    }
}
