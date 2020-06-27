# Simple annotation processing in Java (using javac)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Introduction](#introduction)
- [Code structure](#code-structure)
- [Code generation](#code-generation)
- [Debugging the annotation processors](#debugging-the-annotation-processors)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Introduction

An example on how to use Java Annotation Processors with plain **javac**.

1. To test the example, you will need JDK 11 installed on your machine.
2. Run the `run-compiler.fish` file and see the console output when the compiler runs.

# Code structure

1. `javax.annotation.processing.Processor` file (in `META-INF/services` folder) registers the two processors.
2. `DebugAnnotationProcessor` can handle any source files that have any annotations. It just displays some information
   on each annotation it finds and allows other annotation processors to be chained.
3. `AnnotatedElementInfo` (utility class) makes it easy to get information about annotations and annotated elements.
4. `MySampleAnnotation` is a sample annotation created for use by classes below.
5. `MySampleAnnotationProcessor` can only handle source files that have the `@MySampleAnnotation` class / type
   annotation. It does not allow other processors to be chained, after it processes a source file w/ its annotation.
6. `Hello` is a simple class that use the `@MySampleAnnotation` annotation from above.

# Code generation

When you run the script, it will generate a `HelloGenerated.java` file in the same folder as `Hello.java` which simply
contains a list of fields that represent the property for which getters were found in the source file.

# Debugging the annotation processors

So, how to debug the annotation processors when they are running on your code? Not necessarily when the AP code itself
is being compiled. Well, you have to:

1. Create a run configuration that attaches to the JVM on that specific port (eg: 8000).

   - The first step involves creating a "Remote" run configuration (named "debug javac (attach:8000)" in this repo). And
     simply set the port to "8000" and keep the other defaults (such as "Attach to remove JVM"). Then copy the flags
     that you see IDEA generate for the command line for the VM.

2. Create a script to run `javac` w/ some options that allow it a debugger to attach to it (on a specific port, eg:
   8000).

   - The second step involves modifying the `run-compiler.fish` and copying it to `run-compiler-debugger.fish` and
     simply changing the last line w/ the following. The string that you copy from the run configuration simply goes
     below after `-J-Xrunjdwp...`.

     ```shell script
     # https://medium.com/@joachim.beckers/debugging-an-annotation-processor-using-intellij-idea-in-2018-cde72758b78a
     # https://stackoverflow.com/a/15938824/2085356
     sh -c "javac -J-Xdebug -J-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=*:8000 -cp . sample/*.java"
     ```

Then to actually to activate the debugger in IDEA:

1. Now, you can run `run-compiler-debugger.fish` and it will pause waiting for a debugger to attach.
2. Then simply debug the `debug javac (attach:8000)` run configuration, and voila, your breakpoints in your AP will come
   to life in IDEA!
