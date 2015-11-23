#!/usr/bin/env bash
./deploy-local.sh
echo "test clean build"
cd modifier-sample
../gradlew clean build --refresh-dependencies --stacktrace $1 $2
cd ..
