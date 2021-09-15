package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class ServantCachingPolicy extends LocalObject implements Policy {
   public static final int NO_SERVANT_CACHING = 0;
   public static final int FULL_SEMANTICS = 1;
   public static final int INFO_ONLY_SEMANTICS = 2;
   public static final int MINIMAL_SEMANTICS = 3;
   private static ServantCachingPolicy policy = null;
   private static ServantCachingPolicy infoOnlyPolicy = null;
   private static ServantCachingPolicy minimalPolicy = null;
   private int type;

   public String typeToName() {
      switch(this.type) {
      case 1:
         return "FULL";
      case 2:
         return "INFO_ONLY";
      case 3:
         return "MINIMAL";
      default:
         return "UNKNOWN(" + this.type + ")";
      }
   }

   public String toString() {
      return "ServantCachingPolicy[" + this.typeToName() + "]";
   }

   private ServantCachingPolicy(int var1) {
      this.type = var1;
   }

   public int getType() {
      return this.type;
   }

   public static synchronized ServantCachingPolicy getPolicy() {
      return getFullPolicy();
   }

   public static synchronized ServantCachingPolicy getFullPolicy() {
      if (policy == null) {
         policy = new ServantCachingPolicy(1);
      }

      return policy;
   }

   public static synchronized ServantCachingPolicy getInfoOnlyPolicy() {
      if (infoOnlyPolicy == null) {
         infoOnlyPolicy = new ServantCachingPolicy(2);
      }

      return infoOnlyPolicy;
   }

   public static synchronized ServantCachingPolicy getMinimalPolicy() {
      if (minimalPolicy == null) {
         minimalPolicy = new ServantCachingPolicy(3);
      }

      return minimalPolicy;
   }

   public int policy_type() {
      return 1398079488;
   }

   public Policy copy() {
      return this;
   }

   public void destroy() {
   }
}
