TARGET = thesis
SOURCES = *.tex *.bib chapters/*.tex
PDFREADER = okular
IMAGES = $(shell find images/ -name '*.png')

########################################################
# There is no need to modify anything below this point #
########################################################
IMAGES_CONVERTED = $(shell /bin/bash -c 'for image in $(IMAGES) ; do echo $${image%.*}.eps ; done')

dvi: $(TARGET).dvi

dvishow: dvi
	xdvi $(TARGET).dvi &

$(TARGET).dvi: sources converted_images
	latex $(TARGET)
	latex $(TARGET) \
	&& bibtex $(TARGET) \
	&& bibtex $(TARGET) \
	&& latex $(TARGET) \
	&& latex $(TARGET) \
	&& latex $(TARGET)

psshow: ps
	gv $(TARGET).ps&

ps: $(TARGET).ps

$(TARGET).ps: dvi
	dvips -X 600 -Y 600 -t a4 -o $(TARGET).ps $(TARGET).dvi

im: $(TARGET).pdf

pdfshow: pdf
	$(PDFREADER) $(TARGET).pdf

$(TARGET).pdf: sources converted_images
	pdflatex $(TARGET) \
	pdflatex $(TARGET) \
	&& bibtex $(TARGET) \
	&& bibtex $(TARGET) \
	&& pdflatex $(TARGET) \
	&& pdflatex $(TARGET) \
	&& pdflatex $(TARGET)

pdf: $(TARGET).pdf

images/%.eps: images/%.png
	convert $< $@

converted_images: $(IMAGES_CONVERTED)

clean:
	rm -f *.backup *~ $(TARGET).aux $(TARGET).lof $(TARGET).lot $(TARGET).toc $(TARGET).dvi $(TARGET).log $(TARGET).ps $(TARGET).pdf $(TARGET).bbl $(TARGET).blg $(TARGET).out $(TARGET).bst $(TARGET).inx images/*~ images/*.eps *.bak $(TARGET).svn $(TARGET).lol $(TARGET).fot $(TARGET).idx $(TARGET).cb $(TARGET).loa $(TARGET).abk.aux $(TARGET).gls.aux

sources: $(SOURCES)
