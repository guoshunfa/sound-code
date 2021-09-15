package sun.net.www.content.text;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URLConnection;

public class plain extends ContentHandler {
   public Object getContent(URLConnection var1) {
      try {
         InputStream var2 = var1.getInputStream();
         return new PlainTextInputStream(var1.getInputStream());
      } catch (IOException var3) {
         return "Error reading document:\n" + var3.toString();
      }
   }
}
