package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class SOAPActionBasedOperationFinder extends WSDLOperationFinder {
   private final Map<String, WSDLOperationMapping> methodHandlers = new HashMap();

   public SOAPActionBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
      super(wsdlModel, binding, seiModel);
      Map<String, Integer> unique = new HashMap();
      Iterator var5;
      if (seiModel != null) {
         var5 = ((AbstractSEIModelImpl)seiModel).getJavaMethods().iterator();

         JavaMethodImpl m;
         String soapAction;
         while(var5.hasNext()) {
            m = (JavaMethodImpl)var5.next();
            soapAction = m.getSOAPAction();
            Integer count = (Integer)unique.get(soapAction);
            if (count == null) {
               unique.put(soapAction, 1);
            } else {
               unique.put(soapAction, count + 1);
            }
         }

         var5 = ((AbstractSEIModelImpl)seiModel).getJavaMethods().iterator();

         while(var5.hasNext()) {
            m = (JavaMethodImpl)var5.next();
            soapAction = m.getSOAPAction();
            if ((Integer)unique.get(soapAction) == 1) {
               this.methodHandlers.put('"' + soapAction + '"', this.wsdlOperationMapping(m));
            }
         }
      } else {
         var5 = wsdlModel.getBinding().getBindingOperations().iterator();

         while(var5.hasNext()) {
            WSDLBoundOperation wsdlOp = (WSDLBoundOperation)var5.next();
            this.methodHandlers.put(wsdlOp.getSOAPAction(), this.wsdlOperationMapping(wsdlOp));
         }
      }

   }

   public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
      return request.soapAction == null ? null : (WSDLOperationMapping)this.methodHandlers.get(request.soapAction);
   }
}
