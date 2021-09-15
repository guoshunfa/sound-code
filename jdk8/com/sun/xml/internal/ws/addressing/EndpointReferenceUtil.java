package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EndpointReferenceUtil {
   private static boolean w3cMetadataWritten = false;

   public static <T extends EndpointReference> T transform(Class<T> clazz, @NotNull EndpointReference epr) {
      assert epr != null;

      if (clazz.isAssignableFrom(W3CEndpointReference.class)) {
         if (epr instanceof W3CEndpointReference) {
            return epr;
         }

         if (epr instanceof MemberSubmissionEndpointReference) {
            return toW3CEpr((MemberSubmissionEndpointReference)epr);
         }
      } else if (clazz.isAssignableFrom(MemberSubmissionEndpointReference.class)) {
         if (epr instanceof W3CEndpointReference) {
            return toMSEpr((W3CEndpointReference)epr);
         }

         if (epr instanceof MemberSubmissionEndpointReference) {
            return epr;
         }
      }

      throw new WebServiceException("Unknwon EndpointReference: " + epr.getClass());
   }

   private static W3CEndpointReference toW3CEpr(MemberSubmissionEndpointReference msEpr) {
      StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
      w3cMetadataWritten = false;

      try {
         writer.writeStartDocument();
         writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
         writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
         writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
         writer.writeCharacters(msEpr.addr.uri);
         writer.writeEndElement();
         if (msEpr.referenceProperties != null && msEpr.referenceProperties.elements.size() > 0 || msEpr.referenceParameters != null && msEpr.referenceParameters.elements.size() > 0) {
            writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "ReferenceParameters", AddressingVersion.W3C.nsUri);
            Iterator var2;
            Element e;
            if (msEpr.referenceProperties != null) {
               var2 = msEpr.referenceProperties.elements.iterator();

               while(var2.hasNext()) {
                  e = (Element)var2.next();
                  DOMUtil.serializeNode(e, writer);
               }
            }

            if (msEpr.referenceParameters != null) {
               var2 = msEpr.referenceParameters.elements.iterator();

               while(var2.hasNext()) {
                  e = (Element)var2.next();
                  DOMUtil.serializeNode(e, writer);
               }
            }

            writer.writeEndElement();
         }

         Element wsdlElement = null;
         Element e;
         Iterator var8;
         if (msEpr.elements != null && msEpr.elements.size() > 0) {
            var8 = msEpr.elements.iterator();

            while(var8.hasNext()) {
               e = (Element)var8.next();
               if (e.getNamespaceURI().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI()) && e.getLocalName().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart())) {
                  NodeList nl = e.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
                  if (nl != null) {
                     wsdlElement = (Element)nl.item(0);
                  }
               }
            }
         }

         if (wsdlElement != null) {
            DOMUtil.serializeNode(wsdlElement, writer);
         }

         if (w3cMetadataWritten) {
            writer.writeEndElement();
         }

         if (msEpr.elements != null && msEpr.elements.size() > 0) {
            for(var8 = msEpr.elements.iterator(); var8.hasNext(); DOMUtil.serializeNode(e, writer)) {
               e = (Element)var8.next();
               if (e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && e.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
               }
            }
         }

         writer.writeEndElement();
         writer.writeEndDocument();
         writer.flush();
      } catch (XMLStreamException var6) {
         throw new WebServiceException(var6);
      }

      return new W3CEndpointReference(new XMLStreamBufferSource(writer.getXMLStreamBuffer()));
   }

   private static MemberSubmissionEndpointReference toMSEpr(W3CEndpointReference w3cEpr) {
      DOMResult result = new DOMResult();
      w3cEpr.writeTo(result);
      Node eprNode = result.getNode();
      Element e = DOMUtil.getFirstElementChild(eprNode);
      if (e == null) {
         return null;
      } else {
         MemberSubmissionEndpointReference msEpr = new MemberSubmissionEndpointReference();
         NodeList nodes = e.getChildNodes();

         for(int i = 0; i < nodes.getLength(); ++i) {
            String wsdlLocation;
            if (nodes.item(i).getNodeType() != 1) {
               if (nodes.item(i).getNodeType() == 2) {
                  Node n = nodes.item(i);
                  if (msEpr.attributes == null) {
                     msEpr.attributes = new HashMap();
                     String prefix = fixNull(n.getPrefix());
                     wsdlLocation = fixNull(n.getNamespaceURI());
                     String localName = n.getLocalName();
                     msEpr.attributes.put(new QName(wsdlLocation, localName, prefix), n.getNodeValue());
                  }
               }
            } else {
               Element child = (Element)nodes.item(i);
               if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals(AddressingVersion.W3C.eprType.address)) {
                  if (msEpr.addr == null) {
                     msEpr.addr = new MemberSubmissionEndpointReference.Address();
                  }

                  msEpr.addr.uri = XmlUtil.getTextForNode(child);
               } else {
                  NodeList refParams;
                  if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals("ReferenceParameters")) {
                     refParams = child.getChildNodes();

                     for(int j = 0; j < refParams.getLength(); ++j) {
                        if (refParams.item(j).getNodeType() == 1) {
                           if (msEpr.referenceParameters == null) {
                              msEpr.referenceParameters = new MemberSubmissionEndpointReference.Elements();
                              msEpr.referenceParameters.elements = new ArrayList();
                           }

                           msEpr.referenceParameters.elements.add((Element)refParams.item(j));
                        }
                     }
                  } else if (child.getNamespaceURI().equals(AddressingVersion.W3C.nsUri) && child.getLocalName().equals(AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart())) {
                     refParams = child.getChildNodes();
                     wsdlLocation = child.getAttributeNS("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
                     Element wsdlDefinitions = null;

                     Element elm;
                     String portType;
                     for(int j = 0; j < refParams.getLength(); ++j) {
                        Node node = refParams.item(j);
                        if (node.getNodeType() == 1) {
                           elm = (Element)node;
                           String prefix;
                           String name;
                           String ns;
                           if ((elm.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || elm.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && elm.getLocalName().equals(AddressingVersion.W3C.eprType.serviceName)) {
                              msEpr.serviceName = new MemberSubmissionEndpointReference.ServiceNameType();
                              msEpr.serviceName.portName = elm.getAttribute(AddressingVersion.W3C.eprType.portName);
                              portType = elm.getTextContent();
                              prefix = XmlUtil.getPrefix(portType);
                              name = XmlUtil.getLocalPart(portType);
                              if (name != null) {
                                 if (prefix != null) {
                                    ns = elm.lookupNamespaceURI(prefix);
                                    if (ns != null) {
                                       msEpr.serviceName.name = new QName(ns, name, prefix);
                                    }
                                 } else {
                                    msEpr.serviceName.name = new QName((String)null, name);
                                 }

                                 msEpr.serviceName.attributes = getAttributes(elm);
                              }
                           } else if ((elm.getNamespaceURI().equals(AddressingVersion.W3C.wsdlNsUri) || elm.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata")) && elm.getLocalName().equals(AddressingVersion.W3C.eprType.portTypeName)) {
                              msEpr.portTypeName = new MemberSubmissionEndpointReference.AttributedQName();
                              portType = elm.getTextContent();
                              prefix = XmlUtil.getPrefix(portType);
                              name = XmlUtil.getLocalPart(portType);
                              if (name != null) {
                                 if (prefix != null) {
                                    ns = elm.lookupNamespaceURI(prefix);
                                    if (ns != null) {
                                       msEpr.portTypeName.name = new QName(ns, name, prefix);
                                    }
                                 } else {
                                    msEpr.portTypeName.name = new QName((String)null, name);
                                 }

                                 msEpr.portTypeName.attributes = getAttributes(elm);
                              }
                           } else if (elm.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && elm.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                              wsdlDefinitions = elm;
                           } else {
                              if (msEpr.elements == null) {
                                 msEpr.elements = new ArrayList();
                              }

                              msEpr.elements.add(elm);
                           }
                        }
                     }

                     Document doc = DOMUtil.createDom();
                     Element mexEl = doc.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart());
                     elm = doc.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart());
                     elm.setAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
                     if (wsdlDefinitions == null && wsdlLocation != null && !wsdlLocation.equals("")) {
                        wsdlLocation = wsdlLocation.trim();
                        portType = wsdlLocation.substring(0, wsdlLocation.indexOf(32));
                        wsdlLocation = wsdlLocation.substring(wsdlLocation.indexOf(32) + 1);
                        Element wsdlEl = doc.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
                        Element wsdlImportEl = doc.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_IMPORT.getLocalPart());
                        wsdlImportEl.setAttribute("namespace", portType);
                        wsdlImportEl.setAttribute("location", wsdlLocation);
                        wsdlEl.appendChild(wsdlImportEl);
                        elm.appendChild(wsdlEl);
                     } else if (wsdlDefinitions != null) {
                        elm.appendChild(wsdlDefinitions);
                     }

                     mexEl.appendChild(elm);
                     if (msEpr.elements == null) {
                        msEpr.elements = new ArrayList();
                     }

                     msEpr.elements.add(mexEl);
                  } else {
                     if (msEpr.elements == null) {
                        msEpr.elements = new ArrayList();
                     }

                     msEpr.elements.add(child);
                  }
               }
            }
         }

         return msEpr;
      }
   }

   private static Map<QName, String> getAttributes(Node node) {
      Map<QName, String> attribs = null;
      NamedNodeMap nm = node.getAttributes();

      for(int i = 0; i < nm.getLength(); ++i) {
         if (attribs == null) {
            attribs = new HashMap();
         }

         Node n = nm.item(i);
         String prefix = fixNull(n.getPrefix());
         String ns = fixNull(n.getNamespaceURI());
         String localName = n.getLocalName();
         if (!prefix.equals("xmlns") && (prefix.length() != 0 || !localName.equals("xmlns")) && !localName.equals(AddressingVersion.W3C.eprType.portName)) {
            attribs.put(new QName(ns, localName, prefix), n.getNodeValue());
         }
      }

      return attribs;
   }

   @NotNull
   private static String fixNull(@Nullable String s) {
      return s == null ? "" : s;
   }
}
