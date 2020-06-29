package sample.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/** This annotation works at the Java class aka {@link ElementType#TYPE} level. */
@Target({ElementType.TYPE})
public @interface MySampleAnnotation {
}