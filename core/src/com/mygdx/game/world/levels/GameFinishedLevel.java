package com.mygdx.game.world.levels;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Perlin;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.Octree;
import com.mygdx.game.world.Terrain;
import com.mygdx.game.world.entities.Celebrator;
import com.mygdx.game.world.entities.NPC;
import com.mygdx.game.world.player.Axe;
import com.mygdx.game.world.player.Player;

public class GameFinishedLevel extends GameWorld {
    public GameFinishedLevel(MyGdxGame game, int level, float easiness) {
        super(game, level, easiness);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.75f, 0.875f, 1f));

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        RandomXS128 random = new RandomXS128(64);
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
        player.setPosition(-2, 0);
        octree.add(player);

        Terrain.CircleAreas spawnPoints = terrain.newCircleAreas();
        spawnPoints.add(0, 0, 6);

        generateForest(spawnPoints, 180, 150, 2.0f);

        player.addWeapon(new Axe(player), true);
        player.addWeapon(MyGdxGame.assets.createPistol(player), true);
        player.addWeapon(MyGdxGame.assets.createAutoRifle(player), true);

        {
            NPC npc = new Celebrator(this, "Suit");
            npc.setPosition(2, -5);
            npc.init();
            octree.add(npc);
        }
        {
            NPC npc = new Celebrator(this, "ManMesh");
            npc.setPosition(2, 0);
            npc.init();
            octree.add(npc);
        }
        {
            NPC npc = new Celebrator(this, "Sainta");
            npc.setPosition(2, 5);
            npc.init();
            octree.add(npc);
        }
    }

    @Override
    public void addedToScreen() {
        this.setScriptedEvent(new Cutscene(new ScriptedEvent[]{
                screen.subtitle("You have finished the game!\nThanks for playing!"),
        } ));
    }
}
