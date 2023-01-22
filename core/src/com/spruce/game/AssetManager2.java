package com.spruce.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.spruce.game.world.geom.BoxShape;
import com.spruce.game.world.geom.Shape;
import com.spruce.game.world.geom.SphereShape;
import com.spruce.game.world.objects.HealthPickupObject;
import com.spruce.game.world.objects.WeaponPickupObject;
import com.spruce.game.world.objects.TreeObject;
import com.spruce.game.world.player.Firearm;
import com.spruce.game.world.player.Player;

public class AssetManager2 extends AssetManager {
    public static final BoundingBox tempBox = new BoundingBox();
    public Model allModel;
    public Model npcModel;

    public Texture muzzleFlash = new Texture(Gdx.files.internal("textures/muzzle.png"));
    public TextureRegion muzzleFlashRegion = new TextureRegion(muzzleFlash);
    public Texture bulletTrace = new Texture(Gdx.files.internal("textures/trace.png"));
    public Texture bottomGrad = new Texture(Gdx.files.internal("textures/bottomGrad.png"));
    public Texture hurtOverlay = new Texture(Gdx.files.internal("textures/hurtOverlay.png"));

    public Sound[] swooshes = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh0.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh1.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh2.ogg")),
    };

    public Sound[] slowSwooshes = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh-slow0.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh-slow1.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh-slow2.ogg")),
    };

    public Sound enemyPistol = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy-pistol.ogg"));
    public Sound enemyRifle = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy-rifle.ogg"));
    public Sound gunEmpty = Gdx.audio.newSound(Gdx.files.internal("sounds/gun-empty.ogg"));

    public Sound[] death = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/man-death-0.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/man-death-1.ogg")),
    };

    public Sound[] stabs = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/stab-0.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/stab-1.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/stab-2.ogg")),
    };

    public Sound[] woodCuts = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/wood-0.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/wood-1.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/wood-2.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/wood-3.ogg")),
    };

    public Sound[] metalHits = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/metal-0.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/metal-1.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/metal-2.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/metal-3.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/metal-4.ogg")),
    };

    public AssetManager2() {
        super();
        load("models/all.g3db", Model.class);
        load("models/npcs.g3db", Model.class);
        load("crosshair010.png", Texture.class);
        load("crosshair010.png", Texture.class);
        load("textures/grass.png", Texture.class);
        load("textures/snow.png", Texture.class);
    }

    public static class GameObjectTemplate {
        public final ModelInstance model;
        public final Shape modelShape;
        public final Shape physicsShape;

        public GameObjectTemplate(ModelInstance model, Shape modelShape, Shape physicsShape) {
            this.model = model;
            this.modelShape = modelShape;
            this.physicsShape = physicsShape;
        }
    }

    public GameObjectTemplate treeTemplate;
    public GameObjectTemplate spruceTemplate;
    public GameObjectTemplate metalSpruceTemplate;
    public Firearm.Template pistolTemplate, autoRifleTemplate;
    public ModelInstance candyBarModel;
    public Shape candyBarShape;

    public void done() {
        allModel = get("models/all.g3db", Model.class);
        npcModel = SpruceGame.assets.get("models/npcs.g3db", Model.class);

        treeTemplate = buildObjectTemplate("tree");
        treeTemplate.model.materials.get(1).set(
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
                FloatAttribute.createAlphaTest(0.25f),
                IntAttribute.createCullFace(GL20.GL_NONE)
        );
        spruceTemplate = buildObjectTemplate("spruce");
        metalSpruceTemplate = buildObjectTemplate("metal-spruce");

        pistolTemplate = buildFirearmTemplate("pistol");
        autoRifleTemplate = buildFirearmTemplate("autorifle");
        candyBarModel = new ModelInstance(allModel, "candy-bar");
        candyBarShape = new SphereShape(candyBarModel.calculateBoundingBox(tempBox));

//        System.out.println("done");
    }

    private GameObjectTemplate buildObjectTemplate(String name) {
        Node node = allModel.getNode(name);
        BoxShape visibilityHitBox = new BoxShape(node.calculateBoundingBox(tempBox));
        BoxShape hitBox = visibilityHitBox;
        Node hitBoxNode = allModel.getNode(name + ".hitbox0");
        if (hitBoxNode != null) {
            hitBox = new BoxShape(hitBoxNode.calculateBoundingBox(tempBox));
        }
        return new GameObjectTemplate(
                new ModelInstance(allModel, name),
                visibilityHitBox,
                hitBox
        );
    }

    private Firearm.Template buildFirearmTemplate(String name) {
        Vector3 muzzlePoint = new Vector3();
        Node muzzle = allModel.getNode(name + ".muzzle");
        if (muzzle != null) {
            muzzlePoint.set(muzzle.translation);
        }
        ModelInstance model = new ModelInstance(allModel, name);
        Shape shape = new SphereShape(model.calculateBoundingBox(tempBox));
        return new Firearm.Template(model, shape, muzzlePoint,
                Gdx.audio.newSound(Gdx.files.internal("sounds/"+ name + "-shoot.ogg")),
                Gdx.audio.newSound(Gdx.files.internal("sounds/"+ name + "-reload.ogg"))
                );
    }

    public TreeObject createTree() {
        return new TreeObject(treeTemplate, false);
    }

    public TreeObject createSpruce() {
        return new TreeObject(spruceTemplate, true);
    }

    public Firearm createPistol(Player player) {
        Firearm pistol = new Firearm(player, pistolTemplate);
        pistol.ammoInClip = 7;
        pistol.maxAmmoInClip = 7;
        pistol.reloadSpeed = 2.0f;
        pistol.recoveryTranslateZ = 0.125f;
        pistol.recoveryRoll = 20f;
        pistol.recoveryPitch = 30f;
        pistol.recoverySpeed = 4.0f;
        return pistol;
    }

    public WeaponPickupObject createPistolPickup(Player player, Vector3 position) {
        return new WeaponPickupObject(
                pistolTemplate.model,
                pistolTemplate.shape,
                position,
                createPistol(player)
        );
    }

    public Firearm createAutoRifle(Player player) {
        Firearm gun = new Firearm(player, autoRifleTemplate);
        return gun;
    }

    public WeaponPickupObject createAutoRiflePickup(Player player, Vector3 position) {
        return new WeaponPickupObject(
                autoRifleTemplate.model,
                autoRifleTemplate.shape,
                position,
                createAutoRifle(player)
        );
    }

    public HealthPickupObject createHealthPickup(Vector3 position, float amount) {
        return new HealthPickupObject(
                candyBarModel,
                candyBarShape,
                position,
                amount
        );
    }

    public HealthPickupObject createHealthPickup(Vector3 position) {
        return createHealthPickup(position, 20);
    }
}
