package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WebResult {
   String name() default "";

   String partName() default "";

   String targetNamespace() default "";

   boolean header() default false;
}
