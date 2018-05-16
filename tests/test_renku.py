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
"""Renku integration tests."""

import os
from urllib.parse import urljoin

import pytest
import splinter


def test_renku_login(browser):
    """Test Renku login."""
    url = urljoin(os.getenv('RENGA_ENDPOINT', 'http://localhost'), '/login')
    browser.visit(url)

    assert browser.is_element_present_by_id('username', wait_time=60)
    browser.fill('username', 'demo')
    browser.fill('password', 'demo')
    browser.find_by_id('kc-login').click()
    assert 'Renku' in browser.title
