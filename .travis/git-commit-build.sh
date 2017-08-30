#!/bin/bash

git add --force source/images/generated
git commit -m "Travis build ${TRAVIS_BUILD_NUMBER} from ${TRAVIS_BRANCH}/${TRAVIS_COMMIT}"
git push --force origin HEAD:travis-build

