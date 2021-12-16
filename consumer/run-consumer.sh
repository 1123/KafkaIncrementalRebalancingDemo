#!/bin/bash

set -e -u 

echo "Spring profiles: $SPRING_PROFILES_ACTIVE"

mvn spring-boot:run
