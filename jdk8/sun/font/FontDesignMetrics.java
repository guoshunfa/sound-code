package sun.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public final class FontDesignMetrics extends FontMetrics {
   static final long serialVersionUID = 4480069578560887773L;
   private static final float UNKNOWN_WIDTH = -1.0F;
   private static final int CURRENT_VERSION = 1;
   private static float roundingUpValue = 0.95F;
   private Font font;
   private float ascent;
   private float descent;
   private float leading;
   private float maxAdvance;
   private double[] matrix;
   private int[] cache;
   private int serVersion;
   private boolean isAntiAliased;
   private boolean usesFractionalMetrics;
   private AffineTransform frcTx;
   private transient float[] advCache;
   private transient int height;
   private transient FontRenderContext frc;
   private transient double[] devmatrix;
   private transient FontStrike fontStrike;
   private static FontRenderContext DEFAULT_FRC = null;
   private static final ConcurrentHashMap<Object, FontDesignMetrics.KeyReference> metricsCache = new ConcurrentHashMap();
   private static final int MAXRECENT = 5;
   private static final FontDesignMetrics[] recentMetrics = new FontDesignMetrics[5];
   private static int recentIndex = 0;

   private static FontRenderContext getDefaultFrc() {
      if (DEFAULT_FRC == null) {
         AffineTransform var0;
         if (GraphicsEnvironment.isHeadless()) {
            var0 = new AffineTransform();
         } else {
            var0 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
         }

         DEFAULT_FRC = new FontRenderContext(var0, false, false);
      }

      return DEFAULT_FRC;
   }

   public static FontDesignMetrics getMetrics(Font var0) {
      return getMetrics(var0, getDefaultFrc());
   }

   public static FontDesignMetrics getMetrics(Font var0, FontRenderContext var1) {
      SunFontManager var2 = SunFontManager.getInstance();
      if (var2.maybeUsingAlternateCompositeFonts() && FontUtilities.getFont2D(var0) instanceof CompositeFont) {
         return new FontDesignMetrics(var0, var1);
      } else {
         FontDesignMetrics var3 = null;
         boolean var5 = var1.equals(getDefaultFrc());
         FontDesignMetrics.KeyReference var4;
         if (var5) {
            var4 = (FontDesignMetrics.KeyReference)metricsCache.get(var0);
         } else {
            Class var6 = FontDesignMetrics.MetricsKey.class;
            synchronized(FontDesignMetrics.MetricsKey.class) {
               FontDesignMetrics.MetricsKey.key.init(var0, var1);
               var4 = (FontDesignMetrics.KeyReference)metricsCache.get(FontDesignMetrics.MetricsKey.key);
            }
         }

         if (var4 != null) {
            var3 = (FontDesignMetrics)var4.get();
         }

         if (var3 == null) {
            var3 = new FontDesignMetrics(var0, var1);
            if (var5) {
               metricsCache.put(var0, new FontDesignMetrics.KeyReference(var0, var3));
            } else {
               FontDesignMetrics.MetricsKey var11 = new FontDesignMetrics.MetricsKey(var0, var1);
               metricsCache.put(var11, new FontDesignMetrics.KeyReference(var11, var3));
            }
         }

         for(int var12 = 0; var12 < recentMetrics.length; ++var12) {
            if (recentMetrics[var12] == var3) {
               return var3;
            }
         }

         synchronized(recentMetrics) {
            recentMetrics[recentIndex++] = var3;
            if (recentIndex == 5) {
               recentIndex = 0;
            }

            return var3;
         }
      }
   }

   private FontDesignMetrics(Font var1) {
      this(var1, getDefaultFrc());
   }

   private FontDesignMetrics(Font var1, FontRenderContext var2) {
      super(var1);
      this.serVersion = 0;
      this.height = -1;
      this.devmatrix = null;
      this.font = var1;
      this.frc = var2;
      this.isAntiAliased = var2.isAntiAliased();
      this.usesFractionalMetrics = var2.usesFractionalMetrics();
      this.frcTx = var2.getTransform();
      this.matrix = new double[4];
      this.initMatrixAndMetrics();
      this.initAdvCache();
   }

   private void initMatrixAndMetrics() {
      Font2D var1 = FontUtilities.getFont2D(this.font);
      this.fontStrike = var1.getStrike(this.font, this.frc);
      StrikeMetrics var2 = this.fontStrike.getFontMetrics();
      this.ascent = var2.getAscent();
      this.descent = var2.getDescent();
      this.leading = var2.getLeading();
      this.maxAdvance = var2.getMaxAdvance();
      this.devmatrix = new double[4];
      this.frcTx.getMatrix(this.devmatrix);
   }

   private void initAdvCache() {
      this.advCache = new float[256];

      for(int var1 = 0; var1 < 256; ++var1) {
         this.advCache[var1] = -1.0F;
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.serVersion != 1) {
         this.frc = getDefaultFrc();
         this.isAntiAliased = this.frc.isAntiAliased();
         this.usesFractionalMetrics = this.frc.usesFractionalMetrics();
         this.frcTx = this.frc.getTransform();
      } else {
         this.frc = new FontRenderContext(this.frcTx, this.isAntiAliased, this.usesFractionalMetrics);
      }

      this.height = -1;
      this.cache = null;
      this.initMatrixAndMetrics();
      this.initAdvCache();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.cache = new int[256];

      for(int var2 = 0; var2 < 256; ++var2) {
         this.cache[var2] = -1;
      }

      this.serVersion = 1;
      var1.defaultWriteObject();
      this.cache = null;
   }

   private float handleCharWidth(int var1) {
      return this.fontStrike.getCodePointAdvance(var1);
   }

   private float getLatinCharWidth(char var1) {
      float var2 = this.advCache[var1];
      if (var2 == -1.0F) {
         var2 = this.handleCharWidth(var1);
         this.advCache[var1] = var2;
      }

      return var2;
   }

   public FontRenderContext getFontRenderContext() {
      return this.frc;
   }

   public int charWidth(char var1) {
      float var2;
      if (var1 < 256) {
         var2 = this.getLatinCharWidth(var1);
      } else {
         var2 = this.handleCharWidth(var1);
      }

      return (int)(0.5D + (double)var2);
   }

   public int charWidth(int var1) {
      if (!Character.isValidCodePoint(var1)) {
         var1 = 65535;
      }

      float var2 = this.handleCharWidth(var1);
      return (int)(0.5D + (double)var2);
   }

   public int stringWidth(String var1) {
      float var2 = 0.0F;
      if (this.font.hasLayoutAttributes()) {
         if (var1 == null) {
            throw new NullPointerException("str is null");
         }

         if (var1.length() == 0) {
            return 0;
         }

         var2 = (new TextLayout(var1, this.font, this.frc)).getAdvance();
      } else {
         int var3 = var1.length();

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var1.charAt(var4);
            if (var5 < 256) {
               var2 += this.getLatinCharWidth(var5);
            } else {
               if (FontUtilities.isNonSimpleChar(var5)) {
                  var2 = (new TextLayout(var1, this.font, this.frc)).getAdvance();
                  break;
               }

               var2 += this.handleCharWidth(var5);
            }
         }
      }

      return (int)(0.5D + (double)var2);
   }

   public int charsWidth(char[] var1, int var2, int var3) {
      float var4 = 0.0F;
      if (this.font.hasLayoutAttributes()) {
         if (var3 == 0) {
            return 0;
         }

         String var5 = new String(var1, var2, var3);
         var4 = (new TextLayout(var5, this.font, this.frc)).getAdvance();
      } else {
         if (var3 < 0) {
            throw new IndexOutOfBoundsException("len=" + var3);
         }

         int var9 = var2 + var3;

         for(int var6 = var2; var6 < var9; ++var6) {
            char var7 = var1[var6];
            if (var7 < 256) {
               var4 += this.getLatinCharWidth(var7);
            } else {
               if (FontUtilities.isNonSimpleChar(var7)) {
                  String var8 = new String(var1, var2, var3);
                  var4 = (new TextLayout(var8, this.font, this.frc)).getAdvance();
                  break;
               }

               var4 += this.handleCharWidth(var7);
            }
         }
      }

      return (int)(0.5D + (double)var4);
   }

   public int[] getWidths() {
      int[] var1 = new int[256];

      for(char var2 = 0; var2 < 256; ++var2) {
         float var3 = this.advCache[var2];
         if (var3 == -1.0F) {
            var3 = this.advCache[var2] = this.handleCharWidth(var2);
         }

         var1[var2] = (int)(0.5D + (double)var3);
      }

      return var1;
   }

   public int getMaxAdvance() {
      return (int)(0.99F + this.maxAdvance);
   }

   public int getAscent() {
      return (int)(roundingUpValue + this.ascent);
   }

   public int getDescent() {
      return (int)(roundingUpValue + this.descent);
   }

   public int getLeading() {
      return (int)(roundingUpValue + this.descent + this.leading) - (int)(roundingUpValue + this.descent);
   }

   public int getHeight() {
      if (this.height < 0) {
         this.height = this.getAscent() + (int)(roundingUpValue + this.descent + this.leading);
      }

      return this.height;
   }

   private static class MetricsKey {
      Font font;
      FontRenderContext frc;
      int hash;
      static final FontDesignMetrics.MetricsKey key = new FontDesignMetrics.MetricsKey();

      MetricsKey() {
      }

      MetricsKey(Font var1, FontRenderContext var2) {
         this.init(var1, var2);
      }

      void init(Font var1, FontRenderContext var2) {
         this.font = var1;
         this.frc = var2;
         this.hash = var1.hashCode() + var2.hashCode();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof FontDesignMetrics.MetricsKey)) {
            return false;
         } else {
            return this.font.equals(((FontDesignMetrics.MetricsKey)var1).font) && this.frc.equals(((FontDesignMetrics.MetricsKey)var1).frc);
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }

   private static class KeyReference extends SoftReference implements DisposerRecord, Disposer.PollDisposable {
      static ReferenceQueue queue = Disposer.getQueue();
      Object key;

      KeyReference(Object var1, Object var2) {
         super(var2, queue);
         this.key = var1;
         Disposer.addReference(this, this);
      }

      public void dispose() {
         if (FontDesignMetrics.metricsCache.get(this.key) == this) {
            FontDesignMetrics.metricsCache.remove(this.key);
         }

      }
   }
}
