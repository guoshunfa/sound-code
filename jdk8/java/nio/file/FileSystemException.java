package java.nio.file;

import java.io.IOException;

public class FileSystemException extends IOException {
   static final long serialVersionUID = -3055425747967319812L;
   private final String file;
   private final String other;

   public FileSystemException(String var1) {
      super((String)null);
      this.file = var1;
      this.other = null;
   }

   public FileSystemException(String var1, String var2, String var3) {
      super(var3);
      this.file = var1;
      this.other = var2;
   }

   public String getFile() {
      return this.file;
   }

   public String getOtherFile() {
      return this.other;
   }

   public String getReason() {
      return super.getMessage();
   }

   public String getMessage() {
      if (this.file == null && this.other == null) {
         return this.getReason();
      } else {
         StringBuilder var1 = new StringBuilder();
         if (this.file != null) {
            var1.append(this.file);
         }

         if (this.other != null) {
            var1.append(" -> ");
            var1.append(this.other);
         }

         if (this.getReason() != null) {
            var1.append(": ");
            var1.append(this.getReason());
         }

         return var1.toString();
      }
   }
}
