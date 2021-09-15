package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

final class ColorPanel extends JPanel implements ActionListener {
   private final SlidingSpinner[] spinners = new SlidingSpinner[5];
   private final float[] values;
   private final ColorModel model;
   private Color color;
   private int x;
   private int y;
   private int z;

   ColorPanel(ColorModel var1) {
      super(new GridBagLayout());
      this.values = new float[this.spinners.length];
      this.x = 1;
      this.y = 2;
      GridBagConstraints var2 = new GridBagConstraints();
      var2.fill = 2;
      var2.gridx = 1;
      ButtonGroup var3 = new ButtonGroup();
      EmptyBorder var4 = null;

      for(int var5 = 0; var5 < this.spinners.length; ++var5) {
         if (var5 < 3) {
            JRadioButton var6 = new JRadioButton();
            if (var5 == 0) {
               Insets var7 = var6.getInsets();
               var7.left = var6.getPreferredSize().width;
               var4 = new EmptyBorder(var7);
               var6.setSelected(true);
               var2.insets.top = 5;
            }

            this.add(var6, var2);
            var3.add(var6);
            var6.setActionCommand(Integer.toString(var5));
            var6.addActionListener(this);
            this.spinners[var5] = new SlidingSpinner(this, var6);
         } else {
            JLabel var10 = new JLabel();
            this.add(var10, var2);
            var10.setBorder(var4);
            var10.setFocusable(false);
            this.spinners[var5] = new SlidingSpinner(this, var10);
         }
      }

      var2.gridx = 2;
      var2.weightx = 1.0D;
      var2.insets.top = 0;
      var2.insets.left = 5;
      SlidingSpinner[] var9 = this.spinners;
      int var11 = var9.length;

      SlidingSpinner var8;
      int var12;
      for(var12 = 0; var12 < var11; ++var12) {
         var8 = var9[var12];
         this.add(var8.getSlider(), var2);
         var2.insets.top = 5;
      }

      var2.gridx = 3;
      var2.weightx = 0.0D;
      var2.insets.top = 0;
      var9 = this.spinners;
      var11 = var9.length;

      for(var12 = 0; var12 < var11; ++var12) {
         var8 = var9[var12];
         this.add(var8.getSpinner(), var2);
         var2.insets.top = 5;
      }

      this.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
      this.setFocusTraversalPolicyProvider(true);
      this.setFocusable(false);
      this.model = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      try {
         this.z = Integer.parseInt(var1.getActionCommand());
         this.y = this.z != 2 ? 2 : 1;
         this.x = this.z != 0 ? 0 : 1;
         this.getParent().repaint();
      } catch (NumberFormatException var3) {
      }

   }

   void buildPanel() {
      int var1 = this.model.getCount();
      this.spinners[4].setVisible(var1 > 4);

      for(int var2 = 0; var2 < var1; ++var2) {
         String var3 = this.model.getLabel(this, var2);
         JComponent var4 = this.spinners[var2].getLabel();
         if (var4 instanceof JRadioButton) {
            JRadioButton var5 = (JRadioButton)var4;
            var5.setText(var3);
            var5.getAccessibleContext().setAccessibleDescription(var3);
         } else if (var4 instanceof JLabel) {
            JLabel var6 = (JLabel)var4;
            var6.setText(var3);
         }

         this.spinners[var2].setRange(this.model.getMinimum(var2), this.model.getMaximum(var2));
         this.spinners[var2].setValue(this.values[var2]);
         this.spinners[var2].getSlider().getAccessibleContext().setAccessibleName(var3);
         this.spinners[var2].getSpinner().getAccessibleContext().setAccessibleName(var3);
         JSpinner.DefaultEditor var7 = (JSpinner.DefaultEditor)this.spinners[var2].getSpinner().getEditor();
         var7.getTextField().getAccessibleContext().setAccessibleName(var3);
         this.spinners[var2].getSlider().getAccessibleContext().setAccessibleDescription(var3);
         this.spinners[var2].getSpinner().getAccessibleContext().setAccessibleDescription(var3);
         var7.getTextField().getAccessibleContext().setAccessibleDescription(var3);
      }

   }

   void colorChanged() {
      this.color = new Color(this.getColor(0), true);
      Container var1 = this.getParent();
      if (var1 instanceof ColorChooserPanel) {
         ColorChooserPanel var2 = (ColorChooserPanel)var1;
         var2.setSelectedColor(this.color);
         var2.repaint();
      }

   }

   float getValueX() {
      return this.spinners[this.x].getValue();
   }

   float getValueY() {
      return 1.0F - this.spinners[this.y].getValue();
   }

   float getValueZ() {
      return 1.0F - this.spinners[this.z].getValue();
   }

   void setValue(float var1) {
      this.spinners[this.z].setValue(1.0F - var1);
      this.colorChanged();
   }

   void setValue(float var1, float var2) {
      this.spinners[this.x].setValue(var1);
      this.spinners[this.y].setValue(1.0F - var2);
      this.colorChanged();
   }

   int getColor(float var1) {
      this.setDefaultValue(this.x);
      this.setDefaultValue(this.y);
      this.values[this.z] = 1.0F - var1;
      return this.getColor(3);
   }

   int getColor(float var1, float var2) {
      this.values[this.x] = var1;
      this.values[this.y] = 1.0F - var2;
      this.setValue(this.z);
      return this.getColor(3);
   }

   void setColor(Color var1) {
      if (!var1.equals(this.color)) {
         this.color = var1;
         this.model.setColor(var1.getRGB(), this.values);

         for(int var2 = 0; var2 < this.model.getCount(); ++var2) {
            this.spinners[var2].setValue(this.values[var2]);
         }
      }

   }

   private int getColor(int var1) {
      while(var1 < this.model.getCount()) {
         this.setValue(var1++);
      }

      return this.model.getColor(this.values);
   }

   private void setValue(int var1) {
      this.values[var1] = this.spinners[var1].getValue();
   }

   private void setDefaultValue(int var1) {
      float var2 = this.model.getDefault(var1);
      this.values[var1] = var2 < 0.0F ? this.spinners[var1].getValue() : var2;
   }
}
