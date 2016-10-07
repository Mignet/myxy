package com.v5ent.game.ui;

public interface StoreInventoryObserver {
    public static enum StoreInventoryEvent {
        PLAYER_GP_TOTAL_UPDATED,
        PLAYER_INVENTORY_UPDATED
    }

    void onNotify(String value, StoreInventoryEvent event);
}
