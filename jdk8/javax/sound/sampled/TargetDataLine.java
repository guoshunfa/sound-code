package javax.sound.sampled;

public interface TargetDataLine extends DataLine {
   void open(AudioFormat var1, int var2) throws LineUnavailableException;

   void open(AudioFormat var1) throws LineUnavailableException;

   int read(byte[] var1, int var2, int var3);
}
