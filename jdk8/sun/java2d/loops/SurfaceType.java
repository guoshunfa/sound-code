package sun.java2d.loops;

import java.awt.image.ColorModel;
import java.util.HashMap;
import sun.awt.image.PixelConverter;

public final class SurfaceType {
   private static int unusedUID = 1;
   private static HashMap<String, Integer> surfaceUIDMap = new HashMap(100);
   public static final String DESC_ANY = "Any Surface";
   public static final String DESC_INT_RGB = "Integer RGB";
   public static final String DESC_INT_ARGB = "Integer ARGB";
   public static final String DESC_INT_ARGB_PRE = "Integer ARGB Premultiplied";
   public static final String DESC_INT_BGR = "Integer BGR";
   public static final String DESC_3BYTE_BGR = "3 Byte BGR";
   public static final String DESC_4BYTE_ABGR = "4 Byte ABGR";
   public static final String DESC_4BYTE_ABGR_PRE = "4 Byte ABGR Premultiplied";
   public static final String DESC_USHORT_565_RGB = "Short 565 RGB";
   public static final String DESC_USHORT_555_RGB = "Short 555 RGB";
   public static final String DESC_USHORT_555_RGBx = "Short 555 RGBx";
   public static final String DESC_USHORT_4444_ARGB = "Short 4444 ARGB";
   public static final String DESC_BYTE_GRAY = "8-bit Gray";
   public static final String DESC_USHORT_INDEXED = "16-bit Indexed";
   public static final String DESC_USHORT_GRAY = "16-bit Gray";
   public static final String DESC_BYTE_BINARY = "Packed Binary Bitmap";
   public static final String DESC_BYTE_INDEXED = "8-bit Indexed";
   public static final String DESC_ANY_INT = "Any Discrete Integer";
   public static final String DESC_ANY_SHORT = "Any Discrete Short";
   public static final String DESC_ANY_BYTE = "Any Discrete Byte";
   public static final String DESC_ANY_3BYTE = "Any 3 Byte Component";
   public static final String DESC_ANY_4BYTE = "Any 4 Byte Component";
   public static final String DESC_ANY_INT_DCM = "Any Integer DCM";
   public static final String DESC_INT_RGBx = "Integer RGBx";
   public static final String DESC_INT_BGRx = "Integer BGRx";
   public static final String DESC_3BYTE_RGB = "3 Byte RGB";
   public static final String DESC_INT_ARGB_BM = "Int ARGB (Bitmask)";
   public static final String DESC_BYTE_INDEXED_BM = "8-bit Indexed (Bitmask)";
   public static final String DESC_BYTE_INDEXED_OPAQUE = "8-bit Indexed (Opaque)";
   public static final String DESC_INDEX8_GRAY = "8-bit Palettized Gray";
   public static final String DESC_INDEX12_GRAY = "12-bit Palettized Gray";
   public static final String DESC_BYTE_BINARY_1BIT = "Packed Binary 1-bit Bitmap";
   public static final String DESC_BYTE_BINARY_2BIT = "Packed Binary 2-bit Bitmap";
   public static final String DESC_BYTE_BINARY_4BIT = "Packed Binary 4-bit Bitmap";
   public static final String DESC_ANY_PAINT = "Paint Object";
   public static final String DESC_ANY_COLOR = "Single Color";
   public static final String DESC_OPAQUE_COLOR = "Opaque Color";
   public static final String DESC_GRADIENT_PAINT = "Gradient Paint";
   public static final String DESC_OPAQUE_GRADIENT_PAINT = "Opaque Gradient Paint";
   public static final String DESC_TEXTURE_PAINT = "Texture Paint";
   public static final String DESC_OPAQUE_TEXTURE_PAINT = "Opaque Texture Paint";
   public static final String DESC_LINEAR_GRADIENT_PAINT = "Linear Gradient Paint";
   public static final String DESC_OPAQUE_LINEAR_GRADIENT_PAINT = "Opaque Linear Gradient Paint";
   public static final String DESC_RADIAL_GRADIENT_PAINT = "Radial Gradient Paint";
   public static final String DESC_OPAQUE_RADIAL_GRADIENT_PAINT = "Opaque Radial Gradient Paint";
   public static final SurfaceType Any;
   public static final SurfaceType AnyInt;
   public static final SurfaceType AnyShort;
   public static final SurfaceType AnyByte;
   public static final SurfaceType AnyByteBinary;
   public static final SurfaceType Any3Byte;
   public static final SurfaceType Any4Byte;
   public static final SurfaceType AnyDcm;
   public static final SurfaceType Custom;
   public static final SurfaceType IntRgb;
   public static final SurfaceType IntArgb;
   public static final SurfaceType IntArgbPre;
   public static final SurfaceType IntBgr;
   public static final SurfaceType ThreeByteBgr;
   public static final SurfaceType FourByteAbgr;
   public static final SurfaceType FourByteAbgrPre;
   public static final SurfaceType Ushort565Rgb;
   public static final SurfaceType Ushort555Rgb;
   public static final SurfaceType Ushort555Rgbx;
   public static final SurfaceType Ushort4444Argb;
   public static final SurfaceType UshortIndexed;
   public static final SurfaceType ByteGray;
   public static final SurfaceType UshortGray;
   public static final SurfaceType ByteBinary1Bit;
   public static final SurfaceType ByteBinary2Bit;
   public static final SurfaceType ByteBinary4Bit;
   public static final SurfaceType ByteIndexed;
   public static final SurfaceType IntRgbx;
   public static final SurfaceType IntBgrx;
   public static final SurfaceType ThreeByteRgb;
   public static final SurfaceType IntArgbBm;
   public static final SurfaceType ByteIndexedBm;
   public static final SurfaceType ByteIndexedOpaque;
   public static final SurfaceType Index8Gray;
   public static final SurfaceType Index12Gray;
   public static final SurfaceType AnyPaint;
   public static final SurfaceType AnyColor;
   public static final SurfaceType OpaqueColor;
   public static final SurfaceType GradientPaint;
   public static final SurfaceType OpaqueGradientPaint;
   public static final SurfaceType LinearGradientPaint;
   public static final SurfaceType OpaqueLinearGradientPaint;
   public static final SurfaceType RadialGradientPaint;
   public static final SurfaceType OpaqueRadialGradientPaint;
   public static final SurfaceType TexturePaint;
   public static final SurfaceType OpaqueTexturePaint;
   private int uniqueID;
   private String desc;
   private SurfaceType next;
   protected PixelConverter pixelConverter;

