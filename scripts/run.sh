#!/bin/bash
docker-compose   -f ./docker-compose.yml -f ../apiq/scripts/docker-compose.yml -f ../bridgeq/scripts/docker-compose.yml -f ./alyson.yml  up -d 
