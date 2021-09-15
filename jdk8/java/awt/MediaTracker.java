package java.awt;

import java.io.Serializable;
import sun.awt.image.MultiResolutionToolkitImage;

public class MediaTracker implements Serializable {
   Component target;
   MediaEntry head;
   private static final long serialVersionUID = -483174189758638095L;
   public static final int LOADING = 1;
   public static final int ABORTED = 2;
   public static final int ERRORED = 4;
   public static final int COMPLETE = 8;
   static final int DONE = 14;

   public MediaTracker(Component var1) {
      this.target = var1;
   }

   public void addImage(Image var1, int var2) {
      this.addImage(var1, var2, -1, -1);
   }

   public synchronized void addImage(Image var1, int var2, int var3, int var4) {
      this.addImageImpl(var1, var2, var3, var4);
      Image var5 = getResolutionVariant(var1);
      if (var5 != null) {
         this.addImageImpl(var5, var2, var3 == -1 ? -1 : 2 * var3, var4 == -1 ? -1 : 2 * var4);
      }

   }

   private void addImageImpl(Image var1, int var2, int var3, int var4) {
      this.head = MediaEntry.insert(this.head, new ImageMediaEntry(this, var1, var2, var3, var4));
   }

   public boolean checkAll() {
      return this.checkAll(false, true);
   }

   public boolean checkAll(boolean var1) {
      return this.checkAll(var1, true);
   }

   private synchronized boolean checkAll(boolean var1, boolean var2) {
      MediaEntry var3 = this.head;

      boolean var4;
      for(var4 = true; var3 != null; var3 = var3.next) {
         if ((var3.getStatus(var1, var2) & 14) == 0) {
            var4 = false;
         }
      }

      return var4;
   }

   public synchronized boolean isErrorAny() {
      for(MediaEntry var1 = this.head; var1 != null; var1 = var1.next) {
         if ((var1.getStatus(false, true) & 4) != 0) {
            return true;
         }
      }

      return false;
   }

   public synchronized Object[] getErrorsAny() {
      MediaEntry var1 = this.head;

      int var2;
      for(var2 = 0; var1 != null; var1 = var1.next) {
         if ((var1.getStatus(false, true) & 4) != 0) {
            ++var2;
         }
      }

      if (var2 == 0) {
         return null;
      } else {
         Object[] var3 = new Object[var2];
         var1 = this.head;

         for(var2 = 0; var1 != null; var1 = var1.next) {
            if ((var1.getStatus(false, false) & 4) != 0) {
               var3[var2++] = var1.getMedia();
            }
         }

         return var3;
      }
   }

   public void waitForAll() throws InterruptedException {
      this.waitForAll(0L);
   }

   public synchronized boolean waitForAll(long var1) throws InterruptedException {
      long var3 = System.currentTimeMillis() + var1;
      boolean var5 = true;

      while(true) {
         int var6 = this.statusAll(var5, var5);
         if ((var6 & 1) == 0) {
            return var6 == 8;
         }

         var5 = false;
         long var7;
         if (var1 == 0L) {
            var7 = 0L;
         } else {
            var7 = var3 - System.currentTimeMillis();
            if (var7 <= 0L) {
               return false;
            }
         }

         this.wait(var7);
      }
   }

   public int statusAll(boolean var1) {
      return this.statusAll(var1, true);
   }

   private synchronized int statusAll(boolean var1, boolean var2) {
      MediaEntry var3 = this.head;

      int var4;
      for(var4 = 0; var3 != null; var3 = var3.next) {
         var4 |= var3.getStatus(var1, var2);
      }

      return var4;
   }

   public boolean checkID(int var1) {
      return this.checkID(var1, false, true);
   }

   public boolean checkID(int var1, boolean var2) {
      return this.checkID(var1, var2, true);
   }

   private synchronized boolean checkID(int var1, boolean var2, boolean var3) {
      MediaEntry var4 = this.head;

      boolean var5;
      for(var5 = true; var4 != null; var4 = var4.next) {
         if (var4.getID() == var1 && (var4.getStatus(var2, var3) & 14) == 0) {
            var5 = false;
         }
      }

      return var5;
   }

   public synchronized boolean isErrorID(int var1) {
      for(MediaEntry var2 = this.head; var2 != null; var2 = var2.next) {
         if (var2.getID() == var1 && (var2.getStatus(false, true) & 4) != 0) {
            return true;
         }
      }

      return false;
   }

