PDFLATEX = pdflatex -interaction=nonstopmode
BIBTEX = bibtex
PDFCROP = pdfcrop
PDFTOPS = pdftops
CONVERT = convert

all: expose.pdf

expose.pdf: expose.tex
	$(PDFLATEX) $^
	$(PDFLATEX) $^
	$(PDFLATEX) $^

tempclean:
	$(RM) $(shell ls *.aux *.bbl *.blg *.dvi *.log *.toc *.lof *.lot *.lol *.loa *.out 2>/dev/null)

clean: tempclean
	$(RM) expose.pdf
