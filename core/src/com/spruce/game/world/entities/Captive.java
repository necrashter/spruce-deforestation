package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;
import com.spruce.game.world.HoverInfo;
import com.spruce.game.world.Usable;
import com.spruce.game.world.levels.LevelObjective;

public class Captive extends NPC implements Pool.Poolable, HoverInfo, Usable {
    public static class FreeCaptivesObjective implements LevelObjective {
        final GameWorld world;
        int freed;
        final int target;
        final LevelObjective nextObjective;

        public FreeCaptivesObjective(GameWorld world, int target, LevelObjective next) {
            this.world = world;
            this.target = target;
            nextObjective = next;
        }

        @Override
        public void init() {
            freed = 0;
        }

        @Override
        public void update(float delta) {}

        @Override
        public void buildHudText(StringBuilder stringBuilder) {
            stringBuilder.append("Captives freed: ").append(freed).append('/').append(target);
        }

        public void captiveFreed() {
            freed += 1;
            if (freed >= target) world.setObjective(nextObjective);
        }
    }

    WaitIdle waitCaptive;
    WaitIdle cheerState;
    RunAwayFromEntity runAway;

    public Captive(final GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", "ManMesh");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);
        maxHealth = Float.POSITIVE_INFINITY;

        waitCaptive = new WaitIdle("captive", 0);

        cheerState = new WaitIdle("charm", 3.0f);
        runAway = new RunAwayFromEntity(true);
        runAway.targetEntity = world.player;
        runAway.onEscaped = new RemoveSelfState();
        cheerState.onTimeout = new State() {
            @Override
            void init() {
                LevelObjective objective = world.getObjective();
                if (objective instanceof FreeCaptivesObjective) {
                    ((FreeCaptivesObjective)objective).captiveFreed();
                }
                switchState(runAway);
            }
        };

        reset();
    }

    @Override
    public void reset() {
        dead = false;
        health = maxHealth;
        switchState(waitCaptive);
    }

    public void respawn() {
        reset();
        Vector2 point = world.randomPointOutsideView();
        setPosition(point.x, point.y);
        octree.add(this);
    }

    public static int totalCount = 0;
    public static final int MIN_COUNT = 10;

    @Override
    public void init() {
        super.init();
        ++totalCount;
    }

    @Override
    public boolean onRemove(boolean worldDisposal) {
        --totalCount;
        if (worldDisposal) return true;
        if (totalCount < MIN_COUNT) {
            respawn();
            return false;
        }
        return true;
    }

    @Override
    public String getInfo(float dist) {
        if (dist <= 2.5f && currentState == waitCaptive) return "Press E to set free";
        return null;
    }

    @Override
    public void use(float dist) {
        if (dist > 2.5f || currentState != waitCaptive) return;
        switchState(cheerState);
    }
}
