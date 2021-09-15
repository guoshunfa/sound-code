package jdk.internal.instrumentation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TypeMappings {
   TypeMapping[] value();
}
