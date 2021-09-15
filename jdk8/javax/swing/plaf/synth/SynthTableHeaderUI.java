package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class SynthTableHeaderUI extends BasicTableHeaderUI implements PropertyChangeListener, SynthUI {
   private TableCellRenderer prevRenderer = null;
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthTableHeaderUI();
   }

   protected void installDefaults() {
      this.prevRenderer = this.header.getDefaultRenderer();
      if (this.prevRenderer instanceof UIResource) {
         this.header.setDefaultRenderer(new SynthTableHeaderUI.HeaderRenderer());
      }

      this.updateStyle(this.header);
   }

   private void updateStyle(JTableHeader var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3 && var3 != null) {
         this.uninstallKeyboardActions();
         this.installKeyboardActions();
      }

      var2.dispose();
   }

   protected void installListeners() {
      super.installListeners();
      this.header.addPropertyChangeListener(this);
   }

   protected void uninstallDefaults() {
      if (this.header.getDefaultRenderer() instanceof SynthTableHeaderUI.HeaderRenderer) {
         this.header.setDefaultRenderer(this.prevRenderer);
      }

      SynthContext var1 = this.getContext(this.header, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   protected void uninstallListeners() {
      this.header.removePropertyChangeListener(this);
      super.uninstallListeners();
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintTableHeaderBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      super.paint(var2, var1.getComponent());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTableHeaderBorder(var1, var2, var3, var4, var5, var6);
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   protected void rolloverColumnUpdated(int var1, int var2) {
      this.header.repaint(this.header.getHeaderRect(var1));
      this.header.repaint(this.header.getHeaderRect(var2));
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JTableHeader)var1.getSource());
      }

   }

   private class HeaderRenderer extends DefaultTableCellHeaderRenderer {
      HeaderRenderer() {
         this.setHorizontalAlignment(10);
         this.setName("TableHeader.renderer");
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         boolean var7 = var6 == SynthTableHeaderUI.this.getRolloverColumn();
         if (!var3 && !var7 && !var4) {
            SynthLookAndFeel.resetSelectedUI();
         } else {
            SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), var3, var4, var1.isEnabled(), var7);
         }

         RowSorter var8 = var1 == null ? null : var1.getRowSorter();
         List var9 = var8 == null ? null : var8.getSortKeys();
         if (var9 != null && var9.size() > 0 && ((RowSorter.SortKey)var9.get(0)).getColumn() == var1.convertColumnIndexToModel(var6)) {
            switch(((RowSorter.SortKey)var9.get(0)).getSortOrder()) {
            case ASCENDING:
               this.putClientProperty("Table.sortOrder", "ASCENDING");
               break;
            case DESCENDING:
               this.putClientProperty("Table.sortOrder", "DESCENDING");
               break;
            case UNSORTED:
               this.putClientProperty("Table.sortOrder", "UNSORTED");
               break;
            default:
               throw new AssertionError("Cannot happen");
            }
         } else {
            this.putClientProperty("Table.sortOrder", "UNSORTED");
         }

         super.getTableCellRendererComponent(var1, var2, var3, var4, var5, var6);
         return this;
      }

      public void setBorder(Border var1) {
         if (var1 instanceof SynthBorder) {
            super.setBorder(var1);
         }

      }
   }
}
