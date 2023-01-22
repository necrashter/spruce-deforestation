package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;
import com.spruce.game.world.HoverInfo;
import com.spruce.game.world.Usable;

public class Suit extends NPC implements HoverInfo, Usable {
    PursueEntity pursueEntity;
    FollowEntity followPlayer;
    boolean angry = false;
    public boolean levelDone = false;

    public Node weaponNode;

    public Suit(GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", "Suit", "pistol");
        weaponNode = modelInstance.getNode("pistol");
        modelInstance.nodes.removeValue(weaponNode, true);
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);
        // Plot armor
        health = Float.POSITIVE_INFINITY;
        maxHealth = health;

        pursueEntity = new PursueEntity(true);
        pursueEntity.targetEntity = world.player;
        pursueEntity.desiredDistance = 10.0f;
        pursueEntity.checkRay = true;
        PistolShoot shootOnce = new PistolShoot();
        shootOnce.target = world.player;
        shootOnce.baseInaccuracy = 0.0f;
        shootOnce.movementInaccuracy = 0.0f;
        shootOnce.damage = 10f;
        pursueEntity.onReached = shootOnce;
        shootOnce.onEnd = pursueEntity;

        followPlayer = new FollowEntity();
        followPlayer.prepare(world.player);
        switchState(followPlayer);
    }

    @Override
    public boolean takeDamage(float amount, DamageAgent agent, DamageSource source) {
        boolean out = super.takeDamage(amount, agent, source);
        if (agent == DamageAgent.Player && !angry && !dead) {
            switchState(pursueEntity);
            modelInstance.nodes.add(weaponNode);
            angry = true;
        }
        return out;
    }

    @Override
    public String getInfo(float dist) {
        if (dist <= 2.5f && levelDone) return "Press E to talk";
        return angry ? "An angry Mr. Suit" : "Mr. Suit";
    }

    public void playerTalk() {}

    @Override
    public void use(float dist) {
        if (dist > 2.5f || angry) return;
        playerTalk();
    }
}
