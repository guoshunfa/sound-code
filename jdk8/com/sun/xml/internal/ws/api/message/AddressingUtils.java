package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingPropertySet;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.OneWayFeature;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

public class AddressingUtils {
   public static void fillRequestAddressingHeaders(MessageHeaders headers, Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action) {
      fillRequestAddressingHeaders(headers, packet, av, sv, oneway, action, false);
   }

   public static void fillRequestAddressingHeaders(MessageHeaders headers, Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action, boolean mustUnderstand) {
      fillCommonAddressingHeaders(headers, packet, av, sv, action, mustUnderstand);
      if (!oneway) {
         WSEndpointReference epr = av.anonymousEpr;
         if (headers.get(av.replyToTag, false) == null) {
            headers.add(epr.createHeader(av.replyToTag));
         }

         if (headers.get(av.faultToTag, false) == null) {
            headers.add(epr.createHeader(av.faultToTag));
         }

         if (packet.getMessage().getHeaders().get(av.messageIDTag, false) == null && headers.get(av.messageIDTag, false) == null) {
            Header h = new StringHeader(av.messageIDTag, Message.generateMessageID());
            headers.add(h);
         }
      }

   }

   public static void fillRequestAddressingHeaders(MessageHeaders headers, WSDLPort wsdlPort, WSBinding binding, Packet packet) {
      if (binding == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_BINDING());
      } else if (!binding.isFeatureEnabled(SuppressAutomaticWSARequestHeadersFeature.class)) {
         MessageHeaders hl = packet.getMessage().getHeaders();
         String action = getAction(hl, binding.getAddressingVersion(), binding.getSOAPVersion());
         if (action == null) {
            AddressingVersion addressingVersion = binding.getAddressingVersion();
            WsaTubeHelper wsaHelper = addressingVersion.getWsaHelper(wsdlPort, (SEIModel)null, binding);
            String effectiveInputAction = wsaHelper.getEffectiveInputAction(packet);
            if (effectiveInputAction == null || effectiveInputAction.equals("") && binding.getSOAPVersion() == SOAPVersion.SOAP_11) {
               throw new WebServiceException(ClientMessages.INVALID_SOAP_ACTION());
            } else {
               boolean oneway = !packet.expectReply;
               if (wsdlPort != null && !oneway && packet.getMessage() != null && packet.getWSDLOperation() != null) {
                  WSDLBoundOperation wbo = wsdlPort.getBinding().get(packet.getWSDLOperation());
                  if (wbo != null && wbo.getAnonymous() == WSDLBoundOperation.ANONYMOUS.prohibited) {
                     throw new WebServiceException(AddressingMessages.WSAW_ANONYMOUS_PROHIBITED());
                  }
               }

               OneWayFeature oneWayFeature = (OneWayFeature)binding.getFeature(OneWayFeature.class);
               AddressingPropertySet addressingPropertySet = (AddressingPropertySet)packet.getSatellite(AddressingPropertySet.class);
               oneWayFeature = addressingPropertySet == null ? oneWayFeature : new OneWayFeature(addressingPropertySet, addressingVersion);
               if (oneWayFeature != null && oneWayFeature.isEnabled()) {
                  fillRequestAddressingHeaders(headers, packet, addressingVersion, binding.getSOAPVersion(), oneWayFeature, oneway, effectiveInputAction);
               } else {
                  fillRequestAddressingHeaders(headers, packet, addressingVersion, binding.getSOAPVersion(), oneway, effectiveInputAction, AddressingVersion.isRequired(binding));
               }

            }
         }
      }
   }

   public static String getAction(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else {
         String action = null;
         Header h = getFirstHeader(headers, av.actionTag, true, sv);
         if (h != null) {
            action = h.getStringContent();
         }

         return action;
      }
   }

   public static WSEndpointReference getFaultTo(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else {
         Header h = getFirstHeader(headers, av.faultToTag, true, sv);
         WSEndpointReference faultTo = null;
         if (h != null) {
            try {
               faultTo = h.readAsEPR(av);
            } catch (XMLStreamException var6) {
               throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), var6);
            }
         }

         return faultTo;
      }
   }

   public static String getMessageID(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else {
         Header h = getFirstHeader(headers, av.messageIDTag, true, sv);
         String messageId = null;
         if (h != null) {
            messageId = h.getStringContent();
         }

         return messageId;
      }
   }

   public static String getRelatesTo(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else {
         Header h = getFirstHeader(headers, av.relatesToTag, true, sv);
         String relatesTo = null;
         if (h != null) {
            relatesTo = h.getStringContent();
         }

         return relatesTo;
      }
   }

   public static WSEndpointReference getReplyTo(@NotNull MessageHeaders headers, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else {
         Header h = getFirstHeader(headers, av.replyToTag, true, sv);
         WSEndpointReference replyTo;
         if (h != null) {
            try {
               replyTo = h.readAsEPR(av);
            } catch (XMLStreamException var6) {
               throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), var6);
            }
         } else {
            replyTo = av.anonymousEpr;
         }

         return replyTo;
      }
   }

   public static String getTo(MessageHeaders headers, AddressingVersion av, SOAPVersion sv) {
      if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else {
         Header h = getFirstHeader(headers, av.toTag, true, sv);
         String to;
         if (h != null) {
            to = h.getStringContent();
         } else {
            to = av.anonymousUri;
         }

         return to;
      }
   }

   public static Header getFirstHeader(MessageHeaders headers, QName name, boolean markUnderstood, SOAPVersion sv) {
      if (sv == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
      } else {
         Iterator iter = headers.getHeaders(name.getNamespaceURI(), name.getLocalPart(), markUnderstood);

         Header h;
         do {
            if (!iter.hasNext()) {
               return null;
            }

            h = (Header)iter.next();
         } while(!h.getRole(sv).equals(sv.implicitRole));

         return h;
      }
   }

   private static void fillRequestAddressingHeaders(@NotNull MessageHeaders headers, @NotNull Packet packet, @NotNull AddressingVersion av, @NotNull SOAPVersion sv, @NotNull OneWayFeature oneWayFeature, boolean oneway, @NotNull String action) {
      if (!oneway && !oneWayFeature.isUseAsyncWithSyncInvoke() && Boolean.TRUE.equals(packet.isSynchronousMEP)) {
         fillRequestAddressingHeaders(headers, packet, av, sv, oneway, action);
      } else {
         fillCommonAddressingHeaders(headers, packet, av, sv, action, false);
         boolean isMessageIdAdded = false;
         if (headers.get(av.replyToTag, false) == null) {
            WSEndpointReference replyToEpr = oneWayFeature.getReplyTo();
            if (replyToEpr != null) {
               headers.add(replyToEpr.createHeader(av.replyToTag));
               if (packet.getMessage().getHeaders().get(av.messageIDTag, false) == null) {
                  String newID = oneWayFeature.getMessageId() == null ? Message.generateMessageID() : oneWayFeature.getMessageId();
                  headers.add(new StringHeader(av.messageIDTag, newID));
                  isMessageIdAdded = true;
               }
            }
         }

         String messageId = oneWayFeature.getMessageId();
         if (!isMessageIdAdded && messageId != null) {
            headers.add(new StringHeader(av.messageIDTag, messageId));
         }

         if (headers.get(av.faultToTag, false) == null) {
            WSEndpointReference faultToEpr = oneWayFeature.getFaultTo();
            if (faultToEpr != null) {
               headers.add(faultToEpr.createHeader(av.faultToTag));
               if (headers.get(av.messageIDTag, false) == null) {
                  headers.add(new StringHeader(av.messageIDTag, Message.generateMessageID()));
               }
            }
         }

         if (oneWayFeature.getFrom() != null) {
            headers.addOrReplace(oneWayFeature.getFrom().createHeader(av.fromTag));
         }

         if (oneWayFeature.getRelatesToID() != null) {
            headers.addOrReplace(new RelatesToHeader(av.relatesToTag, oneWayFeature.getRelatesToID()));
         }
      }

   }

   private static void fillCommonAddressingHeaders(MessageHeaders headers, Packet packet, @NotNull AddressingVersion av, @NotNull SOAPVersion sv, @NotNull String action, boolean mustUnderstand) {
      if (packet == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_PACKET());
      } else if (av == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
      } else if (sv == null) {
         throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
      } else if (action == null && !sv.httpBindingId.equals("http://www.w3.org/2003/05/soap/bindings/HTTP/")) {
         throw new IllegalArgumentException(AddressingMessages.NULL_ACTION());
      } else {
         StringHeader h;
         if (headers.get(av.toTag, false) == null) {
            h = new StringHeader(av.toTag, packet.endpointAddress.toString());
            headers.add(h);
         }

         if (action != null) {
            packet.soapAction = action;
            if (headers.get(av.actionTag, false) == null) {
               h = new StringHeader(av.actionTag, action, sv, mustUnderstand);
               headers.add(h);
            }
         }

      }
   }
}
