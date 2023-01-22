package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;

public class Celebrator extends NPC {
    FollowEntity followPlayer;

    public Celebrator(GameWorld world, String modelId) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", modelId);
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        health = Float.POSITIVE_INFINITY;
        maxHealth = health;

        followPlayer = new FollowEntity();
        followPlayer.idleAnimation = "charm";
        followPlayer.prepare(world.player);
        switchState(followPlayer);
    }
}
