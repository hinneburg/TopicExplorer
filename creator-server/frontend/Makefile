build:	html/index.html

build-deps:

html/index.html:	src/CreatorConfig.elm build-deps
	mkdir -p $$(dirname $@)
	elm make --yes --warn --output $@ $<
	perl -i -p -e "s:<title>Main</title>:<title>Creator jobs</title>:" $@
