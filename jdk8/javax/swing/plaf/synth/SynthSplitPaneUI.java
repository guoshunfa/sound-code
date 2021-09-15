package javax.swing.plaf.synth;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SynthSplitPaneUI extends BasicSplitPaneUI implements PropertyChangeListener, SynthUI {
   private static Set<KeyStroke> managingFocusForwardTraversalKeys;
   private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
   private SynthStyle style;
   private SynthStyle dividerStyle;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthSplitPaneUI();
   }

   protected void installDefaults() {
      this.updateStyle(this.splitPane);
      this.setOrientation(this.splitPane.getOrientation());
      this.setContinuousLayout(this.splitPane.isContinuousLayout());
      this.resetLayoutManager();
      if (this.nonContinuousLayoutDivider == null) {
         this.setNonContinuousLayoutDivider(this.createDefaultNonContinuousLayoutDivider(), true);
      } else {
         this.setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider, true);
      }

      if (managingFocusForwardTraversalKeys == null) {
         managingFocusForwardTraversalKeys = new HashSet();
         managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
      }

      this.splitPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
      if (managingFocusBackwardTraversalKeys == null) {
         managingFocusBackwardTraversalKeys = new HashSet();
         managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
      }

      this.splitPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
   }

   private void updateStyle(JSplitPane var1) {
      SynthContext var2 = this.getContext(var1, Region.SPLIT_PANE_DIVIDER, 1);
      SynthStyle var3 = this.dividerStyle;
      this.dividerStyle = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
      var2 = this.getContext(var1, 1);
      SynthStyle var4 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var4) {
         Object var5 = this.style.get(var2, "SplitPane.size");
         if (var5 == null) {
            var5 = 6;
         }

         LookAndFeel.installProperty(var1, "dividerSize", var5);
         var5 = this.style.get(var2, "SplitPane.oneTouchExpandable");
         if (var5 != null) {
            LookAndFeel.installProperty(var1, "oneTouchExpandable", var5);
         }

         if (this.divider != null) {
            var1.remove(this.divider);
            this.divider.setDividerSize(var1.getDividerSize());
         }

         if (var4 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      if (this.style != var4 || this.dividerStyle != var3) {
         if (this.divider != null) {
            var1.remove(this.divider);
         }

         this.divider = this.createDefaultDivider();
         this.divider.setBasicSplitPaneUI(this);
         var1.add(this.divider, "divider");
      }

      var2.dispose();
   }

   protected void installListeners() {
      super.installListeners();
      this.splitPane.addPropertyChangeListener(this);
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.splitPane, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      var1 = this.getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER, 1);
      this.dividerStyle.uninstallDefaults(var1);
      var1.dispose();
      this.dividerStyle = null;
      super.uninstallDefaults();
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.splitPane.removePropertyChangeListener(this);
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   SynthContext getContext(JComponent var1, Region var2) {
      return this.getContext(var1, var2, this.getComponentState(var1, var2));
   }

   private SynthContext getContext(JComponent var1, Region var2, int var3) {
      return var2 == Region.SPLIT_PANE_DIVIDER ? SynthContext.getContext(var1, var2, this.dividerStyle, var3) : SynthContext.getContext(var1, var2, this.style, var3);
   }

   private int getComponentState(JComponent var1, Region var2) {
      int var3 = SynthLookAndFeel.getComponentState(var1);
      if (this.divider.isMouseOver()) {
         var3 |= 2;
      }

      return var3;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JSplitPane)var1.getSource());
      }

   }

   public BasicSplitPaneDivider createDefaultDivider() {
      SynthSplitPaneDivider var1 = new SynthSplitPaneDivider(this);
      var1.setDividerSize(this.splitPane.getDividerSize());
      return var1;
   }

   protected Component createDefaultNonContinuousLayoutDivider() {
      return new Canvas() {
         public void paint(Graphics var1) {
            SynthSplitPaneUI.this.paintDragDivider(var1, 0, 0, this.getWidth(), this.getHeight());
         }
      };
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintSplitPaneBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      super.paint(var2, this.splitPane);
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintSplitPaneBorder(var1, var2, var3, var4, var5, var6);
   }

   private void paintDragDivider(Graphics var1, int var2, int var3, int var4, int var5) {
      SynthContext var6 = this.getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER);
      var6.setComponentState((var6.getComponentState() | 2) ^ 2 | 4);
      Shape var7 = var1.getClip();
      var1.clipRect(var2, var3, var4, var5);
      var6.getPainter().paintSplitPaneDragDivider(var6, var1, var2, var3, var4, var5, this.splitPane.getOrientation());
      var1.setClip(var7);
      var6.dispose();
   }

   public void finishedPaintingChildren(JSplitPane var1, Graphics var2) {
      if (var1 == this.splitPane && this.getLastDragLocation() != -1 && !this.isContinuousLayout() && !this.draggingHW) {
         if (var1.getOrientation() == 1) {
            this.paintDragDivider(var2, this.getLastDragLocation(), 0, this.dividerSize - 1, this.splitPane.getHeight() - 1);
         } else {
            this.paintDragDivider(var2, 0, this.getLastDragLocation(), this.splitPane.getWidth() - 1, this.dividerSize - 1);
         }
      }

   }
}
