package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.filechooser.FileSystemView;
import sun.awt.shell.ShellFolder;

public class BasicDirectoryModel extends AbstractListModel<Object> implements PropertyChangeListener {
   private JFileChooser filechooser = null;
   private Vector<File> fileCache = new Vector(50);
   private BasicDirectoryModel.LoadFilesThread loadThread = null;
   private Vector<File> files = null;
   private Vector<File> directories = null;
   private int fetchID = 0;
   private PropertyChangeSupport changeSupport;
   private boolean busy = false;

   public BasicDirectoryModel(JFileChooser var1) {
      this.filechooser = var1;
      this.validateFileCache();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 != "directoryChanged" && var2 != "fileViewChanged" && var2 != "fileFilterChanged" && var2 != "FileHidingChanged" && var2 != "fileSelectionChanged") {
         if ("UI".equals(var2)) {
            Object var3 = var1.getOldValue();
            if (var3 instanceof BasicFileChooserUI) {
               BasicFileChooserUI var4 = (BasicFileChooserUI)var3;
               BasicDirectoryModel var5 = var4.getModel();
               if (var5 != null) {
                  var5.invalidateFileCache();
               }
            }
         } else if ("JFileChooserDialogIsClosingProperty".equals(var2)) {
            this.invalidateFileCache();
         }
      } else {
         this.validateFileCache();
      }

   }

   public void invalidateFileCache() {
      if (this.loadThread != null) {
         this.loadThread.interrupt();
         this.loadThread.cancelRunnables();
         this.loadThread = null;
      }

   }

   public Vector<File> getDirectories() {
      synchronized(this.fileCache) {
         if (this.directories != null) {
            return this.directories;
         } else {
            Vector var2 = this.getFiles();
            return this.directories;
         }
      }
   }

   public Vector<File> getFiles() {
      synchronized(this.fileCache) {
         if (this.files != null) {
            return this.files;
         } else {
            this.files = new Vector();
            this.directories = new Vector();
            this.directories.addElement(this.filechooser.getFileSystemView().createFileObject(this.filechooser.getCurrentDirectory(), ".."));

            for(int var2 = 0; var2 < this.getSize(); ++var2) {
               File var3 = (File)this.fileCache.get(var2);
               if (this.filechooser.isTraversable(var3)) {
                  this.directories.add(var3);
               } else {
                  this.files.add(var3);
               }
            }

            return this.files;
         }
      }
   }

   public void validateFileCache() {
      File var1 = this.filechooser.getCurrentDirectory();
      if (var1 != null) {
         if (this.loadThread != null) {
            this.loadThread.interrupt();
            this.loadThread.cancelRunnables();
         }

         this.setBusy(true, ++this.fetchID);
         this.loadThread = new BasicDirectoryModel.LoadFilesThread(var1, this.fetchID);
         this.loadThread.start();
      }
   }

   public boolean renameFile(File var1, File var2) {
      synchronized(this.fileCache) {
         if (var1.renameTo(var2)) {
            this.validateFileCache();
            return true;
         } else {
            return false;
         }
      }
   }

   public void fireContentsChanged() {
      this.fireContentsChanged(this, 0, this.getSize() - 1);
   }

   public int getSize() {
      return this.fileCache.size();
   }

   public boolean contains(Object var1) {
      return this.fileCache.contains(var1);
   }

   public int indexOf(Object var1) {
      return this.fileCache.indexOf(var1);
   }

   public Object getElementAt(int var1) {
      return this.fileCache.get(var1);
   }

   public void intervalAdded(ListDataEvent var1) {
   }

   public void intervalRemoved(ListDataEvent var1) {
   }

   protected void sort(Vector<? extends File> var1) {
      ShellFolder.sort(var1);
   }

   protected boolean lt(File var1, File var2) {
      int var3 = var1.getName().toLowerCase().compareTo(var2.getName().toLowerCase());
      if (var3 != 0) {
         return var3 < 0;
      } else {
         return var1.getName().compareTo(var2.getName()) < 0;
      }
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport == null) {
         this.changeSupport = new PropertyChangeSupport(this);
      }

      this.changeSupport.addPropertyChangeListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport != null) {
         this.changeSupport.removePropertyChangeListener(var1);
      }

   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (this.changeSupport != null) {
         this.changeSupport.firePropertyChange(var1, var2, var3);
      }

   }

   private synchronized void setBusy(final boolean var1, int var2) {
      if (var2 == this.fetchID) {
         boolean var3 = this.busy;
         this.busy = var1;
         if (this.changeSupport != null && var1 != var3) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  BasicDirectoryModel.this.firePropertyChange("busy", !var1, var1);
               }
            });
         }
      }

   }

   class DoChangeContents implements Runnable {
      private List<File> addFiles;
      private List<File> remFiles;
      private boolean doFire = true;
      private int fid;
      private int addStart = 0;
      private int remStart = 0;

      public DoChangeContents(List<File> var2, int var3, List<File> var4, int var5, int var6) {
         this.addFiles = var2;
         this.addStart = var3;
         this.remFiles = var4;
         this.remStart = var5;
         this.fid = var6;
      }

      synchronized void cancel() {
         this.doFire = false;
      }

      public synchronized void run() {
         if (BasicDirectoryModel.this.fetchID == this.fid && this.doFire) {
            int var1 = this.remFiles == null ? 0 : this.remFiles.size();
            int var2 = this.addFiles == null ? 0 : this.addFiles.size();
            synchronized(BasicDirectoryModel.this.fileCache) {
               if (var1 > 0) {
                  BasicDirectoryModel.this.fileCache.removeAll(this.remFiles);
               }

               if (var2 > 0) {
                  BasicDirectoryModel.this.fileCache.addAll(this.addStart, this.addFiles);
               }

               BasicDirectoryModel.this.files = null;
               BasicDirectoryModel.this.directories = null;
            }

            if (var1 > 0 && var2 == 0) {
               BasicDirectoryModel.this.fireIntervalRemoved(BasicDirectoryModel.this, this.remStart, this.remStart + var1 - 1);
            } else if (var2 > 0 && var1 == 0 && this.addStart + var2 <= BasicDirectoryModel.this.fileCache.size()) {
               BasicDirectoryModel.this.fireIntervalAdded(BasicDirectoryModel.this, this.addStart, this.addStart + var2 - 1);
            } else {
               BasicDirectoryModel.this.fireContentsChanged();
            }
         }

      }
   }

   class LoadFilesThread extends Thread {
      File currentDirectory = null;
      int fid;
      Vector<BasicDirectoryModel.DoChangeContents> runnables = new Vector(10);

      public LoadFilesThread(File var2, int var3) {
         super("Basic L&F File Loading Thread");
         this.currentDirectory = var2;
         this.fid = var3;
      }

      public void run() {
         this.run0();
         BasicDirectoryModel.this.setBusy(false, this.fid);
      }

      public void run0() {
         FileSystemView var1 = BasicDirectoryModel.this.filechooser.getFileSystemView();
         if (!this.isInterrupted()) {
            File[] var2 = var1.getFiles(this.currentDirectory, BasicDirectoryModel.this.filechooser.isFileHidingEnabled());
            if (!this.isInterrupted()) {
               final Vector var3 = new Vector();
               Vector var4 = new Vector();
               File[] var5 = var2;
               int var6 = var2.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  File var8 = var5[var7];
                  if (BasicDirectoryModel.this.filechooser.accept(var8)) {
                     boolean var9 = BasicDirectoryModel.this.filechooser.isTraversable(var8);
                     if (var9) {
                        var3.addElement(var8);
                     } else if (BasicDirectoryModel.this.filechooser.isFileSelectionEnabled()) {
                        var4.addElement(var8);
                     }

                     if (this.isInterrupted()) {
                        return;
                     }
                  }
               }

               BasicDirectoryModel.this.sort(var3);
               BasicDirectoryModel.this.sort(var4);
               var3.addAll(var4);
               BasicDirectoryModel.DoChangeContents var10 = (BasicDirectoryModel.DoChangeContents)ShellFolder.invoke(new Callable<BasicDirectoryModel.DoChangeContents>() {
                  public BasicDirectoryModel.DoChangeContents call() {
                     int var1 = var3.size();
                     int var2 = BasicDirectoryModel.this.fileCache.size();
                     int var3x;
                     int var4;
                     int var5;
                     if (var1 > var2) {
                        var3x = var2;
                        var4 = var1;

                        label64:
                        for(var5 = 0; var5 < var2; ++var5) {
                           if (!((File)var3.get(var5)).equals(BasicDirectoryModel.this.fileCache.get(var5))) {
                              var3x = var5;
                              int var6 = var5;

                              while(true) {
                                 if (var6 >= var1) {
                                    break label64;
                                 }

                                 if (((File)var3.get(var6)).equals(BasicDirectoryModel.this.fileCache.get(var5))) {
                                    var4 = var6;
                                    break label64;
                                 }

                                 ++var6;
                              }
                           }
                        }

                        if (var3x >= 0 && var4 > var3x && var3.subList(var4, var1).equals(BasicDirectoryModel.this.fileCache.subList(var3x, var2))) {
                           if (LoadFilesThread.this.isInterrupted()) {
                              return null;
                           }

                           return BasicDirectoryModel.this.new DoChangeContents(var3.subList(var3x, var4), var3x, (List)null, 0, LoadFilesThread.this.fid);
                        }
                     } else if (var1 < var2) {
                        var3x = -1;
                        var4 = -1;

                        for(var5 = 0; var5 < var1; ++var5) {
                           if (!((File)var3.get(var5)).equals(BasicDirectoryModel.this.fileCache.get(var5))) {
                              var3x = var5;
                              var4 = var5 + var2 - var1;
                              break;
                           }
                        }

                        if (var3x >= 0 && var4 > var3x && BasicDirectoryModel.this.fileCache.subList(var4, var2).equals(var3.subList(var3x, var1))) {
                           if (LoadFilesThread.this.isInterrupted()) {
                              return null;
                           }

                           return BasicDirectoryModel.this.new DoChangeContents((List)null, 0, new Vector(BasicDirectoryModel.this.fileCache.subList(var3x, var4)), var3x, LoadFilesThread.this.fid);
                        }
                     }

                     if (!BasicDirectoryModel.this.fileCache.equals(var3)) {
                        if (LoadFilesThread.this.isInterrupted()) {
                           LoadFilesThread.this.cancelRunnables(LoadFilesThread.this.runnables);
                        }

                        return BasicDirectoryModel.this.new DoChangeContents(var3, 0, BasicDirectoryModel.this.fileCache, 0, LoadFilesThread.this.fid);
                     } else {
                        return null;
                     }
                  }
               });
               if (var10 != null) {
                  this.runnables.addElement(var10);
                  SwingUtilities.invokeLater(var10);
               }

            }
         }
      }

      public void cancelRunnables(Vector<BasicDirectoryModel.DoChangeContents> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            BasicDirectoryModel.DoChangeContents var3 = (BasicDirectoryModel.DoChangeContents)var2.next();
            var3.cancel();
         }

      }

      public void cancelRunnables() {
         this.cancelRunnables(this.runnables);
      }
   }
}
