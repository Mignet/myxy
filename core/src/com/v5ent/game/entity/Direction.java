package com.v5ent.game.entity;

import com.badlogic.gdx.math.MathUtils;

public enum Direction {
	UP,
	RIGHT,
	DOWN,
	LEFT, 
	LEFTUP,
	RIGHTUP,
	LEFTDOWN,
	RIGHTDOWN;

	/**
	 * NPC随机走动
	 * @return
	 */
	public static Direction getRandomNext() {
		switch(MathUtils.random(3)){
		case 1:return Direction.LEFTDOWN;
		case 2:return Direction.RIGHTUP;
		case 3:return Direction.LEFTUP;
		default:return Direction.RIGHTDOWN;
		}
	}

	public Direction getOpposite() {
		if( this == LEFT){
			return RIGHT;
		}else if( this == RIGHT){
			return LEFT;
		}else if( this == UP){
			return DOWN;
		}else if( this == DOWN){
			return UP;
		}else if( this == LEFTUP){
			return RIGHTDOWN;
		}else if( this == RIGHTDOWN){
			return LEFTUP;
		}else if( this == RIGHTUP){
			return LEFTDOWN;
		}else if( this == LEFTDOWN){
			return RIGHTUP;
		}
		return UP;
	}
}
