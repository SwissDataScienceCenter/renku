#!/bin/bash
target_virtualenv_version=20.0.29
installed_virtualenv_version=`virtualenv --version`
echo "Installed virtualenv version: $installed_virtualenv_version"

if [[ ! "$installed_virtualenv_version" == *"$target_virtualenv_version" ]]; then
  echo "WARNING: Incompatible version of virtualenv installed: $installed_virtualenv_version . Replacing with version $target_virtualenv_version"
  python3 -m pip install virtualenv==$target_virtualenv_version
fi

virtualenv venv
source ./venv/bin/activate
pip install 'renku==%s'