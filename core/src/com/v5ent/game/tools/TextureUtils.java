package com.v5ent.game.tools;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

public class TextureUtils {
	/**
	 * 创建像素图片-BOX
	 * @param width
	 * @param height
	 * @return
	 */
	public static Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        // Fill square with red color at 50% opacity
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();
        // Draw a yellow-colored X shape on square
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        // Draw a cyan-colored border around square
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }
	
	/**
	 * 创建像素图片-BOX
	 * @param width
	 * @param height
	 * @return
	 */
	public static Pixmap createBoxImagePixmap(int width, int height,float r,float g,float b,float a) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(r, g, b, a);
		pixmap.fill();
		pixmap.drawRectangle(0, 0, width, height);
		return pixmap;
	}
	/**
	 * 创建像素图片-原点
	 * @param width
	 * @param height
	 * @return
	 */
	public static Pixmap createCyclePixmap(int radius) {
		Pixmap pixmap = new Pixmap(radius, radius, Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		pixmap.drawCircle(0, 0, radius);
		return pixmap;
	}
}
