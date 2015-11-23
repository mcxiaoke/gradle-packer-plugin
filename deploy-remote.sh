#!/usr/bin/env bash
echo "build and deploy plugin artifacts to remote repo..."
./gradlew clean build uploadArchives --stacktrace $1
