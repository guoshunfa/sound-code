package javax.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;

public class DebugGraphics extends Graphics {
   Graphics graphics;
   Image buffer;
   int debugOptions;
   int graphicsID;
   int xOffset;
   int yOffset;
   private static int graphicsCount = 0;
   private static ImageIcon imageLoadingIcon = new ImageIcon();
   public static final int LOG_OPTION = 1;
   public static final int FLASH_OPTION = 2;
   public static final int BUFFERED_OPTION = 4;
   public static final int NONE_OPTION = -1;
   private static final Class debugGraphicsInfoKey;

   public DebugGraphics() {
      this.graphicsID = graphicsCount++;
      this.buffer = null;
      this.xOffset = this.yOffset = 0;
   }

   public DebugGraphics(Graphics var1, JComponent var2) {
      this(var1);
      this.setDebugOptions(var2.shouldDebugGraphics());
   }

   public DebugGraphics(Graphics var1) {
      this();
      this.graphics = var1;
   }

   public Graphics create() {
      DebugGraphics var1 = new DebugGraphics();
      var1.graphics = this.graphics.create();
      var1.debugOptions = this.debugOptions;
      var1.buffer = this.buffer;
      return var1;
   }

   public Graphics create(int var1, int var2, int var3, int var4) {
      DebugGraphics var5 = new DebugGraphics();
      var5.graphics = this.graphics.create(var1, var2, var3, var4);
      var5.debugOptions = this.debugOptions;
      var5.buffer = this.buffer;
      var5.xOffset = this.xOffset + var1;
      var5.yOffset = this.yOffset + var2;
      return var5;
   }

   public static void setFlashColor(Color var0) {
      info().flashColor = var0;
   }

   public static Color flashColor() {
      return info().flashColor;
   }

   public static void setFlashTime(int var0) {
      info().flashTime = var0;
   }

   public static int flashTime() {
      return info().flashTime;
   }

   public static void setFlashCount(int var0) {
      info().flashCount = var0;
   }

   public static int flashCount() {
      return info().flashCount;
   }

   public static void setLogStream(PrintStream var0) {
      info().stream = var0;
   }

   public static PrintStream logStream() {
      return info().stream;
   }

   public void setFont(Font var1) {
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting font: " + var1);
      }

