package jdk.internal.instrumentation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(TypeMappings.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeMapping {
   String from();

   String to();
}
