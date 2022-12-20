package com.mygdx.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.GameWorld;

public class L4Sainta extends NPC {
    private final float DETECTION_DIST = 12;
    private final float DETECTION_DIST2 = DETECTION_DIST * DETECTION_DIST;
    FollowEntity followPlayer;
    PursueEntity pursueEntity;
    PistolShoot pistolShoot;

    public L4Sainta(GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(MyGdxGame.assets.npcModel, "ManArmature", "Sainta", "pistol");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        // Plot Armor
        health = Float.POSITIVE_INFINITY;
        maxHealth = health;

        pursueEntity = new PursueEntity(true);
        followPlayer = new FollowEntity();
        pistolShoot = new PistolShoot();

        pursueEntity.desiredDistance = 9.0f;
        pursueEntity.checkRay = true;
        pursueEntity.onReached = pistolShoot;
        pistolShoot.onEnd = pursueEntity;
        pursueEntity.onTargetDead = followPlayer;
        setTarget(null);

        followPlayer.prepare(world.player);
        final FilterFunction filterFunction = new FilterFunction() {
            @Override
            public boolean check(GameEntity entity) {
                return entity instanceof L4PistolSuit || entity instanceof L4RifleSuit;
            }
        };
        followPlayer.addStateSwitchCondition(new StateTransitionCondition(pursueEntity) {
            @Override
            boolean check() {
                GameEntity entity = getClosestEntity(DETECTION_DIST, filterFunction);
                if (entity == null) return false;
                setTarget(entity);
                return true;
            }
        });
        switchState(followPlayer);
    }

    public void setTarget(GameEntity entity) {
        pursueEntity.targetEntity = entity;
        pistolShoot.target = entity;
    }
}
