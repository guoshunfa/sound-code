package javax.sound.midi;

public class MidiEvent {
   private final MidiMessage message;
   private long tick;

   public MidiEvent(MidiMessage var1, long var2) {
      this.message = var1;
      this.tick = var2;
   }

   public MidiMessage getMessage() {
      return this.message;
   }

   public void setTick(long var1) {
      this.tick = var1;
   }

   public long getTick() {
      return this.tick;
   }
}
