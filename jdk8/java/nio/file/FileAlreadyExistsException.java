package java.nio.file;

public class FileAlreadyExistsException extends FileSystemException {
   static final long serialVersionUID = 7579540934498831181L;

   public FileAlreadyExistsException(String var1) {
      super(var1);
   }

   public FileAlreadyExistsException(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }
}
