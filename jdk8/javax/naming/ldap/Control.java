package javax.naming.ldap;

import java.io.Serializable;

public interface Control extends Serializable {
   boolean CRITICAL = true;
   boolean NONCRITICAL = false;

   String getID();

   boolean isCritical();

   byte[] getEncodedValue();
}
