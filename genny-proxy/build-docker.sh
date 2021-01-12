#!/bin/bash

#docker build -f src/main/docker/Dockerfile.jvm -t gennyproject/abn-lite .

#version=7.0.0  #$(grep 'git.build.version')
#pwd=${PWD##*/}
#function prop() {
#  grep "${1}=" ${file} | cut -d'=' -f2
#}
#
#
#echo "Building docker gennyproject/${pwd}:${version}"
#pushd $PWD
#./mvnw clean package -Dquarkus.container-image.build=true -DskipTests=true
#docker tag ${USER}/${pwd}:${version}  gennyproject/${pwd}:latest
#docker tag ${USER}/${pwd}:${version}  gennyproject/${pwd}:${version}
#popd
project=genny-proxy
file="src/main/resources/${project}-git.properties"

function prop() {
  grep "${1}=" ${file} | cut -d'=' -f2
}

if [ -z "${1}" ]; then
  version="latest"
else
  version="${1}"
fi

echo "The version here is ${version}"
if [ -f "$file" ]; then
  echo "$file found."
  echo "git.commit.id = " "$(prop 'git.commit.id')"
  echo "git.build.version = " "$(prop 'git.build.version')"
  docker build -f src/main/docker/Dockerfile.jvm -t gennyproject/${project}:"latest" .
  docker tag gennyproject/${project}:"latest" gennyproject/${project}:"$(prop 'git.build.version')"
else
  echo "ERROR: git properties $file not found."
fi

image_ids=$(docker images | grep ${project} | grep none)
if [ "${image_ids:-0}" == 0 ]; then
  echo 'Skip clean up'
else
  docker images | grep ${project} | grep none | awk '{print $3}' | xargs docker rmi
fi

