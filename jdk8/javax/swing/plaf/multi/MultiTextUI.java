package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public class MultiTextUI extends TextUI {
   protected Vector uis = new Vector();

   public ComponentUI[] getUIs() {
      return MultiLookAndFeel.uisToArray(this.uis);
   }

   public String getToolTipText(JTextComponent var1, Point var2) {
      String var3 = ((TextUI)((TextUI)this.uis.elementAt(0))).getToolTipText(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((TextUI)((TextUI)this.uis.elementAt(var4))).getToolTipText(var1, var2);
      }

      return var3;
   }

   public Rectangle modelToView(JTextComponent var1, int var2) throws BadLocationException {
      Rectangle var3 = ((TextUI)((TextUI)this.uis.elementAt(0))).modelToView(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((TextUI)((TextUI)this.uis.elementAt(var4))).modelToView(var1, var2);
      }

      return var3;
   }

   public Rectangle modelToView(JTextComponent var1, int var2, Position.Bias var3) throws BadLocationException {
      Rectangle var4 = ((TextUI)((TextUI)this.uis.elementAt(0))).modelToView(var1, var2, var3);

      for(int var5 = 1; var5 < this.uis.size(); ++var5) {
         ((TextUI)((TextUI)this.uis.elementAt(var5))).modelToView(var1, var2, var3);
      }

      return var4;
   }

   public int viewToModel(JTextComponent var1, Point var2) {
      int var3 = ((TextUI)((TextUI)this.uis.elementAt(0))).viewToModel(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((TextUI)((TextUI)this.uis.elementAt(var4))).viewToModel(var1, var2);
      }

      return var3;
   }

   public int viewToModel(JTextComponent var1, Point var2, Position.Bias[] var3) {
      int var4 = ((TextUI)((TextUI)this.uis.elementAt(0))).viewToModel(var1, var2, var3);

      for(int var5 = 1; var5 < this.uis.size(); ++var5) {
         ((TextUI)((TextUI)this.uis.elementAt(var5))).viewToModel(var1, var2, var3);
      }

      return var4;
   }

   public int getNextVisualPositionFrom(JTextComponent var1, int var2, Position.Bias var3, int var4, Position.Bias[] var5) throws BadLocationException {
      int var6 = ((TextUI)((TextUI)this.uis.elementAt(0))).getNextVisualPositionFrom(var1, var2, var3, var4, var5);

      for(int var7 = 1; var7 < this.uis.size(); ++var7) {
         ((TextUI)((TextUI)this.uis.elementAt(var7))).getNextVisualPositionFrom(var1, var2, var3, var4, var5);
      }

      return var6;
   }

   public void damageRange(JTextComponent var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.uis.size(); ++var4) {
         ((TextUI)((TextUI)this.uis.elementAt(var4))).damageRange(var1, var2, var3);
      }

   }

   public void damageRange(JTextComponent var1, int var2, int var3, Position.Bias var4, Position.Bias var5) {
      for(int var6 = 0; var6 < this.uis.size(); ++var6) {
         ((TextUI)((TextUI)this.uis.elementAt(var6))).damageRange(var1, var2, var3, var4, var5);
      }

   }

   public EditorKit getEditorKit(JTextComponent var1) {
      EditorKit var2 = ((TextUI)((TextUI)this.uis.elementAt(0))).getEditorKit(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((TextUI)((TextUI)this.uis.elementAt(var3))).getEditorKit(var1);
      }

      return var2;
   }

   public View getRootView(JTextComponent var1) {
      View var2 = ((TextUI)((TextUI)this.uis.elementAt(0))).getRootView(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((TextUI)((TextUI)this.uis.elementAt(var3))).getRootView(var1);
      }

      return var2;
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
      MultiTextUI var1 = new MultiTextUI();
      return MultiLookAndFeel.createUIs(var1, ((MultiTextUI)var1).uis, var0);
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
