# RENGA documentation

## Dependencies:

* Sphinx 1.6 or higher

    * http://www.sphinx-doc.org/

* Sphinx scala module

    * https://pythonhosted.org/sphinxcontrib-scaladomain/

* Sphinx readthedoc themes:

    * http://docs.readthedocs.io/en/latest/theme.html
    * https://pypi.python.org/pypi/sphinx_rtd_theme#installation)

* Graphviz (Plantuml diagrams):

    * http://www.graphviz.org/Download..php

    pip install -r docs/requirements.txt

## Usage:

    cd docs && make html

## Contributing

Please make sure that it is possible to build documentation without
warnings and error before creating pull request.

    sphinx-build -qnNW docs docs/_build/html
