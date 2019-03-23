# Introduction
Insert very awesome introduction here.

# Installation
After cloning the repository, import the pom.xml.
Run the Install maven target on the root project.

# Contributing
Submit a pull request.



# Creating the GameServer Schema
### H2 Database
Run the H2 Shell from the swg-server directory. After connection to the
database, run the following command.
```
RUNSCRIPT FROM 'game/src/main/resources/schema/h2/h2-schema.sql'
```