package java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;

public abstract class CharsetProvider {
   protected CharsetProvider() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("charsetProvider"));
      }

   }

   public abstract Iterator<Charset> charsets();

   public abstract Charset charsetForName(String var1);
}
