package com.spruce.game.world.levels;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.SpruceGame;
import com.spruce.game.Perlin;
import com.spruce.game.world.Octree;
import com.spruce.game.world.Terrain;
import com.spruce.game.world.entities.L4Sainta;
import com.spruce.game.world.objects.FakeTreeObject;
import com.spruce.game.world.player.Axe;
import com.spruce.game.world.player.Player;

public class L4Destruction extends PhaseSurvivalLevel {
    L4Sainta sainta;

    public L4Destruction(SpruceGame game, int level, float easiness) {
        super(game, level, easiness);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.75f, 0.875f, 1f));

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        RandomXS128 random = new RandomXS128(42);
        Perlin perlin = new Perlin();
        perlin.xShift = random.nextFloat() * 100.0f;
        perlin.yShift = random.nextFloat() * 100.0f;
        terrain = new Terrain(environment, perlin, 85, 85, random);
        octree = new Octree(
                this,
                new Vector3(0, 0, 0),
                Math.max(terrain.width, terrain.height)
        );

        player = new Player(this);
        player.setPosition(-6, 1);
        octree.add(player);

        Terrain.CircleAreas spawnPoints = terrain.newCircleAreas();
        spawnPoints.add(0, 0, 6);

        generateForest(spawnPoints, 180, 100, 2.0f);

        player.addWeapon(new Axe(player), true);
        player.addWeapon(SpruceGame.assets.createPistol(player), true);

        sainta = new L4Sainta(this);
        sainta.setPosition(-2.0f, 1);
        sainta.init();
        octree.add(sainta);

        FakeTreeObject fakeTree = new FakeTreeObject(SpruceGame.assets.metalSpruceTemplate) {
            @Override
            public void remove() {
                setObjective(phaseCountdown);
                SpruceGame.music.start(SpruceGame.music.actionMusic);
                super.remove();
            }
        };
        fakeTree.model.transform
                .translate(terrain.getPoint(-3,2.5f));
        octree.add(fakeTree);

        preparePhaseSurvival();
    }

    @Override
    public void addedToScreen() {
        setScriptedEvent(cutscene(
                screen.subtitle("Sainta:\nCongratulations for freeing some of our people!"),
                screen.subtitle("Sainta:\nBut we have more work to do.\nDo you see that metal spruce near me?"),
                screen.subtitle("Sainta:\nWell, it turns out this is the weapon of X-mass destruction\nMr. Suit was talking about!"),
                screen.subtitle("Sainta:\nThey camouflaged it to look like an innocent spruce tree."),
                screen.subtitle("Sainta:\nWe need to destroy it before they activate it."),
                screen.subtitle("Sainta:\nIt's almost completely bulletproof,\ntherefore you need to use your axe."),
                screen.subtitle("Sainta:\nNote that they will come for us when they notice\nwe have destroyed their precious toy."),
                screen.subtitle("Sainta:\nSo, destroy the weapon of X-mass destruction and\nget ready for a battle!"),
                new ScriptedEvent.OneTimeEvent() {
                    @Override
                    public void activate() {
                        setObjective(new LevelObjective.TextObjective("Cut down the weapon of X-mass destruction with axe"));
                    }
                }
        ));
        SpruceGame.music.fadeOut();
    }

    @Override
    void initiatePhase(int phase) {
        switch (phase) {
            case 1:
            case 2:
            case 3:
                pistolSuitPool.spawn(3+phase);
                break;
            case 4:
                pistolSuitPool.spawn(phase);
                rifleSuitPool.spawn(phase-3);
                phaseCountdown.countdownTime = 7.0f;
                break;
            case 5:
            case 6:
            case 7:
                pistolSuitPool.spawn(phase-1);
                rifleSuitPool.spawn(phase-3);
                phaseCountdown.countdownTime = 6.0f;
                break;
            default:
                pistolSuitPool.spawn(6, 25);
                rifleSuitPool.spawn(4, 25);
                phaseCountdown.countdownTime = 6.0f;
                break;
        }
    }

    @Override
    void endOfPhases() {
        setScriptedEvent(cutscene(
                screen.subtitle("Sainta:\nI think that was all of them."),
                screen.subtitle("Sainta:\nWe did it, we survived!"),
                screen.winGameEvent()));
    }
}
