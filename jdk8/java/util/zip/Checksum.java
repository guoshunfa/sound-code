package java.util.zip;

public interface Checksum {
   void update(int var1);

   void update(byte[] var1, int var2, int var3);

   long getValue();

   void reset();
}
