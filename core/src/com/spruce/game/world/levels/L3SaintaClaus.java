package com.spruce.game.world.levels;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.spruce.game.SpruceGame;
import com.spruce.game.Perlin;
import com.spruce.game.world.GameWorld;
import com.spruce.game.world.Octree;
import com.spruce.game.world.Terrain;
import com.spruce.game.world.entities.Captive;
import com.spruce.game.world.entities.L3Sainta;
import com.spruce.game.world.entities.L3Suit;
import com.spruce.game.world.entities.NPC;
import com.spruce.game.world.entities.PistolWanderer;
import com.spruce.game.world.player.Axe;
import com.spruce.game.world.player.Player;

public class L3SaintaClaus extends GameWorld {
    L3Suit suit;
    L3Sainta sainta;

    public L3SaintaClaus(SpruceGame game, int level, float easiness) {
        super(game, level, easiness);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.75f, 0.875f, 1f));

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        RandomXS128 random = new RandomXS128(3);
        Perlin perlin = new Perlin();
        perlin.xShift = random.nextFloat() * 100f;
        perlin.yShift = random.nextFloat() * 100f;// - 10f;
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

        suit = new L3Suit(this) {
            @Override
            public boolean onRemove(boolean worldDisposal) {
                if (worldDisposal) return true;
                Vector3 pickupPos = new Vector3(world.terrain.getPoint(hitBox.position.x, hitBox.position.z));
                octree.add(SpruceGame.assets.createPistolPickup(world.player, pickupPos).spawnAnimation());
                setObjective(new LevelObjective.TextObjective("Talk to Red Man"));
                return true;
            }
        };
        suit.setPosition(5.0f, 0.0f);
        suit.init();
        octree.add(suit);

        sainta = new L3Sainta(this) {
            @Override
            public void playerTalk() {
                talkToSainta();
            }
        };
        sainta.setPosition(0.0f, 26.0f);
        sainta.init();
        octree.add(sainta);

        suit.prepareTarget(sainta);
        sainta.prepareTarget(suit);
    }


    @Override
    public void addedToScreen() {
        this.setScriptedEvent(new Cutscene(new ScriptedEvent[]{
                screen.subtitle("Mr. Suit:\nGovernment is working hard to solve\nthis X-mas frenzy."),
                screen.subtitle("Mr. Suit:\nDespite our best efforts, the issue is still growing."),
                screen.subtitle("Mr. Suit:\nWe may have to resort to\nweapons of X-mass destruction."),
                screen.subtitle("Mr. Suit:\nHold on, I think I heard something."),
                new ScriptedEvent.OneTimeEvent() {
                    @Override
                    public void activate() {
                        sainta.beginPursuingTarget();
                    }
                }
        } ));

        SpruceGame.music.fadeOut();
    }

    public void talkToSainta() {
        setScriptedEvent(cutscene(
                screen.subtitle("Red Man:\nWe are starting an uprising against the dictatorship!"),
                screen.subtitle("Red Man:\nI am the leader of the rebellion.\nMy name is Sainta Claus."),
                screen.subtitle("Sainta:\nI'm sure you will agree to help us overthrow\nthese tyrants."),
                screen.subtitle("Sainta:\nThey have captured some of our members in this forest.\nThey are patrolling everywhere."),
                screen.subtitle("Sainta:\nYou should locate and free\nat least 10 of them as soon as possible."),
                screen.subtitle("Sainta:\nI need to go to another forest and help my followers there."),
                screen.subtitle("Sainta:\nSee you later! Sainta out!"),
                screen.subtitle("(Take the pistol dropped by Mr. Suit)\n(Scroll Wheel or Q to switch weapons)\n(R to reload, all guns have infinite ammo)"),
                new ScriptedEvent.OneTimeEvent() {
                    @Override
                    public void activate() {
                        sainta.beginRunAway();
                        for (int i = 0; i < PistolWanderer.MIN_COUNT; ++i) {
                            NPC entity = new PistolWanderer(L3SaintaClaus.this);
                            Vector2 point = randomPointOutsideView();
                            entity.setPosition(point.x, point.y);
                            entity.init();
                            octree.add(entity);
                        }
                        for (int i = 0; i < Captive.MIN_COUNT; ++i) {
                            NPC entity = new Captive(L3SaintaClaus.this);
                            Vector2 point = randomPointOutsideView();
                            entity.setPosition(point.x, point.y);
                            entity.init();
                            octree.add(entity);
                        }
                        Captive.FreeCaptivesObjective objective = new Captive.FreeCaptivesObjective(L3SaintaClaus.this, 10, new WinImmediatelyObjective());
                        setObjective(objective);
                        SpruceGame.music.start(SpruceGame.music.actionMusic);
                    }
                }
        ));
    }
}
