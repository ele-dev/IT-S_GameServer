# IT-S GameServer

# What is this application for?
This Java Application runs on a linux machine.
In combination with a MariaDB/MySQL Database it forms the backend of 
an online multiplayer game.

# Software Requirements
A Linux based Server that has Java SDK 8 (or higher) installed is required.
The application also depends on MariaDB/MySQL Database which has to run on the same machine.
The Firewall must be configured properly to allow network traffic through the configured port

# Installation/Setup Steps:
- Download the executable GameServer.jar file and the config.txt file from the repository
- Setup the MariaDB/MySQL Backend
- Set the environment variables and settings in the config.txt file
- Open terminal, execute java -server -jar GameServer.jar in the directory where the downloaded files are


