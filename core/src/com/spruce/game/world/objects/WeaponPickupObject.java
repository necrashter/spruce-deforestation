package com.spruce.game.world.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.world.geom.Shape;
import com.spruce.game.world.player.Player;
import com.spruce.game.world.player.PlayerWeapon;

public class WeaponPickupObject extends BasePickupObject {
    public PlayerWeapon weapon;

    public WeaponPickupObject(ModelInstance model, Shape shape, Vector3 position, PlayerWeapon weapon) {
        super(model, shape, position);
        this.weapon = weapon;
    }

    @Override
    public void onTaken(Player player) {
        player.addWeapon(weapon, true);
    }
}
