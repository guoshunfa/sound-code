package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementNodeProperty<BeanT, ValueT> extends PropertyImpl<BeanT> {
   private final Accessor<BeanT, ValueT> acc;
   private final boolean nillable;
   private final QName[] acceptedElements;
   private final Map<Class, TagAndType> typeNames = new HashMap();
   private RuntimeElementPropertyInfo prop;
   private final Name nullTagName;

   public SingleElementNodeProperty(JAXBContextImpl context, RuntimeElementPropertyInfo prop) {
      super(context, prop);
      this.acc = prop.getAccessor().optimize(context);
      this.prop = prop;
      QName nt = null;
      boolean nil = false;
      this.acceptedElements = new QName[prop.getTypes().size()];

      for(int i = 0; i < this.acceptedElements.length; ++i) {
         this.acceptedElements[i] = ((RuntimeTypeRef)prop.getTypes().get(i)).getTagName();
      }

      RuntimeTypeRef e;
      for(Iterator var8 = prop.getTypes().iterator(); var8.hasNext(); nil |= e.isNillable()) {
         e = (RuntimeTypeRef)var8.next();
         JaxBeanInfo beanInfo = context.getOrCreate((RuntimeTypeInfo)e.getTarget());
         if (nt == null) {
            nt = e.getTagName();
         }

         this.typeNames.put(beanInfo.jaxbType, new TagAndType(context.nameBuilder.createElementName(e.getTagName()), beanInfo));
      }

      this.nullTagName = context.nameBuilder.createElementName(nt);
      this.nillable = nil;
   }

   public void wrapUp() {
      super.wrapUp();
      this.prop = null;
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
         Class vtype = v.getClass();
         TagAndType tt = (TagAndType)this.typeNames.get(vtype);
         if (tt == null) {
            Iterator var7 = this.typeNames.entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry<Class, TagAndType> e = (Map.Entry)var7.next();
               if (((Class)e.getKey()).isAssignableFrom(vtype)) {
                  tt = (TagAndType)e.getValue();
                  break;
               }
            }
         }

         boolean addNilDecl = o instanceof JAXBElement && ((JAXBElement)o).isNil();
         if (tt == null) {
            w.startElement(((TagAndType)this.typeNames.values().iterator().next()).tagName, (Object)null);
            w.childAsXsiType(v, this.fieldName, w.grammar.getBeanInfo(Object.class), addNilDecl && this.nillable);
         } else {
            w.startElement(tt.tagName, (Object)null);
            w.childAsXsiType(v, this.fieldName, tt.beanInfo, addNilDecl && this.nillable);
         }

         w.endElement();
      } else if (this.nillable) {
         w.startElement(this.nullTagName, (Object)null);
         w.writeXsiNilTrue();
         w.endElement();
      }

   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
      JAXBContextImpl context = chain.context;

      TypeRef e;
      Object l;
      for(Iterator var4 = this.prop.getTypes().iterator(); var4.hasNext(); handlers.put((QName)e.getTagName(), new ChildLoader((Loader)l, this.acc))) {
         e = (TypeRef)var4.next();
         JaxBeanInfo bi = context.getOrCreate((RuntimeTypeInfo)e.getTarget());
         l = bi.getLoader(context, !Modifier.isFinal(bi.jaxbType.getModifiers()));
         if (e.getDefaultValue() != null) {
            l = new DefaultValueLoaderDecorator((Loader)l, e.getDefaultValue());
         }

         if (this.nillable || chain.context.allNillable) {
            l = new XsiNilLoader.Single((Loader)l, this.acc);
         }
      }

   }

   public PropertyKind getKind() {
      return PropertyKind.ELEMENT;
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      QName[] var3 = this.acceptedElements;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         QName n = var3[var5];
         if (n.getNamespaceURI().equals(nsUri) && n.getLocalPart().equals(localName)) {
            return this.acc;
         }
      }

      return null;
   }
}
