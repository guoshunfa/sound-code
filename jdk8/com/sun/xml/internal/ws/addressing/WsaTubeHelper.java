package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

public abstract class WsaTubeHelper {
   protected SEIModel seiModel;
   protected WSDLPort wsdlPort;
   protected WSBinding binding;
   protected final SOAPVersion soapVer;
   protected final AddressingVersion addVer;

   public WsaTubeHelper(WSBinding binding, SEIModel seiModel, WSDLPort wsdlPort) {
      this.binding = binding;
      this.wsdlPort = wsdlPort;
      this.seiModel = seiModel;
      this.soapVer = binding.getSOAPVersion();
      this.addVer = binding.getAddressingVersion();
   }

   public String getFaultAction(Packet requestPacket, Packet responsePacket) {
      String action = null;
      if (this.seiModel != null) {
         action = this.getFaultActionFromSEIModel(requestPacket, responsePacket);
      }

      if (action != null) {
         return action;
      } else {
         action = this.addVer.getDefaultFaultAction();
         if (this.wsdlPort != null) {
            WSDLOperationMapping wsdlOp = requestPacket.getWSDLOperationMapping();
            if (wsdlOp != null) {
               WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
               return this.getFaultAction(wbo, responsePacket);
            }
         }

         return action;
      }
   }

   String getFaultActionFromSEIModel(Packet requestPacket, Packet responsePacket) {
      String action = null;
      if (this.seiModel != null && this.wsdlPort != null) {
         try {
            SOAPMessage sm = responsePacket.getMessage().copy().readAsSOAPMessage();
            if (sm == null) {
               return (String)action;
            } else if (sm.getSOAPBody() == null) {
               return (String)action;
            } else if (sm.getSOAPBody().getFault() == null) {
               return (String)action;
            } else {
               Detail detail = sm.getSOAPBody().getFault().getDetail();
               if (detail == null) {
                  return (String)action;
               } else {
                  String ns = detail.getFirstChild().getNamespaceURI();
                  String name = detail.getFirstChild().getLocalName();
                  WSDLOperationMapping wsdlOp = requestPacket.getWSDLOperationMapping();
                  JavaMethodImpl jm = wsdlOp != null ? (JavaMethodImpl)wsdlOp.getJavaMethod() : null;
                  if (jm != null) {
                     Iterator var10 = jm.getCheckedExceptions().iterator();

                     while(var10.hasNext()) {
                        CheckedExceptionImpl ce = (CheckedExceptionImpl)var10.next();
                        if (ce.getDetailType().tagName.getLocalPart().equals(name) && ce.getDetailType().tagName.getNamespaceURI().equals(ns)) {
                           return ce.getFaultAction();
                        }
                     }
                  }

                  return (String)action;
               }
            }
         } catch (SOAPException var12) {
            throw new WebServiceException(var12);
         }
      } else {
         return (String)action;
      }
   }

   String getFaultAction(@Nullable WSDLBoundOperation wbo, Packet responsePacket) {
      String action = AddressingUtils.getAction(responsePacket.getMessage().getHeaders(), this.addVer, this.soapVer);
      if (action != null) {
         return action;
      } else {
         action = this.addVer.getDefaultFaultAction();
         if (wbo == null) {
            return action;
         } else {
            try {
               SOAPMessage sm = responsePacket.getMessage().copy().readAsSOAPMessage();
               if (sm == null) {
                  return action;
               } else if (sm.getSOAPBody() == null) {
                  return action;
               } else if (sm.getSOAPBody().getFault() == null) {
                  return action;
               } else {
                  Detail detail = sm.getSOAPBody().getFault().getDetail();
                  if (detail == null) {
                     return action;
                  } else {
                     String ns = detail.getFirstChild().getNamespaceURI();
                     String name = detail.getFirstChild().getLocalName();
                     WSDLOperation o = wbo.getOperation();
                     WSDLFault fault = o.getFault(new QName(ns, name));
                     if (fault == null) {
                        return action;
                     } else {
                        action = fault.getAction();
                        return action;
                     }
                  }
               }
            } catch (SOAPException var10) {
               throw new WebServiceException(var10);
            }
         }
      }
   }

   public String getInputAction(Packet packet) {
      String action = null;
      if (this.wsdlPort != null) {
         WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
         if (wsdlOp != null) {
            WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
            WSDLOperation op = wbo.getOperation();
            action = op.getInput().getAction();
         }
      }

      return action;
   }

