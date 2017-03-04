package Run;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DBAccess.DBAccess;
import LevenshteinDistance.LevenshteinDistanceCalculator;
import MultiThread.ExecutorSeparator;
import Preprocessor.WordFilter;
import ThulacAdapter.thulac.ThulacAdapter;
import DBAccess.WordBean;
import DBAccess.DistanceBean;
import DBAccess.QuestionBean;

public class main
{
	public static void main(String[] args) throws IOException {
		//从数据库中读取字符串
		DBAccess db = new DBAccess();
		ThulacAdapter thulac = new ThulacAdapter();

		//将所有question表中题目进行分词并且将词存入word表中
		/*
		if(db.createConn()) {
			String sql = "select question from chinese_question";
			db.query(sql);
			while (db.next()) {
				StoreWord(thulac.run(db.getValue("question")));
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
*/


		//将原始数据进行第一次过滤
/*		if (db.createConn()) {
			String sql = "select q_id,question from chinese_question";
			db.query(sql);
			while (db.next()) {
				int q_id = Integer.parseInt(db.getValue("q_id"));
				String question = db.getValue("question");
				WordFilter wf = new WordFilter();
				question = wf.doFilter(question);
				QuestionBean qb = new QuestionBean();
				qb.add(q_id, question);
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
		}
*/
		long startTime = System.currentTimeMillis();    //获取开始时间

		//EDITED
		if (db.createConn()) {
			String sql = "select q_id,question from chinese_question_copy";
			db.query(sql);
			int size = 0;
			String[][] arr_str = new String[10000][];
			int[] arr_int = new int[10000];

			//initialization
			long start = System.currentTimeMillis();    //获取结束时间

//			while (db.next() && size < 100000) {
//				arr_str[size] = thulac.run(db.getValue("question"));
//				arr_int[size] = db.getIntValue("q_id");
//				size++;
//				System.out.println(size);
//			}


			String [] temp_arr_str = new String[10000];
			while (db.next() && size < 10000) {
				temp_arr_str[size] = db.getValue("question");
				arr_int[size] = db.getIntValue("q_id");
				size++;
			}

			ExecutorSeparator es = new ExecutorSeparator();
			arr_str = es.separate(temp_arr_str);

			long end = System.currentTimeMillis();    //获取结束时间
			System.out.println("分词所用时间：" + (end - start) + "ms");    //分词所用时间

			int count1 = 0;
			while (count1 < size) {
				int count2 = count1 + 1;
				while (count2 < size) {
					//String[] arr_str1 = arr_str[count1];
					//String[] arr_str2 = arr_str[count2];

					//计算编辑距离，存入数据库中
					LevenshteinDistanceCalculator calculator = new LevenshteinDistanceCalculator(arr_str[count1], arr_str[count2]);
					int disatance = calculator.getLevenshteinDistance();
					double similarity = calculator.getSimilarity();

					DistanceBean disb = new DistanceBean();
					disb.add(arr_int[count1], arr_int[count2], disatance, similarity);
					count2++;
				}
				//过滤
				count1 ++;
			}
		}
		db.closeRs();
		db.closeStm();
		db.closeConn();

		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("程序运行时间：" + (endTime - startTime)/1000/60 + "min");    //输出程序运行时间

	}

	private static void StoreWord(String [] wordlist){
		WordBean wb = new WordBean();
		int frequency;
		String wordname;
		String characteristic;
		int occurance;
		for(int i=0;i<wordlist.length;i++){
			occurance = wordlist[i].lastIndexOf("_");
			wordname = wordlist[i].substring(0, occurance);
			characteristic = wordlist[i].substring(occurance+1);
			if(wb.isExist(wordname,characteristic)){
				frequency = wb.getFrequency(wordname, characteristic);
				wb.updateFreq(wordname, characteristic, frequency+1);
			}else{
				wb.add(wordname, characteristic);
			}
		}
	}
}