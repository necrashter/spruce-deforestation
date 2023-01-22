package com.spruce.game.world.entities;

import com.spruce.game.world.GameWorld;

public class L1Suit extends Suit {
    public L1Suit(GameWorld world) {
        super(world);
    }

    public void playerTalk() {
        if (levelDone) {
            world.setScriptedEvent(world.cutscene(
                    world.screen.subtitle("Mr. Suit:\nWell done!"),
                    world.screen.subtitle("Mr. Suit:\nWe will go to another forest now."),
                    world.screen.winGameEvent()
            ));
        } else {
            world.setScriptedEvent(world.cutscene(world.screen.subtitle("Mr. Suit:\nDo your job!")));
        }
    }
}
