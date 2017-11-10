# antsp-gui

The purpose of this program is to solve TSP problems using Ant Colony Algorithms.

It requires two libraries to work:
https://github.com/dogenkigen/tsplib-parser
https://github.com/dogenkigen/antsp/tree/master

To build the project first you need to build both mentioned above libraries and then run maven command form root 
directory:

`mvn clean assembly:assembly`

This will create `antsp-gui-jar-with-dependencies.jar` file in target directory which can be run as any other Java 
executable JAR. 