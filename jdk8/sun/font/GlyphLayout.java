package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class GlyphLayout {
   private GlyphLayout.GVData _gvdata = new GlyphLayout.GVData();
   private static volatile GlyphLayout cache;
   private GlyphLayout.LayoutEngineFactory _lef;
   private TextRecord _textRecord = new TextRecord();
   private ScriptRun _scriptRuns = new ScriptRun();
   private FontRunIterator _fontRuns = new FontRunIterator();
   private int _ercount;
   private ArrayList _erecords = new ArrayList(10);
   private Point2D.Float _pt = new Point2D.Float();
   private FontStrikeDesc _sd = new FontStrikeDesc();
   private float[] _mat = new float[4];
   private int _typo_flags;
   private int _offset;

   public static GlyphLayout get(GlyphLayout.LayoutEngineFactory var0) {
      if (var0 == null) {
         var0 = SunLayoutEngine.instance();
      }

      GlyphLayout var1 = null;
      Class var2 = GlyphLayout.class;
      synchronized(GlyphLayout.class) {
         if (cache != null) {
            var1 = cache;
            cache = null;
         }
      }

      if (var1 == null) {
         var1 = new GlyphLayout();
      }

      var1._lef = var0;
      return var1;
   }

   public static void done(GlyphLayout var0) {
      var0._lef = null;
      cache = var0;
   }

   public StandardGlyphVector layout(Font var1, FontRenderContext var2, char[] var3, int var4, int var5, int var6, StandardGlyphVector var7) {
      if (var3 != null && var4 >= 0 && var5 >= 0 && var5 <= var3.length - var4) {
         this.init(var5);
         if (var1.hasLayoutAttributes()) {
            AttributeValues var8 = ((AttributeMap)var1.getAttributes()).getValues();
            if (var8.getKerning() != 0) {
               this._typo_flags |= 1;
            }

            if (var8.getLigatures() != 0) {
               this._typo_flags |= 2;
            }
         }

         this._offset = var4;
         GlyphLayout.SDCache var21 = GlyphLayout.SDCache.get(var1, var2);
         this._mat[0] = (float)var21.gtx.getScaleX();
         this._mat[1] = (float)var21.gtx.getShearY();
         this._mat[2] = (float)var21.gtx.getShearX();
         this._mat[3] = (float)var21.gtx.getScaleY();
         this._pt.setLocation(var21.delta);
         int var9 = var4 + var5;
         int var10 = 0;
         int var11 = var3.length;
         if (var6 != 0) {
            if ((var6 & 1) != 0) {
               this._typo_flags |= Integer.MIN_VALUE;
            }

            if ((var6 & 2) != 0) {
               var10 = var4;
            }

            if ((var6 & 4) != 0) {
               var11 = var9;
            }
         }

         byte var12 = -1;
         Object var13 = FontUtilities.getFont2D(var1);
         if (var13 instanceof FontSubstitution) {
            var13 = ((FontSubstitution)var13).getCompositeFont2D();
         }

         this._textRecord.init(var3, var4, var9, var10, var11);
         int var14 = var4;
         int var15;
         int var16;
         if (var13 instanceof CompositeFont) {
            this._scriptRuns.init(var3, var4, var5);
            this._fontRuns.init((CompositeFont)var13, var3, var4, var9);

            while(this._scriptRuns.next()) {
               var15 = this._scriptRuns.getScriptLimit();

               int var19;
               for(var16 = this._scriptRuns.getScriptCode(); this._fontRuns.next(var16, var15); var14 = var19) {
                  PhysicalFont var17 = this._fontRuns.getFont();
                  if (var17 instanceof NativeFont) {
                     var17 = ((NativeFont)var17).getDelegateFont();
                  }

                  int var18 = this._fontRuns.getGlyphMask();
                  var19 = this._fontRuns.getPos();
                  this.nextEngineRecord(var14, var19, var16, var12, var17, var18);
               }
            }
         } else {
            this._scriptRuns.init(var3, var4, var5);

            while(this._scriptRuns.next()) {
               var15 = this._scriptRuns.getScriptLimit();
               var16 = this._scriptRuns.getScriptCode();
               this.nextEngineRecord(var14, var15, var16, var12, (Font2D)var13, 0);
               var14 = var15;
            }
         }

         var15 = 0;
         var16 = this._ercount;
         byte var22 = 1;
         if (this._typo_flags < 0) {
            var15 = var16 - 1;
            var16 = -1;
            var22 = -1;
         }

         for(this._sd = var21.sd; var15 != var16; var15 += var22) {
            GlyphLayout.EngineRecord var23 = (GlyphLayout.EngineRecord)this._erecords.get(var15);

            while(true) {
               try {
                  var23.layout();
                  break;
               } catch (IndexOutOfBoundsException var20) {
                  if (this._gvdata._count >= 0) {
                     this._gvdata.grow();
                  }
               }
            }

            if (this._gvdata._count < 0) {
               break;
            }
         }

         StandardGlyphVector var24;
         if (this._gvdata._count < 0) {
            var24 = new StandardGlyphVector(var1, var3, var4, var5, var2);
            if (FontUtilities.debugFonts()) {
               FontUtilities.getLogger().warning("OpenType layout failed on font: " + var1);
            }
         } else {
            var24 = this._gvdata.createGlyphVector(var1, var2, var7);
         }

         return var24;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private GlyphLayout() {
   }

   private void init(int var1) {
      this._typo_flags = 0;
      this._ercount = 0;
      this._gvdata.init(var1);
   }

   private void nextEngineRecord(int var1, int var2, int var3, int var4, Font2D var5, int var6) {
      GlyphLayout.EngineRecord var7 = null;
      if (this._ercount == this._erecords.size()) {
         var7 = new GlyphLayout.EngineRecord();
         this._erecords.add(var7);
      } else {
         var7 = (GlyphLayout.EngineRecord)this._erecords.get(this._ercount);
      }

      var7.init(var1, var2, var5, var3, var4, var6);
      ++this._ercount;
   }

   private final class EngineRecord {
      private int start;
      private int limit;
      private int gmask;
      private int eflags;
      private GlyphLayout.LayoutEngineKey key = new GlyphLayout.LayoutEngineKey();
      private GlyphLayout.LayoutEngine engine;

      EngineRecord() {
      }

      void init(int var1, int var2, Font2D var3, int var4, int var5, int var6) {
         this.start = var1;
         this.limit = var2;
         this.gmask = var6;
         this.key.init(var3, var4, var5);
         this.eflags = 0;

         for(int var7 = var1; var7 < var2; ++var7) {
            int var8 = GlyphLayout.this._textRecord.text[var7];
            if (Character.isHighSurrogate((char)var8) && var7 < var2 - 1 && Character.isLowSurrogate(GlyphLayout.this._textRecord.text[var7 + 1])) {
               char var10000 = (char)var8;
               ++var7;
               var8 = Character.toCodePoint(var10000, GlyphLayout.this._textRecord.text[var7]);
            }

            int var9 = Character.getType(var8);
            if (var9 == 6 || var9 == 7 || var9 == 8) {
               this.eflags = 4;
               break;
            }
         }

         this.engine = GlyphLayout.this._lef.getEngine(this.key);
      }

      void layout() {
         GlyphLayout.this._textRecord.start = this.start;
         GlyphLayout.this._textRecord.limit = this.limit;
         this.engine.layout(GlyphLayout.this._sd, GlyphLayout.this._mat, this.gmask, this.start - GlyphLayout.this._offset, GlyphLayout.this._textRecord, GlyphLayout.this._typo_flags | this.eflags, GlyphLayout.this._pt, GlyphLayout.this._gvdata);
      }
   }

   public static final class GVData {
      public int _count;
      public int _flags;
      public int[] _glyphs;
      public float[] _positions;
      public int[] _indices;
      private static final int UNINITIALIZED_FLAGS = -1;

      public void init(int var1) {
         this._count = 0;
         this._flags = -1;
         if (this._glyphs == null || this._glyphs.length < var1) {
            if (var1 < 20) {
               var1 = 20;
            }

            this._glyphs = new int[var1];
            this._positions = new float[var1 * 2 + 2];
            this._indices = new int[var1];
         }

      }

      public void grow() {
         this.grow(this._glyphs.length / 4);
      }

      public void grow(int var1) {
         int var2 = this._glyphs.length + var1;
         int[] var3 = new int[var2];
         System.arraycopy(this._glyphs, 0, var3, 0, this._count);
         this._glyphs = var3;
         float[] var4 = new float[var2 * 2 + 2];
         System.arraycopy(this._positions, 0, var4, 0, this._count * 2 + 2);
         this._positions = var4;
         int[] var5 = new int[var2];
         System.arraycopy(this._indices, 0, var5, 0, this._count);
         this._indices = var5;
      }

      public void adjustPositions(AffineTransform var1) {
         var1.transform((float[])this._positions, 0, (float[])this._positions, 0, this._count);
      }

      public StandardGlyphVector createGlyphVector(Font var1, FontRenderContext var2, StandardGlyphVector var3) {
         if (this._flags == -1) {
            this._flags = 0;
            if (this._count > 1) {
               boolean var4 = true;
               boolean var5 = true;
               int var6 = this._count;

               for(int var7 = 0; var7 < this._count && (var4 || var5); ++var7) {
                  boolean var10000;
                  label54: {
                     int var8 = this._indices[var7];
                     var4 = var4 && var8 == var7;
                     if (var5) {
                        --var6;
                        if (var8 == var6) {
                           var10000 = true;
                           break label54;
                        }
                     }

                     var10000 = false;
                  }

                  var5 = var10000;
               }

               if (var5) {
                  this._flags |= 4;
               }

               if (!var5 && !var4) {
                  this._flags |= 8;
               }
            }

            this._flags |= 2;
         }

         int[] var9 = new int[this._count];
         System.arraycopy(this._glyphs, 0, var9, 0, this._count);
         float[] var10 = null;
         if ((this._flags & 2) != 0) {
            var10 = new float[this._count * 2 + 2];
            System.arraycopy(this._positions, 0, var10, 0, var10.length);
         }

         int[] var11 = null;
         if ((this._flags & 8) != 0) {
            var11 = new int[this._count];
            System.arraycopy(this._indices, 0, var11, 0, this._count);
         }

         if (var3 == null) {
            var3 = new StandardGlyphVector(var1, var2, var9, var10, var11, this._flags);
         } else {
            var3.initGlyphVector(var1, var2, var9, var10, var11, this._flags);
         }

         return var3;
      }
   }

   private static final class SDCache {
      public Font key_font;
      public FontRenderContext key_frc;
      public AffineTransform dtx;
      public AffineTransform invdtx;
      public AffineTransform gtx;
      public Point2D.Float delta;
      public FontStrikeDesc sd;
      private static final Point2D.Float ZERO_DELTA = new Point2D.Float();
      private static SoftReference<ConcurrentHashMap<GlyphLayout.SDCache.SDKey, GlyphLayout.SDCache>> cacheRef;

      private SDCache(Font var1, FontRenderContext var2) {
         this.key_font = var1;
         this.key_frc = var2;
         this.dtx = var2.getTransform();
         this.dtx.setTransform(this.dtx.getScaleX(), this.dtx.getShearY(), this.dtx.getShearX(), this.dtx.getScaleY(), 0.0D, 0.0D);
         if (!this.dtx.isIdentity()) {
            try {
               this.invdtx = this.dtx.createInverse();
            } catch (NoninvertibleTransformException var6) {
               throw new InternalError(var6);
            }
         }

         float var3 = var1.getSize2D();
         if (var1.isTransformed()) {
            this.gtx = var1.getTransform();
            this.gtx.scale((double)var3, (double)var3);
            this.delta = new Point2D.Float((float)this.gtx.getTranslateX(), (float)this.gtx.getTranslateY());
            this.gtx.setTransform(this.gtx.getScaleX(), this.gtx.getShearY(), this.gtx.getShearX(), this.gtx.getScaleY(), 0.0D, 0.0D);
            this.gtx.preConcatenate(this.dtx);
         } else {
            this.delta = ZERO_DELTA;
            this.gtx = new AffineTransform(this.dtx);
            this.gtx.scale((double)var3, (double)var3);
         }

         int var4 = FontStrikeDesc.getAAHintIntVal(var2.getAntiAliasingHint(), FontUtilities.getFont2D(var1), (int)Math.abs(var3));
         int var5 = FontStrikeDesc.getFMHintIntVal(var2.getFractionalMetricsHint());
         this.sd = new FontStrikeDesc(this.dtx, this.gtx, var1.getStyle(), var4, var5);
      }

      public static GlyphLayout.SDCache get(Font var0, FontRenderContext var1) {
         if (var1.isTransformed()) {
            AffineTransform var2 = var1.getTransform();
            if (var2.getTranslateX() != 0.0D || var2.getTranslateY() != 0.0D) {
               var2 = new AffineTransform(var2.getScaleX(), var2.getShearY(), var2.getShearX(), var2.getScaleY(), 0.0D, 0.0D);
               var1 = new FontRenderContext(var2, var1.getAntiAliasingHint(), var1.getFractionalMetricsHint());
            }
         }

         GlyphLayout.SDCache.SDKey var5 = new GlyphLayout.SDCache.SDKey(var0, var1);
         ConcurrentHashMap var3 = null;
         GlyphLayout.SDCache var4 = null;
         if (cacheRef != null) {
            var3 = (ConcurrentHashMap)cacheRef.get();
            if (var3 != null) {
               var4 = (GlyphLayout.SDCache)var3.get(var5);
            }
         }

         if (var4 == null) {
            var4 = new GlyphLayout.SDCache(var0, var1);
            if (var3 == null) {
               var3 = new ConcurrentHashMap(10);
               cacheRef = new SoftReference(var3);
            } else if (var3.size() >= 512) {
               var3.clear();
            }

            var3.put(var5, var4);
         }

         return var4;
      }

      private static final class SDKey {
         private final Font font;
         private final FontRenderContext frc;
         private final int hash;

         SDKey(Font var1, FontRenderContext var2) {
            this.font = var1;
            this.frc = var2;
            this.hash = var1.hashCode() ^ var2.hashCode();
         }

         public int hashCode() {
            return this.hash;
         }

         public boolean equals(Object var1) {
            try {
               GlyphLayout.SDCache.SDKey var2 = (GlyphLayout.SDCache.SDKey)var1;
               return this.hash == var2.hash && this.font.equals(var2.font) && this.frc.equals(var2.frc);
            } catch (ClassCastException var3) {
               return false;
            }
         }
      }
   }

   public interface LayoutEngine {
      void layout(FontStrikeDesc var1, float[] var2, int var3, int var4, TextRecord var5, int var6, Point2D.Float var7, GlyphLayout.GVData var8);
   }

   public interface LayoutEngineFactory {
      GlyphLayout.LayoutEngine getEngine(Font2D var1, int var2, int var3);

      GlyphLayout.LayoutEngine getEngine(GlyphLayout.LayoutEngineKey var1);
   }

   public static final class LayoutEngineKey {
      private Font2D font;
      private int script;
      private int lang;

      LayoutEngineKey() {
      }

      LayoutEngineKey(Font2D var1, int var2, int var3) {
         this.init(var1, var2, var3);
      }

      void init(Font2D var1, int var2, int var3) {
         this.font = var1;
         this.script = var2;
         this.lang = var3;
      }

      GlyphLayout.LayoutEngineKey copy() {
         return new GlyphLayout.LayoutEngineKey(this.font, this.script, this.lang);
      }

      Font2D font() {
         return this.font;
      }

      int script() {
         return this.script;
      }

      int lang() {
         return this.lang;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null) {
            return false;
         } else {
            try {
               GlyphLayout.LayoutEngineKey var2 = (GlyphLayout.LayoutEngineKey)var1;
               return this.script == var2.script && this.lang == var2.lang && this.font.equals(var2.font);
            } catch (ClassCastException var3) {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.script ^ this.lang ^ this.font.hashCode();
      }
   }
}
