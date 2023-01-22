package com.spruce.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;

public class L4RifleSuit extends NPC implements Pool.Poolable {
    PursueEntity pursueEntity;
    RifleShoot shootOnce;

    public L4RifleSuit(GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(SpruceGame.assets.npcModel, "ManArmature", "Suit", "autorifle");
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        muzzlePoint.set(autoRifleMuzzlePoint);

        // States
        pursueEntity = new PursueEntity(true);
        pursueEntity.targetEntity = world.player;
//        pursueEntity.checkRay = true;
        shootOnce = new RifleShoot();
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

    public static class Pool extends com.badlogic.gdx.utils.Pool<L4RifleSuit> {
        final GameWorld level;
        boolean firstKill;

        public Pool(GameWorld level, int limit) {
            super(limit, limit);
            this.level = level;
            firstKill = true;
            fill(limit);
        }

        @Override
        protected L4RifleSuit newObject() {
            return new L4RifleSuit(level) {
                @Override
                public boolean onRemove(boolean worldDisposal) {
                    if (worldDisposal) return true;
                    if (firstKill) {
                        Vector3 pickupPos = new Vector3(world.terrain.getPoint(hitBox.position.x, hitBox.position.z));
                        octree.add(SpruceGame.assets.createAutoRiflePickup(world.player, pickupPos).spawnAnimation());
                        firstKill = false;
                    } else {
                        super.onRemove(false);
                    }
                    free(this);
                    return true;
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
