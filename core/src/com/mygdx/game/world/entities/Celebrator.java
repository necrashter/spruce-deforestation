package com.mygdx.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.geom.SphereShape;

public class Celebrator extends NPC {
    FollowEntity followPlayer;

    public Celebrator(GameWorld world, String modelId) {
        super(world);
        modelInstance = new ModelInstance(MyGdxGame.assets.npcModel, "ManArmature", modelId);
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
