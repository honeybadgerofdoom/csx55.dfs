module purge
module load courses/cs555
gradle build
clear
java -cp ./build/libs/csx55.chord.jar csx55.chord.Discovery $1