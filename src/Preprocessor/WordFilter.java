package Preprocessor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordFilter
{
	//存放所有允许通过的词性
	private HashMap <String, String> keyWord = new HashMap<String, String>();
	private String markSet="n/名词 np/人名 ns/地名 ni/机构名 nz/其它专名"+
	" m/数词 q/量词 mq/数量词 t/时间词 f/方位词"+
	" s/处所词 v/动词 a/形容词 d/副词 h/前接成分"+
	" k/后接成分 i/习语 j/简称 r/代词"+
	" y/语气助词 o/拟声词 g/语素 x/其它";
	private String [] input_str = null;

	/**
	 * 构造一个HashMap
	 * 其中key存放的是词性，value存放的是词性对应的标记符
	 * 例：名词 n
	 */
	public WordFilter(){
		String [] temp = markSet.split(" ");
		String [] mark = null;
		for(int i = 0;i<temp.length;i++){
			mark = temp[i].split("/");
			keyWord.put(mark[1], mark[0]);
		}
	}

    public String[] CharacteristicFilter(Word[] words){
        String [] strArr = new String[words.length];
        int j = 0;
        for(int i=0;i<words.length;i++){
            if(keyWord.containsValue(words[i].characteristic)){
                strArr[j] = words[i].wordName;
                j++;
            }
        }
        return strArr;
    }

	/**
	 * 构造一个HashMap
	 * 其中key存放的是词性，value存放的是词性对应的标记符
	 * 例：名词 n
	 * @param arr_str 已经分好词的字符串数组
	 */
	public WordFilter(String [] arr_str){
		String [] temp = markSet.split(" ");
		String [] mark = null;
		for(int i = 0;i<temp.length;i++){
			mark = temp[i].split("/");
			keyWord.put(mark[1], "_"+mark[0]);
		}
		input_str = arr_str;
	}
	
	/**
	 * 移除不需要的词性
	 * @param str 要移除的词性，例如：“名词”
	 */
	public void removeMark(String str){
		keyWord.remove(str);
	}

	public String doFilter(String str){
		String temp;

		Pattern p = Pattern.compile("&nbsp;");
		Matcher m = p.matcher(str);
		temp = m.replaceAll("");// 过滤空格符&nbsp

		p = Pattern.compile("_");
		m = p.matcher(temp);
		temp = m.replaceAll("");// 过滤下划线

		p = Pattern.compile("'");
		m = p.matcher(temp);
		temp = m.replaceAll("");// 过滤单引号

		/*
		p = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE);
		m = p.matcher(temp);
		temp = m.replaceAll(""); // 过滤script标签
*/

        p = Pattern.compile("<[\\s\\S]*?>", Pattern.CASE_INSENSITIVE);
        m = p.matcher(temp);
        temp = m.replaceAll(""); // 过滤script标签

        p = Pattern.compile("</[\\s\\S]*?>", Pattern.CASE_INSENSITIVE);
        m = p.matcher(temp);
        temp = m.replaceAll(""); // 过滤span标签

		temp = temp.replaceAll("\\s*", "");

		return temp;
	}
	
	//public String[] WordScan（String[] str){
	
//	}
}
