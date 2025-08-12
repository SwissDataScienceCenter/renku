## Usage: make help

MIRROR:=https://netcologne.dl.sourceforge.net

UMLJAR?=lib/plantuml-jar-asl-1.2017.15.jar
PS2PDF?=pstopdf

UMLTXT:=$(shell grep -l "@startuml" *.uml)
UMLEPS:=$(patsubst %.uml,%.eps,$(UMLTXT))
UMLPDF:=$(patsubst %.uml,%.pdf,$(UMLTXT))
UMLPNG:=$(patsubst %.uml,%.png,$(UMLTXT))

.PHONY: clean eps pdf nuke help

help:
	@-echo
	@-echo "  Usage:"
	@-echo "       make png"
	@-echo "       make pdf"
	@-echo "       make eps"
	@-echo
	@-echo "  Automatically find *.uml file with UML code (with @startuml in it) and create"
	@-echo "  the corresponding PNG|PDF|EPS. See http://plantuml.com for details."
	@-echo "  The plantuml code must be between @startuml and @enduml in the *.uml files."
	@-echo


png: $(UMLJAR) $(UMLPNG)

pdf: $(UMLJAR) $(UMLPDF)

eps: $(UMLJAR) $(UMLEPS)

lib/%.jar: lib/%.zip
	unzip -o -d lib $^
	mv lib/plantuml.jar $@
	touch $@

$(UMLJAR:%.jar=%.zip):
	@ if [ ! -f $(UMLJAR) ]; then \
	    mkdir -p lib; \
	    curl -o lib/plantuml-jar-asl-1.2017.15.zip '$(MIRROR)/project/plantuml/1.2017.15/plantuml-jar-asl-1.2017.15.zip' ; \
	fi

%.eps: %.uml
	sed -ne '/@startuml/,/@enduml/p' $^|java -jar $(UMLJAR) -teps -p > $@

%.png: %.uml
	sed -ne '/@startuml/,/@enduml/p' $^|java -jar $(UMLJAR) -tpng -p > $@

%.pdf: %.eps
	$(PS2PDF) $<

clean:
	@-rm -f $(UMLEPS)
	@-rm -rf lib

nuke: clean
	@-rm -f $(UMLPDF)
	@-rm -f $(UMLPNG)

