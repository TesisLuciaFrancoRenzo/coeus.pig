package ar.edu.unrc.coeus.pig;

import ar.edu.unrc.coeus.tdlearning.interfaces.IAction;
import ar.edu.unrc.coeus.tdlearning.interfaces.IActor;
import ar.edu.unrc.coeus.tdlearning.interfaces.IProblemToTrain;
import ar.edu.unrc.coeus.tdlearning.interfaces.IState;

import java.util.List;


/**
 * entradas a la red: <br> 1- puntaje total del enemigo. <br> 2- puntaje total del jugador actual. <br> 3- cantidad de veces tirados el dado en este
 * turno. <br>
 **/
public
class Pig
        implements IProblemToTrain {

    public static
    void main( String[] args ) {
        System.out.println("Hola Jugamos al Pig!!!");
    }

    @Override
    public
    boolean canExploreThisTurn( long currentTurn ) {
        return false;
    }

    @Override
    public
    IState computeAfterState(
            IState turnInitialState,
            IAction action
    ) {
        return null;
    }

    @Override
    public
    IState computeNextTurnStateFromAfterState( IState afterState ) {
        return null;
    }

    @Override
    public
    Double computeNumericRepresentationFor(
            Object[] output,
            IActor actor
    ) {
        return null;
    }

    @Override
    public
    double deNormalizeValueFromPerceptronOutput( Object value ) {
        return 0;
    }

    @Override
    public
    Object[] evaluateBoardWithPerceptron( IState state ) {
        return new Object[0];
    }

    @Override
    public
    IActor getActorToTrain() {
        return null;
    }

    @Override
    public
    IState initialize( IActor actor ) {
        return null;
    }

    @Override
    public
    List< IAction > listAllPossibleActions( IState turnInitialState ) {
        return null;
    }

    @Override
    public
    double normalizeValueToPerceptronOutput( Object value ) {
        return 0;
    }

    @Override
    public
    void setCurrentState( IState nextTurnState ) {

    }
}
