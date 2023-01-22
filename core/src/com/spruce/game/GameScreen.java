package com.spruce.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.spruce.game.world.GameWorld;
import com.spruce.game.world.GameWorldRenderer;
import com.spruce.game.world.LowResWorldRenderer;
import com.spruce.game.world.levels.ScriptedEvent;

public class GameScreen implements Screen {
    public static final float CROSSHAIR_SIZE = 48f;
    final SpruceGame game;

    public final GameWorld world;
    private GameWorldRenderer worldRenderer;

    private final Stage stage;

    private final WidgetGroup hudGroup;
    private final Label label;
    private final Label bottomLabel;

    private final WidgetGroup subtitleGroup;
    private final Label subtitleLabel;

    private final Dialog pauseDialog;
    private Dialog currentDialog = null;
    private final Queue<Dialog> dialogQueue = new Queue<>();

    private final Image hurtOverlay;

    public Touchpad movementTouch;

    public GameScreen(final SpruceGame game, final GameWorld world) {
        this.game = game;

        this.world = world;
        world.screen = this;
        worldRenderer = new LowResWorldRenderer(world);
//        worldRenderer = world;

        stage = new Stage(new ScreenViewport());

        {
            hurtOverlay = new Image(SpruceGame.assets.hurtOverlay);
            Container<Image> container = new Container<>(hurtOverlay);
            container.setFillParent(true);
            container.fill();
            stage.addActor(container);
            hurtOverlay.setColor(1, 1, 1, 0);
        }

        {
            hudGroup = new WidgetGroup();
            hudGroup.setFillParent(true);

            label = new Label("f", game.skin);
            Container<Label> labelContainer = new Container<>(label);
            labelContainer.setFillParent(true);
            labelContainer.top().left().pad(20);
            hudGroup.addActor(labelContainer);

            bottomLabel = new Label("", game.skin);
            Container<Label> labelContainer1 = new Container<>(bottomLabel);
            labelContainer1.setFillParent(true);
            labelContainer1.center().padTop(200f);
            hudGroup.addActor(labelContainer1);

            Texture crosshairTexture = SpruceGame.assets.get("crosshair010.png");
            Image crosshairImage = new Image(crosshairTexture);
            Container<Image> crosshairContainer = new Container<>(crosshairImage);
            crosshairContainer.setFillParent(true);
            crosshairContainer.size(CROSSHAIR_SIZE).center();
            hudGroup.addActor(crosshairContainer);

            stage.addActor(hudGroup);
        }

        {
            subtitleGroup = new WidgetGroup();
            subtitleGroup.setFillParent(true);

            final Image backgroundImage = new Image(SpruceGame.assets.bottomGrad);
            Container<Image> backgroundContainer = new Container<>(backgroundImage);
            backgroundContainer.setFillParent(true);
            backgroundContainer.fill(1.0f, 0.33f).bottom();
            subtitleGroup.addActor(backgroundContainer);

            subtitleLabel = new Label("", game.skin);
            Container<Label> labelContainer1 = new Container<>(subtitleLabel);
            labelContainer1.setFillParent(true);
            labelContainer1.center().bottom().padBottom(80f);
            subtitleGroup.addActor(labelContainer1);

            if (SpruceGame.isMobile()) {
                TextButton nextButton = new TextButton("Next", game.skin);
                nextButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if (activeSubtitle != null) activeSubtitle.shouldFade = true;
                        return true;
                    }
                });
                Container<TextButton> nextButtonContainer = new Container<>(nextButton);
                nextButtonContainer.setFillParent(true);
                nextButtonContainer.pad(40).align(Align.right | Align.center);
                subtitleGroup.addActor(nextButtonContainer);
            } else {
                Label label1 = new Label("Press SPACE to continue...", game.skin, "old-font", Color.WHITE);
                Container<Label> labelContainer2 = new Container<>(label1);
                labelContainer2.setFillParent(true);
                labelContainer2.center().bottom().padBottom(10f);
                subtitleGroup.addActor(labelContainer2);
            }

