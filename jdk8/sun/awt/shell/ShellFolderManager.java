package sun.awt.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

class ShellFolderManager {
   public ShellFolder createShellFolder(File var1) throws FileNotFoundException {
      return new DefaultShellFolder((ShellFolder)null, var1);
   }

   public Object get(String var1) {
      if (var1.equals("fileChooserDefaultFolder")) {
         File var2 = new File(System.getProperty("user.home"));

         try {
            return this.createShellFolder(var2);
         } catch (FileNotFoundException var4) {
            return var2;
         }
      } else if (var1.equals("roots")) {
         return File.listRoots();
      } else if (var1.equals("fileChooserComboBoxFolders")) {
         return this.get("roots");
      } else {
         return var1.equals("fileChooserShortcutPanelFolders") ? new File[]{(File)this.get("fileChooserDefaultFolder")} : null;
      }
   }

   public boolean isComputerNode(File var1) {
      return false;
   }

   public boolean isFileSystemRoot(File var1) {
      if (var1 instanceof ShellFolder && !((ShellFolder)var1).isFileSystem()) {
         return false;
      } else {
         return var1.getParentFile() == null;
      }
   }

   protected ShellFolder.Invoker createInvoker() {
      return new ShellFolderManager.DirectInvoker();
   }

   private static class DirectInvoker implements ShellFolder.Invoker {
      private DirectInvoker() {
      }

      public <T> T invoke(Callable<T> var1) throws Exception {
         return var1.call();
      }

      // $FF: synthetic method
      DirectInvoker(Object var1) {
         this();
      }
   }
}
