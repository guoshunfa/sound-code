package apple.laf;

public class JRSUIState {
   final long encodedState;
   long derivedEncodedState;
   static JRSUIState prototype = new JRSUIState(0L);

   public static JRSUIState getInstance() {
      return prototype.derive();
   }

   JRSUIState(JRSUIConstants.Widget var1) {
      this(var1.apply(0L));
   }

   JRSUIState(long var1) {
      this.encodedState = this.derivedEncodedState = var1;
   }

   boolean isDerivationSame() {
      return this.encodedState == this.derivedEncodedState;
   }

   public <T extends JRSUIState> T derive() {
      if (this.isDerivationSame()) {
         return this;
      } else {
         JRSUIState var1 = this.createDerivation();
         return var1;
      }
   }

   public <T extends JRSUIState> T createDerivation() {
      return new JRSUIState(this.derivedEncodedState);
   }

   public void reset() {
      this.derivedEncodedState = this.encodedState;
   }

   public void set(JRSUIConstants.Property var1) {
      this.derivedEncodedState = var1.apply(this.derivedEncodedState);
   }

   public void apply(JRSUIControl var1) {
      var1.setEncodedState(this.encodedState);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JRSUIState)) {
         return false;
      } else {
         return this.encodedState == ((JRSUIState)var1).encodedState && this.getClass().equals(var1.getClass());
      }
   }

   public boolean is(JRSUIConstants.Property var1) {
      return (byte)((int)((this.derivedEncodedState & var1.encoding.mask) >> var1.encoding.shift)) == var1.ordinal;
   }

   public int hashCode() {
      return (int)(this.encodedState ^ this.encodedState >>> 32) ^ this.getClass().hashCode();
   }

   public static class ScrollBarState extends JRSUIState.ValueState {
      final double thumbProportion;
      double derivedThumbProportion;
      final double thumbStart;
      double derivedThumbStart;

      ScrollBarState(long var1, double var3, double var5, double var7) {
         super(var1, var3);
         this.thumbProportion = this.derivedThumbProportion = var5;
         this.thumbStart = this.derivedThumbStart = var7;
      }

      boolean isDerivationSame() {
         return super.isDerivationSame() && this.thumbProportion == this.derivedThumbProportion && this.thumbStart == this.derivedThumbStart;
      }

      public <T extends JRSUIState> T createDerivation() {
         return new JRSUIState.ScrollBarState(this.derivedEncodedState, this.derivedValue, this.derivedThumbProportion, this.derivedThumbStart);
      }

      public void reset() {
         super.reset();
         this.derivedThumbProportion = this.thumbProportion;
         this.derivedThumbStart = this.thumbStart;
      }

      public void setThumbPercent(double var1) {
         this.derivedThumbProportion = var1;
      }

      public void setThumbStart(double var1) {
         this.derivedThumbStart = var1;
      }

      public void apply(JRSUIControl var1) {
         super.apply(var1);
         var1.set(JRSUIConstants.Key.THUMB_PROPORTION, this.thumbProportion);
         var1.set(JRSUIConstants.Key.THUMB_START, this.thumbStart);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof JRSUIState.ScrollBarState)) {
            return false;
         } else {
            return this.thumbProportion == ((JRSUIState.ScrollBarState)var1).thumbProportion && this.thumbStart == ((JRSUIState.ScrollBarState)var1).thumbStart && super.equals(var1);
         }
      }

      public int hashCode() {
         long var1 = Double.doubleToRawLongBits(this.thumbProportion) ^ Double.doubleToRawLongBits(this.thumbStart);
         return super.hashCode() ^ (int)var1 ^ (int)(var1 >>> 32);
      }
   }

   public static class TitleBarHeightState extends JRSUIState.ValueState {
      TitleBarHeightState(long var1, double var3) {
         super(var1, var3);
      }

      public <T extends JRSUIState> T createDerivation() {
         return new JRSUIState.TitleBarHeightState(this.derivedEncodedState, this.derivedValue);
      }

      public void apply(JRSUIControl var1) {
         super.apply(var1);
         var1.set(JRSUIConstants.Key.WINDOW_TITLE_BAR_HEIGHT, this.value);
      }
   }

   public static class ValueState extends JRSUIState {
      final double value;
      double derivedValue;

      ValueState(long var1, double var3) {
         super(var1);
         this.value = this.derivedValue = var3;
      }

      boolean isDerivationSame() {
         return super.isDerivationSame() && this.value == this.derivedValue;
      }

      public <T extends JRSUIState> T createDerivation() {
         return new JRSUIState.ValueState(this.derivedEncodedState, this.derivedValue);
      }

      public void reset() {
         super.reset();
         this.derivedValue = this.value;
      }

      public void setValue(double var1) {
         this.derivedValue = var1;
      }

      public void apply(JRSUIControl var1) {
         super.apply(var1);
         var1.set(JRSUIConstants.Key.VALUE, this.value);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof JRSUIState.ValueState)) {
            return false;
         } else {
            return this.value == ((JRSUIState.ValueState)var1).value && super.equals(var1);
         }
      }

      public int hashCode() {
         long var1 = Double.doubleToRawLongBits(this.value);
         return super.hashCode() ^ (int)var1 ^ (int)(var1 >>> 32);
      }
   }

   public static class AnimationFrameState extends JRSUIState {
      final int animationFrame;
      int derivedAnimationFrame;

      AnimationFrameState(long var1, int var3) {
         super(var1);
         this.animationFrame = this.derivedAnimationFrame = var3;
      }

      boolean isDerivationSame() {
         return super.isDerivationSame() && this.animationFrame == this.derivedAnimationFrame;
      }

      public <T extends JRSUIState> T createDerivation() {
         return new JRSUIState.AnimationFrameState(this.derivedEncodedState, this.derivedAnimationFrame);
      }

      public void reset() {
         super.reset();
         this.derivedAnimationFrame = this.animationFrame;
      }

      public void setAnimationFrame(int var1) {
         this.derivedAnimationFrame = var1;
      }

      public void apply(JRSUIControl var1) {
         super.apply(var1);
         var1.set(JRSUIConstants.Key.ANIMATION_FRAME, (double)this.animationFrame);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof JRSUIState.AnimationFrameState)) {
            return false;
         } else {
            return this.animationFrame == ((JRSUIState.AnimationFrameState)var1).animationFrame && super.equals(var1);
         }
      }

      public int hashCode() {
         return super.hashCode() ^ this.animationFrame;
      }
   }
}
