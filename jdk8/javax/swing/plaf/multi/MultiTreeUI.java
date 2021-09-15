package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreePath;

public class MultiTreeUI extends TreeUI {
   protected Vector uis = new Vector();

   public ComponentUI[] getUIs() {
      return MultiLookAndFeel.uisToArray(this.uis);
   }

   public Rectangle getPathBounds(JTree var1, TreePath var2) {
      Rectangle var3 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).getPathBounds(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var4))).getPathBounds(var1, var2);
      }

      return var3;
   }

   public TreePath getPathForRow(JTree var1, int var2) {
      TreePath var3 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).getPathForRow(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var4))).getPathForRow(var1, var2);
      }

      return var3;
   }

   public int getRowForPath(JTree var1, TreePath var2) {
      int var3 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).getRowForPath(var1, var2);

      for(int var4 = 1; var4 < this.uis.size(); ++var4) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var4))).getRowForPath(var1, var2);
      }

      return var3;
   }

   public int getRowCount(JTree var1) {
      int var2 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).getRowCount(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var3))).getRowCount(var1);
      }

      return var2;
   }

   public TreePath getClosestPathForLocation(JTree var1, int var2, int var3) {
      TreePath var4 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).getClosestPathForLocation(var1, var2, var3);

      for(int var5 = 1; var5 < this.uis.size(); ++var5) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var5))).getClosestPathForLocation(var1, var2, var3);
      }

      return var4;
   }

   public boolean isEditing(JTree var1) {
      boolean var2 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).isEditing(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var3))).isEditing(var1);
      }

      return var2;
   }

   public boolean stopEditing(JTree var1) {
      boolean var2 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).stopEditing(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var3))).stopEditing(var1);
      }

      return var2;
   }

   public void cancelEditing(JTree var1) {
      for(int var2 = 0; var2 < this.uis.size(); ++var2) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var2))).cancelEditing(var1);
      }

   }

   public void startEditingAtPath(JTree var1, TreePath var2) {
      for(int var3 = 0; var3 < this.uis.size(); ++var3) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var3))).startEditingAtPath(var1, var2);
      }

   }

   public TreePath getEditingPath(JTree var1) {
      TreePath var2 = ((TreeUI)((TreeUI)this.uis.elementAt(0))).getEditingPath(var1);

      for(int var3 = 1; var3 < this.uis.size(); ++var3) {
         ((TreeUI)((TreeUI)this.uis.elementAt(var3))).getEditingPath(var1);
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
      MultiTreeUI var1 = new MultiTreeUI();
      return MultiLookAndFeel.createUIs(var1, ((MultiTreeUI)var1).uis, var0);
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
