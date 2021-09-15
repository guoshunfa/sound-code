package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.UnsolicitedNotification;

final class UnsolicitedResponseImpl implements UnsolicitedNotification {
   private String oid;
   private String[] referrals;
   private byte[] extensionValue;
   private NamingException exception;
   private Control[] controls;
   private static final long serialVersionUID = 5913778898401784775L;

   UnsolicitedResponseImpl(String var1, byte[] var2, Vector<Vector<String>> var3, int var4, String var5, String var6, Control[] var7) {
      this.oid = var1;
      this.extensionValue = var2;
      if (var3 != null && var3.size() > 0) {
         int var8 = var3.size();
         this.referrals = new String[var8];

         for(int var9 = 0; var9 < var8; ++var9) {
            this.referrals[var9] = (String)((Vector)var3.elementAt(var9)).elementAt(0);
         }
      }

      this.exception = LdapCtx.mapErrorCode(var4, var5);
      this.controls = var7;
   }

   public String getID() {
      return this.oid;
   }

   public byte[] getEncodedValue() {
      return this.extensionValue;
   }

   public String[] getReferrals() {
      return this.referrals;
   }

   public NamingException getException() {
      return this.exception;
   }

   public Control[] getControls() throws NamingException {
      return this.controls;
   }
}
