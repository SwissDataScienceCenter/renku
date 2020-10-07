#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#
# RENKU documentation build configuration file, created by
# sphinx-quickstart on Tue Aug 22 10:42:25 2017.
#
# This file is execfile()d with the current directory set to its
# containing dir.
#
# Note that not all possible configuration values are present in this
# autogenerated file.
#
# All configuration values have a default; values that are commented out
# serve to show the default.

import os
import sys
from os.path import abspath, join, dirname

import requests
import yaml

sys.path.insert(0, abspath(join(dirname(__file__))))

# -- General configuration ------------------------------------------------

# If your documentation needs a minimal Sphinx version, state it here.
#
# needs_sphinx = '1.0'

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = [
    "plantweb.directive",
    "sphinx.ext.autodoc",
    "sphinx.ext.coverage",
    "sphinx.ext.doctest",
    "sphinx.ext.githubpages",
    "sphinx.ext.graphviz",
    "sphinx.ext.ifconfig",
    "sphinx.ext.intersphinx",
    "sphinx.ext.todo",
    "sphinx.ext.viewcode",
    "sphinxcontrib.spelling",
]

# Plantweb configuration
plantweb_defaults = {"format": "png"}

# Add any paths that contain templates here, relative to this directory.
templates_path = ["_templates"]

# The suffix(es) of source filenames.
# You can specify multiple suffix as a list of string:
#
# source_suffix = ['.rst', '.md']
source_suffix = [".rst", ".md"]

# The master toctree document.
master_doc = "index"

# General information about the project.
project = "Renku"
copyright = "2017-2020, Swiss Data Science Center"
author = (
    "Mohammad Alisafaee, Andreas Bleuler, Eric Bouillet, \n"
    "Lorenzo Cavazzi, Christine Choirat, Jakub Chrobasik, \n"
    "Pamela Delgado, Julien Eberle, Virginia Friedrich, \n"
    "Fotis Georgatos, Ralf Grubenmann, Emma Jablonski, \n"
    "Jiri Kuncar, Izabela Moise, Chandrasekhar Ramakrishnan, \n"
    "Rok Roškar, Sofiane Sarni, Sandra Savchenko De Jong, \n"
    "Johann-Michael Thiebaut, Olivier Verscheure"
)

# The version info for the project you're documenting, acts as replacement for
# |version| and |release|, also used in various other places throughout the
# built documents.
#
# The short X.Y version.
version = "0.4"
# The full version, including alpha/beta/rc tags.
release = "0.6.0"

# The language for content autogenerated by Sphinx. Refer to documentation
# for a list of supported languages.
#
# This is also used if you do content translation via gettext catalogs.
# Usually you set "language" from the command line for these cases.
language = None

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This patterns also effect to html_static_path and html_extra_path
exclude_patterns = ["_build/*"]

# The name of the Pygments (syntax highlighting) style to use.
# pygments_style = 'sphinx'

# If true, `todo` and `todoList` produce output, else they produce nothing.
todo_include_todos = True

# Figure numbering
numfig = True

# -- Options for HTML output ----------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
#
html_theme = "renku"

# Theme options are theme-specific and customize the look and feel of a theme
# further.  For a list of options available for each theme, see the
# documentation.
#
html_theme_options = {
    "description": "A software platform and tools for "
    "reproducible and collaborative data analysis.",
    "github_repo": "renku",
    "extra_nav_links": {
        "renku@GitHub": "https://github.com/SwissDataScienceCenter/renku"
    },
    "fixed_sidebar": True,
    "show_relbars": True,
}

# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
html_static_path = ["_static"]

# Custom sidebar templates, must be a dictionary that maps document names
# to template names.
#
# This is required for the alabaster theme
# refs: http://alabaster.readthedocs.io/en/latest/installation.html#sidebars
html_sidebars = {
    "**": [
        "about.html",
        "navigation.html",
        "relations.html",  # needs 'show_related': True theme option to display
        "searchbox.html",
        "donate.html",
    ]
}

# -- Options for HTMLHelp output ------------------------------------------

# Output file base name for HTML help builder.
htmlhelp_basename = "RENKUdoc"

# -- Options for LaTeX output ---------------------------------------------

latex_elements = {
    # The paper size ('letterpaper' or 'a4paper').
    #
    # 'papersize': 'letterpaper',
    # The font size ('10pt', '11pt' or '12pt').
    #
    # 'pointsize': '10pt',
    # Additional stuff for the LaTeX preamble.
    #
    # 'preamble': '',
    # Latex figure (float) alignment
    #
    # 'figure_align': 'htbp',
}

# Grouping the document tree into LaTeX files. List of tuples
# (source start file, target name, title,
#  author, documentclass [howto, manual, or own class]).
latex_documents = [
    (
        master_doc,
        "RENKU.tex",
        "RENKU Documentation",
        author,
        "manual",
    )
]

# -- Options for manual page output ---------------------------------------

# One entry per manual page. List of tuples
# (source start file, name, description, authors, manual section).
man_pages = [(master_doc, "renku", "RENKU Documentation", [author], 1)]

# -- Options for Texinfo output -------------------------------------------

# Grouping the document tree into Texinfo files. List of tuples
# (source start file, target name, title, author,
#  dir menu entry, description, category)
texinfo_documents = [
    (
        master_doc,
        "RENKU",
        "RENKU Documentation",
        author,
        "RENKU",
        "One line description of project.",
        "Miscellaneous",
    )
]

# Example configuration for intersphinx: refer to the Python standard library.
intersphinx_mapping = {"python": ("https://docs.python.org/3/", None)}

# spellchecking config
spelling_show_suggestions = True
spelling_lang = "en_US"

# graphviz config
graphviz_output_format = "svg"

# suppress the warning about graph being overriden
suppress_warnings = ["app.add_directive"]


# sidebar
on_rtd = os.environ.get("READTHEDOCS", None) == "True"
rtd_version = os.environ.get("READTHEDOCS_VERSION", "latest")
if rtd_version not in ["stable", "latest"]:
    # tag build, update respective renku-python as well
    with open("./charts/renku/requirements.yaml", "r") as f:
        requirements = yaml.load(f)
    renku_core_entry = next(
        (d for d in requirements["dependencies"] if d["name"] == "renku-core"), None
    )
    rtd_version = "v{}".format(renku_core_entry["version"])

    # retrigger build of renku-python docs so they point to the correct version
    token = os.environ.get("RTD_TOKEN")

    r = requests.post(
        f"https://readthedocs.org/api/v3/projects/renku/versions/{rtd_version}/builds/",
        headers={"Authorization": f"Token {token}"},
    )
    r.raise_for_status()

intersphinx_mapping = {
    "python": ("https://docs.python.org/", None),
}

# -- Custom Document processing ----------------------------------------------

from gensidebar import generate_sidebar

generate_sidebar(on_rtd, rtd_version, "renku")