   public String getEffectiveInputAction(Packet packet) {
      if (packet.soapAction != null && !packet.soapAction.equals("")) {
         return packet.soapAction;
      } else {
         String action;
         if (this.wsdlPort != null) {
            WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
            if (wsdlOp != null) {
               WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
               WSDLOperation op = wbo.getOperation();
               action = op.getInput().getAction();
            } else {
               action = packet.soapAction;
            }
         } else {
            action = packet.soapAction;
         }

         return action;
      }
   }

   public boolean isInputActionDefault(Packet packet) {
      if (this.wsdlPort == null) {
         return false;
      } else {
         WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
         if (wsdlOp == null) {
            return false;
         } else {
            WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
            WSDLOperation op = wbo.getOperation();
            return op.getInput().isDefaultAction();
         }
      }
   }

   public String getSOAPAction(Packet packet) {
      String action = "";
      if (packet != null && packet.getMessage() != null) {
         if (this.wsdlPort == null) {
            return action;
         } else {
            WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
            if (wsdlOp == null) {
               return action;
            } else {
               WSDLBoundOperation op = wsdlOp.getWSDLBoundOperation();
               action = op.getSOAPAction();
               return action;
            }
         }
      } else {
         return action;
      }
   }

   public String getOutputAction(Packet packet) {
      String action = null;
      WSDLOperationMapping wsdlOp = packet.getWSDLOperationMapping();
      if (wsdlOp != null) {
         JavaMethod javaMethod = wsdlOp.getJavaMethod();
         if (javaMethod != null) {
            JavaMethodImpl jm = (JavaMethodImpl)javaMethod;
            if (jm != null && jm.getOutputAction() != null && !jm.getOutputAction().equals("")) {
               return jm.getOutputAction();
            }
         }

         WSDLBoundOperation wbo = wsdlOp.getWSDLBoundOperation();
         if (wbo != null) {
            return this.getOutputAction(wbo);
         }
      }

      return (String)action;
   }

   String getOutputAction(@Nullable WSDLBoundOperation wbo) {
      String action = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
      if (wbo != null) {
         WSDLOutput op = wbo.getOperation().getOutput();
         if (op != null) {
            action = op.getAction();
         }
      }

      return action;
   }

   public SOAPFault createInvalidAddressingHeaderFault(InvalidAddressingHeaderException e, AddressingVersion av) {
      QName name = e.getProblemHeader();
      QName subsubcode = e.getSubsubcode();
      QName subcode = av.invalidMapTag;
      String faultstring = String.format(av.getInvalidMapText(), name, subsubcode);

      try {
         SOAPFactory factory;
         SOAPFault fault;
         if (this.soapVer == SOAPVersion.SOAP_12) {
            factory = SOAPVersion.SOAP_12.getSOAPFactory();
            fault = factory.createFault();
            fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
            fault.appendFaultSubcode(subcode);
            fault.appendFaultSubcode(subsubcode);
            this.getInvalidMapDetail(name, fault.addDetail());
         } else {
            factory = SOAPVersion.SOAP_11.getSOAPFactory();
            fault = factory.createFault();
            fault.setFaultCode(subsubcode);
         }

         fault.setFaultString(faultstring);
         return fault;
      } catch (SOAPException var9) {
         throw new WebServiceException(var9);
      }
   }

   public SOAPFault newMapRequiredFault(MissingAddressingHeaderException e) {
      QName subcode = this.addVer.mapRequiredTag;
      QName subsubcode = this.addVer.mapRequiredTag;
      String faultstring = this.addVer.getMapRequiredText();

      try {
         SOAPFactory factory;
         SOAPFault fault;
         if (this.soapVer == SOAPVersion.SOAP_12) {
            factory = SOAPVersion.SOAP_12.getSOAPFactory();
            fault = factory.createFault();
            fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
            fault.appendFaultSubcode(subcode);
            fault.appendFaultSubcode(subsubcode);
            this.getMapRequiredDetail(e.getMissingHeaderQName(), fault.addDetail());
         } else {
            factory = SOAPVersion.SOAP_11.getSOAPFactory();
            fault = factory.createFault();
            fault.setFaultCode(subsubcode);
         }

         fault.setFaultString(faultstring);
         return fault;
      } catch (SOAPException var7) {
         throw new WebServiceException(var7);
      }
   }

   public abstract void getProblemActionDetail(String var1, Element var2);

   public abstract void getInvalidMapDetail(QName var1, Element var2);

   public abstract void getMapRequiredDetail(QName var1, Element var2);
}
