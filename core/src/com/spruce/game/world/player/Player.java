package com.spruce.game.world.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.spruce.game.world.HoverInfo;
import com.spruce.game.world.Usable;
import com.spruce.game.world.entities.GameEntity;
import com.spruce.game.world.GameWorld;
import com.spruce.game.SpruceGame;
import com.spruce.game.world.geom.RayIntersection;

public class Player extends GameEntity {
    public abstract class PlayerInput extends InputAdapter {
        public boolean disabled = false;

        public void update(float delta) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                jump();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                shouldReload = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) useKeyPressed();
        }

        @Override
        public boolean keyDown(int keycode) {
            if (disabled) return false;
            switch (keycode) {
                case Input.Keys.Q:
                    nextWeapon();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (disabled) return false;
            if (amountY < 0) {
                prevWeapon();
                return true;
            } else if (amountY > 0) {
                nextWeapon();
                return true;
            }
            return false;
        }
    }

    public class MobileInputAdapter extends PlayerInput {
        private int lastButton = -1;
        private int touched;
        private boolean multiTouch;
        long lastTouch = 0;

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            long now = TimeUtils.millis();
            if (now - lastTouch < 400) {
                firing1 = true;
            }
            lastTouch = now;
            touched |= (1 << pointer);
            multiTouch = !MathUtils.isPowerOfTwo(touched);
            if (multiTouch) {
                lastButton = -1;
            } else if (lastButton < 0) {
                lastX = screenX;
                lastY = screenY;
                lastButton = button;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            boolean result = super.touchDragged(screenX, screenY, pointer);
            if (result || lastButton < 0) return result;
            movePointer(screenX, screenY);
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            touched &= ~(1 << pointer);
            multiTouch = !MathUtils.isPowerOfTwo(touched);
            if (button == lastButton) lastButton = -1;
            firing1 = false;
            return false;
        }
    }

    public class DesktopInputAdapter extends PlayerInput {
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if (mouseReset) {
                lastX = screenX;
                lastY = screenY;
                mouseReset = false;
            } else {
                movePointer(screenX, screenY);
            }
            return true;
        }
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            movePointer(screenX, screenY);
            return true;
        }

        @Override
        public void update(float delta) {
            super.update(delta);
            firing1 = Gdx.input.isButtonPressed(0);
        }
    }

    public void movePointer(int screenX, int screenY) {
        final float deltaX = (screenX - lastX) / Gdx.graphics.getWidth();
        final float deltaY = (SpruceGame.invertMouseY ? (screenY - lastY) : (lastY - screenY)) / Gdx.graphics.getHeight();
        lastX = screenX;
        lastY = screenY;
//        if (world.paused || inputAdapter.disabled) return;
        if (world.paused) return;
        pitch = MathUtils.clamp(pitch + deltaY * rotateAngle, -90f, 90f);
        forward.rotate(Vector3.Y, deltaX * -rotateAngle);
    }

    private boolean mouseReset;
    public void resetMouse() {
        mouseReset = true;
    }

    private static final float MOVEMENT_SPEED = 4f;
    private static final float JUMP_VELOCITY = 6f;
    private static final float PLAYER_HEIGHT = 1.5f;
    private static final float CAMERA_HEIGHT = PLAYER_HEIGHT * 3.0f/4.0f;
    public Camera camera;
    private float lastX, lastY;
    public float rotateAngle = 360f;
    public PlayerInput inputAdapter;

    public float pitch = 0.0f;

    private final Vector3 tmpV1 = new Vector3();
    public final Vector2 movementInput = new Vector2();

    public Array<PlayerWeapon> weapons = new Array<>();
    public int activeWeaponIndex = -1;
    public PlayerWeapon activeWeapon = null;
    public boolean firing1 = false;
    public boolean shouldReload = false;

    public Player(final GameWorld world) {
        super(world, PLAYER_HEIGHT, PLAYER_HEIGHT/4.0f);
        camera = world.cam;
        inputAdapter = SpruceGame.isMobile() ? new MobileInputAdapter() : new DesktopInputAdapter();
        camera.position.set(hitBox.position);
        camera.position.add(0, CAMERA_HEIGHT, 0);
        mouseReset = true;
        maxHealth *= world.easiness;
        health *= world.easiness;
        // Quick cheat for debugging
//        maxHealth = Float.POSITIVE_INFINITY;
//        health = Float.POSITIVE_INFINITY;
    }

    public Ray aim;
    public RayIntersection aimIntersection = new RayIntersection();

    public Ray getAim() {
        return camera.getPickRay(Gdx.graphics.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f);
    }

    public static final Vector3 aimTarget = new Vector3();
    public Vector3 getAimTargetPoint() {
        return aimTarget.set(aim.origin).mulAdd(aim.direction, Math.min(aimIntersection.t, world.viewDistance));
    }


    public void jump() {
        jump(JUMP_VELOCITY);
    }

    @Override
    public void update(float delta) {
        if (!inputAdapter.disabled) inputAdapter.update(delta);
        aim = getAim();
        aimIntersection.set(world.intersectRay(aim, this));

        camera.direction.set(forward);
        camera.up.set(Vector3.Y);
        tmpV1.set(forward).crs(Vector3.Y);
        camera.rotate(tmpV1.nor(), pitch);

        movement.set(movementInput.x * forward.x + movementInput.y * tmpV1.x, 0, movementInput.x * forward.z + movementInput.y * tmpV1.z);
        if (movement.len2() > 1) movement.nor();
        movement.scl(MOVEMENT_SPEED);

        super.update(delta);

        camera.position.set(hitBox.position);
        camera.position.add(0, CAMERA_HEIGHT, 0);

        camera.update();

        if (activeWeapon != null) {
            activeWeapon.update(delta);
            activeWeapon.setView(camera);
        }
    }

    public void renderViewModel(GameWorld world) {
        if (activeWeapon != null) {
            activeWeapon.render(world);
        }
    }

    public void addWeapon(PlayerWeapon weapon, boolean equip) {
        weapons.add(weapon);
        if (equip) {
            equipWeapon(weapons.size - 1);
        }
    }

    public void equipWeapon(int i) {
        shouldReload = false;
        i = MathUtils.clamp(i, 0, weapons.size - 1);
        activeWeaponIndex = i;
        activeWeapon = weapons.get(i);
    }

    public void nextWeapon() {
        equipWeapon((activeWeaponIndex + 1) % weapons.size);
    }

    public void prevWeapon() {
        equipWeapon((activeWeaponIndex + weapons.size - 1) % weapons.size);
    }

    @Override
    public void die() {
        world.screen.playerDied();
        super.die();
    }

    public String getHoverInfo() {
        String text = null;
        if (aimIntersection.object != null && aimIntersection.object instanceof HoverInfo) {
            text = ((HoverInfo)aimIntersection.object).getInfo(aimIntersection.t);
        } else if (aimIntersection.entity != null && aimIntersection.entity instanceof HoverInfo) {
            text = ((HoverInfo)aimIntersection.entity).getInfo(aimIntersection.t);
        }
        return text == null ? "" : text;
    }

    public void useKeyPressed() {
        if (aimIntersection.object != null && aimIntersection.object instanceof Usable) {
            ((Usable)aimIntersection.object).use(aimIntersection.t);
        } else if (aimIntersection.entity != null && aimIntersection.entity instanceof Usable) {
            ((Usable)aimIntersection.entity).use(aimIntersection.t);
        }
    }

    public void buildHudText(StringBuilder stringBuilder) {
        stringBuilder.append("Health: ").append(health);
        stringBuilder.append('\n');
    }

    @Override
    public boolean takeDamage(float amount, DamageAgent agent, DamageSource source) {
        world.screen.playerHurt();
        return super.takeDamage(amount, agent, source);
    }
}
