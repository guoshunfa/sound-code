package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

public class SynthEditorPaneUI extends BasicEditorPaneUI implements SynthUI {
   private SynthStyle style;
   private Boolean localTrue;

   public SynthEditorPaneUI() {
      this.localTrue = Boolean.TRUE;
   }

   public static ComponentUI createUI(JComponent var0) {
      return new SynthEditorPaneUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      JTextComponent var1 = this.getComponent();
      Object var2 = var1.getClientProperty("JEditorPane.honorDisplayProperties");
      if (var2 == null) {
         var1.putClientProperty("JEditorPane.honorDisplayProperties", this.localTrue);
      }

      this.updateStyle(this.getComponent());
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.getComponent(), 1);
      JTextComponent var2 = this.getComponent();
      var2.putClientProperty("caretAspectRatio", (Object)null);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      Object var3 = var2.getClientProperty("JEditorPane.honorDisplayProperties");
      if (var3 == this.localTrue) {
         var2.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.FALSE);
      }

      super.uninstallDefaults();
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JTextComponent)var1.getSource());
      }

      super.propertyChange(var1);
   }

   private void updateStyle(JTextComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         SynthTextFieldUI.updateStyle(var1, var2, this.getPropertyPrefix());
         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

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
      this.paintBackground(var3, var1, var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      super.paint(var2, this.getComponent());
   }

   protected void paintBackground(Graphics var1) {
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      var1.getPainter().paintEditorPaneBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintEditorPaneBorder(var1, var2, var3, var4, var5, var6);
   }
}
