package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

class ArrayReferenceNodeProperty<BeanT, ListT, ItemT> extends ArrayERProperty<BeanT, ListT, ItemT> {
   private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
   private final boolean isMixed;
   private final DomHandler domHandler;
   private final WildcardMode wcMode;

   public ArrayReferenceNodeProperty(JAXBContextImpl p, RuntimeReferencePropertyInfo prop) {
      super(p, prop, prop.getXmlName(), prop.isCollectionNillable());
      Iterator var3 = prop.getElements().iterator();

      while(var3.hasNext()) {
         RuntimeElement e = (RuntimeElement)var3.next();
         JaxBeanInfo bi = p.getOrCreate((RuntimeTypeInfo)e);
         this.expectedElements.put(e.getElementName().getNamespaceURI(), e.getElementName().getLocalPart(), bi);
      }

      this.isMixed = prop.isMixed();
      if (prop.getWildcard() != null) {
         this.domHandler = (DomHandler)ClassFactory.create((Class)prop.getDOMHandler());
         this.wcMode = prop.getWildcard();
      } else {
         this.domHandler = null;
         this.wcMode = null;
      }

   }

   protected final void serializeListBody(BeanT o, XMLSerializer w, ListT list) throws IOException, XMLStreamException, SAXException {
      ListIterator itr = this.lister.iterator(list, w);

      while(itr.hasNext()) {
         try {
            ItemT item = itr.next();
            if (item != null) {
               if (this.isMixed && item.getClass() == String.class) {
                  w.text((String)((String)item), (String)null);
               } else {
                  JaxBeanInfo bi = w.grammar.getBeanInfo(item, true);
                  if (bi.jaxbType == Object.class && this.domHandler != null) {
                     w.writeDom(item, this.domHandler, o, this.fieldName);
                  } else {
                     bi.serializeRoot(item, w);
                  }
               }
            }
         } catch (JAXBException var7) {
            w.reportError(this.fieldName, var7);
         }
      }

   }

   public void createBodyUnmarshaller(UnmarshallerChain chain, QNameMap<ChildLoader> loaders) {
      int offset = chain.allocateOffset();
      Receiver recv = new ArrayERProperty.ReceiverImpl(offset);
      Iterator var5 = this.expectedElements.entrySet().iterator();

      while(var5.hasNext()) {
         QNameMap.Entry<JaxBeanInfo> n = (QNameMap.Entry)var5.next();
         JaxBeanInfo beanInfo = (JaxBeanInfo)n.getValue();
         loaders.put(n.nsUri, n.localName, new ChildLoader(beanInfo.getLoader(chain.context, true), recv));
      }

      if (this.isMixed) {
         loaders.put((QName)TEXT_HANDLER, new ChildLoader(new ArrayReferenceNodeProperty.MixedTextLoader(recv), (Receiver)null));
      }

      if (this.domHandler != null) {
         loaders.put((QName)CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), recv));
      }

   }

   public PropertyKind getKind() {
      return PropertyKind.REFERENCE;
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      if (this.wrapperTagName != null) {
         if (this.wrapperTagName.equals(nsUri, localName)) {
            return this.acc;
         }
      } else if (this.expectedElements.containsKey(nsUri, localName)) {
         return this.acc;
      }

      return null;
   }

   private static final class MixedTextLoader extends Loader {
      private final Receiver recv;

      public MixedTextLoader(Receiver recv) {
         super(true);
         this.recv = recv;
      }

      public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
         if (text.length() != 0) {
            this.recv.receive(state, text.toString());
         }

      }
   }
}
