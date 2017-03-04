package LevenshteinDistance;
import java.util.Arrays;
public class LevenshteinDistanceCalculator
{
	private int LevenshteinDistance;
	private int [][]matrix;
	
	//默认构造函数，但是基本不会使用
	public LevenshteinDistanceCalculator(){
		LevenshteinDistance = -1;
	}
	
	/**
	 * 构造函数
	 * @param str1 进行编辑距离计算的字符串数组1
	 * @param str2 进行编辑距离计算的字符串数组2
	 */
	public LevenshteinDistanceCalculator(String [] str1,String [] str2){
		LevenshteinDistance = -1;
		matrix = new int[str2.length+1][];
		for(int i=0;i<=str2.length;i++){
			matrix[i] = new int [str1.length+1];
		}

		//在矩阵的第一行写上0~s，s为str1的长度
		for(int i=0;i<=str1.length;i++){
			matrix[0][i]= i;
		}

		//在矩阵的第一列写上0~t，t为str2的长度
		for(int j=1;j<=str2.length;j++){
			matrix[j][0]= j;
		}

		LevenshteinDistanceCalculate(str1,str2);
	}



	/**
	 * 计算两个字符串的编辑距离
	 * @param str1 进行编辑距离计算的字符串数组1
	 * @param str2 进行编辑距离计算的字符串数组2
	 * @return 编辑距离
	 */
	private void LevenshteinDistanceCalculate(String []str1,String []str2){
		int left_top,left,top,price;
		for(int i=1;i<=str2.length;i++){
			for(int j=1;j<=str1.length;j++){
				left = matrix[i][j-1];
				top = matrix[i-1][j];
				left_top = matrix[i-1][j-1];
				if(str1[j-1].equals(str2[i-1])){
					price = 0;
				}
				else{
					price = 1;
				}
				int min = min(left_top,left,top);
				if(min==left_top){
					matrix[i][j] = min + price;
				}else{
					matrix[i][j] = min + 1;
				}
			}
		}
		/*
		//输出matrix矩阵
		for(int i=0;i<=str2.length;i++){
			for(int j=0;j<=str1.length;j++){
				System.out.print(matrix[i][j]+",");
			}
			System.out.println();
		}
		*/
		LevenshteinDistance = matrix[str2.length][str1.length];
	}

	public int getLevenshteinDistance(){
		return LevenshteinDistance;
	}

	public double getSimilarity(){
        double max = 0;
        if(matrix.length>matrix[0].length){
            max = matrix.length;
        }
        else{
            max = matrix[0].length;
        }
        return 1-LevenshteinDistance/max;
    }

	/**
	 * 计算left_top_value,left_value,top_value中的最小值
	 * @param n1 left_top_value
	 * @param n2 left_value
	 * @param n3 top_value
	 * @return 三个value中的最小值
	 */


	private int min(int n1,int n2,int n3){
		int temp;
		if(n1>n2){
			temp = n2;
		}else{
			temp = n1;
		}
		if(temp>n3){
			return n3;
		}else{
			return temp;
		}
	}
}
