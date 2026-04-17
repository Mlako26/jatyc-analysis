#!/usr/bin/env bash

set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <test-folder>"
  exit 1
fi

TEST_DIR="$1"

if [ ! -d "$TEST_DIR" ]; then
  echo "Error: Directory '$TEST_DIR' does not exist"
  echo "Available tests:"
  ls -d */ | sed 's#/##'
  exit 1
fi

cd "$TEST_DIR"

echo "Compiling test: $TEST_DIR"

java -jar ../../dist/checker/dist/checker.jar  -classpath ../../dist/jatyc.jar  -processor jatyc.JavaTypestateChecker  *.java