      this.graphics.setFont(var1);
   }

   public Font getFont() {
      return this.graphics.getFont();
   }

   public void setColor(Color var1) {
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting color: " + var1);
      }

      this.graphics.setColor(var1);
   }

   public Color getColor() {
      return this.graphics.getColor();
   }

   public FontMetrics getFontMetrics() {
      return this.graphics.getFontMetrics();
   }

   public FontMetrics getFontMetrics(Font var1) {
      return this.graphics.getFontMetrics(var1);
   }

   public void translate(int var1, int var2) {
      if (this.debugLog()) {
         info().log(this.toShortString() + " Translating by: " + new Point(var1, var2));
      }

      this.xOffset += var1;
      this.yOffset += var2;
      this.graphics.translate(var1, var2);
   }

   public void setPaintMode() {
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting paint mode");
      }

      this.graphics.setPaintMode();
   }

   public void setXORMode(Color var1) {
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting XOR mode: " + var1);
      }

      this.graphics.setXORMode(var1);
   }

   public Rectangle getClipBounds() {
      return this.graphics.getClipBounds();
   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      this.graphics.clipRect(var1, var2, var3, var4);
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting clipRect: " + new Rectangle(var1, var2, var3, var4) + " New clipRect: " + this.graphics.getClip());
      }

   }

   public void setClip(int var1, int var2, int var3, int var4) {
      this.graphics.setClip(var1, var2, var3, var4);
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting new clipRect: " + this.graphics.getClip());
      }

   }

   public Shape getClip() {
      return this.graphics.getClip();
   }

   public void setClip(Shape var1) {
      this.graphics.setClip(var1);
      if (this.debugLog()) {
         info().log(this.toShortString() + " Setting new clipRect: " + this.graphics.getClip());
      }

   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing rect: " + new Rectangle(var1, var2, var3, var4));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.drawRect(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         Color var9 = this.getColor();
         int var8 = var5.flashCount * 2 - 1;

         for(int var7 = 0; var7 < var8; ++var7) {
            this.graphics.setColor(var7 % 2 == 0 ? var5.flashColor : var9);
            this.graphics.drawRect(var1, var2, var3, var4);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }

         this.graphics.setColor(var9);
      }

      this.graphics.drawRect(var1, var2, var3, var4);
   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Filling rect: " + new Rectangle(var1, var2, var3, var4));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.fillRect(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         Color var9 = this.getColor();
         int var8 = var5.flashCount * 2 - 1;

         for(int var7 = 0; var7 < var8; ++var7) {
            this.graphics.setColor(var7 % 2 == 0 ? var5.flashColor : var9);
            this.graphics.fillRect(var1, var2, var3, var4);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }

         this.graphics.setColor(var9);
      }

      this.graphics.fillRect(var1, var2, var3, var4);
   }

   public void clearRect(int var1, int var2, int var3, int var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Clearing rect: " + new Rectangle(var1, var2, var3, var4));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.clearRect(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         Color var9 = this.getColor();
         int var8 = var5.flashCount * 2 - 1;

         for(int var7 = 0; var7 < var8; ++var7) {
            this.graphics.setColor(var7 % 2 == 0 ? var5.flashColor : var9);
            this.graphics.clearRect(var1, var2, var3, var4);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }

         this.graphics.setColor(var9);
      }

      this.graphics.clearRect(var1, var2, var3, var4);
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      DebugGraphicsInfo var7 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing round rect: " + new Rectangle(var1, var2, var3, var4) + " arcWidth: " + var5 + " archHeight: " + var6);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.drawRoundRect(var1, var2, var3, var4, var5, var6);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         Color var11 = this.getColor();
         int var10 = var7.flashCount * 2 - 1;

         for(int var9 = 0; var9 < var10; ++var9) {
            this.graphics.setColor(var9 % 2 == 0 ? var7.flashColor : var11);
            this.graphics.drawRoundRect(var1, var2, var3, var4, var5, var6);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var7.flashTime);
         }

         this.graphics.setColor(var11);
      }

      this.graphics.drawRoundRect(var1, var2, var3, var4, var5, var6);
   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      DebugGraphicsInfo var7 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Filling round rect: " + new Rectangle(var1, var2, var3, var4) + " arcWidth: " + var5 + " archHeight: " + var6);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.fillRoundRect(var1, var2, var3, var4, var5, var6);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         Color var11 = this.getColor();
         int var10 = var7.flashCount * 2 - 1;

         for(int var9 = 0; var9 < var10; ++var9) {
            this.graphics.setColor(var9 % 2 == 0 ? var7.flashColor : var11);
            this.graphics.fillRoundRect(var1, var2, var3, var4, var5, var6);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var7.flashTime);
         }

         this.graphics.setColor(var11);
      }

      this.graphics.fillRoundRect(var1, var2, var3, var4, var5, var6);
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing line: from " + this.pointToString(var1, var2) + " to " + this.pointToString(var3, var4));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.drawLine(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         Color var9 = this.getColor();
         int var8 = var5.flashCount * 2 - 1;

         for(int var7 = 0; var7 < var8; ++var7) {
            this.graphics.setColor(var7 % 2 == 0 ? var5.flashColor : var9);
            this.graphics.drawLine(var1, var2, var3, var4);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }

         this.graphics.setColor(var9);
      }

      this.graphics.drawLine(var1, var2, var3, var4);
   }

   public void draw3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      DebugGraphicsInfo var6 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing 3D rect: " + new Rectangle(var1, var2, var3, var4) + " Raised bezel: " + var5);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var7 = this.debugGraphics();
            var7.draw3DRect(var1, var2, var3, var4, var5);
            var7.dispose();
         }
      } else if (this.debugFlash()) {
         Color var10 = this.getColor();
         int var9 = var6.flashCount * 2 - 1;

         for(int var8 = 0; var8 < var9; ++var8) {
            this.graphics.setColor(var8 % 2 == 0 ? var6.flashColor : var10);
            this.graphics.draw3DRect(var1, var2, var3, var4, var5);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var6.flashTime);
         }

         this.graphics.setColor(var10);
      }

      this.graphics.draw3DRect(var1, var2, var3, var4, var5);
   }

   public void fill3DRect(int var1, int var2, int var3, int var4, boolean var5) {
      DebugGraphicsInfo var6 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Filling 3D rect: " + new Rectangle(var1, var2, var3, var4) + " Raised bezel: " + var5);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var7 = this.debugGraphics();
            var7.fill3DRect(var1, var2, var3, var4, var5);
            var7.dispose();
         }
      } else if (this.debugFlash()) {
         Color var10 = this.getColor();
         int var9 = var6.flashCount * 2 - 1;

         for(int var8 = 0; var8 < var9; ++var8) {
            this.graphics.setColor(var8 % 2 == 0 ? var6.flashColor : var10);
            this.graphics.fill3DRect(var1, var2, var3, var4, var5);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var6.flashTime);
         }

         this.graphics.setColor(var10);
      }

      this.graphics.fill3DRect(var1, var2, var3, var4, var5);
   }

   public void drawOval(int var1, int var2, int var3, int var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing oval: " + new Rectangle(var1, var2, var3, var4));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.drawOval(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         Color var9 = this.getColor();
         int var8 = var5.flashCount * 2 - 1;

         for(int var7 = 0; var7 < var8; ++var7) {
            this.graphics.setColor(var7 % 2 == 0 ? var5.flashColor : var9);
            this.graphics.drawOval(var1, var2, var3, var4);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }

         this.graphics.setColor(var9);
      }

      this.graphics.drawOval(var1, var2, var3, var4);
   }

   public void fillOval(int var1, int var2, int var3, int var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Filling oval: " + new Rectangle(var1, var2, var3, var4));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.fillOval(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         Color var9 = this.getColor();
         int var8 = var5.flashCount * 2 - 1;

         for(int var7 = 0; var7 < var8; ++var7) {
            this.graphics.setColor(var7 % 2 == 0 ? var5.flashColor : var9);
            this.graphics.fillOval(var1, var2, var3, var4);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }

         this.graphics.setColor(var9);
      }

      this.graphics.fillOval(var1, var2, var3, var4);
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      DebugGraphicsInfo var7 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing arc: " + new Rectangle(var1, var2, var3, var4) + " startAngle: " + var5 + " arcAngle: " + var6);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.drawArc(var1, var2, var3, var4, var5, var6);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         Color var11 = this.getColor();
         int var10 = var7.flashCount * 2 - 1;

         for(int var9 = 0; var9 < var10; ++var9) {
            this.graphics.setColor(var9 % 2 == 0 ? var7.flashColor : var11);
            this.graphics.drawArc(var1, var2, var3, var4, var5, var6);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var7.flashTime);
         }

         this.graphics.setColor(var11);
      }

      this.graphics.drawArc(var1, var2, var3, var4, var5, var6);
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      DebugGraphicsInfo var7 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Filling arc: " + new Rectangle(var1, var2, var3, var4) + " startAngle: " + var5 + " arcAngle: " + var6);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.fillArc(var1, var2, var3, var4, var5, var6);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         Color var11 = this.getColor();
         int var10 = var7.flashCount * 2 - 1;

         for(int var9 = 0; var9 < var10; ++var9) {
            this.graphics.setColor(var9 % 2 == 0 ? var7.flashColor : var11);
            this.graphics.fillArc(var1, var2, var3, var4, var5, var6);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var7.flashTime);
         }

         this.graphics.setColor(var11);
      }

      this.graphics.fillArc(var1, var2, var3, var4, var5, var6);
   }

   public void drawPolyline(int[] var1, int[] var2, int var3) {
      DebugGraphicsInfo var4 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing polyline:  nPoints: " + var3 + " X's: " + var1 + " Y's: " + var2);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var5 = this.debugGraphics();
            var5.drawPolyline(var1, var2, var3);
            var5.dispose();
         }
      } else if (this.debugFlash()) {
         Color var8 = this.getColor();
         int var7 = var4.flashCount * 2 - 1;

         for(int var6 = 0; var6 < var7; ++var6) {
            this.graphics.setColor(var6 % 2 == 0 ? var4.flashColor : var8);
            this.graphics.drawPolyline(var1, var2, var3);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var4.flashTime);
         }

         this.graphics.setColor(var8);
      }

      this.graphics.drawPolyline(var1, var2, var3);
   }

   public void drawPolygon(int[] var1, int[] var2, int var3) {
      DebugGraphicsInfo var4 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing polygon:  nPoints: " + var3 + " X's: " + var1 + " Y's: " + var2);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var5 = this.debugGraphics();
            var5.drawPolygon(var1, var2, var3);
            var5.dispose();
         }
      } else if (this.debugFlash()) {
         Color var8 = this.getColor();
         int var7 = var4.flashCount * 2 - 1;

         for(int var6 = 0; var6 < var7; ++var6) {
            this.graphics.setColor(var6 % 2 == 0 ? var4.flashColor : var8);
            this.graphics.drawPolygon(var1, var2, var3);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var4.flashTime);
         }

         this.graphics.setColor(var8);
      }

      this.graphics.drawPolygon(var1, var2, var3);
   }

   public void fillPolygon(int[] var1, int[] var2, int var3) {
      DebugGraphicsInfo var4 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Filling polygon:  nPoints: " + var3 + " X's: " + var1 + " Y's: " + var2);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var5 = this.debugGraphics();
            var5.fillPolygon(var1, var2, var3);
            var5.dispose();
         }
      } else if (this.debugFlash()) {
         Color var8 = this.getColor();
         int var7 = var4.flashCount * 2 - 1;

         for(int var6 = 0; var6 < var7; ++var6) {
            this.graphics.setColor(var6 % 2 == 0 ? var4.flashColor : var8);
            this.graphics.fillPolygon(var1, var2, var3);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var4.flashTime);
         }

         this.graphics.setColor(var8);
      }

      this.graphics.fillPolygon(var1, var2, var3);
   }

   public void drawString(String var1, int var2, int var3) {
      DebugGraphicsInfo var4 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing string: \"" + var1 + "\" at: " + new Point(var2, var3));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var5 = this.debugGraphics();
            var5.drawString(var1, var2, var3);
            var5.dispose();
         }
      } else if (this.debugFlash()) {
         Color var8 = this.getColor();
         int var7 = var4.flashCount * 2 - 1;

         for(int var6 = 0; var6 < var7; ++var6) {
            this.graphics.setColor(var6 % 2 == 0 ? var4.flashColor : var8);
            this.graphics.drawString(var1, var2, var3);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var4.flashTime);
         }

         this.graphics.setColor(var8);
      }

      this.graphics.drawString(var1, var2, var3);
   }

   public void drawString(AttributedCharacterIterator var1, int var2, int var3) {
      DebugGraphicsInfo var4 = info();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing text: \"" + var1 + "\" at: " + new Point(var2, var3));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var5 = this.debugGraphics();
            var5.drawString(var1, var2, var3);
            var5.dispose();
         }
      } else if (this.debugFlash()) {
         Color var8 = this.getColor();
         int var7 = var4.flashCount * 2 - 1;

         for(int var6 = 0; var6 < var7; ++var6) {
            this.graphics.setColor(var6 % 2 == 0 ? var4.flashColor : var8);
            this.graphics.drawString(var1, var2, var3);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var4.flashTime);
         }

         this.graphics.setColor(var8);
      }

      this.graphics.drawString(var1, var2, var3);
   }

   public void drawBytes(byte[] var1, int var2, int var3, int var4, int var5) {
      DebugGraphicsInfo var6 = info();
      Font var7 = this.graphics.getFont();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing bytes at: " + new Point(var4, var5));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.drawBytes(var1, var2, var3, var4, var5);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         Color var11 = this.getColor();
         int var10 = var6.flashCount * 2 - 1;

         for(int var9 = 0; var9 < var10; ++var9) {
            this.graphics.setColor(var9 % 2 == 0 ? var6.flashColor : var11);
            this.graphics.drawBytes(var1, var2, var3, var4, var5);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var6.flashTime);
         }

         this.graphics.setColor(var11);
      }

      this.graphics.drawBytes(var1, var2, var3, var4, var5);
   }

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5) {
      DebugGraphicsInfo var6 = info();
      Font var7 = this.graphics.getFont();
      if (this.debugLog()) {
         info().log(this.toShortString() + " Drawing chars at " + new Point(var4, var5));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.drawChars(var1, var2, var3, var4, var5);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         Color var11 = this.getColor();
         int var10 = var6.flashCount * 2 - 1;

         for(int var9 = 0; var9 < var10; ++var9) {
            this.graphics.setColor(var9 % 2 == 0 ? var6.flashColor : var11);
            this.graphics.drawChars(var1, var2, var3, var4, var5);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var6.flashTime);
         }

         this.graphics.setColor(var11);
      }

      this.graphics.drawChars(var1, var2, var3, var4, var5);
   }

   public boolean drawImage(Image var1, int var2, int var3, ImageObserver var4) {
      DebugGraphicsInfo var5 = info();
      if (this.debugLog()) {
         var5.log(this.toShortString() + " Drawing image: " + var1 + " at: " + new Point(var2, var3));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var6 = this.debugGraphics();
            var6.drawImage(var1, var2, var3, var4);
            var6.dispose();
         }
      } else if (this.debugFlash()) {
         int var7 = var5.flashCount * 2 - 1;
         ImageProducer var8 = var1.getSource();
         FilteredImageSource var9 = new FilteredImageSource(var8, new DebugGraphicsFilter(var5.flashColor));
         Image var10 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var9);
         DebugGraphicsObserver var11 = new DebugGraphicsObserver();

         for(int var13 = 0; var13 < var7; ++var13) {
            Image var12 = var13 % 2 == 0 ? var10 : var1;
            loadImage(var12);
            this.graphics.drawImage(var12, var2, var3, var11);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var5.flashTime);
         }
      }

      return this.graphics.drawImage(var1, var2, var3, var4);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6) {
      DebugGraphicsInfo var7 = info();
      if (this.debugLog()) {
         var7.log(this.toShortString() + " Drawing image: " + var1 + " at: " + new Rectangle(var2, var3, var4, var5));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var8 = this.debugGraphics();
            var8.drawImage(var1, var2, var3, var4, var5, var6);
            var8.dispose();
         }
      } else if (this.debugFlash()) {
         int var9 = var7.flashCount * 2 - 1;
         ImageProducer var10 = var1.getSource();
         FilteredImageSource var11 = new FilteredImageSource(var10, new DebugGraphicsFilter(var7.flashColor));
         Image var12 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var11);
         DebugGraphicsObserver var13 = new DebugGraphicsObserver();

         for(int var15 = 0; var15 < var9; ++var15) {
            Image var14 = var15 % 2 == 0 ? var12 : var1;
            loadImage(var14);
            this.graphics.drawImage(var14, var2, var3, var4, var5, var13);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var7.flashTime);
         }
      }

      return this.graphics.drawImage(var1, var2, var3, var4, var5, var6);
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      DebugGraphicsInfo var6 = info();
      if (this.debugLog()) {
         var6.log(this.toShortString() + " Drawing image: " + var1 + " at: " + new Point(var2, var3) + ", bgcolor: " + var4);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var7 = this.debugGraphics();
            var7.drawImage(var1, var2, var3, var4, var5);
            var7.dispose();
         }
      } else if (this.debugFlash()) {
         int var8 = var6.flashCount * 2 - 1;
         ImageProducer var9 = var1.getSource();
         FilteredImageSource var10 = new FilteredImageSource(var9, new DebugGraphicsFilter(var6.flashColor));
         Image var11 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var10);
         DebugGraphicsObserver var12 = new DebugGraphicsObserver();

         for(int var14 = 0; var14 < var8; ++var14) {
            Image var13 = var14 % 2 == 0 ? var11 : var1;
            loadImage(var13);
            this.graphics.drawImage(var13, var2, var3, var4, var12);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var6.flashTime);
         }
      }

      return this.graphics.drawImage(var1, var2, var3, var4, var5);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      DebugGraphicsInfo var8 = info();
      if (this.debugLog()) {
         var8.log(this.toShortString() + " Drawing image: " + var1 + " at: " + new Rectangle(var2, var3, var4, var5) + ", bgcolor: " + var6);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var9 = this.debugGraphics();
            var9.drawImage(var1, var2, var3, var4, var5, var6, var7);
            var9.dispose();
         }
      } else if (this.debugFlash()) {
         int var10 = var8.flashCount * 2 - 1;
         ImageProducer var11 = var1.getSource();
         FilteredImageSource var12 = new FilteredImageSource(var11, new DebugGraphicsFilter(var8.flashColor));
         Image var13 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var12);
         DebugGraphicsObserver var14 = new DebugGraphicsObserver();

         for(int var16 = 0; var16 < var10; ++var16) {
            Image var15 = var16 % 2 == 0 ? var13 : var1;
            loadImage(var15);
            this.graphics.drawImage(var15, var2, var3, var4, var5, var6, var14);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var8.flashTime);
         }
      }

      return this.graphics.drawImage(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10) {
      DebugGraphicsInfo var11 = info();
      if (this.debugLog()) {
         var11.log(this.toShortString() + " Drawing image: " + var1 + " destination: " + new Rectangle(var2, var3, var4, var5) + " source: " + new Rectangle(var6, var7, var8, var9));
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var12 = this.debugGraphics();
            var12.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            var12.dispose();
         }
      } else if (this.debugFlash()) {
         int var13 = var11.flashCount * 2 - 1;
         ImageProducer var14 = var1.getSource();
         FilteredImageSource var15 = new FilteredImageSource(var14, new DebugGraphicsFilter(var11.flashColor));
         Image var16 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var15);
         DebugGraphicsObserver var17 = new DebugGraphicsObserver();

         for(int var19 = 0; var19 < var13; ++var19) {
            Image var18 = var19 % 2 == 0 ? var16 : var1;
            loadImage(var18);
            this.graphics.drawImage(var18, var2, var3, var4, var5, var6, var7, var8, var9, var17);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var11.flashTime);
         }
      }

      return this.graphics.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      DebugGraphicsInfo var12 = info();
      if (this.debugLog()) {
         var12.log(this.toShortString() + " Drawing image: " + var1 + " destination: " + new Rectangle(var2, var3, var4, var5) + " source: " + new Rectangle(var6, var7, var8, var9) + ", bgcolor: " + var10);
      }

      if (this.isDrawingBuffer()) {
         if (this.debugBuffered()) {
            Graphics var13 = this.debugGraphics();
            var13.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
            var13.dispose();
         }
      } else if (this.debugFlash()) {
         int var14 = var12.flashCount * 2 - 1;
         ImageProducer var15 = var1.getSource();
         FilteredImageSource var16 = new FilteredImageSource(var15, new DebugGraphicsFilter(var12.flashColor));
         Image var17 = Toolkit.getDefaultToolkit().createImage((ImageProducer)var16);
         DebugGraphicsObserver var18 = new DebugGraphicsObserver();

         for(int var20 = 0; var20 < var14; ++var20) {
            Image var19 = var20 % 2 == 0 ? var17 : var1;
            loadImage(var19);
            this.graphics.drawImage(var19, var2, var3, var4, var5, var6, var7, var8, var9, var10, var18);
            Toolkit.getDefaultToolkit().sync();
            this.sleep(var12.flashTime);
         }
      }

      return this.graphics.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   static void loadImage(Image var0) {
      imageLoadingIcon.loadImage(var0);
   }

   public void copyArea(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.debugLog()) {
         info().log(this.toShortString() + " Copying area from: " + new Rectangle(var1, var2, var3, var4) + " to: " + new Point(var5, var6));
      }

      this.graphics.copyArea(var1, var2, var3, var4, var5, var6);
   }

   final void sleep(int var1) {
      try {
         Thread.sleep((long)var1);
      } catch (Exception var3) {
      }

   }

   public void dispose() {
      this.graphics.dispose();
      this.graphics = null;
   }

   public boolean isDrawingBuffer() {
      return this.buffer != null;
   }

   String toShortString() {
      return "Graphics" + (this.isDrawingBuffer() ? "<B>" : "") + "(" + this.graphicsID + "-" + this.debugOptions + ")";
   }

   String pointToString(int var1, int var2) {
      return "(" + var1 + ", " + var2 + ")";
   }

   public void setDebugOptions(int var1) {
      if (var1 != 0) {
         if (var1 == -1) {
            if (this.debugOptions != 0) {
               System.err.println(this.toShortString() + " Disabling debug");
               this.debugOptions = 0;
            }
         } else if (this.debugOptions != var1) {
            this.debugOptions |= var1;
            if (this.debugLog()) {
               System.err.println(this.toShortString() + " Enabling debug");
            }
         }
      }

   }

   public int getDebugOptions() {
      return this.debugOptions;
   }

   static void setDebugOptions(JComponent var0, int var1) {
      info().setDebugOptions(var0, var1);
   }

   static int getDebugOptions(JComponent var0) {
      DebugGraphicsInfo var1 = info();
      return var1 == null ? 0 : var1.getDebugOptions(var0);
   }

   static int shouldComponentDebug(JComponent var0) {
      DebugGraphicsInfo var1 = info();
      if (var1 == null) {
         return 0;
      } else {
         Object var2 = var0;

         int var3;
         for(var3 = 0; var2 != null && var2 instanceof JComponent; var2 = ((Container)var2).getParent()) {
            var3 |= var1.getDebugOptions((JComponent)var2);
         }

         return var3;
      }
   }

   static int debugComponentCount() {
      DebugGraphicsInfo var0 = info();
      return var0 != null && var0.componentToDebug != null ? var0.componentToDebug.size() : 0;
   }

   boolean debugLog() {
      return (this.debugOptions & 1) == 1;
   }

   boolean debugFlash() {
      return (this.debugOptions & 2) == 2;
   }

   boolean debugBuffered() {
      return (this.debugOptions & 4) == 4;
   }

   private Graphics debugGraphics() {
      DebugGraphicsInfo var2 = info();
      if (var2.debugFrame == null) {
         var2.debugFrame = new JFrame();
         var2.debugFrame.setSize(500, 500);
      }

      JFrame var3 = var2.debugFrame;
      var3.show();
      DebugGraphics var1 = new DebugGraphics(var3.getGraphics());
      var1.setFont(this.getFont());
      var1.setColor(this.getColor());
      var1.translate(this.xOffset, this.yOffset);
      var1.setClip(this.getClipBounds());
      if (this.debugFlash()) {
         var1.setDebugOptions(2);
      }

      return var1;
   }

   static DebugGraphicsInfo info() {
      DebugGraphicsInfo var0 = (DebugGraphicsInfo)SwingUtilities.appContextGet(debugGraphicsInfoKey);
      if (var0 == null) {
         var0 = new DebugGraphicsInfo();
         SwingUtilities.appContextPut(debugGraphicsInfoKey, var0);
      }

      return var0;
   }

   static {
      JComponent.DEBUG_GRAPHICS_LOADED = true;
      debugGraphicsInfoKey = DebugGraphicsInfo.class;
   }
}
