package ar.edu.unrc.coeus.pig;

import ar.edu.unrc.coeus.tdlearning.interfaces.IState;
import ar.edu.unrc.coeus.tdlearning.interfaces.IStateNTuple;
import ar.edu.unrc.coeus.tdlearning.interfaces.IStatePerceptron;
import ar.edu.unrc.coeus.tdlearning.training.ntuple.SamplePointValue;

public
class State
        implements IStatePerceptron, IStateNTuple {
    @Override
    public
    IState getCopy() {
        return null;
    }

    @Override
    public
    SamplePointValue[] getNTuple( int nTupleIndex ) {
        return new SamplePointValue[0];
    }

    @Override
    public
    double getStateReward( int outputNeuron ) {
        return 0;
    }

    @Override
    public
    boolean isTerminalState() {
        return false;
    }

    @Override
    public
    Double translateToPerceptronInput( int neuronIndex ) {
        return null;
    }
}