   public SurfaceType deriveSubType(String var1) {
      return new SurfaceType(this, var1);
   }

   public SurfaceType deriveSubType(String var1, PixelConverter var2) {
      return new SurfaceType(this, var1, var2);
   }

   private SurfaceType(SurfaceType var1, String var2, PixelConverter var3) {
      this.next = var1;
      this.desc = var2;
      this.uniqueID = makeUniqueID(var2);
      this.pixelConverter = var3;
   }

   private SurfaceType(SurfaceType var1, String var2) {
      this.next = var1;
      this.desc = var2;
      this.uniqueID = makeUniqueID(var2);
      this.pixelConverter = var1.pixelConverter;
   }

   public static final synchronized int makeUniqueID(String var0) {
      Integer var1 = (Integer)surfaceUIDMap.get(var0);
      if (var1 == null) {
         if (unusedUID > 255) {
            throw new InternalError("surface type id overflow");
         }

         var1 = unusedUID++;
         surfaceUIDMap.put(var0, var1);
      }

      return var1;
   }

   public int getUniqueID() {
      return this.uniqueID;
   }

   public String getDescriptor() {
      return this.desc;
   }

   public SurfaceType getSuperType() {
      return this.next;
   }

   public PixelConverter getPixelConverter() {
      return this.pixelConverter;
   }

   public int pixelFor(int var1, ColorModel var2) {
      return this.pixelConverter.rgbToPixel(var1, var2);
   }

   public int rgbFor(int var1, ColorModel var2) {
      return this.pixelConverter.pixelToRgb(var1, var2);
   }

   public int getAlphaMask() {
      return this.pixelConverter.getAlphaMask();
   }

