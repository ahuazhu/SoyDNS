#!/bin/bash

clear > test.data

echo 'generating test data...'

for i in `seq 1 10000`
do
   echo www.`uuidgen | tail -c 5`.com	A  >> test.data
done

