package javax.sound.sampled;

import java.io.IOException;

public interface Clip extends DataLine {
   int LOOP_CONTINUOUSLY = -1;

   void open(AudioFormat var1, byte[] var2, int var3, int var4) throws LineUnavailableException;

   void open(AudioInputStream var1) throws LineUnavailableException, IOException;

   int getFrameLength();

   long getMicrosecondLength();

   void setFramePosition(int var1);

   void setMicrosecondPosition(long var1);

   void setLoopPoints(int var1, int var2);

   void loop(int var1);
}
