package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class SafePolicyReader {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(SafePolicyReader.class);
   private final Set<String> urlsRead = new HashSet();
   private final Set<String> qualifiedPolicyUris = new HashSet();

   public SafePolicyReader.PolicyRecord readPolicyElement(XMLStreamReader reader, String baseUrl) {
      if (null != reader && reader.isStartElement()) {
         StringBuffer elementCode = new StringBuffer();
         SafePolicyReader.PolicyRecord policyRec = new SafePolicyReader.PolicyRecord();
         QName elementName = reader.getName();
         int depth = 0;

         try {
            do {
               QName curName;
               switch(reader.getEventType()) {
               case 1:
                  curName = reader.getName();
                  boolean insidePolicyReferenceAttr = NamespaceVersion.resolveAsToken(curName) == XmlToken.PolicyReference;
                  if (elementName.equals(curName)) {
                     ++depth;
                  }

                  StringBuffer xmlnsCode = new StringBuffer();
                  Set<String> tmpNsSet = new HashSet();
                  if (null != curName.getPrefix() && !"".equals(curName.getPrefix())) {
                     elementCode.append('<').append(curName.getPrefix()).append(':').append(curName.getLocalPart());
                     xmlnsCode.append(" xmlns:").append(curName.getPrefix()).append("=\"").append(curName.getNamespaceURI()).append('"');
                     tmpNsSet.add(curName.getPrefix());
                  } else {
                     elementCode.append('<').append(curName.getLocalPart());
                     xmlnsCode.append(" xmlns=\"").append(curName.getNamespaceURI()).append('"');
                  }

                  int attrCount = reader.getAttributeCount();
                  StringBuffer attrCode = new StringBuffer();

                  for(int i = 0; i < attrCount; ++i) {
                     boolean uriAttrFlg = false;
                     if (insidePolicyReferenceAttr && "URI".equals(reader.getAttributeName(i).getLocalPart())) {
                        uriAttrFlg = true;
                        if (null == policyRec.unresolvedURIs) {
                           policyRec.unresolvedURIs = new HashSet();
                        }

                        policyRec.unresolvedURIs.add(relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl));
                     }

                     if (!"xmlns".equals(reader.getAttributePrefix(i)) || !tmpNsSet.contains(reader.getAttributeLocalName(i))) {
                        if (null != reader.getAttributePrefix(i) && !"".equals(reader.getAttributePrefix(i))) {
                           attrCode.append(' ').append(reader.getAttributePrefix(i)).append(':').append(reader.getAttributeLocalName(i)).append("=\"").append(uriAttrFlg ? relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl) : reader.getAttributeValue(i)).append('"');
                           if (!tmpNsSet.contains(reader.getAttributePrefix(i))) {
                              xmlnsCode.append(" xmlns:").append(reader.getAttributePrefix(i)).append("=\"").append(reader.getAttributeNamespace(i)).append('"');
                              tmpNsSet.add(reader.getAttributePrefix(i));
                           }
                        } else {
                           attrCode.append(' ').append(reader.getAttributeLocalName(i)).append("=\"").append(uriAttrFlg ? relativeToAbsoluteUrl(reader.getAttributeValue(i), baseUrl) : reader.getAttributeValue(i)).append('"');
                        }
                     }
                  }

                  elementCode.append(xmlnsCode).append(attrCode).append('>');
                  break;
               case 2:
                  curName = reader.getName();
                  if (elementName.equals(curName)) {
                     --depth;
                  }

                  elementCode.append("</").append("".equals(curName.getPrefix()) ? "" : curName.getPrefix() + ':').append(curName.getLocalPart()).append('>');
               case 3:
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               default:
                  break;
               case 4:
                  elementCode.append(reader.getText());
                  break;
               case 12:
                  elementCode.append("<![CDATA[").append(reader.getText()).append("]]>");
               }

               if (reader.hasNext() && depth > 0) {
                  reader.next();
               }
            } while(8 != reader.getEventType() && depth > 0);

            policyRec.policyModel = ModelUnmarshaller.getUnmarshaller().unmarshalModel(new StringReader(elementCode.toString()));
            if (null != policyRec.policyModel.getPolicyId()) {
               policyRec.setUri(baseUrl + "#" + policyRec.policyModel.getPolicyId(), policyRec.policyModel.getPolicyId());
            } else if (policyRec.policyModel.getPolicyName() != null) {
               policyRec.setUri(policyRec.policyModel.getPolicyName(), policyRec.policyModel.getPolicyName());
            }
         } catch (Exception var15) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(elementCode.toString()), var15));
         }

         this.urlsRead.add(baseUrl);
         return policyRec;
      } else {
         return null;
      }
   }

   public Set<String> getUrlsRead() {
      return this.urlsRead;
   }

   public String readPolicyReferenceElement(XMLStreamReader reader) {
      try {
         if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.PolicyReference) {
            for(int i = 0; i < reader.getAttributeCount(); ++i) {
               if (XmlToken.resolveToken(reader.getAttributeName(i).getLocalPart()) == XmlToken.Uri) {
                  String uriValue = reader.getAttributeValue(i);
                  reader.next();
                  return uriValue;
               }
            }
         }

         reader.next();
         return null;
      } catch (XMLStreamException var4) {
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE(), var4));
      }
   }

   public static String relativeToAbsoluteUrl(String relativeUri, String baseUri) {
      if ('#' != relativeUri.charAt(0)) {
         return relativeUri;
      } else {
         return null == baseUri ? relativeUri : baseUri + relativeUri;
      }
   }

   public final class PolicyRecord {
      SafePolicyReader.PolicyRecord next;
      PolicySourceModel policyModel;
      Set<String> unresolvedURIs;
      private String uri;

      PolicyRecord() {
      }

      SafePolicyReader.PolicyRecord insert(SafePolicyReader.PolicyRecord insertedRec) {
         if (null != insertedRec.unresolvedURIs && !insertedRec.unresolvedURIs.isEmpty()) {
            SafePolicyReader.PolicyRecord oneBeforeCurrent = null;

            SafePolicyReader.PolicyRecord current;
            for(current = this; null != current.next; current = current.next) {
               if (null != current.unresolvedURIs && current.unresolvedURIs.contains(insertedRec.uri)) {
                  if (null == oneBeforeCurrent) {
                     insertedRec.next = current;
                     return insertedRec;
                  }

                  oneBeforeCurrent.next = insertedRec;
                  insertedRec.next = current;
                  return this;
               }

               if (insertedRec.unresolvedURIs.remove(current.uri) && insertedRec.unresolvedURIs.isEmpty()) {
                  insertedRec.next = current.next;
                  current.next = insertedRec;
                  return this;
               }

               oneBeforeCurrent = current;
            }

            insertedRec.next = null;
            current.next = insertedRec;
            return this;
         } else {
            insertedRec.next = this;
            return insertedRec;
         }
      }

      public void setUri(String uri, String id) throws PolicyException {
         if (SafePolicyReader.this.qualifiedPolicyUris.contains(uri)) {
            throw (PolicyException)SafePolicyReader.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1020_DUPLICATE_ID(id)));
         } else {
            this.uri = uri;
            SafePolicyReader.this.qualifiedPolicyUris.add(uri);
         }
      }

      public String getUri() {
         return this.uri;
      }

      public String toString() {
         String result = this.uri;
         if (null != this.next) {
            result = result + "->" + this.next.toString();
         }

         return result;
      }
   }
}
