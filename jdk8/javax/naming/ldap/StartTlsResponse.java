package javax.naming.ldap;

import java.io.IOException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public abstract class StartTlsResponse implements ExtendedResponse {
   public static final String OID = "1.3.6.1.4.1.1466.20037";
   private static final long serialVersionUID = 8372842182579276418L;

   protected StartTlsResponse() {
   }

   public String getID() {
      return "1.3.6.1.4.1.1466.20037";
   }

   public byte[] getEncodedValue() {
      return null;
   }

   public abstract void setEnabledCipherSuites(String[] var1);

   public abstract void setHostnameVerifier(HostnameVerifier var1);

   public abstract SSLSession negotiate() throws IOException;

   public abstract SSLSession negotiate(SSLSocketFactory var1) throws IOException;

   public abstract void close() throws IOException;
}
