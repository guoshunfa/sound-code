package java.io;

public class FileReader extends InputStreamReader {
   public FileReader(String var1) throws FileNotFoundException {
      super(new FileInputStream(var1));
   }

   public FileReader(File var1) throws FileNotFoundException {
      super(new FileInputStream(var1));
   }

   public FileReader(FileDescriptor var1) {
      super(new FileInputStream(var1));
   }
}
