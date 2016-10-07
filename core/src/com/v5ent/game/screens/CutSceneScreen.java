package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.v5ent.game.MyRpgGame;
import com.v5ent.game.audio.AudioObserver;
import com.v5ent.game.battle.MonsterFactory;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.entity.EntityFactory;
import com.v5ent.game.map.Map;
import com.v5ent.game.map.MapFactory;
import com.v5ent.game.profile.ProfileManager;
import com.v5ent.game.sfx.ScreenTransitionAction;
import com.v5ent.game.sfx.ScreenTransitionActor;
import com.v5ent.game.tools.Utility;
import com.v5ent.game.ui.AnimatedImage;

public class CutSceneScreen extends MainGameScreen {
    private MyRpgGame _game;
    private Stage _stage;
    private Viewport _viewport;
    private Stage _UIStage;
    private Viewport _UIViewport;
    private Actor _followingActor;
    private Dialog _messageBoxUI;
    private Label _label;
    private boolean _isCameraFixed = true;
    private ScreenTransitionActor _transitionActor;
    private Action _introCutSceneAction;
    private Action _switchScreenAction;
    private Action _setupScene01;
    private Action _setupScene02;
    private Action _setupScene03;
    private Action _setupScene04;
    private Action _setupScene05;

    private AnimatedImage _animBlackSmith;
    private AnimatedImage _animInnKeeper;
    private AnimatedImage _animMage;
    private AnimatedImage _animFire;
    private AnimatedImage _animDemon;

    public CutSceneScreen(MyRpgGame game) {
        super(game);

        _game = game;

        _viewport = new ScreenViewport(_camera);
        _stage = new Stage(_viewport);

        _UIViewport = new ScreenViewport(_hudCamera);
        _UIStage = new Stage(_UIViewport);

        _label = new Label("Test", Utility.STATUSUI_SKIN);
        _label.setWrap(true);

        _messageBoxUI = new Dialog("", Utility.STATUSUI_SKIN, "solidbackground");
        _messageBoxUI.setVisible(false);
        _messageBoxUI.getContentTable().add(_label).width(_stage.getWidth()/2).pad(10, 10, 10, 0);
        _messageBoxUI.pack();
        _messageBoxUI.setPosition(_stage.getWidth() / 2 - _messageBoxUI.getWidth() / 2, _stage.getHeight() - _messageBoxUI.getHeight());

        _followingActor = new Actor();
        _followingActor.setPosition(0, 0);

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE);

        _animBlackSmith = getAnimatedImage(EntityFactory.EntityName.TOWN_BLACKSMITH);
        _animInnKeeper = getAnimatedImage(EntityFactory.EntityName.TOWN_INNKEEPER);
        _animMage = getAnimatedImage(EntityFactory.EntityName.TOWN_MAGE);
        _animFire = getAnimatedImage(EntityFactory.EntityName.FIRE);
        _animDemon = getAnimatedImage(MonsterFactory.MonsterEntityType.MONSTER042);

        //Actions
        _switchScreenAction = new RunnableAction(){
            @Override
            public void run() {
                _game.setScreen(_game.getScreenType(MyRpgGame.ScreenType.MainMenu));
            }
        };

        _setupScene01 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                _mapMgr.loadMap(MapFactory.MapType.TOWN,new Vector2(16,14).scl(32));
                _mapMgr.disableCurrentmapMusic();
                setCameraPosition(16*32, 14*32);

                _animBlackSmith.setVisible(true);
                _animInnKeeper.setVisible(true);
                _animMage.setVisible(true);

                _animBlackSmith.setPosition(16*32, 14*32);
                _animInnKeeper.setPosition(22*32, 15*32);
                _animMage.setPosition(19*32, 17*32);

