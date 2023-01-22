package com.spruce.game.world.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.Damageable;

public class Axe extends PlayerWeapon {
    public float damage = 5.0f;
    public float range = 1.5f;
    public float knockback = 10.0f;

    private float swingRotation = 0.0f;
    private float roll = 0.0f;
    private float nextRoll = 0.0f;
    private boolean notHitYet = true;
    private boolean noSoundYet = true;

    public Axe(Player player, ModelInstance viewModel) {
        super(player);
        this.viewModel = viewModel;
    }

    public Axe(Player player) {
        super(player);
        this.viewModel = new ModelInstance(SpruceGame.assets.allModel, "axe");
    }

    @Override
    public void update(float delta) {
        float rollDiff = nextRoll - roll;
        if (Math.abs(rollDiff) < 1) {
            roll = nextRoll;
        } else {
            roll += Math.signum(nextRoll - roll) * delta*100f;
        }
        if (player.firing1) {
            swingRotation += delta * 6.0f;
            if (swingRotation > 2.0f) {
                swingRotation -= 2.0f;
                notHitYet = true;
                noSoundYet = true;
                nextRoll = MathUtils.random(-40f, 40f);
            }
        } else if (swingRotation != 0) {
            nextRoll = 0;
            swingRotation += delta * 6.0f;
            if (swingRotation > 2.0f) {
                swingRotation = 0.0f;
                notHitYet = true;
                noSoundYet = true;
            }
        }
        if (noSoundYet && swingRotation > 0.5f) {
            noSoundYet = false;
            SpruceGame.assets.swooshes[MathUtils.random.nextInt(SpruceGame.assets.swooshes.length)].play();
        }
        if (notHitYet && swingRotation > 1.5f) {
            hit();
        }
    }

    private void hit() {
        notHitYet = false;
        if (player.aimIntersection.t > range) {
            return;
        }
        if (player.aimIntersection.object != null) {
            if (player.aimIntersection.object instanceof Damageable) {
                final Damageable damageable = (Damageable) player.aimIntersection.object;
                damageable.takeDamage(damage, Damageable.DamageAgent.Player, Damageable.DamageSource.Axe);
            }
        } else if (player.aimIntersection.entity != null) {
            player.aimIntersection.entity.takeDamage(damage, Damageable.DamageAgent.Player, Damageable.DamageSource.Axe);
            player.aimIntersection.entity.applyImpulse(
                    player.aim.direction.x * knockback,
                    player.aim.direction.y * knockback,
                    player.aim.direction.z * knockback
            );
        }
    }

    public void setView(Camera camera) {
        if (viewModel != null) {
            float rollOffset = MathUtils.sin(MathUtils.degreesToRadians * roll) * 0.25f;
            viewModel.transform
                    .set(camera.view).inv()
                    .scale(0.33f, 0.33f, 0.33f)
                    .translate(0.5f + rollOffset, -0.75f, -0.5f)
                    .rotate(Vector3.X, (float) Math.sin(swingRotation * MathUtils.PI)*45f)
                    .rotate(Vector3.Z, roll)
            ;
        }
    }
}
