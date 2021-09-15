package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

public class SynthColorChooserUI extends BasicColorChooserUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthColorChooserUI();
   }

   protected AbstractColorChooserPanel[] createDefaultChoosers() {
      SynthContext var1 = this.getContext(this.chooser, 1);
      AbstractColorChooserPanel[] var2 = (AbstractColorChooserPanel[])((AbstractColorChooserPanel[])var1.getStyle().get(var1, "ColorChooser.panels"));
      var1.dispose();
      if (var2 == null) {
         var2 = ColorChooserComponentFactory.getDefaultChooserPanels();
      }

      return var2;
   }

   protected void installDefaults() {
      super.installDefaults();
      this.updateStyle(this.chooser);
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.chooser, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      super.uninstallDefaults();
   }

   protected void installListeners() {
      super.installListeners();
      this.chooser.addPropertyChangeListener(this);
   }

   protected void uninstallListeners() {
      this.chooser.removePropertyChangeListener(this);
      super.uninstallListeners();
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
      var3.getPainter().paintColorChooserBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
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
      var1.getPainter().paintColorChooserBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JColorChooser)var1.getSource());
      }

   }
}
