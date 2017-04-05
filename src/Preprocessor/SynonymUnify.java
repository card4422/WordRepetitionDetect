package Preprocessor;

import DBAccess.DBAccess;

import java.io.*;
import java.util.HashMap;
import java.lang.String;

/**
 * Created by zhuzheng on 17/2/24.
 */
public class SynonymUnify {
    public void ReadData(String filePath) throws IOException {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath),"UTF-8");

            BufferedReader br = new BufferedReader(isr);

            String str = br.readLine();
            DBAccess db = new DBAccess();
            if(db.createConn()) {
                while (str != null) {
                    String[] strArr = str.split(" ");
                    if (strArr[0].charAt(7) == '=') {
                        for (int i = 2; i < strArr.length; i++) {
                            String sql = "insert into synonym(key_word,similar_word) values('"
                                    + strArr[1] + "','" + strArr[i] + "')";
                            db.update(sql);
                        }
                    }
                    str=br.readLine();
                }
                db.closeStm();
                db.closeConn();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
