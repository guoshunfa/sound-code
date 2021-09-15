package javax.swing.text.html;

import javax.swing.text.Element;

class BRView extends InlineView {
   public BRView(Element var1) {
      super(var1);
   }

   public int getBreakWeight(int var1, float var2, float var3) {
      return var1 == 0 ? 3000 : super.getBreakWeight(var1, var2, var3);
   }
}
