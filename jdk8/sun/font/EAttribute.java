package sun.font;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;

public enum EAttribute {
   EFAMILY(TextAttribute.FAMILY),
   EWEIGHT(TextAttribute.WEIGHT),
   EWIDTH(TextAttribute.WIDTH),
   EPOSTURE(TextAttribute.POSTURE),
   ESIZE(TextAttribute.SIZE),
   ETRANSFORM(TextAttribute.TRANSFORM),
   ESUPERSCRIPT(TextAttribute.SUPERSCRIPT),
   EFONT(TextAttribute.FONT),
   ECHAR_REPLACEMENT(TextAttribute.CHAR_REPLACEMENT),
   EFOREGROUND(TextAttribute.FOREGROUND),
   EBACKGROUND(TextAttribute.BACKGROUND),
   EUNDERLINE(TextAttribute.UNDERLINE),
   ESTRIKETHROUGH(TextAttribute.STRIKETHROUGH),
   ERUN_DIRECTION(TextAttribute.RUN_DIRECTION),
   EBIDI_EMBEDDING(TextAttribute.BIDI_EMBEDDING),
   EJUSTIFICATION(TextAttribute.JUSTIFICATION),
   EINPUT_METHOD_HIGHLIGHT(TextAttribute.INPUT_METHOD_HIGHLIGHT),
   EINPUT_METHOD_UNDERLINE(TextAttribute.INPUT_METHOD_UNDERLINE),
   ESWAP_COLORS(TextAttribute.SWAP_COLORS),
   ENUMERIC_SHAPING(TextAttribute.NUMERIC_SHAPING),
   EKERNING(TextAttribute.KERNING),
   ELIGATURES(TextAttribute.LIGATURES),
   ETRACKING(TextAttribute.TRACKING),
   EBASELINE_TRANSFORM((TextAttribute)null);

   final int mask = 1 << this.ordinal();
   final TextAttribute att;
   static final EAttribute[] atts = (EAttribute[])EAttribute.class.getEnumConstants();

   private EAttribute(TextAttribute var3) {
      this.att = var3;
   }

   public static EAttribute forAttribute(AttributedCharacterIterator.Attribute var0) {
      EAttribute[] var1 = atts;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EAttribute var4 = var1[var3];
         if (var4.att == var0) {
            return var4;
         }
      }

      return null;
   }

   public String toString() {
      return this.name().substring(1).toLowerCase();
   }
}
