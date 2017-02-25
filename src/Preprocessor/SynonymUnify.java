package Preprocessor;

import java.io.*;
import java.util.HashMap;

/**
 * Created by zhuzheng on 17/2/24.
 */
public class SynonymUnify {
    //Read from file
    public void ReadFromFile(File file)throws IOException {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            HashMap<String,String> tmpMap = new HashMap<String,String>();
            String str = new String();
            str = br.readLine();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    //Store in to database
    public void StornInDB(HashMap map){

    }
}
