package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public final class AssertionData implements Cloneable, Serializable {
   private static final long serialVersionUID = 4416256070795526315L;
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionData.class);
   private final QName name;
   private final String value;
   private final Map<QName, String> attributes;
   private ModelNode.Type type;
   private boolean optional;
   private boolean ignorable;

   public static AssertionData createAssertionData(QName name) throws IllegalArgumentException {
      return new AssertionData(name, (String)null, (Map)null, ModelNode.Type.ASSERTION, false, false);
   }

   public static AssertionData createAssertionParameterData(QName name) throws IllegalArgumentException {
      return new AssertionData(name, (String)null, (Map)null, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
   }

   public static AssertionData createAssertionData(QName name, String value, Map<QName, String> attributes, boolean optional, boolean ignorable) throws IllegalArgumentException {
      return new AssertionData(name, value, attributes, ModelNode.Type.ASSERTION, optional, ignorable);
   }

   public static AssertionData createAssertionParameterData(QName name, String value, Map<QName, String> attributes) throws IllegalArgumentException {
      return new AssertionData(name, value, attributes, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
   }

   AssertionData(QName name, String value, Map<QName, String> attributes, ModelNode.Type type, boolean optional, boolean ignorable) throws IllegalArgumentException {
      this.name = name;
      this.value = value;
      this.optional = optional;
      this.ignorable = ignorable;
      this.attributes = new HashMap();
      if (attributes != null && !attributes.isEmpty()) {
         this.attributes.putAll(attributes);
      }

      this.setModelNodeType(type);
   }

   private void setModelNodeType(ModelNode.Type type) throws IllegalArgumentException {
      if (type != ModelNode.Type.ASSERTION && type != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(type, ModelNode.Type.ASSERTION, ModelNode.Type.ASSERTION_PARAMETER_NODE)));
      } else {
         this.type = type;
      }
   }

   AssertionData(AssertionData data) {
      this.name = data.name;
      this.value = data.value;
      this.attributes = new HashMap();
      if (!data.attributes.isEmpty()) {
         this.attributes.putAll(data.attributes);
      }

      this.type = data.type;
   }

   protected AssertionData clone() throws CloneNotSupportedException {
      return (AssertionData)super.clone();
   }

   public boolean containsAttribute(QName name) {
      synchronized(this.attributes) {
         return this.attributes.containsKey(name);
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof AssertionData)) {
         return false;
      } else {
         boolean var10000;
         boolean result;
         AssertionData that;
         label46: {
            label45: {
               result = true;
               that = (AssertionData)obj;
               result = result && this.name.equals(that.name);
               if (result) {
                  if (this.value == null) {
                     if (that.value == null) {
                        break label45;
                     }
                  } else if (this.value.equals(that.value)) {
                     break label45;
                  }
               }

               var10000 = false;
               break label46;
            }

            var10000 = true;
         }

         result = var10000;
         synchronized(this.attributes) {
            result = result && this.attributes.equals(that.attributes);
            return result;
         }
      }
   }

   public String getAttributeValue(QName name) {
      synchronized(this.attributes) {
         return (String)this.attributes.get(name);
      }
   }

   public Map<QName, String> getAttributes() {
      synchronized(this.attributes) {
         return new HashMap(this.attributes);
      }
   }

   public Set<Map.Entry<QName, String>> getAttributesSet() {
      synchronized(this.attributes) {
         return new HashSet(this.attributes.entrySet());
      }
   }

   public QName getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public int hashCode() {
      int result = 17;
      int result = 37 * result + this.name.hashCode();
      result = 37 * result + (this.value == null ? 0 : this.value.hashCode());
      synchronized(this.attributes) {
         result = 37 * result + this.attributes.hashCode();
         return result;
      }
   }

   public boolean isPrivateAttributeSet() {
      return "private".equals(this.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE));
   }

   public String removeAttribute(QName name) {
      synchronized(this.attributes) {
         return (String)this.attributes.remove(name);
      }
   }

   public void setAttribute(QName name, String value) {
      synchronized(this.attributes) {
         this.attributes.put(name, value);
      }
   }

   public void setOptionalAttribute(boolean value) {
      this.optional = value;
   }

   public boolean isOptionalAttributeSet() {
      return this.optional;
   }

   public void setIgnorableAttribute(boolean value) {
      this.ignorable = value;
   }

   public boolean isIgnorableAttributeSet() {
      return this.ignorable;
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   public StringBuffer toString(int indentLevel, StringBuffer buffer) {
      String indent = PolicyUtils.Text.createIndent(indentLevel);
      String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
      String innerDoubleIndent = PolicyUtils.Text.createIndent(indentLevel + 2);
      buffer.append(indent);
      if (this.type == ModelNode.Type.ASSERTION) {
         buffer.append("assertion data {");
      } else {
         buffer.append("assertion parameter data {");
      }

      buffer.append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("namespace = '").append(this.name.getNamespaceURI()).append('\'').append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("prefix = '").append(this.name.getPrefix()).append('\'').append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("local name = '").append(this.name.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("value = '").append(this.value).append('\'').append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("optional = '").append(this.optional).append('\'').append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("ignorable = '").append(this.ignorable).append('\'').append(PolicyUtils.Text.NEW_LINE);
      synchronized(this.attributes) {
         if (this.attributes.isEmpty()) {
            buffer.append(innerIndent).append("no attributes");
         } else {
            buffer.append(innerIndent).append("attributes {").append(PolicyUtils.Text.NEW_LINE);
            Iterator var7 = this.attributes.entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry<QName, String> entry = (Map.Entry)var7.next();
               QName aName = (QName)entry.getKey();
               buffer.append(innerDoubleIndent).append("name = '").append(aName.getNamespaceURI()).append(':').append(aName.getLocalPart());
               buffer.append("', value = '").append((String)entry.getValue()).append('\'').append(PolicyUtils.Text.NEW_LINE);
            }

            buffer.append(innerIndent).append('}');
         }
      }

      buffer.append(PolicyUtils.Text.NEW_LINE).append(indent).append('}');
      return buffer;
   }

   public ModelNode.Type getNodeType() {
      return this.type;
   }
}
