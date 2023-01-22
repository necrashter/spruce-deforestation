package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;

public class PistolWanderer extends NPC {
    public static int totalCount = 0;
    public static final int MIN_COUNT = 15;
    public static final float DETECTION_DISTANCE = 12;
    public static final Vector3 temp = new Vector3();

    PursueEntity pursueEntity;
    WalkToRandomPosition walkToRandomPosition;
    PistolShoot shootOnce;

    public PistolWanderer(final GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", "Suit", "pistol");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        // States
        walkToRandomPosition = new WalkToRandomPosition();
        RandomWaitIdle randomWaitIdle = new RandomWaitIdle("idle", 3.0f, 9.0f);

        pursueEntity = new PursueEntity(true);
        pursueEntity.targetEntity = world.player;
//        pursueEntity.checkRay = true;
        shootOnce = new PistolShoot();
        shootOnce.target = world.player;

        // Transitions
        walkToRandomPosition.onReached = randomWaitIdle;
        randomWaitIdle.onTimeout = walkToRandomPosition;

        StateTransitionCondition playerInRangeCondition = new StateTransitionCondition(pursueEntity) {
            @Override
            boolean check() {
                temp.set(world.player.hitBox.position).sub(hitBox.position);
                if (temp.len2() < DETECTION_DISTANCE * DETECTION_DISTANCE) {
                    switchState(pursueEntity);
                    return true;
                }
                return false;
            }
        };
        walkToRandomPosition.addStateSwitchCondition(playerInRangeCondition);
        randomWaitIdle.addStateSwitchCondition(playerInRangeCondition);

        pursueEntity.onReached = shootOnce;
        shootOnce.onEnd = pursueEntity;

        randomize();
        switchState(walkToRandomPosition);
    }

    public void randomize() {
        dead = false;
        maxHealth = 20.0f;
        health = maxHealth;

        pursueEntity.desiredDistance = 6.0f + MathUtils.random()*3.0f;
    }

    public void respawn() {
        randomize();
        Vector2 point = world.randomPointOutsideView();
        setPosition(point.x, point.y);
        switchState(walkToRandomPosition);
        octree.add(this);
    }

    @Override
    public void init() {
        super.init();
        ++totalCount;
    }

    @Override
    public boolean onRemove(boolean worldDisposal) {
        super.onRemove(worldDisposal);
        --totalCount;
        if (worldDisposal) return true;
        if (totalCount < MIN_COUNT) {
            respawn();
            return false;
        }
        return true;
    }

    @Override
    public boolean takeDamage(float amount, DamageAgent agent, DamageSource source) {
        boolean out = super.takeDamage(amount, agent, source);
        if (dead) return out;
        if (currentState != pursueEntity && currentState != shootOnce && agent == DamageAgent.Player) {
            switchState(pursueEntity);
        }
        return out;
    }
}
