package com.spruce.game.world.levels;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.SpruceGame;
import com.spruce.game.Perlin;
import com.spruce.game.world.GameWorld;
import com.spruce.game.world.Octree;
import com.spruce.game.world.Terrain;
import com.spruce.game.world.entities.PistolWanderer;
import com.spruce.game.world.entities.NPC;
import com.spruce.game.world.objects.WeaponPickupObject;
import com.spruce.game.world.player.Axe;
import com.spruce.game.world.player.Player;

public class GunnerTest extends GameWorld {
    public GunnerTest(SpruceGame game, final int level, float easiness) {
        super(game, level, easiness);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.75f, 0.875f, 1f));

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        RandomXS128 random = new RandomXS128(42);
        Perlin perlin = new Perlin();
        perlin.xShift = random.nextFloat() * 100.0f;
        perlin.yShift = random.nextFloat() * 100.0f;
        terrain = new Terrain(environment, perlin, 100, 100, random);
        octree = new Octree(
                this,
                new Vector3(0, 0, 0),
                Math.max(terrain.width, terrain.height)
        );

        player = new Player(this);
        octree.add(player);

        Terrain.CircleAreas spawnPoints = terrain.newCircleAreas();
        spawnPoints.add(0, 0, 6);

        generateForest(spawnPoints, 200, 200, 2.0f);

        player.addWeapon(new Axe(player), true);

        {
            Vector3 pickupPos = new Vector3(terrain.getPoint(0, 5).add(0, 1.25f, 0));
            WeaponPickupObject pickupObject = SpruceGame.assets.createPistolPickup(player, pickupPos);
            octree.add(pickupObject);
        }
        {
            Vector3 pickupPos = new Vector3(terrain.getPoint(0, -5).add(0, 1.25f, 0));
            WeaponPickupObject pickupObject = SpruceGame.assets.createAutoRiflePickup(player, pickupPos);
            octree.add(pickupObject);
        }
        {
            Vector3 pickupPos = new Vector3(terrain.getPoint(5, 0).add(0, 1.25f, 0));
            octree.add(SpruceGame.assets.createHealthPickup(pickupPos));
        }

        {
            for (int i = 0; i < 20; ++i) {
                NPC entity = new PistolWanderer(this);
                entity.setPosition(terrain.randomX(2.0f), terrain.randomZ(2.0f));
                entity.init();
                octree.add(entity);
            }
//            suit = new Suit(this);
//            suit.setPosition(5.0f, 0.0f);
//            suit.init();
//            octree.add(suit);
        }
    }
}
