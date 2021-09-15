package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.Map;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public interface WSDLBoundOperation extends WSDLObject, WSDLExtensible {
   @NotNull
   QName getName();

   @NotNull
   String getSOAPAction();

   @NotNull
   WSDLOperation getOperation();

   @NotNull
   WSDLBoundPortType getBoundPortType();

   WSDLBoundOperation.ANONYMOUS getAnonymous();

   @Nullable
   WSDLPart getPart(@NotNull String var1, @NotNull WebParam.Mode var2);

   ParameterBinding getInputBinding(String var1);

   ParameterBinding getOutputBinding(String var1);

   ParameterBinding getFaultBinding(String var1);

   String getMimeTypeForInputPart(String var1);

   String getMimeTypeForOutputPart(String var1);

   String getMimeTypeForFaultPart(String var1);

   @NotNull
   Map<String, ? extends WSDLPart> getInParts();

   @NotNull
   Map<String, ? extends WSDLPart> getOutParts();

   @NotNull
   Iterable<? extends WSDLBoundFault> getFaults();

   Map<String, ParameterBinding> getInputParts();

   Map<String, ParameterBinding> getOutputParts();

   Map<String, ParameterBinding> getFaultParts();

   @Nullable
   QName getRequestPayloadName();

   @Nullable
   QName getResponsePayloadName();

   String getRequestNamespace();

   String getResponseNamespace();

   public static enum ANONYMOUS {
      optional,
      required,
      prohibited;
   }
}
