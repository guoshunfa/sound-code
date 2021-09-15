package com.apple.laf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

class AquaFileSystemModel extends AbstractTableModel implements PropertyChangeListener {
   private final JTable fFileList;
   private AquaFileSystemModel.LoadFilesThread loadThread = null;
   private Vector<File> files = null;
   JFileChooser filechooser = null;
   Vector<AquaFileSystemModel.SortableFile> fileCache = null;
   Object fileCacheLock = new Object();
   Vector<File> directories = null;
   int fetchID = 0;
   private final boolean[] fSortAscending = new boolean[]{true, true};
   private boolean fSortNames = true;
   private final String[] fColumnNames;
   public static final String SORT_BY_CHANGED = "sortByChanged";
   public static final String SORT_ASCENDING_CHANGED = "sortAscendingChanged";
   final AquaFileSystemModel.QuickSortNames sSortNames = new AquaFileSystemModel.QuickSortNames();
   final AquaFileSystemModel.QuickSortDates sSortDates = new AquaFileSystemModel.QuickSortDates();

   public AquaFileSystemModel(JFileChooser var1, JTable var2, String[] var3) {
      this.filechooser = var1;
      this.fFileList = var2;
      this.fColumnNames = var3;
      this.validateFileCache();
      this.updateSelectionMode();
   }

   void updateSelectionMode() {
      boolean var1 = this.filechooser.isMultiSelectionEnabled() && this.filechooser.getDialogType() != 1;
      this.fFileList.setSelectionMode(var1 ? 2 : 0);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 != "directoryChanged" && var2 != "fileViewChanged" && var2 != "fileFilterChanged" && var2 != "FileHidingChanged") {
         if (var2.equals("MultiSelectionEnabledChangedProperty")) {
            this.updateSelectionMode();
         } else if (var2 == "fileSelectionChanged") {
            this.invalidateFileCache();
            this.validateFileCache();
         }
      } else {
         this.invalidateFileCache();
         this.validateFileCache();
      }

      if (var2 == "sortByChanged") {
         this.fSortNames = (Integer)var1.getNewValue() == 0;
         this.invalidateFileCache();
         this.validateFileCache();
         this.fFileList.repaint();
      }

