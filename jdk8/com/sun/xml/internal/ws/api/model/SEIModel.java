package com.sun.xml.internal.ws.api.model;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.util.Pool;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

public interface SEIModel {
   Pool.Marshaller getMarshallerPool();

   /** @deprecated */
   JAXBContext getJAXBContext();

   JavaMethod getJavaMethod(Method var1);

   JavaMethod getJavaMethod(QName var1);

   JavaMethod getJavaMethodForWsdlOperation(QName var1);

   Collection<? extends JavaMethod> getJavaMethods();

   @NotNull
   String getWSDLLocation();

   @NotNull
   QName getServiceQName();

   @NotNull
   WSDLPort getPort();

   @NotNull
   QName getPortName();

   @NotNull
   QName getPortTypeName();

   @NotNull
   QName getBoundPortTypeName();

   @NotNull
   String getTargetNamespace();
}
