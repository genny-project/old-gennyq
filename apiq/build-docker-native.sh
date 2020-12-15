#!/bin/bash

./build-native.sh
#./mvnw package -Pnative -Dquarkus.native.container-build=true -DskipTests=true
docker build -f src/main/docker/Dockerfile.native -t gennyproject/apiq:native .
docker tag gennyproject/apiq:native gennyproject/apiq:8.0.0-native

