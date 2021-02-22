#!/bin/bash
parms=$1
for project in qwandaq quarkus-genny 
do
    echo $project
    cd $project
    parentdir="$(dirname `pwd`)"
    rm -Rf $parentdir/$project/target/*
    mvn -N versions:update-child-modules -Dmaven.test.skip=true
    mvn clean install -DskipTests=true -U
    cd ..
    #mvn $clean install 
done


for project in apiq bridgeq messagesq keisha genny-proxy notes shleemy
do
    echo $project
    cd $project
    parentdir="$(dirname `pwd`)"
    rm -Rf $parentdir/$project/target/*
    mvn -N versions:update-child-modules -Dmaven.test.skip=true
    mvn clean package -DskipTests=true -U
    ./build-docker.sh
    cd ..
done

echo "Finished"
say "Finished building all"

