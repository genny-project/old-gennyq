#!/bin/bash

project=${PWD##*/}
version=$(cat src/main/resources/${project}-git.properties | grep 'git.build.version' | cut -d'=' -f2)
echo "version is ${version}"
echo "project is ${project}"
function prop() {
  grep "${1}=" ${file} | cut -d'=' -f2
}


echo "Building docker gennyproject/${project}:${version}"
./mvnw clean package -Dquarkus.container-image.build=true -DskipTests=true
docker tag ${USER}/${project}:${version}  gennyproject/${project}:latest 
docker tag ${USER}/${project}:${version}  gennyproject/${project}:${version} 
