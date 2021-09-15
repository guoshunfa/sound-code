package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.internal.ws.resources.UtilMessages;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;

abstract class AbstractExtensibleImpl extends AbstractObjectImpl implements WSDLExtensible {
   protected final Set<WSDLExtension> extensions = new HashSet();
   protected List<AbstractExtensibleImpl.UnknownWSDLExtension> notUnderstoodExtensions = new ArrayList();

   protected AbstractExtensibleImpl(XMLStreamReader xsr) {
      super(xsr);
   }

   protected AbstractExtensibleImpl(String systemId, int lineNumber) {
      super(systemId, lineNumber);
   }

   public final Iterable<WSDLExtension> getExtensions() {
      return this.extensions;
   }

   public final <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> type) {
      List<T> r = new ArrayList(this.extensions.size());
      Iterator var3 = this.extensions.iterator();

      while(var3.hasNext()) {
         WSDLExtension e = (WSDLExtension)var3.next();
         if (type.isInstance(e)) {
            r.add(type.cast(e));
         }
      }

      return r;
   }

   public <T extends WSDLExtension> T getExtension(Class<T> type) {
      Iterator var2 = this.extensions.iterator();

      WSDLExtension e;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         e = (WSDLExtension)var2.next();
      } while(!type.isInstance(e));

      return (WSDLExtension)type.cast(e);
   }

   public void addExtension(WSDLExtension ex) {
      if (ex == null) {
         throw new IllegalArgumentException();
      } else {
         this.extensions.add(ex);
      }
   }

   public List<? extends AbstractExtensibleImpl.UnknownWSDLExtension> getNotUnderstoodExtensions() {
      return this.notUnderstoodExtensions;
   }

   public void addNotUnderstoodExtension(QName extnEl, Locator locator) {
      this.notUnderstoodExtensions.add(new AbstractExtensibleImpl.UnknownWSDLExtension(extnEl, locator));
   }

   public boolean areRequiredExtensionsUnderstood() {
      if (this.notUnderstoodExtensions.size() == 0) {
         return true;
      } else {
         StringBuilder buf = new StringBuilder("Unknown WSDL extensibility elements:");
         Iterator var2 = this.notUnderstoodExtensions.iterator();

         while(var2.hasNext()) {
            AbstractExtensibleImpl.UnknownWSDLExtension extn = (AbstractExtensibleImpl.UnknownWSDLExtension)var2.next();
            buf.append('\n').append(extn.toString());
         }

         throw new WebServiceException(buf.toString());
      }
   }

   protected static class UnknownWSDLExtension implements WSDLExtension, WSDLObject {
      private final QName extnEl;
      private final Locator locator;

      public UnknownWSDLExtension(QName extnEl, Locator locator) {
         this.extnEl = extnEl;
         this.locator = locator;
      }

      public QName getName() {
         return this.extnEl;
      }

      @NotNull
      public Locator getLocation() {
         return this.locator;
      }

      public String toString() {
         return this.extnEl + " " + UtilMessages.UTIL_LOCATION(this.locator.getLineNumber(), this.locator.getSystemId());
      }
   }
}
