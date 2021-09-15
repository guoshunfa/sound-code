package java.io;

public class FileWriter extends OutputStreamWriter {
   public FileWriter(String var1) throws IOException {
      super(new FileOutputStream(var1));
   }

   public FileWriter(String var1, boolean var2) throws IOException {
      super(new FileOutputStream(var1, var2));
   }

   public FileWriter(File var1) throws IOException {
      super(new FileOutputStream(var1));
   }

   public FileWriter(File var1, boolean var2) throws IOException {
      super(new FileOutputStream(var1, var2));
   }

   public FileWriter(FileDescriptor var1) {
      super(new FileOutputStream(var1));
   }
}
