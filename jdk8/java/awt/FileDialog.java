package java.awt;

import java.awt.peer.FileDialogPeer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AWTAccessor;

public class FileDialog extends Dialog {
   public static final int LOAD = 0;
   public static final int SAVE = 1;
   int mode;
   String dir;
   String file;
   private File[] files;
   private boolean multipleMode;
   FilenameFilter filter;
   private static final String base = "filedlg";
   private static int nameCounter = 0;
   private static final long serialVersionUID = 5035145889651310422L;

   private static native void initIDs();

   public FileDialog(Frame var1) {
      this((Frame)var1, "", 0);
   }

   public FileDialog(Frame var1, String var2) {
      this((Frame)var1, var2, 0);
   }

   public FileDialog(Frame var1, String var2, int var3) {
      super(var1, var2, true);
      this.multipleMode = false;
      this.setMode(var3);
      this.setLayout((LayoutManager)null);
   }

   public FileDialog(Dialog var1) {
      this((Dialog)var1, "", 0);
   }

   public FileDialog(Dialog var1, String var2) {
      this((Dialog)var1, var2, 0);
   }

   public FileDialog(Dialog var1, String var2, int var3) {
      super(var1, var2, true);
      this.multipleMode = false;
      this.setMode(var3);
      this.setLayout((LayoutManager)null);
   }

   String constructComponentName() {
      Class var1 = FileDialog.class;
      synchronized(FileDialog.class) {
         return "filedlg" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.parent != null && this.parent.getPeer() == null) {
            this.parent.addNotify();
         }

         if (this.peer == null) {
            this.peer = this.getToolkit().createFileDialog(this);
         }

         super.addNotify();
      }
   }

   public int getMode() {
      return this.mode;
   }

   public void setMode(int var1) {
      switch(var1) {
      case 0:
      case 1:
         this.mode = var1;
         return;
      default:
         throw new IllegalArgumentException("illegal file dialog mode");
      }
   }

   public String getDirectory() {
      return this.dir;
   }

   public void setDirectory(String var1) {
      this.dir = var1 != null && var1.equals("") ? null : var1;
      FileDialogPeer var2 = (FileDialogPeer)this.peer;
      if (var2 != null) {
         var2.setDirectory(this.dir);
      }

   }

   public String getFile() {
      return this.file;
   }

   public File[] getFiles() {
      synchronized(this.getObjectLock()) {
         return this.files != null ? (File[])this.files.clone() : new File[0];
      }
   }

   private void setFiles(File[] var1) {
      synchronized(this.getObjectLock()) {
         this.files = var1;
      }
   }

   public void setFile(String var1) {
      this.file = var1 != null && var1.equals("") ? null : var1;
      FileDialogPeer var2 = (FileDialogPeer)this.peer;
      if (var2 != null) {
         var2.setFile(this.file);
      }

   }

   public void setMultipleMode(boolean var1) {
      synchronized(this.getObjectLock()) {
         this.multipleMode = var1;
      }
   }

   public boolean isMultipleMode() {
      synchronized(this.getObjectLock()) {
         return this.multipleMode;
      }
   }

   public FilenameFilter getFilenameFilter() {
      return this.filter;
   }

   public synchronized void setFilenameFilter(FilenameFilter var1) {
      this.filter = var1;
      FileDialogPeer var2 = (FileDialogPeer)this.peer;
      if (var2 != null) {
         var2.setFilenameFilter(var1);
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      if (this.dir != null && this.dir.equals("")) {
         this.dir = null;
      }

      if (this.file != null && this.file.equals("")) {
         this.file = null;
      }

   }

   protected String paramString() {
      String var1 = super.paramString();
      var1 = var1 + ",dir= " + this.dir;
      var1 = var1 + ",file= " + this.file;
      return var1 + (this.mode == 0 ? ",load" : ",save");
   }

   boolean postsOldMouseEvents() {
      return false;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setFileDialogAccessor(new AWTAccessor.FileDialogAccessor() {
         public void setFiles(FileDialog var1, File[] var2) {
            var1.setFiles(var2);
         }

         public void setFile(FileDialog var1, String var2) {
            var1.file = "".equals(var2) ? null : var2;
         }

         public void setDirectory(FileDialog var1, String var2) {
            var1.dir = "".equals(var2) ? null : var2;
         }

         public boolean isMultipleMode(FileDialog var1) {
            synchronized(var1.getObjectLock()) {
               return var1.multipleMode;
            }
         }
      });
   }
}
