package ar.edu.unrc.coeus.pig;

import org.junit.Assert;
import org.junit.Test;

import static ar.edu.unrc.coeus.pig.Game.*;

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
            if ( state.translateToPerceptronInput(i) == 1.0 ) {
                count++;
            }
        }
        if ( count != NEURONS_TO_ACTIVATE ) {
            Assert.fail("No hay " + NEURONS_TO_ACTIVATE + " neuronas activas como input, hay : " + count + ". Estado = " + state.toString());
        }
    }

    @Test
    public final
    void translateToPerceptronInput()
            throws Exception {

        final int[] scoreToTest     = { 0, 1, 2, 3, MAX_SCORE - 3, MAX_SCORE - 2, MAX_SCORE - 1, MAX_SCORE };
        final int[] diceToTest      = { 1, 2, 3, MAX_DICES_TO_ROLL - 3, MAX_DICES_TO_ROLL - 2, MAX_DICES_TO_ROLL - 1, MAX_DICES_TO_ROLL };
        final int[] maxRewordToTest = { 0, 1, 2, 3, MAX_TOTAL_REWARD - 3, MAX_TOTAL_REWARD - 2, MAX_TOTAL_REWARD - 1, MAX_TOTAL_REWARD };

        for ( int player1Score : scoreToTest ) {
            for ( int player2Score : scoreToTest ) {
                for ( int player1TotalReward : maxRewordToTest ) {
                    for ( int player2TotalReward : maxRewordToTest ) {
                        for ( int dicesToRoll : diceToTest ) {
                            inputTestFor(player1Score, player2Score, player1TotalReward, player2TotalReward, dicesToRoll);
                        }
                    }
                }
            }
        }
    }

}
