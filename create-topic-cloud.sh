#!/bin/bash

set -e -u 

confluent kafka topic create \
  test-topic \
  --partitions $1
