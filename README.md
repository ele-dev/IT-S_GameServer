# IT-S GameServer

# What is this application for?
This Java Application runs on a linux machine.
In combination with a MariaDB/MySQL Database it forms the backend of 
a online multiplayer game.

# Software Requirements
A Linux based Server that has Java SDK 8 (or higher) installed is required.
The application also depends on MariaDB/MySQL Database which has to run on the same machine.
The Firewall must be configured properly to allow network traffic through the configured port

# Installation/Setup Steps:
- Clone the master branch of the repo on a linux machine
- Run the buildApplication.sh script to get a runnable .jar file
- Set the environment variables and settings in the config.txt file
- Make sure the Database is running on the same host


