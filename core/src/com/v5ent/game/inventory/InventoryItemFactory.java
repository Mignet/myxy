package com.v5ent.game.inventory;


import java.util.ArrayList;
import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.v5ent.game.inventory.InventoryItem.ItemTypeID;
import com.v5ent.game.tools.Utility;

public class InventoryItemFactory {

    private Json _json = new Json();
    private final String INVENTORY_ITEM = "scripts/inventory_items.json";
    private static InventoryItemFactory _instance = null;
    private Hashtable<ItemTypeID,InventoryItem> _inventoryItemList;

    public static InventoryItemFactory getInstance() {
        if (_instance == null) {
            _instance = new InventoryItemFactory();
        }

        return _instance;
    }

    private InventoryItemFactory(){
        ArrayList<JsonValue> list = _json.fromJson(ArrayList.class, Gdx.files.internal(INVENTORY_ITEM));
        _inventoryItemList = new Hashtable<ItemTypeID, InventoryItem>();

        for (JsonValue jsonVal : list) {
            InventoryItem inventoryItem = _json.readValue(InventoryItem.class, jsonVal);
            _inventoryItemList.put(inventoryItem.getItemTypeID(), inventoryItem);
        }
    }

    public InventoryItem getInventoryItem(ItemTypeID inventoryItemType){
        InventoryItem item = new InventoryItem(_inventoryItemList.get(inventoryItemType));
        item.setDrawable(new TextureRegionDrawable(Utility.ITEMS_TEXTUREATLAS.findRegion(item.getItemTypeID().toString())));
        item.setScaling(Scaling.none);
        return item;
    }

    /*
    public void testAllItemLoad(){
        for(ItemTypeID itemTypeID : ItemTypeID.values()) {
            InventoryItem item = new InventoryItem(_inventoryItemList.get(itemTypeID));
            item.setDrawable(new TextureRegionDrawable(PlayerHUD.itemsTextureAtlas.findRegion(item.getItemTypeID().toString())));
            item.setScaling(Scaling.none);
        }
    }*/

}
