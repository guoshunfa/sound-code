package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.util.QNameMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class PayloadQNameBasedOperationFinder extends WSDLOperationFinder {
   private static final Logger LOGGER = Logger.getLogger(PayloadQNameBasedOperationFinder.class.getName());
   public static final String EMPTY_PAYLOAD_LOCAL = "";
   public static final String EMPTY_PAYLOAD_NSURI = "";
   public static final QName EMPTY_PAYLOAD = new QName("", "");
   private final QNameMap<WSDLOperationMapping> methodHandlers = new QNameMap();
   private final QNameMap<List<String>> unique = new QNameMap();

   public PayloadQNameBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
      super(wsdlModel, binding, seiModel);
      Iterator var4;
      QName name;
      WSDLBoundOperation wsdlOp;
      if (seiModel != null) {
         var4 = ((AbstractSEIModelImpl)seiModel).getJavaMethods().iterator();

         JavaMethodImpl m;
         while(var4.hasNext()) {
            m = (JavaMethodImpl)var4.next();
            if (!m.getMEP().isAsync) {
               name = m.getRequestPayloadName();
               if (name == null) {
                  name = EMPTY_PAYLOAD;
               }

               List<String> methods = (List)this.unique.get(name);
               if (methods == null) {
                  methods = new ArrayList();
                  this.unique.put(name, methods);
               }

               ((List)methods).add(m.getMethod().getName());
            }
         }

         var4 = this.unique.entrySet().iterator();

         while(var4.hasNext()) {
            QNameMap.Entry<List<String>> e = (QNameMap.Entry)var4.next();
            if (((List)e.getValue()).size() > 1) {
               LOGGER.warning(ServerMessages.NON_UNIQUE_DISPATCH_QNAME(e.getValue(), e.createQName()));
            }
         }

         var4 = ((AbstractSEIModelImpl)seiModel).getJavaMethods().iterator();

         while(var4.hasNext()) {
            m = (JavaMethodImpl)var4.next();
            name = m.getRequestPayloadName();
            if (name == null) {
               name = EMPTY_PAYLOAD;
            }

            if (((List)this.unique.get(name)).size() == 1) {
               this.methodHandlers.put(name, this.wsdlOperationMapping(m));
            }
         }
      } else {
         for(var4 = wsdlModel.getBinding().getBindingOperations().iterator(); var4.hasNext(); this.methodHandlers.put(name, this.wsdlOperationMapping(wsdlOp))) {
            wsdlOp = (WSDLBoundOperation)var4.next();
            name = wsdlOp.getRequestPayloadName();
            if (name == null) {
               name = EMPTY_PAYLOAD;
            }
         }
      }

   }

   public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
      Message message = request.getMessage();
      String localPart = message.getPayloadLocalPart();
      String nsUri;
      if (localPart == null) {
         localPart = "";
         nsUri = "";
      } else {
         nsUri = message.getPayloadNamespaceURI();
         if (nsUri == null) {
            nsUri = "";
         }
      }

      WSDLOperationMapping op = (WSDLOperationMapping)this.methodHandlers.get(nsUri, localPart);
      if (op == null && !this.unique.containsKey(nsUri, localPart)) {
         String dispatchKey = "{" + nsUri + "}" + localPart;
         String faultString = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(dispatchKey);
         throw new DispatchException(SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), faultString, this.binding.getSOAPVersion().faultCodeClient));
      } else {
         return op;
      }
   }
}
