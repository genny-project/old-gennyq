#!/bin/bash
mysql -P 3310 -h 127.0.0.1 -u gennydb --password=password gennydb  -e 'drop table qvalidation;';
mysql -P 3310 -h 127.0.0.1 -u gennydb --password=password gennydb  -e 'drop table qattribute;';
mysql -P 3310 -h 127.0.0.1 -u gennydb --password=password gennydb  -e 'drop table qentity_q;';
mysql -P 3310 -h 127.0.0.1 -u gennydb --password=password gennydb  -e 'drop table qbaseentity_baseentity;';
mysql -P 3310 -h 127.0.0.1 -u gennydb --password=password gennydb  -e 'drop table qbaseentity_attribute;';
mysql -P 3310 -h 127.0.0.1 -u gennydb --password=password gennydb  -e 'drop table qbaseentity;';
