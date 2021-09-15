package java.awt.im;

import java.awt.font.TextAttribute;
import java.util.Map;

public class InputMethodHighlight {
   public static final int RAW_TEXT = 0;
   public static final int CONVERTED_TEXT = 1;
   public static final InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 0);
   public static final InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 0);
   public static final InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 1);
   public static final InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 1);
   private boolean selected;
   private int state;
   private int variation;
   private Map<TextAttribute, ?> style;

   public InputMethodHighlight(boolean var1, int var2) {
      this(var1, var2, 0, (Map)null);
   }

   public InputMethodHighlight(boolean var1, int var2, int var3) {
      this(var1, var2, var3, (Map)null);
   }

   public InputMethodHighlight(boolean var1, int var2, int var3, Map<TextAttribute, ?> var4) {
      this.selected = var1;
      if (var2 != 0 && var2 != 1) {
         throw new IllegalArgumentException("unknown input method highlight state");
      } else {
         this.state = var2;
         this.variation = var3;
         this.style = var4;
      }
   }

   public boolean isSelected() {
      return this.selected;
   }

   public int getState() {
      return this.state;
   }

   public int getVariation() {
      return this.variation;
   }

   public Map<TextAttribute, ?> getStyle() {
      return this.style;
   }
}
