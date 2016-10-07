package com.v5ent.game.ecs;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;

public abstract class InputComponent extends ComponentSubject implements Component, InputProcessor {

    protected Direction _currentDirection = null;
    protected Entity.State _currentState = null;
    protected Json _json;

    /** P=Pause Q=quit    */
    protected enum Keys {
        LEFT, RIGHT, UP, DOWN, 
        LEFTUP, RIGHTUP, LEFTDOWN, RIGHTDOWN,
        QUIT, PAUSE
    }

    protected enum Mouse {
        SELECT, DOACTION
    }

    protected static Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
    protected static Map<Mouse, Boolean> mouseButtons = new HashMap<Mouse, Boolean>();

    //initialize the hashmap for inputs
    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.LEFTUP, false);
        keys.put(Keys.RIGHTUP, false);
        keys.put(Keys.LEFTDOWN, false);
        keys.put(Keys.RIGHTDOWN, false);
        keys.put(Keys.QUIT, false);
        keys.put(Keys.PAUSE, false);
    };

    static {
        mouseButtons.put(Mouse.SELECT, false);
        mouseButtons.put(Mouse.DOACTION, false);
    };

    public InputComponent(){
        _json = new Json();
    }

    public abstract void update(Entity entity, float delta);

}
