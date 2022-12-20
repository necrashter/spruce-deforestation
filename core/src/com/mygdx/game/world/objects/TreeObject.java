package com.mygdx.game.world.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.AssetManager2;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.Damageable;

public class TreeObject extends DamageableStaticObject {
    public final boolean isSpruce;

    public TreeObject(AssetManager2.GameObjectTemplate template, boolean isSpruce) {
        super(template);
        this.isSpruce = isSpruce;
    }

    @Override
    public boolean takeDamage(float amount, DamageAgent agent, DamageSource source) {
        if (source == DamageSource.Axe) {
            amount *= 3;
            world.playSound(
                    MyGdxGame.assets.woodCuts[MathUtils.random.nextInt(MyGdxGame.assets.woodCuts.length)],
                    model.transform.getTranslation(tempPos)
            );
        }
        return super.takeDamage(amount, agent, source);
    }

    @Override
    public void remove() {
        ++world.stats.treesCut;
        if (isSpruce) ++world.stats.sprucesCut;
        super.remove();
    }
}
