package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.text.JTextComponent;

public class SynthScrollPaneUI extends BasicScrollPaneUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private boolean viewportViewHasFocus = false;
   private SynthScrollPaneUI.ViewportViewFocusHandler viewportViewFocusHandler;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthScrollPaneUI();
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintScrollPaneBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      Border var3 = this.scrollpane.getViewportBorder();
      if (var3 != null) {
         Rectangle var4 = this.scrollpane.getViewportBorderBounds();
         var3.paintBorder(this.scrollpane, var2, var4.x, var4.y, var4.width, var4.height);
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintScrollPaneBorder(var1, var2, var3, var4, var5, var6);
   }

   protected void installDefaults(JScrollPane var1) {
      this.updateStyle(var1);
   }

   private void updateStyle(JScrollPane var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         Border var4 = this.scrollpane.getViewportBorder();
         if (var4 == null || var4 instanceof UIResource) {
            this.scrollpane.setViewportBorder(new SynthScrollPaneUI.ViewportBorder(var2));
         }

         if (var3 != null) {
            this.uninstallKeyboardActions(var1);
            this.installKeyboardActions(var1);
         }
      }

      var2.dispose();
   }

   protected void installListeners(JScrollPane var1) {
      super.installListeners(var1);
      var1.addPropertyChangeListener(this);
      if (UIManager.getBoolean("ScrollPane.useChildTextComponentFocus")) {
         this.viewportViewFocusHandler = new SynthScrollPaneUI.ViewportViewFocusHandler();
         var1.getViewport().addContainerListener(this.viewportViewFocusHandler);
         Component var2 = var1.getViewport().getView();
         if (var2 instanceof JTextComponent) {
            var2.addFocusListener(this.viewportViewFocusHandler);
         }
      }

   }

   protected void uninstallDefaults(JScrollPane var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      if (this.scrollpane.getViewportBorder() instanceof UIResource) {
         this.scrollpane.setViewportBorder((Border)null);
      }

   }

   protected void uninstallListeners(JComponent var1) {
      super.uninstallListeners(var1);
      var1.removePropertyChangeListener(this);
      if (this.viewportViewFocusHandler != null) {
         JViewport var2 = ((JScrollPane)var1).getViewport();
         var2.removeContainerListener(this.viewportViewFocusHandler);
         if (var2.getView() != null) {
            var2.getView().removeFocusListener(this.viewportViewFocusHandler);
         }

         this.viewportViewFocusHandler = null;
      }

   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      int var2 = SynthLookAndFeel.getComponentState(var1);
      if (this.viewportViewFocusHandler != null && this.viewportViewHasFocus) {
         var2 |= 256;
      }

      return var2;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle(this.scrollpane);
      }

   }

   private class ViewportViewFocusHandler implements ContainerListener, FocusListener {
      private ViewportViewFocusHandler() {
      }

      public void componentAdded(ContainerEvent var1) {
         if (var1.getChild() instanceof JTextComponent) {
            var1.getChild().addFocusListener(this);
            SynthScrollPaneUI.this.viewportViewHasFocus = var1.getChild().isFocusOwner();
            SynthScrollPaneUI.this.scrollpane.repaint();
         }

      }

      public void componentRemoved(ContainerEvent var1) {
         if (var1.getChild() instanceof JTextComponent) {
            var1.getChild().removeFocusListener(this);
         }

      }

      public void focusGained(FocusEvent var1) {
         SynthScrollPaneUI.this.viewportViewHasFocus = true;
         SynthScrollPaneUI.this.scrollpane.repaint();
      }

      public void focusLost(FocusEvent var1) {
         SynthScrollPaneUI.this.viewportViewHasFocus = false;
         SynthScrollPaneUI.this.scrollpane.repaint();
      }

      // $FF: synthetic method
      ViewportViewFocusHandler(Object var2) {
         this();
      }
   }

   private class ViewportBorder extends AbstractBorder implements UIResource {
      private Insets insets;

      ViewportBorder(SynthContext var2) {
         this.insets = (Insets)var2.getStyle().get(var2, "ScrollPane.viewportBorderInsets");
         if (this.insets == null) {
            this.insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
         }

      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         JComponent var7 = (JComponent)var1;
         SynthContext var8 = SynthScrollPaneUI.this.getContext(var7);
         SynthStyle var9 = var8.getStyle();
         if (var9 == null) {
            assert false : "SynthBorder is being used outside after the  UI has been uninstalled";

         } else {
            var8.getPainter().paintViewportBorder(var8, var2, var3, var4, var5, var6);
            var8.dispose();
         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         if (var2 == null) {
            return new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
         } else {
            var2.top = this.insets.top;
            var2.bottom = this.insets.bottom;
            var2.left = this.insets.left;
            var2.right = this.insets.left;
            return var2;
         }
      }

      public boolean isBorderOpaque() {
         return false;
      }
   }
}
