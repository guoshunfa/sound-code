package com.sun.xml.internal.ws.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class NestedPolicy extends Policy {
   private static final String NESTED_POLICY_TOSTRING_NAME = "nested policy";

   private NestedPolicy(AssertionSet set) {
      super("nested policy", Arrays.asList(set));
   }

   private NestedPolicy(String name, String policyId, AssertionSet set) {
      super("nested policy", name, (String)policyId, (Collection)Arrays.asList(set));
   }

   static NestedPolicy createNestedPolicy(AssertionSet set) {
      return new NestedPolicy(set);
   }

   static NestedPolicy createNestedPolicy(String name, String policyId, AssertionSet set) {
      return new NestedPolicy(name, policyId, set);
   }

   public AssertionSet getAssertionSet() {
      Iterator<AssertionSet> iterator = this.iterator();
      return iterator.hasNext() ? (AssertionSet)iterator.next() : null;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof NestedPolicy)) {
         return false;
      } else {
         NestedPolicy that = (NestedPolicy)obj;
         return super.equals(that);
      }
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   StringBuffer toString(int indentLevel, StringBuffer buffer) {
      return super.toString(indentLevel, buffer);
   }
}
