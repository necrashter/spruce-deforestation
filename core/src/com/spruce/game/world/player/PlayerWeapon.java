package com.spruce.game.world.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.spruce.game.world.GameWorld;

public abstract class PlayerWeapon {
    public Player player;
    public ModelInstance viewModel;

    public PlayerWeapon(Player player) {
        this.player = player;
    }

    public void setView(Camera camera) {
        if (viewModel != null) {
            viewModel.transform
                    .set(camera.view).inv()
                    .scale(0.33f, 0.33f, 0.33f)
                    .translate(0.5f, -0.75f, -0.5f)
            ;
        }
    }

    public abstract void update(float delta);

    public void render(GameWorld world) {
        world.modelBatch.render(viewModel, world.environment);
    }
}
