package com.v5ent.game.map;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.entity.Direction;
/***
 * 动态滚动地图
 * @author Mignet
 *
 */
public class ScollMap {
private static final String TAG = ScollMap.class.getSimpleName();

	public static final float SUB_MAP_WIDTH = 10f;
	public static final float SUB_MAP_HEIGHT = 7.5f;
//	preloadMap
	public HashMap<String,Texture> visibleMap = new HashMap<String,Texture>();
	public AssetManager manager = new AssetManager();
	public Vector2 current,preCurrent = null;
	public String ext = "png";
	protected MapManager _mapMgr;
	
	public ScollMap(MapManager mapMgr){
		_mapMgr = mapMgr;
	}
	//初始化人物所在位置地图
		public void initMap(boolean bg){
			Vector2 center = _mapMgr.getPlayerPositionInMap();
			Object _mapName = _mapMgr.getCurrentTiledMap().getProperties().get("mapName");
			int width = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("width").toString())/10f);
			int height = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("height").toString())/SUB_MAP_HEIGHT);
			if(_mapName!=null){
				String mapName = _mapName.toString();
				if(!bg){
					 mapName = _mapName.toString()+"_";
					 ext = "png";
				}else{
					ext = "png";
				}
				int x0 = Math.max((int)(center.x/SUB_MAP_WIDTH),0);
				int y0 = Math.max((int)(center.y/SUB_MAP_HEIGHT),0);
				Gdx.app.debug(TAG, "x0:"+x0+",y0:"+y0);
				Gdx.app.debug(TAG, "===INIT MAP IMAGES===");
//					visibleMap.clear();
//					manager.clear();
					for(int i=x0-2;i<=x0+2;i++){
						for(int j=y0-2;j<=y0+2;j++){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPath  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							manager.load(srcPath, Texture.class);
							Gdx.app.debug(TAG, "load:x:"+i+",y:"+j+",bg:"+bg);
						}
					}
//					long start = (System.currentTimeMillis());
					manager.finishLoading();
					//System.err.println((System.currentTimeMillis()-start)+" in "+(bg?"bg":"fg"));
					for(int i=x0-2;i<=x0+2;i++){
						for(int j=y0-2;j<=y0+2;j++){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPath  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							if(manager.isLoaded(srcPath)){
								// texture is available, let's fetch it and do something interesting
								Texture tex = manager.get(srcPath, Texture.class);
								visibleMap.put(i+"-"+j+"-"+bg, tex);
							}
						}
					}
					//preload
					/*for(int j=y0-2;j<=y0+2;j++){
						for(int i=x0-2;i<=x0+2;i++){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							//有可能补充的
							if(!manager.isLoaded(srcPathAdd)){
								manager.load(srcPathAdd, Texture.class);
								Gdx.app.debug(TAG, "预加载补充：x:"+i+",y:"+j+",bg:"+bg);
							}
						}
					}
					manager.update();*/
				}
			preCurrent = center.cpy();
		}
		
	/**
	 * 预加载图块
	 * @param dir
	 * @param bg
	 */
	private void preloadMap(Direction dir,boolean bg){
		Vector2 center = _mapMgr.getPlayerPositionInMap();
		Object _mapName = _mapMgr.getCurrentTiledMap().getProperties().get("mapName");
		int width = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("width").toString())/SUB_MAP_WIDTH);
		int height = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("height").toString())/SUB_MAP_HEIGHT);
		if(_mapName!=null){
			String mapName = _mapName.toString();
			if(!bg){
				 mapName = _mapName.toString()+"_";
				 ext = "png";
			}else{
				ext = "png";
			}
			int x0 = Math.max((int)(center.x/SUB_MAP_WIDTH),0);
			int y0 = Math.max((int)(center.y/SUB_MAP_HEIGHT),0);
			if(preCurrent!=null&&center.x==preCurrent.x && center.y==preCurrent.y)return;
			Gdx.app.debug(TAG, "x0:"+x0+",y0:"+y0);
			if(dir == Direction.LEFT){
				x0--;
				for(int j=y0-2;j<=y0+2;j++){
					for(int i=x0-2;i<=x0+2;i++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "预加载补充：x:"+i+",y:"+j+",bg:"+bg);
//							manager.update();
						}
					}
				}
			}else if(dir == Direction.RIGHT){
				x0++;
				for(int j=y0-2;j<=y0+2;j++){
					for(int i=x0-2;i<=x0+2;i++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "预加载补充：x:"+i+",y:"+j+",bg:"+bg);
//							manager.update();
						}
					}
				}
			}else if(dir == Direction.DOWN){
				y0--;
				for(int i=x0-2;i<=x0+2;i++){
					for(int j=y0-2;j<=y0+2;j++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "预加载补充：x:"+i+",y:"+j+",bg:"+bg);
//							manager.update();
						}
					}
				}
			}else if(dir == Direction.UP){
				y0++;
				for(int i=x0-2;i<=x0+2;i++){
					for(int j=y0-2;j<=y0+2;j++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "预加载补充：x:"+i+",y:"+j+",bg:"+bg);
//							manager.update();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 动态滚动地图
	 * @param dir
	 * @param bg
	 */
	private void scollMap(Direction dir,boolean bg){
		Vector2 center = _mapMgr.getPlayerPositionInMap();
		Object _mapName = _mapMgr.getCurrentTiledMap().getProperties().get("mapName");
		int width = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("width").toString())/SUB_MAP_WIDTH);
		int height = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("height").toString())/SUB_MAP_HEIGHT);
		if(_mapName!=null){
			String mapName = _mapName.toString();
			if(!bg){
				 mapName = _mapName.toString()+"_";
				 ext = "png";
			}else{
				ext = "png";
			}
			int x0 = Math.max((int)(center.x/SUB_MAP_WIDTH),0);
			int y0 = Math.max((int)(center.y/SUB_MAP_HEIGHT),0);
			if(current!=null&&x0==(int)(current.x/SUB_MAP_WIDTH) && y0==(int)(current.y/SUB_MAP_HEIGHT))return;
			Gdx.app.debug(TAG, "x0:"+x0+",y0:"+y0);
			if(dir == Direction.LEFT){
				for(int j=y0-2;j<=y0+2;j++){
					for(int i=x0-2;i<=x0+2;i++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null&&!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "LEFT补充：x:"+i+",y:"+j+",bg:"+bg);
						}
					}
					//移除
					int i = x0+3;
					if(i<0||j<0||i>=width||j>=height)continue;
					String srcPathSub  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
					Gdx.app.debug(TAG, "LEFT减掉：x:"+i+",y:"+j+",bg:"+bg);
					if(visibleMap.get(i+"-"+j+"-"+bg) != null){
						manager.unload(srcPathSub);
					}
				}
