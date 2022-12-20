package com.mygdx.game.world.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.Damageable;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.geom.Shape;

public class Firearm extends PlayerWeapon {
    public static final Vector3 temp1 = new Vector3();
    public static final Vector3 temp2 = new Vector3();
    public Decal decal = Decal.newDecal(MyGdxGame.assets.muzzleFlashRegion, true);
    public final Sound shootSound;
    public final Sound reloadSound;
    public final Vector3 muzzlePoint;

    public float damage = 5.0f;
    public float knockback = 10.0f;

    private float nextRoll = 0.0f;
    private float decalRotation = 0.0f;

    private float progress = 0.0f;
    private boolean noSoundYet = true;
    private enum State {
        Ready,
        Firing,
        Reloading,
    }
    private State state = State.Ready;

    public static class Template {
        public final ModelInstance model;
        /**
         * Used for pickup objects.
         */
        public final Shape shape;
        public final Vector3 muzzlePoint;

        public final Sound shootSound;
        public final Sound reloadSound;

        public Template(ModelInstance model, Shape shape, Vector3 muzzlePoint, Sound shootSound, Sound reloadSound) {
            this.model = model;
            this.shape = shape;
            this.muzzlePoint = muzzlePoint;
            this.shootSound = shootSound;
            this.reloadSound = reloadSound;
        }
    }

    public Firearm(Player player, Template template) {
        super(player);
        this.viewModel = template.model.copy();
        this.muzzlePoint = template.muzzlePoint;
        this.shootSound = template.shootSound;
        this.reloadSound = template.reloadSound;
    }

    public void playShootSound() {
        float ammoRatio = (ammoInClip / maxAmmoInClip);
        shootSound.play(1.0f, ammoRatio*0.25f+1f, 0);
        if (ammoRatio < 0.5f) {
            MyGdxGame.assets.gunEmpty.play((0.5f-ammoRatio)*2);
        }
    }

    @Override
    public void update(float delta) {
        if (state == State.Ready) {
            if (player.firing1) {
                playShootSound();
                state = State.Firing;
                player.world.decalPool.addBulletTrace(decal.getPosition(), player.getAimTargetPoint());
                decalRotation = MathUtils.random(0, MathUtils.PI2);
                nextRoll = MathUtils.random(-recoveryRoll, recoveryRoll);
                if (player.aimIntersection.object != null) {
                    if (player.aimIntersection.object instanceof Damageable) {
                        final Damageable damageable = (Damageable) player.aimIntersection.object;
                        damageable.takeDamage(damage, Damageable.DamageAgent.Player, Damageable.DamageSource.Firearm);
                    }
                } else if (player.aimIntersection.entity != null) {
                    player.aimIntersection.entity.takeDamage(damage, Damageable.DamageAgent.Player, Damageable.DamageSource.Firearm);
                }
            } else if (player.shouldReload && ammoInClip < maxAmmoInClip) {
                player.shouldReload = false;
                beginReload();
            }
        } else if (state == State.Reloading) {
            progress += delta * reloadSpeed;
            if (progress > 1.0f) {
                progress = 0.0f;
                state = State.Ready;
                ammoInClip = maxAmmoInClip;
            }
        }

        if (state == State.Firing) {
            progress += delta * recoverySpeed;
            decal.setColor(1.0f, 1.0f, 1.0f, 1.0f - progress);
            if (noSoundYet && progress > 0.5f) {
                noSoundYet = false;
            }
            if (progress > 1.0f) {
                noSoundYet = true;
                if (--ammoInClip > 0) {
                    progress = 0.0f;
                    state = State.Ready;
                } else {
                    beginReload();
                }
                nextRoll = 0;
            }
        }
    }

    void beginReload() {
        reloadSound.play();
        state = State.Reloading;
        progress = 0.0f;
    }

    public float recoveryTranslateZ = 0.125f;
    public float recoveryRoll = 20f;
    public float recoveryPitch = 10f;
    public float recoverySpeed = 8.0f;

    public float reloadSpeed = 1.0f;
    public float ammoInClip = 30;
    public float maxAmmoInClip = 30;

    public void setView(Camera camera) {
        if (viewModel != null) {
            final float o = 0.75f;
            viewModel.transform
                    .set(camera.view).inv()
                    .scale(0.33f, 0.33f, 0.33f)
                    .translate(o, -0.65f, -o)
            ;
            float progressCos = (1.0f-(float) Math.cos(progress * MathUtils.PI * 2.0f)) * 0.5f;

            if (state == State.Firing) {
                decal.setScale(0.0008f);
                temp1.set(muzzlePoint).mul(viewModel.transform);
                decal.setPosition(temp1);
                temp2.set(camera.position).mulAdd(camera.direction, 4f).sub(temp1).scl(-1);
                decal.setRotation(temp2.nor(), temp1.set(camera.up).nor());
                decal.getRotation().mul(0, 0, MathUtils.sin(decalRotation), MathUtils.cos(decalRotation));

                viewModel.transform
                        .translate(0, 0, progressCos * recoveryTranslateZ)
                        .rotate(Vector3.Z, nextRoll * progressCos)
                        .rotate(Vector3.X, progressCos * recoveryPitch)
                ;
            } else if (state == State.Reloading) {
                viewModel.transform
                        .translate(0, 0, progressCos * -0.5f)
                        .rotate(Vector3.X, (MathUtils.cos(progress * MathUtils.PI)*.5f-.5f) * 360)
                ;
            }
        }
    }

    public void render(GameWorld world) {
        super.render(world);
        if (state == State.Firing) {// && noSoundYet) {
            world.decalBatch.add(decal);
        }
    }
}
