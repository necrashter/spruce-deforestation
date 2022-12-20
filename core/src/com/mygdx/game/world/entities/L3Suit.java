package com.mygdx.game.world.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.geom.SphereShape;

public class L3Suit extends NPC {
    FollowEntity followPlayer;
    PursueEntity pursueSainta;
    public Node weaponNode;

    public L3Suit(final GameWorld world) {
        super(world);
        modelInstance = new ModelInstance(MyGdxGame.assets.npcModel, "ManArmature", "Suit", "pistol");
        weaponNode = modelInstance.getNode("pistol");
        modelInstance.nodes.removeValue(weaponNode, true);
        modelInstance.transform.setToTranslation(hitBox.position);
        animationController = new AnimationController(modelInstance);

        health = 30f;
        maxHealth = health;

        pursueSainta = new PursueEntity(true);
        followPlayer = new FollowEntity();
        followPlayer.prepare(world.player);
        followPlayer.addStateSwitchCondition(
            new StateTransitionCondition(pursueSainta) {
                @Override
                boolean check() {
                    if (SphereShape.position.set(world.player.hitBox.position).sub(pursueSainta.targetEntity.hitBox.position).len2() < 10 * 10) {
                        modelInstance.nodes.add(weaponNode);
                        return true;
                    }
                    return false;
                }
            }
        );
        switchState(followPlayer);
    }

    public void prepareTarget(GameEntity target) {
        pursueSainta.targetEntity = target;
        pursueSainta.desiredDistance = 6.0f;
        pursueSainta.checkRay = true;
        PistolShoot shootOnce = new PistolShoot();
        shootOnce.target = target;
        shootOnce.baseInaccuracy = 0.125f;
        shootOnce.movementInaccuracy = 0.0f;
        pursueSainta.onReached = shootOnce;
        shootOnce.onEnd = pursueSainta;
    }
}
