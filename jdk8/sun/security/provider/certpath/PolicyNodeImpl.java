package sun.security.provider.certpath;

import java.security.cert.PolicyNode;
import java.security.cert.PolicyQualifierInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

final class PolicyNodeImpl implements PolicyNode {
   private static final String ANY_POLICY = "2.5.29.32.0";
   private PolicyNodeImpl mParent;
   private HashSet<PolicyNodeImpl> mChildren;
   private String mValidPolicy;
   private HashSet<PolicyQualifierInfo> mQualifierSet;
   private boolean mCriticalityIndicator;
   private HashSet<String> mExpectedPolicySet;
   private boolean mOriginalExpectedPolicySet;
   private int mDepth;
   private boolean isImmutable;

   PolicyNodeImpl(PolicyNodeImpl var1, String var2, Set<PolicyQualifierInfo> var3, boolean var4, Set<String> var5, boolean var6) {
      this.isImmutable = false;
      this.mParent = var1;
      this.mChildren = new HashSet();
      if (var2 != null) {
         this.mValidPolicy = var2;
      } else {
         this.mValidPolicy = "";
      }

      if (var3 != null) {
         this.mQualifierSet = new HashSet(var3);
      } else {
         this.mQualifierSet = new HashSet();
      }

      this.mCriticalityIndicator = var4;
      if (var5 != null) {
         this.mExpectedPolicySet = new HashSet(var5);
      } else {
         this.mExpectedPolicySet = new HashSet();
      }

      this.mOriginalExpectedPolicySet = !var6;
      if (this.mParent != null) {
         this.mDepth = this.mParent.getDepth() + 1;
         this.mParent.addChild(this);
      } else {
         this.mDepth = 0;
      }

   }

   PolicyNodeImpl(PolicyNodeImpl var1, PolicyNodeImpl var2) {
      this(var1, var2.mValidPolicy, var2.mQualifierSet, var2.mCriticalityIndicator, var2.mExpectedPolicySet, false);
   }

   public PolicyNode getParent() {
      return this.mParent;
   }

   public Iterator<PolicyNodeImpl> getChildren() {
      return Collections.unmodifiableSet(this.mChildren).iterator();
   }

   public int getDepth() {
      return this.mDepth;
   }

   public String getValidPolicy() {
      return this.mValidPolicy;
   }

   public Set<PolicyQualifierInfo> getPolicyQualifiers() {
      return Collections.unmodifiableSet(this.mQualifierSet);
   }

   public Set<String> getExpectedPolicies() {
      return Collections.unmodifiableSet(this.mExpectedPolicySet);
   }

   public boolean isCritical() {
      return this.mCriticalityIndicator;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.asString());
      Iterator var2 = this.mChildren.iterator();

      while(var2.hasNext()) {
         PolicyNodeImpl var3 = (PolicyNodeImpl)var2.next();
         var1.append((Object)var3);
      }

