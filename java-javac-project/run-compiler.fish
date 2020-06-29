#!/usr/bin/env fish

rm sample/*.class
rm sample/annotation/*.class
rm sample/*Generated.java

# Run w/out doing annotation processing or compilation. Compile the registered annotation processor.
# More info: https://docs.oracle.com/javase/7/docs/technotes/tools/solaris/javac.html
javac -cp . -proc:none sample/annotation/*.java

# Run the compiler on Hello using annotations.
javac -cp . sample/*.java