package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;

public class MultiSpinnerUI extends SpinnerUI {
   protected Vector uis = new Vector();

   public ComponentUI[] getUIs() {
      return MultiLookAndFeel.uisToArray(this.uis);
   }

   public boolean contains(JComponent var1, int var2, int var3) {
      boolean var4 = ((ComponentUI)((ComponentUI)this.uis.elementAt(0))).contains(var1, var2, var3);

      for(int var5 = 1; var5 < this.uis.size(); ++var5) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var5))).contains(var1, var2, var3);
      }

      return var4;
   }

   public void update(Graphics var1, JComponent var2) {
      for(int var3 = 0; var3 < this.uis.size(); ++var3) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var3))).update(var1, var2);
      }

   }

   public static ComponentUI createUI(JComponent var0) {
      MultiSpinnerUI var1 = new MultiSpinnerUI();
      return MultiLookAndFeel.createUIs(var1, ((MultiSpinnerUI)var1).uis, var0);
   }

   public void installUI(JComponent var1) {
      for(int var2 = 0; var2 < this.uis.size(); ++var2) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var2))).installUI(var1);
      }

   }

   public void uninstallUI(JComponent var1) {
      for(int var2 = 0; var2 < this.uis.size(); ++var2) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var2))).uninstallUI(var1);
      }

   }

   public void paint(Graphics var1, JComponent var2) {
      for(int var3 = 0; var3 < this.uis.size(); ++var3) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var3))).paint(var1, var2);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = ((ComponentUI)((ComponentUI)this.uis.elementAt(0))).getPreferredSize(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var3))).getPreferredSize(var1);
      }

      return var2;
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = ((ComponentUI)((ComponentUI)this.uis.elementAt(0))).getMinimumSize(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var3))).getMinimumSize(var1);
      }

      return var2;
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = ((ComponentUI)((ComponentUI)this.uis.elementAt(0))).getMaximumSize(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var3))).getMaximumSize(var1);
      }

      return var2;
   }

   public int getAccessibleChildrenCount(JComponent var1) {
      int var2 = ((ComponentUI)((ComponentUI)this.uis.elementAt(0))).getAccessibleChildrenCount(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var3))).getAccessibleChildrenCount(var1);
      }

      return var2;
   }

   public Accessible getAccessibleChild(JComponent var1, int var2) {
      Accessible var3 = ((ComponentUI)((ComponentUI)this.uis.elementAt(0))).getAccessibleChild(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((ComponentUI)((ComponentUI)this.uis.elementAt(var4))).getAccessibleChild(var1, var2);
      }

      return var3;
   }
}
