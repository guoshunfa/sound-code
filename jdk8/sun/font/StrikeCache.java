package sun.font;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import sun.java2d.Disposer;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.misc.Unsafe;

public final class StrikeCache {
   static final Unsafe unsafe = Unsafe.getUnsafe();
   static ReferenceQueue refQueue = Disposer.getQueue();
   static ArrayList<GlyphDisposedListener> disposeListeners = new ArrayList(1);
   static int MINSTRIKES = 8;
   static int recentStrikeIndex = 0;
   static FontStrike[] recentStrikes;
   static boolean cacheRefTypeWeak;
   static int nativeAddressSize;
   static int glyphInfoSize;
   static int xAdvanceOffset;
   static int yAdvanceOffset;
   static int boundsOffset;
   static int widthOffset;
   static int heightOffset;
   static int rowBytesOffset;
   static int topLeftXOffset;
   static int topLeftYOffset;
   static int pixelDataOffset;
   static int cacheCellOffset;
   static int managedOffset;
   static long invisibleGlyphPtr;

   static native void getGlyphCacheDescription(long[] var0);

   static void refStrike(FontStrike var0) {
      int var1 = recentStrikeIndex;
      recentStrikes[var1] = var0;
      ++var1;
      if (var1 == MINSTRIKES) {
         var1 = 0;
      }

      recentStrikeIndex = var1;
   }

   private static final void doDispose(FontStrikeDisposer var0) {
      if (var0.intGlyphImages != null) {
         freeCachedIntMemory(var0.intGlyphImages, var0.pScalerContext);
      } else if (var0.longGlyphImages != null) {
         freeCachedLongMemory(var0.longGlyphImages, var0.pScalerContext);
      } else {
         int var1;
         if (var0.segIntGlyphImages != null) {
            for(var1 = 0; var1 < var0.segIntGlyphImages.length; ++var1) {
               if (var0.segIntGlyphImages[var1] != null) {
                  freeCachedIntMemory(var0.segIntGlyphImages[var1], var0.pScalerContext);
                  var0.pScalerContext = 0L;
                  var0.segIntGlyphImages[var1] = null;
               }
            }

            if (var0.pScalerContext != 0L) {
               freeCachedIntMemory(new int[0], var0.pScalerContext);
            }
         } else if (var0.segLongGlyphImages != null) {
            for(var1 = 0; var1 < var0.segLongGlyphImages.length; ++var1) {
               if (var0.segLongGlyphImages[var1] != null) {
                  freeCachedLongMemory(var0.segLongGlyphImages[var1], var0.pScalerContext);
                  var0.pScalerContext = 0L;
                  var0.segLongGlyphImages[var1] = null;
               }
            }

            if (var0.pScalerContext != 0L) {
               freeCachedLongMemory(new long[0], var0.pScalerContext);
            }
         } else if (var0.pScalerContext != 0L) {
            if (longAddresses()) {
               freeCachedLongMemory(new long[0], var0.pScalerContext);
            } else {
               freeCachedIntMemory(new int[0], var0.pScalerContext);
            }
         }
      }

   }

   private static boolean longAddresses() {
      return nativeAddressSize == 8;
   }

   static void disposeStrike(final FontStrikeDisposer var0) {
      if (Disposer.pollingQueue) {
         doDispose(var0);
      } else {
         RenderQueue var1 = null;
         GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         if (!GraphicsEnvironment.isHeadless()) {
            GraphicsConfiguration var3 = var2.getDefaultScreenDevice().getDefaultConfiguration();
            if (var3 instanceof AccelGraphicsConfig) {
               AccelGraphicsConfig var4 = (AccelGraphicsConfig)var3;
               BufferedContext var5 = var4.getContext();
               if (var5 != null) {
                  var1 = var5.getRenderQueue();
               }
            }
         }

         if (var1 != null) {
            var1.lock();

            try {
               var1.flushAndInvokeNow(new Runnable() {
                  public void run() {
                     StrikeCache.doDispose(var0);
                     Disposer.pollRemove();
                  }
               });
            } finally {
               var1.unlock();
            }
         } else {
            doDispose(var0);
         }

      }
   }

   static native void freeIntPointer(int var0);

   static native void freeLongPointer(long var0);

   private static native void freeIntMemory(int[] var0, long var1);

   private static native void freeLongMemory(long[] var0, long var1);

