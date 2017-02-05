package Run;

import Preprocessor.WordFilter;
import ThulacAdapter.thulac.ThulacAdapter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jimmy on 2017/1/26.
 */
public class test {
    public static void main(String [] args) throws IOException {
        String str = "&nbsp; &nbsp; 余光中先生说：一个方块字是一个天地，美丽的中文不老。许多汉字自身的构成就能诠释含义、激发联想。请仿照示例拆拼汉字，并用富有文采的语言描述它。要求：至少运用一种修辞方法。<p>【例】墨：大地滋养出一个黑色的精灵，在古朴的宣纸上翩翩起舞。</p><p>(1)尘：____________________________________________________________________</p><p>(2)鸿：____________________________________________________________________</p>";
        WordFilter wf = new WordFilter();
        str = wf.doFilter(str);
        System.out.println(str);
    }
}
