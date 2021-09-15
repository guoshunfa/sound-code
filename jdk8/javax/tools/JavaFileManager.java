package javax.tools;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public interface JavaFileManager extends Closeable, Flushable, OptionChecker {
   ClassLoader getClassLoader(JavaFileManager.Location var1);

   Iterable<JavaFileObject> list(JavaFileManager.Location var1, String var2, Set<JavaFileObject.Kind> var3, boolean var4) throws IOException;

   String inferBinaryName(JavaFileManager.Location var1, JavaFileObject var2);

   boolean isSameFile(FileObject var1, FileObject var2);

   boolean handleOption(String var1, Iterator<String> var2);

   boolean hasLocation(JavaFileManager.Location var1);

   JavaFileObject getJavaFileForInput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3) throws IOException;

   JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException;

   FileObject getFileForInput(JavaFileManager.Location var1, String var2, String var3) throws IOException;

   FileObject getFileForOutput(JavaFileManager.Location var1, String var2, String var3, FileObject var4) throws IOException;

   void flush() throws IOException;

   void close() throws IOException;

   public interface Location {
      String getName();

      boolean isOutputLocation();
   }
}
