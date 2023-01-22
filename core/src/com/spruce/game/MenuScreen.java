package com.spruce.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    final SpruceGame game;
    private Stage stage;

    public MenuScreen(final SpruceGame game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final TextButton start=new TextButton("Start Game",game.skin);
        start.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startLevel(1);
            }
        });

        final TextButton levelSelect=new TextButton("Level Select",game.skin);
        levelSelect.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                levelSelectDialog();
            }
        });

        TextButton exit=new TextButton("Exit",game.skin);
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        final CheckBox invertMouseCheckbox = new CheckBox("Invert Mouse Y", game.skin);
        invertMouseCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpruceGame.invertMouseY = invertMouseCheckbox.isChecked();
            }
        });
        invertMouseCheckbox.getImage().setScaling(Scaling.fill);
        invertMouseCheckbox.getImageCell().size(24);
        invertMouseCheckbox.left().pad(8);
        invertMouseCheckbox.getLabelCell().pad(8);
        invertMouseCheckbox.setChecked(SpruceGame.invertMouseY);

        Table table=new Table();
        table.setFillParent(true);

        table.padTop(10);//.padLeft(100);
        table.add(start).height(start.getHeight()).width(start.getWidth());
        table.row().padTop(10);
        table.add(levelSelect).height(levelSelect.getHeight()).width(levelSelect.getWidth());
        table.row().padTop(10);
        table.add(exit).height(exit.getHeight()).width(exit.getWidth());
        table.row().padTop(60);
        table.add(invertMouseCheckbox).height(invertMouseCheckbox.getHeight()).width(invertMouseCheckbox.getWidth());

        stage.addActor(table);
    }

    public void startLevel(int level) {
        game.setScreen(game.getLevel(level, 1.0f));
        dispose();
    }

    public void levelSelectDialog() {
        Dialog dialog = new Dialog("Select Level", game.skin) {
            @Override
            protected void result(Object object) {
                int i = (int) object;
                if (i > 0) startLevel(i);
            }
        };
        dialog.button("Go Back", 0);
        dialog.getButtonTable().row();
        dialog.button("1: Wood Chopping", 1);
        dialog.getButtonTable().row();
        dialog.button("2: Man Chopping", 2);
        dialog.getButtonTable().row();
        dialog.button("3: Sainta Claus", 3);
        dialog.getButtonTable().row();
        dialog.button("4: Weapon of X-mass Destruction", 4);
        dialog.padTop(new GlyphLayout(game.skin.getFont("default-font"),"Pause Menu").height*1.2f);
        dialog.padLeft(16); dialog.padRight(16);
        dialog.show(stage);
    }

    @Override
    public void show() {
        SpruceGame.music.fadeOut();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        double s = (double) TimeUtils.millis() / 100.0;
        double y = 100 * Math.sin(s) + 100;
        double x = 100 * Math.cos(s) + 100;
        double b = y > 100 ? (y - 100) * 0.01 : 0;
//        ScreenUtils.clear((float)b, (float)b, (float)b, 1);
        ScreenUtils.clear(0, 0, 0, 1);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
