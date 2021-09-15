package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public final class ModelNode implements Iterable<ModelNode>, Cloneable {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelNode.class);
   private LinkedList<ModelNode> children;
   private Collection<ModelNode> unmodifiableViewOnContent;
   private final ModelNode.Type type;
   private ModelNode parentNode;
   private PolicySourceModel parentModel;
   private PolicyReferenceData referenceData;
   private PolicySourceModel referencedModel;
   private AssertionData nodeData;

   static ModelNode createRootPolicyNode(PolicySourceModel model) throws IllegalArgumentException {
      if (model == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL()));
      } else {
         return new ModelNode(ModelNode.Type.POLICY, model);
      }
   }

   private ModelNode(ModelNode.Type type, PolicySourceModel parentModel) {
      this.type = type;
      this.parentModel = parentModel;
      this.children = new LinkedList();
      this.unmodifiableViewOnContent = Collections.unmodifiableCollection(this.children);
   }

   private ModelNode(ModelNode.Type type, PolicySourceModel parentModel, AssertionData data) {
      this(type, parentModel);
      this.nodeData = data;
   }

   private ModelNode(PolicySourceModel parentModel, PolicyReferenceData data) {
      this(ModelNode.Type.POLICY_REFERENCE, parentModel);
      this.referenceData = data;
   }

   private void checkCreateChildOperationSupportForType(ModelNode.Type type) throws UnsupportedOperationException {
      if (!this.type.isChildTypeSupported(type)) {
         throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(type, this.type)));
      }
   }

   public ModelNode createChildPolicyNode() {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.POLICY);
      ModelNode node = new ModelNode(ModelNode.Type.POLICY, this.parentModel);
      this.addChild(node);
      return node;
   }

   public ModelNode createChildAllNode() {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.ALL);
      ModelNode node = new ModelNode(ModelNode.Type.ALL, this.parentModel);
      this.addChild(node);
      return node;
   }

   public ModelNode createChildExactlyOneNode() {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.EXACTLY_ONE);
      ModelNode node = new ModelNode(ModelNode.Type.EXACTLY_ONE, this.parentModel);
      this.addChild(node);
      return node;
   }

   public ModelNode createChildAssertionNode() {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.ASSERTION);
      ModelNode node = new ModelNode(ModelNode.Type.ASSERTION, this.parentModel);
      this.addChild(node);
      return node;
   }

   public ModelNode createChildAssertionNode(AssertionData nodeData) {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.ASSERTION);
      ModelNode node = new ModelNode(ModelNode.Type.ASSERTION, this.parentModel, nodeData);
      this.addChild(node);
      return node;
   }

   public ModelNode createChildAssertionParameterNode() {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.ASSERTION_PARAMETER_NODE);
      ModelNode node = new ModelNode(ModelNode.Type.ASSERTION_PARAMETER_NODE, this.parentModel);
      this.addChild(node);
      return node;
   }

   ModelNode createChildAssertionParameterNode(AssertionData nodeData) {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.ASSERTION_PARAMETER_NODE);
      ModelNode node = new ModelNode(ModelNode.Type.ASSERTION_PARAMETER_NODE, this.parentModel, nodeData);
      this.addChild(node);
      return node;
   }

   ModelNode createChildPolicyReferenceNode(PolicyReferenceData referenceData) {
      this.checkCreateChildOperationSupportForType(ModelNode.Type.POLICY_REFERENCE);
      ModelNode node = new ModelNode(this.parentModel, referenceData);
      this.parentModel.addNewPolicyReference(node);
      this.addChild(node);
      return node;
   }

   Collection<ModelNode> getChildren() {
      return this.unmodifiableViewOnContent;
   }

   void setParentModel(PolicySourceModel model) throws IllegalAccessException {
      if (this.parentNode != null) {
         throw (IllegalAccessException)LOGGER.logSevereException(new IllegalAccessException(LocalizationMessages.WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED()));
      } else {
         this.updateParentModelReference(model);
      }
   }

   private void updateParentModelReference(PolicySourceModel model) {
      this.parentModel = model;
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         ModelNode child = (ModelNode)var2.next();
         child.updateParentModelReference(model);
      }

   }

   public PolicySourceModel getParentModel() {
      return this.parentModel;
   }

   public ModelNode.Type getType() {
      return this.type;
   }

   public ModelNode getParentNode() {
      return this.parentNode;
   }

   public AssertionData getNodeData() {
      return this.nodeData;
   }

   PolicyReferenceData getPolicyReferenceData() {
      return this.referenceData;
   }

   public AssertionData setOrReplaceNodeData(AssertionData newData) {
      if (!this.isDomainSpecific()) {
         throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(this.type)));
      } else {
         AssertionData oldData = this.nodeData;
         this.nodeData = newData;
         return oldData;
      }
   }

   boolean isDomainSpecific() {
      return this.type == ModelNode.Type.ASSERTION || this.type == ModelNode.Type.ASSERTION_PARAMETER_NODE;
   }

   private boolean addChild(ModelNode child) {
      this.children.add(child);
      child.parentNode = this;
      return true;
   }

   void setReferencedModel(PolicySourceModel model) {
      if (this.type != ModelNode.Type.POLICY_REFERENCE) {
         throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(this.type)));
      } else {
         this.referencedModel = model;
      }
   }

   PolicySourceModel getReferencedModel() {
      return this.referencedModel;
   }

   public int childrenSize() {
      return this.children.size();
   }

   public boolean hasChildren() {
      return !this.children.isEmpty();
   }

   public Iterator<ModelNode> iterator() {
      return this.children.iterator();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ModelNode)) {
         return false;
      } else {
         boolean var10000;
         boolean result;
         ModelNode that;
         label44: {
            label43: {
               result = true;
               that = (ModelNode)obj;
               result = result && this.type.equals(that.type);
               if (result) {
                  if (this.nodeData == null) {
                     if (that.nodeData == null) {
                        break label43;
                     }
                  } else if (this.nodeData.equals(that.nodeData)) {
                     break label43;
                  }
               }

               var10000 = false;
               break label44;
            }

            var10000 = true;
         }

         label35: {
            label34: {
               result = var10000;
               if (result) {
                  if (this.children == null) {
                     if (that.children == null) {
                        break label34;
                     }
                  } else if (this.children.equals(that.children)) {
                     break label34;
                  }
               }

               var10000 = false;
               break label35;
            }

            var10000 = true;
         }

         result = var10000;
         return result;
      }
   }

   public int hashCode() {
      int result = 17;
      int result = 37 * result + this.type.hashCode();
      result = 37 * result + (this.parentNode == null ? 0 : this.parentNode.hashCode());
      result = 37 * result + (this.nodeData == null ? 0 : this.nodeData.hashCode());
      result = 37 * result + this.children.hashCode();
      return result;
   }

   public String toString() {
      return this.toString(0, new StringBuffer()).toString();
   }

   public StringBuffer toString(int indentLevel, StringBuffer buffer) {
      String indent = PolicyUtils.Text.createIndent(indentLevel);
      String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
      buffer.append(indent).append((Object)this.type).append(" {").append(PolicyUtils.Text.NEW_LINE);
      if (this.type == ModelNode.Type.ASSERTION) {
         if (this.nodeData == null) {
            buffer.append(innerIndent).append("no assertion data set");
         } else {
            this.nodeData.toString(indentLevel + 1, buffer);
         }

         buffer.append(PolicyUtils.Text.NEW_LINE);
      } else if (this.type == ModelNode.Type.POLICY_REFERENCE) {
         if (this.referenceData == null) {
            buffer.append(innerIndent).append("no policy reference data set");
         } else {
            this.referenceData.toString(indentLevel + 1, buffer);
         }

         buffer.append(PolicyUtils.Text.NEW_LINE);
      } else if (this.type == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
         if (this.nodeData == null) {
            buffer.append(innerIndent).append("no parameter data set");
         } else {
            this.nodeData.toString(indentLevel + 1, buffer);
         }

         buffer.append(PolicyUtils.Text.NEW_LINE);
      }

      if (this.children.size() > 0) {
         Iterator var5 = this.children.iterator();

         while(var5.hasNext()) {
            ModelNode child = (ModelNode)var5.next();
            child.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
         }
      } else {
         buffer.append(innerIndent).append("no child nodes").append(PolicyUtils.Text.NEW_LINE);
      }

      buffer.append(indent).append('}');
      return buffer;
   }

   protected ModelNode clone() throws CloneNotSupportedException {
      ModelNode clone = (ModelNode)super.clone();
      if (this.nodeData != null) {
         clone.nodeData = this.nodeData.clone();
      }

      if (this.referencedModel != null) {
         clone.referencedModel = this.referencedModel.clone();
      }

      clone.children = new LinkedList();
      clone.unmodifiableViewOnContent = Collections.unmodifiableCollection(clone.children);
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         ModelNode thisChild = (ModelNode)var2.next();
         clone.addChild(thisChild.clone());
      }

      return clone;
   }

   PolicyReferenceData getReferenceData() {
      return this.referenceData;
   }

   public static enum Type {
      POLICY(XmlToken.Policy),
      ALL(XmlToken.All),
      EXACTLY_ONE(XmlToken.ExactlyOne),
      POLICY_REFERENCE(XmlToken.PolicyReference),
      ASSERTION(XmlToken.UNKNOWN),
      ASSERTION_PARAMETER_NODE(XmlToken.UNKNOWN);

      private XmlToken token;

      private Type(XmlToken token) {
         this.token = token;
      }

      public XmlToken getXmlToken() {
         return this.token;
      }

      private boolean isChildTypeSupported(ModelNode.Type childType) {
         switch(this) {
         case ASSERTION_PARAMETER_NODE:
            switch(childType) {
            case ASSERTION_PARAMETER_NODE:
               return true;
            default:
               return false;
            }
         case POLICY:
         case ALL:
         case EXACTLY_ONE:
            switch(childType) {
            case ASSERTION_PARAMETER_NODE:
               return false;
            default:
               return true;
            }
         case POLICY_REFERENCE:
            return false;
         case ASSERTION:
            switch(childType) {
            case ASSERTION_PARAMETER_NODE:
            case POLICY:
            case POLICY_REFERENCE:
               return true;
            default:
               return false;
            }
         default:
            throw (IllegalStateException)ModelNode.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(this)));
         }
      }
   }
}
