package javax.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface XmlElement {
   String name() default "##default";

   boolean nillable() default false;

   boolean required() default false;

   String namespace() default "##default";

   String defaultValue() default "\u0000";

   Class type() default XmlElement.DEFAULT.class;

   public static final class DEFAULT {
   }
}
