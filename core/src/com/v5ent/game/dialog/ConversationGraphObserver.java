package com.v5ent.game.dialog;

public interface ConversationGraphObserver {
    public static enum ConversationCommandEvent {
        LOAD_STORE_INVENTORY,
        EXIT_CONVERSATION,
        ACCEPT_QUEST,
        INN_SLEEP,
        ADD_ENTITY_TO_INVENTORY,
        RETURN_QUEST,
        NONE
    }

    void onNotify(final ConversationGraph graph, ConversationCommandEvent event);
}
