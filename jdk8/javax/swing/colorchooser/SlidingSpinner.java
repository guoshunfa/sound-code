package javax.swing.colorchooser;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

final class SlidingSpinner implements ChangeListener {
   private final ColorPanel panel;
   private final JComponent label;
   private final SpinnerNumberModel model = new SpinnerNumberModel();
   private final JSlider slider = new JSlider();
   private final JSpinner spinner;
   private float value;
   private boolean internal;

   SlidingSpinner(ColorPanel var1, JComponent var2) {
      this.spinner = new JSpinner(this.model);
      this.panel = var1;
      this.label = var2;
      this.slider.addChangeListener(this);
      this.spinner.addChangeListener(this);
      JSpinner.DefaultEditor var3 = (JSpinner.DefaultEditor)this.spinner.getEditor();
      ValueFormatter.init(3, false, var3.getTextField());
      var3.setFocusable(false);
      this.spinner.setFocusable(false);
   }

   JComponent getLabel() {
      return this.label;
   }

   JSlider getSlider() {
      return this.slider;
   }

   JSpinner getSpinner() {
      return this.spinner;
   }

   float getValue() {
      return this.value;
   }

   void setValue(float var1) {
      int var2 = this.slider.getMinimum();
      int var3 = this.slider.getMaximum();
      this.internal = true;
      this.slider.setValue(var2 + (int)(var1 * (float)(var3 - var2)));
      this.spinner.setValue(this.slider.getValue());
      this.internal = false;
      this.value = var1;
   }

   void setRange(int var1, int var2) {
      this.internal = true;
      this.slider.setMinimum(var1);
      this.slider.setMaximum(var2);
      this.model.setMinimum(var1);
      this.model.setMaximum(var2);
      this.internal = false;
   }

   void setVisible(boolean var1) {
      this.label.setVisible(var1);
      this.slider.setVisible(var1);
      this.spinner.setVisible(var1);
   }

   public void stateChanged(ChangeEvent var1) {
      if (!this.internal) {
         if (this.spinner == var1.getSource()) {
            Object var2 = this.spinner.getValue();
            if (var2 instanceof Integer) {
               this.internal = true;
               this.slider.setValue((Integer)var2);
               this.internal = false;
            }
         }

         int var5 = this.slider.getValue();
         this.internal = true;
         this.spinner.setValue(var5);
         this.internal = false;
         int var3 = this.slider.getMinimum();
         int var4 = this.slider.getMaximum();
         this.value = (float)(var5 - var3) / (float)(var4 - var3);
         this.panel.colorChanged();
      }

   }
}
