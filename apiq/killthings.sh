#!/bin/bash
jps -l | grep apiq | cut -d" " -f1 | xargs kill -9

