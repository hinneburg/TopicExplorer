TESTDIR = te-test

all:	build

build:	build-frontend build-backend

build-backend:
	cabal install --force-reinstalls common/ creator/

build-frontend:
	cd frontend; make build


nginx:	nginx.conf
	nginx -c $(PWD)/$< -g 'daemon off;'

%.conf:	%-template.conf
	cat $< | \
	    perl -p -e 's!CREATOR_SERVER!http://localhost:7200!g' | \
	    perl -p -e 's!DOCUMENT_ROOT!$(PWD)/frontend/html!g' >$@


run:	build
	cd creator; cabal run creator-server -- --port 7200 \
	    >>/tmp/creator.out 2>>/tmp/creator.err &
	make nginx


testgit:
	(export TE=$$PWD/.. && cd /tmp/ && git clone $$TE $(TESTDIR) && \
	 (cd $(TESTDIR)/creator-server && make build) && \
	 rm -r $(TESTDIR)/)

testgit-first:
	(export TE=$$PWD/.. && cd /tmp/ && git clone $$TE $(TESTDIR))
	cd /tmp/$(TESTDIR)/creator-server && make build

testgit-again:
	(export TE=$$PWD/.. && cd /tmp/$(TESTDIR)/ && git pull $$TE)
	cd /tmp/$(TESTDIR)/creator-server && make build

testgit-revert:
	(cd /tmp/$(TESTDIR)/creator-server/ && git reset HEAD^ && git checkout -f)
