package javax.swing.filechooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import javax.swing.UIManager;

class WindowsFileSystemView extends FileSystemView {
   private static final String newFolderString = UIManager.getString("FileChooser.win32.newFolder");
   private static final String newFolderNextString = UIManager.getString("FileChooser.win32.newFolder.subsequent");

   public Boolean isTraversable(File var1) {
      return this.isFileSystemRoot(var1) || this.isComputerNode(var1) || var1.isDirectory();
   }

   public File getChild(File var1, String var2) {
      if (var2.startsWith("\\") && !var2.startsWith("\\\\") && this.isFileSystem(var1)) {
         String var3 = var1.getAbsolutePath();
         if (var3.length() >= 2 && var3.charAt(1) == ':' && Character.isLetter(var3.charAt(0))) {
            return this.createFileObject(var3.substring(0, 2) + var2);
         }
      }

      return super.getChild(var1, var2);
   }

   public String getSystemTypeDescription(File var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            return this.getShellFolder(var1).getFolderType();
         } catch (FileNotFoundException var3) {
            return null;
         }
      }
   }

   public File getHomeDirectory() {
      File[] var1 = this.getRoots();
      return var1.length == 0 ? null : var1[0];
   }

   public File createNewFolder(File var1) throws IOException {
      if (var1 == null) {
         throw new IOException("Containing directory is null:");
      } else {
         File var2 = this.createFileObject(var1, newFolderString);

         for(int var3 = 2; var2.exists() && var3 < 100; ++var3) {
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

   public boolean isDrive(File var1) {
      return this.isFileSystemRoot(var1);
   }

   public boolean isFloppyDrive(final File var1) {
      String var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return var1.getAbsolutePath();
         }
      });
      return var2 != null && (var2.equals("A:\\") || var2.equals("B:\\"));
   }

   public File createFileObject(String var1) {
      if (var1.length() >= 2 && var1.charAt(1) == ':' && Character.isLetter(var1.charAt(0))) {
         if (var1.length() == 2) {
            var1 = var1 + "\\";
         } else if (var1.charAt(2) != '\\') {
            var1 = var1.substring(0, 2) + "\\" + var1.substring(2);
         }
      }

      return super.createFileObject(var1);
   }

   protected File createFileSystemRoot(File var1) {
      return new FileSystemView.FileSystemRoot(var1) {
         public boolean exists() {
            return true;
         }
      };
   }
}
