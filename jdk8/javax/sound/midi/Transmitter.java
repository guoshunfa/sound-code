package javax.sound.midi;

public interface Transmitter extends AutoCloseable {
   void setReceiver(Receiver var1);

   Receiver getReceiver();

   void close();
}