      return var1.toString();
   }

   boolean isImmutable() {
      return this.isImmutable;
   }

   void setImmutable() {
      if (!this.isImmutable) {
         Iterator var1 = this.mChildren.iterator();

         while(var1.hasNext()) {
            PolicyNodeImpl var2 = (PolicyNodeImpl)var1.next();
            var2.setImmutable();
         }

         this.isImmutable = true;
      }
   }

   private void addChild(PolicyNodeImpl var1) {
      if (this.isImmutable) {
         throw new IllegalStateException("PolicyNode is immutable");
      } else {
         this.mChildren.add(var1);
      }
   }

   void addExpectedPolicy(String var1) {
      if (this.isImmutable) {
         throw new IllegalStateException("PolicyNode is immutable");
      } else {
         if (this.mOriginalExpectedPolicySet) {
            this.mExpectedPolicySet.clear();
            this.mOriginalExpectedPolicySet = false;
         }

         this.mExpectedPolicySet.add(var1);
      }
   }

   void prune(int var1) {
      if (this.isImmutable) {
         throw new IllegalStateException("PolicyNode is immutable");
      } else if (this.mChildren.size() != 0) {
         Iterator var2 = this.mChildren.iterator();

         while(var2.hasNext()) {
            PolicyNodeImpl var3 = (PolicyNodeImpl)var2.next();
            var3.prune(var1);
            if (var3.mChildren.size() == 0 && var1 > this.mDepth + 1) {
               var2.remove();
            }
         }

      }
   }

   void deleteChild(PolicyNode var1) {
      if (this.isImmutable) {
         throw new IllegalStateException("PolicyNode is immutable");
      } else {
         this.mChildren.remove(var1);
      }
   }

   PolicyNodeImpl copyTree() {
      return this.copyTree((PolicyNodeImpl)null);
   }

   private PolicyNodeImpl copyTree(PolicyNodeImpl var1) {
      PolicyNodeImpl var2 = new PolicyNodeImpl(var1, this);
      Iterator var3 = this.mChildren.iterator();

      while(var3.hasNext()) {
         PolicyNodeImpl var4 = (PolicyNodeImpl)var3.next();
         var4.copyTree(var2);
      }

      return var2;
   }

   Set<PolicyNodeImpl> getPolicyNodes(int var1) {
      HashSet var2 = new HashSet();
      this.getPolicyNodes(var1, var2);
      return var2;
   }

   private void getPolicyNodes(int var1, Set<PolicyNodeImpl> var2) {
      if (this.mDepth == var1) {
         var2.add(this);
      } else {
         Iterator var3 = this.mChildren.iterator();

         while(var3.hasNext()) {
            PolicyNodeImpl var4 = (PolicyNodeImpl)var3.next();
            var4.getPolicyNodes(var1, var2);
         }
      }

   }

   Set<PolicyNodeImpl> getPolicyNodesExpected(int var1, String var2, boolean var3) {
      return var2.equals("2.5.29.32.0") ? this.getPolicyNodes(var1) : this.getPolicyNodesExpectedHelper(var1, var2, var3);
   }

   private Set<PolicyNodeImpl> getPolicyNodesExpectedHelper(int var1, String var2, boolean var3) {
      HashSet var4 = new HashSet();
      if (this.mDepth < var1) {
         Iterator var5 = this.mChildren.iterator();

         while(var5.hasNext()) {
            PolicyNodeImpl var6 = (PolicyNodeImpl)var5.next();
            var4.addAll(var6.getPolicyNodesExpectedHelper(var1, var2, var3));
         }
      } else if (var3) {
         if (this.mExpectedPolicySet.contains("2.5.29.32.0")) {
            var4.add(this);
         }
      } else if (this.mExpectedPolicySet.contains(var2)) {
         var4.add(this);
      }

      return var4;
   }

   Set<PolicyNodeImpl> getPolicyNodesValid(int var1, String var2) {
      HashSet var3 = new HashSet();
      if (this.mDepth < var1) {
         Iterator var4 = this.mChildren.iterator();

         while(var4.hasNext()) {
            PolicyNodeImpl var5 = (PolicyNodeImpl)var4.next();
            var3.addAll(var5.getPolicyNodesValid(var1, var2));
         }
      } else if (this.mValidPolicy.equals(var2)) {
         var3.add(this);
      }

      return var3;
   }

   private static String policyToString(String var0) {
      return var0.equals("2.5.29.32.0") ? "anyPolicy" : var0;
   }

   String asString() {
      if (this.mParent == null) {
         return "anyPolicy  ROOT\n";
      } else {
         StringBuilder var1 = new StringBuilder();
         int var2 = 0;

         for(int var3 = this.getDepth(); var2 < var3; ++var2) {
            var1.append("  ");
         }

         var1.append(policyToString(this.getValidPolicy()));
         var1.append("  CRIT: ");
         var1.append(this.isCritical());
         var1.append("  EP: ");
         Iterator var4 = this.getExpectedPolicies().iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            var1.append(policyToString(var5));
            var1.append(" ");
         }

         var1.append(" (");
         var1.append(this.getDepth());
         var1.append(")\n");
         return var1.toString();
      }
   }
}
