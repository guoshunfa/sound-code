package sun.java2d.pipe;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import sun.java2d.SunGraphics2D;

public class NullPipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, TextPipe, DrawImagePipe {
   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
   }

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
   }

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
   }

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
   }

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
   }

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
   }

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
   }

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
   }

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
   }

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
   }

   public void draw(SunGraphics2D var1, Shape var2) {
   }

   public void fill(SunGraphics2D var1, Shape var2) {
   }

   public void drawString(SunGraphics2D var1, String var2, double var3, double var5) {
   }

   public void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
   }

   public void drawChars(SunGraphics2D var1, char[] var2, int var3, int var4, int var5, int var6) {
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, Color var5, ImageObserver var6) {
      return false;
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, Color var9, ImageObserver var10) {
      return false;
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7, ImageObserver var8) {
      return false;
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11, ImageObserver var12) {
      return false;
   }

   public boolean transformImage(SunGraphics2D var1, Image var2, AffineTransform var3, ImageObserver var4) {
      return false;
   }

   public void transformImage(SunGraphics2D var1, BufferedImage var2, BufferedImageOp var3, int var4, int var5) {
   }
}
