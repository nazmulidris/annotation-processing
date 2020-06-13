package sample.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Once registered, this processor catches every single annotation that's available in your source code, and simply
 * displays some debug information about the sources it finds during the compilation process. It does not stop any
 * annotations from processing since it returns {@code false} from {@link #process(Set, RoundEnvironment)}.
 */
@SupportedAnnotationTypes("*") // "*" matches every annotation that is found (of any type).
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class DebugAnnotationProcessor extends AbstractProcessor {

@Override
public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
  if (annotations.isEmpty()) {
    return false; // Continue processing.
  }
  StringBuilder sb = new StringBuilder();
  sb.append(String.format("🤖 %s 🤖\n",
                          this.getClass().getCanonicalName()));
  sb.append("Annotations found:\n");
  annotations.forEach(typeElement -> sb.append("\t- annotation: " + typeElement + "\n"));
  annotations.forEach(typeElement -> processEachAnnotation(roundEnv, typeElement, sb));
  consoleLog(sb.toString());
  return false; // Continue processing.
}

/**
 * Since, "*" is used for {@link SupportedAnnotationTypes} this will match all annotations in the available source code.
 * You can compare the annotation's {@link TypeElement#getQualifiedName()} to your annotation processor class's {@link
 * Class#getCanonicalName()} to see if there's a match to a specific processor.
 */
private void processEachAnnotation(RoundEnvironment roundEnv,
                                   TypeElement annotation,
                                   StringBuilder sb) {
  String annotationClassName = annotation.getQualifiedName().toString();
  sb.append(String.format("Elements that are annotated with the annotation '%s' found\n",
                          annotationClassName));
  roundEnv.getElementsAnnotatedWith(annotation)
          .forEach(element -> processEachAnnotatedElement(annotation, element, sb));
}

private void processEachAnnotatedElement(TypeElement annotation,
                                         Element element,
                                         StringBuilder sb) {
  AnnotatedElementInfo info = AnnotatedElementInfo.getInstance(processingEnv, annotation, element);
  sb.append(info.toString());
}

private void consoleLog(String message) {
  processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "🚀\n" + message);
}

}
