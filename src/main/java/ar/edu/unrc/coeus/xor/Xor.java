package ar.edu.unrc.coeus.xor;

import ar.edu.unrc.coeus.tdlearning.interfaces.IAction;
import ar.edu.unrc.coeus.tdlearning.interfaces.IActor;
import ar.edu.unrc.coeus.tdlearning.interfaces.IProblemToTrain;
import ar.edu.unrc.coeus.tdlearning.interfaces.IState;

import java.util.List;

public
class Xor
        implements IProblemToTrain {

    public static
    void main( String[] args ) {
        System.out.println("Hello World!");
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
