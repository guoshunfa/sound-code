package java.awt;

import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public abstract class Graphics {
   protected Graphics() {
   }

   public abstract Graphics create();

   public Graphics create(int var1, int var2, int var3, int var4) {
      Graphics var5 = this.create();
      if (var5 == null) {
         return null;
      } else {
         var5.translate(var1, var2);
         var5.clipRect(0, 0, var3, var4);
         return var5;
      }
   }

   public abstract void translate(int var1, int var2);

   public abstract Color getColor();

   public abstract void setColor(Color var1);

   public abstract void setPaintMode();

   public abstract void setXORMode(Color var1);

   public abstract Font getFont();

   public abstract void setFont(Font var1);

   public FontMetrics getFontMetrics() {
      return this.getFontMetrics(this.getFont());
   }

   public abstract FontMetrics getFontMetrics(Font var1);

   public abstract Rectangle getClipBounds();

   public abstract void clipRect(int var1, int var2, int var3, int var4);

   public abstract void setClip(int var1, int var2, int var3, int var4);

   public abstract Shape getClip();

   public abstract void setClip(Shape var1);

   public abstract void copyArea(int var1, int var2, int var3, int var4, int var5, int var6);

   public abstract void drawLine(int var1, int var2, int var3, int var4);

   public abstract void fillRect(int var1, int var2, int var3, int var4);

   public void drawRect(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         if (var4 != 0 && var3 != 0) {
            this.drawLine(var1, var2, var1 + var3 - 1, var2);
            this.drawLine(var1 + var3, var2, var1 + var3, var2 + var4 - 1);
            this.drawLine(var1 + var3, var2 + var4, var1 + 1, var2 + var4);
            this.drawLine(var1, var2 + var4, var1, var2 + 1);
         } else {
            this.drawLine(var1, var2, var1 + var3, var2 + var4);
         }

      }
   }

   public abstract void clearRect(int var1, int var2, int var3, int var4);

   public abstract void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public abstract void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public void draw3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      Color var6 = this.getColor();
      Color var7 = var6.brighter();
      Color var8 = var6.darker();
      this.setColor(var5 ? var7 : var8);
      this.drawLine(var1, var2, var1, var2 + var4);
      this.drawLine(var1 + 1, var2, var1 + var3 - 1, var2);
      this.setColor(var5 ? var8 : var7);
      this.drawLine(var1 + 1, var2 + var4, var1 + var3, var2 + var4);
      this.drawLine(var1 + var3, var2, var1 + var3, var2 + var4 - 1);
      this.setColor(var6);
   }

   public void fill3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      Color var6 = this.getColor();
      Color var7 = var6.brighter();
      Color var8 = var6.darker();
      if (!var5) {
         this.setColor(var8);
      }

      this.fillRect(var1 + 1, var2 + 1, var3 - 2, var4 - 2);
      this.setColor(var5 ? var7 : var8);
      this.drawLine(var1, var2, var1, var2 + var4 - 1);
      this.drawLine(var1 + 1, var2, var1 + var3 - 2, var2);
      this.setColor(var5 ? var8 : var7);
      this.drawLine(var1 + 1, var2 + var4 - 1, var1 + var3 - 1, var2 + var4 - 1);
      this.drawLine(var1 + var3 - 1, var2, var1 + var3 - 1, var2 + var4 - 2);
      this.setColor(var6);
   }

   public abstract void drawOval(int var1, int var2, int var3, int var4);

   public abstract void fillOval(int var1, int var2, int var3, int var4);

   public abstract void drawArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public abstract void fillArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public abstract void drawPolyline(int[] var1, int[] var2, int var3);

   public abstract void drawPolygon(int[] var1, int[] var2, int var3);

   public void drawPolygon(Polygon var1) {
      this.drawPolygon(var1.xpoints, var1.ypoints, var1.npoints);
   }

   public abstract void fillPolygon(int[] var1, int[] var2, int var3);

   public void fillPolygon(Polygon var1) {
      this.fillPolygon(var1.xpoints, var1.ypoints, var1.npoints);
   }

   public abstract void drawString(String var1, int var2, int var3);

   public abstract void drawString(AttributedCharacterIterator var1, int var2, int var3);

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5) {
      this.drawString(new String(var1, var2, var3), var4, var5);
   }

   public void drawBytes(byte[] var1, int var2, int var3, int var4, int var5) {
      this.drawString(new String(var1, 0, var2, var3), var4, var5);
   }

   public abstract boolean drawImage(Image var1, int var2, int var3, ImageObserver var4);

   public abstract boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6);

   public abstract boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5);

   public abstract boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7);

   public abstract boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10);

   public abstract boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11);

   public abstract void dispose();

   public void finalize() {
      this.dispose();
   }

   public String toString() {
      return this.getClass().getName() + "[font=" + this.getFont() + ",color=" + this.getColor() + "]";
   }

   /** @deprecated */
   @Deprecated
   public Rectangle getClipRect() {
      return this.getClipBounds();
   }

   public boolean hitClip(int var1, int var2, int var3, int var4) {
      Rectangle var5 = this.getClipBounds();
      return var5 == null ? true : var5.intersects((double)var1, (double)var2, (double)var3, (double)var4);
   }

   public Rectangle getClipBounds(Rectangle var1) {
      Rectangle var2 = this.getClipBounds();
      if (var2 != null) {
         var1.x = var2.x;
         var1.y = var2.y;
         var1.width = var2.width;
         var1.height = var2.height;
      } else if (var1 == null) {
         throw new NullPointerException("null rectangle parameter");
      }

      return var1;
   }
}
