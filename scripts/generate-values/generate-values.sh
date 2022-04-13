#!/usr/bin/env bash

POSITIONAL_ARGS=()

DOCKER=0
while [[ $# -gt 0 ]]; do
  case $1 in
    --docker)
      DOCKER=1
      shift
      ;;
    *)
      POSITIONAL_ARGS+=("$1") # save positional arg
      shift
      ;;
  esac
done

RENKU_REPO="https://github.com/SwissDataScienceCenter/renku"
REPO_PATH="/blob/master/scripts/generate-values/"
SCRIPT="generate-values.py"
TEMPLATE_FILE="base-renku-values.yaml.template"

if [ $DOCKER -ne 0 ]
then
docker run --rm -ti -v ${PWD}:/work renku/generate-values $POSITIONAL_ARGS
exit 0
fi

# fetch the files we need
if [ ! -f "$SCRIPT" ]
then
wget ${RENKU_REPO}${REPO_PATH}${SCRIPT}
fi

if [ ! -f "$TEMPLATE_FILE" ]
then
wget ${RENKU_REPO}${REPO_PATH}${TEMPLATE_FILE}
fi

if [ ! -d ".venv-renku-values" ]
then
echo "virtual environment not found, creating one..."
pip install virtualenv
virtualenv .venv-renku-values
source .venv-renku-values/bin/activate
pip install -r requirements.txt
deactivate
fi

source .venv-renku-values/bin/activate
python3 $SCRIPT $POSITIONAL_ARGS

deactivate
