#!/bin/bash
jps -l | grep bootxport | cut -d" " -f1 | xargs kill -9

