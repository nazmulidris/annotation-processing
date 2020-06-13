package sample.annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Once registered, this processor generates a file for every class that has the annotation {@link MySampleAnnotation}.
 * For example if {@code Hello.java} has this annotation, then {@code HelloGenerated.java} will be written once this
 * class is processed by this processor.
 */
@SupportedAnnotationTypes("sample.annotation.MySampleAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class MySampleAnnotationProcessor extends AbstractProcessor {

@Override
public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
  if (annotations.isEmpty()) {
    return false; // Continue processing.
  }
  StringBuilder log = new StringBuilder();
  StringBuilder err = new StringBuilder();
  log.append(String.format("🤖 %s 🤖\n",
                           this.getClass().getCanonicalName()));
  annotations.forEach(annotation -> roundEnv.getElementsAnnotatedWith(annotation)
                                            .forEach(element -> processEachAnnotatedElement(annotation,
                                                                                            element,
                                                                                            log,
                                                                                            err)));
  if (!log.toString().isEmpty()) {
    consoleLog(log.toString());
  }
  if (!err.toString().isEmpty()) {
    consoleErr(err.toString());
  }
  return true; // Stop processing.
}

private void processEachAnnotatedElement(TypeElement annotation,
                                         Element element,
                                         StringBuilder log,
                                         StringBuilder err) {
  log.append("🎉 found @MySampleAnnotation at " + element + "\n");
  try {
    AnnotatedElementInfo info = AnnotatedElementInfo.getInstance(processingEnv, annotation, element);
    Filer filer = processingEnv.getFiler();
    String targetClassName = String.format("%sGenerated", info.annotatedClassName);
    String targetClassNameWithPackage = String.format("%s.%s",
                                                      info.annotatedPackageName,
                                                      targetClassName);
    log.append(String.format("Generating: targetClassName: '%s', targetClassNameWithPackage: '%s'\n",
                             targetClassName,
                             targetClassNameWithPackage));
    deletePreexistingFile(log, info, filer, targetClassName);
    JavaFileObject fileObject = filer.createSourceFile(targetClassNameWithPackage, element);
    String sourceFile = generateSourceFile(info, targetClassName);
    log.append(sourceFile);
    try (Writer writer = fileObject.openWriter()) {
      writer.write(sourceFile);
    }
  } catch (Exception e) {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    err.append(sw.toString());
  }
}

private void deletePreexistingFile(StringBuilder log, AnnotatedElementInfo info, Filer filer, String targetClassName)
    throws IOException {
  FileObject sourceFileObject = filer.getResource(StandardLocation.SOURCE_OUTPUT,
                                                  info.annotatedPackageName,
                                                  targetClassName + JavaFileObject.Kind.SOURCE.extension);
  File existingSourceFile = new File(sourceFileObject.getName());
  log.append(String.format("sourceFileObject: %s, fileExists: %s\n",
                           sourceFileObject.getName(),
                           existingSourceFile.exists()));
  if (existingSourceFile.exists()) existingSourceFile.delete();
}

private String generateSourceFile(AnnotatedElementInfo info, String targetClassName) {
  StringBuilder sourceFileBuilder = new StringBuilder();
  sourceFileBuilder
      .append(String.format("package %s;\n", info.annotatedPackageName))
      .append(String.format("public final class %s {\n", targetClassName))
      .append(generateFields(info))
      .append(String.format("}\n"));
  return sourceFileBuilder.toString();
}

private String generateFields(AnnotatedElementInfo info) {
  StringBuilder source = new StringBuilder();
  List<String> getterList = info.annotatedElementGetters;
  for (String getter : getterList) {
    String fieldName = getter.substring(3);
    source.append(String.format("  public static int %s;\n", fieldName));
  }
  return source.toString();
}

private void consoleLog(String message) {
  processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "🚀\n" + message);
}

private void consoleErr(String message) {
  processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "🔥\n" + message);
}

}
