package com.spruce.game.world.objects;

import com.badlogic.gdx.math.MathUtils;
import com.spruce.game.AssetManager2;
import com.spruce.game.SpruceGame;

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
                    SpruceGame.assets.woodCuts[MathUtils.random.nextInt(SpruceGame.assets.woodCuts.length)],
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
