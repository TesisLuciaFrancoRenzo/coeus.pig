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

import ar.edu.unrc.coeus.tdlearning.learning.ELearningStyle;
import org.encog.engine.network.activation.ActivationFunction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static ar.edu.unrc.coeus.pig.Game.FIRST_DICES_TO_ROLL_INDEX;
import static ar.edu.unrc.coeus.pig.Game.LAZY_PERCEPTRON_INITIAL_WEIGHT;

public
class PerceptronConfiguration {
    private final double[]       alpha;
    private final boolean        collectStatistics;
    private final boolean        computeParallelBestPossibleAction;
    private final boolean[]      concurrencyInLayer;
    private final EncogInterface encogInterface;
    private final double         gamma;
    private final double         lambda;
    private final File           lazyFile;
    private final ELearningStyle learningStyle;
    private final boolean        replaceEligibilityTraces;
    private final File           trainedFile;

    public
    PerceptronConfiguration(
            final @NotNull String experimentName,
            final @NotNull File pathToFile,
            final @NotNull ActivationFunction[] encogActivationFunctions,
            final double activationFunctionMax,
            final double activationFunctionMin,
            final double maxReward,
            final double minReward,
            final boolean hasBias,
            final @NotNull int[] neuronQuantityInLayer,
            final boolean concurrentInput,
            final ELearningStyle learningStyle,
            final @NotNull double[] alpha,
            final double lambda,
            final boolean replaceEligibilityTraces,
            final double gamma,
            final @NotNull boolean[] concurrencyInLayer,
            final boolean computeParallelBestPossibleAction,
            final boolean collectStatistics
    )
            throws IOException {
        if ( !pathToFile.exists() ) {
            pathToFile.mkdirs();
        }
        if ( !pathToFile.isDirectory() ) {
            throw new IllegalArgumentException("pathToFile must be a directory");
        }
        encogInterface = new EncogInterface(encogActivationFunctions,
                activationFunctionMax,
                activationFunctionMin,
                maxReward,
                minReward,
                hasBias,
                neuronQuantityInLayer,
                concurrentInput);
        trainedFile = new File(pathToFile.getCanonicalPath() + File.separator + experimentName + "_Trained.ser");
        lazyFile = new File(pathToFile.getCanonicalPath() + File.separator + experimentName + "_Lazy.ser");
        this.computeParallelBestPossibleAction = computeParallelBestPossibleAction;
        this.learningStyle = learningStyle;
        this.alpha = alpha;
        this.lambda = lambda;
        this.replaceEligibilityTraces = replaceEligibilityTraces;
        this.gamma = gamma;
        this.concurrencyInLayer = concurrencyInLayer;
        this.collectStatistics = collectStatistics;
    }

    public
    double[] getAlpha() {
        return alpha;
    }

    public
    boolean[] getConcurrencyInLayer() {
        return concurrencyInLayer;
    }

    public
    EncogInterface getEncogInterface() {
        return encogInterface;
    }

    public
    double getGamma() {
        return gamma;
    }

    public
    double getLambda() {
        return lambda;
    }

    public
    ELearningStyle getLearningStyle() {
        return learningStyle;
    }

    public
    void initPerceptronToTrain()
            throws IOException, ClassNotFoundException {
        if ( lazyFile.exists() ) {
            lazyFile.delete();
        }
        if ( trainedFile.exists() ) {
            trainedFile.delete();
        }
        encogInterface.createPerceptron(lazyFile, false);
        encogInterface.setWeight(1, 0, FIRST_DICES_TO_ROLL_INDEX, LAZY_PERCEPTRON_INITIAL_WEIGHT);
        encogInterface.saveNeuralNetwork(lazyFile);
    }

    public
    boolean isCollectStatistics() {
        return collectStatistics;
    }

    public
    boolean isComputeParallelBestPossibleAction() {
        return computeParallelBestPossibleAction;
    }

    public
    boolean isReplaceEligibilityTraces() {
        return replaceEligibilityTraces;
    }

    public
    void loadLazyPerceptron()
            throws IOException, ClassNotFoundException {
        encogInterface.loadPerceptron(lazyFile);
    }

    public
    void loadTrainedPerceptron()
            throws IOException, ClassNotFoundException {
        encogInterface.loadPerceptron(trainedFile);
    }

    public
    void saveLazyNeuralNetwork()
            throws IOException {
        encogInterface.saveNeuralNetwork(lazyFile);
    }

    public
    void saveTrainedNeuralNetwork()
            throws IOException {
        encogInterface.saveNeuralNetwork(trainedFile);
    }

    @Override
    public
    String toString() {
        return "PerceptronConfiguration{" + "alpha=" + Arrays.toString(alpha) + ", collectStatistics=" + collectStatistics +
               ", computeParallelBestPossibleAction=" + computeParallelBestPossibleAction + ", concurrencyInLayer=" +
               Arrays.toString(concurrencyInLayer) + ", encogInterface=" + encogInterface + ", gamma=" + gamma + ", lambda=" + lambda +
               ", lazyFile=" + lazyFile + ", learningStyle=" + learningStyle + ", replaceEligibilityTraces=" + replaceEligibilityTraces +
               ", trainedFile=" + trainedFile + '}';
    }
}
