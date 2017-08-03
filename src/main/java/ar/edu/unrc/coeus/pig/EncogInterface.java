/*
 * Copyright (C) 2017  Lucia Bressan <lucyluz333@gmial.com>,
 *                     Franco Pellegrini <francogpellegrini@gmail.com>,
 *                     Renzo Bianchini <renzobianchini85@gmail.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ar.edu.unrc.coeus.pig;

import ar.edu.unrc.coeus.interfaces.INeuralNetworkInterface;
import ar.edu.unrc.coeus.utils.FunctionUtils;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.encog.util.obj.SerializeObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public
class EncogInterface
        implements INeuralNetworkInterface {
    private final boolean concurrentInput;
    private final double  maxReward;
    private final double  minReward;
    private List< Function< Double, Double > > activationFunction         = null;
    private ActivationFunction[]               activationFunctionForEncog = null;
    private double                             activationFunctionMax      = 0.0;
    private double                             activationFunctionMin      = 0.0;
    private List< Function< Double, Double > > derivedActivationFunction  = null;
    private boolean                            hasBias                    = true;
    private BasicNetwork neuralNetwork;
    private int[]           neuronQuantityInLayer = null;
    private NormalizedField normOutput            = null;

    public
    EncogInterface(
            final ActivationFunction[] encogActivationFunctions,
            final double activationFunctionMax,
            final double activationFunctionMin,
            final double maxReward,
            final double minReward,
            final boolean hasBias,
            final int[] neuronQuantityInLayer,
            final boolean concurrentInput
    ) {
        activationFunctionForEncog = encogActivationFunctions;
        activationFunction = new ArrayList<>(encogActivationFunctions.length);
        derivedActivationFunction = new ArrayList<>(encogActivationFunctions.length);
        this.maxReward = maxReward;
        this.minReward = minReward;
        this.activationFunctionMax = activationFunctionMax;
        this.activationFunctionMin = activationFunctionMin;
        this.hasBias = hasBias;
        this.neuronQuantityInLayer = neuronQuantityInLayer;
        this.concurrentInput = concurrentInput;
        for ( final ActivationFunction activationFunctionForEncog : encogActivationFunctions ) {
            if ( activationFunctionForEncog instanceof ActivationTANH ) {
                activationFunction.add(FunctionUtils.TANH);
                derivedActivationFunction.add(FunctionUtils.TANH_DERIVED);
            } else if ( activationFunctionForEncog instanceof ActivationSigmoid ) {
                activationFunction.add(FunctionUtils.SIGMOID);
                derivedActivationFunction.add(FunctionUtils.SIGMOID_DERIVED);
            } else if ( activationFunctionForEncog instanceof ActivationLinear ) {
                activationFunction.add(FunctionUtils.LINEAR);
                derivedActivationFunction.add(FunctionUtils.LINEAR_DERIVED);
            } else {
                throw new IllegalArgumentException("El test esta pensado para utilizar TANH, Sigmoid o Linear como función de activación");
            }
        }
        normOutput = new NormalizedField(NormalizationAction.Normalize, null, maxReward, minReward, activationFunctionMax, activationFunctionMin);
    }

    public
    boolean containBias() {
        return hasBias;
    }

    public
    double deNormalizeOutput( final double value ) {
        return normOutput.deNormalize(value);
    }

    public
    List< Function< Double, Double > > getActivationFunction() {
        return activationFunction;
    }

    @Override
    public
    Function< Double, Double > getActivationFunction( final int layerIndex ) {
        return activationFunction.get(layerIndex - 1);
    }

    public
    double getActivationFunctionMax() {
        return activationFunctionMax;
    }

    public
    double getActivationFunctionMin() {
        return activationFunctionMin;
    }

    @Override
    public
    double getBias(
            final int layerIndex,
            final int neuronIndex
    ) {
        if ( hasBias(layerIndex) ) {
            return neuralNetwork.getWeight(layerIndex - 1, neuralNetwork.getLayerNeuronCount(layerIndex - 1), neuronIndex);
        } else {
            throw new IllegalStateException("No hay bias en la capa " + layerIndex);
        }
    }

    public
    List< Function< Double, Double > > getDerivedActivationFunction() {
        return derivedActivationFunction;
    }

    @Override
    public
    Function< Double, Double > getDerivedActivationFunction( final int layerIndex ) {
        return derivedActivationFunction.get(layerIndex - 1);
    }

    @Override
    public
    int getLayerQuantity() {
        return neuralNetwork.getLayerCount();
    }

    public
    double getMaxReward() {
        return maxReward;
    }

    public
    double getMinReward() {
        return minReward;
    }

    public
    BasicNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public
    int[] getNeuronQuantityInLayer() {
        return neuronQuantityInLayer;
    }

    @Override
    public
    int getNeuronQuantityInLayer( final int layerIndex ) {
        return neuralNetwork.getLayerNeuronCount(layerIndex);
    }

    @Override
    public
    double getWeight(
            final int layerIndex,
            final int neuronIndex,
            final int neuronIndexPreviousLayer
    ) {
        return neuralNetwork.getWeight(layerIndex - 1, neuronIndexPreviousLayer, neuronIndex);
    }

    @Override
    public
    boolean hasBias( final int layerIndex ) {
        return neuralNetwork.isLayerBiased(layerIndex - 1);
    }

    /**
     * Inicializa red neuronal con Encog.
     *
     * @param randomized true si debe ser iniciado con pesos y bias con valores al azar.
     *
     * @return red neuronal inicializada.
     */
    public
    BasicNetwork initializeEncogPerceptron( final boolean randomized ) {
        if ( ( neuronQuantityInLayer == null ) || ( neuronQuantityInLayer.length < 2 ) ) {
            throw new IllegalArgumentException("la cantidad de capas es de mínimo 2 para un perceptrón (incluyendo entrada y salida)");
        }
        final BasicNetwork perceptron = new BasicNetwork();
        perceptron.addLayer(new BasicLayer(null, containBias(), neuronQuantityInLayer[0]));
        final int          getNeuronQuantityInLayerLength = neuronQuantityInLayer.length - 1;
        ActivationFunction function;
        for ( int i = 1; i < getNeuronQuantityInLayerLength; i++ ) {
            function = activationFunctionForEncog[i - 1].clone();
            perceptron.addLayer(new BasicLayer(function, containBias(), neuronQuantityInLayer[i]));
        }
        function = activationFunctionForEncog[neuronQuantityInLayer.length - 2].clone();
        perceptron.addLayer(new BasicLayer(function, false, neuronQuantityInLayer[neuronQuantityInLayer.length - 1]));
        perceptron.getStructure().finalizeStructure();
        if ( randomized ) {
            perceptron.reset();
        }
        return perceptron;
    }

    public
    boolean isConcurrentInputEnabled() {
        return concurrentInput;
    }

    /**
     * Carga desde un archivo una red neuronal, o crea una nueva (con valores al azar o con valores por defecto).
     *
     * @param perceptronFile       archivo con la red neuronal.
     * @param randomizedIfNotExist true si debe inicializar al azar los pesos y bias al crear una nueva red neuronal.
     * @param createFile           true si debe crear una nueva red neuronal.
     */
    public
    void loadOrCreatePerceptron(
            final File perceptronFile,
            final boolean randomizedIfNotExist,
            final boolean createFile
    )
            throws IOException, ClassNotFoundException {
        if ( createFile ) {
            if ( perceptronFile.exists() ) {
                //si el archivo existe, lo cargamos como perceptron entrenado al juego
                neuralNetwork = (BasicNetwork) SerializeObject.load(perceptronFile);
            } else {
                //Si el archivo no existe, creamos un perceptron nuevo inicializado al azar
                neuralNetwork = initializeEncogPerceptron(randomizedIfNotExist);
                SerializeObject.save(perceptronFile, neuralNetwork);
            }
        } else {
            neuralNetwork = initializeEncogPerceptron(randomizedIfNotExist);
        }
    }

    public
    double normalizeOutput( final Double value ) {
        return normOutput.normalize(value);
    }

    /**
     * Guarda la red neuronal en un archivo
     *
     * @param neuralNetworkFile red neuronal a salvar en archivo.
     */
    public
    void saveNeuralNetwork( final File neuralNetworkFile )
            throws IOException {
        SerializeObject.save(neuralNetworkFile, neuralNetwork);
    }


    @Override
    public
    void setBias(
            final int layerIndex,
            final int neuronIndex,
            final double correctedBias
    ) {
        if ( hasBias(layerIndex) ) {
            neuralNetwork.setWeight(layerIndex - 1, neuralNetwork.getLayerNeuronCount(layerIndex - 1), neuronIndex, correctedBias);
        } else {
            throw new IllegalStateException("No hay bias en la capa " + layerIndex);
        }
    }

    @Override
    public
    void setWeight(
            final int layerIndex,
            final int neuronIndex,
            final int neuronIndexPreviousLayer,
            final double correctedWeight
    ) {
        neuralNetwork.setWeight(layerIndex - 1, neuronIndexPreviousLayer, neuronIndex, correctedWeight);
    }

    @Override
    public
    String toString() {
        return "EncogInterface{" + "concurrentInput=" + concurrentInput + ", activationFunction=" + activationFunction +
               ", activationFunctionForEncog=" + Arrays.toString(activationFunctionForEncog) + ", activationFunctionMax=" + activationFunctionMax +
               ", activationFunctionMin=" + activationFunctionMin + ", derivedActivationFunction=" + derivedActivationFunction + ", hasBias=" +
               hasBias + ", neuralNetwork=" + neuralNetwork + ", neuronQuantityInLayer=" + Arrays.toString(neuronQuantityInLayer) + ", normOutput=" +
               normOutput + '}';
    }
}
