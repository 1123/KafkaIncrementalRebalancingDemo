#!/bin/bash

set -e -u

echo "active Spring profile: $SPRING_PROFILES_ACTIVE"
mvn spring-boot:run