//				long start = (System.currentTimeMillis());
//				manager.finishLoading();
//				//System.err.println((System.currentTimeMillis()-start)+" in "+(bg?"bg":"fg"));
				for(int j=y0-2;j<=y0+2;j++){
					for(int i=x0-2;i<=x0+2;i++){
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							if(manager.isLoaded(srcPathAdd)){
								Texture tex = manager.get(srcPathAdd, Texture.class);
								visibleMap.put(i+"-"+j+"-"+bg, tex);
								Gdx.app.debug(TAG, "LEFT-map补充：x:"+i+",y:"+j+",bg:"+bg);
							}
						}
					}
					//移除
					int i = x0+3;
					if(i<0||j<0||i>=width||j>=height)continue;
					Gdx.app.debug(TAG, "LEFT-map减掉：x:"+i+",y:"+j+",bg:"+bg);
					visibleMap.remove(i+"-"+j+"-"+bg);
				}
			}else if(dir == Direction.RIGHT){
				for(int j=y0-2;j<=y0+2;j++){
					for(int i=x0-2;i<=x0+2;i++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null&&!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "RIGHT补充：x:"+i+",y:"+j+",bg:"+bg);
						}
					}
					//移除
					int i = x0-3;
					if(i<0||j<0||i>=width||j>=height)continue;
					String srcPathSub  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
					Gdx.app.debug(TAG, "RIGHT减掉：x:"+i+",y:"+j+",bg:"+bg);
					if(visibleMap.get(i+"-"+j+"-"+bg) != null){
						manager.unload(srcPathSub);
					}
				}
