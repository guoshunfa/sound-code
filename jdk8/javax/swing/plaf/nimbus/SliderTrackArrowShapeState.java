package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class SliderTrackArrowShapeState extends State {
   SliderTrackArrowShapeState() {
      super("ArrowShape");
   }

   protected boolean isInState(JComponent var1) {
      return var1.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE;
   }
}
