package sun.font;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import sun.awt.SunToolkit;
import sun.java2d.xr.GrowableIntArray;
import sun.java2d.xr.MutableInteger;
import sun.java2d.xr.XRBackend;
import sun.java2d.xr.XRCompositeManager;

public class XRGlyphCache implements GlyphDisposedListener {
   XRBackend con;
   XRCompositeManager maskBuffer;
   HashMap<MutableInteger, XRGlyphCacheEntry> cacheMap = new HashMap(256);
   int nextID = 1;
   MutableInteger tmp = new MutableInteger(0);
   int grayGlyphSet;
   int lcdGlyphSet;
   int time = 0;
   int cachedPixels = 0;
   static final int MAX_CACHED_PIXELS = 100000;
   ArrayList<Integer> freeGlyphIDs = new ArrayList(255);
   static final boolean batchGlyphUpload = true;

   public XRGlyphCache(XRCompositeManager var1) {
      this.con = var1.getBackend();
      this.maskBuffer = var1;
      this.grayGlyphSet = this.con.XRenderCreateGlyphSet(2);
      this.lcdGlyphSet = this.con.XRenderCreateGlyphSet(0);
      StrikeCache.addGlyphDisposedListener(this);
   }

   public void glyphDisposed(ArrayList<Long> var1) {
      try {
         SunToolkit.awtLock();
         GrowableIntArray var2 = new GrowableIntArray(1, var1.size());
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            long var4 = (Long)var3.next();
            int var6 = XRGlyphCacheEntry.getGlyphID(var4);
            if (var6 != 0) {
               var2.addInt(var6);
            }
         }

         this.freeGlyphs(var2);
      } finally {
         SunToolkit.awtUnlock();
      }
   }

   protected int getFreeGlyphID() {
      if (this.freeGlyphIDs.size() > 0) {
         int var1 = (Integer)this.freeGlyphIDs.remove(this.freeGlyphIDs.size() - 1);
         return var1;
      } else {
         return this.nextID++;
      }
   }

   protected XRGlyphCacheEntry getEntryForPointer(long var1) {
      int var3 = XRGlyphCacheEntry.getGlyphID(var1);
      if (var3 == 0) {
         return null;
      } else {
         this.tmp.setValue(var3);
         return (XRGlyphCacheEntry)this.cacheMap.get(this.tmp);
      }
   }

   public XRGlyphCacheEntry[] cacheGlyphs(GlyphList var1) {
      ++this.time;
      XRGlyphCacheEntry[] var2 = new XRGlyphCacheEntry[var1.getNumGlyphs()];
      long[] var3 = var1.getImages();
      ArrayList var4 = null;

      for(int var5 = 0; var5 < var1.getNumGlyphs(); ++var5) {
         XRGlyphCacheEntry var6;
         if ((var6 = this.getEntryForPointer(var3[var5])) == null) {
            var6 = new XRGlyphCacheEntry(var3[var5], var1);
            var6.setGlyphID(this.getFreeGlyphID());
            this.cacheMap.put(new MutableInteger(var6.getGlyphID()), var6);
            if (var4 == null) {
               var4 = new ArrayList();
            }

            var4.add(var6);
         }

         var6.setLastUsed(this.time);
         var2[var5] = var6;
      }

      if (var4 != null) {
         this.uploadGlyphs(var2, var4, var1, (int[])null);
      }

      return var2;
   }

   protected void uploadGlyphs(XRGlyphCacheEntry[] var1, ArrayList<XRGlyphCacheEntry> var2, GlyphList var3, int[] var4) {
      XRGlyphCacheEntry var6;
      for(Iterator var5 = var2.iterator(); var5.hasNext(); this.cachedPixels += var6.getPixelCnt()) {
         var6 = (XRGlyphCacheEntry)var5.next();
      }

      if (this.cachedPixels > 100000) {
         this.clearCache(var1);
      }

      boolean var9 = this.containsLCDGlyphs(var2);
      List[] var10 = this.seperateGlyphTypes(var2, var9);
      List var7 = var10[0];
      List var8 = var10[1];
      if (var7 != null && var7.size() > 0) {
         this.con.XRenderAddGlyphs(this.grayGlyphSet, var3, var7, this.generateGlyphImageStream(var7));
      }

      if (var8 != null && var8.size() > 0) {
         this.con.XRenderAddGlyphs(this.lcdGlyphSet, var3, var8, this.generateGlyphImageStream(var8));
      }

   }

   protected List<XRGlyphCacheEntry>[] seperateGlyphTypes(List<XRGlyphCacheEntry> var1, boolean var2) {
      ArrayList var3 = null;
      ArrayList var4 = null;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         XRGlyphCacheEntry var6 = (XRGlyphCacheEntry)var5.next();
         if (var6.isGrayscale(var2)) {
            if (var4 == null) {
               var4 = new ArrayList(var1.size());
            }

            var6.setGlyphSet(this.grayGlyphSet);
            var4.add(var6);
         } else {
            if (var3 == null) {
               var3 = new ArrayList(var1.size());
            }

            var6.setGlyphSet(this.lcdGlyphSet);
            var3.add(var6);
         }
      }

      return new List[]{var4, var3};
   }

   protected byte[] generateGlyphImageStream(List<XRGlyphCacheEntry> var1) {
      boolean var2 = ((XRGlyphCacheEntry)var1.get(0)).getGlyphSet() == this.lcdGlyphSet;
      ByteArrayOutputStream var3 = new ByteArrayOutputStream((var2 ? 4 : 1) * 48 * var1.size());
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         XRGlyphCacheEntry var5 = (XRGlyphCacheEntry)var4.next();
         var5.writePixelData(var3, var2);
      }

      return var3.toByteArray();
   }

   protected boolean containsLCDGlyphs(List<XRGlyphCacheEntry> var1) {
      boolean var2 = false;
      Iterator var3 = var1.iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }

         XRGlyphCacheEntry var4 = (XRGlyphCacheEntry)var3.next();
         var2 = var4.getSourceRowBytes() != var4.getWidth();
      } while(!var2);

      return true;
   }

   protected void clearCache(XRGlyphCacheEntry[] var1) {
      ArrayList var2 = new ArrayList(this.cacheMap.values());
      Collections.sort(var2, new Comparator<XRGlyphCacheEntry>() {
         public int compare(XRGlyphCacheEntry var1, XRGlyphCacheEntry var2) {
            return var2.getLastUsed() - var1.getLastUsed();
         }
      });
      XRGlyphCacheEntry[] var3 = var1;
      int var4 = var1.length;

      int var5;
      XRGlyphCacheEntry var6;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         var6.setPinned();
      }

      GrowableIntArray var9 = new GrowableIntArray(1, 10);
      var4 = this.cachedPixels - 100000;

      for(var5 = var2.size() - 1; var5 >= 0 && var4 > 0; --var5) {
         var6 = (XRGlyphCacheEntry)var2.get(var5);
         if (!var6.isPinned()) {
            var4 -= var6.getPixelCnt();
            var9.addInt(var6.getGlyphID());
         }
      }

      XRGlyphCacheEntry[] var10 = var1;
      int var11 = var1.length;

      for(int var7 = 0; var7 < var11; ++var7) {
         XRGlyphCacheEntry var8 = var10[var7];
         var8.setUnpinned();
      }

      this.freeGlyphs(var9);
   }

   private void freeGlyphs(GrowableIntArray var1) {
      GrowableIntArray var2 = new GrowableIntArray(1, 10);
      GrowableIntArray var3 = new GrowableIntArray(1, 10);

      for(int var4 = 0; var4 < var1.getSize(); ++var4) {
         int var5 = var1.getInt(var4);
         this.freeGlyphIDs.add(var5);
         this.tmp.setValue(var5);
         XRGlyphCacheEntry var6 = (XRGlyphCacheEntry)this.cacheMap.get(this.tmp);
         this.cachedPixels -= var6.getPixelCnt();
         this.cacheMap.remove(this.tmp);
         if (var6.getGlyphSet() == this.grayGlyphSet) {
            var3.addInt(var5);
         } else {
            var2.addInt(var5);
         }

         var6.setGlyphID(0);
      }

      if (var3.getSize() > 0) {
         this.con.XRenderFreeGlyphs(this.grayGlyphSet, var3.getSizedArray());
      }

      if (var2.getSize() > 0) {
         this.con.XRenderFreeGlyphs(this.lcdGlyphSet, var2.getSizedArray());
      }

   }
}
