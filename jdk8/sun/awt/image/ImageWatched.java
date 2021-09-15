package sun.awt.image;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;

public abstract class ImageWatched {
   public static ImageWatched.Link endlink = new ImageWatched.Link();
   public ImageWatched.Link watcherList;

   public ImageWatched() {
      this.watcherList = endlink;
   }

   public synchronized void addWatcher(ImageObserver var1) {
      if (var1 != null && !this.isWatcher(var1)) {
         this.watcherList = new ImageWatched.WeakLink(var1, this.watcherList);
      }

      this.watcherList = this.watcherList.removeWatcher((ImageObserver)null);
   }

   public synchronized boolean isWatcher(ImageObserver var1) {
      return this.watcherList.isWatcher(var1);
   }

   public void removeWatcher(ImageObserver var1) {
      synchronized(this) {
         this.watcherList = this.watcherList.removeWatcher(var1);
      }

      if (this.watcherList == endlink) {
         this.notifyWatcherListEmpty();
      }

   }

   public boolean isWatcherListEmpty() {
      synchronized(this) {
         this.watcherList = this.watcherList.removeWatcher((ImageObserver)null);
      }

      return this.watcherList == endlink;
   }

   public void newInfo(Image var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.watcherList.newInfo(var1, var2, var3, var4, var5, var6)) {
         this.removeWatcher((ImageObserver)null);
      }

   }

   protected abstract void notifyWatcherListEmpty();

   public static class WeakLink extends ImageWatched.Link {
      private final ImageWatched.AccWeakReference<ImageObserver> myref;
      private ImageWatched.Link next;

      public WeakLink(ImageObserver var1, ImageWatched.Link var2) {
         this.myref = new ImageWatched.AccWeakReference(var1);
         this.next = var2;
      }

      public boolean isWatcher(ImageObserver var1) {
         return this.myref.get() == var1 || this.next.isWatcher(var1);
      }

      public ImageWatched.Link removeWatcher(ImageObserver var1) {
         ImageObserver var2 = (ImageObserver)this.myref.get();
         if (var2 == null) {
            return this.next.removeWatcher(var1);
         } else if (var2 == var1) {
            return this.next;
         } else {
            this.next = this.next.removeWatcher(var1);
            return this;
         }
      }

      private static boolean update(ImageObserver var0, AccessControlContext var1, Image var2, int var3, int var4, int var5, int var6, int var7) {
         return var1 == null && System.getSecurityManager() == null ? false : (Boolean)AccessController.doPrivileged(() -> {
            return var0.imageUpdate(var2, var3, var4, var5, var6, var7);
         }, var1);
      }

      public boolean newInfo(Image var1, int var2, int var3, int var4, int var5, int var6) {
         boolean var7 = this.next.newInfo(var1, var2, var3, var4, var5, var6);
         ImageObserver var8 = (ImageObserver)this.myref.get();
         if (var8 == null) {
            var7 = true;
         } else if (!update(var8, this.myref.acc, var1, var2, var3, var4, var5, var6)) {
            this.myref.clear();
            var7 = true;
         }

         return var7;
      }
   }

   static class AccWeakReference<T> extends WeakReference<T> {
      private final AccessControlContext acc = AccessController.getContext();

      AccWeakReference(T var1) {
         super(var1);
      }
   }

   public static class Link {
      public boolean isWatcher(ImageObserver var1) {
         return false;
      }

      public ImageWatched.Link removeWatcher(ImageObserver var1) {
         return this;
      }

      public boolean newInfo(Image var1, int var2, int var3, int var4, int var5, int var6) {
         return false;
      }
   }
}
