package javax.swing.filechooser;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.UIManager;

class UnixFileSystemView extends FileSystemView {
   private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
   private static final String newFolderNextString = UIManager.getString("FileChooser.other.newFolder.subsequent");

   public File createNewFolder(File var1) throws IOException {
      if (var1 == null) {
         throw new IOException("Containing directory is null:");
      } else {
         File var2 = this.createFileObject(var1, newFolderString);

         for(int var3 = 1; var2.exists() && var3 < 100; ++var3) {
            var2 = this.createFileObject(var1, MessageFormat.format(newFolderNextString, new Integer(var3)));
         }

         if (var2.exists()) {
            throw new IOException("Directory already exists:" + var2.getAbsolutePath());
         } else {
            var2.mkdirs();
            return var2;
         }
      }
   }

   public boolean isFileSystemRoot(File var1) {
      return var1 != null && var1.getAbsolutePath().equals("/");
   }

   public boolean isDrive(File var1) {
      return this.isFloppyDrive(var1);
   }

   public boolean isFloppyDrive(File var1) {
      return false;
   }

   public boolean isComputerNode(File var1) {
      if (var1 != null) {
         String var2 = var1.getParent();
         if (var2 != null && var2.equals("/net")) {
            return true;
         }
      }

      return false;
   }
}
