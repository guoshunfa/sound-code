package sun.awt.shell;

import java.io.File;
import java.io.ObjectStreamException;

class DefaultShellFolder extends ShellFolder {
   DefaultShellFolder(ShellFolder var1, File var2) {
      super(var1, var2.getAbsolutePath());
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new File(this.getPath());
   }

   public File[] listFiles() {
      File[] var1 = super.listFiles();
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = new DefaultShellFolder(this, var1[var2]);
         }
      }

      return var1;
   }

   public boolean isLink() {
      return false;
   }

   public boolean isHidden() {
      String var1 = this.getName();
      if (var1.length() > 0) {
         return var1.charAt(0) == '.';
      } else {
         return false;
      }
   }

   public ShellFolder getLinkLocation() {
      return null;
   }

   public String getDisplayName() {
      return this.getName();
   }

   public String getFolderType() {
      return this.isDirectory() ? "File Folder" : "File";
   }

   public String getExecutableType() {
      return null;
   }
}
