#!/usr/bin/env sh
# -*- coding: utf-8 -*-
#
# Copyright 2017-2018 - Swiss Data Science Center (SDSC)
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

# quit on errors:
set -o errexit

# quit on unbound symbols:
set -o nounset

# run the renku-demo container
docker run --rm --network host renku/renku-demo

pytest -v
sphinx-build -nNW -b spelling -d docs/_build/doctrees docs docs/_build/spelling
sphinx-build -qnNW docs docs/_build/html
shellcheck */**/*.sh
