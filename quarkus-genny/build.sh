#!/bin/bash
mvn -N versions:update-child-modules
mvn clean package 
