#!/usr/bin/env bash
# -*- coding: utf-8 -*-
#
# Copyright 2017 - Swiss Data Science Center (SDSC)
# A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
# Eidgenössische Technische Hochschule Zürich (ETHZ).
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

log()
{
    echo $(date +'%Y%m%d-%H%M%S%z')"} $*"
}

error()
{
    log "ERROR -- $*"
}

info()
{
    (( $verbose>0 )) && log "INFO  -- $*"
}

usage()
{
    cat <<-HERE
	usage: $0 [-v][-e VIRTUAL_ENV][-p PYTHON_EXE]
	
	    Build SPHINX documentation locally.

	    Options:
	    -v  Increase verbosity
	    -e  Specify or reuse a python virtual environment
	    -p  Force a python version.

HERE
}

run_python_cmd()
{
    local -r PYTHON_EXE=${1}
    local -r VIRTUAL_ENV_DIR=${2:-_build/python.$$}

    if [[ ! -e "${VIRTUAL_ENV_DIR}" ]]; then
        info "create python virtual environment: ${VIRTUAL_ENV_DIR}"
        if ! command -v virtualenv; then
            error "virtualenv must be installed."
            return
        fi

        local -a VIRTUAL_ENV_OPT=("--clear")
        (( verbose>0 )) && VIRTUAL_ENV_OPT+=("--verbose")
        if [[ -n "${PYTHON_EXE}" ]]; then
            if ! command -v ${python_exe}; then
                error "command not found: ${python_exe}"
                return
            fi
            VIRTUAL_ENV_OPT+=("-p ${PYTHON_EXE}")
        fi

        virtualenv "${VIRTUAL_ENV_OPT}" "${VIRTUAL_ENV_DIR}"
        if [[ ! -e "${VIRTUAL_ENV_DIR}"/bin/activate ]]; then
            error "directory ${VIRTUAL_ENV_DIR} could not be created."
            return
        fi
        . "${VIRTUAL_ENV_DIR}"/bin/activate
    elif [[ ! -e "${VIRTUAL_ENV_DIR}/bin/activate" ]]; then
        error "invalid virtualenv '${VIRTUAL_ENV_DIR}': no activate script."
        return
    else
        info "reuse python virtual environment: ${VIRTUAL_ENV_DIR}"
        . "${VIRTUAL_ENV_DIR}"/bin/activate
    fi
    if ! command -v sphinx-build; then
        info "install required python packages"
        pip install -r ./requirements.txt
    fi
    info "build doc"
    sphinx-build -qnNW . _build/html
    deactivate
    if [[ "${2}" != "${VIRTUAL_ENV_DIR}" ]]; then
        info "remove python virtual env: ${VIRTUAL_ENV_DIR}"
        rm -rf "${VIRTUAL_ENV_DIR}"
    fi
}

main()
{
    unalias -a
    local -r ROOT="$(dirname ${BASH_SOURCE[0]})"
    local -i verbose=0
    local python_exe=
    local virtual_env=
    while getopts "e:hp:v" ARG; do
        case "${ARG}" in
            e) virtual_env="${OPTARG}" ;;
            h) usage; return ;;
            v) verbose=1 ;;
            p) python_exe="${OPTARG}" ;;
        esac
    done

    pushd "${ROOT}"
    run_python_cmd "${python_exe}" "${virtual_env}"
    popd
}

main "$@"
