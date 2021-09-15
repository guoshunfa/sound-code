package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLGeneratorExtension extends WSDLGeneratorExtension {
   private boolean enabled;
   private boolean required = false;
   private static final Logger LOGGER = Logger.getLogger(W3CAddressingWSDLGeneratorExtension.class.getName());

   public void start(WSDLGenExtnContext ctxt) {
      WSBinding binding = ctxt.getBinding();
      TypedXmlWriter root = ctxt.getRoot();
      this.enabled = binding.isFeatureEnabled(AddressingFeature.class);
      if (this.enabled) {
         AddressingFeature ftr = (AddressingFeature)binding.getFeature(AddressingFeature.class);
         this.required = ftr.isRequired();
         root._namespace(AddressingVersion.W3C.wsdlNsUri, AddressingVersion.W3C.getWsdlPrefix());
      }
   }

   public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
      if (this.enabled) {
         Action a = (Action)method.getSEIMethod().getAnnotation(Action.class);
         if (a != null && !a.input().equals("")) {
            this.addAttribute(input, a.input());
         } else {
            String soapAction = method.getBinding().getSOAPAction();
            if (soapAction == null || soapAction.equals("")) {
               String defaultAction = getDefaultAction(method);
               this.addAttribute(input, defaultAction);
            }
         }

      }
   }

   protected static final String getDefaultAction(JavaMethod method) {
      String tns = method.getOwner().getTargetNamespace();
      String delim = "/";

      try {
         URI uri = new URI(tns);
         if (uri.getScheme().equalsIgnoreCase("urn")) {
            delim = ":";
         }
      } catch (URISyntaxException var4) {
         LOGGER.warning("TargetNamespace of WebService is not a valid URI");
      }

      if (tns.endsWith(delim)) {
         tns = tns.substring(0, tns.length() - 1);
      }

      String name = method.getMEP().isOneWay() ? method.getOperationName() : method.getOperationName() + "Request";
      return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
   }

   public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
      if (this.enabled) {
         Action a = (Action)method.getSEIMethod().getAnnotation(Action.class);
         if (a != null && !a.output().equals("")) {
            this.addAttribute(output, a.output());
         }

      }
   }

   public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
      if (this.enabled) {
         Action a = (Action)method.getSEIMethod().getAnnotation(Action.class);
         Class[] exs = method.getSEIMethod().getExceptionTypes();
         if (exs != null) {
            if (a != null && a.fault() != null) {
               FaultAction[] var6 = a.fault();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  FaultAction fa = var6[var8];
                  if (fa.className().getName().equals(ce.getExceptionClass().getName())) {
                     if (fa.value().equals("")) {
                        return;
                     }

                     this.addAttribute(fault, fa.value());
                     return;
                  }
               }
            }

         }
      }
   }

   private void addAttribute(TypedXmlWriter writer, String attrValue) {
      writer._attribute((QName)AddressingVersion.W3C.wsdlActionTag, attrValue);
   }

   public void addBindingExtension(TypedXmlWriter binding) {
      if (this.enabled) {
         binding._element(AddressingVersion.W3C.wsdlExtensionTag, UsingAddressing.class);
      }
   }
}
