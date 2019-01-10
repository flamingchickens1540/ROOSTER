#!/usr/bin/env bash

set -ev

# check if $TRAVIS_TAG is set-if so, rename output jars to make sense in preparation for uploading them to Github
if [ -n "$TRAVIS_TAG" ]
then
  mv build/libs/ROOSTER.jar "build/libs/ROOSTER-$TRAVIS_TAG.jar"
  mv build/libs/ROOSTER-sources.jar "build/libs/ROOSTER-$TRAVIS_TAG-sources.jar"
  mv build/libs/ROOSTER-javadoc.jar "build/libs/ROOSTER-$TRAVIS_TAG-javadoc.jar"
fi
