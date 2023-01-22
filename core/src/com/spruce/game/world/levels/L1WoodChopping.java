package com.spruce.game.world.levels;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.SpruceGame;
import com.spruce.game.Perlin;
import com.spruce.game.world.Octree;
import com.spruce.game.world.Terrain;
import com.spruce.game.world.entities.Everyman;
import com.spruce.game.world.entities.L1Suit;
import com.spruce.game.world.entities.NPC;
import com.spruce.game.world.player.Axe;
import com.spruce.game.world.player.Player;

public class L1WoodChopping extends SuitMissionLevel {
    public L1WoodChopping(SpruceGame game, final int level, float easiness) {
        super(game, level, easiness);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.75f, 0.875f, 1f));

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        RandomXS128 random = new RandomXS128(1);
        Perlin perlin = new Perlin();
        perlin.xShift = random.nextFloat() * 100f;
        perlin.yShift = random.nextFloat() * 100f;
        terrain = new Terrain(environment, perlin, 100 ,100, random);
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
            for (int i = 0; i < 20; ++i) {
                NPC entity = new Everyman(this);
                entity.setPosition(terrain.randomX(2.0f), terrain.randomZ(2.0f));
                entity.init();
                octree.add(entity);
            }
            suit = new L1Suit(this);
            suit.setPosition(5.0f, 0.0f);
            suit.init();
            octree.add(suit);
        }

        // debug thing
//        octree.node.printObjectCounts(0);

        setObjective(new WoodChoppingObjective(10, new SuitMissionLevel.TalkToSuitObjective()));
    }

    @Override
    public void addedToScreen() {
        this.setScriptedEvent(new Cutscene(new ScriptedEvent[]{
                screen.subtitle("Mr. Suit:\nDear Citizen #3938407142,"),
                screen.subtitle("Mr. Suit:\nWe have noticed an increasing\ninterest in an ancient tradition called \"X-mas\"."),
                screen.subtitle("Mr. Suit:\nFun-Prevention Ministry decided to stop this trend."),
                screen.subtitle("Mr. Suit:\nSpruce trees seem to be important for this ancient tradition."),
                screen.subtitle("Mr. Suit:\nSo, your first task is to cut down spruce trees."),
                screen.subtitle("Mr. Suit:\nThere are some ancient \"X-mas\" cultists\nwandering around in this forest."),
                screen.subtitle("Mr. Suit:\nYou are free to kill them if they bother you,\nbut it's not required."),
                screen.subtitle("(WASD to move, mouse to look, space to jump)\n(Left mouse button to attack)\nGood Luck!"),
        } ));

        SpruceGame.music.start(SpruceGame.music.darkMusic);
    }
}
