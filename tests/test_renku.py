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
import time
import json


def test_renku_login(browser):
    """Test Renku login."""
    url = urljoin(os.getenv('RENKU_ENDPOINT', 'http://localhost'), '/login')
    browser.visit(url)

    assert browser.is_element_present_by_id('username', wait_time=60)
    browser.fill('username', 'demo')
    browser.fill('password', 'demo')
    browser.find_by_id('kc-login').click()
    assert 'Renku' in browser.title


def test_notebook_launch(browser):
    """Test launching a notebook from UI."""
    with open('/tests/users.json', 'r') as f:
      users = json.load(f)
    url = urljoin(os.getenv('RENKU_ENDPOINT', 'http://localhost'), '/login')
    browser.visit(url)

    browser.fill('username', users[0]['username'])
    browser.fill('password', users[0]['password'])
    browser.find_by_id('kc-login').click()

    # wait a bit for the page to load, helps to avoid test failures
    time.sleep(2)
    assert 'Renku' in browser.title

    # go to the projects overview page
    assert browser.is_element_present_by_text(
        'Projects', wait_time=10
    )
    projects_link = browser.find_link_by_text('Projects')
    assert projects_link
    projects_link[0].click()

    # go to the project page
    assert browser.is_element_present_by_text(
        users[0]['username'] + '/weather-zh', wait_time=10
    )
    proj_link = browser.find_link_by_text(users[0]['username'] + '/weather-zh')
    assert proj_link
    proj_link[0].click()

    # go to the files tab
    files_link = browser.find_link_by_text('Files')
    assert files_link
    files_link[0].click()

    # click the notebooks tab
    notebooks_link = [
        link for link in browser.find_link_by_text('Notebooks')
        if 'projects' in link.outer_html
    ]
    assert notebooks_link
    notebooks_link[0].click()

    # click the notebook to be viewed
    analysis_link = browser.find_link_by_partial_text('notebooks/Analysis.ipynb')
    assert analysis_link
    analysis_link[0].click()

    ## TODO: check that the notebook actually gets launched
    # click the "Launch notebook" button
    # assert browser.is_element_present_by_text(
    #     'Launch Notebook', wait_time=10
    # )
    # notebook_button = browser.find_by_text('Launch Notebook')
    # assert notebook_button
    # notebook_button[0].click()

    # assert 'weather_ch' in browser.html
