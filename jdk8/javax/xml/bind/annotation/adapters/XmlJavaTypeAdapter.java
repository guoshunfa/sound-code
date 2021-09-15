package javax.xml.bind.annotation.adapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface XmlJavaTypeAdapter {
   Class<? extends XmlAdapter> value();

   Class type() default XmlJavaTypeAdapter.DEFAULT.class;

   public static final class DEFAULT {
   }
}
