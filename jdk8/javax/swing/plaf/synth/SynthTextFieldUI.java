package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

public class SynthTextFieldUI extends BasicTextFieldUI implements SynthUI {
   private SynthTextFieldUI.Handler handler = new SynthTextFieldUI.Handler();
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthTextFieldUI();
   }

   private void updateStyle(JTextComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         updateStyle(var1, var2, this.getPropertyPrefix());
         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
   }

   static void updateStyle(JTextComponent var0, SynthContext var1, String var2) {
      SynthStyle var3 = var1.getStyle();
      Color var4 = var0.getCaretColor();
      if (var4 == null || var4 instanceof UIResource) {
         var0.setCaretColor((Color)var3.get(var1, var2 + ".caretForeground"));
      }

      Color var5 = var0.getForeground();
      if (var5 == null || var5 instanceof UIResource) {
         var5 = var3.getColorForState(var1, ColorType.TEXT_FOREGROUND);
         if (var5 != null) {
            var0.setForeground(var5);
         }
      }

      Object var6 = var3.get(var1, var2 + ".caretAspectRatio");
      if (var6 instanceof Number) {
         var0.putClientProperty("caretAspectRatio", var6);
      }

      var1.setComponentState(768);
      Color var7 = var0.getSelectionColor();
      if (var7 == null || var7 instanceof UIResource) {
         var0.setSelectionColor(var3.getColor(var1, ColorType.TEXT_BACKGROUND));
      }

      Color var8 = var0.getSelectedTextColor();
      if (var8 == null || var8 instanceof UIResource) {
         var0.setSelectedTextColor(var3.getColor(var1, ColorType.TEXT_FOREGROUND));
      }

      var1.setComponentState(8);
      Color var9 = var0.getDisabledTextColor();
      if (var9 == null || var9 instanceof UIResource) {
         var0.setDisabledTextColor(var3.getColor(var1, ColorType.TEXT_FOREGROUND));
      }

      Insets var10 = var0.getMargin();
      if (var10 == null || var10 instanceof UIResource) {
         var10 = (Insets)var3.get(var1, var2 + ".margin");
         if (var10 == null) {
            var10 = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
         }

         var0.setMargin(var10);
      }

      Caret var11 = var0.getCaret();
      if (var11 instanceof UIResource) {
         Object var12 = var3.get(var1, var2 + ".caretBlinkRate");
         if (var12 != null && var12 instanceof Integer) {
            Integer var13 = (Integer)var12;
            var11.setBlinkRate(var13);
         }
      }

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
      this.paintBackground(var3, var1, var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      super.paint(var2, this.getComponent());
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      var1.getPainter().paintTextFieldBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintTextFieldBorder(var1, var2, var3, var4, var5, var6);
   }

   protected void paintBackground(Graphics var1) {
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JTextComponent)var1.getSource());
      }

      super.propertyChange(var1);
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

   private final class Handler implements FocusListener {
      private Handler() {
      }

      public void focusGained(FocusEvent var1) {
         SynthTextFieldUI.this.getComponent().repaint();
      }

      public void focusLost(FocusEvent var1) {
         SynthTextFieldUI.this.getComponent().repaint();
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
