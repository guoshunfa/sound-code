package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;

public class SynthViewportUI extends ViewportUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthViewportUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.installDefaults(var1);
      this.installListeners(var1);
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      this.uninstallListeners(var1);
      this.uninstallDefaults(var1);
   }

   protected void installDefaults(JComponent var1) {
      this.updateStyle(var1);
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = SynthLookAndFeel.getStyle(var2.getComponent(), var2.getRegion());
      SynthStyle var4 = var2.getStyle();
      if (var3 != var4) {
         if (var4 != null) {
            var4.uninstallDefaults(var2);
         }

         var2.setStyle(var3);
         var3.installDefaults(var2);
      }

      this.style = var3;
      var2.dispose();
   }

   protected void installListeners(JComponent var1) {
      var1.addPropertyChangeListener(this);
   }

   protected void uninstallListeners(JComponent var1) {
      var1.removePropertyChangeListener(this);
   }

   protected void uninstallDefaults(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private Region getRegion(JComponent var1) {
      return SynthLookAndFeel.getRegion(var1);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintViewportBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JComponent)var1.getSource());
      }

   }
}
