package java.awt.font;

import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public final class TextAttribute extends AttributedCharacterIterator.Attribute {
   private static final Map<String, TextAttribute> instanceMap = new HashMap(29);
   static final long serialVersionUID = 7744112784117861702L;
   public static final TextAttribute FAMILY = new TextAttribute("family");
   public static final TextAttribute WEIGHT = new TextAttribute("weight");
   public static final Float WEIGHT_EXTRA_LIGHT = 0.5F;
   public static final Float WEIGHT_LIGHT = 0.75F;
   public static final Float WEIGHT_DEMILIGHT = 0.875F;
   public static final Float WEIGHT_REGULAR = 1.0F;
   public static final Float WEIGHT_SEMIBOLD = 1.25F;
   public static final Float WEIGHT_MEDIUM = 1.5F;
   public static final Float WEIGHT_DEMIBOLD = 1.75F;
   public static final Float WEIGHT_BOLD = 2.0F;
   public static final Float WEIGHT_HEAVY = 2.25F;
   public static final Float WEIGHT_EXTRABOLD = 2.5F;
   public static final Float WEIGHT_ULTRABOLD = 2.75F;
   public static final TextAttribute WIDTH = new TextAttribute("width");
   public static final Float WIDTH_CONDENSED = 0.75F;
   public static final Float WIDTH_SEMI_CONDENSED = 0.875F;
   public static final Float WIDTH_REGULAR = 1.0F;
   public static final Float WIDTH_SEMI_EXTENDED = 1.25F;
   public static final Float WIDTH_EXTENDED = 1.5F;
   public static final TextAttribute POSTURE = new TextAttribute("posture");
   public static final Float POSTURE_REGULAR = 0.0F;
   public static final Float POSTURE_OBLIQUE = 0.2F;
   public static final TextAttribute SIZE = new TextAttribute("size");
   public static final TextAttribute TRANSFORM = new TextAttribute("transform");
   public static final TextAttribute SUPERSCRIPT = new TextAttribute("superscript");
   public static final Integer SUPERSCRIPT_SUPER = 1;
   public static final Integer SUPERSCRIPT_SUB = -1;
   public static final TextAttribute FONT = new TextAttribute("font");
   public static final TextAttribute CHAR_REPLACEMENT = new TextAttribute("char_replacement");
   public static final TextAttribute FOREGROUND = new TextAttribute("foreground");
   public static final TextAttribute BACKGROUND = new TextAttribute("background");
   public static final TextAttribute UNDERLINE = new TextAttribute("underline");
   public static final Integer UNDERLINE_ON = 0;
   public static final TextAttribute STRIKETHROUGH = new TextAttribute("strikethrough");
   public static final Boolean STRIKETHROUGH_ON;
   public static final TextAttribute RUN_DIRECTION;
   public static final Boolean RUN_DIRECTION_LTR;
   public static final Boolean RUN_DIRECTION_RTL;
   public static final TextAttribute BIDI_EMBEDDING;
   public static final TextAttribute JUSTIFICATION;
   public static final Float JUSTIFICATION_FULL;
   public static final Float JUSTIFICATION_NONE;
   public static final TextAttribute INPUT_METHOD_HIGHLIGHT;
   public static final TextAttribute INPUT_METHOD_UNDERLINE;
   public static final Integer UNDERLINE_LOW_ONE_PIXEL;
   public static final Integer UNDERLINE_LOW_TWO_PIXEL;
   public static final Integer UNDERLINE_LOW_DOTTED;
   public static final Integer UNDERLINE_LOW_GRAY;
   public static final Integer UNDERLINE_LOW_DASHED;
   public static final TextAttribute SWAP_COLORS;
   public static final Boolean SWAP_COLORS_ON;
   public static final TextAttribute NUMERIC_SHAPING;
   public static final TextAttribute KERNING;
   public static final Integer KERNING_ON;
   public static final TextAttribute LIGATURES;
   public static final Integer LIGATURES_ON;
   public static final TextAttribute TRACKING;
   public static final Float TRACKING_TIGHT;
   public static final Float TRACKING_LOOSE;

   protected TextAttribute(String var1) {
      super(var1);
      if (this.getClass() == TextAttribute.class) {
         instanceMap.put(var1, this);
      }

   }

   protected Object readResolve() throws InvalidObjectException {
      if (this.getClass() != TextAttribute.class) {
         throw new InvalidObjectException("subclass didn't correctly implement readResolve");
      } else {
         TextAttribute var1 = (TextAttribute)instanceMap.get(this.getName());
         if (var1 != null) {
            return var1;
         } else {
            throw new InvalidObjectException("unknown attribute name");
         }
      }
   }

   static {
      STRIKETHROUGH_ON = Boolean.TRUE;
      RUN_DIRECTION = new TextAttribute("run_direction");
      RUN_DIRECTION_LTR = Boolean.FALSE;
      RUN_DIRECTION_RTL = Boolean.TRUE;
      BIDI_EMBEDDING = new TextAttribute("bidi_embedding");
      JUSTIFICATION = new TextAttribute("justification");
      JUSTIFICATION_FULL = 1.0F;
      JUSTIFICATION_NONE = 0.0F;
      INPUT_METHOD_HIGHLIGHT = new TextAttribute("input method highlight");
      INPUT_METHOD_UNDERLINE = new TextAttribute("input method underline");
      UNDERLINE_LOW_ONE_PIXEL = 1;
      UNDERLINE_LOW_TWO_PIXEL = 2;
      UNDERLINE_LOW_DOTTED = 3;
      UNDERLINE_LOW_GRAY = 4;
      UNDERLINE_LOW_DASHED = 5;
      SWAP_COLORS = new TextAttribute("swap_colors");
      SWAP_COLORS_ON = Boolean.TRUE;
      NUMERIC_SHAPING = new TextAttribute("numeric_shaping");
      KERNING = new TextAttribute("kerning");
      KERNING_ON = 1;
      LIGATURES = new TextAttribute("ligatures");
      LIGATURES_ON = 1;
      TRACKING = new TextAttribute("tracking");
      TRACKING_TIGHT = -0.04F;
      TRACKING_LOOSE = 0.04F;
   }
}
