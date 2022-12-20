package com.mygdx.game.world.levels;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.world.GameWorld;
import com.mygdx.game.world.entities.L4PistolSuit;
import com.mygdx.game.world.entities.L4RifleSuit;

public abstract class PhaseSurvivalLevel extends GameWorld {
    L4PistolSuit.Pool pistolSuitPool;
    L4RifleSuit.Pool rifleSuitPool;

    public PhaseSurvivalLevel(MyGdxGame game, int level, float easiness) {
        super(game, level, easiness);
    }

    /**
     * Call after initializing terrain, etc.
     */
    void preparePhaseSurvival() {
        pistolSuitPool = new L4PistolSuit.Pool(this, 32);
        rifleSuitPool = new L4RifleSuit.Pool(this, 32);

        phaseCountdown = new PhaseCountdownObjective();
        clearPhaseObjective = new ClearPhaseObjective();
    }

    int phase = 0;
    int maxPhase = 10;
    PhaseCountdownObjective phaseCountdown;
    ClearPhaseObjective clearPhaseObjective;

    class PhaseCountdownObjective implements LevelObjective {
        float countdownTime = 3.0f;
        float remainingTime;

        @Override
        public void init() {
            remainingTime = countdownTime;
        }

        @Override
        public void update(float delta) {
            remainingTime -= delta;
            if (remainingTime < 0) {
                phase += 1;
                initiatePhase(phase);
                setObjective(clearPhaseObjective);
            }
        }

        @Override
        public void buildHudText(StringBuilder stringBuilder) {
            stringBuilder.append("Phase: ").append(phase).append('/').append(maxPhase);
            stringBuilder.append('\n');
            stringBuilder.append("Next phase in ").append(MathUtils.ceil(remainingTime));
        }
    }

    abstract void initiatePhase(int phase);

    class ClearPhaseObjective implements LevelObjective {
        int remaining = 0;
        @Override
        public void init() {
            remaining = pistolSuitPool.getAlive() + rifleSuitPool.getAlive();
        }

        @Override
        public void update(float delta) {
            remaining = pistolSuitPool.getAlive() + rifleSuitPool.getAlive();
            if (remaining == 0) {
                phaseDone();
            }
        }

        @Override
        public void buildHudText(StringBuilder stringBuilder) {
            stringBuilder.append("Phase: ").append(phase).append('/').append(maxPhase);
            stringBuilder.append('\n');
            stringBuilder.append("Remaining: ").append(remaining);
        }
    }

    private void phaseDone() {
        if (phase < maxPhase) {
            setObjective(phaseCountdown);
        } else {
            removeObjective();
            endOfPhases();
        }
    }

    abstract void endOfPhases();
}
