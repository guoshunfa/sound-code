package javax.sound.sampled;

public interface SourceDataLine extends DataLine {
   void open(AudioFormat var1, int var2) throws LineUnavailableException;

   void open(AudioFormat var1) throws LineUnavailableException;

   int write(byte[] var1, int var2, int var3);
}
