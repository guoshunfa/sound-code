package sun.nio.fs;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;

class UnixException extends Exception {
   static final long serialVersionUID = 7227016794320723218L;
   private int errno;
   private String msg;

   UnixException(int var1) {
      this.errno = var1;
      this.msg = null;
   }

   UnixException(String var1) {
      this.errno = 0;
      this.msg = var1;
   }

   int errno() {
      return this.errno;
   }

   void setError(int var1) {
      this.errno = var1;
      this.msg = null;
   }

   String errorString() {
      return this.msg != null ? this.msg : Util.toString(UnixNativeDispatcher.strerror(this.errno()));
   }

   public String getMessage() {
      return this.errorString();
   }

   private IOException translateToIOException(String var1, String var2) {
      if (this.msg != null) {
         return new IOException(this.msg);
      } else if (this.errno() == 13) {
         return new AccessDeniedException(var1, var2, (String)null);
      } else if (this.errno() == 2) {
         return new NoSuchFileException(var1, var2, (String)null);
      } else {
         return (IOException)(this.errno() == 17 ? new FileAlreadyExistsException(var1, var2, (String)null) : new FileSystemException(var1, var2, this.errorString()));
      }
   }

   void rethrowAsIOException(String var1) throws IOException {
      IOException var2 = this.translateToIOException(var1, (String)null);
      throw var2;
   }

   void rethrowAsIOException(UnixPath var1, UnixPath var2) throws IOException {
      String var3 = var1 == null ? null : var1.getPathForExceptionMessage();
      String var4 = var2 == null ? null : var2.getPathForExceptionMessage();
      IOException var5 = this.translateToIOException(var3, var4);
      throw var5;
   }

   void rethrowAsIOException(UnixPath var1) throws IOException {
      this.rethrowAsIOException(var1, (UnixPath)null);
   }

   IOException asIOException(UnixPath var1) {
      return this.translateToIOException(var1.getPathForExceptionMessage(), (String)null);
   }
}
