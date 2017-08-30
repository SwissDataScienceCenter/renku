# Minimal makefile for Sphinx documentation
#

# You can set these variables from the command line.
SPHINXOPTS    =
SPHINXBUILD   = python3 -msphinx
SPHINXPROJ    = RENGA
SOURCEDIR     = source
BUILDDIR      = build

# Put it first so that "make" without argument is like "make help".
help:
	@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
	@echo
	@if [ -t 1 ]; then\
	 echo $$'\033[1mGeneral\033[0m';\
	 echo $$'\033[1;34m  uml\033[0m         to make files from resources/uml/%.uml in source/images/generated/%.png';\
	 echo $$'\033[1;34m  clean\033[0m       to clean the build directory';\
	 echo ;\
	else\
	 echo 'General';\
	 echo '  uml         to make files from resources/uml/%.uml in source/images/generated/%.png';\
	 echo '  clean       to clean the build directory';\
	 echo ;\
	fi

.PHONY: help Makefile clean uml


# Catch-all target: route all unknown targets to Sphinx using the new
# "make mode" option.  $(O) is meant as a shortcut for $(SPHINXOPTS).
%: Makefile uml
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

uml:
	@make -C resources/uml png
	@mkdir -p source/images/generated
	@cp -f resources/uml/*.png source/images/generated/

clean:
	@- rm -rf build/*
	@- rm -rf source/images/generated/
	@make -C resources/uml nuke
