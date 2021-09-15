package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.ElementBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleReferenceNodeProperty<BeanT, ValueT> extends PropertyImpl<BeanT> {
   private final Accessor<BeanT, ValueT> acc;
   private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
   private final DomHandler domHandler;
   private final WildcardMode wcMode;

   public SingleReferenceNodeProperty(JAXBContextImpl context, RuntimeReferencePropertyInfo prop) {
      super(context, prop);
      this.acc = prop.getAccessor().optimize(context);
      Iterator var3 = prop.getElements().iterator();

      while(var3.hasNext()) {
         RuntimeElement e = (RuntimeElement)var3.next();
         this.expectedElements.put((QName)e.getElementName(), context.getOrCreate((RuntimeTypeInfo)e));
      }

      if (prop.getWildcard() != null) {
         this.domHandler = (DomHandler)ClassFactory.create((Class)prop.getDOMHandler());
         this.wcMode = prop.getWildcard();
      } else {
         this.domHandler = null;
         this.wcMode = null;
      }

   }

   public void reset(BeanT bean) throws AccessorException {
      this.acc.set(bean, (Object)null);
   }

   public String getIdValue(BeanT beanT) {
      return null;
   }

   public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
      ValueT v = this.acc.get(o);
      if (v != null) {
         try {
            JaxBeanInfo bi = w.grammar.getBeanInfo(v, true);
            if (bi.jaxbType == Object.class && this.domHandler != null) {
               w.writeDom(v, this.domHandler, o, this.fieldName);
            } else {
               bi.serializeRoot(v, w);
            }
         } catch (JAXBException var6) {
            w.reportError(this.fieldName, var6);
         }
      }

   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
      Iterator var3 = this.expectedElements.entrySet().iterator();

      while(var3.hasNext()) {
         QNameMap.Entry<JaxBeanInfo> n = (QNameMap.Entry)var3.next();
         handlers.put(n.nsUri, n.localName, new ChildLoader(((JaxBeanInfo)n.getValue()).getLoader(chain.context, true), this.acc));
      }

      if (this.domHandler != null) {
         handlers.put((QName)CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), this.acc));
      }

   }

   public PropertyKind getKind() {
      return PropertyKind.REFERENCE;
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      JaxBeanInfo bi = (JaxBeanInfo)this.expectedElements.get(nsUri, localName);
      if (bi != null) {
         if (bi instanceof ElementBeanInfoImpl) {
            final ElementBeanInfoImpl ebi = (ElementBeanInfoImpl)bi;
            return new Accessor<BeanT, Object>(ebi.expectedType) {
               public Object get(BeanT bean) throws AccessorException {
                  ValueT r = SingleReferenceNodeProperty.this.acc.get(bean);
                  return r instanceof JAXBElement ? ((JAXBElement)r).getValue() : r;
               }

               public void set(BeanT bean, Object value) throws AccessorException {
                  if (value != null) {
                     try {
                        value = ebi.createInstanceFromValue(value);
                     } catch (IllegalAccessException var4) {
                        throw new AccessorException(var4);
                     } catch (InvocationTargetException var5) {
                        throw new AccessorException(var5);
                     } catch (InstantiationException var6) {
                        throw new AccessorException(var6);
                     }
                  }

                  SingleReferenceNodeProperty.this.acc.set(bean, value);
               }
            };
         } else {
            return this.acc;
         }
      } else {
         return null;
      }
   }
}
