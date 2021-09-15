package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Map;
import javax.xml.namespace.QName;

public interface EditableWSDLModel extends WSDLModel {
   EditableWSDLPortType getPortType(@NotNull QName var1);

   void addBinding(EditableWSDLBoundPortType var1);

   EditableWSDLBoundPortType getBinding(@NotNull QName var1);

   EditableWSDLBoundPortType getBinding(@NotNull QName var1, @NotNull QName var2);

   EditableWSDLService getService(@NotNull QName var1);

   @NotNull
   Map<QName, ? extends EditableWSDLMessage> getMessages();

   void addMessage(EditableWSDLMessage var1);

   @NotNull
   Map<QName, ? extends EditableWSDLPortType> getPortTypes();

   void addPortType(EditableWSDLPortType var1);

   @NotNull
   Map<QName, ? extends EditableWSDLBoundPortType> getBindings();

   @NotNull
   Map<QName, ? extends EditableWSDLService> getServices();

   void addService(EditableWSDLService var1);

   EditableWSDLMessage getMessage(QName var1);

   /** @deprecated */
   void setPolicyMap(PolicyMap var1);

   void finalizeRpcLitBinding(EditableWSDLBoundPortType var1);

   void freeze();
}
