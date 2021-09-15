package sun.applet;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;

class AppletPropsErrorDialog extends Dialog {
   public AppletPropsErrorDialog(Frame var1, String var2, String var3, String var4) {
      super(var1, var2, true);
      Panel var5 = new Panel();
      this.add("Center", new Label(var3));
      var5.add(new Button(var4));
      this.add("South", var5);
      this.pack();
      Dimension var6 = this.size();
      Rectangle var7 = var1.bounds();
      this.move(var7.x + (var7.width - var6.width) / 2, var7.y + (var7.height - var6.height) / 2);
   }

   public boolean action(Event var1, Object var2) {
      this.hide();
      this.dispose();
      return true;
   }
}
