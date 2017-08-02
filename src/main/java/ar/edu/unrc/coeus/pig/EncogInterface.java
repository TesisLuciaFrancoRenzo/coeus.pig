package ar.edu.unrc.coeus.pig;

import ar.edu.unrc.coeus.interfaces.INeuralNetworkInterface;
import ar.edu.unrc.coeus.utils.FunctionUtils;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
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
    private List< Function< Double, Double > > activationFunction         = null;
    private ActivationFunction[]               activationFunctionForEncog = null;
    private double                             activationFunctionMax      = 0.0;
    private double                             activationFunctionMin      = 0.0;
    private List< Function< Double, Double > > derivedActivationFunction  = null;
    private boolean                            hasBias                    = true;
    private BasicNetwork neuralNetwork;
    private int[]           neuronQuantityInLayer = null;
    private NormalizedField normInput             = null;
    private NormalizedField normOutput            = null;

    public
    EncogInterface(
            final ActivationFunction[] encogActivationFunctions,
            final double activationFunctionMax,
            final double activationFunctionMin,
            final boolean hasBias,
            final int[] neuronQuantityInLayer,
            final boolean concurrentInput,
            final NormalizedField normInput,
            final NormalizedField normOutput,
            final BasicNetwork neuralNetwork
    ) {
        activationFunctionForEncog = encogActivationFunctions;
        activationFunction = new ArrayList<>(encogActivationFunctions.length);
        derivedActivationFunction = new ArrayList<>(encogActivationFunctions.length);
        this.activationFunctionMax = activationFunctionMax;
        this.activationFunctionMin = activationFunctionMin;
        this.hasBias = hasBias;
        this.neuronQuantityInLayer = neuronQuantityInLayer;
        this.concurrentInput = concurrentInput;
        this.normInput = normInput;
        this.normOutput = normOutput;
        this.neuralNetwork = neuralNetwork;
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

    public
    NormalizedField getNormInput() {
        return normInput;
    }

    public
    NormalizedField getNormOutput() {
        return normOutput;
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
               hasBias + ", neuralNetwork=" + neuralNetwork + ", neuronQuantityInLayer=" + Arrays.toString(neuronQuantityInLayer) + ", normInput=" +
               normInput + ", normOutput=" + normOutput + '}';
    }
}
