package javax.swing.text;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class TextAction extends AbstractAction {
   public TextAction(String var1) {
      super(var1);
   }

   protected final JTextComponent getTextComponent(ActionEvent var1) {
      if (var1 != null) {
         Object var2 = var1.getSource();
         if (var2 instanceof JTextComponent) {
            return (JTextComponent)var2;
         }
      }

      return this.getFocusedComponent();
   }

   public static final Action[] augmentList(Action[] var0, Action[] var1) {
      Hashtable var2 = new Hashtable();
      Action[] var3 = var0;
      int var4 = var0.length;

      int var5;
      Action var6;
      String var7;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         var7 = (String)var6.getValue("Name");
         var2.put(var7 != null ? var7 : "", var6);
      }

      var3 = var1;
      var4 = var1.length;

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         var7 = (String)var6.getValue("Name");
         var2.put(var7 != null ? var7 : "", var6);
      }

      var3 = new Action[var2.size()];
      var4 = 0;

      for(Enumeration var8 = var2.elements(); var8.hasMoreElements(); var3[var4++] = (Action)var8.nextElement()) {
      }

      return var3;
   }

   protected final JTextComponent getFocusedComponent() {
      return JTextComponent.getFocusedComponent();
   }
}
