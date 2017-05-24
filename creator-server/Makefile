TESTDIR = te-test

all:	build

build:	build-backend

build-backend:
	cabal install --force-reinstalls common/ creator/


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
