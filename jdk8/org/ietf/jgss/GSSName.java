package org.ietf.jgss;

public interface GSSName {
   Oid NT_HOSTBASED_SERVICE = Oid.getInstance("1.2.840.113554.1.2.1.4");
   Oid NT_USER_NAME = Oid.getInstance("1.2.840.113554.1.2.1.1");
   Oid NT_MACHINE_UID_NAME = Oid.getInstance("1.2.840.113554.1.2.1.2");
   Oid NT_STRING_UID_NAME = Oid.getInstance("1.2.840.113554.1.2.1.3");
   Oid NT_ANONYMOUS = Oid.getInstance("1.3.6.1.5.6.3");
   Oid NT_EXPORT_NAME = Oid.getInstance("1.3.6.1.5.6.4");

   boolean equals(GSSName var1) throws GSSException;

   boolean equals(Object var1);

   int hashCode();

   GSSName canonicalize(Oid var1) throws GSSException;

   byte[] export() throws GSSException;

   String toString();

   Oid getStringNameType() throws GSSException;

   boolean isAnonymous();

   boolean isMN();
}