   public synchronized Object[] getErrorsID(int var1) {
      MediaEntry var2 = this.head;

      int var3;
      for(var3 = 0; var2 != null; var2 = var2.next) {
         if (var2.getID() == var1 && (var2.getStatus(false, true) & 4) != 0) {
            ++var3;
         }
      }

      if (var3 == 0) {
         return null;
      } else {
         Object[] var4 = new Object[var3];
         var2 = this.head;

         for(var3 = 0; var2 != null; var2 = var2.next) {
            if (var2.getID() == var1 && (var2.getStatus(false, false) & 4) != 0) {
               var4[var3++] = var2.getMedia();
            }
         }

         return var4;
      }
   }

   public void waitForID(int var1) throws InterruptedException {
      this.waitForID(var1, 0L);
   }

   public synchronized boolean waitForID(int var1, long var2) throws InterruptedException {
      long var4 = System.currentTimeMillis() + var2;
      boolean var6 = true;

      while(true) {
         int var7 = this.statusID(var1, var6, var6);
         if ((var7 & 1) == 0) {
            return var7 == 8;
         }

         var6 = false;
         long var8;
         if (var2 == 0L) {
            var8 = 0L;
         } else {
            var8 = var4 - System.currentTimeMillis();
            if (var8 <= 0L) {
               return false;
            }
         }

         this.wait(var8);
      }
   }

   public int statusID(int var1, boolean var2) {
      return this.statusID(var1, var2, true);
   }

   private synchronized int statusID(int var1, boolean var2, boolean var3) {
      MediaEntry var4 = this.head;

      int var5;
      for(var5 = 0; var4 != null; var4 = var4.next) {
         if (var4.getID() == var1) {
            var5 |= var4.getStatus(var2, var3);
         }
      }

      return var5;
   }

   public synchronized void removeImage(Image var1) {
      this.removeImageImpl(var1);
      Image var2 = getResolutionVariant(var1);
      if (var2 != null) {
         this.removeImageImpl(var2);
      }

      this.notifyAll();
   }

   private void removeImageImpl(Image var1) {
      MediaEntry var2 = this.head;

      MediaEntry var4;
      for(MediaEntry var3 = null; var2 != null; var2 = var4) {
         var4 = var2.next;
         if (var2.getMedia() == var1) {
            if (var3 == null) {
               this.head = var4;
            } else {
               var3.next = var4;
            }

            var2.cancel();
         } else {
            var3 = var2;
         }
      }

   }

   public synchronized void removeImage(Image var1, int var2) {
      this.removeImageImpl(var1, var2);
      Image var3 = getResolutionVariant(var1);
      if (var3 != null) {
         this.removeImageImpl(var3, var2);
      }

      this.notifyAll();
   }

   private void removeImageImpl(Image var1, int var2) {
      MediaEntry var3 = this.head;

      MediaEntry var5;
      for(MediaEntry var4 = null; var3 != null; var3 = var5) {
         var5 = var3.next;
         if (var3.getID() == var2 && var3.getMedia() == var1) {
            if (var4 == null) {
               this.head = var5;
            } else {
               var4.next = var5;
            }

            var3.cancel();
         } else {
            var4 = var3;
         }
      }

   }

   public synchronized void removeImage(Image var1, int var2, int var3, int var4) {
      this.removeImageImpl(var1, var2, var3, var4);
      Image var5 = getResolutionVariant(var1);
      if (var5 != null) {
         this.removeImageImpl(var5, var2, var3 == -1 ? -1 : 2 * var3, var4 == -1 ? -1 : 2 * var4);
      }

      this.notifyAll();
   }

   private void removeImageImpl(Image var1, int var2, int var3, int var4) {
      MediaEntry var5 = this.head;

      MediaEntry var7;
      for(MediaEntry var6 = null; var5 != null; var5 = var7) {
         var7 = var5.next;
         if (var5.getID() == var2 && var5 instanceof ImageMediaEntry && ((ImageMediaEntry)var5).matches(var1, var3, var4)) {
            if (var6 == null) {
               this.head = var7;
            } else {
               var6.next = var7;
            }

            var5.cancel();
         } else {
            var6 = var5;
         }
      }

   }

   synchronized void setDone() {
      this.notifyAll();
   }

   private static Image getResolutionVariant(Image var0) {
      return var0 instanceof MultiResolutionToolkitImage ? ((MultiResolutionToolkitImage)var0).getResolutionVariant() : null;
   }
}
