package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class SynthMenuBarUI extends BasicMenuBarUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthMenuBarUI();
   }

   protected void installDefaults() {
      if (this.menuBar.getLayout() == null || this.menuBar.getLayout() instanceof UIResource) {
         this.menuBar.setLayout(new SynthMenuLayout(this.menuBar, 2));
      }

      this.updateStyle(this.menuBar);
   }

   protected void installListeners() {
      super.installListeners();
      this.menuBar.addPropertyChangeListener(this);
   }

   private void updateStyle(JMenuBar var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3 && var3 != null) {
         this.uninstallKeyboardActions();
         this.installKeyboardActions();
      }

      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.menuBar, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.menuBar.removePropertyChangeListener(this);
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
      var3.getPainter().paintMenuBarBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
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
      var1.getPainter().paintMenuBarBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JMenuBar)var1.getSource());
      }

   }
}
