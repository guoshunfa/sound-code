package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.DisplayChangedListener;
import sun.awt.image.SurfaceManager;
import sun.java2d.loops.Blit;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;

public abstract class SurfaceDataProxy implements DisplayChangedListener, SurfaceManager.FlushableCacheData {
   private static boolean cachingAllowed = true;
   private static int defaultThreshold;
   public static SurfaceDataProxy UNCACHED;
   private int threshold;
   private StateTracker srcTracker;
   private int numtries;
   private SurfaceData cachedSD;
   private StateTracker cacheTracker;
   private boolean valid;

   public static boolean isCachingAllowed() {
      return cachingAllowed;
   }

   public abstract boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4);

   public abstract SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4);

   public StateTracker getRetryTracker(SurfaceData var1) {
      return new SurfaceDataProxy.CountdownTracker(this.threshold);
   }

   public SurfaceDataProxy() {
      this(defaultThreshold);
   }

   public SurfaceDataProxy(int var1) {
      this.threshold = var1;
      this.srcTracker = StateTracker.NEVER_CURRENT;
      this.cacheTracker = StateTracker.NEVER_CURRENT;
      this.valid = true;
   }

   public boolean isValid() {
      return this.valid;
   }

   public void invalidate() {
      this.valid = false;
   }

   public boolean flush(boolean var1) {
      if (var1) {
         this.invalidate();
      }

      this.flush();
      return !this.isValid();
   }

   public synchronized void flush() {
      SurfaceData var1 = this.cachedSD;
      this.cachedSD = null;
      this.cacheTracker = StateTracker.NEVER_CURRENT;
      if (var1 != null) {
         var1.flush();
      }

   }

   public boolean isAccelerated() {
      return this.isValid() && this.srcTracker.isCurrent() && this.cacheTracker.isCurrent();
   }

   protected void activateDisplayListener() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (var1 instanceof SunGraphicsEnvironment) {
         ((SunGraphicsEnvironment)var1).addDisplayChangedListener(this);
      }

   }

   public void displayChanged() {
      this.flush();
   }

   public void paletteChanged() {
      this.srcTracker = StateTracker.NEVER_CURRENT;
   }

   public SurfaceData replaceData(SurfaceData var1, int var2, CompositeType var3, Color var4) {
      if (this.isSupportedOperation(var1, var2, var3, var4)) {
         if (!this.srcTracker.isCurrent()) {
            synchronized(this) {
               this.numtries = this.threshold;
               this.srcTracker = var1.getStateTracker();
               this.cacheTracker = StateTracker.NEVER_CURRENT;
            }

            if (!this.srcTracker.isCurrent()) {
               if (var1.getState() == StateTrackable.State.UNTRACKABLE) {
                  this.invalidate();
                  this.flush();
               }

               return var1;
            }
         }

         SurfaceData var5 = this.cachedSD;
         if (!this.cacheTracker.isCurrent()) {
            synchronized(this) {
               if (this.numtries > 0) {
                  --this.numtries;
                  return var1;
               }
            }

            Rectangle var6 = var1.getBounds();
            int var7 = var6.width;
            int var8 = var6.height;
            StateTracker var9 = this.srcTracker;
            var5 = this.validateSurfaceData(var1, var5, var7, var8);
            if (var5 == null) {
               synchronized(this) {
                  if (var9 == this.srcTracker) {
                     this.cacheTracker = this.getRetryTracker(var1);
                     this.cachedSD = null;
                  }

                  return var1;
               }
            }

            this.updateSurfaceData(var1, var5, var7, var8);
            if (!var5.isValid()) {
               return var1;
            }

            synchronized(this) {
               if (var9 == this.srcTracker && var9.isCurrent()) {
                  this.cacheTracker = var5.getStateTracker();
                  this.cachedSD = var5;
               }
            }
         }

         if (var5 != null) {
            return var5;
         }
      }

      return var1;
   }

   public void updateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
      SurfaceType var5 = var1.getSurfaceType();
      SurfaceType var6 = var2.getSurfaceType();
      Blit var7 = Blit.getFromCache(var5, CompositeType.SrcNoEa, var6);
      var7.Blit(var1, var2, AlphaComposite.Src, (Region)null, 0, 0, 0, 0, var3, var4);
      var2.markDirty();
   }

   public void updateSurfaceDataBg(SurfaceData var1, SurfaceData var2, int var3, int var4, Color var5) {
      SurfaceType var6 = var1.getSurfaceType();
      SurfaceType var7 = var2.getSurfaceType();
      BlitBg var8 = BlitBg.getFromCache(var6, CompositeType.SrcNoEa, var7);
      var8.BlitBg(var1, var2, AlphaComposite.Src, (Region)null, var5.getRGB(), 0, 0, 0, 0, var3, var4);
      var2.markDirty();
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.managedimages")));
      if (var0 != null && var0.equals("false")) {
         cachingAllowed = false;
         System.out.println("Disabling managed images");
      }

      defaultThreshold = 1;
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.accthreshold")));
      if (var1 != null) {
         try {
            int var2 = Integer.parseInt(var1);
            if (var2 >= 0) {
               defaultThreshold = var2;
               System.out.println("New Default Acceleration Threshold: " + defaultThreshold);
            }
         } catch (NumberFormatException var3) {
            System.err.println("Error setting new threshold:" + var3);
         }
      }

      UNCACHED = new SurfaceDataProxy(0) {
         public boolean isAccelerated() {
            return false;
         }

         public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
            return false;
         }

         public SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
            throw new InternalError("UNCACHED should never validate SDs");
         }

         public SurfaceData replaceData(SurfaceData var1, int var2, CompositeType var3, Color var4) {
            return var1;
         }
      };
   }

   public static class CountdownTracker implements StateTracker {
      private int countdown;

      public CountdownTracker(int var1) {
         this.countdown = var1;
      }

      public synchronized boolean isCurrent() {
         return --this.countdown >= 0;
      }
   }
}
