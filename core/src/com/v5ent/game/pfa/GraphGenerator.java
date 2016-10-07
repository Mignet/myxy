package com.v5ent.game.pfa;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.entity.Entity;

public class GraphGenerator {

	private static String TAG = GraphGenerator.class.getSimpleName();

	public static MyGraph generateGraph(TiledMapTileLayer mapCollisionLayer,int numCols,int numRows,int tileW,int tileH) {
		final MyNode[][] nodes = new MyNode[numCols][numRows];
		final Array<MyNode> indexedNodes = new Array<MyNode>(numCols * numRows);
		//初始化数据地图
		final String[][] tiles = new String[numCols][numRows];
		for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++) {
				tiles[x][y] = ".";
				if(mapCollisionLayer!=null&&mapCollisionLayer.getCell(x, y)!=null){
					tiles[x][y] = "#";
				}
			}
		}
		/*if (mapCollisionLayer != null) {
			for (MapObject e : mapCollisionLayer.getObjects()) {
				int x = (int)(e.getProperties().get("x",Float.class)/tileW);
				int y = (int)(e.getProperties().get("y",Float.class)/tileH);
				if(x<numCols && y < numRows && x >=0 && y >= 0){
					tiles[x][y] = "#";
				}
			}
		}*/
		//测试打印
		if(Gdx.app.getLogLevel()==Application.LOG_DEBUG){
			Gdx.app.debug(TAG,"----------------------------------");
			StringBuilder temp = new StringBuilder("\n");
			for (int y = 0; y < numRows; y++) {
				for (int x = 0; x < numCols; x++) {
					temp.append(tiles[x][numRows-1-y]) ;
				}
				temp.append("\n");
			}
			Gdx.app.debug(TAG,temp.toString());
			Gdx.app.debug(TAG,"----------------------------------");
		}
		int index = 0;
		for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++, index++) {
				nodes[x][y] = new MyNode(index, x, y,tiles[x][y], 4);
				indexedNodes.add(nodes[x][y]);
			}
		}

		for (int y = 0; y < numRows; y++, index++) {
			for (int x = 0; x < numCols; x++, index++) {
				if (tiles[x][y].equals("#")) {
					continue;
				}

				if (x - 1 >= 0 && tiles[x - 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x - 1][y]));
				}

				if (x + 1 < numCols && tiles[x + 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x + 1][y]));
				}

				if (y - 1 >= 0 && tiles[x][y - 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y - 1]));
				}

				if (y + 1 < numRows && tiles[x][y + 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y + 1]));
				}

			}
		}

		return new MyGraph(indexedNodes);
	}
	
	public static MyGraph generateGraph(TiledMapTileLayer mapCollisionLayer,Array<Entity> entities,int numCols,int numRows,int tileW,int tileH,Vector2 startPoint) {
		final MyNode[][] nodes = new MyNode[numCols][numRows];
		final Array<MyNode> indexedNodes = new Array<MyNode>(numCols * numRows);
		//初始化数据地图
		final String[][] tiles = new String[numCols][numRows];
		for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++) {
				tiles[x][y] = ".";
				if(mapCollisionLayer!=null&&mapCollisionLayer.getCell(x, y)!=null){
					tiles[x][y] = "#";
				}
			}
		}
		if (entities != null) {
			for (Entity e : entities) {
				int x = (int)(e.getCurrentPosition().x/tileW);
				int y = (int)(e.getCurrentPosition().y/tileH);
				if(x<numCols && y < numRows && x >=0 && y >= 0){
					tiles[x][y] = "#";
				}
			}
		}
		if (startPoint != null && startPoint.x<numCols && startPoint.y<numRows) {
			tiles[(int) startPoint.x][(int) startPoint.y] = ".";
		}
		//测试打印
		if(Gdx.app.getLogLevel()==Application.LOG_DEBUG){
			Gdx.app.debug(TAG,"----------------------------------");
			StringBuilder temp = new StringBuilder("\n");
			for (int y = 0; y < numRows; y++) {
				for (int x = 0; x < numCols; x++) {
					temp.append(tiles[x][numRows-1-y]) ;
				}
				temp.append("\n");
			}
			Gdx.app.debug(TAG,temp.toString());
			Gdx.app.debug(TAG,"----------------------------------");
		}
		int index = 0;
		for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++, index++) {
				nodes[x][y] = new MyNode(index, x, y,tiles[x][y], 4);
				indexedNodes.add(nodes[x][y]);
			}
		}
		
		for (int y = 0; y < numRows; y++, index++) {
			for (int x = 0; x < numCols; x++, index++) {
				if (tiles[x][y].equals("#")) {
					continue;
				}
				
				if (x - 1 >= 0 && tiles[x - 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x - 1][y]));
				}
				
				if (x + 1 < numCols && tiles[x + 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x + 1][y]));
				}
				
				if (y - 1 >= 0 && tiles[x][y - 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y - 1]));
				}
				
				if (y + 1 < numRows && tiles[x][y + 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y + 1]));
				}
				
			}
		}
		
		return new MyGraph(indexedNodes);
	}
	
	public static void main(String[] args) {
		// @off - disable libgdx formatter
				final String graphDrawing =
					".....#....\n" +
					".....#....\n" +
					".....#....";
				// @on - enable libgdx formatter

				final MyGraph graph = createGraphFromTextRepresentation(graphDrawing);

				final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<MyNode>(graph);

				final GraphPath<MyNode> outPath = new DefaultGraphPath<MyNode>();

				// @off - disable libgdx formatter
				// 0123456789
				// S....#...E 0
				// .....#.... 10
				// .....#.... 20
				// @on - enable libgdx formatter
				final boolean searchResult = pathfinder.searchNodePath(graph.getNodes().get(0), graph.getNodes().get(20), new ManhattanDistance(),
					outPath);

				System.out.println(""+searchResult);
				System.out.println(""+outPath.getCount());
				for(int i=0;i<outPath.getCount();i++){
					System.out.println(""+outPath.get(i));
				}
	}
	
	private static MyGraph createGraphFromTextRepresentation (final String graphTextRepresentation) {
		final String[][] tiles = createStringTilesFromGraphTextRepresentation(graphTextRepresentation);

		final int numRows = tiles[0].length;
		final int numCols = tiles.length;

		final MyNode[][] nodes = new MyNode[numCols][numRows];
		final Array<MyNode> indexedNodes = new Array<MyNode>(numCols * numRows);

		int index = 0;
		for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++, index++) {
				nodes[x][y] = new MyNode(index, x, y,tiles[x][y], 4);
				indexedNodes.add(nodes[x][y]);
			}
		}

		for (int y = 0; y < numRows; y++, index++) {
			for (int x = 0; x < numCols; x++, index++) {
				if (tiles[x][y].equals("#")) {
					continue;
				}

				if (x - 1 >= 0 && tiles[x - 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x - 1][y]));
				}

				if (x + 1 < numCols && tiles[x + 1][y].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x + 1][y]));
				}

				if (y - 1 >= 0 && tiles[x][y - 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y - 1]));
				}

				if (y + 1 < numRows && tiles[x][y + 1].equals(".")) {
					nodes[x][y].getConnections().add(new DefaultConnection<MyNode>(nodes[x][y], nodes[x][y + 1]));
				}

			}
		}

		return new MyGraph(indexedNodes);
	}

	private static String[][] createStringTilesFromGraphTextRepresentation (final String graphTextRepresentation) {
		final String[] rows = graphTextRepresentation.split("\n");

		final int numRows = rows.length;
		final int numCols = rows[0].length();

		final String[][] tiles = new String[numCols][numRows];

		for (int y = 0; y < numRows; y++) {
			final String row = rows[y];
			for (int x = 0; x < numCols; x++) {
				tiles[x][y] = "" + row.charAt(x);
			}
		}

		return tiles;
	}

}
