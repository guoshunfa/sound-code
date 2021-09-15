package javax.imageio.stream;

import java.io.DataOutput;
import java.io.IOException;

public interface ImageOutputStream extends ImageInputStream, DataOutput {
   void write(int var1) throws IOException;

   void write(byte[] var1) throws IOException;

   void write(byte[] var1, int var2, int var3) throws IOException;

   void writeBoolean(boolean var1) throws IOException;

   void writeByte(int var1) throws IOException;

   void writeShort(int var1) throws IOException;

   void writeChar(int var1) throws IOException;

   void writeInt(int var1) throws IOException;

   void writeLong(long var1) throws IOException;

   void writeFloat(float var1) throws IOException;

   void writeDouble(double var1) throws IOException;

   void writeBytes(String var1) throws IOException;

   void writeChars(String var1) throws IOException;

   void writeUTF(String var1) throws IOException;

   void writeShorts(short[] var1, int var2, int var3) throws IOException;

   void writeChars(char[] var1, int var2, int var3) throws IOException;

   void writeInts(int[] var1, int var2, int var3) throws IOException;

   void writeLongs(long[] var1, int var2, int var3) throws IOException;

   void writeFloats(float[] var1, int var2, int var3) throws IOException;

   void writeDoubles(double[] var1, int var2, int var3) throws IOException;

   void writeBit(int var1) throws IOException;

   void writeBits(long var1, int var3) throws IOException;

   void flushBefore(long var1) throws IOException;
}
