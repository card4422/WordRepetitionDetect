package ImgCompare;

import java.awt.image.BufferedImage;

public class ImgCompare
{
	final int N = 4;
	double [][] large_DCT = new double [N][N];
	//double [][] arr_from = new double[N][N];
	double [][] arr_to = new double [8][8];
	
	//将图片缩放至指定比例
	public BufferedImage zoomingImg(BufferedImage origin_img){
		int width = N;
		int height = N;
		BufferedImage newImage = new BufferedImage(width,height,origin_img.getType());
		return newImage;
	}
	
	//将图片灰度化
	public double[][] convertToGrayImg(BufferedImage new_img){
		int color,red,green,blue;
		double [][] img_arr = new double[N][N];
		for(int i=0;i<new_img.getWidth();i++){
			for(int j=0;j<new_img.getHeight();j++){
				color = new_img.getRGB(i, j);
				red = (color >> 16) & 0xff;
				green = (color >> 8) & 0xff;
				blue = color & 0xff;
				img_arr[i][j] = (0.3*red + 0.59*green + 0.11*blue);
			}
		}
		return img_arr;
	}
	
	//计算DCT矩阵值
	public void CalculateDCT(double [][] arr_from){
		double cu;
		double cv;
		double sum = 0;
		for(int u=0;u<N;u++){
			for(int v=0;v<N;v++){
				if(u==0){
					cu = Math.sqrt(1.0/N);
				}else{
					cu = Math.sqrt(2.0/N);
				}
				if(v==0){
					cv = Math.sqrt(1.0/N);
				}else{
					cv = Math.sqrt(2.0/N);					
				}
				for(int i=0;i<N;i++){
					for(int j=0;j<N;j++){						
						sum = sum + arr_from[i][j]
								*Math.cos(((i+0.5)*Math.PI*u)/N)
								*Math.cos(((j+0.5)*Math.PI*v)/N);
					}
				}
				System.out.println(sum);
				large_DCT [u][v] = cu*cv*sum;
				sum = 0;
			}
		}
		//输出矩阵
		/*
		for(int u=0;u<N;u++){
			for(int v=0;v<N;v++){
				System.out.print(large_DCT[u][v]+"//");
				
			}
			System.out.println();
		}
		*/
	}
	
	//缩小DCT矩阵
	public double[][] zoomOutDCT(double [][] arr_from){
		double [][]arr_to = new double[8][8];
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				arr_to [i][j] = arr_from[i][j];
			}
		}
		return arr_to;
	}
	
	//比较每个元素的灰度与平均值之间的关系
	public int[][] getBinaryArr(double [][]grayImg_arr){
		double sum = 0;
		double avg;
		int [][]res_arr = new int [8][8];
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				sum = sum + grayImg_arr[i][j];
			}
		}
		avg = sum/(8*8);
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(grayImg_arr[i][j]>avg){
					res_arr[i][j] = 1;
				}else{
					res_arr[i][j] = 0;
				}
			}
		}
	return res_arr;
	}
	
	//计算hash值
	public String Convert2Hash(int [][] arr){
		String hashString = "";
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				hashString = hashString + arr[i][j];
			}
		}
		return hashString;
	}
	
	//计算海明距离
	public int HammingDis(String str1,String str2){
		int dis = 0;
		for(int i=0;i<64;i++){
			if(str1.charAt(i)!=str1.charAt(i)){
				dis++;
			}
		}
		return dis;
	}
}
