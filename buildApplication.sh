#!/bin/sh

# This is a linux shell script that can compile sourcefiles
# and packages them into a .jar archive together with external libraries

# First step: source file compilation
javac -cp lib/*.jar -d bin src/*/*.java -Xlint:deprecation

# Second step: packaging/exporting binaries
jar cfm GameServer.jar manifest.txt -C bin serverPackage
# jar --create --file GameServer.jar --manifest manifest.txt @classes.list

exit