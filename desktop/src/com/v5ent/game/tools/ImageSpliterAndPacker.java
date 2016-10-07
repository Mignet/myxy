package com.v5ent.game.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageSpliterAndPacker {
	public static void main(String[] args) throws IOException {
//		mergeImage("F:/backend/rpg/core/assets/maps/长安", "长安","jpg",24,35);
//		mergeImage("F:/backend/rpg/core/assets/maps/长安_", "长安","png",24,35);
//		mergeImage("F:/backend/rpg/core/assets/maps/建邺城_", "建邺城","png",12,18,true);
//		mergeImage("F:/backend/rpg/core/assets/maps/建邺城", "建邺城","jpg",12,18,true);
//		mergeImage("D:/RPG/NPC资源/walk", "建邺士兵-行走","png",4,8,true);
		mergeImage("D:\\RPG\\waddon.wdf.ResFiles", "trans","png",1,18,true);
//		splitImage("F:/backend/rpg/core/assets/maps/建邺.jpg","建邺","jpg",12,18);
//		splitImage("F:/backend/rpg/core/assets/maps/建邺-.jpg","建邺城_","jpg",12,18);
//		splitImage("F:/backend/rpg/core/assets/maps/长安.jpg","长安",24,35);
//		splitImage("F:/backend/rpg/core/assets/maps/长安-.png","长安_",24,35);
	}
	
	private static void splitImage(String originalImg,String name,String ext,int rows,int cols) throws IOException {  
	    // 读入大图  
	    File file = new File(originalImg);  
	    FileInputStream fis = new FileInputStream(file);  
	    BufferedImage image = ImageIO.read(fis);  
	  
	    // 分割成4*4(16)个小图  
//	    int rows = 4;  
//	    int cols = 4;  
	    int chunks = rows * cols;  
	  
	    // 计算每个小图的宽度和高度  
	    int chunkWidth = image.getWidth() / cols;  
	    int chunkHeight = image.getHeight() / rows;  
	  
	    int count = 0;  
	    BufferedImage imgs[] = new BufferedImage[chunks];  
	    for (int x = 0; x < rows; x++) {  
	        for (int y = 0; y < cols; y++) {  
	            //设置小图的大小和类型  
	            imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());  
	  
	            //写入图像内容  
	            Graphics2D gr = imgs[count++].createGraphics();  
	            gr.drawImage(image, 0, 0,   
	                    chunkWidth, chunkHeight,   
	                    chunkWidth* y, chunkHeight * x,   
	                    chunkWidth * y + chunkWidth,  
	                    chunkHeight * x + chunkHeight, null);  
	            gr.dispose();  
	        }  
	    }  
	  File outfile = new File(file.getParent()+"/"+name);
	    if(outfile.exists()){
	            System.out.println("创建目录" + file.getParent()+"/"+name + "失败，目标目录已经存在");  
	        }else{
	    	if(!outfile.mkdirs()){
	    		System.out.println("创建目标文件所在目录失败！");  
	    	};
	    }
	    // 输出小图  
	    for (int i = 0; i < imgs.length; i++) {  
	    	int n = i;
	    	int x = n%cols;
	    	int y = rows - n/cols -1;
//	    	System.out.println(x + "-" + y);
	        ImageIO.write(imgs[i], ext, new File(file.getParent()+"/"+name+"/"+ x + "-" + y + "."+ext));  
	    } 
	    System.out.println("完成分割！");  
	}  
	
	private static void mergeImage(String dir,String name,final String ext,final int rows,final int cols,final boolean filpY) throws IOException {  
		  
	    int chunks = rows * cols;  
	    int chunkWidth, chunkHeight;  
	    int type;  
	  
	    //读入小图  
	    File[] imgFiles = new File(dir).listFiles();
	    List<File> fileList = new ArrayList<File>();
	    for (File f : imgFiles) {
	        fileList.add(f);
	    }
//	    Collections.sort(fileList);
	   /* Collections.sort(fileList, new Comparator<File>() {
	    	@Override  
	    	   public int compare(File o1, File o2) {  
	    		String[] pos1 = o1.getName().replace("."+ext, "").split("-");
	    		int x1 = Integer.valueOf(pos1[0]);
	    		int y1 = Integer.valueOf(pos1[1]);
	    		String[] pos2 = o2.getName().replace("."+ext, "").split("-");
	    		int x2 = Integer.valueOf(pos2[0]);
	    		int y2 = Integer.valueOf(pos2[1]);
	    		if(x1==x2&&y1==y2){
	    			return 0;
	    		}
	    		if(filpY){
	    			return x1+(rows-y1)*cols > x2+(rows-y2)*cols ? 1 : -1;
	    		}else{
	    			return x1+y1*cols > x2+y2*cols ? 1 : -1;
	    		}
	    	   }  
	    });*/
	    for (File f : fileList) {  
	    	System.out.println(f.getName());  
	    }  
	    
	    //创建BufferedImage  
	    BufferedImage[] buffImages = new BufferedImage[chunks];  
	    for (int i = 0; i < chunks; i++) {  
	        buffImages[i] = ImageIO.read(fileList.get(i));  
	    }  
	    type = buffImages[0].getType();  
	    chunkWidth = buffImages[0].getWidth();  
	    chunkHeight = buffImages[0].getHeight();  
	  
	    //设置拼接后图的大小和类型  
	    BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);  
	  
	    //写入图像内容  
	    int num = 0;  
	    for (int i = 0; i < rows; i++) {  
	        for (int j = 0; j < cols; j++) {  
	            finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);  
	            num++;  
	        }  
	    }  
	  
	    //输出拼接后的图像  
	    ImageIO.write(finalImg, ext, new File(dir+"\\"+name+"-"+chunkWidth+"-"+chunkHeight+"."+ext));  
	  
	    System.out.println("完成拼接！");  
	}  
}
