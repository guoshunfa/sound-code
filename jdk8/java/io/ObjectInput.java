package java.io;

public interface ObjectInput extends DataInput, AutoCloseable {
   Object readObject() throws ClassNotFoundException, IOException;

   int read() throws IOException;

   int read(byte[] var1) throws IOException;

   int read(byte[] var1, int var2, int var3) throws IOException;

   long skip(long var1) throws IOException;

   int available() throws IOException;

   void close() throws IOException;
}
