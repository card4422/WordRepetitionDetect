package ThulacAdapter.thulac;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.regex.*;

import ThulacAdapter.manage.Filter;
import ThulacAdapter.manage.NegWord;
import ThulacAdapter.manage.Postprocesser;
import ThulacAdapter.manage.Preprocesser;
import ThulacAdapter.manage.Punctuation;
import ThulacAdapter.manage.TimeWord;
import ThulacAdapter.manage.VerbWord;

import ThulacAdapter.base.POCGraph;
import ThulacAdapter.base.SegmentedSentence;
import ThulacAdapter.base.TaggedSentence;

import ThulacAdapter.character.CBTaggingDecoder;

public class ThulacAdapter {

	/**
	 * @param str
	 * @throws IOException 
	 */
	
	public String[] run(String str) throws IOException {		
	    Character separator = '_';

	    //基础参数的设置
	    boolean useT2S = false;
	    boolean seg_only = false;
	    boolean useFilter = false;
	    int maxLength = 50000;


	    //载入模型
	    String prefix;
	    //prefix = "E:/workspace/WordRepetitionDetect-master/lib/models/";
    	prefix = "/Users/zhuzheng/workspace/WordRepetitionDetect/lib/models/";
	    String oiraw;
	    String raw =new String();
	    POCGraph poc_cands = new POCGraph();
	    TaggedSentence tagged = new TaggedSentence();
	    SegmentedSentence segged = new SegmentedSentence();
	    
	    
	    //读取模型
	    CBTaggingDecoder cws_tagging_decoder=new CBTaggingDecoder();
	    CBTaggingDecoder tagging_decoder=new CBTaggingDecoder();
	    if(seg_only) {
	    	cws_tagging_decoder.threshold = 0;
	    	cws_tagging_decoder.separator = separator;
		    cws_tagging_decoder.init((prefix+"cws_model.bin"),(prefix+"cws_dat.bin"),(prefix+"cws_label.txt"));
		    cws_tagging_decoder.setLabelTrans();
	    } else {
	    	tagging_decoder.threshold = 10000;
	    	tagging_decoder.separator = separator;
		    tagging_decoder.init((prefix+"model_c_model.bin"),(prefix+"model_c_dat.bin"),(prefix+"model_c_label.txt"));
		    tagging_decoder.setLabelTrans();
	    }
	    
	    Preprocesser preprocesser = new Preprocesser();
	    preprocesser.setT2SMap((prefix+"t2s.dat"));
	    Postprocesser nsDict = new Postprocesser((prefix+"ns.dat"), "ns", false);
	    Postprocesser idiomDict = new Postprocesser((prefix+"idiom.dat"), "i", false);
	    Postprocesser userDict = null;
	    Punctuation punctuation = new Punctuation((prefix+"singlepun.dat"));
	    TimeWord timeword = new TimeWord();
	    NegWord negword = new NegWord((prefix+"neg.dat"));
	    Filter filter = null;
	    if(useFilter){
	        filter = new Filter((prefix+"xu.dat"), (prefix+"time.dat"));
	    }

	    
	    //读取模型结束，开始读取文本数据
	    InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
	    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		//计时器      
        //long startTime = System.currentTimeMillis();//获取当前时间
        Vector<String> vec = null;
        
	    while(true)
	    {
	    	vec=getRaw(reader, maxLength);
	    	if(vec.size() ==0) break;
			for(int i = 0; i < vec.size(); i++) {
		    	oiraw = vec.get(i);
		    	if(useT2S) {
		    		String traw = new String();
		    		traw = preprocesser.clean(oiraw,poc_cands);
					raw = preprocesser.T2S(traw);
				}
		    	else{
		    		raw = preprocesser.clean(oiraw,poc_cands);
				}
		    	if(raw.length()>0)
		    	{
		    		if(seg_only) {
		    			cws_tagging_decoder.segment(raw, poc_cands, tagged);
		    			cws_tagging_decoder.get_seg_result(segged);
		    			nsDict.adjust(segged);
			    		idiomDict.adjust(segged);
			    		punctuation.adjust(segged);
			    		timeword.adjust(segged);
			    		negword.adjust(segged);
			    		if(userDict!=null){
		                    userDict.adjust(segged);
		                }
			    		if(useFilter){
		                    filter.adjust(segged);
		                }
			    		String [] split_words = new String[segged.size()];
			    		split_words [i] = segged.get(i);
		    			for(int j=1;j<segged.size();j++) 
		    				split_words [j] = segged.get(j);
		    			return split_words;
		    		}
		    		else {
		    			tagging_decoder.segment(raw, poc_cands, tagged);
		    			nsDict.adjust(tagged);
		    			idiomDict.adjust(tagged);
		    			punctuation.adjust(tagged);
		    			timeword.adjustDouble(tagged);
		    			negword.adjust(tagged);
		    			if(userDict!=null){
		    				userDict.adjust(tagged);
		    			}
		    			if(useFilter){
		    				filter.adjust(tagged);
		    			}		    			

		    	        String [] split_words = new String[tagged.size()];
		    			for(int j=0;j<tagged.size();j++) {
		    				split_words[j] = tagged.get(j).word+tagged.get(j).separator+tagged.get(j).tag;
		    			}
		    			return split_words;
		    		}
		    	}
		    }
	    }
	    //计时器
	    //long endTime = System.currentTimeMillis();
        //System.out.println("程序运行时间："+(endTime-startTime)+"ms");
        
    	return null;
	}
	public static Vector<String> getRaw(BufferedReader reader, int maxLength)
	{
		String ans=null;
		Vector<String> ans_vec = new Vector<String>();
		try {
			while((ans = reader.readLine()) != null)
			{
				break;	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ans == null) return ans_vec;
		if(ans.length() < maxLength) {
			ans_vec.add(ans);
		}
		else {
			Pattern p = Pattern.compile(".*?[。？！；;!?]");
			Matcher m = p.matcher(ans);
			int num = 0, pos = 0;
			String tmp;
			while(m.find()) {
				tmp = m.group(0);
				if(num + tmp.length() > maxLength) {
					ans_vec.add(ans.substring(pos, pos+num));
					pos += num; 
					num = tmp.length();
				}
				else {
					num += tmp.length();
				}
			}
			if(pos != ans.length()) ans_vec.add(ans.substring(pos));
		}
		return ans_vec;
	}
}
