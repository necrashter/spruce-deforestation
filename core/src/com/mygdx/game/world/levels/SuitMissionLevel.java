package com.mygdx.game.world.levels;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.entities.Suit;

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

    public SuitMissionLevel(MyGdxGame game, final int level, float easiness) {
        super(game, level, easiness);
    }
}
