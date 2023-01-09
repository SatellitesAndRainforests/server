#!/bin/bash
mvn clean
mvn package
java -jar target/risServer-0.0.1-SNAPSHOT.jar 
