#!/usr/bin/env fish

rm sample/*.class
rm sample/annotation/*.class
rm sample/*Generated.java

# Run w/out doing annotation processing or compilation. Compile the registered annotation processor.
# More info: https://docs.oracle.com/javase/7/docs/technotes/tools/solaris/javac.html
javac -cp . -proc:none sample/annotation/*.java

# Run the compiler on Hello using annotations.
# https://medium.com/@joachim.beckers/debugging-an-annotation-processor-using-intellij-idea-in-2018-cde72758b78a
# https://stackoverflow.com/a/15938824/2085356
sh -c "javac -J-Xdebug -J-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=*:8000 -cp . sample/*.java"