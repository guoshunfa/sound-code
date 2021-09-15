package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.util.HashMap;

public final class CompositeType {
   private static int unusedUID = 1;
   private static final HashMap<String, Integer> compositeUIDMap = new HashMap(100);
   public static final String DESC_ANY = "Any CompositeContext";
   public static final String DESC_XOR = "XOR mode";
   public static final String DESC_CLEAR = "Porter-Duff Clear";
   public static final String DESC_SRC = "Porter-Duff Src";
   public static final String DESC_DST = "Porter-Duff Dst";
   public static final String DESC_SRC_OVER = "Porter-Duff Src Over Dst";
   public static final String DESC_DST_OVER = "Porter-Duff Dst Over Src";
   public static final String DESC_SRC_IN = "Porter-Duff Src In Dst";
   public static final String DESC_DST_IN = "Porter-Duff Dst In Src";
   public static final String DESC_SRC_OUT = "Porter-Duff Src HeldOutBy Dst";
   public static final String DESC_DST_OUT = "Porter-Duff Dst HeldOutBy Src";
   public static final String DESC_SRC_ATOP = "Porter-Duff Src Atop Dst";
   public static final String DESC_DST_ATOP = "Porter-Duff Dst Atop Src";
   public static final String DESC_ALPHA_XOR = "Porter-Duff Xor";
   public static final String DESC_SRC_NO_EA = "Porter-Duff Src, No Extra Alpha";
   public static final String DESC_SRC_OVER_NO_EA = "Porter-Duff SrcOverDst, No Extra Alpha";
   public static final String DESC_ANY_ALPHA = "Any AlphaComposite Rule";
   public static final CompositeType Any = new CompositeType((CompositeType)null, "Any CompositeContext");
   public static final CompositeType General;
   public static final CompositeType AnyAlpha;
   public static final CompositeType Xor;
   public static final CompositeType Clear;
   public static final CompositeType Src;
   public static final CompositeType Dst;
   public static final CompositeType SrcOver;
   public static final CompositeType DstOver;
   public static final CompositeType SrcIn;
   public static final CompositeType DstIn;
   public static final CompositeType SrcOut;
   public static final CompositeType DstOut;
   public static final CompositeType SrcAtop;
   public static final CompositeType DstAtop;
   public static final CompositeType AlphaXor;
   public static final CompositeType SrcNoEa;
   public static final CompositeType SrcOverNoEa;
   public static final CompositeType OpaqueSrcOverNoEa;
   private int uniqueID;
   private String desc;
   private CompositeType next;

   public CompositeType deriveSubType(String var1) {
      return new CompositeType(this, var1);
   }

   public static CompositeType forAlphaComposite(AlphaComposite var0) {
      switch(var0.getRule()) {
      case 1:
         return Clear;
      case 2:
         if (var0.getAlpha() >= 1.0F) {
            return SrcNoEa;
         }

         return Src;
      case 3:
         if (var0.getAlpha() >= 1.0F) {
            return SrcOverNoEa;
         }

         return SrcOver;
      case 4:
         return DstOver;
      case 5:
         return SrcIn;
      case 6:
         return DstIn;
      case 7:
         return SrcOut;
      case 8:
         return DstOut;
      case 9:
         return Dst;
      case 10:
         return SrcAtop;
      case 11:
         return DstAtop;
      case 12:
         return AlphaXor;
      default:
         throw new InternalError("Unrecognized alpha rule");
      }
   }

   private CompositeType(CompositeType var1, String var2) {
      this.next = var1;
      this.desc = var2;
      this.uniqueID = makeUniqueID(var2);
   }

   public static final synchronized int makeUniqueID(String var0) {
      Integer var1 = (Integer)compositeUIDMap.get(var0);
      if (var1 == null) {
         if (unusedUID > 255) {
            throw new InternalError("composite type id overflow");
         }

         var1 = unusedUID++;
         compositeUIDMap.put(var0, var1);
      }

      return var1;
   }

   public int getUniqueID() {
      return this.uniqueID;
   }

   public String getDescriptor() {
      return this.desc;
   }

   public CompositeType getSuperType() {
      return this.next;
   }

   public int hashCode() {
      return this.desc.hashCode();
   }

   public boolean isDerivedFrom(CompositeType var1) {
      CompositeType var2 = this;

      while(var2.desc != var1.desc) {
         var2 = var2.next;
         if (var2 == null) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof CompositeType) {
         return ((CompositeType)var1).uniqueID == this.uniqueID;
      } else {
         return false;
      }
   }

   public String toString() {
      return this.desc;
   }

   static {
      General = Any;
      AnyAlpha = General.deriveSubType("Any AlphaComposite Rule");
      Xor = General.deriveSubType("XOR mode");
      Clear = AnyAlpha.deriveSubType("Porter-Duff Clear");
      Src = AnyAlpha.deriveSubType("Porter-Duff Src");
      Dst = AnyAlpha.deriveSubType("Porter-Duff Dst");
      SrcOver = AnyAlpha.deriveSubType("Porter-Duff Src Over Dst");
      DstOver = AnyAlpha.deriveSubType("Porter-Duff Dst Over Src");
      SrcIn = AnyAlpha.deriveSubType("Porter-Duff Src In Dst");
      DstIn = AnyAlpha.deriveSubType("Porter-Duff Dst In Src");
      SrcOut = AnyAlpha.deriveSubType("Porter-Duff Src HeldOutBy Dst");
      DstOut = AnyAlpha.deriveSubType("Porter-Duff Dst HeldOutBy Src");
      SrcAtop = AnyAlpha.deriveSubType("Porter-Duff Src Atop Dst");
      DstAtop = AnyAlpha.deriveSubType("Porter-Duff Dst Atop Src");
      AlphaXor = AnyAlpha.deriveSubType("Porter-Duff Xor");
      SrcNoEa = Src.deriveSubType("Porter-Duff Src, No Extra Alpha");
      SrcOverNoEa = SrcOver.deriveSubType("Porter-Duff SrcOverDst, No Extra Alpha");
      OpaqueSrcOverNoEa = SrcOverNoEa.deriveSubType("Porter-Duff Src").deriveSubType("Porter-Duff Src, No Extra Alpha");
   }
}