//				long start = (System.currentTimeMillis());
//				manager.finishLoading();
//				//System.err.println((System.currentTimeMillis()-start)+" in "+(bg?"bg":"fg"));
				for(int j=y0-2;j<=y0+2;j++){
					for(int i=x0-2;i<=x0+2;i++){
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							if(manager.isLoaded(srcPathAdd)){
								Texture tex = manager.get(srcPathAdd, Texture.class);
								visibleMap.put(i+"-"+j+"-"+bg, tex);
								Gdx.app.debug(TAG, "RIGHT-map补充：x:"+i+",y:"+j+",bg:"+bg);
							}
						}
					}
					//移除
					int i = x0-3;
					if(i<0||j<0||i>=width||j>=height)continue;
					Gdx.app.debug(TAG, "RIGHT-map减掉：x:"+i+",y:"+j+",bg:"+bg);
					visibleMap.remove(i+"-"+j+"-"+bg);
				}
			}else if(dir == Direction.DOWN){
				for(int i=x0-2;i<=x0+2;i++){
					for(int j=y0-2;j<=y0+2;j++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null&&!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "DOWN补充：x:"+i+",y:"+j+",bg:"+bg);
						}
					}
					//移除
					int j = y0+3;
					if(i<0||j<0||i>=width||j>=height)continue;
					String srcPathSub  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
					Gdx.app.debug(TAG, "DOWN减掉：x:"+i+",y:"+j+",bg:"+bg);
					if(visibleMap.get(i+"-"+j+"-"+bg) != null){
						manager.unload(srcPathSub);
					}
				}
//				long start = (System.currentTimeMillis());
//				manager.finishLoading();
//				//System.err.println((System.currentTimeMillis()-start)+" in "+(bg?"bg":"fg"));
				for(int i=x0-2;i<=x0+2;i++){
					for(int j=y0-2;j<=y0+2;j++){
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							if(manager.isLoaded(srcPathAdd)){
								Texture tex = manager.get(srcPathAdd, Texture.class);
								visibleMap.put(i+"-"+j+"-"+bg, tex);
								Gdx.app.debug(TAG, "DOWN-map补充：x:"+i+",y:"+j+",bg:"+bg);
							}
						}
					}
					//移除
					int j = y0+3;
					if(i<0||j<0||i>=width||j>=height)continue;
					Gdx.app.debug(TAG, "DOWN-map减掉：x:"+i+",y:"+j+",bg:"+bg);
					visibleMap.remove(i+"-"+j+"-"+bg);
				}
			}else if(dir == Direction.UP){
				for(int i=x0-2;i<=x0+2;i++){
					for(int j=y0-2;j<=y0+2;j++){
						if(i<0||j<0||i>=width||j>=height)continue;
						String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null&&!manager.isLoaded(srcPathAdd)){
							manager.load(srcPathAdd, Texture.class);
							Gdx.app.debug(TAG, "UP补充：x:"+i+",y:"+j+",bg:"+bg);
						}
					}
					//移除
					int j = y0 - 3;
					if(i<0||j<0||i>=width||j>=height)continue;
					String srcPathSub  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
					Gdx.app.debug(TAG, "UP减掉：x:"+i+",y:"+j+",bg:"+bg);
					if(visibleMap.get(i+"-"+j+"-"+bg) != null){
						manager.unload(srcPathSub);
					}
				}
