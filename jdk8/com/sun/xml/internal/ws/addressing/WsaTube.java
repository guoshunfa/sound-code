package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.SOAPBinding;

abstract class WsaTube extends AbstractFilterTubeImpl {
   @NotNull
   protected final WSDLPort wsdlPort;
   protected final WSBinding binding;
   final WsaTubeHelper helper;
   @NotNull
   protected final AddressingVersion addressingVersion;
   protected final SOAPVersion soapVersion;
   private final boolean addressingRequired;
   private static final Logger LOGGER = Logger.getLogger(WsaTube.class.getName());

   public WsaTube(WSDLPort wsdlPort, WSBinding binding, Tube next) {
      super(next);
      this.wsdlPort = wsdlPort;
      this.binding = binding;
      this.addKnownHeadersToBinding(binding);
      this.addressingVersion = binding.getAddressingVersion();
      this.soapVersion = binding.getSOAPVersion();
      this.helper = this.getTubeHelper();
      this.addressingRequired = AddressingVersion.isRequired(binding);
   }

   public WsaTube(WsaTube that, TubeCloner cloner) {
      super(that, cloner);
      this.wsdlPort = that.wsdlPort;
      this.binding = that.binding;
      this.helper = that.helper;
      this.addressingVersion = that.addressingVersion;
      this.soapVersion = that.soapVersion;
      this.addressingRequired = that.addressingRequired;
   }

