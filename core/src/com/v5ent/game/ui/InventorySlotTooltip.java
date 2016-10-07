package com.v5ent.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.v5ent.game.inventory.InventoryItem;

public class InventorySlotTooltip extends Window {

    private Skin _skin;
    private Label _description;

    public InventorySlotTooltip(final Skin skin){
        super("", skin);
        this._skin = skin;

        _description = new Label("", skin, "inventory-item-count");

        this.add(_description);
        this.padLeft(5).padRight(5);
        this.pack();
        this.setVisible(false);
    }

    public void setVisible(InventorySlot inventorySlot, boolean visible) {
        super.setVisible(visible);

        if( inventorySlot == null ){
            return;
        }

        if (!inventorySlot.hasItem()) {
            super.setVisible(false);
        }
    }

    public void updateDescription(InventorySlot inventorySlot){
        if( inventorySlot.hasItem() ){
            StringBuilder string = new StringBuilder();
            InventoryItem item = inventorySlot.getTopInventoryItem();
            string.append(item.getItemShortDescription());
            if( item.isInventoryItemOffensive() ){
                string.append(System.getProperty("line.separator"));
                string.append(String.format("攻击力: %s", item.getItemUseTypeValue()));
            }else if( item.isInventoryItemDefensive() ){
                string.append(System.getProperty("line.separator"));
                string.append(String.format("防御力: %s", item.getItemUseTypeValue()));
            }
            string.append(System.getProperty("line.separator"));
            string.append(String.format("售出价: %s 金币", item.getItemValue()));
            string.append(System.getProperty("line.separator"));
            string.append(String.format("收购价: %s 金币", item.getTradeValue()));

            _description.setText(string);
            this.pack();
        }else{
            _description.setText("");
            this.pack();
        }

    }
}
