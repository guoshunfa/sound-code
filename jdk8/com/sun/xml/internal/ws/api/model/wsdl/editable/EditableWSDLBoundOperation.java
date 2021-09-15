package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;

public interface EditableWSDLBoundOperation extends WSDLBoundOperation {
   @NotNull
   EditableWSDLOperation getOperation();

   @NotNull
   EditableWSDLBoundPortType getBoundPortType();

   @Nullable
   EditableWSDLPart getPart(@NotNull String var1, @NotNull WebParam.Mode var2);

   @NotNull
   Map<String, ? extends EditableWSDLPart> getInParts();

   @NotNull
   Map<String, ? extends EditableWSDLPart> getOutParts();

   @NotNull
   Iterable<? extends EditableWSDLBoundFault> getFaults();

   void addPart(EditableWSDLPart var1, WebParam.Mode var2);

   void addFault(@NotNull EditableWSDLBoundFault var1);

   void setAnonymous(WSDLBoundOperation.ANONYMOUS var1);

   void setInputExplicitBodyParts(boolean var1);

   void setOutputExplicitBodyParts(boolean var1);

   void setFaultExplicitBodyParts(boolean var1);

   void setRequestNamespace(String var1);

   void setResponseNamespace(String var1);

   void setSoapAction(String var1);

   void setStyle(SOAPBinding.Style var1);

   void freeze(EditableWSDLModel var1);
}
