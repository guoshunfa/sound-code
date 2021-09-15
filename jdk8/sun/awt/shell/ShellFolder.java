package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import javax.swing.SortOrder;

public abstract class ShellFolder extends File {
   private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
   private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
   private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";
   protected ShellFolder parent;
   private static final ShellFolderManager shellFolderManager;
   private static final ShellFolder.Invoker invoker;
   private static final Comparator DEFAULT_COMPARATOR;
   private static final Comparator<File> FILE_COMPARATOR;

   ShellFolder(ShellFolder var1, String var2) {
      super(var2 != null ? var2 : "ShellFolder");
      this.parent = var1;
   }

   public boolean isFileSystem() {
      return !this.getPath().startsWith("ShellFolder");
   }

   protected abstract Object writeReplace() throws ObjectStreamException;

   public String getParent() {
      if (this.parent == null && this.isFileSystem()) {
         return super.getParent();
      } else {
         return this.parent != null ? this.parent.getPath() : null;
      }
   }

   public File getParentFile() {
      if (this.parent != null) {
         return this.parent;
      } else {
         return this.isFileSystem() ? super.getParentFile() : null;
      }
   }

   public File[] listFiles() {
      return this.listFiles(true);
   }

   public File[] listFiles(boolean var1) {
      File[] var2 = super.listFiles();
      if (!var1) {
         Vector var3 = new Vector();
         int var4 = var2 == null ? 0 : var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            if (!var2[var5].isHidden()) {
               var3.addElement(var2[var5]);
            }
         }

         var2 = (File[])((File[])var3.toArray(new File[var3.size()]));
      }

