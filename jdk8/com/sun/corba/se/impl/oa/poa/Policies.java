package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.spi.extension.CopyObjectPolicy;
import com.sun.corba.se.spi.extension.ServantCachingPolicy;
import com.sun.corba.se.spi.extension.ZeroPortPolicy;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.POAPackage.InvalidPolicy;

public final class Policies {
   private static final int MIN_POA_POLICY_ID = 16;
   private static final int MAX_POA_POLICY_ID = 22;
   private static final int POLICY_TABLE_SIZE = 7;
   int defaultObjectCopierFactoryId;
   private HashMap policyMap;
   public static final Policies defaultPolicies = new Policies();
   public static final Policies rootPOAPolicies = new Policies(0, 0, 0, 1, 0, 0, 0);
   private int[] poaPolicyValues;

   private int getPolicyValue(int var1) {
      return this.poaPolicyValues[var1 - 16];
   }

   private void setPolicyValue(int var1, int var2) {
      this.poaPolicyValues[var1 - 16] = var2;
   }

   private Policies(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.policyMap = new HashMap();
      this.poaPolicyValues = new int[]{var1, var2, var3, var4, var5, var6, var7};
   }

   private Policies() {
      this(0, 0, 0, 1, 1, 0, 0);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("Policies[");
      boolean var2 = true;

      for(Iterator var3 = this.policyMap.values().iterator(); var3.hasNext(); var1.append(var3.next().toString())) {
         if (var2) {
            var2 = false;
         } else {
            var1.append(",");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   private int getPOAPolicyValue(Policy var1) {
      if (var1 instanceof ThreadPolicy) {
         return ((ThreadPolicy)var1).value().value();
      } else if (var1 instanceof LifespanPolicy) {
         return ((LifespanPolicy)var1).value().value();
      } else if (var1 instanceof IdUniquenessPolicy) {
         return ((IdUniquenessPolicy)var1).value().value();
      } else if (var1 instanceof IdAssignmentPolicy) {
         return ((IdAssignmentPolicy)var1).value().value();
      } else if (var1 instanceof ServantRetentionPolicy) {
         return ((ServantRetentionPolicy)var1).value().value();
      } else if (var1 instanceof RequestProcessingPolicy) {
         return ((RequestProcessingPolicy)var1).value().value();
      } else {
         return var1 instanceof ImplicitActivationPolicy ? ((ImplicitActivationPolicy)var1).value().value() : -1;
      }
   }

   private void checkForPolicyError(BitSet var1) throws InvalidPolicy {
      for(short var2 = 0; var2 < var1.length(); ++var2) {
         if (var1.get(var2)) {
            throw new InvalidPolicy(var2);
         }
      }

   }

   private void addToErrorSet(Policy[] var1, int var2, BitSet var3) {
      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var1[var4].policy_type() == var2) {
            var3.set(var4);
            return;
         }
      }

   }

   Policies(Policy[] var1, int var2) throws InvalidPolicy {
      this();
      this.defaultObjectCopierFactoryId = var2;
      if (var1 != null) {
         BitSet var3 = new BitSet(var1.length);

         for(short var4 = 0; var4 < var1.length; ++var4) {
            Policy var5 = var1[var4];
            int var6 = this.getPOAPolicyValue(var5);
            Integer var7 = new Integer(var5.policy_type());
            Policy var8 = (Policy)((Policy)this.policyMap.get(var7));
            if (var8 == null) {
               this.policyMap.put(var7, var5);
            }

            if (var6 >= 0) {
               this.setPolicyValue(var7, var6);
               if (var8 != null && this.getPOAPolicyValue(var8) != var6) {
                  var3.set(var4);
               }
            }
         }

         if (!this.retainServants() && this.useActiveMapOnly()) {
            this.addToErrorSet(var1, 21, var3);
            this.addToErrorSet(var1, 22, var3);
         }

         if (this.isImplicitlyActivated()) {
            if (!this.retainServants()) {
               this.addToErrorSet(var1, 20, var3);
               this.addToErrorSet(var1, 21, var3);
            }

            if (!this.isSystemAssignedIds()) {
               this.addToErrorSet(var1, 20, var3);
               this.addToErrorSet(var1, 19, var3);
            }
         }

         this.checkForPolicyError(var3);
      }
   }

   public Policy get_effective_policy(int var1) {
      Integer var2 = new Integer(var1);
      Policy var3 = (Policy)((Policy)this.policyMap.get(var2));
      return var3;
   }

   public final boolean isOrbControlledThreads() {
      return this.getPolicyValue(16) == 0;
   }

   public final boolean isSingleThreaded() {
      return this.getPolicyValue(16) == 1;
   }

   public final boolean isTransient() {
      return this.getPolicyValue(17) == 0;
   }

   public final boolean isPersistent() {
      return this.getPolicyValue(17) == 1;
   }

   public final boolean isUniqueIds() {
      return this.getPolicyValue(18) == 0;
   }

   public final boolean isMultipleIds() {
      return this.getPolicyValue(18) == 1;
   }

   public final boolean isUserAssignedIds() {
      return this.getPolicyValue(19) == 0;
   }

   public final boolean isSystemAssignedIds() {
      return this.getPolicyValue(19) == 1;
   }

   public final boolean retainServants() {
      return this.getPolicyValue(21) == 0;
   }

   public final boolean useActiveMapOnly() {
      return this.getPolicyValue(22) == 0;
   }

   public final boolean useDefaultServant() {
      return this.getPolicyValue(22) == 1;
   }

   public final boolean useServantManager() {
      return this.getPolicyValue(22) == 2;
   }

   public final boolean isImplicitlyActivated() {
      return this.getPolicyValue(20) == 0;
   }

   public final int servantCachingLevel() {
      Integer var1 = new Integer(1398079488);
      ServantCachingPolicy var2 = (ServantCachingPolicy)this.policyMap.get(var1);
      return var2 == null ? 0 : var2.getType();
   }

   public final boolean forceZeroPort() {
      Integer var1 = new Integer(1398079489);
      ZeroPortPolicy var2 = (ZeroPortPolicy)this.policyMap.get(var1);
      return var2 == null ? false : var2.forceZeroPort();
   }

   public final int getCopierId() {
      Integer var1 = new Integer(1398079490);
      CopyObjectPolicy var2 = (CopyObjectPolicy)this.policyMap.get(var1);
      return var2 != null ? var2.getValue() : this.defaultObjectCopierFactoryId;
   }
}
