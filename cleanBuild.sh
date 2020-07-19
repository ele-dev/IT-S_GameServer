#!/bin/sh

# This shellscript deletes all compiled binary files and 
# the final java package from the source tree 

# First delete the .class files
rm -r bin/

# Then delete the java package
rm GameServer.jar

exit
