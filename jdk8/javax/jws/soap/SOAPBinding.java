package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SOAPBinding {
   SOAPBinding.Style style() default SOAPBinding.Style.DOCUMENT;

   SOAPBinding.Use use() default SOAPBinding.Use.LITERAL;

   SOAPBinding.ParameterStyle parameterStyle() default SOAPBinding.ParameterStyle.WRAPPED;

   public static enum ParameterStyle {
      BARE,
      WRAPPED;
   }

   public static enum Use {
      LITERAL,
      ENCODED;
   }

   public static enum Style {
      DOCUMENT,
      RPC;
   }
}
