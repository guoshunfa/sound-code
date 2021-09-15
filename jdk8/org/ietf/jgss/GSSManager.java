package org.ietf.jgss;

import java.security.Provider;
import sun.security.jgss.GSSManagerImpl;

public abstract class GSSManager {
   public static GSSManager getInstance() {
      return new GSSManagerImpl();
   }

   public abstract Oid[] getMechs();

   public abstract Oid[] getNamesForMech(Oid var1) throws GSSException;

   public abstract Oid[] getMechsForName(Oid var1);

   public abstract GSSName createName(String var1, Oid var2) throws GSSException;

   public abstract GSSName createName(byte[] var1, Oid var2) throws GSSException;

   public abstract GSSName createName(String var1, Oid var2, Oid var3) throws GSSException;

   public abstract GSSName createName(byte[] var1, Oid var2, Oid var3) throws GSSException;

   public abstract GSSCredential createCredential(int var1) throws GSSException;

   public abstract GSSCredential createCredential(GSSName var1, int var2, Oid var3, int var4) throws GSSException;

   public abstract GSSCredential createCredential(GSSName var1, int var2, Oid[] var3, int var4) throws GSSException;

   public abstract GSSContext createContext(GSSName var1, Oid var2, GSSCredential var3, int var4) throws GSSException;

   public abstract GSSContext createContext(GSSCredential var1) throws GSSException;

   public abstract GSSContext createContext(byte[] var1) throws GSSException;

   public abstract void addProviderAtFront(Provider var1, Oid var2) throws GSSException;

   public abstract void addProviderAtEnd(Provider var1, Oid var2) throws GSSException;
}
