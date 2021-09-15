package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import javax.xml.namespace.QName;

public abstract class WSDLOperationFinder {
   protected final WSDLPort wsdlModel;
   protected final WSBinding binding;
   protected final SEIModel seiModel;

   public WSDLOperationFinder(@NotNull WSDLPort wsdlModel, @NotNull WSBinding binding, @Nullable SEIModel seiModel) {
      this.wsdlModel = wsdlModel;
      this.binding = binding;
      this.seiModel = seiModel;
   }

   /** @deprecated */
   public QName getWSDLOperationQName(Packet request) throws DispatchException {
      WSDLOperationMapping m = this.getWSDLOperationMapping(request);
      return m != null ? m.getOperationName() : null;
   }

   public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
      return null;
   }

   protected WSDLOperationMapping wsdlOperationMapping(JavaMethodImpl j) {
      return new WSDLOperationFinder.WSDLOperationMappingImpl(j.getOperation(), j);
   }

   protected WSDLOperationMapping wsdlOperationMapping(WSDLBoundOperation o) {
      return new WSDLOperationFinder.WSDLOperationMappingImpl(o, (JavaMethodImpl)null);
   }

   static class WSDLOperationMappingImpl implements WSDLOperationMapping {
      private WSDLBoundOperation wsdlOperation;
      private JavaMethod javaMethod;
      private QName operationName;

      WSDLOperationMappingImpl(WSDLBoundOperation wsdlOperation, JavaMethodImpl javaMethod) {
         this.wsdlOperation = wsdlOperation;
         this.javaMethod = javaMethod;
         this.operationName = javaMethod != null ? javaMethod.getOperationQName() : wsdlOperation.getName();
      }

      public WSDLBoundOperation getWSDLBoundOperation() {
         return this.wsdlOperation;
      }

      public JavaMethod getJavaMethod() {
         return this.javaMethod;
      }

      public QName getOperationName() {
         return this.operationName;
      }
   }
}
