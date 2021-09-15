package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import javax.naming.ldap.Control;

class DigestClientId extends SimpleClientId {
   private static final String[] SASL_PROPS = new String[]{"java.naming.security.sasl.authorizationId", "java.naming.security.sasl.realm", "javax.security.sasl.qop", "javax.security.sasl.strength", "javax.security.sasl.reuse", "javax.security.sasl.server.authentication", "javax.security.sasl.maxbuffer", "javax.security.sasl.policy.noplaintext", "javax.security.sasl.policy.noactive", "javax.security.sasl.policy.nodictionary", "javax.security.sasl.policy.noanonymous", "javax.security.sasl.policy.forward", "javax.security.sasl.policy.credentials"};
   private final String[] propvals;
   private final int myHash;

   DigestClientId(int var1, String var2, int var3, String var4, Control[] var5, OutputStream var6, String var7, String var8, Object var9, Hashtable<?, ?> var10) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (var10 == null) {
         this.propvals = null;
      } else {
         this.propvals = new String[SASL_PROPS.length];

         for(int var11 = 0; var11 < SASL_PROPS.length; ++var11) {
            this.propvals[var11] = (String)var10.get(SASL_PROPS[var11]);
         }
      }

      this.myHash = super.hashCode() ^ Arrays.hashCode((Object[])this.propvals);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DigestClientId)) {
         return false;
      } else {
         DigestClientId var2 = (DigestClientId)var1;
         return this.myHash == var2.myHash && super.equals(var1) && Arrays.equals((Object[])this.propvals, (Object[])var2.propvals);
      }
   }

   public int hashCode() {
      return this.myHash;
   }

   public String toString() {
      if (this.propvals != null) {
         StringBuffer var1 = new StringBuffer();

         for(int var2 = 0; var2 < this.propvals.length; ++var2) {
            var1.append(':');
            if (this.propvals[var2] != null) {
               var1.append(this.propvals[var2]);
            }
         }

         return super.toString() + var1.toString();
      } else {
         return super.toString();
      }
   }
}
