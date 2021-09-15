package java.nio.file;

public class FileSystemAlreadyExistsException extends RuntimeException {
   static final long serialVersionUID = -5438419127181131148L;

   public FileSystemAlreadyExistsException() {
   }

   public FileSystemAlreadyExistsException(String var1) {
      super(var1);
   }
}
