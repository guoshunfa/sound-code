package com.apple.laf;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;

class AquaFileView extends FileView {
   private static final boolean DEBUG = false;
   private static final int UNINITALIZED_LS_INFO = -1;
   static final int kLSItemInfoIsPlainFile = 1;
   static final int kLSItemInfoIsPackage = 2;
   static final int kLSItemInfoIsApplication = 4;
   static final int kLSItemInfoIsContainer = 8;
   static final int kLSItemInfoIsAliasFile = 16;
   static final int kLSItemInfoIsSymlink = 32;
   static final int kLSItemInfoIsInvisible = 64;
   static final int kLSItemInfoIsNativeApp = 128;
   static final int kLSItemInfoIsClassicApp = 256;
   static final int kLSItemInfoAppPrefersNative = 512;
   static final int kLSItemInfoAppPrefersClassic = 1024;
   static final int kLSItemInfoAppIsScriptable = 2048;
   static final int kLSItemInfoIsVolume = 4096;
   static final int kLSItemInfoExtensionIsHidden = 1048576;
   static final AquaUtils.RecyclableSingleton<String> machineName;
   final int MAX_CACHED_ENTRIES = 256;
   protected final Map<File, AquaFileView.FileInfo> cache = new LinkedHashMap<File, AquaFileView.FileInfo>() {
      protected boolean removeEldestEntry(Map.Entry<File, AquaFileView.FileInfo> var1) {
         return this.size() > 256;
      }
   };
   final AquaFileChooserUI fFileChooserUI;

   private static native String getNativePathToSharedJDKBundle();

   private static native String getNativeMachineName();

   private static native String getNativeDisplayName(byte[] var0, boolean var1);

   private static native int getNativeLSInfo(byte[] var0, boolean var1);

   private static native String getNativePathForResolvedAlias(byte[] var0, boolean var1);

   private static String getMachineName() {
      return (String)machineName.get();
   }

   protected static String getPathToRunningJDKBundle() {
      return "";
   }

   protected static String getPathToSharedJDKBundle() {
      return getNativePathToSharedJDKBundle();
   }

