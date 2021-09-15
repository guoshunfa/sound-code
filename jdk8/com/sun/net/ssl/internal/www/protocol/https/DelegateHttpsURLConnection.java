package com.sun.net.ssl.internal.www.protocol.https;

import com.sun.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection;

public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection {
   public HttpsURLConnection httpsURLConnection;

   DelegateHttpsURLConnection(URL var1, sun.net.www.protocol.http.Handler var2, HttpsURLConnection var3) throws IOException {
      this(var1, (Proxy)null, var2, var3);
   }

   DelegateHttpsURLConnection(URL var1, Proxy var2, sun.net.www.protocol.http.Handler var3, HttpsURLConnection var4) throws IOException {
      super(var1, var2, var3);
      this.httpsURLConnection = var4;
   }

   protected SSLSocketFactory getSSLSocketFactory() {
      return this.httpsURLConnection.getSSLSocketFactory();
   }

   protected HostnameVerifier getHostnameVerifier() {
      return new VerifierWrapper(this.httpsURLConnection.getHostnameVerifier());
   }

   protected void dispose() throws Throwable {
      super.finalize();
   }
}