//				long start = (System.currentTimeMillis());
//				manager.finishLoading();
//				//System.err.println((System.currentTimeMillis()-start)+" in "+(bg?"bg":"fg"));
				for(int i=x0-2;i<=x0+2;i++){
					for(int j=y0-2;j<=y0+2;j++){
						//有可能补充的
						if(visibleMap.get(i+"-"+j+"-"+bg) == null){
							if(i<0||j<0||i>=width||j>=height)continue;
							String srcPathAdd  = "maps/"+mapName+"/"+i+"-"+j+"."+ext;
							if(manager.isLoaded(srcPathAdd)){
								Texture tex = manager.get(srcPathAdd, Texture.class);
								visibleMap.put(i+"-"+j+"-"+bg, tex);
								Gdx.app.debug(TAG, "UP-map补充：x:"+i+",y:"+j+",bg:"+bg);
							}
						}
					}
					//移除
					int j = y0 - 3;
					if(i<0||j<0||i>=width||j>=height)continue;
					Gdx.app.debug(TAG, "UP-map减掉：x:"+i+",y:"+j+",bg:"+bg);
					visibleMap.remove(i+"-"+j+"-"+bg);
				}
			}
		}
		preCurrent = center.cpy();
	}
	
	public void drawScollImage(Batch batch,boolean bg) {
		batch.begin();
		for (Entry<String, Texture> entry : visibleMap.entrySet()) {
			   Texture t = entry.getValue();
			   String[] point = entry.getKey().split("-");
			   float i = Float.valueOf(point[0]);
			   float j = Float.valueOf(point[1]);
			   boolean bm = Boolean.valueOf(point[2]);
			   if(t!=null && bm == bg){
				  batch.draw(t, (i*SUB_MAP_WIDTH)*32, (j*SUB_MAP_HEIGHT)*32);
			   }
		}
		batch.end();
	}
	
	public void scollSubMaps(MapManager _mapMgr){
		long start = System.currentTimeMillis();
		//人物坐标FocusPoint
		Vector2 center = _mapMgr.getPlayerPositionInMap();
		if( _mapMgr.hasMapChanged() || current==null){
			initMap(true);
			initMap(false);
		}else{
			int width = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("width").toString()));
			int height = (int)(Integer.valueOf(_mapMgr.getCurrentTiledMap().getProperties().get("height").toString()));
			//预加载
			if(center.x+SUB_MAP_WIDTH<=width && center.x > current.x && center.x < (int)(current.x/SUB_MAP_WIDTH)*SUB_MAP_WIDTH+SUB_MAP_WIDTH){
				preloadMap(Direction.RIGHT,true);
				preloadMap(Direction.RIGHT,false);
//				preloadFrontMap(Direction.RIGHT,manager);
			}
			if(center.x-SUB_MAP_WIDTH>=0 && center.x < current.x && center.x > (int)(current.x/SUB_MAP_WIDTH)*SUB_MAP_WIDTH){
				preloadMap(Direction.LEFT,true);
				preloadMap(Direction.LEFT,false);
//				preloadFrontMap(Direction.LEFT,manager);
			}
			if(center.y+SUB_MAP_HEIGHT<=height && center.y > current.y && center.y < ((int)(current.y/SUB_MAP_HEIGHT)*SUB_MAP_HEIGHT+SUB_MAP_HEIGHT)){
				preloadMap(Direction.UP,true);
				preloadMap(Direction.UP,false);
//				preloadFrontMap(Direction.UP,manager);
			}
			if(center.y-SUB_MAP_HEIGHT>=0 && center.y < current.y && center.y > (int)(current.y/SUB_MAP_HEIGHT)*SUB_MAP_HEIGHT){
				preloadMap(Direction.DOWN,true);
				preloadMap(Direction.DOWN,false);
//				preloadFrontMap(Direction.DOWN,manager);
			}
			//真加载
			if(center.x+SUB_MAP_WIDTH<=width && center.x >= (int)(current.x/SUB_MAP_WIDTH)*SUB_MAP_WIDTH+SUB_MAP_WIDTH){
				scollMap(Direction.RIGHT,true);
				scollMap(Direction.RIGHT,false);
			}
			if(center.x-SUB_MAP_WIDTH>=0 &&  center.x <= (int)(current.x/SUB_MAP_WIDTH)*SUB_MAP_WIDTH){
				scollMap(Direction.LEFT,true);
				scollMap(Direction.LEFT,false);
			}
			if(center.y+SUB_MAP_HEIGHT<=height && center.y>=((int)(current.y/SUB_MAP_HEIGHT)*SUB_MAP_HEIGHT+SUB_MAP_HEIGHT)){
				scollMap(Direction.UP,true);
				scollMap(Direction.UP,false);
			}
			if(center.y-SUB_MAP_HEIGHT>=0 && center.y<=(int)(current.y/SUB_MAP_HEIGHT)*SUB_MAP_HEIGHT){
				scollMap(Direction.DOWN,true);
				scollMap(Direction.DOWN,false);
			}
		}
		current = center.cpy();
		if(manager.update(1)){
//			Gdx.app.error(TAG, "scollSubMaps 加载完成:");
			long hs = (System.currentTimeMillis()-start);
			if(hs>0)
				Gdx.app.debug(TAG, "scollSubMaps每次滚动耗时:"+hs);
		}
	}
	
	public void dispose() {
		manager.dispose();
	}
}