   public int hashCode() {
      return this.desc.hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof SurfaceType) {
         return ((SurfaceType)var1).uniqueID == this.uniqueID;
      } else {
         return false;
      }
   }

   public String toString() {
      return this.desc;
   }

   static {
      Any = new SurfaceType((SurfaceType)null, "Any Surface", PixelConverter.instance);
      AnyInt = Any.deriveSubType("Any Discrete Integer");
      AnyShort = Any.deriveSubType("Any Discrete Short");
      AnyByte = Any.deriveSubType("Any Discrete Byte");
      AnyByteBinary = Any.deriveSubType("Packed Binary Bitmap");
      Any3Byte = Any.deriveSubType("Any 3 Byte Component");
      Any4Byte = Any.deriveSubType("Any 4 Byte Component");
      AnyDcm = AnyInt.deriveSubType("Any Integer DCM");
      Custom = Any;
      IntRgb = AnyDcm.deriveSubType("Integer RGB", PixelConverter.Xrgb.instance);
      IntArgb = AnyDcm.deriveSubType("Integer ARGB", PixelConverter.Argb.instance);
      IntArgbPre = AnyDcm.deriveSubType("Integer ARGB Premultiplied", PixelConverter.ArgbPre.instance);
      IntBgr = AnyDcm.deriveSubType("Integer BGR", PixelConverter.Xbgr.instance);
      ThreeByteBgr = Any3Byte.deriveSubType("3 Byte BGR", PixelConverter.Xrgb.instance);
      FourByteAbgr = Any4Byte.deriveSubType("4 Byte ABGR", PixelConverter.Rgba.instance);
      FourByteAbgrPre = Any4Byte.deriveSubType("4 Byte ABGR Premultiplied", PixelConverter.RgbaPre.instance);
      Ushort565Rgb = AnyShort.deriveSubType("Short 565 RGB", PixelConverter.Ushort565Rgb.instance);
      Ushort555Rgb = AnyShort.deriveSubType("Short 555 RGB", PixelConverter.Ushort555Rgb.instance);
      Ushort555Rgbx = AnyShort.deriveSubType("Short 555 RGBx", PixelConverter.Ushort555Rgbx.instance);
      Ushort4444Argb = AnyShort.deriveSubType("Short 4444 ARGB", PixelConverter.Ushort4444Argb.instance);
      UshortIndexed = AnyShort.deriveSubType("16-bit Indexed");
      ByteGray = AnyByte.deriveSubType("8-bit Gray", PixelConverter.ByteGray.instance);
      UshortGray = AnyShort.deriveSubType("16-bit Gray", PixelConverter.UshortGray.instance);
      ByteBinary1Bit = AnyByteBinary.deriveSubType("Packed Binary 1-bit Bitmap");
      ByteBinary2Bit = AnyByteBinary.deriveSubType("Packed Binary 2-bit Bitmap");
      ByteBinary4Bit = AnyByteBinary.deriveSubType("Packed Binary 4-bit Bitmap");
      ByteIndexed = AnyByte.deriveSubType("8-bit Indexed");
      IntRgbx = AnyDcm.deriveSubType("Integer RGBx", PixelConverter.Rgbx.instance);
      IntBgrx = AnyDcm.deriveSubType("Integer BGRx", PixelConverter.Bgrx.instance);
      ThreeByteRgb = Any3Byte.deriveSubType("3 Byte RGB", PixelConverter.Xbgr.instance);
      IntArgbBm = AnyDcm.deriveSubType("Int ARGB (Bitmask)", PixelConverter.ArgbBm.instance);
      ByteIndexedBm = ByteIndexed.deriveSubType("8-bit Indexed (Bitmask)");
      ByteIndexedOpaque = ByteIndexedBm.deriveSubType("8-bit Indexed (Opaque)");
      Index8Gray = ByteIndexedOpaque.deriveSubType("8-bit Palettized Gray");
      Index12Gray = Any.deriveSubType("12-bit Palettized Gray");
      AnyPaint = Any.deriveSubType("Paint Object");
      AnyColor = AnyPaint.deriveSubType("Single Color");
      OpaqueColor = AnyColor.deriveSubType("Opaque Color");
      GradientPaint = AnyPaint.deriveSubType("Gradient Paint");
      OpaqueGradientPaint = GradientPaint.deriveSubType("Opaque Gradient Paint");
      LinearGradientPaint = AnyPaint.deriveSubType("Linear Gradient Paint");
      OpaqueLinearGradientPaint = LinearGradientPaint.deriveSubType("Opaque Linear Gradient Paint");
      RadialGradientPaint = AnyPaint.deriveSubType("Radial Gradient Paint");
      OpaqueRadialGradientPaint = RadialGradientPaint.deriveSubType("Opaque Radial Gradient Paint");
      TexturePaint = AnyPaint.deriveSubType("Texture Paint");
      OpaqueTexturePaint = TexturePaint.deriveSubType("Opaque Texture Paint");
   }
}
