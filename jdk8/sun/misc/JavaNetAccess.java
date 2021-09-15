package sun.misc;

import java.net.InetAddress;
import java.net.URLClassLoader;

public interface JavaNetAccess {
   URLClassPath getURLClassPath(URLClassLoader var1);

   String getOriginalHostName(InetAddress var1);
}
