#!/bin/bash

for project in apiq bridgeq messagesq keisha genny-proxy notes shleemy
do
    echo $project
    docker push gennyproject/${project}:latest
done

echo "Finished"
say "Finished pushing all"

