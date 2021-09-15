package sun.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class TransformerManager {
   private TransformerManager.TransformerInfo[] mTransformerList = new TransformerManager.TransformerInfo[0];
   private boolean mIsRetransformable;

   TransformerManager(boolean var1) {
      this.mIsRetransformable = var1;
   }

   boolean isRetransformable() {
      return this.mIsRetransformable;
   }

   public synchronized void addTransformer(ClassFileTransformer var1) {
      TransformerManager.TransformerInfo[] var2 = this.mTransformerList;
      TransformerManager.TransformerInfo[] var3 = new TransformerManager.TransformerInfo[var2.length + 1];
      System.arraycopy(var2, 0, var3, 0, var2.length);
      var3[var2.length] = new TransformerManager.TransformerInfo(var1);
      this.mTransformerList = var3;
   }

   public synchronized boolean removeTransformer(ClassFileTransformer var1) {
      boolean var2 = false;
      TransformerManager.TransformerInfo[] var3 = this.mTransformerList;
      int var4 = var3.length;
      int var5 = var4 - 1;
      int var6 = 0;

      for(int var7 = var4 - 1; var7 >= 0; --var7) {
         if (var3[var7].transformer() == var1) {
            var2 = true;
            var6 = var7;
            break;
         }
      }

      if (var2) {
         TransformerManager.TransformerInfo[] var8 = new TransformerManager.TransformerInfo[var5];
         if (var6 > 0) {
            System.arraycopy(var3, 0, var8, 0, var6);
         }

         if (var6 < var5) {
            System.arraycopy(var3, var6 + 1, var8, var6, var5 - var6);
         }

         this.mTransformerList = var8;
      }

      return var2;
   }

   synchronized boolean includesTransformer(ClassFileTransformer var1) {
      TransformerManager.TransformerInfo[] var2 = this.mTransformerList;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TransformerManager.TransformerInfo var5 = var2[var4];
         if (var5.transformer() == var1) {
            return true;
         }
      }

      return false;
   }

   private TransformerManager.TransformerInfo[] getSnapshotTransformerList() {
      return this.mTransformerList;
   }

   public byte[] transform(ClassLoader var1, String var2, Class<?> var3, ProtectionDomain var4, byte[] var5) {
      boolean var6 = false;
      TransformerManager.TransformerInfo[] var7 = this.getSnapshotTransformerList();
      byte[] var8 = var5;

      for(int var9 = 0; var9 < var7.length; ++var9) {
         TransformerManager.TransformerInfo var10 = var7[var9];
         ClassFileTransformer var11 = var10.transformer();
         byte[] var12 = null;

         try {
            var12 = var11.transform(var1, var2, var3, var4, var8);
         } catch (Throwable var14) {
         }

         if (var12 != null) {
            var6 = true;
            var8 = var12;
         }
      }

      byte[] var15;
      if (var6) {
         var15 = var8;
      } else {
         var15 = null;
      }

      return var15;
   }

   int getTransformerCount() {
      TransformerManager.TransformerInfo[] var1 = this.getSnapshotTransformerList();
      return var1.length;
   }

   boolean setNativeMethodPrefix(ClassFileTransformer var1, String var2) {
      TransformerManager.TransformerInfo[] var3 = this.getSnapshotTransformerList();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         TransformerManager.TransformerInfo var5 = var3[var4];
         ClassFileTransformer var6 = var5.transformer();
         if (var6 == var1) {
            var5.setPrefix(var2);
            return true;
         }
      }

      return false;
   }

   String[] getNativeMethodPrefixes() {
      TransformerManager.TransformerInfo[] var1 = this.getSnapshotTransformerList();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         TransformerManager.TransformerInfo var4 = var1[var3];
         var2[var3] = var4.getPrefix();
      }

      return var2;
   }

   private class TransformerInfo {
      final ClassFileTransformer mTransformer;
      String mPrefix;

      TransformerInfo(ClassFileTransformer var2) {
         this.mTransformer = var2;
         this.mPrefix = null;
      }

      ClassFileTransformer transformer() {
         return this.mTransformer;
      }

      String getPrefix() {
         return this.mPrefix;
      }

      void setPrefix(String var1) {
         this.mPrefix = var1;
      }
   }
}
