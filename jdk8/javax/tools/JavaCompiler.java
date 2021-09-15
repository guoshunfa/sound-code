package javax.tools;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.annotation.processing.Processor;

public interface JavaCompiler extends Tool, OptionChecker {
   JavaCompiler.CompilationTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener<? super JavaFileObject> var3, Iterable<String> var4, Iterable<String> var5, Iterable<? extends JavaFileObject> var6);

   StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> var1, Locale var2, Charset var3);

   public interface CompilationTask extends Callable<Boolean> {
      void setProcessors(Iterable<? extends Processor> var1);

      void setLocale(Locale var1);

      Boolean call();
   }
}
