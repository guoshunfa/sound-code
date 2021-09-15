package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class SynthRootPaneUI extends BasicRootPaneUI implements SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthRootPaneUI();
   }

   protected void installDefaults(JRootPane var1) {
      this.updateStyle(var1);
   }

   protected void uninstallDefaults(JRootPane var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
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

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3 && var3 != null) {
         this.uninstallKeyboardActions((JRootPane)var1);
         this.installKeyboardActions((JRootPane)var1);
      }

      var2.dispose();
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintRootPaneBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintRootPaneBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JRootPane)var1.getSource());
      }

      super.propertyChange(var1);
   }
}
