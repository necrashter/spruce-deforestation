package com.mygdx.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.HoverInfo;
import com.mygdx.game.world.Usable;

public abstract class L3Sainta extends NPC implements HoverInfo, Usable {
    WaitIdle waitIdle;
    PursueEntity pursueEntity;
    RunAwayFromEntity runAwayFromPlayer;

    public L3Sainta(GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(MyGdxGame.assets.npcModel, "ManArmature", "Sainta", "pistol");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        // Plot Armor
        health = Float.POSITIVE_INFINITY;
        maxHealth = health;

        runAwayFromPlayer = new RunAwayFromEntity("run", 6.0f);
        runAwayFromPlayer.targetEntity = world.player;
        runAwayFromPlayer.onEscaped = new RemoveSelfState();

        waitIdle = new WaitIdle("idle", 0);
        switchState(waitIdle);
    }

    public void prepareTarget(GameEntity target) {
        pursueEntity = new PursueEntity("run", 6.0f);
        pursueEntity.targetEntity = target;
        pursueEntity.desiredDistance = 8.0f;
        pursueEntity.checkRay = true;
        PistolShoot shootOnce = new PistolShoot();
        shootOnce.target = target;
        shootOnce.baseInaccuracy = 0.0f;
        shootOnce.movementInaccuracy = 0.0f;
        pursueEntity.onReached = shootOnce;
        pursueEntity.onTargetDead = waitIdle;
        shootOnce.onEnd = pursueEntity;
    }

    public void beginPursuingTarget() {
        switchState(pursueEntity);
    }

    public void beginRunAway() {
        switchState(runAwayFromPlayer);
    }

    @Override
    public String getInfo(float dist) {
        if (dist <= 2.5f && currentState == waitIdle) return "Press E to talk";
        return null;
    }

    @Override
    public void use(float dist) {
        if (dist > 2.5f || currentState != waitIdle) return;
        playerTalk();
    }

    public abstract void playerTalk();
}