      return var2;
   }

   public abstract boolean isLink();

   public abstract ShellFolder getLinkLocation() throws FileNotFoundException;

   public abstract String getDisplayName();

   public abstract String getFolderType();

   public abstract String getExecutableType();

   public int compareTo(File var1) {
      if (var1 != null && var1 instanceof ShellFolder && (!(var1 instanceof ShellFolder) || !((ShellFolder)var1).isFileSystem())) {
         return this.isFileSystem() ? 1 : this.getName().compareTo(var1.getName());
      } else {
         return this.isFileSystem() ? super.compareTo(var1) : -1;
      }
   }

   public Image getIcon(boolean var1) {
      return null;
   }

   public static ShellFolder getShellFolder(File var0) throws FileNotFoundException {
      if (var0 instanceof ShellFolder) {
         return (ShellFolder)var0;
      } else if (!var0.exists()) {
         throw new FileNotFoundException();
      } else {
         return shellFolderManager.createShellFolder(var0);
      }
   }

   public static Object get(String var0) {
      return shellFolderManager.get(var0);
   }

   public static boolean isComputerNode(File var0) {
      return shellFolderManager.isComputerNode(var0);
   }

   public static boolean isFileSystemRoot(File var0) {
      return shellFolderManager.isFileSystemRoot(var0);
   }

   public static File getNormalizedFile(File var0) throws IOException {
      File var1 = var0.getCanonicalFile();
      return var0.equals(var1) ? var1 : new File(var0.toURI().normalize());
   }

   public static void sort(final List<? extends File> var0) {
      if (var0 != null && var0.size() > 1) {
         invoke(new Callable<Void>() {
            public Void call() {
               File var1 = null;
               Iterator var2 = var0.iterator();

               while(var2.hasNext()) {
                  File var3 = (File)var2.next();
                  File var4 = var3.getParentFile();
                  if (var4 != null && var3 instanceof ShellFolder) {
                     if (var1 == null) {
                        var1 = var4;
                        continue;
                     }

                     if (var1 == var4 || var1.equals(var4)) {
                        continue;
                     }

                     var1 = null;
                     break;
                  }

                  var1 = null;
                  break;
               }

               if (var1 instanceof ShellFolder) {
                  ((ShellFolder)var1).sortChildren(var0);
               } else {
                  Collections.sort(var0, ShellFolder.FILE_COMPARATOR);
               }

               return null;
            }
         });
      }
   }

   public void sortChildren(final List<? extends File> var1) {
      invoke(new Callable<Void>() {
         public Void call() {
            Collections.sort(var1, ShellFolder.FILE_COMPARATOR);
            return null;
         }
      });
   }

   public boolean isAbsolute() {
      return !this.isFileSystem() || super.isAbsolute();
   }

   public File getAbsoluteFile() {
      return (File)(this.isFileSystem() ? super.getAbsoluteFile() : this);
   }

   public boolean canRead() {
      return this.isFileSystem() ? super.canRead() : true;
   }

   public boolean canWrite() {
      return this.isFileSystem() ? super.canWrite() : false;
   }

   public boolean exists() {
      return !this.isFileSystem() || isFileSystemRoot(this) || super.exists();
   }

   public boolean isDirectory() {
      return this.isFileSystem() ? super.isDirectory() : true;
   }

   public boolean isFile() {
      return this.isFileSystem() ? super.isFile() : !this.isDirectory();
   }

   public long lastModified() {
      return this.isFileSystem() ? super.lastModified() : 0L;
   }

   public long length() {
      return this.isFileSystem() ? super.length() : 0L;
   }

   public boolean createNewFile() throws IOException {
      return this.isFileSystem() ? super.createNewFile() : false;
   }

   public boolean delete() {
      return this.isFileSystem() ? super.delete() : false;
   }

   public void deleteOnExit() {
      if (this.isFileSystem()) {
         super.deleteOnExit();
      }

   }

   public boolean mkdir() {
      return this.isFileSystem() ? super.mkdir() : false;
   }

   public boolean mkdirs() {
      return this.isFileSystem() ? super.mkdirs() : false;
   }

   public boolean renameTo(File var1) {
      return this.isFileSystem() ? super.renameTo(var1) : false;
   }

   public boolean setLastModified(long var1) {
      return this.isFileSystem() ? super.setLastModified(var1) : false;
   }

   public boolean setReadOnly() {
      return this.isFileSystem() ? super.setReadOnly() : false;
   }

   public String toString() {
      return this.isFileSystem() ? super.toString() : this.getDisplayName();
   }

   public static ShellFolderColumnInfo[] getFolderColumns(File var0) {
      ShellFolderColumnInfo[] var1 = null;
      if (var0 instanceof ShellFolder) {
         var1 = ((ShellFolder)var0).getFolderColumns();
      }

      if (var1 == null) {
         var1 = new ShellFolderColumnInfo[]{new ShellFolderColumnInfo("FileChooser.fileNameHeaderText", 150, 10, true, (SortOrder)null, FILE_COMPARATOR), new ShellFolderColumnInfo("FileChooser.fileSizeHeaderText", 75, 4, true, (SortOrder)null, DEFAULT_COMPARATOR, true), new ShellFolderColumnInfo("FileChooser.fileDateHeaderText", 130, 10, true, (SortOrder)null, DEFAULT_COMPARATOR, true)};
      }

      return var1;
   }

   public ShellFolderColumnInfo[] getFolderColumns() {
      return null;
   }

   public static Object getFolderColumnValue(File var0, int var1) {
      if (var0 instanceof ShellFolder) {
         Object var2 = ((ShellFolder)var0).getFolderColumnValue(var1);
         if (var2 != null) {
            return var2;
         }
      }

      if (var0 != null && var0.exists()) {
         switch(var1) {
         case 0:
            return var0;
         case 1:
            return var0.isDirectory() ? null : var0.length();
         case 2:
            if (isFileSystemRoot(var0)) {
               return null;
            }

            long var4 = var0.lastModified();
            return var4 == 0L ? null : new Date(var4);
         default:
            return null;
         }
      } else {
         return null;
      }
   }

   public Object getFolderColumnValue(int var1) {
      return null;
   }

   public static <T> T invoke(Callable<T> var0) {
      try {
         return invoke(var0, RuntimeException.class);
      } catch (InterruptedException var2) {
         return null;
      }
   }

   public static <T, E extends Throwable> T invoke(Callable<T> var0, Class<E> var1) throws InterruptedException, E {
      try {
         return invoker.invoke(var0);
      } catch (Exception var3) {
         if (var3 instanceof RuntimeException) {
            throw (RuntimeException)var3;
         } else if (var3 instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            throw (InterruptedException)var3;
         } else if (var1.isInstance(var3)) {
            throw (Throwable)var1.cast(var3);
         } else {
            throw new RuntimeException("Unexpected error", var3);
         }
      }
   }

   static {
      String var0 = (String)Toolkit.getDefaultToolkit().getDesktopProperty("Shell.shellFolderManager");
      Class var1 = null;

      try {
         var1 = Class.forName(var0, false, (ClassLoader)null);
         if (!ShellFolderManager.class.isAssignableFrom(var1)) {
            var1 = null;
         }
      } catch (ClassNotFoundException var5) {
      } catch (NullPointerException var6) {
      } catch (SecurityException var7) {
      }

      if (var1 == null) {
         var1 = ShellFolderManager.class;
      }

      try {
         shellFolderManager = (ShellFolderManager)var1.newInstance();
      } catch (InstantiationException var3) {
         throw new Error("Could not instantiate Shell Folder Manager: " + var1.getName());
      } catch (IllegalAccessException var4) {
         throw new Error("Could not access Shell Folder Manager: " + var1.getName());
      }

      invoker = shellFolderManager.createInvoker();
      DEFAULT_COMPARATOR = new Comparator() {
         public int compare(Object var1, Object var2) {
            int var3;
            if (var1 == null && var2 == null) {
               var3 = 0;
            } else if (var1 != null && var2 == null) {
               var3 = 1;
            } else if (var1 == null && var2 != null) {
               var3 = -1;
            } else if (var1 instanceof Comparable) {
               var3 = ((Comparable)var1).compareTo(var2);
            } else {
               var3 = 0;
            }

            return var3;
         }
      };
      FILE_COMPARATOR = new Comparator<File>() {
         public int compare(File var1, File var2) {
            ShellFolder var3 = null;
            ShellFolder var4 = null;
            if (var1 instanceof ShellFolder) {
               var3 = (ShellFolder)var1;
               if (var3.isFileSystem()) {
                  var3 = null;
               }
            }

            if (var2 instanceof ShellFolder) {
               var4 = (ShellFolder)var2;
               if (var4.isFileSystem()) {
                  var4 = null;
               }
            }

            if (var3 != null && var4 != null) {
               return var3.compareTo((File)var4);
            } else if (var3 != null) {
               return -1;
            } else if (var4 != null) {
               return 1;
            } else {
               String var5 = var1.getName();
               String var6 = var2.getName();
               int var7 = var5.compareToIgnoreCase(var6);
               return var7 != 0 ? var7 : var5.compareTo(var6);
            }
         }
      };
   }

   public interface Invoker {
      <T> T invoke(Callable<T> var1) throws Exception;
   }
}
