#!/bin/bash

git_init() {
    git config --global user.email "travis@travis-ci.org"
    git config --global user.name "Travis CI"
}

git_commit() {
    git add --force source/images/generated
    git commit -m "Travis build ${TRAVIS_BUILD_NUMBER} from ${TRAVIS_BRANCH}/${TRAVIS_COMMIT}"
}

git_push() {
    if [[ -n "${GITHUB_TOKEN}" ]]; then
        git remote add origin-travis https://${GITHUB_TOKEN}@github.com/SwissDataScienceCenter/renga-documentation.git > /dev/null 2>&1
        git push --force origin-travis HEAD:travis-build
    else
        git push --force origin HEAD:travis-build
    fi
}

git_init
git_commit
git_push

