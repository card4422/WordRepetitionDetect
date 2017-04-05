package Run;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DBAccess.DBAccess;
import LevenshteinDistance.LevenshteinDistanceCalculator;
import Preprocessor.SynonymUnify;
import Preprocessor.Word;
import Preprocessor.WordFilter;
import ThulacAdapter.thulac.ThulacAdapter;
import DBAccess.WordBean;
import DBAccess.DistanceBean;
import DBAccess.QuestionBean;
import DBAccess.ParticipleBean;
import DBAccess.SynonymBean;
public class main {
    public static void main(String[] args) throws IOException {
        //从txt中读取同义词数据，输入到数据库中
//        SynonymReader sr = new SynonymReader();
//        sr.ReadData("C:\\Users\\Jimmy\\Desktop\\同义词.txt");

        //从数据库中读取字符串
        DBAccess db = new DBAccess();
        ThulacAdapter thulac = new ThulacAdapter();
        DBAccess attr_db = new DBAccess();
        //将所有question表中题目进行分词并且将词存入word表中

        //过滤特殊符号+分词，存入数据库
        if (db.createConn() && attr_db.createConn()) {
            long startMili = System.currentTimeMillis();// 当前时间对应的毫秒数
            long endMili = System.currentTimeMillis();
            long participleTime = 0;
            long filterTime = 0;
            String sql = "select q_id,content from question";
            db.query(sql);
            while (db.next()) {
                //将原始数据进行第一次过滤,去除无关符号以及标签
                startMili = System.currentTimeMillis();

                int q_id = Integer.parseInt(db.getValue("q_id"));
                String question = db.getValue("content");
                WordFilter wf = new WordFilter();
                question = wf.doFilter(question);

                endMili = System.currentTimeMillis();
                filterTime += endMili - startMili;

                startMili = System.currentTimeMillis();

                //预处理：分词过程
                Word[] temp = thulac.run(question);

                endMili = System.currentTimeMillis();
                participleTime += endMili - startMili;

                //将word存入word表中
                StoreWord(temp);

                startMili = System.currentTimeMillis();
                //过滤某些不需要的词性的词
                String []tmp = wf.CharacteristicFilter(temp);

                //同义词替换
                SynonymBean sb = new SynonymBean();
                for (int i = 0; i < tmp.length; i++) {
                    if (sb.isExist(tmp[i])) {
                        tmp[i] = sb.getString(tmp[i]);
                    }
                }

                endMili = System.currentTimeMillis();
                filterTime += endMili - startMili;


                String str = "";
                for (int i = 0; i < tmp.length; i++) {
                    if(tmp[i]!=null)
                       str += tmp[i] + " ";
                }
                ParticipleBean pb = new ParticipleBean();
                pb.add(attr_db, q_id, str, tmp.length);
            }
            db.closeRs();
            db.closeStm();
            db.closeConn();
            attr_db.closeRs();
            attr_db.closeStm();
            attr_db.closeConn();

            endMili = System.currentTimeMillis();
            System.out.println("分词耗时为：" + participleTime / 1000 + "秒");
            System.out.println("过滤耗时为：" + filterTime / 1000 + "秒");

        }

		long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

		//计算编辑距离并且存入数据库
		if (db.createConn()) {
			//将数据库中的数据取出存放入数组（内存）中
			String sql = "select q_id,content_after,number from question_after";
			db.query(sql);
			int size = 0;
			//arr_str存放数据库中取出的题目，有序
			String[][] arr_str = new String[10000][];
			//arr_int存放数据库中取出的题目id，有序
			int[] arr_int = new int[10000];
			//arr_num存放数据库取出的分词数量，有序
			int[] arr_num = new int[10000];

			//初始化数组的值
			//该数组暂时存放从数据库中取出的字符串题目
			String[] temp_arr_str = new String[10000];

			while (db.next() && size < 10000) {
				temp_arr_str[size] = db.getValue("content_after");
				arr_int[size] = db.getIntValue("q_id");
				arr_num[size] = db.getIntValue("number");
				size++;
			}

			//将数据库中的题目以空格进行分割，存入数组arr_str中
			for(int i=0;i<size;i++){
				arr_str[i] = temp_arr_str[i].split(" ");
			}

//			ExecutorSeparator es = new ExecutorSeparator();
//			arr_str = es.separate(temp_arr_str);
			int count1 = 0;
			while (count1 < size) {
				int count2 = count1 + 1;
				while (count2 < size) {
					//判断是否有必要进行计算
					double diff = 0;
					if (arr_num[count1] >= arr_num[count2]) {
						diff = (double) Math.abs(arr_num[count1] - arr_num[count2]) / arr_num[count1];
					} else {
						diff = (double) Math.abs(arr_num[count1] - arr_num[count2]) / arr_num[count2];
					}
					if (arr_num[count1] <= 10 && arr_num[count2] < 10 || diff <= 0.3) {
						//计算编辑距离，存入数据库中
						LevenshteinDistanceCalculator calculator = new LevenshteinDistanceCalculator(arr_str[count1], arr_str[count2]);
						int disatance = calculator.getLevenshteinDistance();
						double similarity = calculator.getSimilarity();

						DistanceBean disb = new DistanceBean();
						disb.add(arr_int[count1], arr_int[count2], disatance, similarity);
					}
					count2++;
				}
				count1++;
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
		long endMili=System.currentTimeMillis();
		System.out.println("总耗时为："+(endMili-startMili)/1000+"秒");
	}

	private static void StoreWord(Word [] wordlist){
		WordBean wb = new WordBean();
		int frequency;
		for(int i=0;i<wordlist.length;i++){
			if(wb.isExist(wordlist[i].wordName,wordlist[i].characteristic)){
				frequency = wb.getFrequency(wordlist[i].wordName, wordlist[i].characteristic);
				wb.updateFreq(wordlist[i].wordName, wordlist[i].characteristic, frequency+1);
			}else{
				wb.add(wordlist[i].wordName, wordlist[i].characteristic);
			}
		}
    }
}