package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.peer.FontPeer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import sun.font.AttributeMap;
import sun.font.AttributeValues;
import sun.font.CompositeFont;
import sun.font.CoreMetrics;
import sun.font.CreatedFontTracker;
import sun.font.EAttribute;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontAccess;
import sun.font.FontLineMetrics;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.FontUtilities;
import sun.font.GlyphLayout;
import sun.font.StandardGlyphVector;

public class Font implements Serializable {
   private Hashtable<Object, Object> fRequestedAttributes;
   public static final String DIALOG = "Dialog";
   public static final String DIALOG_INPUT = "DialogInput";
   public static final String SANS_SERIF = "SansSerif";
   public static final String SERIF = "Serif";
   public static final String MONOSPACED = "Monospaced";
   public static final int PLAIN = 0;
   public static final int BOLD = 1;
   public static final int ITALIC = 2;
   public static final int ROMAN_BASELINE = 0;
   public static final int CENTER_BASELINE = 1;
   public static final int HANGING_BASELINE = 2;
   public static final int TRUETYPE_FONT = 0;
   public static final int TYPE1_FONT = 1;
   protected String name;
   protected int style;
   protected int size;
   protected float pointSize;
   private transient FontPeer peer;
   private transient long pData;
   private transient Font2DHandle font2DHandle;
   private transient AttributeValues values;
   private transient boolean hasLayoutAttributes;
   private transient boolean createdFont;
   private transient boolean nonIdentityTx;
   private static final AffineTransform identityTx;
   private static final long serialVersionUID = -4206021311591459213L;
   private static final int RECOGNIZED_MASK;
   private static final int PRIMARY_MASK;
   private static final int SECONDARY_MASK;
   private static final int LAYOUT_MASK;
   private static final int EXTRA_MASK;
   private static final float[] ssinfo;
   transient int hash;
   private int fontSerializedDataVersion;
   private transient SoftReference<FontLineMetrics> flmref;
   public static final int LAYOUT_LEFT_TO_RIGHT = 0;
   public static final int LAYOUT_RIGHT_TO_LEFT = 1;
   public static final int LAYOUT_NO_START_CONTEXT = 2;
   public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;

   /** @deprecated */
   @Deprecated
   public FontPeer getPeer() {
      return this.getPeer_NoClientCode();
   }

   final FontPeer getPeer_NoClientCode() {
      if (this.peer == null) {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         this.peer = var1.getFontPeer(this.name, this.style);
      }

      return this.peer;
   }

   private AttributeValues getAttributeValues() {
      if (this.values == null) {
         AttributeValues var1 = new AttributeValues();
         var1.setFamily(this.name);
         var1.setSize(this.pointSize);
         if ((this.style & 1) != 0) {
            var1.setWeight(2.0F);
         }

         if ((this.style & 2) != 0) {
            var1.setPosture(0.2F);
         }

         var1.defineAll(PRIMARY_MASK);
         this.values = var1;
      }

      return this.values;
   }

   private Font2D getFont2D() {
      FontManager var1 = FontManagerFactory.getInstance();
      if (var1.usingPerAppContextComposites() && this.font2DHandle != null && this.font2DHandle.font2D instanceof CompositeFont && ((CompositeFont)((CompositeFont)this.font2DHandle.font2D)).isStdComposite()) {
         return var1.findFont2D(this.name, this.style, 2);
      } else {
         if (this.font2DHandle == null) {
            this.font2DHandle = var1.findFont2D(this.name, this.style, 2).handle;
         }

         return this.font2DHandle.font2D;
      }
   }

   public Font(String var1, int var2, int var3) {
      this.createdFont = false;
      this.fontSerializedDataVersion = 1;
      this.name = var1 != null ? var1 : "Default";
      this.style = (var2 & -4) == 0 ? var2 : 0;
      this.size = var3;
      this.pointSize = (float)var3;
   }

   private Font(String var1, int var2, float var3) {
      this.createdFont = false;
      this.fontSerializedDataVersion = 1;
      this.name = var1 != null ? var1 : "Default";
      this.style = (var2 & -4) == 0 ? var2 : 0;
      this.size = (int)((double)var3 + 0.5D);
      this.pointSize = var3;
   }

