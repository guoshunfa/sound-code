package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

public class SynthPanelUI extends BasicPanelUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthPanelUI();
   }

   public void installUI(JComponent var1) {
      JPanel var2 = (JPanel)var1;
      super.installUI(var1);
      this.installListeners(var2);
   }

   public void uninstallUI(JComponent var1) {
      JPanel var2 = (JPanel)var1;
      this.uninstallListeners(var2);
      super.uninstallUI(var1);
   }

   protected void installListeners(JPanel var1) {
      var1.addPropertyChangeListener(this);
   }

   protected void uninstallListeners(JPanel var1) {
      var1.removePropertyChangeListener(this);
   }

   protected void installDefaults(JPanel var1) {
      this.updateStyle(var1);
   }

   protected void uninstallDefaults(JPanel var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
   }

   private void updateStyle(JPanel var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
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

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintPanelBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
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
      var1.getPainter().paintPanelBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JPanel)var1.getSource());
      }

   }
}
