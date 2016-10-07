package com.v5ent.game.tools;

public class AnimationJsoner {
	private static final int MODEL_IDLE = 0;
	public static void main(String[] args) {
		animal("IDLE_RIGHTDOWN","建邺士兵-站立-74-172",74,172,0,8,4);
		animal("IDLE","建邺士兵-站立-74-172",74,172,0,8,4);
		animal("IDLE_LEFTDOWN","建邺士兵-站立-74-172",74,172,1,8,4);
		animal("IDLE_RIGHTUP","建邺士兵-站立-74-172",74,172,2,8,4);
		animal("IDLE_LEFTUP","建邺士兵-站立-74-172",74,172,3,8,4);
		animal("WALK_RIGHTDOWN","建邺士兵-行走-212-137",212,137,0,8,4);
		animal("WALK_LEFTDOWN","建邺士兵-行走-212-137",212,137,1,8,4);
		animal("WALK_RIGHTUP","建邺士兵-行走-212-137",212,137,2,8,4);
		animal("WALK_LEFTUP","建邺士兵-行走-212-137",212,137,3,8,4);
	}
	private static void directGene(){
		
	}
	/**
	 * 
	 * @param w
	 * @param h
	 */
	private static void animal(String dir,String name,int frameWidth,int frameHeight,int order,int w,int h){
		System.out.println("{\n"+
		"frameDuration: 0.125\n"+
		"animationType: "+dir+"\n"+
		"texturePaths: [\n"+
		"	sprites/characters/"+name+".png\n"+
		"]\n"+
		"frameWidth: "+frameWidth+"\n"+
		"frameHeight: "+frameHeight+"\n"+
		"gridPoints: [");
		for(int i =0;i<w;i++){
			System.out.println("{\n"+
			"	x: "+order+"\n"+
			"	y: "+i+"\n"+
			"}");
		}
		System.out.println("]\n}\n");
	}
}
