package com.v5ent.game.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharsGenerator {
	private static String dir = "F:\\backend\\myxy\\core";
	
	public static void main(String[] args) {
		try {
			String str =readFiles(dir);
			Pattern p = Pattern.compile("([^\\x00-\\xff]+)");
			Matcher m = p.matcher(str);
			String mv = null;
			while (m.find()) {
				mv = m.group(0);
				System.out.println(mv);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	private static String readFiles(String dir) throws IOException{
		String result = "";
		TreeSet<String> noReapted = new TreeSet<String>();
		File f = new File(dir+"\\assets");
		if(f.isDirectory()){
			for(File fi:f.listFiles()){
				if(fi.isDirectory()){
					for(File file:fi.listFiles()){
						if(file.isFile()&&file.getName().contains(".json")){
							FileReader fr = new FileReader(file);
							int ch = 0;
							while((ch = fr.read())!=-1 )
							{
								noReapted.add(""+(char)ch);
							}
							fr.close();
						}
					}
				}
			}
		}
		f = new File(dir+"\\src\\com\\v5ent\\game\\screens");
		if(f.isDirectory()){
			for(File file:f.listFiles()){
				if(file.isFile()&&file.getName().contains(".java")){
					FileReader fr = new FileReader(file);
					int ch = 0;
					while((ch = fr.read())!=-1 )
					{
						noReapted.add(""+(char)ch);
					}
					fr.close();
				}
			}
		}
		f = new File(dir+"\\src\\com\\v5ent\\game\\ui");
		if(f.isDirectory()){
			for(File file:f.listFiles()){
				if(file.isFile()&&file.getName().contains(".java")){
					FileReader fr = new FileReader(file);
					int ch = 0;
					while((ch = fr.read())!=-1 )
					{
						noReapted.add(""+(char)ch);
					}
					fr.close();
				}
			}
		}
        //结果
        for(String index:noReapted){
        	result += index;
        }
		return result;
	}
}
