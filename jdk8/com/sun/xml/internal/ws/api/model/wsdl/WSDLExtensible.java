package com.sun.xml.internal.ws.api.model.wsdl;

import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public interface WSDLExtensible extends WSDLObject {
   Iterable<WSDLExtension> getExtensions();

   <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> var1);

   <T extends WSDLExtension> T getExtension(Class<T> var1);

   void addExtension(WSDLExtension var1);

   boolean areRequiredExtensionsUnderstood();

   void addNotUnderstoodExtension(QName var1, Locator var2);

   List<? extends WSDLExtension> getNotUnderstoodExtensions();
}
