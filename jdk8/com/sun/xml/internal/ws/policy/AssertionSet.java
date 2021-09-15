package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public final class AssertionSet implements Iterable<PolicyAssertion>, Comparable<AssertionSet> {
   private static final AssertionSet EMPTY_ASSERTION_SET = new AssertionSet(Collections.unmodifiableList(new LinkedList()));
   private static final Comparator<PolicyAssertion> ASSERTION_COMPARATOR = new Comparator<PolicyAssertion>() {
      public int compare(PolicyAssertion pa1, PolicyAssertion pa2) {
         if (pa1.equals(pa2)) {
            return 0;
         } else {
            int result = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(pa1.getName(), pa2.getName());
            if (result != 0) {
               return result;
            } else {
               result = PolicyUtils.Comparison.compareNullableStrings(pa1.getValue(), pa2.getValue());
               if (result != 0) {
                  return result;
               } else {
                  result = PolicyUtils.Comparison.compareBoolean(pa1.hasNestedAssertions(), pa2.hasNestedAssertions());
                  if (result != 0) {
                     return result;
                  } else {
                     result = PolicyUtils.Comparison.compareBoolean(pa1.hasNestedPolicy(), pa2.hasNestedPolicy());
                     return result != 0 ? result : Math.round(Math.signum((float)(pa1.hashCode() - pa2.hashCode())));
                  }
               }
            }
         }
      }
   };
   private final List<PolicyAssertion> assertions;
   private final Set<QName> vocabulary;
   private final Collection<QName> immutableVocabulary;

   private AssertionSet(List<PolicyAssertion> list) {
      this.vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
      this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);

      assert list != null : LocalizationMessages.WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL();

      this.assertions = list;
   }

   private AssertionSet(Collection<AssertionSet> alternatives) {
      this.vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
      this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
      this.assertions = new LinkedList();
      Iterator var2 = alternatives.iterator();

      while(var2.hasNext()) {
         AssertionSet alternative = (AssertionSet)var2.next();
         this.addAll(alternative.assertions);
      }

   }

   private boolean add(PolicyAssertion assertion) {
      if (assertion == null) {
         return false;
      } else if (this.assertions.contains(assertion)) {
         return false;
      } else {
         this.assertions.add(assertion);
         this.vocabulary.add(assertion.getName());
         return true;
      }
   }

   private boolean addAll(Collection<? extends PolicyAssertion> assertions) {
      boolean result = true;
      PolicyAssertion assertion;
      if (assertions != null) {
         for(Iterator var3 = assertions.iterator(); var3.hasNext(); result &= this.add(assertion)) {
            assertion = (PolicyAssertion)var3.next();
         }
      }

      return result;
   }

   Collection<PolicyAssertion> getAssertions() {
      return this.assertions;
   }

   Collection<QName> getVocabulary() {
      return this.immutableVocabulary;
   }

   boolean isCompatibleWith(AssertionSet alternative, PolicyIntersector.CompatibilityMode mode) {
      boolean result = mode == PolicyIntersector.CompatibilityMode.LAX || this.vocabulary.equals(alternative.vocabulary);
      result = result && this.areAssertionsCompatible(alternative, mode);
      result = result && alternative.areAssertionsCompatible(this, mode);
      return result;
   }

   private boolean areAssertionsCompatible(AssertionSet alternative, PolicyIntersector.CompatibilityMode mode) {
      Iterator var3 = this.assertions.iterator();

      label30:
      while(true) {
         PolicyAssertion thisAssertion;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            thisAssertion = (PolicyAssertion)var3.next();
         } while(mode != PolicyIntersector.CompatibilityMode.STRICT && thisAssertion.isIgnorable());

         Iterator var5 = alternative.assertions.iterator();

         while(var5.hasNext()) {
            PolicyAssertion thatAssertion = (PolicyAssertion)var5.next();
            if (thisAssertion.isCompatibleWith(thatAssertion, mode)) {
               continue label30;
            }
         }

         return false;
      }
   }

   public static AssertionSet createMergedAssertionSet(Collection<AssertionSet> alternatives) {
      if (alternatives != null && !alternatives.isEmpty()) {
         AssertionSet result = new AssertionSet(alternatives);
         Collections.sort(result.assertions, ASSERTION_COMPARATOR);
         return result;
      } else {
         return EMPTY_ASSERTION_SET;
      }
   }

   public static AssertionSet createAssertionSet(Collection<? extends PolicyAssertion> assertions) {
      if (assertions != null && !assertions.isEmpty()) {
         AssertionSet result = new AssertionSet(new LinkedList());
         result.addAll(assertions);
         Collections.sort(result.assertions, ASSERTION_COMPARATOR);
         return result;
      } else {
         return EMPTY_ASSERTION_SET;
      }
   }

   public static AssertionSet emptyAssertionSet() {
      return EMPTY_ASSERTION_SET;
   }

   public Iterator<PolicyAssertion> iterator() {
      return this.assertions.iterator();
   }

   public Collection<PolicyAssertion> get(QName name) {
      List<PolicyAssertion> matched = new LinkedList();
      if (this.vocabulary.contains(name)) {
         Iterator var3 = this.assertions.iterator();

         while(var3.hasNext()) {
            PolicyAssertion assertion = (PolicyAssertion)var3.next();
            if (assertion.getName().equals(name)) {
               matched.add(assertion);
            }
         }
      }

      return matched;
   }

   public boolean isEmpty() {
      return this.assertions.isEmpty();
   }

   public boolean contains(QName assertionName) {
      return this.vocabulary.contains(assertionName);
   }

   public int compareTo(AssertionSet that) {
      if (this.equals(that)) {
         return 0;
      } else {
         Iterator<QName> vIterator1 = this.getVocabulary().iterator();
         Iterator vIterator2 = that.getVocabulary().iterator();

         int result;
         do {
            if (!vIterator1.hasNext()) {
               if (vIterator2.hasNext()) {
                  return -1;
               }

               Iterator<PolicyAssertion> pIterator1 = this.getAssertions().iterator();
               Iterator pIterator2 = that.getAssertions().iterator();

               int result;
               do {
                  if (!pIterator1.hasNext()) {
                     if (pIterator2.hasNext()) {
                        return -1;
                     }

                     return 1;
                  }

                  PolicyAssertion pa1 = (PolicyAssertion)pIterator1.next();
                  if (!pIterator2.hasNext()) {
                     return 1;
                  }

                  PolicyAssertion pa2 = (PolicyAssertion)pIterator2.next();
                  result = ASSERTION_COMPARATOR.compare(pa1, pa2);
               } while(result == 0);

               return result;
            }

            QName entry1 = (QName)vIterator1.next();
            if (!vIterator2.hasNext()) {
               return 1;
            }

            QName entry2 = (QName)vIterator2.next();
            result = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(entry1, entry2);
         } while(result == 0);

         return result;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof AssertionSet)) {
         return false;
      } else {
         AssertionSet that = (AssertionSet)obj;
         boolean result = true;
         result = result && this.vocabulary.equals(that.vocabulary);
         result = result && this.assertions.size() == that.assertions.size() && this.assertions.containsAll(that.assertions);
         return result;
      }
   }

   public int hashCode() {
      int result = 17;
      int result = 37 * result + this.vocabulary.hashCode();
      result = 37 * result + this.assertions.hashCode();
      return result;
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   StringBuffer toString(int indentLevel, StringBuffer buffer) {
      String indent = PolicyUtils.Text.createIndent(indentLevel);
      String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
      buffer.append(indent).append("assertion set {").append(PolicyUtils.Text.NEW_LINE);
      if (this.assertions.isEmpty()) {
         buffer.append(innerIndent).append("no assertions").append(PolicyUtils.Text.NEW_LINE);
      } else {
         Iterator var5 = this.assertions.iterator();

         while(var5.hasNext()) {
            PolicyAssertion assertion = (PolicyAssertion)var5.next();
            assertion.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
         }
      }

      buffer.append(indent).append('}');
      return buffer;
   }
}
