package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.sourcemodel.ModelNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class PolicyAssertion {
   private final AssertionData data;
   private AssertionSet parameters;
   private NestedPolicy nestedPolicy;

   protected PolicyAssertion() {
      this.data = AssertionData.createAssertionData((QName)null);
      this.parameters = AssertionSet.createAssertionSet((Collection)null);
   }

   /** @deprecated */
   @Deprecated
   protected PolicyAssertion(AssertionData assertionData, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
      this.data = assertionData;
      if (nestedAlternative != null) {
         this.nestedPolicy = NestedPolicy.createNestedPolicy(nestedAlternative);
      }

      this.parameters = AssertionSet.createAssertionSet(assertionParameters);
   }

   protected PolicyAssertion(AssertionData assertionData, Collection<? extends PolicyAssertion> assertionParameters) {
      if (assertionData == null) {
         this.data = AssertionData.createAssertionData((QName)null);
      } else {
         this.data = assertionData;
      }

      this.parameters = AssertionSet.createAssertionSet(assertionParameters);
   }

   public final QName getName() {
      return this.data.getName();
   }

   public final String getValue() {
      return this.data.getValue();
   }

   public boolean isOptional() {
      return this.data.isOptionalAttributeSet();
   }

   public boolean isIgnorable() {
      return this.data.isIgnorableAttributeSet();
   }

   public final boolean isPrivate() {
      return this.data.isPrivateAttributeSet();
   }

   public final Set<Map.Entry<QName, String>> getAttributesSet() {
      return this.data.getAttributesSet();
   }

   public final Map<QName, String> getAttributes() {
      return this.data.getAttributes();
   }

   public final String getAttributeValue(QName name) {
      return this.data.getAttributeValue(name);
   }

   /** @deprecated */
   @Deprecated
   public final boolean hasNestedAssertions() {
      return !this.parameters.isEmpty();
   }

   public final boolean hasParameters() {
      return !this.parameters.isEmpty();
   }

   /** @deprecated */
   @Deprecated
   public final Iterator<PolicyAssertion> getNestedAssertionsIterator() {
      return this.parameters.iterator();
   }

   public final Iterator<PolicyAssertion> getParametersIterator() {
      return this.parameters.iterator();
   }

   boolean isParameter() {
      return this.data.getNodeType() == ModelNode.Type.ASSERTION_PARAMETER_NODE;
   }

   public boolean hasNestedPolicy() {
      return this.getNestedPolicy() != null;
   }

   public NestedPolicy getNestedPolicy() {
      return this.nestedPolicy;
   }

   public <T extends PolicyAssertion> T getImplementation(Class<T> type) {
      return type.isAssignableFrom(this.getClass()) ? (PolicyAssertion)type.cast(this) : null;
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   protected StringBuffer toString(int indentLevel, StringBuffer buffer) {
      String indent = PolicyUtils.Text.createIndent(indentLevel);
      String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
      buffer.append(indent).append("Assertion[").append(this.getClass().getName()).append("] {").append(PolicyUtils.Text.NEW_LINE);
      this.data.toString(indentLevel + 1, buffer);
      buffer.append(PolicyUtils.Text.NEW_LINE);
      if (this.hasParameters()) {
         buffer.append(innerIndent).append("parameters {").append(PolicyUtils.Text.NEW_LINE);
         Iterator var5 = this.parameters.iterator();

         while(var5.hasNext()) {
            PolicyAssertion parameter = (PolicyAssertion)var5.next();
            parameter.toString(indentLevel + 2, buffer).append(PolicyUtils.Text.NEW_LINE);
         }

         buffer.append(innerIndent).append('}').append(PolicyUtils.Text.NEW_LINE);
      } else {
         buffer.append(innerIndent).append("no parameters").append(PolicyUtils.Text.NEW_LINE);
      }

      if (this.hasNestedPolicy()) {
         this.getNestedPolicy().toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
      } else {
         buffer.append(innerIndent).append("no nested policy").append(PolicyUtils.Text.NEW_LINE);
      }

      buffer.append(indent).append('}');
      return buffer;
   }

   boolean isCompatibleWith(PolicyAssertion assertion, PolicyIntersector.CompatibilityMode mode) {
      boolean result = this.data.getName().equals(assertion.data.getName()) && this.hasNestedPolicy() == assertion.hasNestedPolicy();
      if (result && this.hasNestedPolicy()) {
         result = this.getNestedPolicy().getAssertionSet().isCompatibleWith(assertion.getNestedPolicy().getAssertionSet(), mode);
      }

      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof PolicyAssertion)) {
         return false;
      } else {
         boolean var10000;
         boolean result;
         label32: {
            label31: {
               PolicyAssertion that = (PolicyAssertion)obj;
               result = true;
               result = result && this.data.equals(that.data);
               result = result && this.parameters.equals(that.parameters);
               if (result) {
                  if (this.getNestedPolicy() == null) {
                     if (that.getNestedPolicy() == null) {
                        break label31;
                     }
                  } else if (this.getNestedPolicy().equals(that.getNestedPolicy())) {
                     break label31;
                  }
               }

               var10000 = false;
               break label32;
            }

            var10000 = true;
         }

         result = var10000;
         return result;
      }
   }

   public int hashCode() {
      int result = 17;
      int result = 37 * result + this.data.hashCode();
      result = 37 * result + (this.hasParameters() ? 17 : 0);
      result = 37 * result + (this.hasNestedPolicy() ? 17 : 0);
      return result;
   }
}
