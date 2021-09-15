package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Control;

public final class LdapResult {
   int msgId;
   public int status;
   String matchedDN;
   String errorMessage;
   Vector<Vector<String>> referrals = null;
   LdapReferralException refEx = null;
   Vector<LdapEntry> entries = null;
   Vector<Control> resControls = null;
   public byte[] serverCreds = null;
   String extensionId = null;
   byte[] extensionValue = null;

   boolean compareToSearchResult(String var1) {
      boolean var2 = false;
      switch(this.status) {
      case 5:
         this.status = 0;
         this.entries = new Vector(0);
         var2 = true;
         break;
      case 6:
         this.status = 0;
         this.entries = new Vector(1, 1);
         BasicAttributes var3 = new BasicAttributes(true);
         LdapEntry var4 = new LdapEntry(var1, var3);
         this.entries.addElement(var4);
         var2 = true;
         break;
      default:
         var2 = false;
      }

      return var2;
   }
}
