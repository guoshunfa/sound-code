package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MetalScrollPaneUI extends BasicScrollPaneUI {
   private PropertyChangeListener scrollBarSwapListener;

   public static ComponentUI createUI(JComponent var0) {
      return new MetalScrollPaneUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      JScrollPane var2 = (JScrollPane)var1;
      this.updateScrollbarsFreeStanding();
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      JScrollPane var2 = (JScrollPane)var1;
      JScrollBar var3 = var2.getHorizontalScrollBar();
      JScrollBar var4 = var2.getVerticalScrollBar();
      if (var3 != null) {
         var3.putClientProperty("JScrollBar.isFreeStanding", (Object)null);
      }

      if (var4 != null) {
         var4.putClientProperty("JScrollBar.isFreeStanding", (Object)null);
      }

   }

   public void installListeners(JScrollPane var1) {
      super.installListeners(var1);
      this.scrollBarSwapListener = this.createScrollBarSwapListener();
      var1.addPropertyChangeListener(this.scrollBarSwapListener);
   }

   protected void uninstallListeners(JComponent var1) {
      super.uninstallListeners(var1);
      var1.removePropertyChangeListener(this.scrollBarSwapListener);
   }

   /** @deprecated */
   @Deprecated
   public void uninstallListeners(JScrollPane var1) {
      super.uninstallListeners(var1);
      var1.removePropertyChangeListener(this.scrollBarSwapListener);
   }

   private void updateScrollbarsFreeStanding() {
      if (this.scrollpane != null) {
         Border var1 = this.scrollpane.getBorder();
         Boolean var2;
         if (var1 instanceof MetalBorders.ScrollPaneBorder) {
            var2 = Boolean.FALSE;
         } else {
            var2 = Boolean.TRUE;
         }

         JScrollBar var3 = this.scrollpane.getHorizontalScrollBar();
         if (var3 != null) {
            var3.putClientProperty("JScrollBar.isFreeStanding", var2);
         }

         var3 = this.scrollpane.getVerticalScrollBar();
         if (var3 != null) {
            var3.putClientProperty("JScrollBar.isFreeStanding", var2);
         }

      }
   }

   protected PropertyChangeListener createScrollBarSwapListener() {
      return new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            String var2 = var1.getPropertyName();
            if (!var2.equals("verticalScrollBar") && !var2.equals("horizontalScrollBar")) {
               if ("border".equals(var2)) {
                  MetalScrollPaneUI.this.updateScrollbarsFreeStanding();
               }
            } else {
               JScrollBar var3 = (JScrollBar)var1.getOldValue();
               if (var3 != null) {
                  var3.putClientProperty("JScrollBar.isFreeStanding", (Object)null);
               }

               JScrollBar var4 = (JScrollBar)var1.getNewValue();
               if (var4 != null) {
                  var4.putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE);
               }
            }

         }
      };
   }
}