   private void addKnownHeadersToBinding(WSBinding binding) {
      AddressingVersion[] var2 = AddressingVersion.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AddressingVersion addrVersion = var2[var4];
         binding.addKnownHeader(addrVersion.actionTag);
         binding.addKnownHeader(addrVersion.faultDetailTag);
         binding.addKnownHeader(addrVersion.faultToTag);
         binding.addKnownHeader(addrVersion.fromTag);
         binding.addKnownHeader(addrVersion.messageIDTag);
         binding.addKnownHeader(addrVersion.relatesToTag);
         binding.addKnownHeader(addrVersion.replyToTag);
         binding.addKnownHeader(addrVersion.toTag);
      }

   }

   @NotNull
   public NextAction processException(Throwable t) {
      return super.processException(t);
   }

   protected WsaTubeHelper getTubeHelper() {
      if (this.binding.isFeatureEnabled(AddressingFeature.class)) {
         return new WsaTubeHelperImpl(this.wsdlPort, (SEIModel)null, this.binding);
      } else if (this.binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) {
         return new com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl(this.wsdlPort, (SEIModel)null, this.binding);
      } else {
         throw new WebServiceException(AddressingMessages.ADDRESSING_NOT_ENABLED(this.getClass().getSimpleName()));
      }
   }

   protected Packet validateInboundHeaders(Packet packet) {
      SOAPFault soapFault;
      FaultDetailHeader s11FaultDetailHeader;
      try {
         this.checkMessageAddressingProperties(packet);
         return packet;
      } catch (InvalidAddressingHeaderException var5) {
         LOGGER.log(Level.WARNING, (String)(this.addressingVersion.getInvalidMapText() + ", Problem header:" + var5.getProblemHeader() + ", Reason: " + var5.getSubsubcode()), (Throwable)var5);
         soapFault = this.helper.createInvalidAddressingHeaderFault(var5, this.addressingVersion);
         s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), var5.getProblemHeader());
      } catch (MissingAddressingHeaderException var6) {
         LOGGER.log(Level.WARNING, (String)(this.addressingVersion.getMapRequiredText() + ", Problem header:" + var6.getMissingHeaderQName()), (Throwable)var6);
         soapFault = this.helper.newMapRequiredFault(var6);
         s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), var6.getMissingHeaderQName());
      }

      if (soapFault != null) {
         if (this.wsdlPort != null && packet.getMessage().isOneWay(this.wsdlPort)) {
            return packet.createServerResponse((Message)null, (WSDLPort)this.wsdlPort, (SEIModel)null, (WSBinding)this.binding);
         } else {
            Message m = Messages.create(soapFault);
            if (this.soapVersion == SOAPVersion.SOAP_11) {
               m.getHeaders().add(s11FaultDetailHeader);
            }

            return packet.createServerResponse(m, (WSDLPort)this.wsdlPort, (SEIModel)null, (WSBinding)this.binding);
         }
      } else {
         return packet;
      }
   }

   protected void checkMessageAddressingProperties(Packet packet) {
      this.checkCardinality(packet);
   }

   final boolean isAddressingEngagedOrRequired(Packet packet, WSBinding binding) {
      if (AddressingVersion.isRequired(binding)) {
         return true;
      } else if (packet == null) {
         return false;
      } else if (packet.getMessage() == null) {
         return false;
      } else if (packet.getMessage().getHeaders() != null) {
         return false;
      } else {
         String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
         return action == null ? true : true;
      }
   }

   protected void checkCardinality(Packet packet) {
      Message message = packet.getMessage();
      if (message == null) {
         if (this.addressingRequired) {
            throw new WebServiceException(AddressingMessages.NULL_MESSAGE());
         }
      } else {
         Iterator<Header> hIter = message.getHeaders().getHeaders(this.addressingVersion.nsUri, true);
         if (!hIter.hasNext()) {
            if (this.addressingRequired) {
               throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, packet);
            }
         } else {
            boolean foundFrom = false;
            boolean foundTo = false;
            boolean foundReplyTo = false;
            boolean foundFaultTo = false;
            boolean foundAction = false;
            boolean foundMessageId = false;
            boolean foundRelatesTo = false;
            QName duplicateHeader = null;

            while(hIter.hasNext()) {
               Header h = (Header)hIter.next();
               if (this.isInCurrentRole(h, this.binding)) {
                  String local = h.getLocalPart();
                  if (local.equals(this.addressingVersion.fromTag.getLocalPart())) {
                     if (foundFrom) {
                        duplicateHeader = this.addressingVersion.fromTag;
                        break;
                     }

                     foundFrom = true;
                  } else if (local.equals(this.addressingVersion.toTag.getLocalPart())) {
                     if (foundTo) {
                        duplicateHeader = this.addressingVersion.toTag;
                        break;
                     }

                     foundTo = true;
                  } else if (local.equals(this.addressingVersion.replyToTag.getLocalPart())) {
                     if (foundReplyTo) {
                        duplicateHeader = this.addressingVersion.replyToTag;
                        break;
                     }

                     foundReplyTo = true;

                     try {
                        h.readAsEPR(this.addressingVersion);
                     } catch (XMLStreamException var16) {
                        throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), var16);
                     }
                  } else if (local.equals(this.addressingVersion.faultToTag.getLocalPart())) {
                     if (foundFaultTo) {
                        duplicateHeader = this.addressingVersion.faultToTag;
                        break;
                     }

                     foundFaultTo = true;

                     try {
                        h.readAsEPR(this.addressingVersion);
                     } catch (XMLStreamException var15) {
                        throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), var15);
                     }
                  } else if (local.equals(this.addressingVersion.actionTag.getLocalPart())) {
                     if (foundAction) {
                        duplicateHeader = this.addressingVersion.actionTag;
                        break;
                     }

                     foundAction = true;
                  } else if (local.equals(this.addressingVersion.messageIDTag.getLocalPart())) {
                     if (foundMessageId) {
                        duplicateHeader = this.addressingVersion.messageIDTag;
                        break;
                     }

                     foundMessageId = true;
                  } else if (local.equals(this.addressingVersion.relatesToTag.getLocalPart())) {
                     foundRelatesTo = true;
                  } else if (!local.equals(this.addressingVersion.faultDetailTag.getLocalPart())) {
                     System.err.println(AddressingMessages.UNKNOWN_WSA_HEADER());
                  }
               }
            }

            if (duplicateHeader != null) {
               throw new InvalidAddressingHeaderException(duplicateHeader, this.addressingVersion.invalidCardinalityTag);
            } else {
               if (foundAction || this.addressingRequired) {
                  this.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
               }

            }
         }
      }
   }

   final boolean isInCurrentRole(Header header, WSBinding binding) {
      return binding == null ? true : ((SOAPBinding)binding).getRoles().contains(header.getRole(this.soapVersion));
   }

   protected final WSDLBoundOperation getWSDLBoundOperation(Packet packet) {
      if (this.wsdlPort == null) {
         return null;
      } else {
         QName opName = packet.getWSDLOperation();
         return opName != null ? this.wsdlPort.getBinding().get(opName) : null;
      }
   }

   protected void validateSOAPAction(Packet packet) {
      String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
      if (gotA == null) {
         throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
      } else if (packet.soapAction != null && !packet.soapAction.equals("\"\"") && !packet.soapAction.equals("\"" + gotA + "\"")) {
         throw new InvalidAddressingHeaderException(this.addressingVersion.actionTag, this.addressingVersion.actionMismatchTag);
      }
   }

   protected abstract void validateAction(Packet var1);

   protected void checkMandatoryHeaders(Packet packet, boolean foundAction, boolean foundTo, boolean foundReplyTo, boolean foundFaultTo, boolean foundMessageId, boolean foundRelatesTo) {
      if (!foundAction) {
         throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, packet);
      } else {
         this.validateSOAPAction(packet);
      }
   }
}
