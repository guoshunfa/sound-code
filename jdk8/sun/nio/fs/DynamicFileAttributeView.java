package sun.nio.fs;

import java.io.IOException;
import java.util.Map;

interface DynamicFileAttributeView {
   void setAttribute(String var1, Object var2) throws IOException;

   Map<String, Object> readAttributes(String[] var1) throws IOException;
}
