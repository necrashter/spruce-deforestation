package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;

public class L4PistolSuit extends NPC implements Pool.Poolable {
    PursueEntity pursueEntity;
    PistolShoot shootOnce;

    public L4PistolSuit(GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", "Suit", "pistol");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        // States
        pursueEntity = new PursueEntity(true);
        pursueEntity.targetEntity = world.player;
//        pursueEntity.checkRay = true;
        shootOnce = new PistolShoot();
        shootOnce.target = world.player;

        // Transitions
        pursueEntity.onReached = shootOnce;
        shootOnce.onEnd = pursueEntity;

        maxHealth = 20f;
        health = maxHealth;

        reset();
    }

    @Override
    public void reset() {
        dead = false;
        pursueEntity.desiredDistance = 6.0f + MathUtils.random()*3.0f;
        initialized = false;
        switchState(pursueEntity);
    }

    public void spawn(float newHealth) {
        maxHealth = newHealth;
        spawn();
    }

    public void spawn() {
        health = maxHealth;
        Vector2 point = world.randomPointOutsideView();
        setPosition(point.x, point.y);
        world.octree.add(this);
        init();
        updateTransform();
    }

    public static class Pool extends com.badlogic.gdx.utils.Pool<L4PistolSuit> {
        final GameWorld level;

        public Pool(GameWorld level, int limit) {
            super(limit, limit);
            this.level = level;
            fill(limit);
        }

        @Override
        protected L4PistolSuit newObject() {
            return new L4PistolSuit(level) {
                @Override
                public boolean onRemove(boolean worldDisposal) {
                    boolean out = super.onRemove(worldDisposal);
                    free(this);
                    return out;
                }
            };
        }

        public int getAlive() {
            return max - getFree();
        }

        public void spawn(int count) {
            for (int i = 0; i < count; ++i) {
                obtain().spawn();
            }
        }

        public void spawn(int count, float health) {
            for (int i = 0; i < count; ++i) {
                obtain().spawn(health);
            }
        }
    }
}
