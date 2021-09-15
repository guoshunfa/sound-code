package javax.naming.ldap;

import java.io.Serializable;

public interface ExtendedResponse extends Serializable {
   String getID();

   byte[] getEncodedValue();
}
