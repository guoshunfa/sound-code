package org.ietf.jgss;

public interface GSSCredential extends Cloneable {
   int INITIATE_AND_ACCEPT = 0;
   int INITIATE_ONLY = 1;
   int ACCEPT_ONLY = 2;
   int DEFAULT_LIFETIME = 0;
   int INDEFINITE_LIFETIME = Integer.MAX_VALUE;

   void dispose() throws GSSException;

   GSSName getName() throws GSSException;

   GSSName getName(Oid var1) throws GSSException;

   int getRemainingLifetime() throws GSSException;

   int getRemainingInitLifetime(Oid var1) throws GSSException;

   int getRemainingAcceptLifetime(Oid var1) throws GSSException;

   int getUsage() throws GSSException;

   int getUsage(Oid var1) throws GSSException;

   Oid[] getMechs() throws GSSException;

   void add(GSSName var1, int var2, int var3, Oid var4, int var5) throws GSSException;

   boolean equals(Object var1);

   int hashCode();
}
