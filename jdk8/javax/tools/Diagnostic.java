package javax.tools;

import java.util.Locale;

public interface Diagnostic<S> {
   long NOPOS = -1L;

   Diagnostic.Kind getKind();

   S getSource();

   long getPosition();

   long getStartPosition();

   long getEndPosition();

   long getLineNumber();

   long getColumnNumber();

   String getCode();

   String getMessage(Locale var1);

   public static enum Kind {
      ERROR,
      WARNING,
      MANDATORY_WARNING,
      NOTE,
      OTHER;
   }
}
