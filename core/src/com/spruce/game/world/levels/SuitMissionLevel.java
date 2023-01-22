package com.spruce.game.world.levels;

import com.spruce.game.SpruceGame;
import com.spruce.game.world.GameWorld;
import com.spruce.game.world.entities.Suit;

public abstract class SuitMissionLevel extends GameWorld {
    Suit suit;

    class TalkToSuitObjective implements LevelObjective {
        @Override
        public void init() {
            suit.levelDone = true;
        }

        @Override
        public void update(float delta) { }

        @Override
        public void buildHudText(StringBuilder stringBuilder) {
            stringBuilder.append("Talk to Mr.Suit\n");
        }
    }

    public SuitMissionLevel(SpruceGame game, final int level, float easiness) {
        super(game, level, easiness);
    }
}
