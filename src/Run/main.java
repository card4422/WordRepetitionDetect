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
import DBAccess.ParticipleBean;

public class main
{
	public static void main(String[] args) throws IOException {
		//从数据库中读取字符串
		DBAccess db = new DBAccess();
		ThulacAdapter thulac = new ThulacAdapter();
		DBAccess attr_db = new DBAccess();
		//将所有question表中题目进行分词并且将词存入word表中

//		if(db.createConn()) {
//			String sql = "select question from chinese_question";
//			db.query(sql);
//			while (db.next()) {
//				StoreWord(thulac.run(db.getValue("question")));
//			}
//			db.closeRs();
//			db.closeStm();
//			db.closeConn();
//		}

		//过滤特殊符号+分词，存入数据库
		if (db.createConn() && attr_db.createConn()) {

			String sql = "select q_id,content from question";
			db.query(sql);
			while (db.next()) {
				//将原始数据进行第一次过滤,去除无关符号以及标签
				int q_id = Integer.parseInt(db.getValue("q_id"));
				String question = db.getValue("content");
				WordFilter wf = new WordFilter();
				question = wf.doFilter(question);

				//预处理：分词过程
				String [] temp = thulac.run(question);

					//过滤某些不需要的词性的词
					//同义词替换
				String str = "";
				for(int i=0;i<temp.length;i++){
					str += temp[i] +" ";
				}
				ParticipleBean pb = new ParticipleBean();
				pb.add(attr_db,q_id,str,temp.length);
			}
			db.closeRs();
			db.closeStm();
			db.closeConn();
			attr_db.closeRs();
			attr_db.closeStm();
			attr_db.closeConn();
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
					if (arr_num[count1] <= 10 && arr_num[count2] < 10 || diff < 0.3) {
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