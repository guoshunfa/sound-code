package javax.tools;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;

public interface DocumentationTool extends Tool, OptionChecker {
   DocumentationTool.DocumentationTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener<? super JavaFileObject> var3, Class<?> var4, Iterable<String> var5, Iterable<? extends JavaFileObject> var6);

   StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> var1, Locale var2, Charset var3);

   public static enum Location implements JavaFileManager.Location {
      DOCUMENTATION_OUTPUT,
      DOCLET_PATH,
      TAGLET_PATH;

      public String getName() {
         return this.name();
      }

      public boolean isOutputLocation() {
         switch(this) {
         case DOCUMENTATION_OUTPUT:
            return true;
         default:
            return false;
         }
      }
   }

   public interface DocumentationTask extends Callable<Boolean> {
      void setLocale(Locale var1);

      Boolean call();
   }
}
