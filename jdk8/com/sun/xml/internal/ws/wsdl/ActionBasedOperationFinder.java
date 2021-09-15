package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class ActionBasedOperationFinder extends WSDLOperationFinder {
   private static final Logger LOGGER = Logger.getLogger(ActionBasedOperationFinder.class.getName());
   private final Map<ActionBasedOperationSignature, WSDLOperationMapping> uniqueOpSignatureMap;
   private final Map<String, WSDLOperationMapping> actionMap;
   @NotNull
   private final AddressingVersion av;

   public ActionBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
      super(wsdlModel, binding, seiModel);

      assert binding.getAddressingVersion() != null;

      this.av = binding.getAddressingVersion();
      this.uniqueOpSignatureMap = new HashMap();
      this.actionMap = new HashMap();
      Iterator var4;
      ActionBasedOperationSignature opSignature;
      if (seiModel != null) {
         var4 = ((AbstractSEIModelImpl)seiModel).getJavaMethods().iterator();

         while(true) {
            JavaMethodImpl m;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               m = (JavaMethodImpl)var4.next();
            } while(m.getMEP().isAsync);

            String action = m.getInputAction();
            QName payloadName = m.getRequestPayloadName();
            if (payloadName == null) {
               payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
            }

            if ((action == null || action.equals("")) && m.getOperation() != null) {
               action = m.getOperation().getOperation().getInput().getAction();
            }

            if (action != null) {
               opSignature = new ActionBasedOperationSignature(action, payloadName);
               if (this.uniqueOpSignatureMap.get(opSignature) != null) {
                  LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(opSignature), m.getOperationQName(), action, payloadName));
               }

               this.uniqueOpSignatureMap.put(opSignature, this.wsdlOperationMapping(m));
               this.actionMap.put(action, this.wsdlOperationMapping(m));
            }
         }
      } else {
         var4 = wsdlModel.getBinding().getBindingOperations().iterator();

         while(var4.hasNext()) {
            WSDLBoundOperation wsdlOp = (WSDLBoundOperation)var4.next();
            QName payloadName = wsdlOp.getRequestPayloadName();
            if (payloadName == null) {
               payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
            }

            String action = wsdlOp.getOperation().getInput().getAction();
            opSignature = new ActionBasedOperationSignature(action, payloadName);
            if (this.uniqueOpSignatureMap.get(opSignature) != null) {
               LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(opSignature), wsdlOp.getName(), action, payloadName));
            }

            this.uniqueOpSignatureMap.put(opSignature, this.wsdlOperationMapping(wsdlOp));
            this.actionMap.put(action, this.wsdlOperationMapping(wsdlOp));
         }

      }
   }

   public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
      MessageHeaders hl = request.getMessage().getHeaders();
      String action = AddressingUtils.getAction(hl, this.av, this.binding.getSOAPVersion());
      if (action == null) {
         return null;
      } else {
         Message message = request.getMessage();
         String localPart = message.getPayloadLocalPart();
         QName payloadName;
         if (localPart == null) {
            payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
         } else {
            String nsUri = message.getPayloadNamespaceURI();
            if (nsUri == null) {
               nsUri = "";
            }

            payloadName = new QName(nsUri, localPart);
         }

         WSDLOperationMapping opMapping = (WSDLOperationMapping)this.uniqueOpSignatureMap.get(new ActionBasedOperationSignature(action, payloadName));
         if (opMapping != null) {
            return opMapping;
         } else {
            opMapping = (WSDLOperationMapping)this.actionMap.get(action);
            if (opMapping != null) {
               return opMapping;
            } else {
               Message result = Messages.create(action, this.av, this.binding.getSOAPVersion());
               throw new DispatchException(result);
            }
         }
      }
   }
}
