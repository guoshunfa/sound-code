package javax.sound.midi;

public interface Receiver extends AutoCloseable {
   void send(MidiMessage var1, long var2);

   void close();
}
