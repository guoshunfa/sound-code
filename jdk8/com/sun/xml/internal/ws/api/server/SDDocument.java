package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@ManagedData
public interface SDDocument {
   @ManagedAttribute
   QName getRootName();

   @ManagedAttribute
   boolean isWSDL();

   @ManagedAttribute
   boolean isSchema();

   @ManagedAttribute
   Set<String> getImports();

   @ManagedAttribute
   URL getURL();

   void writeTo(@Nullable PortAddressResolver var1, DocumentAddressResolver var2, OutputStream var3) throws IOException;

   void writeTo(PortAddressResolver var1, DocumentAddressResolver var2, XMLStreamWriter var3) throws XMLStreamException, IOException;

   public interface WSDL extends SDDocument {
      @ManagedAttribute
      String getTargetNamespace();

      @ManagedAttribute
      boolean hasPortType();

      @ManagedAttribute
      boolean hasService();

      @ManagedAttribute
      Set<QName> getAllServices();
   }

   public interface Schema extends SDDocument {
      @ManagedAttribute
      String getTargetNamespace();
   }
}
