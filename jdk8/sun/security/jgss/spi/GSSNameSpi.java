package sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public interface GSSNameSpi {
   Provider getProvider();

   boolean equals(GSSNameSpi var1) throws GSSException;

   boolean equals(Object var1);

   int hashCode();

   byte[] export() throws GSSException;

   Oid getMechanism();

   String toString();

   Oid getStringNameType();

   boolean isAnonymousName();
}
