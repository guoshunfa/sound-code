package javax.tools;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ForwardingJavaFileManager<M extends JavaFileManager> implements JavaFileManager {
   protected final M fileManager;

   protected ForwardingJavaFileManager(M var1) {
      var1.getClass();
      this.fileManager = var1;
   }

   public ClassLoader getClassLoader(JavaFileManager.Location var1) {
      return this.fileManager.getClassLoader(var1);
   }

   public Iterable<JavaFileObject> list(JavaFileManager.Location var1, String var2, Set<JavaFileObject.Kind> var3, boolean var4) throws IOException {
      return this.fileManager.list(var1, var2, var3, var4);
   }

   public String inferBinaryName(JavaFileManager.Location var1, JavaFileObject var2) {
      return this.fileManager.inferBinaryName(var1, var2);
   }

   public boolean isSameFile(FileObject var1, FileObject var2) {
      return this.fileManager.isSameFile(var1, var2);
   }

   public boolean handleOption(String var1, Iterator<String> var2) {
      return this.fileManager.handleOption(var1, var2);
   }

   public boolean hasLocation(JavaFileManager.Location var1) {
      return this.fileManager.hasLocation(var1);
   }

   public int isSupportedOption(String var1) {
      return this.fileManager.isSupportedOption(var1);
   }

   public JavaFileObject getJavaFileForInput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3) throws IOException {
      return this.fileManager.getJavaFileForInput(var1, var2, var3);
   }

   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location var1, String var2, JavaFileObject.Kind var3, FileObject var4) throws IOException {
      return this.fileManager.getJavaFileForOutput(var1, var2, var3, var4);
   }

   public FileObject getFileForInput(JavaFileManager.Location var1, String var2, String var3) throws IOException {
      return this.fileManager.getFileForInput(var1, var2, var3);
   }

   public FileObject getFileForOutput(JavaFileManager.Location var1, String var2, String var3, FileObject var4) throws IOException {
      return this.fileManager.getFileForOutput(var1, var2, var3, var4);
   }

   public void flush() throws IOException {
      this.fileManager.flush();
   }

   public void close() throws IOException {
      this.fileManager.close();
   }
}
