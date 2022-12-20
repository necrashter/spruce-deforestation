package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.world.levels.GameFinishedLevel;
import com.mygdx.game.world.levels.L1WoodChopping;
import com.mygdx.game.world.levels.L2ManChopping;
import com.mygdx.game.world.levels.L3SaintaClaus;
import com.mygdx.game.world.levels.L4Destruction;

public class MyGdxGame extends Game {
	public static AssetManager2 assets;
	public static MusicManager music;
	public static boolean invertMouseY = false;
	public Skin skin;

	TextureAtlas skinAtlas;

	@Override
	public void create () {
		skinAtlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
		skin = new Skin();
		skin.add("default-font", new BitmapFont(Gdx.files.internal("label.fnt")), BitmapFont.class);
		skin.addRegions(skinAtlas);
		skin.load(Gdx.files.internal("uiskin.json"));

		assets = new AssetManager2();
		music = new MusicManager();
		// loading
		while (!assets.update());
		assets.done();

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		music.update(delta);
		if (screen != null) screen.render(delta);
	}

	@Override
	public void dispose () {
		skin.dispose();
		skinAtlas.dispose();
		assets.dispose();
	}

	public static boolean isMobile() {
//		return true;
		return Gdx.app.getType().equals(Application.ApplicationType.Android) || Gdx.app.getType().equals(Application.ApplicationType.iOS);
	}

	public Screen getLevel(int level, float easiness) {
		switch (level) {
			case 1: return new GameScreen(this, new L1WoodChopping(this, 1, easiness));
			case 2: return new GameScreen(this, new L2ManChopping(this, 2,easiness));
			case 3: return new GameScreen(this, new L3SaintaClaus(this, 3,easiness));
			case 4: return new GameScreen(this, new L4Destruction(this, 4, easiness));
			case 5: return new GameScreen(this, new GameFinishedLevel(this, 5, easiness));
			default: return new MenuScreen(this);
		}
	}
}