   private Font(String var1, int var2, float var3, boolean var4, Font2DHandle var5) {
      this(var1, var2, var3);
      this.createdFont = var4;
      if (var4) {
         if (var5.font2D instanceof CompositeFont && var5.font2D.getStyle() != var2) {
            FontManager var6 = FontManagerFactory.getInstance();
            this.font2DHandle = var6.getNewComposite((String)null, var2, var5);
         } else {
            this.font2DHandle = var5;
         }
      }

   }

   private Font(File var1, int var2, boolean var3, CreatedFontTracker var4) throws FontFormatException {
      this.createdFont = false;
      this.fontSerializedDataVersion = 1;
      this.createdFont = true;
      FontManager var5 = FontManagerFactory.getInstance();
      this.font2DHandle = var5.createFont2D(var1, var2, var3, var4).handle;
      this.name = this.font2DHandle.font2D.getFontName(Locale.getDefault());
      this.style = 0;
      this.size = 1;
      this.pointSize = 1.0F;
   }

   private Font(AttributeValues var1, String var2, int var3, boolean var4, Font2DHandle var5) {
      this.createdFont = false;
      this.fontSerializedDataVersion = 1;
      this.createdFont = var4;
      if (var4) {
         this.font2DHandle = var5;
         String var6 = null;
         if (var2 != null) {
            var6 = var1.getFamily();
            if (var2.equals(var6)) {
               var6 = null;
            }
         }

         int var7 = 0;
         if (var3 == -1) {
            var7 = -1;
         } else {
            if (var1.getWeight() >= 2.0F) {
               var7 = 1;
            }

            if (var1.getPosture() >= 0.2F) {
               var7 |= 2;
            }

            if (var3 == var7) {
               var7 = -1;
            }
         }

         if (var5.font2D instanceof CompositeFont) {
            if (var7 != -1 || var6 != null) {
               FontManager var8 = FontManagerFactory.getInstance();
               this.font2DHandle = var8.getNewComposite(var6, var7, var5);
            }
         } else if (var6 != null) {
            this.createdFont = false;
            this.font2DHandle = null;
         }
      }

      this.initFromValues(var1);
   }

   public Font(Map<? extends AttributedCharacterIterator.Attribute, ?> var1) {
      this.createdFont = false;
      this.fontSerializedDataVersion = 1;
      this.initFromValues(AttributeValues.fromMap(var1, RECOGNIZED_MASK));
   }

   protected Font(Font var1) {
      this.createdFont = false;
      this.fontSerializedDataVersion = 1;
      if (var1.values != null) {
         this.initFromValues(var1.getAttributeValues().clone());
      } else {
         this.name = var1.name;
         this.style = var1.style;
         this.size = var1.size;
         this.pointSize = var1.pointSize;
      }

      this.font2DHandle = var1.font2DHandle;
      this.createdFont = var1.createdFont;
   }

   private void initFromValues(AttributeValues var1) {
      this.values = var1;
      var1.defineAll(PRIMARY_MASK);
      this.name = var1.getFamily();
      this.pointSize = var1.getSize();
      this.size = (int)((double)var1.getSize() + 0.5D);
      if (var1.getWeight() >= 2.0F) {
         this.style |= 1;
      }

      if (var1.getPosture() >= 0.2F) {
         this.style |= 2;
      }

      this.nonIdentityTx = var1.anyNonDefault(EXTRA_MASK);
      this.hasLayoutAttributes = var1.anyNonDefault(LAYOUT_MASK);
   }

   public static Font getFont(Map<? extends AttributedCharacterIterator.Attribute, ?> var0) {
      if (var0 instanceof AttributeMap && ((AttributeMap)var0).getValues() != null) {
         AttributeValues var3 = ((AttributeMap)var0).getValues();
         if (var3.isNonDefault(EAttribute.EFONT)) {
            Font var4 = var3.getFont();
            if (!var3.anyDefined(SECONDARY_MASK)) {
               return var4;
            } else {
               var3 = var4.getAttributeValues().clone();
               var3.merge(var0, SECONDARY_MASK);
               return new Font(var3, var4.name, var4.style, var4.createdFont, var4.font2DHandle);
            }
         } else {
            return new Font(var0);
         }
      } else {
         Font var1 = (Font)var0.get(TextAttribute.FONT);
         if (var1 != null) {
            if (var0.size() > 1) {
               AttributeValues var2 = var1.getAttributeValues().clone();
               var2.merge(var0, SECONDARY_MASK);
               return new Font(var2, var1.name, var1.style, var1.createdFont, var1.font2DHandle);
            } else {
               return var1;
            }
         } else {
            return new Font(var0);
         }
      }
   }

