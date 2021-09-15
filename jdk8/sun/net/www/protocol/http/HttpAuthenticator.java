package sun.net.www.protocol.http;

import java.net.URL;

/** @deprecated */
@Deprecated
public interface HttpAuthenticator {
   boolean schemeSupported(String var1);

   String authString(URL var1, String var2, String var3);
}
