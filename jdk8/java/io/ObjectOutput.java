package java.io;

public interface ObjectOutput extends DataOutput, AutoCloseable {
   void writeObject(Object var1) throws IOException;

   void write(int var1) throws IOException;

   void write(byte[] var1) throws IOException;

   void write(byte[] var1, int var2, int var3) throws IOException;

   void flush() throws IOException;

   void close() throws IOException;
}
