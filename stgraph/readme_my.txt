STGraph - MY README

To remove newly ignored files from the remote github:
git rm -r --cached . && git add . && git commit -m "Removing all files in .gitignore"
and then push

Upon a new installation of the whole environment:
[http://svn.cleartech.it/svn/cetic   lmari/v1nKgjUhB4HpZ]
- all required projects must be checked out, one by one, from the SVN server;
- the directory src/datafiles/fun must be manually generated;
- the app [stgraphdoc]STFunctionDocumenter must be run.

Upon the addition of a new jar in lib, the reference to it must be added in:
- src/datafiles/stgraph.basic.properties
- key/signall.sh
- launch4j*
- makestgzip.sh

Upon the change of a jar, it must be:
- signed
- copied on the webserver for both updates and jnlp
- touch'ed before including it in the new version of the zip (to avoid a useless upgrade)