//            stage.addActor(subtitleGroup);

            subtitleGroup.setColor(1, 1, 1, 0);
        }

        {
            pauseDialog = new Dialog("Pause Menu", game.skin);

            pauseDialog.padTop(new GlyphLayout(game.skin.getFont("default-font"),"Pause Menu").height*1.2f);
            pauseDialog.padLeft(16); pauseDialog.padRight(16);

            {
                final TextButton button = new TextButton("Resume", game.skin);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        togglePause();
                    }
                });
                pauseDialog.getButtonTable().add(button).height(button.getHeight()).width(button.getWidth()).row();
            }

            {
                final TextButton button = new TextButton("Restart Level", game.skin);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        restart();
                    }
                });
                pauseDialog.getButtonTable().add(button).height(button.getHeight()).width(button.getWidth()).row();
            }

            {
                final TextButton button = new TextButton("Exit Game", game.skin);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        mainMenu();
                    }
                });
                pauseDialog.getButtonTable().add(button).height(button.getHeight()).width(button.getWidth()).row();
            }
        }
        Gdx.input.setInputProcessor(new InputMultiplexer(
                stage, world.player.inputAdapter
        ));
        if (SpruceGame.isMobile()) {
            movementTouch = new Touchpad(0, game.skin);
            float touchpadSize = 240f;
            Container<Touchpad> movementTouchContainer = new Container<>(movementTouch);
            movementTouchContainer.setFillParent(true);
            movementTouchContainer.bottom().left().pad(20).size(touchpadSize);
            hudGroup.addActor(movementTouchContainer);

            TextButton shootButton = new TextButton("JUMP", game.skin);
            shootButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!world.player.inputAdapter.disabled) {
                        world.player.jump();
                    }
                    return true;
                }
            });
            Container<TextButton> shootButtonContainer = new Container<>(shootButton);
            shootButtonContainer.setFillParent(true);
            shootButtonContainer.pad(40).align(Align.bottomRight);
            hudGroup.addActor(shootButtonContainer);

            TextButton menuButton = new TextButton("Menu", game.skin);
            menuButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pause();
                }
            });
            Container<TextButton> menuButtonContainer = new Container<>(menuButton);
            menuButtonContainer.setFillParent(true);
            menuButtonContainer.pad(20).align(Align.center | Align.top);
            stage.addActor(menuButtonContainer);

            TextButton useButton = new TextButton("USE", game.skin);
            useButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    world.player.useKeyPressed();
                }
            });
            TextButton reloadButton = new TextButton("RELOAD", game.skin);
            reloadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    world.player.shouldReload = true;
                }
            });
            TextButton nextWeaponButton = new TextButton("SWITCH", game.skin);
            nextWeaponButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    world.player.nextWeapon();
                }
            });
            Table topLeftTable = new Table(game.skin);
            topLeftTable.setFillParent(true);
            topLeftTable.pad(20).align(Align.topRight);
            topLeftTable.add(useButton).pad(5).row();
            topLeftTable.add(nextWeaponButton).pad(5).row();
            topLeftTable.add(reloadButton).pad(5).row();
            hudGroup.addActor(topLeftTable);
        }
        setPaused(false);

        world.addedToScreen();
    }

    public void getPlayerMovement(Vector2 movement) {
        float x = 0.0f;
        float y = 0.0f;
        if (movementTouch != null) {
            x += movementTouch.getKnobPercentY();
            y += movementTouch.getKnobPercentX();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            x += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            x -= 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            y += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            y -= 1.0f;
        }
        movement.set(x, y);
        if (movement.len2() > 1) movement.nor();
    }

    @Override
    public void render(float delta) {
        // Input handling
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }
        if (!world.player.inputAdapter.disabled) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                delta *= 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                delta *= 0.1f;
            }
            getPlayerMovement(world.player.movementInput);
        } else {
            world.player.movementInput.setZero();
        }

        // Update
        if (currentDialog != null && currentDialog.getStage() == null) {
            currentDialog = null;
            if (dialogQueue.notEmpty()) {
                showDialog(dialogQueue.removeFirst(), false);
            } else {
                setPaused(false);
            }
        }
        world.update(delta);

        bottomLabel.setText(world.player.getHoverInfo());
        stage.act(delta);

//        double s = (double) TimeUtils.millis() / 500.0;
//        double y = 100 * Math.sin(s) + 100;
//        double b = y > 100 ? (y - 100) * 0.01 : 0;
//        ScreenUtils.clear((float)b, (float)b, (float)b, 1);
//        ScreenUtils.clear(1, 0, 0, 1, true);

        worldRenderer.render();

        stage.getViewport().apply();
        stage.draw();

        StringBuilder stringBuilder = new StringBuilder();
        world.buildHudText(stringBuilder);
        label.setText(stringBuilder);
