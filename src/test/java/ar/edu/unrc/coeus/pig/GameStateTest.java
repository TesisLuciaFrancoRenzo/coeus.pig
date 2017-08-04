package ar.edu.unrc.coeus.pig;

import junit.framework.TestCase;
import org.junit.Test;

import static ar.edu.unrc.coeus.pig.Game.*;

public
class GameStateTest {
    private
    void inputTestFor(
            int player1Score,
            int player2Score,
            int player1TotalReward,
            int player2TotalReward,
            int player
    ) {
        GameState state;
        state = new GameState(0, player == 0, player1Score, player2Score, player1TotalReward, player2TotalReward);
        int count = 0;
        for ( int i = 0; i < INPUT_NEURONS; i++ ) {
            if ( state.translateToPerceptronInput(i) == 1 ) {
                count++;
            }
        }
        if ( count != 5 ) {
            TestCase.fail(state.toString());
        }
    }

    @Test
    public
    void translateToPerceptronInput()
            throws Exception {

        int[] scoreToTest     = { 0, 1, 2, 3, MAX_SCORE - 3, MAX_SCORE - 2, MAX_SCORE - 1, MAX_SCORE };
        int[] maxRewordToTest = { 0, 1, 2, 3, MAX_REWARD - 3, MAX_REWARD - 2, MAX_REWARD - 1, MAX_REWARD };

        for ( int player1Score = 0; player1Score < scoreToTest.length; player1Score++ ) {
            for ( int player2Score = 0; player2Score < scoreToTest.length; player2Score++ ) {
                for ( int player1TotalReward = 0; player1TotalReward < maxRewordToTest.length; player1TotalReward++ ) {
                    for ( int player2TotalReward = 0; player2TotalReward < maxRewordToTest.length; player2TotalReward++ ) {
                        for ( int player = 0; player < 2; player++ ) {
                            inputTestFor(scoreToTest[player1Score],
                                    scoreToTest[player2Score],
                                    maxRewordToTest[player1TotalReward],
                                    maxRewordToTest[player2TotalReward],
                                    player);
                        }
                    }
                }
            }
        }
    }

}
