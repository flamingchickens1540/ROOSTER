#!/usr/bin/env bash

set -ev

# check if $TRAVIS_TAG is set-if so, rename lib's output jars to make sense in preparation for uploading them to Github
if [ -n "$TRAVIS_TAG" ]
then
  mv lib/build/libs/lib.jar "build/libs/ROOSTER-$TRAVIS_TAG.jar"
  mv lib/build/libs/lib-sources.jar "build/libs/ROOSTER-$TRAVIS_TAG-sources.jar"
  mv lib/build/libs/lib-javadoc.jar "build/libs/ROOSTER-$TRAVIS_TAG-javadoc.jar"
fi
