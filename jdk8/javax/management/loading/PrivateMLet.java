package javax.management.loading;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

public class PrivateMLet extends MLet implements PrivateClassLoader {
   private static final long serialVersionUID = 2503458973393711979L;

   public PrivateMLet(URL[] var1, boolean var2) {
      super(var1, var2);
   }

   public PrivateMLet(URL[] var1, ClassLoader var2, boolean var3) {
      super(var1, var2, var3);
   }

   public PrivateMLet(URL[] var1, ClassLoader var2, URLStreamHandlerFactory var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
