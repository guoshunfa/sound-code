package javax.swing.plaf.multi;

import javax.swing.UIDefaults;

class MultiUIDefaults extends UIDefaults {
   MultiUIDefaults(int var1, float var2) {
      super(var1, var2);
   }

   protected void getUIError(String var1) {
      System.err.println("Multiplexing LAF:  " + var1);
   }
}
