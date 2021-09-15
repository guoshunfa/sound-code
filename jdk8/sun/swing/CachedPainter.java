package sun.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.util.HashMap;
import java.util.Map;

public abstract class CachedPainter {
   private static final Map<Object, ImageCache> cacheMap = new HashMap();

   private static ImageCache getCache(Object var0) {
      Class var1 = CachedPainter.class;
      synchronized(CachedPainter.class) {
         ImageCache var2 = (ImageCache)cacheMap.get(var0);
         if (var2 == null) {
            var2 = new ImageCache(1);
            cacheMap.put(var0, var2);
         }

         return var2;
      }
   }

   public CachedPainter(int var1) {
      getCache(this.getClass()).setMaxCount(var1);
   }

   public void paint(Component var1, Graphics var2, int var3, int var4, int var5, int var6, Object... var7) {
      if (var5 > 0 && var6 > 0) {
         Class var8 = CachedPainter.class;
         synchronized(CachedPainter.class) {
            this.paint0(var1, var2, var3, var4, var5, var6, var7);
         }
      }
   }

   private void paint0(Component var1, Graphics var2, int var3, int var4, int var5, int var6, Object... var7) {
      Class var8 = this.getClass();
      GraphicsConfiguration var9 = this.getGraphicsConfiguration(var1);
      ImageCache var10 = getCache(var8);
      Image var11 = var10.getImage(var8, var9, var5, var6, var7);
      int var12 = 0;

      do {
         boolean var13 = false;
         if (var11 instanceof VolatileImage) {
            switch(((VolatileImage)var11).validate(var9)) {
            case 1:
               var13 = true;
               break;
            case 2:
               ((VolatileImage)var11).flush();
               var11 = null;
            }
         }

         if (var11 == null) {
            var11 = this.createImage(var1, var5, var6, var9, var7);
            var10.setImage(var8, var9, var5, var6, var7, var11);
            var13 = true;
         }

         if (var13) {
            Graphics var14 = var11.getGraphics();
            this.paintToImage(var1, var11, var14, var5, var6, var7);
            var14.dispose();
         }

         this.paintImage(var1, var2, var3, var4, var5, var6, var11, var7);
         if (!(var11 instanceof VolatileImage) || !((VolatileImage)var11).contentsLost()) {
            break;
         }

         ++var12;
      } while(var12 < 3);

   }

   protected abstract void paintToImage(Component var1, Image var2, Graphics var3, int var4, int var5, Object[] var6);

   protected void paintImage(Component var1, Graphics var2, int var3, int var4, int var5, int var6, Image var7, Object[] var8) {
      var2.drawImage(var7, var3, var4, (ImageObserver)null);
   }

   protected Image createImage(Component var1, int var2, int var3, GraphicsConfiguration var4, Object[] var5) {
      return (Image)(var4 == null ? new BufferedImage(var2, var3, 1) : var4.createCompatibleVolatileImage(var2, var3));
   }

   protected void flush() {
      Class var1 = CachedPainter.class;
      synchronized(CachedPainter.class) {
         getCache(this.getClass()).flush();
      }
   }

   private GraphicsConfiguration getGraphicsConfiguration(Component var1) {
      return var1 == null ? null : var1.getGraphicsConfiguration();
   }
}