      if (var2 == "sortAscendingChanged") {
         int var3 = this.fSortNames ? 0 : 1;
         this.fSortAscending[var3] = (Boolean)var1.getNewValue();
         this.invalidateFileCache();
         this.validateFileCache();
         this.fFileList.repaint();
      }

   }

   public void invalidateFileCache() {
      this.files = null;
      this.directories = null;
      synchronized(this.fileCacheLock) {
         if (this.fileCache != null) {
            int var2 = this.fileCache.size();
            this.fileCache = null;
            this.fireTableRowsDeleted(0, var2);
         }

      }
   }

   public Vector<File> getDirectories() {
      return this.directories != null ? this.directories : this.directories;
   }

   public Vector<File> getFiles() {
      if (this.files != null) {
         return this.files;
      } else {
         this.files = new Vector();
         this.directories = new Vector();
         this.directories.addElement(this.filechooser.getFileSystemView().createFileObject(this.filechooser.getCurrentDirectory(), ".."));
         synchronized(this.fileCacheLock) {
            for(int var2 = 0; var2 < this.fileCache.size(); ++var2) {
               AquaFileSystemModel.SortableFile var3 = (AquaFileSystemModel.SortableFile)this.fileCache.elementAt(var2);
               File var4 = var3.fFile;
               if (this.filechooser.isTraversable(var4)) {
                  this.directories.addElement(var4);
               } else {
                  this.files.addElement(var4);
               }
            }

            return this.files;
         }
      }
   }

   public void runWhenDone(Runnable var1) {
      synchronized(this.fileCacheLock) {
         if (this.loadThread != null && this.loadThread.isAlive()) {
            this.loadThread.queuedTasks.add(var1);
         } else {
            SwingUtilities.invokeLater(var1);
         }
      }
   }

   public void validateFileCache() {
      File var1 = this.filechooser.getCurrentDirectory();
      if (var1 == null) {
         this.invalidateFileCache();
      } else {
         if (this.loadThread != null) {
            this.loadThread.interrupt();
         }

         ++this.fetchID;
         this.invalidateFileCache();
         synchronized(this.fileCacheLock) {
            this.fileCache = new Vector(50);
         }

         this.loadThread = new AquaFileSystemModel.LoadFilesThread(var1, this.fetchID);
         this.loadThread.start();
      }
   }

   public int getColumnCount() {
      return 2;
   }

   public String getColumnName(int var1) {
      return this.fColumnNames[var1];
   }

   public Class<? extends Object> getColumnClass(int var1) {
      return var1 == 0 ? File.class : Date.class;
   }

   public int getRowCount() {
      synchronized(this.fileCacheLock) {
         return this.fileCache != null ? this.fileCache.size() : 0;
      }
   }

   public boolean contains(File var1) {
      synchronized(this.fileCacheLock) {
         return this.fileCache != null ? this.fileCache.contains(new AquaFileSystemModel.SortableFile(var1)) : false;
      }
   }

   public int indexOf(File var1) {
      synchronized(this.fileCacheLock) {
         if (this.fileCache != null) {
            boolean var3 = this.fSortNames ? this.fSortAscending[0] : this.fSortAscending[1];
            int var4 = this.fileCache.indexOf(new AquaFileSystemModel.SortableFile(var1));
            return var3 ? var4 : this.fileCache.size() - var4 - 1;
         } else {
            return 0;
         }
      }
   }

   public Object getElementAt(int var1) {
      return this.getValueAt(var1, 0);
   }

   public Object getValueAt(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         boolean var3 = this.fSortNames ? this.fSortAscending[0] : this.fSortAscending[1];
         synchronized(this.fileCacheLock) {
            if (this.fileCache != null) {
               if (!var3) {
                  var1 = this.fileCache.size() - var1 - 1;
               }

               return ((AquaFileSystemModel.SortableFile)this.fileCache.elementAt(var1)).getValueAt(var2);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public void intervalAdded(ListDataEvent var1) {
   }

   public void intervalRemoved(ListDataEvent var1) {
   }

   protected void sort(Vector<Object> var1) {
      if (this.fSortNames) {
         this.sSortNames.quickSort(var1, 0, var1.size() - 1);
      } else {
         this.sSortDates.quickSort(var1, 0, var1.size() - 1);
      }

   }

   class DoChangeContents implements Runnable {
      private Vector<AquaFileSystemModel.SortableFile> contentFiles;
      private boolean doFire = true;
      private final Object lock = new Object();
      private final int fid;

      public DoChangeContents(Vector<AquaFileSystemModel.SortableFile> var2, int var3) {
         this.contentFiles = var2;
         this.fid = var3;
      }

      synchronized void cancel() {
         synchronized(this.lock) {
            this.doFire = false;
         }
      }

      public void run() {
         if (AquaFileSystemModel.this.fetchID == this.fid) {
            synchronized(this.lock) {
               if (this.doFire) {
                  synchronized(AquaFileSystemModel.this.fileCacheLock) {
                     if (AquaFileSystemModel.this.fileCache != null) {
                        for(int var3 = 0; var3 < this.contentFiles.size(); ++var3) {
                           AquaFileSystemModel.this.fileCache.addElement(this.contentFiles.elementAt(var3));
                           AquaFileSystemModel.this.fireTableRowsInserted(var3, var3);
                        }
                     }
                  }
               }

               this.contentFiles = null;
               AquaFileSystemModel.this.directories = null;
            }
         }

      }
   }

   class LoadFilesThread extends Thread {
      Vector<Runnable> queuedTasks = new Vector();
      File currentDirectory = null;
      int fid;

      public LoadFilesThread(File var2, int var3) {
         super("Aqua L&F File Loading Thread");
         this.currentDirectory = var2;
         this.fid = var3;
      }

      public void run() {
         Vector var1 = new Vector(10);
         FileSystemView var2 = AquaFileSystemModel.this.filechooser.getFileSystemView();
         File[] var3 = var2.getFiles(this.currentDirectory, AquaFileSystemModel.this.filechooser.isFileHidingEnabled());
         Vector var4 = new Vector();
         File[] var5 = var3;
         int var6 = var3.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            File var8 = var5[var7];
            var4.addElement(AquaFileSystemModel.this.new SortableFile(var8));
         }

         AquaFileSystemModel.this.sort(var4);
         Vector var12 = new Vector(10);
         var6 = var4.size();
         var7 = 0;

         do {
            if (var7 >= var6) {
               synchronized(AquaFileSystemModel.this.fileCacheLock) {
                  Iterator var14 = this.queuedTasks.iterator();

                  while(var14.hasNext()) {
                     Runnable var16 = (Runnable)var14.next();
                     SwingUtilities.invokeLater(var16);
                  }

                  return;
               }
            }

            for(int var9 = 0; var9 < 10 && var7 < var6; ++var7) {
               AquaFileSystemModel.SortableFile var13 = (AquaFileSystemModel.SortableFile)var4.elementAt(var7);
               var12.addElement(var13);
               ++var9;
            }

            AquaFileSystemModel.DoChangeContents var15 = AquaFileSystemModel.this.new DoChangeContents(var12, this.fid);
            var1.addElement(var15);
            SwingUtilities.invokeLater(var15);
            var12 = new Vector(10);
         } while(!this.isInterrupted());

         this.cancelRunnables(var1);
      }

      public void cancelRunnables(Vector<AquaFileSystemModel.DoChangeContents> var1) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            ((AquaFileSystemModel.DoChangeContents)var1.elementAt(var2)).cancel();
         }

      }
   }

   class SortableFile {
      File fFile;
      String fName;
      long fDateValue;
      Date fDate;

      SortableFile(File var2) {
         this.fFile = var2;
         this.fName = this.fFile.getName();
         this.fDateValue = this.fFile.lastModified();
         this.fDate = new Date(this.fDateValue);
      }

      public Object getValueAt(int var1) {
         return var1 == 0 ? this.fFile : this.fDate;
      }

      public boolean equals(Object var1) {
         AquaFileSystemModel.SortableFile var2 = (AquaFileSystemModel.SortableFile)var1;
         return var2.fFile.equals(this.fFile);
      }
   }

   class QuickSortDates extends AquaFileSystemModel.QuickSort {
      QuickSortDates() {
         super();
      }

      protected boolean lt(AquaFileSystemModel.SortableFile var1, AquaFileSystemModel.SortableFile var2) {
         return var1.fDateValue < var2.fDateValue;
      }
   }

   class QuickSortNames extends AquaFileSystemModel.QuickSort {
      QuickSortNames() {
         super();
      }

      protected boolean lt(AquaFileSystemModel.SortableFile var1, AquaFileSystemModel.SortableFile var2) {
         String var3 = var1.fName.toLowerCase();
         String var4 = var2.fName.toLowerCase();
         return var3.compareTo(var4) < 0;
      }
   }

   abstract class QuickSort {
      final void quickSort(Vector<Object> var1, int var2, int var3) {
         int var4 = var2;
         int var5 = var3;
         if (var3 > var2) {
            AquaFileSystemModel.SortableFile var6 = (AquaFileSystemModel.SortableFile)var1.elementAt((var2 + var3) / 2);

            while(var4 <= var5) {
               while(var4 < var3 && this.lt((AquaFileSystemModel.SortableFile)var1.elementAt(var4), var6)) {
                  ++var4;
               }

               while(var5 > var2 && this.lt(var6, (AquaFileSystemModel.SortableFile)var1.elementAt(var5))) {
                  --var5;
               }

               if (var4 <= var5) {
                  this.swap(var1, var4, var5);
                  ++var4;
                  --var5;
               }
            }

            if (var2 < var5) {
               this.quickSort(var1, var2, var5);
            }

            if (var4 < var3) {
               this.quickSort(var1, var4, var3);
            }
         }

      }

      private final void swap(Vector<Object> var1, int var2, int var3) {
         Object var4 = var1.elementAt(var2);
         var1.setElementAt(var1.elementAt(var3), var2);
         var1.setElementAt(var4, var3);
      }

      protected abstract boolean lt(AquaFileSystemModel.SortableFile var1, AquaFileSystemModel.SortableFile var2);
   }
}
