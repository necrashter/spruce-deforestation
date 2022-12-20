package com.mygdx.game.world;

public interface Damageable {
    enum DamageSource {
        Axe,
        Firearm,
    }
    enum DamageAgent {
        Player,
        NPC,
    }


    boolean takeDamage(float amount, DamageAgent agent, DamageSource source);
}
