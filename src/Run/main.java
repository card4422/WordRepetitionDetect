package Run;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DBAccess.DBAccess;
import LevenshteinDistance.LevenshteinDistanceCalculator;
import Preprocessor.WordFilter;
import ThulacAdapter.thulac.ThulacAdapter;
import DBAccess.WordBean;
import DBAccess.DistanceBean;
import DBAccess.QuestionBean;

public class main
{
	public static void main(String[] args) throws IOException{
		//从数据库中读取字符串
		DBAccess db1 = new DBAccess();
		DBAccess db2 = new DBAccess();
		ThulacAdapter thulac = new ThulacAdapter();

		//将所有question表中题目进行分词并且将词存入word表中
		/*
		if(db1.createConn()) {
			String sql = "select question from chinese_question";
			db1.query(sql);
			while (db1.next()) {
				StoreWord(thulac.run(db1.getValue("question")));
			}
			db1.closeRs();
			db1.closeStm();
			db1.closeConn();
		}
*/
		if(db1.createConn()) {
			String sql = "select q_id,question from chinese_question";
			db1.query(sql);
			while(db1.next()) {
				int q_id = Integer.parseInt(db1.getValue("q_id"));
				String question = db1.getValue("question");
				WordFilter wf = new WordFilter();
				question = wf.doFilter(question);
				QuestionBean qb = new QuestionBean();
				qb.add(q_id,question);
			}
			db1.closeRs();
			db1.closeStm();
			db1.closeConn();
		}

		System.out.println();

		if(db1.createConn() && db2.createConn()) {
			String sql = "select q_id,question from chinese_question_copy";
			db1.query(sql);
			while(db1.next()) {
				String str1 = db1.getValue("question");
				String temp_sql = "select q_id,question from chinese_question_copy where q_id>" + db1.getValue("q_id");
				db2.query(temp_sql);
				while (db2.next()) {
					String str2 = db2.getValue("question");
					Pattern p = Pattern.compile("&nbsp;");
					String [] arr_str1 = thulac.run(p.matcher(str1).replaceAll(""));
					String [] arr_str2 = thulac.run(p.matcher(str2).replaceAll(""));
					//计算编辑距离，存入数据库中
					LevenshteinDistanceCalculator calculator = new LevenshteinDistanceCalculator(arr_str1,arr_str2);
					int disatance = calculator.getLevenshteinDistance();
					double similarity = calculator.getSimilarity();
					DistanceBean disb = new DistanceBean();
					//disb.add(Integer.parseInt(db1.getValue("q_id")),Integer.parseInt(db2.getValue("q_id")),disatance);
					disb.add(Integer.parseInt(db1.getValue("q_id")),Integer.parseInt(db2.getValue("q_id")),disatance,similarity);
				}
			}

			db1.closeRs();
			db1.closeStm();
			db1.closeConn();
			db2.closeRs();
			db2.closeStm();
			db2.closeConn();

			//过滤
		}
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