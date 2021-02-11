#!/bin/bash
ENV_FILE=genny.env docker-compose down  $@ 
ENV_FILE=genny.env docker-compose rm -f $@
