package sun.java2d;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class HeadlessGraphicsEnvironment extends GraphicsEnvironment {
   private GraphicsEnvironment ge;

   public HeadlessGraphicsEnvironment(GraphicsEnvironment var1) {
      this.ge = var1;
   }

   public GraphicsDevice[] getScreenDevices() throws HeadlessException {
      throw new HeadlessException();
   }

   public GraphicsDevice getDefaultScreenDevice() throws HeadlessException {
      throw new HeadlessException();
   }

   public Point getCenterPoint() throws HeadlessException {
      throw new HeadlessException();
   }

   public Rectangle getMaximumWindowBounds() throws HeadlessException {
      throw new HeadlessException();
   }

   public Graphics2D createGraphics(BufferedImage var1) {
      return this.ge.createGraphics(var1);
   }

   public Font[] getAllFonts() {
      return this.ge.getAllFonts();
   }

   public String[] getAvailableFontFamilyNames() {
      return this.ge.getAvailableFontFamilyNames();
   }

   public String[] getAvailableFontFamilyNames(Locale var1) {
      return this.ge.getAvailableFontFamilyNames(var1);
   }

   public GraphicsEnvironment getSunGraphicsEnvironment() {
      return this.ge;
   }
}
