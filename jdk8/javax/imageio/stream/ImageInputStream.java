package javax.imageio.stream;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteOrder;

public interface ImageInputStream extends DataInput, Closeable {
   void setByteOrder(ByteOrder var1);

   ByteOrder getByteOrder();

   int read() throws IOException;

   int read(byte[] var1) throws IOException;

   int read(byte[] var1, int var2, int var3) throws IOException;

   void readBytes(IIOByteBuffer var1, int var2) throws IOException;

   boolean readBoolean() throws IOException;

   byte readByte() throws IOException;

   int readUnsignedByte() throws IOException;

   short readShort() throws IOException;

   int readUnsignedShort() throws IOException;

   char readChar() throws IOException;

   int readInt() throws IOException;

   long readUnsignedInt() throws IOException;

   long readLong() throws IOException;

   float readFloat() throws IOException;

   double readDouble() throws IOException;

   String readLine() throws IOException;

   String readUTF() throws IOException;

   void readFully(byte[] var1, int var2, int var3) throws IOException;

   void readFully(byte[] var1) throws IOException;

   void readFully(short[] var1, int var2, int var3) throws IOException;

   void readFully(char[] var1, int var2, int var3) throws IOException;

   void readFully(int[] var1, int var2, int var3) throws IOException;

   void readFully(long[] var1, int var2, int var3) throws IOException;

   void readFully(float[] var1, int var2, int var3) throws IOException;

   void readFully(double[] var1, int var2, int var3) throws IOException;

   long getStreamPosition() throws IOException;

   int getBitOffset() throws IOException;

   void setBitOffset(int var1) throws IOException;

   int readBit() throws IOException;

   long readBits(int var1) throws IOException;

   long length() throws IOException;

   int skipBytes(int var1) throws IOException;

   long skipBytes(long var1) throws IOException;

   void seek(long var1) throws IOException;

   void mark();

   void reset() throws IOException;

   void flushBefore(long var1) throws IOException;

   void flush() throws IOException;

   long getFlushedPosition();

   boolean isCached();

   boolean isCachedMemory();

   boolean isCachedFile();

   void close() throws IOException;
}
