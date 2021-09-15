package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.StaxSerializer;
import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

public final class XmlPolicyModelMarshaller extends PolicyModelMarshaller {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelMarshaller.class);
   private final boolean marshallInvisible;

   XmlPolicyModelMarshaller(boolean marshallInvisible) {
      this.marshallInvisible = marshallInvisible;
   }

   public void marshal(PolicySourceModel model, Object storage) throws PolicyException {
      if (storage instanceof StaxSerializer) {
         this.marshal(model, (StaxSerializer)storage);
      } else if (storage instanceof TypedXmlWriter) {
         this.marshal(model, (TypedXmlWriter)storage);
      } else {
         if (!(storage instanceof XMLStreamWriter)) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(storage.getClass().getName())));
         }

         this.marshal(model, (XMLStreamWriter)storage);
      }

   }

   public void marshal(Collection<PolicySourceModel> models, Object storage) throws PolicyException {
      Iterator var3 = models.iterator();

      while(var3.hasNext()) {
         PolicySourceModel model = (PolicySourceModel)var3.next();
         this.marshal(model, storage);
      }

   }

   private void marshal(PolicySourceModel model, StaxSerializer writer) throws PolicyException {
      TypedXmlWriter policy = TXW.create(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, writer);
      this.marshalDefaultPrefixes(model, policy);
      marshalPolicyAttributes(model, policy);
      this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
      policy.commit();
   }

   private void marshal(PolicySourceModel model, TypedXmlWriter writer) throws PolicyException {
      TypedXmlWriter policy = writer._element(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class);
      this.marshalDefaultPrefixes(model, policy);
      marshalPolicyAttributes(model, policy);
      this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
   }

   private void marshal(PolicySourceModel model, XMLStreamWriter writer) throws PolicyException {
      StaxSerializer serializer = new StaxSerializer(writer);
      TypedXmlWriter policy = TXW.create(model.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, serializer);
      this.marshalDefaultPrefixes(model, policy);
      marshalPolicyAttributes(model, policy);
      this.marshal(model.getNamespaceVersion(), model.getRootNode(), policy);
      policy.commit();
      serializer.flush();
   }

   private static void marshalPolicyAttributes(PolicySourceModel model, TypedXmlWriter writer) {
      String policyId = model.getPolicyId();
      if (policyId != null) {
         writer._attribute((QName)PolicyConstants.WSU_ID, policyId);
      }

      String policyName = model.getPolicyName();
      if (policyName != null) {
         writer._attribute((QName)model.getNamespaceVersion().asQName(XmlToken.Name), policyName);
      }

   }

   private void marshal(NamespaceVersion nsVersion, ModelNode rootNode, TypedXmlWriter writer) {
      Iterator var4 = rootNode.iterator();

      while(true) {
         ModelNode node;
         AssertionData data;
         do {
            if (!var4.hasNext()) {
               return;
            }

            node = (ModelNode)var4.next();
            data = node.getNodeData();
         } while(!this.marshallInvisible && data != null && data.isPrivateAttributeSet());

         TypedXmlWriter child = null;
         if (data == null) {
            child = writer._element(nsVersion.asQName(node.getType().getXmlToken()), TypedXmlWriter.class);
         } else {
            child = writer._element(data.getName(), TypedXmlWriter.class);
            String value = data.getValue();
            if (value != null) {
               child._pcdata(value);
            }

            if (data.isOptionalAttributeSet()) {
               child._attribute((QName)nsVersion.asQName(XmlToken.Optional), Boolean.TRUE);
            }

            if (data.isIgnorableAttributeSet()) {
               child._attribute((QName)nsVersion.asQName(XmlToken.Ignorable), Boolean.TRUE);
            }

            Iterator var9 = data.getAttributesSet().iterator();

            while(var9.hasNext()) {
               Map.Entry<QName, String> entry = (Map.Entry)var9.next();
               child._attribute((QName)entry.getKey(), entry.getValue());
            }
         }

         this.marshal(nsVersion, node, child);
      }
   }

   private void marshalDefaultPrefixes(PolicySourceModel model, TypedXmlWriter writer) throws PolicyException {
      Map<String, String> nsMap = model.getNamespaceToPrefixMapping();
      if (!this.marshallInvisible && nsMap.containsKey("http://java.sun.com/xml/ns/wsit/policy")) {
         nsMap.remove("http://java.sun.com/xml/ns/wsit/policy");
      }

      Iterator var4 = nsMap.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<String, String> nsMappingEntry = (Map.Entry)var4.next();
         writer._namespace((String)nsMappingEntry.getKey(), (String)nsMappingEntry.getValue());
      }

   }
}
