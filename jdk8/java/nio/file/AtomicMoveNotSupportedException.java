package java.nio.file;

public class AtomicMoveNotSupportedException extends FileSystemException {
   static final long serialVersionUID = 5402760225333135579L;

   public AtomicMoveNotSupportedException(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }
}
