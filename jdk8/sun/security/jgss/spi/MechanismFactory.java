package sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public interface MechanismFactory {
   Oid getMechanismOid();

   Provider getProvider();

   Oid[] getNameTypes() throws GSSException;

   GSSCredentialSpi getCredentialElement(GSSNameSpi var1, int var2, int var3, int var4) throws GSSException;

   GSSNameSpi getNameElement(String var1, Oid var2) throws GSSException;

   GSSNameSpi getNameElement(byte[] var1, Oid var2) throws GSSException;

   GSSContextSpi getMechanismContext(GSSNameSpi var1, GSSCredentialSpi var2, int var3) throws GSSException;

   GSSContextSpi getMechanismContext(GSSCredentialSpi var1) throws GSSException;

   GSSContextSpi getMechanismContext(byte[] var1) throws GSSException;
}
