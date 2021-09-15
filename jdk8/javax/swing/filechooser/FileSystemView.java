package javax.swing.filechooser;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import sun.awt.shell.ShellFolder;

public abstract class FileSystemView {
   static FileSystemView windowsFileSystemView = null;
   static FileSystemView unixFileSystemView = null;
   static FileSystemView genericFileSystemView = null;
   private boolean useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");

   public static FileSystemView getFileSystemView() {
      if (File.separatorChar == '\\') {
         if (windowsFileSystemView == null) {
            windowsFileSystemView = new WindowsFileSystemView();
         }

         return windowsFileSystemView;
      } else if (File.separatorChar == '/') {
         if (unixFileSystemView == null) {
            unixFileSystemView = new UnixFileSystemView();
         }

         return unixFileSystemView;
      } else {
         if (genericFileSystemView == null) {
            genericFileSystemView = new GenericFileSystemView();
         }

         return genericFileSystemView;
      }
   }

   public FileSystemView() {
      final WeakReference var1 = new WeakReference(this);
      UIManager.addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1x) {
            FileSystemView var2 = (FileSystemView)var1.get();
            if (var2 == null) {
               UIManager.removePropertyChangeListener(this);
            } else if (var1x.getPropertyName().equals("lookAndFeel")) {
               var2.useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
            }

         }
      });
   }

   public boolean isRoot(File var1) {
      if (var1 != null && var1.isAbsolute()) {
         File[] var2 = this.getRoots();
         File[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            if (var6.equals(var1)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public Boolean isTraversable(File var1) {
      return var1.isDirectory();
   }

   public String getSystemDisplayName(File var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.getName();
         if (!var2.equals("..") && !var2.equals(".") && (this.useSystemExtensionHiding || !this.isFileSystem(var1) || this.isFileSystemRoot(var1)) && (var1 instanceof ShellFolder || var1.exists())) {
            try {
               var2 = this.getShellFolder(var1).getDisplayName();
            } catch (FileNotFoundException var4) {
               return null;
            }

            if (var2 == null || var2.length() == 0) {
               var2 = var1.getPath();
            }
         }

         return var2;
      }
   }

   public String getSystemTypeDescription(File var1) {
      return null;
   }

   public Icon getSystemIcon(File var1) {
      if (var1 == null) {
         return null;
      } else {
         ShellFolder var2;
         try {
            var2 = this.getShellFolder(var1);
         } catch (FileNotFoundException var4) {
            return null;
         }

         Image var3 = var2.getIcon(false);
         return (Icon)(var3 != null ? new ImageIcon(var3, var2.getFolderType()) : UIManager.getIcon(var1.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon"));
      }
   }

   public boolean isParent(File var1, File var2) {
      if (var1 != null && var2 != null) {
         if (var1 instanceof ShellFolder) {
            File var3 = var2.getParentFile();
            if (var3 != null && var3.equals(var1)) {
               return true;
            } else {
               File[] var4 = this.getFiles(var1, false);
               File[] var5 = var4;
               int var6 = var4.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  File var8 = var5[var7];
                  if (var2.equals(var8)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return var1.equals(var2.getParentFile());
         }
      } else {
         return false;
      }
   }

   public File getChild(File var1, String var2) {
      if (var1 instanceof ShellFolder) {
         File[] var3 = this.getFiles(var1, false);
         File[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File var7 = var4[var6];
            if (var7.getName().equals(var2)) {
               return var7;
            }
         }
      }

      return this.createFileObject(var1, var2);
   }

   public boolean isFileSystem(File var1) {
      if (!(var1 instanceof ShellFolder)) {
         return true;
      } else {
         ShellFolder var2 = (ShellFolder)var1;
         return var2.isFileSystem() && (!var2.isLink() || !var2.isDirectory());
      }
   }

   public abstract File createNewFolder(File var1) throws IOException;

   public boolean isHiddenFile(File var1) {
      return var1.isHidden();
   }

   public boolean isFileSystemRoot(File var1) {
      return ShellFolder.isFileSystemRoot(var1);
   }

   public boolean isDrive(File var1) {
      return false;
   }

   public boolean isFloppyDrive(File var1) {
      return false;
   }

   public boolean isComputerNode(File var1) {
      return ShellFolder.isComputerNode(var1);
   }

   public File[] getRoots() {
      File[] var1 = (File[])((File[])ShellFolder.get("roots"));

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (this.isFileSystemRoot(var1[var2])) {
            var1[var2] = this.createFileSystemRoot(var1[var2]);
         }
      }

      return var1;
   }

   public File getHomeDirectory() {
      return this.createFileObject(System.getProperty("user.home"));
   }

   public File getDefaultDirectory() {
      File var1 = (File)ShellFolder.get("fileChooserDefaultFolder");
      if (this.isFileSystemRoot(var1)) {
         var1 = this.createFileSystemRoot(var1);
      }

      return var1;
   }

   public File createFileObject(File var1, String var2) {
      return var1 == null ? new File(var2) : new File(var1, var2);
   }

   public File createFileObject(String var1) {
      File var2 = new File(var1);
      if (this.isFileSystemRoot(var2)) {
         var2 = this.createFileSystemRoot(var2);
      }

      return var2;
   }

   public File[] getFiles(File var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      if (!(var1 instanceof ShellFolder)) {
         try {
            var1 = this.getShellFolder((File)var1);
         } catch (FileNotFoundException var10) {
            return new File[0];
         }
      }

      File[] var4 = ((ShellFolder)var1).listFiles(!var2);
      if (var4 == null) {
         return new File[0];
      } else {
         File[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Object var8 = var5[var7];
            if (Thread.currentThread().isInterrupted()) {
               break;
            }

            if (!(var8 instanceof ShellFolder)) {
               if (this.isFileSystemRoot((File)var8)) {
                  var8 = this.createFileSystemRoot((File)var8);
               }

               try {
                  var8 = ShellFolder.getShellFolder((File)var8);
               } catch (FileNotFoundException var11) {
                  continue;
               } catch (InternalError var12) {
                  continue;
               }
            }

            if (!var2 || !this.isHiddenFile((File)var8)) {
               var3.add(var8);
            }
         }

         return (File[])var3.toArray(new File[var3.size()]);
      }
   }

   public File getParentDirectory(File var1) {
      if (var1 != null && var1.exists()) {
         ShellFolder var2;
         try {
            var2 = this.getShellFolder(var1);
         } catch (FileNotFoundException var6) {
            return null;
         }

         File var3 = var2.getParentFile();
         if (var3 == null) {
            return null;
         } else if (!this.isFileSystem(var3)) {
            return var3;
         } else {
            File var4 = var3;
            if (!var3.exists()) {
               File var5 = var3.getParentFile();
               if (var5 == null || !this.isFileSystem(var5)) {
                  var4 = this.createFileSystemRoot(var3);
               }
            }

            return var4;
         }
      } else {
         return null;
      }
   }

   ShellFolder getShellFolder(File var1) throws FileNotFoundException {
      if (!(var1 instanceof ShellFolder) && !(var1 instanceof FileSystemView.FileSystemRoot) && this.isFileSystemRoot(var1)) {
         var1 = this.createFileSystemRoot(var1);
      }

      try {
         return ShellFolder.getShellFolder(var1);
      } catch (InternalError var3) {
         System.err.println("FileSystemView.getShellFolder: f=" + var1);
         var3.printStackTrace();
         return null;
      }
   }

   protected File createFileSystemRoot(File var1) {
      return new FileSystemView.FileSystemRoot(var1);
   }

   static class FileSystemRoot extends File {
      public FileSystemRoot(File var1) {
         super(var1, "");
      }

      public FileSystemRoot(String var1) {
         super(var1);
      }

      public boolean isDirectory() {
         return true;
      }

      public String getName() {
         return this.getPath();
      }
   }
}
