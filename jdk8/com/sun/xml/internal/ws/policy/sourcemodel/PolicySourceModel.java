package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.xml.namespace.QName;

public class PolicySourceModel implements Cloneable {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySourceModel.class);
   private static final Map<String, String> DEFAULT_NAMESPACE_TO_PREFIX = new HashMap();
   private final Map<String, String> namespaceToPrefix;
   private ModelNode rootNode;
   private final String policyId;
   private final String policyName;
   private final NamespaceVersion nsVersion;
   private final List<ModelNode> references;
   private boolean expanded;

   public static PolicySourceModel createPolicySourceModel(NamespaceVersion nsVersion) {
      return new PolicySourceModel(nsVersion);
   }

   public static PolicySourceModel createPolicySourceModel(NamespaceVersion nsVersion, String policyId, String policyName) {
      return new PolicySourceModel(nsVersion, policyId, policyName);
   }

   private PolicySourceModel(NamespaceVersion nsVersion) {
      this(nsVersion, (String)null, (String)null);
   }

   private PolicySourceModel(NamespaceVersion nsVersion, String policyId, String policyName) {
      this(nsVersion, policyId, policyName, (Collection)null);
   }

   protected PolicySourceModel(NamespaceVersion nsVersion, String policyId, String policyName, Collection<PrefixMapper> prefixMappers) {
      this.namespaceToPrefix = new HashMap(DEFAULT_NAMESPACE_TO_PREFIX);
      this.references = new LinkedList();
      this.expanded = false;
      this.rootNode = ModelNode.createRootPolicyNode(this);
      this.nsVersion = nsVersion;
      this.policyId = policyId;
      this.policyName = policyName;
      if (prefixMappers != null) {
         Iterator var5 = prefixMappers.iterator();

         while(var5.hasNext()) {
            PrefixMapper prefixMapper = (PrefixMapper)var5.next();
            this.namespaceToPrefix.putAll(prefixMapper.getPrefixMap());
         }
      }

   }

   public ModelNode getRootNode() {
      return this.rootNode;
   }

   public String getPolicyName() {
      return this.policyName;
   }

   public String getPolicyId() {
      return this.policyId;
   }

   public NamespaceVersion getNamespaceVersion() {
      return this.nsVersion;
   }

   Map<String, String> getNamespaceToPrefixMapping() throws PolicyException {
      Map<String, String> nsToPrefixMap = new HashMap();
      Collection<String> namespaces = this.getUsedNamespaces();
      Iterator var3 = namespaces.iterator();

      while(var3.hasNext()) {
         String namespace = (String)var3.next();
         String prefix = this.getDefaultPrefix(namespace);
         if (prefix != null) {
            nsToPrefixMap.put(namespace, prefix);
         }
      }

      return nsToPrefixMap;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof PolicySourceModel)) {
         return false;
      } else {
         boolean var10000;
         boolean result;
         PolicySourceModel that;
         label49: {
            label48: {
               result = true;
               that = (PolicySourceModel)obj;
               if (result) {
                  if (this.policyId == null) {
                     if (that.policyId == null) {
                        break label48;
                     }
                  } else if (this.policyId.equals(that.policyId)) {
                     break label48;
                  }
               }

               var10000 = false;
               break label49;
            }

            var10000 = true;
         }

         label40: {
            label39: {
               result = var10000;
               if (result) {
                  if (this.policyName == null) {
                     if (that.policyName == null) {
                        break label39;
                     }
                  } else if (this.policyName.equals(that.policyName)) {
                     break label39;
                  }
               }

               var10000 = false;
               break label40;
            }

            var10000 = true;
         }

         result = var10000;
         result = result && this.rootNode.equals(that.rootNode);
         return result;
      }
   }

   public int hashCode() {
      int result = 17;
      int result = 37 * result + (this.policyId == null ? 0 : this.policyId.hashCode());
      result = 37 * result + (this.policyName == null ? 0 : this.policyName.hashCode());
      result = 37 * result + this.rootNode.hashCode();
      return result;
   }

   public String toString() {
      String innerIndent = PolicyUtils.Text.createIndent(1);
      StringBuffer buffer = new StringBuffer(60);
      buffer.append("Policy source model {").append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("policy id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
      buffer.append(innerIndent).append("policy name = '").append(this.policyName).append('\'').append(PolicyUtils.Text.NEW_LINE);
      this.rootNode.toString(1, buffer).append(PolicyUtils.Text.NEW_LINE).append('}');
      return buffer.toString();
   }

   protected PolicySourceModel clone() throws CloneNotSupportedException {
      PolicySourceModel clone = (PolicySourceModel)super.clone();
      clone.rootNode = this.rootNode.clone();

      try {
         clone.rootNode.setParentModel(clone);
         return clone;
      } catch (IllegalAccessException var3) {
         throw (CloneNotSupportedException)LOGGER.logSevereException(new CloneNotSupportedException(LocalizationMessages.WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT()), var3);
      }
   }

   public boolean containsPolicyReferences() {
      return !this.references.isEmpty();
   }

   private boolean isExpanded() {
      return this.references.isEmpty() || this.expanded;
   }

   public synchronized void expand(PolicySourceModelContext context) throws PolicyException {
      if (!this.isExpanded()) {
         ModelNode reference;
         PolicySourceModel referencedModel;
         for(Iterator var2 = this.references.iterator(); var2.hasNext(); reference.setReferencedModel(referencedModel)) {
            reference = (ModelNode)var2.next();
            PolicyReferenceData refData = reference.getPolicyReferenceData();
            String digest = refData.getDigest();
            if (digest == null) {
               referencedModel = context.retrieveModel(refData.getReferencedModelUri());
            } else {
               referencedModel = context.retrieveModel(refData.getReferencedModelUri(), refData.getDigestAlgorithmUri(), digest);
            }
         }

         this.expanded = true;
      }

   }

   void addNewPolicyReference(ModelNode node) {
      if (node.getType() != ModelNode.Type.POLICY_REFERENCE) {
         throw new IllegalArgumentException(LocalizationMessages.WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(node.getType()));
      } else {
         this.references.add(node);
      }
   }

   private Collection<String> getUsedNamespaces() throws PolicyException {
      Set<String> namespaces = new HashSet();
      namespaces.add(this.getNamespaceVersion().toString());
      if (this.policyId != null) {
         namespaces.add("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
      }

      Queue<ModelNode> nodesToBeProcessed = new LinkedList();
      nodesToBeProcessed.add(this.rootNode);

      ModelNode processedNode;
      label45:
      while((processedNode = (ModelNode)nodesToBeProcessed.poll()) != null) {
         Iterator var4 = processedNode.getChildren().iterator();

         while(true) {
            ModelNode child;
            do {
               if (!var4.hasNext()) {
                  continue label45;
               }

               child = (ModelNode)var4.next();
               if (child.hasChildren() && !nodesToBeProcessed.offer(child)) {
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0081_UNABLE_TO_INSERT_CHILD(nodesToBeProcessed, child)));
               }
            } while(!child.isDomainSpecific());

            AssertionData nodeData = child.getNodeData();
            namespaces.add(nodeData.getName().getNamespaceURI());
            if (nodeData.isPrivateAttributeSet()) {
               namespaces.add("http://java.sun.com/xml/ns/wsit/policy");
            }

            Iterator var7 = nodeData.getAttributesSet().iterator();

            while(var7.hasNext()) {
               Map.Entry<QName, String> attribute = (Map.Entry)var7.next();
               namespaces.add(((QName)attribute.getKey()).getNamespaceURI());
            }
         }
      }

      return namespaces;
   }

   private String getDefaultPrefix(String namespace) {
      return (String)this.namespaceToPrefix.get(namespace);
   }

   static {
      PrefixMapper[] prefixMappers = (PrefixMapper[])PolicyUtils.ServiceProvider.load(PrefixMapper.class);
      int var2;
      int var3;
      if (prefixMappers != null) {
         PrefixMapper[] var1 = prefixMappers;
         var2 = prefixMappers.length;

         for(var3 = 0; var3 < var2; ++var3) {
            PrefixMapper mapper = var1[var3];
            DEFAULT_NAMESPACE_TO_PREFIX.putAll(mapper.getPrefixMap());
         }
      }

      NamespaceVersion[] var5 = NamespaceVersion.values();
      var2 = var5.length;

      for(var3 = 0; var3 < var2; ++var3) {
         NamespaceVersion version = var5[var3];
         DEFAULT_NAMESPACE_TO_PREFIX.put(version.toString(), version.getDefaultNamespacePrefix());
      }

      DEFAULT_NAMESPACE_TO_PREFIX.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
      DEFAULT_NAMESPACE_TO_PREFIX.put("http://java.sun.com/xml/ns/wsit/policy", "sunwsp");
   }
}
