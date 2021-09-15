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
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.reflect.NullSafeAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class ArrayElementProperty<BeanT, ListT, ItemT> extends ArrayERProperty<BeanT, ListT, ItemT> {
   private final Map<Class, TagAndType> typeMap = new HashMap();
   private Map<TypeRef<Type, Class>, JaxBeanInfo> refs = new HashMap();
   protected RuntimeElementPropertyInfo prop;
   private final Name nillableTagName;

   protected ArrayElementProperty(JAXBContextImpl grammar, RuntimeElementPropertyInfo prop) {
      super(grammar, prop, prop.getXmlName(), prop.isCollectionNillable());
      this.prop = prop;
      List<? extends RuntimeTypeRef> types = prop.getTypes();
      Name n = null;
      Iterator var5 = types.iterator();

      while(var5.hasNext()) {
         RuntimeTypeRef typeRef = (RuntimeTypeRef)var5.next();
         Class type = (Class)typeRef.getTarget().getType();
         if (type.isPrimitive()) {
            type = (Class)RuntimeUtil.primitiveToBox.get(type);
         }

         JaxBeanInfo beanInfo = grammar.getOrCreate((RuntimeTypeInfo)typeRef.getTarget());
         TagAndType tt = new TagAndType(grammar.nameBuilder.createElementName(typeRef.getTagName()), beanInfo);
         this.typeMap.put(type, tt);
         this.refs.put(typeRef, beanInfo);
         if (typeRef.isNillable() && n == null) {
            n = tt.tagName;
         }
      }

      this.nillableTagName = n;
   }

   public void wrapUp() {
      super.wrapUp();
      this.refs = null;
      this.prop = null;
   }

   protected void serializeListBody(BeanT beanT, XMLSerializer w, ListT list) throws IOException, XMLStreamException, SAXException, AccessorException {
      ListIterator<ItemT> itr = this.lister.iterator(list, w);
      boolean isIdref = itr instanceof Lister.IDREFSIterator;

      while(itr.hasNext()) {
         try {
            ItemT item = itr.next();
            if (item == null) {
               if (this.nillableTagName != null) {
                  w.startElement(this.nillableTagName, (Object)null);
                  w.writeXsiNilTrue();
                  w.endElement();
               }
            } else {
               Class itemType = item.getClass();
               if (isIdref) {
                  itemType = ((Lister.IDREFSIterator)itr).last().getClass();
               }

               TagAndType tt;
               for(tt = (TagAndType)this.typeMap.get(itemType); tt == null && itemType != null; tt = (TagAndType)this.typeMap.get(itemType)) {
                  itemType = itemType.getSuperclass();
               }

               if (tt == null) {
                  w.startElement(((TagAndType)this.typeMap.values().iterator().next()).tagName, (Object)null);
                  w.childAsXsiType(item, this.fieldName, w.grammar.getBeanInfo(Object.class), false);
               } else {
                  w.startElement(tt.tagName, (Object)null);
                  this.serializeItem(tt.beanInfo, item, w);
               }

               w.endElement();
            }
         } catch (JAXBException var9) {
            w.reportError(this.fieldName, var9);
         }
      }

   }

   protected abstract void serializeItem(JaxBeanInfo var1, ItemT var2, XMLSerializer var3) throws SAXException, AccessorException, IOException, XMLStreamException;

   public void createBodyUnmarshaller(UnmarshallerChain chain, QNameMap<ChildLoader> loaders) {
      int offset = chain.allocateOffset();
      Receiver recv = new ArrayERProperty.ReceiverImpl(offset);

      Name tagName;
      Object item;
      for(Iterator var5 = this.prop.getTypes().iterator(); var5.hasNext(); loaders.put((Name)tagName, new ChildLoader((Loader)item, recv))) {
         RuntimeTypeRef typeRef = (RuntimeTypeRef)var5.next();
         tagName = chain.context.nameBuilder.createElementName(typeRef.getTagName());
         item = this.createItemUnmarshaller(chain, typeRef);
         if (typeRef.isNillable() || chain.context.allNillable) {
            item = new XsiNilLoader.Array((Loader)item);
         }

         if (typeRef.getDefaultValue() != null) {
            item = new DefaultValueLoaderDecorator((Loader)item, typeRef.getDefaultValue());
         }
      }

   }

   public final PropertyKind getKind() {
      return PropertyKind.ELEMENT;
   }

   private Loader createItemUnmarshaller(UnmarshallerChain chain, RuntimeTypeRef typeRef) {
      if (PropertyFactory.isLeaf(typeRef.getSource())) {
         Transducer xducer = typeRef.getTransducer();
         return new TextLoader(xducer);
      } else {
         return ((JaxBeanInfo)this.refs.get(typeRef)).getLoader(chain.context, true);
      }
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      if (this.wrapperTagName != null) {
         if (this.wrapperTagName.equals(nsUri, localName)) {
            return this.acc;
         }
      } else {
         Iterator var3 = this.typeMap.values().iterator();

         while(var3.hasNext()) {
            TagAndType tt = (TagAndType)var3.next();
            if (tt.tagName.equals(nsUri, localName)) {
               return new NullSafeAccessor(this.acc, this.lister);
            }
         }
      }

      return null;
   }
}
