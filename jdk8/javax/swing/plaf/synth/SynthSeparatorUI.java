package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.SeparatorUI;
import javax.swing.plaf.UIResource;

public class SynthSeparatorUI extends SeparatorUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthSeparatorUI();
   }

   public void installUI(JComponent var1) {
      this.installDefaults((JSeparator)var1);
      this.installListeners((JSeparator)var1);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallListeners((JSeparator)var1);
      this.uninstallDefaults((JSeparator)var1);
   }

   public void installDefaults(JSeparator var1) {
      this.updateStyle(var1);
   }

   private void updateStyle(JSeparator var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3 && var1 instanceof JToolBar.Separator) {
         Dimension var4 = ((JToolBar.Separator)var1).getSeparatorSize();
         if (var4 == null || var4 instanceof UIResource) {
            DimensionUIResource var5 = (DimensionUIResource)this.style.get(var2, "ToolBar.separatorSize");
            if (var5 == null) {
               var5 = new DimensionUIResource(10, 10);
            }

            ((JToolBar.Separator)var1).setSeparatorSize(var5);
         }
      }

      var2.dispose();
   }

   public void uninstallDefaults(JSeparator var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
   }

   public void installListeners(JSeparator var1) {
      var1.addPropertyChangeListener(this);
   }

   public void uninstallListeners(JSeparator var1) {
      var1.removePropertyChangeListener(this);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      JSeparator var4 = (JSeparator)var3.getComponent();
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintSeparatorBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight(), var4.getOrientation());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      JSeparator var3 = (JSeparator)var1.getComponent();
      var1.getPainter().paintSeparatorForeground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight(), var3.getOrientation());
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      JSeparator var7 = (JSeparator)var1.getComponent();
      var1.getPainter().paintSeparatorBorder(var1, var2, var3, var4, var5, var6, var7.getOrientation());
   }

   public Dimension getPreferredSize(JComponent var1) {
      SynthContext var2 = this.getContext(var1);
      int var3 = this.style.getInt(var2, "Separator.thickness", 2);
      Insets var4 = var1.getInsets();
      Dimension var5;
      if (((JSeparator)var1).getOrientation() == 1) {
         var5 = new Dimension(var4.left + var4.right + var3, var4.top + var4.bottom);
      } else {
         var5 = new Dimension(var4.left + var4.right, var4.top + var4.bottom + var3);
      }

      var2.dispose();
      return var5;
   }

   public Dimension getMinimumSize(JComponent var1) {
      return this.getPreferredSize(var1);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(32767, 32767);
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JSeparator)var1.getSource());
      }

   }
}
