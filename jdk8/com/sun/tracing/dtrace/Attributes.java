package com.sun.tracing.dtrace;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Attributes {
   StabilityLevel name() default StabilityLevel.PRIVATE;

   StabilityLevel data() default StabilityLevel.PRIVATE;

   DependencyClass dependency() default DependencyClass.UNKNOWN;
}
