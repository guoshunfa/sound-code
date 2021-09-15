package javax.naming.ldap;

import java.io.Serializable;
import javax.naming.NamingException;

public interface ExtendedRequest extends Serializable {
   String getID();

   byte[] getEncodedValue();

   ExtendedResponse createExtendedResponse(String var1, byte[] var2, int var3, int var4) throws NamingException;
}
