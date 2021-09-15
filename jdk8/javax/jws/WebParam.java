package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface WebParam {
   String name() default "";

   String partName() default "";

   String targetNamespace() default "";

   WebParam.Mode mode() default WebParam.Mode.IN;

   boolean header() default false;

   public static enum Mode {
      IN,
      OUT,
      INOUT;
   }
}
