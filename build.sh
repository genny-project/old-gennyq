#!/bin/bash
mvn -N versions:update-child-modules -Dmaven.test.skip=true
cd quarkus-genny
mvn -N versions:update-child-modules -Dmaven.test.skip=true
cd ..
mvn clean package