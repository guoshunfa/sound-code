package com.sun.jndi.ldap;

import java.util.Hashtable;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.ReferralException;
import javax.naming.ldap.Control;

public final class LdapReferralException extends javax.naming.ldap.LdapReferralException {
   private static final long serialVersionUID = 627059076356906399L;
   private int handleReferrals;
   private Hashtable<?, ?> envprops;
   private String nextName;
   private Control[] reqCtls;
   private Vector<?> referrals = null;
   private int referralIndex = 0;
   private int referralCount = 0;
   private boolean foundEntry = false;
   private boolean skipThisReferral = false;
   private int hopCount = 1;
   private NamingException errorEx = null;
   private String newRdn = null;
   private boolean debug = false;
   LdapReferralException nextReferralEx = null;

   LdapReferralException(Name var1, Object var2, Name var3, String var4, Hashtable<?, ?> var5, String var6, int var7, Control[] var8) {
      super(var4);
      if (this.debug) {
         System.out.println("LdapReferralException constructor");
      }

      this.setResolvedName(var1);
      this.setResolvedObj(var2);
      this.setRemainingName(var3);
      this.envprops = var5;
      this.nextName = var6;
      this.handleReferrals = var7;
      this.reqCtls = var7 != 1 && var7 != 4 ? null : var8;
   }

   public Context getReferralContext() throws NamingException {
      return this.getReferralContext(this.envprops, (Control[])null);
   }

   public Context getReferralContext(Hashtable<?, ?> var1) throws NamingException {
      return this.getReferralContext(var1, (Control[])null);
   }

   public Context getReferralContext(Hashtable<?, ?> var1, Control[] var2) throws NamingException {
      if (this.debug) {
         System.out.println("LdapReferralException.getReferralContext");
      }

      LdapReferralContext var3 = new LdapReferralContext(this, var1, var2, this.reqCtls, this.nextName, this.skipThisReferral, this.handleReferrals);
      var3.setHopCount(this.hopCount + 1);
      if (this.skipThisReferral) {
         this.skipThisReferral = false;
      }

      return var3;
   }

   public Object getReferralInfo() {
      if (this.debug) {
         System.out.println("LdapReferralException.getReferralInfo");
         System.out.println("  referralIndex=" + this.referralIndex);
      }

      return this.hasMoreReferrals() ? this.referrals.elementAt(this.referralIndex) : null;
   }

   public void retryReferral() {
      if (this.debug) {
         System.out.println("LdapReferralException.retryReferral");
      }

      if (this.referralIndex > 0) {
         --this.referralIndex;
      }

   }

   public boolean skipReferral() {
      if (this.debug) {
         System.out.println("LdapReferralException.skipReferral");
      }

      this.skipThisReferral = true;

      try {
         this.getNextReferral();
      } catch (ReferralException var2) {
      }

      return this.hasMoreReferrals() || this.hasMoreReferralExceptions();
   }

   void setReferralInfo(Vector<?> var1, boolean var2) {
      if (this.debug) {
         System.out.println("LdapReferralException.setReferralInfo");
      }

      this.referrals = var1;
      this.referralCount = var1 == null ? 0 : var1.size();
      if (this.debug) {
         if (var1 != null) {
            for(int var3 = 0; var3 < this.referralCount; ++var3) {
               System.out.println("  [" + var3 + "] " + var1.elementAt(var3));
            }
         } else {
            System.out.println("setReferralInfo : referrals == null");
         }
      }

   }

   String getNextReferral() throws ReferralException {
      if (this.debug) {
         System.out.println("LdapReferralException.getNextReferral");
      }

      if (this.hasMoreReferrals()) {
         return (String)this.referrals.elementAt(this.referralIndex++);
      } else if (this.hasMoreReferralExceptions()) {
         throw this.nextReferralEx;
      } else {
         return null;
      }
   }

   LdapReferralException appendUnprocessedReferrals(LdapReferralException var1) {
      if (this.debug) {
         System.out.println("LdapReferralException.appendUnprocessedReferrals");
         this.dump();
         if (var1 != null) {
            var1.dump();
         }
      }

      LdapReferralException var2 = this;
      if (!this.hasMoreReferrals()) {
         var2 = this.nextReferralEx;
         if (this.errorEx != null && var2 != null) {
            var2.setNamingException(this.errorEx);
         }
      }

      if (this == var1) {
         return var2;
      } else {
         if (var1 != null && !var1.hasMoreReferrals()) {
            var1 = var1.nextReferralEx;
         }

         if (var1 == null) {
            return var2;
         } else {
            LdapReferralException var3;
            for(var3 = var2; var3.nextReferralEx != null; var3 = var3.nextReferralEx) {
            }

            var3.nextReferralEx = var1;
            return var2;
         }
      }
   }

   boolean hasMoreReferrals() {
      if (this.debug) {
         System.out.println("LdapReferralException.hasMoreReferrals");
      }

      return !this.foundEntry && this.referralIndex < this.referralCount;
   }

   boolean hasMoreReferralExceptions() {
      if (this.debug) {
         System.out.println("LdapReferralException.hasMoreReferralExceptions");
      }

      return this.nextReferralEx != null;
   }

   void setHopCount(int var1) {
      if (this.debug) {
         System.out.println("LdapReferralException.setHopCount");
      }

      this.hopCount = var1;
   }

   void setNameResolved(boolean var1) {
      if (this.debug) {
         System.out.println("LdapReferralException.setNameResolved");
      }

      this.foundEntry = var1;
   }

   void setNamingException(NamingException var1) {
      if (this.debug) {
         System.out.println("LdapReferralException.setNamingException");
      }

      if (this.errorEx == null) {
         var1.setRootCause(this);
         this.errorEx = var1;
      }

   }

   String getNewRdn() {
      if (this.debug) {
         System.out.println("LdapReferralException.getNewRdn");
      }

      return this.newRdn;
   }

   void setNewRdn(String var1) {
      if (this.debug) {
         System.out.println("LdapReferralException.setNewRdn");
      }

      this.newRdn = var1;
   }

   NamingException getNamingException() {
      if (this.debug) {
         System.out.println("LdapReferralException.getNamingException");
      }

      return this.errorEx;
   }

   void dump() {
      System.out.println();
      System.out.println("LdapReferralException.dump");

      for(LdapReferralException var1 = this; var1 != null; var1 = var1.nextReferralEx) {
         var1.dumpState();
      }

   }

   private void dumpState() {
      System.out.println("LdapReferralException.dumpState");
      System.out.println("  hashCode=" + this.hashCode());
      System.out.println("  foundEntry=" + this.foundEntry);
      System.out.println("  skipThisReferral=" + this.skipThisReferral);
      System.out.println("  referralIndex=" + this.referralIndex);
      if (this.referrals != null) {
         System.out.println("  referrals:");

         for(int var1 = 0; var1 < this.referralCount; ++var1) {
            System.out.println("    [" + var1 + "] " + this.referrals.elementAt(var1));
         }
      } else {
         System.out.println("  referrals=null");
      }

      System.out.println("  errorEx=" + this.errorEx);
      if (this.nextReferralEx == null) {
         System.out.println("  nextRefEx=null");
      } else {
         System.out.println("  nextRefEx=" + this.nextReferralEx.hashCode());
      }

      System.out.println();
   }
}
