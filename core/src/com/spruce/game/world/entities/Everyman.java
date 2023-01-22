package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;

public class Everyman extends NPC {
    WalkToRandomPosition walkToRandomPosition;
    RandomCheerOrIdle randomCheerOrIdle;
    PursueToStrike pursueToStrike;
    RunAwayFromEntity runAway;

    float runAwayHealth;

    class RanAway extends State {
        @Override
        void init() {
            remove();
        }
    }

    public void respawn() {
        randomize();
        Vector2 point = world.randomPointOutsideView();
        setPosition(point.x, point.y);
        switchState(walkToRandomPosition);
        octree.add(this);
    }

    @Override
    public boolean onRemove(boolean worldDisposal) {
        super.onRemove(worldDisposal);
        if (!worldDisposal) {
            respawn();
            return false;
        }
        return true;
    }

    public Everyman(GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", "ManMesh", "candy-bar");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        // States
        pursueToStrike = new PursueToStrike(MathUtils.randomBoolean());
        pursueToStrike.prepare(world.player);
        walkToRandomPosition = new WalkToRandomPosition();
        randomCheerOrIdle = new RandomCheerOrIdle(3.0f, 9.0f);

        // State transitions
        walkToRandomPosition.onReached = randomCheerOrIdle;
        randomCheerOrIdle.onTimeout = walkToRandomPosition;

//        runAway = new RunAwayFromEntity(true);
        runAway = new RunAwayFromEntity("run", 0);
        runAway.targetEntity = world.player;
        runAway.desiredDistance = world.viewDistance + 1.0f;
        runAway.onEscaped = new RanAway();

        randomize();
        switchState(walkToRandomPosition);
    }

    public void randomize() {
        dead = false;
        maxHealth = 20f + MathUtils.random.nextInt(3) * 5.0f;
        health = maxHealth;

        runAway.movementSpeed = 3f + MathUtils.random() * 2f;

        runAwayHealth = MathUtils.random(-10, 10);
    }

    @Override
    public boolean takeDamage(float amount, DamageAgent agent, DamageSource source) {
        boolean out = super.takeDamage(amount, agent, source);
        if (dead) return out;

        if (health <= runAwayHealth) {
            switchState(runAway);
        } else if (currentState == walkToRandomPosition || currentState == randomCheerOrIdle) {
            switchState(pursueToStrike);
        }
        return out;
    }
}