                _animDemon.setVisible(false);
                _animFire.setVisible(false);
            }
        };

        _setupScene02 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                _mapMgr.loadMap(MapFactory.MapType.TOP_WORLD,new Vector2(50,30).scl(32));
                _mapMgr.disableCurrentmapMusic();
                setCameraPosition(50*32, 30*32);

                _animBlackSmith.setPosition(50*32, 30*32);
                _animInnKeeper.setPosition(52*32, 30*32);
                _animMage.setPosition(50*32, 28*32);

                _animFire.setPosition(52*32, 28*32);
                _animFire.setVisible(true);
            }
        };

        _setupScene03 = new RunnableAction() {
            @Override
            public void run() {
                _animDemon.setPosition(52*32, 28*32);
                _animDemon.setVisible(true);
                hideMessage();
            }
        };

        _setupScene04 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                _animBlackSmith.setVisible(false);
                _animInnKeeper.setVisible(false);
                _animMage.setVisible(false);
                _animFire.setVisible(false);

                _mapMgr.loadMap(MapFactory.MapType.TOP_WORLD,new Vector2(16,16).scl(32));
                _mapMgr.disableCurrentmapMusic();

                _animDemon.setVisible(true);
                _animDemon.setScale(1, 1);
                _animDemon.setSize(16 * Map.UNIT_SCALE, 16 * Map.UNIT_SCALE);
                _animDemon.setPosition(50*32, 40*32);

                followActor(_animDemon);
            }
        };

        _setupScene05 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                _animBlackSmith.setVisible(false);
                _animInnKeeper.setVisible(false);
                _animMage.setVisible(false);
                _animFire.setVisible(false);

                _mapMgr.loadMap(MapFactory.MapType.CASTLE_OF_DOOM,new Vector2(15,1).scl(32));
                _mapMgr.disableCurrentmapMusic();
                followActor(_animDemon);

                _animDemon.setVisible(true);
                _animDemon.setPosition(15*32, 1*32);
            }
        };

        _transitionActor = new ScreenTransitionActor();

         //layout
        _stage.addActor(_animMage);
        _stage.addActor(_animBlackSmith);
        _stage.addActor(_animInnKeeper);
        _stage.addActor(_animFire);
        _stage.addActor(_animDemon);
        _stage.addActor(_transitionActor);

        _UIStage.addActor(_messageBoxUI);
    }

    private Action getCutsceneAction(){
        _setupScene01.reset();
        _setupScene02.reset();
        _setupScene03.reset();
        _setupScene04.reset();
        _setupScene05.reset();
        _switchScreenAction.reset();

        return Actions.sequence(
                Actions.addAction(_setupScene01),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), _transitionActor),
                Actions.delay(3),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("铁匠: 我们已经计划了这么久，现在是时候了...");
                            }
                        }),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("魔法师: 这是黑魔法. 我们必须谨慎对待,否则我们都没好下场!");
                            }
                        }),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("旅馆老板: 小声点，如果被人知道我们使用黑魔法，我们都会被绞死!");
                            }
                        }),
                Actions.delay(5),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), _transitionActor),
                Actions.delay(3),
                Actions.addAction(_setupScene02),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), _transitionActor),
                Actions.delay(3),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("铁匠: 现在,我们继续吧.我真的非常不喜欢这个墓地...");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("魔法师: 我告诉过你, 我们不能匆忙. 让人起死回生并不简单!");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("旅馆老板: 我知道你爱你女儿,但这是不对的...");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("铁匠: 你从来没有自己的孩子。你不明白!");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("魔法师: 你们都需要集中精神, 等等...嗷不, 有点不对劲!!");
                            }
                        }
                ),
                Actions.delay(7),
                Actions.addAction(_setupScene03),
                Actions.addAction(Actions.fadeOut(2), _animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeIn(2), _animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeOut(2), _animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeIn(2), _animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeOut(2), _animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.fadeIn(2), _animDemon),
                Actions.delay(2),
                Actions.addAction(Actions.scaleBy(40, 40, 5, Interpolation.linear), _animDemon),
                Actions.delay(5),
                Actions.addAction(Actions.moveBy(20, 0), _animDemon),
                Actions.delay(2),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("铁匠: 我们...我们都做了些什么...");
                            }
                        }
                ),
                Actions.delay(3),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), _transitionActor),
                Actions.delay(3),
                Actions.addAction(_setupScene04),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), _transitionActor),
                Actions.addAction(Actions.moveTo(54, 65, 13, Interpolation.linear), _animDemon),
                Actions.delay(10),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), _transitionActor),
                Actions.delay(3),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3), _transitionActor),
                Actions.addAction(_setupScene05),
                Actions.addAction(Actions.moveTo(15, 76, 15, Interpolation.linear), _animDemon),
                Actions.delay(15),
                Actions.run(
                        new Runnable() {
                            @Override
                            public void run() {
                                showMessage("恶魔: 现在，我将派遣我的恶魔军团，来摧毁这些肉馕!");
                            }
                        }
                ),
                Actions.delay(5),
                Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3), _transitionActor),
                Actions.delay(5),
                Actions.after(_switchScreenAction)
        );

    }

    private AnimatedImage getAnimatedImage(EntityFactory.EntityName entityName){
        Entity entity = EntityFactory.getInstance().getEntityByName(entityName);
        return setEntityAnimation(entity);
    }

    private AnimatedImage getAnimatedImage(MonsterFactory.MonsterEntityType entityName){
        Entity entity = MonsterFactory.getInstance().getMonster(entityName);
        return setEntityAnimation(entity);
    }

    private AnimatedImage setEntityAnimation(Entity entity){
        final AnimatedImage animEntity = new AnimatedImage();
        animEntity.setEntity(entity);
        animEntity.setSize(animEntity.getWidth() * Map.UNIT_SCALE, animEntity.getHeight() * Map.UNIT_SCALE);
        return animEntity;
    }

    public void followActor(Actor actor){
        _followingActor = actor;
        _isCameraFixed = false;
    }

    public void setCameraPosition(float x, float y){
        _camera.position.set(x, y, 0f);
        _isCameraFixed = true;
    }

    public void showMessage(String message){
        _label.setText(message);
        _messageBoxUI.pack();
        _messageBoxUI.setVisible(true);
    }

    public void hideMessage(){
        _messageBoxUI.setVisible(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _mapRenderer.setView(_camera);

        _mapRenderer.getBatch().enableBlending();
        _mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if( _mapMgr.hasMapChanged() ){
            _mapRenderer.setMap(_mapMgr.getCurrentTiledMap());
            _mapMgr.setMapChanged(false);
        }

        _mapRenderer.render();

        if( !_isCameraFixed ){
            _camera.position.set(_followingActor.getX(), _followingActor.getY(), 0f);
        }
        _camera.update();

        _UIStage.act(delta);
        _UIStage.draw();

        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void show() {
        _introCutSceneAction = getCutsceneAction();
        _stage.addAction(_introCutSceneAction);
        notify(AudioObserver.AudioCommand.MUSIC_STOP_ALL, AudioObserver.AudioTypeEvent.NONE);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE);
        ProfileManager.getInstance().removeAllObservers();
        if( _mapRenderer == null ){
            _mapRenderer = new OrthogonalTiledMapRenderer(_mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
        }
    }

    @Override
    public void hide() {
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_INTRO_CUTSCENE);
        ProfileManager.getInstance().removeAllObservers();
        Gdx.input.setInputProcessor(null);
    }

}