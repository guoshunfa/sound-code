package javax.swing.plaf.synth;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;

class SynthDefaultLookup extends DefaultLookup {
   public Object getDefault(JComponent var1, ComponentUI var2, String var3) {
      if (!(var2 instanceof SynthUI)) {
         Object var6 = super.getDefault(var1, var2, var3);
         return var6;
      } else {
         SynthContext var4 = ((SynthUI)var2).getContext(var1);
         Object var5 = var4.getStyle().get(var4, var3);
         var4.dispose();
         return var5;
      }
   }
}
