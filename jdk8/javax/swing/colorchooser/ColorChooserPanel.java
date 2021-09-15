package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

final class ColorChooserPanel extends AbstractColorChooserPanel implements PropertyChangeListener {
   private static final int MASK = -16777216;
   private final ColorModel model;
   private final ColorPanel panel;
   private final DiagramComponent slider;
   private final DiagramComponent diagram;
   private final JFormattedTextField text;
   private final JLabel label;

   ColorChooserPanel(ColorModel var1) {
      this.model = var1;
      this.panel = new ColorPanel(this.model);
      this.slider = new DiagramComponent(this.panel, false);
      this.diagram = new DiagramComponent(this.panel, true);
      this.text = new JFormattedTextField();
      this.label = new JLabel((String)null, (Icon)null, 4);
      ValueFormatter.init(6, true, this.text);
   }

   public void setEnabled(boolean var1) {
      super.setEnabled(var1);
      setEnabled(this, var1);
   }

   private static void setEnabled(Container var0, boolean var1) {
      Component[] var2 = var0.getComponents();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         var5.setEnabled(var1);
         if (var5 instanceof Container) {
            setEnabled((Container)var5, var1);
         }
      }

   }

   public void updateChooser() {
      Color var1 = this.getColorFromModel();
      if (var1 != null) {
         this.panel.setColor(var1);
         this.text.setValue(var1.getRGB());
         this.slider.repaint();
         this.diagram.repaint();
      }

   }

   protected void buildChooser() {
      if (0 == this.getComponentCount()) {
         this.setLayout(new GridBagLayout());
         GridBagConstraints var1 = new GridBagConstraints();
         var1.gridx = 3;
         var1.gridwidth = 2;
         var1.weighty = 1.0D;
         var1.anchor = 11;
         var1.fill = 2;
         var1.insets.top = 10;
         var1.insets.right = 10;
         this.add(this.panel, var1);
         var1.gridwidth = 1;
         var1.weightx = 1.0D;
         var1.weighty = 0.0D;
         var1.anchor = 10;
         var1.insets.right = 5;
         var1.insets.bottom = 10;
         this.add(this.label, var1);
         var1.gridx = 4;
         var1.weightx = 0.0D;
         var1.insets.right = 10;
         this.add(this.text, var1);
         var1.gridx = 2;
         var1.gridheight = 2;
         var1.anchor = 11;
         var1.ipadx = this.text.getPreferredSize().height;
         var1.ipady = this.getPreferredSize().height;
         this.add(this.slider, var1);
         var1.gridx = 1;
         var1.insets.left = 10;
         var1.ipadx = var1.ipady;
         this.add(this.diagram, var1);
         this.label.setLabelFor(this.text);
         this.text.addPropertyChangeListener("value", this);
         this.slider.setBorder(this.text.getBorder());
         this.diagram.setBorder(this.text.getBorder());
         setInheritsPopupMenu(this, true);
      }

      String var4 = this.model.getText(this, "HexCode");
      boolean var2 = var4 != null;
      this.text.setVisible(var2);
      this.text.getAccessibleContext().setAccessibleDescription(var4);
      this.label.setVisible(var2);
      if (var2) {
         this.label.setText(var4);
         int var3 = this.model.getInteger(this, "HexCodeMnemonic");
         if (var3 > 0) {
            this.label.setDisplayedMnemonic(var3);
            var3 = this.model.getInteger(this, "HexCodeMnemonicIndex");
            if (var3 >= 0) {
               this.label.setDisplayedMnemonicIndex(var3);
            }
         }
      }

      this.panel.buildPanel();
   }

   public String getDisplayName() {
      return this.model.getText(this, "Name");
   }

   public int getMnemonic() {
      return this.model.getInteger(this, "Mnemonic");
   }

   public int getDisplayedMnemonicIndex() {
      return this.model.getInteger(this, "DisplayedMnemonicIndex");
   }

   public Icon getSmallDisplayIcon() {
      return null;
   }

   public Icon getLargeDisplayIcon() {
      return null;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      ColorSelectionModel var2 = this.getColorSelectionModel();
      if (var2 != null) {
         Object var3 = var1.getNewValue();
         if (var3 instanceof Integer) {
            int var4 = -16777216 & var2.getSelectedColor().getRGB() | (Integer)var3;
            var2.setSelectedColor(new Color(var4, true));
         }
      }

      this.text.selectAll();
   }

   private static void setInheritsPopupMenu(JComponent var0, boolean var1) {
      var0.setInheritsPopupMenu(var1);
      Component[] var2 = var0.getComponents();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         if (var5 instanceof JComponent) {
            setInheritsPopupMenu((JComponent)var5, var1);
         }
      }

   }
}
