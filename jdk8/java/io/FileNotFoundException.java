package java.io;

public class FileNotFoundException extends IOException {
   private static final long serialVersionUID = -897856973823710492L;

   public FileNotFoundException() {
   }

   public FileNotFoundException(String var1) {
      super(var1);
   }

   private FileNotFoundException(String var1, String var2) {
      super(var1 + (var2 == null ? "" : " (" + var2 + ")"));
   }
}
