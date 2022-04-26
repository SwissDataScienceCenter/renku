#!/usr/bin/env bash
set -e

DOCKER=0
while [[ $# -gt 0 ]]; do
  case $1 in
    --docker)
      DOCKER=1
      shift
      ;;
  esac
  break 1
done

RENKU_REPO="https://raw.githubusercontent.com/SwissDataScienceCenter/renku"
REPO_PATH="/master/scripts/generate-values/"
SCRIPT="generate-values.py"
TEMPLATE_FILE="base-renku-values.yaml.template"

if [ $DOCKER -ne 0 ]
then
docker run --rm -ti -v ${PWD}:/work renku/generate-values $@
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

if [ ! -f "requirements.txt" ]
then
wget ${RENKU_REPO}${REPO_PATH}requirements.txt
fi

if [ ! -d ".venv-renku-values" ]
then
echo "virtual environment not found, creating one..."
python3 -m pip install virtualenv
virtualenv .venv-renku-values
. .venv-renku-values/bin/activate
pip install -r requirements.txt
deactivate
fi

echo $POSITIONAL_ARGS

. .venv-renku-values/bin/activate
python3 $SCRIPT $@

deactivate