   private static boolean hasTempPermission() {
      if (System.getSecurityManager() == null) {
         return true;
      } else {
         File var0 = null;
         boolean var1 = false;

         try {
            var0 = Files.createTempFile("+~JT", ".tmp").toFile();
            var0.delete();
            var0 = null;
            var1 = true;
         } catch (Throwable var3) {
         }

         return var1;
      }
   }

   public static Font createFont(int var0, InputStream var1) throws FontFormatException, IOException {
      if (hasTempPermission()) {
         return createFont0(var0, var1, (CreatedFontTracker)null);
      } else {
         CreatedFontTracker var2 = CreatedFontTracker.getTracker();
         boolean var3 = false;

         Font var4;
         try {
            var3 = var2.acquirePermit();
            if (!var3) {
               throw new IOException("Timed out waiting for resources.");
            }

            var4 = createFont0(var0, var1, var2);
         } catch (InterruptedException var8) {
            throw new IOException("Problem reading font data.");
         } finally {
            if (var3) {
               var2.releasePermit();
            }

         }

         return var4;
      }
   }

   private static Font createFont0(int var0, InputStream var1, CreatedFontTracker var2) throws FontFormatException, IOException {
      if (var0 != 0 && var0 != 1) {
         throw new IllegalArgumentException("font format not recognized");
      } else {
         boolean var3 = false;

         try {
            final File var4 = (File)AccessController.doPrivileged(new PrivilegedExceptionAction<File>() {
               public File run() throws IOException {
                  return Files.createTempFile("+~JF", ".tmp").toFile();
               }
            });
            if (var2 != null) {
               var2.add(var4);
            }

            int var20 = 0;

            Font var22;
            try {
               OutputStream var6 = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
                  public OutputStream run() throws IOException {
                     return new FileOutputStream(var4);
                  }
               });
               if (var2 != null) {
                  var2.set(var4, var6);
               }

               try {
                  byte[] var7 = new byte[8192];

                  while(true) {
                     int var8 = var1.read(var7);
                     if (var8 < 0) {
                        break;
                     }

                     if (var2 != null) {
                        if (var20 + var8 > 33554432) {
                           throw new IOException("File too big.");
                        }

                        if (var20 + var2.getNumBytes() > 335544320) {
                           throw new IOException("Total files too big.");
                        }

                        var20 += var8;
                        var2.addBytes(var8);
                     }

                     var6.write(var7, 0, var8);
                  }
               } finally {
                  var6.close();
               }

               var3 = true;
               Font var21 = new Font(var4, var0, true, var2);
               var22 = var21;
            } finally {
               if (var2 != null) {
                  var2.remove(var4);
               }

               if (!var3) {
                  if (var2 != null) {
                     var2.subBytes(var20);
                  }

                  AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                     public Void run() {
                        var4.delete();
                        return null;
                     }
                  });
               }

            }

            return var22;
         } catch (Throwable var19) {
            if (var19 instanceof FontFormatException) {
               throw (FontFormatException)var19;
            } else if (var19 instanceof IOException) {
               throw (IOException)var19;
            } else {
               Throwable var5 = var19.getCause();
               if (var5 instanceof FontFormatException) {
                  throw (FontFormatException)var5;
               } else {
                  throw new IOException("Problem reading font data.");
               }
            }
         }
      }
   }

   public static Font createFont(int var0, File var1) throws FontFormatException, IOException {
      var1 = new File(var1.getPath());
      if (var0 != 0 && var0 != 1) {
         throw new IllegalArgumentException("font format not recognized");
      } else {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            FilePermission var3 = new FilePermission(var1.getPath(), "read");
            var2.checkPermission(var3);
         }

         if (!var1.canRead()) {
            throw new IOException("Can't read " + var1);
         } else {
            return new Font(var1, var0, false, (CreatedFontTracker)null);
         }
      }
   }

   public AffineTransform getTransform() {
      if (this.nonIdentityTx) {
         AttributeValues var1 = this.getAttributeValues();
         AffineTransform var2 = var1.isNonDefault(EAttribute.ETRANSFORM) ? new AffineTransform(var1.getTransform()) : new AffineTransform();
         if (var1.getSuperscript() != 0) {
            int var3 = var1.getSuperscript();
            double var4 = 0.0D;
            int var6 = 0;
            boolean var7 = var3 > 0;
            int var8 = var7 ? -1 : 1;

            int var10;
            for(int var9 = var7 ? var3 : -var3; (var9 & 7) > var6; var6 = var10) {
               var10 = var9 & 7;
               var4 += (double)((float)var8 * (ssinfo[var10] - ssinfo[var6]));
               var9 >>= 3;
               var8 = -var8;
            }

            var4 *= (double)this.pointSize;
            double var12 = Math.pow(0.6666666666666666D, (double)var6);
            var2.preConcatenate(AffineTransform.getTranslateInstance(0.0D, var4));
            var2.scale(var12, var12);
         }

         if (var1.isNonDefault(EAttribute.EWIDTH)) {
            var2.scale((double)var1.getWidth(), 1.0D);
         }

         return var2;
      } else {
         return new AffineTransform();
      }
   }

   public String getFamily() {
      return this.getFamily_NoClientCode();
   }

   final String getFamily_NoClientCode() {
      return this.getFamily(Locale.getDefault());
   }

   public String getFamily(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException("null locale doesn't mean default");
      } else {
         return this.getFont2D().getFamilyName(var1);
      }
   }

   public String getPSName() {
      return this.getFont2D().getPostscriptName();
   }

   public String getName() {
      return this.name;
   }

   public String getFontName() {
      return this.getFontName(Locale.getDefault());
   }

   public String getFontName(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException("null locale doesn't mean default");
      } else {
         return this.getFont2D().getFontName(var1);
      }
   }

   public int getStyle() {
      return this.style;
   }

   public int getSize() {
      return this.size;
   }

   public float getSize2D() {
      return this.pointSize;
   }

   public boolean isPlain() {
      return this.style == 0;
   }

   public boolean isBold() {
      return (this.style & 1) != 0;
   }

   public boolean isItalic() {
      return (this.style & 2) != 0;
   }

   public boolean isTransformed() {
      return this.nonIdentityTx;
   }

   public boolean hasLayoutAttributes() {
      return this.hasLayoutAttributes;
   }

   public static Font getFont(String var0) {
      return getFont(var0, (Font)null);
   }

   public static Font decode(String var0) {
      String var2 = "";
      int var3 = 12;
      byte var4 = 0;
      if (var0 == null) {
         return new Font("Dialog", var4, var3);
      } else {
         int var5 = var0.lastIndexOf(45);
         int var6 = var0.lastIndexOf(32);
         int var7 = var5 > var6 ? 45 : 32;
         int var8 = var0.lastIndexOf(var7);
         int var9 = var0.lastIndexOf(var7, var8 - 1);
         int var10 = var0.length();
         if (var8 > 0 && var8 + 1 < var10) {
            try {
               var3 = Integer.valueOf(var0.substring(var8 + 1));
               if (var3 <= 0) {
                  var3 = 12;
               }
            } catch (NumberFormatException var12) {
               var9 = var8;
               var8 = var10;
               if (var0.charAt(var10 - 1) == var7) {
                  var8 = var10 - 1;
               }
            }
         }

         String var1;
         if (var9 >= 0 && var9 + 1 < var10) {
            var2 = var0.substring(var9 + 1, var8);
            var2 = var2.toLowerCase(Locale.ENGLISH);
            if (var2.equals("bolditalic")) {
               var4 = 3;
            } else if (var2.equals("italic")) {
               var4 = 2;
            } else if (var2.equals("bold")) {
               var4 = 1;
            } else if (var2.equals("plain")) {
               var4 = 0;
            } else {
               var9 = var8;
               if (var0.charAt(var8 - 1) == var7) {
                  var9 = var8 - 1;
               }
            }

            var1 = var0.substring(0, var9);
         } else {
            int var11 = var10;
            if (var9 > 0) {
               var11 = var9;
            } else if (var8 > 0) {
               var11 = var8;
            }

            if (var11 > 0 && var0.charAt(var11 - 1) == var7) {
               --var11;
            }

            var1 = var0.substring(0, var11);
         }

         return new Font(var1, var4, var3);
      }
   }

   public static Font getFont(String var0, Font var1) {
      String var2 = null;

      try {
         var2 = System.getProperty(var0);
      } catch (SecurityException var4) {
      }

      return var2 == null ? var1 : decode(var2);
   }

   public int hashCode() {
      if (this.hash == 0) {
         this.hash = this.name.hashCode() ^ this.style ^ this.size;
         if (this.nonIdentityTx && this.values != null && this.values.getTransform() != null) {
            this.hash ^= this.values.getTransform().hashCode();
         }
      }

      return this.hash;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         if (var1 != null) {
            try {
               Font var2 = (Font)var1;
               if (this.size == var2.size && this.style == var2.style && this.nonIdentityTx == var2.nonIdentityTx && this.hasLayoutAttributes == var2.hasLayoutAttributes && this.pointSize == var2.pointSize && this.name.equals(var2.name)) {
                  if (this.values == null) {
                     if (var2.values == null) {
                        return true;
                     }

                     return this.getAttributeValues().equals(var2.values);
                  }

                  return this.values.equals(var2.getAttributeValues());
               }
            } catch (ClassCastException var3) {
            }
         }

         return false;
      }
   }

   public String toString() {
      String var1;
      if (this.isBold()) {
         var1 = this.isItalic() ? "bolditalic" : "bold";
      } else {
         var1 = this.isItalic() ? "italic" : "plain";
      }

      return this.getClass().getName() + "[family=" + this.getFamily() + ",name=" + this.name + ",style=" + var1 + ",size=" + this.size + "]";
   }

   private void writeObject(ObjectOutputStream var1) throws ClassNotFoundException, IOException {
      if (this.values != null) {
         synchronized(this.values) {
            this.fRequestedAttributes = this.values.toSerializableHashtable();
            var1.defaultWriteObject();
            this.fRequestedAttributes = null;
         }
      } else {
         var1.defaultWriteObject();
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      if (this.pointSize == 0.0F) {
         this.pointSize = (float)this.size;
      }

      if (this.fRequestedAttributes != null) {
         this.values = this.getAttributeValues();
         AttributeValues var2 = AttributeValues.fromSerializableHashtable(this.fRequestedAttributes);
         if (!AttributeValues.is16Hashtable(this.fRequestedAttributes)) {
            var2.unsetDefault();
         }

         this.values = this.getAttributeValues().merge(var2);
         this.nonIdentityTx = this.values.anyNonDefault(EXTRA_MASK);
         this.hasLayoutAttributes = this.values.anyNonDefault(LAYOUT_MASK);
         this.fRequestedAttributes = null;
      }

   }

   public int getNumGlyphs() {
      return this.getFont2D().getNumGlyphs();
   }

   public int getMissingGlyphCode() {
      return this.getFont2D().getMissingGlyphCode();
   }

   public byte getBaselineFor(char var1) {
      return this.getFont2D().getBaselineFor(var1);
   }

   public Map<TextAttribute, ?> getAttributes() {
      return new AttributeMap(this.getAttributeValues());
   }

   public AttributedCharacterIterator.Attribute[] getAvailableAttributes() {
      AttributedCharacterIterator.Attribute[] var1 = new AttributedCharacterIterator.Attribute[]{TextAttribute.FAMILY, TextAttribute.WEIGHT, TextAttribute.WIDTH, TextAttribute.POSTURE, TextAttribute.SIZE, TextAttribute.TRANSFORM, TextAttribute.SUPERSCRIPT, TextAttribute.CHAR_REPLACEMENT, TextAttribute.FOREGROUND, TextAttribute.BACKGROUND, TextAttribute.UNDERLINE, TextAttribute.STRIKETHROUGH, TextAttribute.RUN_DIRECTION, TextAttribute.BIDI_EMBEDDING, TextAttribute.JUSTIFICATION, TextAttribute.INPUT_METHOD_HIGHLIGHT, TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.SWAP_COLORS, TextAttribute.NUMERIC_SHAPING, TextAttribute.KERNING, TextAttribute.LIGATURES, TextAttribute.TRACKING};
      return var1;
   }

   public Font deriveFont(int var1, float var2) {
      if (this.values == null) {
         return new Font(this.name, var1, var2, this.createdFont, this.font2DHandle);
      } else {
         AttributeValues var3 = this.getAttributeValues().clone();
         int var4 = this.style != var1 ? this.style : -1;
         applyStyle(var1, var3);
         var3.setSize(var2);
         return new Font(var3, (String)null, var4, this.createdFont, this.font2DHandle);
      }
   }

   public Font deriveFont(int var1, AffineTransform var2) {
      AttributeValues var3 = this.getAttributeValues().clone();
      int var4 = this.style != var1 ? this.style : -1;
      applyStyle(var1, var3);
      applyTransform(var2, var3);
      return new Font(var3, (String)null, var4, this.createdFont, this.font2DHandle);
   }

   public Font deriveFont(float var1) {
      if (this.values == null) {
         return new Font(this.name, this.style, var1, this.createdFont, this.font2DHandle);
      } else {
         AttributeValues var2 = this.getAttributeValues().clone();
         var2.setSize(var1);
         return new Font(var2, (String)null, -1, this.createdFont, this.font2DHandle);
      }
   }

   public Font deriveFont(AffineTransform var1) {
      AttributeValues var2 = this.getAttributeValues().clone();
      applyTransform(var1, var2);
      return new Font(var2, (String)null, -1, this.createdFont, this.font2DHandle);
   }

   public Font deriveFont(int var1) {
      if (this.values == null) {
         return new Font(this.name, var1, (float)this.size, this.createdFont, this.font2DHandle);
      } else {
         AttributeValues var2 = this.getAttributeValues().clone();
         int var3 = this.style != var1 ? this.style : -1;
         applyStyle(var1, var2);
         return new Font(var2, (String)null, var3, this.createdFont, this.font2DHandle);
      }
   }

   public Font deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> var1) {
      if (var1 == null) {
         return this;
      } else {
         AttributeValues var2 = this.getAttributeValues().clone();
         var2.merge(var1, RECOGNIZED_MASK);
         return new Font(var2, this.name, this.style, this.createdFont, this.font2DHandle);
      }
   }

   public boolean canDisplay(char var1) {
      return this.getFont2D().canDisplay(var1);
   }

   public boolean canDisplay(int var1) {
      if (!Character.isValidCodePoint(var1)) {
         throw new IllegalArgumentException("invalid code point: " + Integer.toHexString(var1));
      } else {
         return this.getFont2D().canDisplay(var1);
      }
   }

   public int canDisplayUpTo(String var1) {
      Font2D var2 = this.getFont2D();
      int var3 = var1.length();

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var1.charAt(var4);
         if (!var2.canDisplay(var5)) {
            if (!Character.isHighSurrogate(var5)) {
               return var4;
            }

            if (!var2.canDisplay(var1.codePointAt(var4))) {
               return var4;
            }

            ++var4;
         }
      }

      return -1;
   }

   public int canDisplayUpTo(char[] var1, int var2, int var3) {
      Font2D var4 = this.getFont2D();

      for(int var5 = var2; var5 < var3; ++var5) {
         char var6 = var1[var5];
         if (!var4.canDisplay(var6)) {
            if (!Character.isHighSurrogate(var6)) {
               return var5;
            }

            if (!var4.canDisplay(Character.codePointAt(var1, var5, var3))) {
               return var5;
            }

            ++var5;
         }
      }

      return -1;
   }

   public int canDisplayUpTo(CharacterIterator var1, int var2, int var3) {
      Font2D var4 = this.getFont2D();
      char var5 = var1.setIndex(var2);

      for(int var6 = var2; var6 < var3; var5 = var1.next()) {
         if (!var4.canDisplay(var5)) {
            if (!Character.isHighSurrogate(var5)) {
               return var6;
            }

            char var7 = var1.next();
            if (!Character.isLowSurrogate(var7)) {
               return var6;
            }

            if (!var4.canDisplay(Character.toCodePoint(var5, var7))) {
               return var6;
            }

            ++var6;
         }

         ++var6;
      }

      return -1;
   }

   public float getItalicAngle() {
      return this.getItalicAngle((FontRenderContext)null);
   }

   private float getItalicAngle(FontRenderContext var1) {
      Object var2;
      Object var3;
      if (var1 == null) {
         var2 = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
         var3 = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
      } else {
         var2 = var1.getAntiAliasingHint();
         var3 = var1.getFractionalMetricsHint();
      }

      return this.getFont2D().getItalicAngle(this, identityTx, var2, var3);
   }

   public boolean hasUniformLineMetrics() {
      return false;
   }

   private FontLineMetrics defaultLineMetrics(FontRenderContext var1) {
      FontLineMetrics var2 = null;
      if (this.flmref == null || (var2 = (FontLineMetrics)this.flmref.get()) == null || !var2.frc.equals(var1)) {
         float[] var3 = new float[8];
         this.getFont2D().getFontMetrics(this, identityTx, var1.getAntiAliasingHint(), var1.getFractionalMetricsHint(), var3);
         float var4 = var3[0];
         float var5 = var3[1];
         float var6 = var3[2];
         float var7 = 0.0F;
         if (this.values != null && this.values.getSuperscript() != 0) {
            var7 = (float)this.getTransform().getTranslateY();
            var4 -= var7;
            var5 += var7;
         }

         float var8 = var4 + var5 + var6;
         byte var9 = 0;
         float[] var10 = new float[]{0.0F, (var5 / 2.0F - var4) / 2.0F, -var4};
         float var11 = var3[4];
         float var12 = var3[5];
         float var13 = var3[6];
         float var14 = var3[7];
         float var15 = this.getItalicAngle(var1);
         if (this.isTransformed()) {
            AffineTransform var16 = this.values.getCharTransform();
            if (var16 != null) {
               Point2D.Float var17 = new Point2D.Float();
               var17.setLocation(0.0F, var11);
               var16.deltaTransform(var17, var17);
               var11 = var17.y;
               var17.setLocation(0.0F, var12);
               var16.deltaTransform(var17, var17);
               var12 = var17.y;
               var17.setLocation(0.0F, var13);
               var16.deltaTransform(var17, var17);
               var13 = var17.y;
               var17.setLocation(0.0F, var14);
               var16.deltaTransform(var17, var17);
               var14 = var17.y;
            }
         }

         var11 += var7;
         var13 += var7;
         CoreMetrics var18 = new CoreMetrics(var4, var5, var6, var8, var9, var10, var11, var12, var13, var14, var7, var15);
         var2 = new FontLineMetrics(0, var18, var1);
         this.flmref = new SoftReference(var2);
      }

      return (FontLineMetrics)var2.clone();
   }

   public LineMetrics getLineMetrics(String var1, FontRenderContext var2) {
      FontLineMetrics var3 = this.defaultLineMetrics(var2);
      var3.numchars = var1.length();
      return var3;
   }

   public LineMetrics getLineMetrics(String var1, int var2, int var3, FontRenderContext var4) {
      FontLineMetrics var5 = this.defaultLineMetrics(var4);
      int var6 = var3 - var2;
      var5.numchars = var6 < 0 ? 0 : var6;
      return var5;
   }

   public LineMetrics getLineMetrics(char[] var1, int var2, int var3, FontRenderContext var4) {
      FontLineMetrics var5 = this.defaultLineMetrics(var4);
      int var6 = var3 - var2;
      var5.numchars = var6 < 0 ? 0 : var6;
      return var5;
   }

   public LineMetrics getLineMetrics(CharacterIterator var1, int var2, int var3, FontRenderContext var4) {
      FontLineMetrics var5 = this.defaultLineMetrics(var4);
      int var6 = var3 - var2;
      var5.numchars = var6 < 0 ? 0 : var6;
      return var5;
   }

   public Rectangle2D getStringBounds(String var1, FontRenderContext var2) {
      char[] var3 = var1.toCharArray();
      return this.getStringBounds((char[])var3, 0, var3.length, var2);
   }

   public Rectangle2D getStringBounds(String var1, int var2, int var3, FontRenderContext var4) {
      String var5 = var1.substring(var2, var3);
      return this.getStringBounds(var5, var4);
   }

   public Rectangle2D getStringBounds(char[] var1, int var2, int var3, FontRenderContext var4) {
      if (var2 < 0) {
         throw new IndexOutOfBoundsException("beginIndex: " + var2);
      } else if (var3 > var1.length) {
         throw new IndexOutOfBoundsException("limit: " + var3);
      } else if (var2 > var3) {
         throw new IndexOutOfBoundsException("range length: " + (var3 - var2));
      } else {
         boolean var5 = this.values == null || this.values.getKerning() == 0 && this.values.getLigatures() == 0 && this.values.getBaselineTransform() == null;
         if (var5) {
            var5 = !FontUtilities.isComplexText(var1, var2, var3);
         }

         if (var5) {
            StandardGlyphVector var8 = new StandardGlyphVector(this, var1, var2, var3 - var2, var4);
            return var8.getLogicalBounds();
         } else {
            String var6 = new String(var1, var2, var3 - var2);
            TextLayout var7 = new TextLayout(var6, this, var4);
            return new Rectangle2D.Float(0.0F, -var7.getAscent(), var7.getAdvance(), var7.getAscent() + var7.getDescent() + var7.getLeading());
         }
      }
   }

   public Rectangle2D getStringBounds(CharacterIterator var1, int var2, int var3, FontRenderContext var4) {
      int var5 = var1.getBeginIndex();
      int var6 = var1.getEndIndex();
      if (var2 < var5) {
         throw new IndexOutOfBoundsException("beginIndex: " + var2);
      } else if (var3 > var6) {
         throw new IndexOutOfBoundsException("limit: " + var3);
      } else if (var2 > var3) {
         throw new IndexOutOfBoundsException("range length: " + (var3 - var2));
      } else {
         char[] var7 = new char[var3 - var2];
         var1.setIndex(var2);

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var7[var8] = var1.current();
            var1.next();
         }

         return this.getStringBounds((char[])var7, 0, var7.length, var4);
      }
   }

   public Rectangle2D getMaxCharBounds(FontRenderContext var1) {
      float[] var2 = new float[4];
      this.getFont2D().getFontMetrics(this, var1, var2);
      return new Rectangle2D.Float(0.0F, -var2[0], var2[3], var2[0] + var2[1] + var2[2]);
   }

   public GlyphVector createGlyphVector(FontRenderContext var1, String var2) {
      return new StandardGlyphVector(this, var2, var1);
   }

   public GlyphVector createGlyphVector(FontRenderContext var1, char[] var2) {
      return new StandardGlyphVector(this, var2, var1);
   }

   public GlyphVector createGlyphVector(FontRenderContext var1, CharacterIterator var2) {
      return new StandardGlyphVector(this, var2, var1);
   }

   public GlyphVector createGlyphVector(FontRenderContext var1, int[] var2) {
      return new StandardGlyphVector(this, var2, var1);
   }

   public GlyphVector layoutGlyphVector(FontRenderContext var1, char[] var2, int var3, int var4, int var5) {
      GlyphLayout var6 = GlyphLayout.get((GlyphLayout.LayoutEngineFactory)null);
      StandardGlyphVector var7 = var6.layout(this, var1, var2, var3, var4 - var3, var5, (StandardGlyphVector)null);
      GlyphLayout.done(var6);
      return var7;
   }

   private static void applyTransform(AffineTransform var0, AttributeValues var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("transform must not be null");
      } else {
         var1.setTransform(var0);
      }
   }

   private static void applyStyle(int var0, AttributeValues var1) {
      var1.setWeight((var0 & 1) != 0 ? 2.0F : 1.0F);
      var1.setPosture((var0 & 2) != 0 ? 0.2F : 0.0F);
   }

   private static native void initIDs();

   static {
      Toolkit.loadLibraries();
      initIDs();
      FontAccess.setFontAccess(new Font.FontAccessImpl());
      identityTx = new AffineTransform();
      RECOGNIZED_MASK = AttributeValues.MASK_ALL & ~AttributeValues.getMask(EAttribute.EFONT);
      PRIMARY_MASK = AttributeValues.getMask(EAttribute.EFAMILY, EAttribute.EWEIGHT, EAttribute.EWIDTH, EAttribute.EPOSTURE, EAttribute.ESIZE, EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.ETRACKING);
      SECONDARY_MASK = RECOGNIZED_MASK & ~PRIMARY_MASK;
      LAYOUT_MASK = AttributeValues.getMask(EAttribute.ECHAR_REPLACEMENT, EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.EUNDERLINE, EAttribute.ESTRIKETHROUGH, EAttribute.ERUN_DIRECTION, EAttribute.EBIDI_EMBEDDING, EAttribute.EJUSTIFICATION, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE, EAttribute.ESWAP_COLORS, EAttribute.ENUMERIC_SHAPING, EAttribute.EKERNING, EAttribute.ELIGATURES, EAttribute.ETRACKING, EAttribute.ESUPERSCRIPT);
      EXTRA_MASK = AttributeValues.getMask(EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.EWIDTH);
      ssinfo = new float[]{0.0F, 0.375F, 0.625F, 0.7916667F, 0.9027778F, 0.9768519F, 1.0262346F, 1.0591564F};
   }

   private static class FontAccessImpl extends FontAccess {
      private FontAccessImpl() {
      }

      public Font2D getFont2D(Font var1) {
         return var1.getFont2D();
      }

      public void setFont2D(Font var1, Font2DHandle var2) {
         var1.font2DHandle = var2;
      }

      public void setCreatedFont(Font var1) {
         var1.createdFont = true;
      }

      public boolean isCreatedFont(Font var1) {
         return var1.createdFont;
      }

      // $FF: synthetic method
      FontAccessImpl(Object var1) {
         this();
      }
   }
}
