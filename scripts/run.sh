#!/bin/bash
runcode=$1
GOOGLE_HOSTING_SHEET_ID=`cat ~/.genny/credentials/$runcode}
docker-compose   -f ./docker-compose.yml -f ../apiq/scripts/docker-compose.yml -f ../bridgeq/scripts/docker-compose.yml -f ./alyson.yml  up -d 
