package com.mygdx.game.world.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.world.geom.Shape;
import com.mygdx.game.world.player.Player;
import com.mygdx.game.world.player.PlayerWeapon;

public class HealthPickupObject extends BasePickupObject {
    float amount;

    public HealthPickupObject(ModelInstance model, Shape shape, Vector3 position, float amount) {
        super(model, shape, position);
        this.amount = amount ;
    }

    @Override
    public void onTaken(Player player) {
        player.heal(amount);
    }
}
