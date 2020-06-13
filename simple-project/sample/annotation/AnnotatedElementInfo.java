package sample.annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotatedElementInfo {

public ProcessingEnvironment processingEnv;
public TypeElement           annotation;
public Element               element;
public ElementKind           annotatedElementKind;
public Name                  annotatedClassName;
public String                annotatedPackageName;
public List<String>          annotatedElementGetters;

private AnnotatedElementInfo() {
}

public static AnnotatedElementInfo getInstance(ProcessingEnvironment processingEnv,
                                               TypeElement annotation,
                                               Element element) {
  AnnotatedElementInfo info = new AnnotatedElementInfo();
  info.processingEnv = processingEnv;
  info.annotation = annotation;
  info.element = element;
  return info.process();
}

private AnnotatedElementInfo process() {
  this.annotatedElementKind = element.getKind();
  this.annotatedClassName = element.getSimpleName();
  this.annotatedPackageName = ((PackageElement) (element.getEnclosingElement())).getQualifiedName().toString();
  // Find all member elements.
  // Convert the list into a stream.
  // Remove all elements that are not of the method type.
  // Extract the name of the element.
  // Remove all names not starting with “get”.
  // Create another list from the stream.
  this.annotatedElementGetters = processingEnv.getElementUtils()
                                              .getAllMembers((TypeElement) element)
                                              .stream()
                                              .filter(_element -> _element.getKind() == ElementKind.METHOD)
                                              .map(_element -> _element.getSimpleName().toString())
                                              .filter(_name -> _name.startsWith("get"))
                                              .collect(Collectors.toList());

  return this;
}

@Override public String toString() {
  StringBuilder builder = new StringBuilder();
  builder
      .append(
          String.format("\t- annotated element: %s='%s', %s='%s', %s='%s'\n",
                        "annotatedClassName", this.annotatedClassName,
                        "annotatedPackageName", this.annotatedPackageName,
                        "annotatedElementKind", this.annotatedElementKind))
      .append(
          String.format("\t- getter methods: %s",
                        annotatedElementGetters.toString()));
  return builder.toString();
}
}
