package sun.swing;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.ListIterator;

public class ImageCache {
   private int maxCount;
   private final LinkedList<SoftReference<ImageCache.Entry>> entries;

   public ImageCache(int var1) {
      this.maxCount = var1;
      this.entries = new LinkedList();
   }

   void setMaxCount(int var1) {
      this.maxCount = var1;
   }

   public void flush() {
      this.entries.clear();
   }

   private ImageCache.Entry getEntry(Object var1, GraphicsConfiguration var2, int var3, int var4, Object[] var5) {
      ListIterator var7 = this.entries.listIterator();

      ImageCache.Entry var6;
      while(var7.hasNext()) {
         SoftReference var8 = (SoftReference)var7.next();
         var6 = (ImageCache.Entry)var8.get();
         if (var6 == null) {
            var7.remove();
         } else if (var6.equals(var2, var3, var4, var5)) {
            var7.remove();
            this.entries.addFirst(var8);
            return var6;
         }
      }

      var6 = new ImageCache.Entry(var2, var3, var4, var5);
      if (this.entries.size() >= this.maxCount) {
         this.entries.removeLast();
      }

      this.entries.addFirst(new SoftReference(var6));
      return var6;
   }

   public Image getImage(Object var1, GraphicsConfiguration var2, int var3, int var4, Object[] var5) {
      ImageCache.Entry var6 = this.getEntry(var1, var2, var3, var4, var5);
      return var6.getImage();
   }

   public void setImage(Object var1, GraphicsConfiguration var2, int var3, int var4, Object[] var5, Image var6) {
      ImageCache.Entry var7 = this.getEntry(var1, var2, var3, var4, var5);
      var7.setImage(var6);
   }

   private static class Entry {
      private final GraphicsConfiguration config;
      private final int w;
      private final int h;
      private final Object[] args;
      private Image image;

      Entry(GraphicsConfiguration var1, int var2, int var3, Object[] var4) {
         this.config = var1;
         this.args = var4;
         this.w = var2;
         this.h = var3;
      }

      public void setImage(Image var1) {
         this.image = var1;
      }

      public Image getImage() {
         return this.image;
      }

      public String toString() {
         String var1 = super.toString() + "[ graphicsConfig=" + this.config + ", image=" + this.image + ", w=" + this.w + ", h=" + this.h;
         if (this.args != null) {
            for(int var2 = 0; var2 < this.args.length; ++var2) {
               var1 = var1 + ", " + this.args[var2];
            }
         }

         var1 = var1 + "]";
         return var1;
      }

      public boolean equals(GraphicsConfiguration var1, int var2, int var3, Object[] var4) {
         if (this.w == var2 && this.h == var3 && (this.config != null && this.config.equals(var1) || this.config == null && var1 == null)) {
            if (this.args == null && var4 == null) {
               return true;
            }

            if (this.args != null && var4 != null && this.args.length == var4.length) {
               for(int var5 = var4.length - 1; var5 >= 0; --var5) {
                  Object var6 = this.args[var5];
                  Object var7 = var4[var5];
                  if (var6 == null && var7 != null || var6 != null && !var6.equals(var7)) {
                     return false;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }
}
