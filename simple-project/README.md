# Simple annotation processing in Java (using javac)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Introduction](#introduction)
- [Code structure](#code-structure)
- [Code generation](#code-generation)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Introduction

An example on how to use Java Annotation Processors with plain **javac**.

1. To test the example, you will need JDK 11 installed on your machine.
2. Run the `run-compiler.fish` file and see the console output when the compiler runs.

# Code structure

1. `javax.annotation.processing.Processor` file contains all the registered processors.
2. `DebugAnnotationProcessor` can handle any source files that have annotations.
3. `AnnotatedElementInfo` is a utility class that makes it easy to get information about annotations and annotated
   elements.
4. `MySampleAnnotationProcessor` can only handle source files that have the `@MySampleAnnotation` class / type
   annotation.

# Code generation

When you run the script, it will generate a `HelloGenerated.java` file in the same folder as `Hello.java` which simply
contains a list of fields that represent the property for which getters were found in the source file.
