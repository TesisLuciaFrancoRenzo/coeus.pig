package ar.edu.unrc.coeus.pig;

import ar.edu.unrc.coeus.tdlearning.interfaces.IAction;

public
enum Action
        implements IAction {

    /**
     * Tirar el dado.
     */
    THROW,
    /**
     * Paso, no tiro el dado.
     */
    SKIP
}