   private static void freeCachedIntMemory(int[] var0, long var1) {
      synchronized(disposeListeners) {
         if (disposeListeners.size() > 0) {
            ArrayList var4 = null;

            for(int var5 = 0; var5 < var0.length; ++var5) {
               if (var0[var5] != 0 && unsafe.getByte((long)(var0[var5] + managedOffset)) == 0) {
                  if (var4 == null) {
                     var4 = new ArrayList();
                  }

                  var4.add((long)var0[var5]);
               }
            }

            if (var4 != null) {
               notifyDisposeListeners(var4);
            }
         }
      }

      freeIntMemory(var0, var1);
   }

   private static void freeCachedLongMemory(long[] var0, long var1) {
      synchronized(disposeListeners) {
         if (disposeListeners.size() > 0) {
            ArrayList var4 = null;

            for(int var5 = 0; var5 < var0.length; ++var5) {
               if (var0[var5] != 0L && unsafe.getByte(var0[var5] + (long)managedOffset) == 0) {
                  if (var4 == null) {
                     var4 = new ArrayList();
                  }

                  var4.add(var0[var5]);
               }
            }

            if (var4 != null) {
               notifyDisposeListeners(var4);
            }
         }
      }

      freeLongMemory(var0, var1);
   }

   public static void addGlyphDisposedListener(GlyphDisposedListener var0) {
      synchronized(disposeListeners) {
         disposeListeners.add(var0);
      }
   }

   private static void notifyDisposeListeners(ArrayList<Long> var0) {
      Iterator var1 = disposeListeners.iterator();

      while(var1.hasNext()) {
         GlyphDisposedListener var2 = (GlyphDisposedListener)var1.next();
         var2.glyphDisposed(var0);
      }

   }

   public static Reference getStrikeRef(FontStrike var0) {
      return getStrikeRef(var0, cacheRefTypeWeak);
   }

   public static Reference getStrikeRef(FontStrike var0, boolean var1) {
      if (var0.disposer == null) {
         return (Reference)(var1 ? new WeakReference(var0) : new SoftReference(var0));
      } else {
         return (Reference)(var1 ? new StrikeCache.WeakDisposerRef(var0) : new StrikeCache.SoftDisposerRef(var0));
      }
   }

   static {
      long[] var0 = new long[13];
      getGlyphCacheDescription(var0);
      nativeAddressSize = (int)var0[0];
      glyphInfoSize = (int)var0[1];
      xAdvanceOffset = (int)var0[2];
      yAdvanceOffset = (int)var0[3];
      widthOffset = (int)var0[4];
      heightOffset = (int)var0[5];
      rowBytesOffset = (int)var0[6];
      topLeftXOffset = (int)var0[7];
      topLeftYOffset = (int)var0[8];
      pixelDataOffset = (int)var0[9];
      invisibleGlyphPtr = var0[10];
      cacheCellOffset = (int)var0[11];
      managedOffset = (int)var0[12];
      if (nativeAddressSize < 4) {
         throw new InternalError("Unexpected address size for font data: " + nativeAddressSize);
      } else {
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               String var1 = System.getProperty("sun.java2d.font.reftype", "soft");
               StrikeCache.cacheRefTypeWeak = var1.equals("weak");
               String var2 = System.getProperty("sun.java2d.font.minstrikes");
               if (var2 != null) {
                  try {
                     StrikeCache.MINSTRIKES = Integer.parseInt(var2);
                     if (StrikeCache.MINSTRIKES <= 0) {
                        StrikeCache.MINSTRIKES = 1;
                     }
                  } catch (NumberFormatException var4) {
                  }
               }

               StrikeCache.recentStrikes = new FontStrike[StrikeCache.MINSTRIKES];
               return null;
            }
         });
      }
   }

   static class WeakDisposerRef extends WeakReference implements StrikeCache.DisposableStrike {
      private FontStrikeDisposer disposer;

      public FontStrikeDisposer getDisposer() {
         return this.disposer;
      }

      WeakDisposerRef(FontStrike var1) {
         super(var1, StrikeCache.refQueue);
         this.disposer = var1.disposer;
         Disposer.addReference(this, this.disposer);
      }
   }

   static class SoftDisposerRef extends SoftReference implements StrikeCache.DisposableStrike {
      private FontStrikeDisposer disposer;

      public FontStrikeDisposer getDisposer() {
         return this.disposer;
      }

      SoftDisposerRef(FontStrike var1) {
         super(var1, StrikeCache.refQueue);
         this.disposer = var1.disposer;
         Disposer.addReference(this, this.disposer);
      }
   }

   interface DisposableStrike {
      FontStrikeDisposer getDisposer();
   }
}
