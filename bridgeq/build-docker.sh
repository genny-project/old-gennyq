#!/bin/bash

version=8.0.0  #$(grep 'git.build.version')
pwd=${PWD##*/}
function prop() {
  grep "${1}=" ${file} | cut -d'=' -f2
}


echo "Building docker gennyproject/${pwd}:${version}"
mvn package
docker build -f src/main/docker/Dockerfile.jvm -t gennyproject/${pwd}:${version} .
#./mvnw clean package -Dquarkus.container-image.build=true -DskipTests=true
#docker tag ${USER}/${pwd}:${version}  gennyproject/${pwd}:latest 
#docker tag ${USER}/${pwd}:${version}  gennyproject/${pwd}:${version} 
docker tag gennyproject/${pwd}:${version}  gennyproject/${pwd}:latest 
