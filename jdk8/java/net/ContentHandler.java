package java.net;

import java.io.IOException;

public abstract class ContentHandler {
   public abstract Object getContent(URLConnection var1) throws IOException;

   public Object getContent(URLConnection var1, Class[] var2) throws IOException {
      Object var3 = this.getContent(var1);

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (var2[var4].isInstance(var3)) {
            return var3;
         }
      }

      return null;
   }
}
