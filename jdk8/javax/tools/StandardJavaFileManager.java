package javax.tools;

import java.io.File;
import java.io.IOException;

public interface StandardJavaFileManager extends JavaFileManager {
   boolean isSameFile(FileObject var1, FileObject var2);

   Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> var1);

   Iterable<? extends JavaFileObject> getJavaFileObjects(File... var1);

   Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> var1);

   Iterable<? extends JavaFileObject> getJavaFileObjects(String... var1);

   void setLocation(JavaFileManager.Location var1, Iterable<? extends File> var2) throws IOException;

   Iterable<? extends File> getLocation(JavaFileManager.Location var1);
}