//        stringBuilder.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
//        stringBuilder.append(" Visible: ").append(world.visibleCount);
//        stringBuilder.append('\n');
//        stringBuilder.append("x ").append(world.player.hitBox.position.x);
//        stringBuilder.append(" y ").append(world.player.hitBox.position.y);
//        stringBuilder.append(" z ").append(world.player.hitBox.position.z);
//        stringBuilder.append('\n');
//        stringBuilder.append("Hit: ").append(world.player.aimIntersection.type);
//        stringBuilder.append(" at t ").append(world.player.aimIntersection.t);
//        stringBuilder.append('\n');
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.screenResize(width, height);
        stage.getViewport().update(width, height, true);
    }

    public void togglePause() {
        setPaused(!world.paused);
        if (world.paused) pauseDialog.show(stage);
        else pauseDialog.hide();
    }

    @Override
    public void pause() {
        if (world.paused) return;
        pauseDialog.show(stage);
        setPaused(true);
    }

    @Override
    public void resume() {
    }

    public void setPaused(boolean v) {
        if (world.paused == v) return;
        world.paused = v;
        if (!SpruceGame.isMobile()) Gdx.input.setCursorCatched(!world.paused);
        if (world.paused) {
            SpruceGame.music.paused();
        } else {
            SpruceGame.music.resumed();
            world.player.resetMouse();
        }
    }

    public void showDialog(Dialog dialog, boolean pause) {
        if (currentDialog != null) {
            dialogQueue.addLast(dialog);
        } else {
            currentDialog = dialog;
            dialog.show(stage);
            if (pause) setPaused(true);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // World renderer is supposed to dispose world as well.
        worldRenderer.dispose();
        stage.dispose();
    }

    public void playerDied() {
        Dialog dialog = new Dialog("You Died!", game.skin) {
            @Override
            protected void result(Object object) {
                int i = (int) object;
                switch (i) {
                    case 0: restart(); break;
                    case 1: restartEasier(); break;
                    default: mainMenu(); break;
                }
            }
        };
        dialog.button("Restart", 0);
        dialog.getButtonTable().row();
        dialog.button("Restart (Easier)", 1);
        dialog.getButtonTable().row();
        dialog.button("Main Menu", 2);
        dialog.padTop(new GlyphLayout(game.skin.getFont("default-font"),"Pause Menu").height*1.2f);
        dialog.padLeft(16); dialog.padRight(16);
        showDialog(dialog, true);
    }

    public void playerHurt() {
        hurtOverlay.setColor(1, 1, 1, 1);
        hurtOverlay.clearActions();
        hurtOverlay.addAction(Actions.fadeOut(0.5f));
    }

    public void gameWon() {
        Dialog dialog = new Dialog("Level Complete!", game.skin) {
            @Override
            protected void result(Object object) {
                int i = (int) object;
                switch (i) {
                    case 0: nextLevel(); break;
                    case 1: mainMenu(); break;
                }
            }
        };
        dialog.button("Next Level", 0);
        dialog.getButtonTable().row();
        dialog.button("Main Menu", 1);
        dialog.padTop(new GlyphLayout(game.skin.getFont("default-font"),"Pause Menu").height*1.2f);
        dialog.padLeft(16); dialog.padRight(16);
        showDialog(dialog, true);
    }

    public void mainMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }

    public void restart() {
        game.setScreen(game.getLevel(world.level, world.easiness));
        dispose();
    }

    public void restartEasier() {
        game.setScreen(game.getLevel(world.level, world.easiness + 1.0f));
        dispose();
    }

    public void nextLevel() {
        game.setScreen(game.getLevel(world.level + 1, 1.0f));
        dispose();
    }

    private SubtitleScriptedEvent activeSubtitle = null;
    public class SubtitleScriptedEvent implements ScriptedEvent {
        final String text;
        boolean shouldFade = false;

        public SubtitleScriptedEvent(String text) {
            this.text = text;
        }

        @Override
        public void activate() {
            activeSubtitle = this;
            subtitleLabel.setText(text);
            if (subtitleGroup.getStage() == stage) {
                hudGroup.clearActions();
                subtitleGroup.clearActions();
                return;
            }
            hudGroup.addAction(Actions.fadeOut(0.3f));
            stage.addActor(subtitleGroup);
            subtitleGroup.addAction(Actions.fadeIn(0.3f));
        }

        @Override
        public boolean update(float delta) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || shouldFade) {
                activeSubtitle = null;
                hudGroup.addAction(Actions.fadeIn(0.3f));
                subtitleGroup.addAction(Actions.fadeOut(0.3f));
                subtitleGroup.addAction(Actions.removeActor());
                return true;
            }
            return false;
        }
    }

    public SubtitleScriptedEvent subtitle(String text) {
        return new SubtitleScriptedEvent(text);
    }

    public class WinGameEvent extends ScriptedEvent.OneTimeEvent {
        @Override
        public void activate() {
            gameWon();
        }
    }

    public WinGameEvent winGameEvent() {
        return new WinGameEvent();
    }
}
