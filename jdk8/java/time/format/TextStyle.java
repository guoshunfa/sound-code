package java.time.format;

public enum TextStyle {
   FULL(2, 0),
   FULL_STANDALONE(32770, 0),
   SHORT(1, 1),
   SHORT_STANDALONE(32769, 1),
   NARROW(4, 1),
   NARROW_STANDALONE(32772, 1);

   private final int calendarStyle;
   private final int zoneNameStyleIndex;

   private TextStyle(int var3, int var4) {
      this.calendarStyle = var3;
      this.zoneNameStyleIndex = var4;
   }

   public boolean isStandalone() {
      return (this.ordinal() & 1) == 1;
   }

   public TextStyle asStandalone() {
      return values()[this.ordinal() | 1];
   }

   public TextStyle asNormal() {
      return values()[this.ordinal() & -2];
   }

   int toCalendarStyle() {
      return this.calendarStyle;
   }

   int zoneNameStyleIndex() {
      return this.zoneNameStyleIndex;
   }
}
