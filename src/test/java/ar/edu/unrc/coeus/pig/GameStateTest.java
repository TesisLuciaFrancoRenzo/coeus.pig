package ar.edu.unrc.coeus.pig;

import org.junit.Assert;
import org.junit.Test;

import static ar.edu.unrc.coeus.pig.Game.*;
import static org.hamcrest.CoreMatchers.equalTo;

public
class GameStateTest {

    public static final int NEURONS_TO_ACTIVATE = 5;

    private static
    void inputTestFor(
            final int player1Score,
            final int player2Score,
            final int player1TotalReward,
            final int player2TotalReward,
            final int dicesToRoll
    ) {
        final GameState state = new GameState(dicesToRoll, true, player1Score, player2Score, player1TotalReward, player2TotalReward);
        int             count = 0;
        for ( int i = 0; i < INPUT_NEURONS; i++ ) {
            if ( state.translateToPerceptronInput(i) == 1.0d ) {
                count++;
            }
        }
        if ( count != NEURONS_TO_ACTIVATE ) {
            Assert.fail("No hay " + NEURONS_TO_ACTIVATE + " neuronas activas como input, hay : " + count + ". Estado = " + state);
        }
    }

    @Test
    public final
    void lazyPerceptron() {
        final GameState state = new GameState(1, true, 0, 0, 0, 0);
        Assert.assertThat(state.translateToPerceptronInput(FIRST_DICES_TO_ROLL_INDEX), equalTo(1.0d));
        Assert.assertThat(state.translateToPerceptronInput(FIRST_DICES_TO_ROLL_INDEX - 1), equalTo(0.0d));
        Assert.assertThat(state.translateToPerceptronInput(FIRST_DICES_TO_ROLL_INDEX + 1), equalTo(0.0d));
    }

    @SuppressWarnings( "JUnitTestMethodWithNoAssertions" )
    @Test
    public final
    void translateToPerceptronInput()
            throws Exception {

        final int[] scoreToTest     = { 0, 1, 2, 3, MAX_SCORE - 3, MAX_SCORE - 2, MAX_SCORE - 1, MAX_SCORE };
        final int[] diceToTest      = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final int[] maxRewordToTest = { 0, 1, 2, 3, MAX_TOTAL_REWARD - 3, MAX_TOTAL_REWARD - 2, MAX_TOTAL_REWARD - 1, MAX_TOTAL_REWARD };

        for ( final int player1Score : scoreToTest ) {
            for ( final int player2Score : scoreToTest ) {
                for ( final int player1TotalReward : maxRewordToTest ) {
                    for ( final int player2TotalReward : maxRewordToTest ) {
                        for ( final int dicesToRoll : diceToTest ) {
                            inputTestFor(player1Score, player2Score, player1TotalReward, player2TotalReward, dicesToRoll);
                        }
                    }
                }
            }
        }
    }


}
