package com.v5ent.game.entity;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.v5ent.game.entity.Entity.AnimationType;
import com.v5ent.game.inventory.InventoryItem.ItemTypeID;

public class EntityConfig {
    private Array<AnimationConfig> animationConfig;
    private Array<ItemTypeID> inventory;
    private Entity.State state = Entity.State.IDLE;
    private Direction direction = Direction.DOWN;
    private String entityID;
    private String entityName;
    private String conversationConfigPath;
    private String questConfigPath;
    private String currentQuestID;
    private String itemTypeID;
    /**Collision Box*/
    private int boxWidth = 16;
    private int boxHeight = 16;
    private ObjectMap<String, String> entityProperties;

    public static enum EntityProperties{
        ENTITY_HEALTH_POINTS,
        ENTITY_ATTACK_POINTS,
        ENTITY_DEFENSE_POINTS,
        ENTITY_HIT_DAMAGE_TOTAL,
        ENTITY_XP_REWARD,
        ENTITY_GP_REWARD,
        NONE
    }

    EntityConfig(){
        animationConfig = new Array<AnimationConfig>();
        inventory = new Array<ItemTypeID>();
        entityProperties = new ObjectMap<String, String>();
    }

    EntityConfig(EntityConfig config){
        state = config.getState();
        direction = config.getDirection();
        entityID = config.getEntityID();
        conversationConfigPath = config.getConversationConfigPath();
        questConfigPath = config.getQuestConfigPath();
        currentQuestID = config.getCurrentQuestID();
        itemTypeID = config.getItemTypeID();
        boxWidth = config.getBoxWidth();
        boxHeight = config.getBoxHeight();

        animationConfig = new Array<AnimationConfig>();
        animationConfig.addAll(config.getAnimationConfig());

        inventory = new Array<ItemTypeID>();
        inventory.addAll(config.getInventory());

        entityProperties = new ObjectMap<String, String>();
        entityProperties.putAll(config.entityProperties);
    }

    public ObjectMap<String, String> getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(ObjectMap<String, String> entityProperties) {
        this.entityProperties = entityProperties;
    }

    public void setPropertyValue(String key, String value){
        entityProperties.put(key, value);
    }

    public String getPropertyValue(String key){
        Object propertyVal = entityProperties.get(key);
        if( propertyVal == null ) return new String();
        return propertyVal.toString();
    }

    public String getCurrentQuestID() {
        return currentQuestID;
    }

    public void setCurrentQuestID(String currentQuestID) {
        this.currentQuestID = currentQuestID;
    }

    public String getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(String itemTypeID) {
        this.itemTypeID = itemTypeID;
    }

    public String getQuestConfigPath() {
        return questConfigPath;
    }

    public void setQuestConfigPath(String questConfigPath) {
        this.questConfigPath = questConfigPath;
    }

    public String getConversationConfigPath() {
        return conversationConfigPath;
    }

    public void setConversationConfigPath(String conversationConfigPath) {
        this.conversationConfigPath = conversationConfigPath;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Entity.State getState() {
        return state;
    }

    public void setState(Entity.State state) {
        this.state = state;
    }

    public Array<AnimationConfig> getAnimationConfig() {
        return animationConfig;
    }

    public void addAnimationConfig(AnimationConfig animationConfig) {
        this.animationConfig.add(animationConfig);
    }

    public Array<ItemTypeID> getInventory() {
        return inventory;
    }

    public void setInventory(Array<ItemTypeID> inventory) {
        this.inventory = inventory;
    }

    /**
	 * @return the boxWidth
	 */
	public int getBoxWidth() {
		return boxWidth;
	}

	/**
	 * @param boxWidth the boxWidth to set
	 */
	public void setBoxWidth(int boxWidth) {
		this.boxWidth = boxWidth;
	}

	/**
	 * @return the boxHeight
	 */
	public int getBoxHeight() {
		return boxHeight;
	}

	/**
	 * @param boxHeight the boxHeight to set
	 */
	public void setBoxHeight(int boxHeight) {
		this.boxHeight = boxHeight;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public static class AnimationConfig{
        private float frameDuration = 1.0f;
        private AnimationType animationType;
        private Array<String> texturePaths;
        private Array<GridPoint2> gridPoints;
        private int frameWidth = 16;
        private int frameHeight = 16;

        public AnimationConfig(){
            animationType = AnimationType.IDLE;
            texturePaths = new Array<String>();
            gridPoints = new Array<GridPoint2>();
        }

        public float getFrameDuration() {
            return frameDuration;
        }

        public void setFrameDuration(float frameDuration) {
            this.frameDuration = frameDuration;
        }

        public Array<String> getTexturePaths() {
            return texturePaths;
        }

        public void setTexturePaths(Array<String> texturePaths) {
            this.texturePaths = texturePaths;
        }

        public Array<GridPoint2> getGridPoints() {
            return gridPoints;
        }

        public void setGridPoints(Array<GridPoint2> gridPoints) {
            this.gridPoints = gridPoints;
        }

        public AnimationType getAnimationType() {
            return animationType;
        }

        public void setAnimationType(AnimationType animationType) {
            this.animationType = animationType;
        }

		/**
		 * @return the frameWidth
		 */
		public int getFrameWidth() {
			return frameWidth;
		}

		/**
		 * @param frameWidth the frameWidth to set
		 */
		public void setFrameWidth(int frameWidth) {
			this.frameWidth = frameWidth;
		}

		/**
		 * @return the frameHeight
		 */
		public int getFrameHeight() {
			return frameHeight;
		}

		/**
		 * @param frameHeight the frameHeight to set
		 */
		public void setFrameHeight(int frameHeight) {
			this.frameHeight = frameHeight;
		}
    }

}
