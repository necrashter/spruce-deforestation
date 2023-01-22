package com.spruce.game.world.objects;

import com.badlogic.gdx.math.MathUtils;
import com.spruce.game.AssetManager2;
import com.spruce.game.SpruceGame;

public class FakeTreeObject extends DamageableStaticObject {
    public FakeTreeObject(AssetManager2.GameObjectTemplate template) {
        super(template);
        health = 240;
    }

    @Override
    public boolean takeDamage(float amount, DamageAgent agent, DamageSource source) {
        if (source == DamageSource.Axe) {
            amount *= 3;
            world.playSound(
                    SpruceGame.assets.metalHits[MathUtils.random.nextInt(SpruceGame.assets.metalHits.length)],
                    model.transform.getTranslation(tempPos)
            );
        }
        return super.takeDamage(amount, agent, source);
    }
}
