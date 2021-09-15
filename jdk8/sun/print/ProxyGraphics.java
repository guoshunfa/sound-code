package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class ProxyGraphics extends Graphics {
   private Graphics g;

   public ProxyGraphics(Graphics var1) {
      this.g = var1;
   }

   Graphics getGraphics() {
      return this.g;
   }

   public Graphics create() {
      return new ProxyGraphics(this.g.create());
   }

   public Graphics create(int var1, int var2, int var3, int var4) {
      return new ProxyGraphics(this.g.create(var1, var2, var3, var4));
   }

   public void translate(int var1, int var2) {
      this.g.translate(var1, var2);
   }

   public Color getColor() {
      return this.g.getColor();
   }

   public void setColor(Color var1) {
      this.g.setColor(var1);
   }

   public void setPaintMode() {
      this.g.setPaintMode();
   }

   public void setXORMode(Color var1) {
      this.g.setXORMode(var1);
   }

   public Font getFont() {
      return this.g.getFont();
   }

   public void setFont(Font var1) {
      this.g.setFont(var1);
   }

   public FontMetrics getFontMetrics() {
      return this.g.getFontMetrics();
   }

   public FontMetrics getFontMetrics(Font var1) {
      return this.g.getFontMetrics(var1);
   }

   public Rectangle getClipBounds() {
      return this.g.getClipBounds();
   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      this.g.clipRect(var1, var2, var3, var4);
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      this.g.setClip(var1, var2, var3, var4);
   }

   public Shape getClip() {
      return this.g.getClip();
   }

   public void setClip(Shape var1) {
      this.g.setClip(var1);
   }

   public void copyArea(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.g.copyArea(var1, var2, var3, var4, var5, var6);
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      this.g.drawLine(var1, var2, var3, var4);
   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      this.g.fillRect(var1, var2, var3, var4);
   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      this.g.drawRect(var1, var2, var3, var4);
   }

   public void clearRect(int var1, int var2, int var3, int var4) {
      this.g.clearRect(var1, var2, var3, var4);
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.g.drawRoundRect(var1, var2, var3, var4, var5, var6);
   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.g.fillRoundRect(var1, var2, var3, var4, var5, var6);
   }

   public void draw3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      this.g.draw3DRect(var1, var2, var3, var4, var5);
   }

   public void fill3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      this.g.fill3DRect(var1, var2, var3, var4, var5);
   }

   public void drawOval(int var1, int var2, int var3, int var4) {
      this.g.drawOval(var1, var2, var3, var4);
   }

   public void fillOval(int var1, int var2, int var3, int var4) {
      this.g.fillOval(var1, var2, var3, var4);
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.g.drawArc(var1, var2, var3, var4, var5, var6);
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.g.fillArc(var1, var2, var3, var4, var5, var6);
   }

   public void drawPolyline(int[] var1, int[] var2, int var3) {
      this.g.drawPolyline(var1, var2, var3);
   }

   public void drawPolygon(int[] var1, int[] var2, int var3) {
      this.g.drawPolygon(var1, var2, var3);
   }

   public void drawPolygon(Polygon var1) {
      this.g.drawPolygon(var1);
   }

   public void fillPolygon(int[] var1, int[] var2, int var3) {
      this.g.fillPolygon(var1, var2, var3);
   }

   public void fillPolygon(Polygon var1) {
      this.g.fillPolygon(var1);
   }

   public void drawString(String var1, int var2, int var3) {
      this.g.drawString(var1, var2, var3);
   }

   public void drawString(AttributedCharacterIterator var1, int var2, int var3) {
      this.g.drawString(var1, var2, var3);
   }

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5) {
      this.g.drawChars(var1, var2, var3, var4, var5);
   }

   public void drawBytes(byte[] var1, int var2, int var3, int var4, int var5) {
      this.g.drawBytes(var1, var2, var3, var4, var5);
   }

   public boolean drawImage(Image var1, int var2, int var3, ImageObserver var4) {
      return this.g.drawImage(var1, var2, var3, var4);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6) {
      return this.g.drawImage(var1, var2, var3, var4, var5, var6);
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      return this.g.drawImage(var1, var2, var3, var4, var5);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      return this.g.drawImage(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10) {
      return this.g.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      return this.g.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void dispose() {
      this.g.dispose();
   }

   public void finalize() {
   }

   public String toString() {
      return this.getClass().getName() + "[font=" + this.getFont() + ",color=" + this.getColor() + "]";
   }

   /** @deprecated */
   @Deprecated
   public Rectangle getClipRect() {
      return this.g.getClipRect();
   }

   public boolean hitClip(int var1, int var2, int var3, int var4) {
      return this.g.hitClip(var1, var2, var3, var4);
   }

   public Rectangle getClipBounds(Rectangle var1) {
      return this.g.getClipBounds(var1);
   }
}
