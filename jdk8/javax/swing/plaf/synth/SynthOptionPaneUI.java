package javax.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import sun.swing.DefaultLookup;

public class SynthOptionPaneUI extends BasicOptionPaneUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthOptionPaneUI();
   }

   protected void installDefaults() {
      this.updateStyle(this.optionPane);
   }

   protected void installListeners() {
      super.installListeners();
      this.optionPane.addPropertyChangeListener(this);
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         this.minimumSize = (Dimension)this.style.get(var2, "OptionPane.minimumSize");
         if (this.minimumSize == null) {
            this.minimumSize = new Dimension(262, 90);
         }

         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.optionPane, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.optionPane.removePropertyChangeListener(this);
   }

   protected void installComponents() {
      this.optionPane.add(this.createMessageArea());
      Container var1 = this.createSeparator();
      if (var1 != null) {
         this.optionPane.add(var1);
         SynthContext var2 = this.getContext(this.optionPane, 1);
         this.optionPane.add(Box.createVerticalStrut(var2.getStyle().getInt(var2, "OptionPane.separatorPadding", 6)));
         var2.dispose();
      }

      this.optionPane.add(this.createButtonArea());
      this.optionPane.applyComponentOrientation(this.optionPane.getComponentOrientation());
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
      var3.getPainter().paintOptionPaneBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
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
      var1.getPainter().paintOptionPaneBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JOptionPane)var1.getSource());
      }

   }

   protected boolean getSizeButtonsToSameWidth() {
      return DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.sameSizeButtons", true);
   }

   protected Container createMessageArea() {
      JPanel var1 = new JPanel();
      var1.setName("OptionPane.messageArea");
      var1.setLayout(new BorderLayout());
      JPanel var2 = new JPanel(new GridBagLayout());
      JPanel var3 = new JPanel(new BorderLayout());
      var2.setName("OptionPane.body");
      var3.setName("OptionPane.realBody");
      if (this.getIcon() != null) {
         JPanel var4 = new JPanel();
         var4.setName("OptionPane.separator");
         var4.setPreferredSize(new Dimension(15, 1));
         var3.add((Component)var4, (Object)"Before");
      }

      var3.add((Component)var2, (Object)"Center");
      GridBagConstraints var6 = new GridBagConstraints();
      var6.gridx = var6.gridy = 0;
      var6.gridwidth = 0;
      var6.gridheight = 1;
      SynthContext var5 = this.getContext(this.optionPane, 1);
      var6.anchor = var5.getStyle().getInt(var5, "OptionPane.messageAnchor", 10);
      var5.dispose();
      var6.insets = new Insets(0, 0, 3, 0);
      this.addMessageComponents(var2, var6, this.getMessage(), this.getMaxCharactersPerLineCount(), false);
      var1.add(var3, "Center");
      this.addIcon(var1);
      return var1;
   }

   protected Container createSeparator() {
      JSeparator var1 = new JSeparator(0);
      var1.setName("OptionPane.separator");
      return var1;
   }
}
