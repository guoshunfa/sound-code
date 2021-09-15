package sun.reflect;

interface ByteVector {
   int getLength();

   byte get(int var1);

   void put(int var1, byte var2);

   void add(byte var1);

   void trim();

   byte[] getData();
}
