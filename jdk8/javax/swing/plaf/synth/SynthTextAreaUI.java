package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

public class SynthTextAreaUI extends BasicTextAreaUI implements SynthUI {
   private SynthTextAreaUI.Handler handler = new SynthTextAreaUI.Handler();
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthTextAreaUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      this.updateStyle(this.getComponent());
      this.getComponent().addFocusListener(this.handler);
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.getComponent(), 1);
      this.getComponent().putClientProperty("caretAspectRatio", (Object)null);
      this.getComponent().removeFocusListener(this.handler);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      super.uninstallDefaults();
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
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintTextAreaBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      super.paint(var2, this.getComponent());
   }

   protected void paintBackground(Graphics var1) {
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTextAreaBorder(var1, var2, var3, var4, var5, var6);
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JTextComponent)var1.getSource());
      }

      super.propertyChange(var1);
   }

   private final class Handler implements FocusListener {
      private Handler() {
      }

      public void focusGained(FocusEvent var1) {
         SynthTextAreaUI.this.getComponent().repaint();
      }

      public void focusLost(FocusEvent var1) {
         SynthTextAreaUI.this.getComponent().repaint();
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
