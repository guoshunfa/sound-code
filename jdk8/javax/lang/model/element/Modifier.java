package javax.lang.model.element;

import java.util.Locale;

public enum Modifier {
   PUBLIC,
   PROTECTED,
   PRIVATE,
   ABSTRACT,
   DEFAULT,
   STATIC,
   FINAL,
   TRANSIENT,
   VOLATILE,
   SYNCHRONIZED,
   NATIVE,
   STRICTFP;

   public String toString() {
      return this.name().toLowerCase(Locale.US);
   }
}
