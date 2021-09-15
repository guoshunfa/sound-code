package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;

public class SynthListUI extends BasicListUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private boolean useListColors;
   private boolean useUIBorder;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthListUI();
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintListBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      var3.dispose();
      this.paint(var1, var2);
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintListBorder(var1, var2, var3, var4, var5, var6);
   }

   protected void installListeners() {
      super.installListeners();
      this.list.addPropertyChangeListener(this);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JList)var1.getSource());
      }

   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.list.removePropertyChangeListener(this);
   }

   protected void installDefaults() {
      if (this.list.getCellRenderer() == null || this.list.getCellRenderer() instanceof UIResource) {
         this.list.setCellRenderer(new SynthListUI.SynthListCellRenderer());
      }

      this.updateStyle(this.list);
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(this.list, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         var2.setComponentState(512);
         Color var4 = this.list.getSelectionBackground();
         if (var4 == null || var4 instanceof UIResource) {
            this.list.setSelectionBackground(this.style.getColor(var2, ColorType.TEXT_BACKGROUND));
         }

         Color var5 = this.list.getSelectionForeground();
         if (var5 == null || var5 instanceof UIResource) {
            this.list.setSelectionForeground(this.style.getColor(var2, ColorType.TEXT_FOREGROUND));
         }

         this.useListColors = this.style.getBoolean(var2, "List.rendererUseListColors", true);
         this.useUIBorder = this.style.getBoolean(var2, "List.rendererUseUIBorder", true);
         int var6 = this.style.getInt(var2, "List.cellHeight", -1);
         if (var6 != -1) {
            this.list.setFixedCellHeight(var6);
         }

         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
   }

   protected void uninstallDefaults() {
      super.uninstallDefaults();
      SynthContext var1 = this.getContext(this.list, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      return SynthLookAndFeel.getComponentState(var1);
   }

   private class SynthListCellRenderer extends DefaultListCellRenderer.UIResource {
      private SynthListCellRenderer() {
      }

      public String getName() {
         return "List.cellRenderer";
      }

      public void setBorder(Border var1) {
         if (SynthListUI.this.useUIBorder || var1 instanceof SynthBorder) {
            super.setBorder(var1);
         }

      }

      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         if (SynthListUI.this.useListColors || !var4 && !var5) {
            SynthLookAndFeel.resetSelectedUI();
         } else {
            SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), var4, var5, var1.isEnabled(), false);
         }

         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         return this;
      }

      public void paint(Graphics var1) {
         super.paint(var1);
         SynthLookAndFeel.resetSelectedUI();
      }

      // $FF: synthetic method
      SynthListCellRenderer(Object var2) {
         this();
      }
   }
}
