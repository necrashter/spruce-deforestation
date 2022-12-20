package com.mygdx.game.world.objects;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.AssetManager2;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.Damageable;

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
                    MyGdxGame.assets.metalHits[MathUtils.random.nextInt(MyGdxGame.assets.metalHits.length)],
                    model.transform.getTranslation(tempPos)
            );
        }
        return super.takeDamage(amount, agent, source);
    }
}
