<h1 align="center">
	<img src="https://i.imgur.com/rqOh0KW.png" alt="IA">
</h1>

# Caso de estudio: Easy Pig entrenado por Coeus
Este proyecto es un caso de prueba de la librería [Coeus](https://github.com/TesisLuciaFrancoRenzo/coeus) 
en la cual se configuro para aprender a usar la misma de una forma didáctica. 
Para mas detalles referirse al [Informe](https://docs.google.com/document/d/1arNnKmmV7xc9qDrgPNbtxQXO8b81HknmJQKCshfAzUU/edit?usp=sharing) del mismo. 

## El Juego
El objetivo del Easy Pig es ser el primer jugador en alcanzar una puntuación de 100 o más. Con la intención de no complicar la implementación del juego, decidimos implementar una versión simplificada del PIG para mostrar el uso de la API sin necesidad de extender este capitulo mas alla de los conocimientos básicos. En cada turno el jugador debe elegir cuantas veces tirara el dado ( entre 1 y 10 veces). Cuando finaliza el turno si no se sacó un uno en algún dado, se suman todas las tiradas al puntaje del jugador actual y se pasa el turno al siguiente jugador. Si se saca al menos un uno, se pasa automáticamente al siguiente jugador sin ganar ningún punto.

## Instalación
El proyecto esta construido utilizando Gradle (incorporado en el 
repositorio). 

##### Requisitos
- Java JDK 8 o superior.
- Tener configurada la variable de entorno ***JAVA_HOME***. 

##### Dependencias
- Proyecto git de [Coeus](https://github.com/TesisLuciaFrancoRenzo/coeus) en un directorio contiguo a este proyecto 
(o librería jar en el directorio lib)

##### Instrucciones Recomendadas
- `gradlew clean`: limpia los directorios del proyecto.   
- `gradlew build`: compila el proyecto.
- `gradlew finalFatJar`: crea un jar con la librería lista para 
usar.  
- `gradlew junitPlatformTest`:  ejecuta los test de JUnit.
- `gradlew javadoc`:  compila javadoc.

## Instrucciones de uso
`java -jar coeus.pig-1.0.0.jar [parameter]`
donde los parámetros pueden ser:
- `Humans`: Juega Humano vs Humano.
- `HumanVsRandom (1|2)`: Juega Humano vs IA Random.
- `SimulateGreedy (number)`: Calcula estadísticas utilizando una IA Greedy.
- `SimulateGreedyVsInitial (number)`: Calcula estadísticas utilizando una IA Greedy vs una IA sin entrenar.
- `SimulateGreedyVsLazy (number)`: Calcula estadísticas utilizando una IA Greedy vs una IA Lazy.
- `SimulateGreedyVsRandom (number)`: Calcula estadísticas utilizando una IA Greedy vs una IA Random.
- `SimulateGreedyVsTrained (number)`: Calcula estadísticas utilizando una IA Greedy vs una IA entrenada.
- `SimulateLazy (number)`: Calcula estadísticas utilizando una IA Lazy.
- `SimulateLazyVsInitial (number)`: Calcula estadísticas utilizando una IA Lazy vs una IA sin entrenar. 
- `SimulateLazyVsTrained (number)`: Calcula estadísticas utilizando una IA Lazy vs una IA entrenada.
- `SimulateRandom (number)`: Calcula estadísticas utilizando una IA Random.
- `SimulateRandomVsGreedy (number)`: Calcula estadísticas utilizando una IA Random vs una IA Greedy.
- `SimulateRandomVsInitial (number)`: Calcula estadísticas utilizando una IA Random vs una IA sin entrenar.
- `SimulateRandomVsLazy (number)`: Calcula estadísticas utilizando una IA Random vs una IA Lazy.
- `SimulateRandomVsTrained (number)`: Calcula estadísticas utilizando una IA Random vs una IA entrenada.
- `SimulateTrained (number)`: Calcula estadísticas utilizando una IA Entrenada vs sí misma.
- `TrainedVsHuman`: Juega una IA entrenada vs un Humano 
- `TrainAlone (number)`: Entrena una IA vs sí misma.
- `TrainVsGreedy (number)`: Entrena una IA vs una IA Greedy.
- `TrainVsRandom (number)`: Entrena una IA vs una IA Random.

Donde `(number)` es la cantidad de partidas a jugar.
##### Nota
Todos los parámetros que involucren IA entrenada, sólo funcionan si se entreno algún perceptrón con anterioridad.

## Licencia
[![GNU GPL v3.0](http://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl.html)
