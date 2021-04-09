package com.oh_72.prolit.tools;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Button extends Game {

    Stage stage;
    TextButton button;
    TextButton.TextButtonStyle textButtonStyle;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;

    private Texture settings;

    @Override
    public void create() {
        settings = new Texture("settings.png");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = new TextureRegionDrawable(settings);
        textButtonStyle.down = new TextureRegionDrawable(settings);
        textButtonStyle.checked = new TextureRegionDrawable(settings);
        button = new TextButton("Button1", textButtonStyle);
        stage.addActor(button);
    }


    @Override
    public void render() {
        super.render();
        stage.draw();
    }
}
