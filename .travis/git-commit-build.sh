#!/bin/bash

git add --force resources/uml/*.png
git commit -m "Travis build ${TRAVIS_BUILD_NUMBER} from ${TRAVIS_BRANCH}/${TRAVIS_COMMIT}"
git push --force origin travis-build

