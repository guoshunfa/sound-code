package javax.swing.filechooser;

import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;

class GenericFileSystemView extends FileSystemView {
   private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");

   public File createNewFolder(File var1) throws IOException {
      if (var1 == null) {
         throw new IOException("Containing directory is null:");
      } else {
         File var2 = this.createFileObject(var1, newFolderString);
         if (var2.exists()) {
            throw new IOException("Directory already exists:" + var2.getAbsolutePath());
         } else {
            var2.mkdirs();
            return var2;
         }
      }
   }
}
