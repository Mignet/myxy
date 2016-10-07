/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.v5ent.game.pfa;

import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.Vector2;

/** A raycast collision detector used for path smoothing against a {@link TiledGraph}.
 * 
 * @param <N> Type of node, either flat or hierarchical, extending the {@link TiledNode} class
 * 
 * @author davebaol */
public class MyRaycastCollisionDetector {
	MyGraph worldMap;

	public MyRaycastCollisionDetector (MyGraph worldMap) {
		this.worldMap = worldMap;
	}

	// See http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
	public boolean collides (MyRay ray) {
		int x0 = (int)ray.start.getX();
		int y0 = (int)ray.start.getY();
		int x1 = (int)ray.end.getX();
		int y1 = (int)ray.end.getY();

		int tmp;
		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
		if (steep) {
			// Swap x0 and y0
			tmp = x0;
			x0 = y0;
			y0 = tmp;
			// Swap x1 and y1
			tmp = x1;
			x1 = y1;
			y1 = tmp;
		}
		if (x0 > x1) {
			// Swap x0 and x1
			tmp = x0;
			x0 = x1;
			x1 = tmp;
			// Swap y0 and y1
			tmp = y0;
			y0 = y1;
			y1 = tmp;
		}

		int deltax = x1 - x0;
		int deltay = Math.abs(y1 - y0);
		int error = 0;
		int y = y0;
		int ystep = (y0 < y1 ? 1 : -1);
		for (int x = x0; x <= x1; x++) {
			com.v5ent.game.pfa.MyNode tile = steep ? worldMap.getNode(y, x) : worldMap.getNode(x, y);
			if (!".".equals(tile.getValue())) return true; // We've hit a wall
			error += deltay;
			if (error + error >= deltax) {
				y += ystep;
				error -= deltax;
			}
		}

		return false;
	}

	public boolean findCollision (Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
		throw new UnsupportedOperationException();
	}
}
