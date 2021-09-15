package java.nio.file.attribute;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public interface UserDefinedFileAttributeView extends FileAttributeView {
   String name();

   List<String> list() throws IOException;

   int size(String var1) throws IOException;

   int read(String var1, ByteBuffer var2) throws IOException;

   int write(String var1, ByteBuffer var2) throws IOException;

   void delete(String var1) throws IOException;
}