   AquaFileView.FileInfo getFileInfoFor(File var1) {
      AquaFileView.FileInfo var2 = (AquaFileView.FileInfo)this.cache.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         AquaFileView.FileInfo var3 = new AquaFileView.FileInfo(var1);
         this.cache.put(var1, var3);
         return var3;
      }
   }

   public AquaFileView(AquaFileChooserUI var1) {
      this.fFileChooserUI = var1;
   }

   String _directoryDescriptionText() {
      return this.fFileChooserUI.directoryDescriptionText;
   }

   String _fileDescriptionText() {
      return this.fFileChooserUI.fileDescriptionText;
   }

   boolean _packageIsTraversable() {
      return this.fFileChooserUI.fPackageIsTraversable == 0;
   }

   boolean _applicationIsTraversable() {
      return this.fFileChooserUI.fApplicationIsTraversable == 0;
   }

   public String getName(File var1) {
      AquaFileView.FileInfo var2 = this.getFileInfoFor(var1);
      if (var2.displayName != null) {
         return var2.displayName;
      } else {
         String var3 = getNativeDisplayName(var2.pathBytes, var2.isDirectory);
         if (var3 != null) {
            var2.displayName = var3;
            return var3;
         } else {
            String var4 = var1.getName();
            if (var1.isDirectory() && this.fFileChooserUI.getFileChooser().getFileSystemView().isRoot(var1)) {
               String var5 = getMachineName();
               var2.displayName = var5;
               return var5;
            } else {
               var2.displayName = var4;
               return var4;
            }
         }
      }
   }

   public String getDescription(File var1) {
      return var1.getName();
   }

   public String getTypeDescription(File var1) {
      return var1.isDirectory() ? this._directoryDescriptionText() : this._fileDescriptionText();
   }

   public Icon getIcon(File var1) {
      AquaFileView.FileInfo var2 = this.getFileInfoFor(var1);
      if (var2.icon != null) {
         return var2.icon;
      } else {
         if (var1 == null) {
            var2.icon = AquaIcon.SystemIcon.getDocumentIconUIResource();
         } else {
            AquaIcon.FileIcon var3 = new AquaIcon.FileIcon(var1);
            var2.icon = var3;
            if (!var3.hasIconRef()) {
               if (var1.isDirectory()) {
                  if (this.fFileChooserUI.getFileChooser().getFileSystemView().isRoot(var1)) {
                     var2.icon = AquaIcon.SystemIcon.getComputerIconUIResource();
                  } else if (var1.getParent() != null && !var1.getParent().equals("/")) {
                     var2.icon = AquaIcon.SystemIcon.getFolderIconUIResource();
                  } else {
                     var2.icon = AquaIcon.SystemIcon.getHardDriveIconUIResource();
                  }
               } else {
                  var2.icon = AquaIcon.SystemIcon.getDocumentIconUIResource();
               }
            }
         }

         return var2.icon;
      }
   }

   public Boolean isTraversable(File var1) {
      if (var1.isDirectory()) {
         if (this._packageIsTraversable() && this._applicationIsTraversable()) {
            return Boolean.TRUE;
         } else {
            if (!this._packageIsTraversable() && !this._applicationIsTraversable()) {
               if (this.isPackage(var1) || this.isApplication(var1)) {
                  return Boolean.FALSE;
               }
            } else if (!this._applicationIsTraversable()) {
               if (this.isApplication(var1)) {
                  return Boolean.FALSE;
               }
            } else if (!this._packageIsTraversable() && this.isPackage(var1) && !this.isApplication(var1)) {
               return Boolean.FALSE;
            }

            return Boolean.TRUE;
         }
      } else if (this.isAlias(var1)) {
         File var2 = this.resolveAlias(var1);
         return var2.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
      } else {
         return Boolean.FALSE;
      }
   }

   int getLSInfoFor(File var1) {
      AquaFileView.FileInfo var2 = this.getFileInfoFor(var1);
      if (var2.launchServicesInfo == -1) {
         var2.launchServicesInfo = getNativeLSInfo(var2.pathBytes, var2.isDirectory);
      }

      return var2.launchServicesInfo;
   }

   boolean isAlias(File var1) {
      int var2 = this.getLSInfoFor(var1);
      return (var2 & 16) != 0 && (var2 & 32) == 0;
   }

   boolean isApplication(File var1) {
      return (this.getLSInfoFor(var1) & 4) != 0;
   }

   boolean isPackage(File var1) {
      return (this.getLSInfoFor(var1) & 2) != 0;
   }

   File resolveAlias(File var1) {
      if (var1.exists() && !this.isAlias(var1)) {
         return var1;
      } else {
         LinkedList var2 = getPathComponents(var1);
         if (var2 == null) {
            return var1;
         } else {
            File var3 = new File("/");
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               var3 = new File(var3, var5);
               AquaFileView.FileInfo var6 = this.getFileInfoFor(var3);
               if (!var3.exists()) {
                  return var1;
               }

               if (this.isAlias(var3)) {
                  String var7 = getNativePathForResolvedAlias(var6.pathBytes, var6.isDirectory);
                  if (var7 == null) {
                     return var1;
                  }

                  var3 = new File(var7);
               }
            }

            return var3;
         }
      }
   }

   private static LinkedList<String> getPathComponents(File var0) {
      LinkedList var1 = new LinkedList();
      File var3 = new File(var0.getAbsolutePath());
      var1.add(0, var3.getName());

      String var2;
      while((var2 = var3.getParent()) != null) {
         var3 = new File(var2);
         var1.add(0, var3.getName());
      }

      return var1;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osxui");
            return null;
         }
      });
      machineName = new AquaUtils.RecyclableSingleton<String>() {
         protected String getInstance() {
            return AquaFileView.getNativeMachineName();
         }
      };
   }

   static class FileInfo {
      final boolean isDirectory;
      final String absolutePath;
      byte[] pathBytes;
      String displayName;
      Icon icon;
      int launchServicesInfo = -1;

      FileInfo(File var1) {
         this.isDirectory = var1.isDirectory();
         this.absolutePath = var1.getAbsolutePath();

         try {
            this.pathBytes = this.absolutePath.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var3) {
            this.pathBytes = new byte[0];
         }

      }
   }
}
