package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.v5ent.game.MyRpgGame;
import com.v5ent.game.tools.Utility;

public class CreditScreen extends GameScreen {
    private static String CREDITS_PATH = "licenses/credits.txt";
    private Stage _stage;
    private ScrollPane _scrollPane;
    private MyRpgGame _game;

    public CreditScreen(MyRpgGame game){
        _game = game;
        _stage = new Stage();
        Gdx.input.setInputProcessor(_stage);

        //Get text
        FileHandle file = Gdx.files.internal(CREDITS_PATH);
        String textString = file.readString();

        Label text = new Label(textString, Utility.STATUSUI_SKIN, "credits");
        text.setAlignment(Align.top | Align.center);
        text.setWrap(true);

        _scrollPane = new ScrollPane(text);
        _scrollPane.addListener(new ClickListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        return true;
                                    }

                                    @Override
                                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                        _scrollPane.setScrollY(0);
                                        _scrollPane.updateVisualScroll();
                                        _game.setScreen(_game.getScreenType(MyRpgGame.ScreenType.MainMenu));
                                    }
                               }
        );

        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.defaults().width(Gdx.graphics.getWidth());
        table.add(_scrollPane);

        _stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if( delta == 0){
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _stage.act(delta);
        _stage.draw();

        _scrollPane.setScrollY(_scrollPane.getScrollY()+delta*20);
    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void show() {
        _scrollPane.setVisible(true);
        Gdx.input.setInputProcessor(_stage);
    }

    @Override
    public void hide() {
        _scrollPane.setVisible(false);
        _scrollPane.setScrollY(0);
        _scrollPane.updateVisualScroll();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        _stage.clear();
        _scrollPane = null;
        _stage.dispose();
    }

}