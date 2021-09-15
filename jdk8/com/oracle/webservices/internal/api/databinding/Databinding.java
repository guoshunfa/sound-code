package com.oracle.webservices.internal.api.databinding;

import com.oracle.webservices.internal.api.message.MessageContext;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public interface Databinding {
   JavaCallInfo createJavaCallInfo(Method var1, Object[] var2);

   MessageContext serializeRequest(JavaCallInfo var1);

   JavaCallInfo deserializeResponse(MessageContext var1, JavaCallInfo var2);

   JavaCallInfo deserializeRequest(MessageContext var1);

   MessageContext serializeResponse(JavaCallInfo var1);

   public interface Builder {
      Databinding.Builder targetNamespace(String var1);

      Databinding.Builder serviceName(QName var1);

      Databinding.Builder portName(QName var1);

      /** @deprecated */
      Databinding.Builder wsdlURL(URL var1);

      /** @deprecated */
      Databinding.Builder wsdlSource(Source var1);

      /** @deprecated */
      Databinding.Builder entityResolver(EntityResolver var1);

      Databinding.Builder classLoader(ClassLoader var1);

      Databinding.Builder feature(WebServiceFeature... var1);

      Databinding.Builder property(String var1, Object var2);

      Databinding build();

      WSDLGenerator createWSDLGenerator();
   }
}
