package apple.laf;

public class JRSUIStateFactory {
   public static JRSUIState getSliderTrack() {
      return new JRSUIState(JRSUIConstants.Widget.SLIDER.apply(JRSUIConstants.NoIndicator.YES.apply(0L)));
   }

   public static JRSUIState getSliderThumb() {
      return new JRSUIState(JRSUIConstants.Widget.SLIDER_THUMB.apply(0L));
   }

   public static JRSUIState getSpinnerArrows() {
      return new JRSUIState(JRSUIConstants.Widget.BUTTON_LITTLE_ARROWS.apply(0L));
   }

   public static JRSUIState getSplitPaneDivider() {
      return new JRSUIState(JRSUIConstants.Widget.DIVIDER_SPLITTER.apply(0L));
   }

   public static JRSUIState getTab() {
      return new JRSUIState(JRSUIConstants.Widget.TAB.apply(JRSUIConstants.SegmentTrailingSeparator.YES.apply(0L)));
   }

   public static JRSUIState.AnimationFrameState getDisclosureTriangle() {
      return new JRSUIState.AnimationFrameState(JRSUIConstants.Widget.DISCLOSURE_TRIANGLE.apply(0L), 0);
   }

   public static JRSUIState.ScrollBarState getScrollBar() {
      return new JRSUIState.ScrollBarState(JRSUIConstants.Widget.SCROLL_BAR.apply(0L), 0.0D, 0.0D, 0.0D);
   }

   public static JRSUIState.TitleBarHeightState getTitleBar() {
      return new JRSUIState.TitleBarHeightState(JRSUIConstants.Widget.WINDOW_FRAME.apply(0L), 0.0D);
   }

   public static JRSUIState.ValueState getProgressBar() {
      return new JRSUIState.ValueState(0L, 0.0D);
   }

   public static JRSUIState.ValueState getLabeledButton() {
      return new JRSUIState.ValueState(0L, 0.0D);
   }
}
