package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeMapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleMapNodeProperty<BeanT, ValueT extends Map> extends PropertyImpl<BeanT> {
   private final Accessor<BeanT, ValueT> acc;
   private final Name tagName;
   private final Name entryTag;
   private final Name keyTag;
   private final Name valueTag;
   private final boolean nillable;
   private JaxBeanInfo keyBeanInfo;
   private JaxBeanInfo valueBeanInfo;
   private final Class<? extends ValueT> mapImplClass;
   private static final Class[] knownImplClasses = new Class[]{HashMap.class, TreeMap.class, LinkedHashMap.class};
   private Loader keyLoader;
   private Loader valueLoader;
   private final Loader itemsLoader = new Loader(false) {
      private ThreadLocal<BeanT> target = new ThreadLocal();
      private ThreadLocal<ValueT> map = new ThreadLocal();
      private int depthCounter = 0;

      public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         try {
            this.target.set(state.getPrev().getTarget());
            this.map.set(SingleMapNodeProperty.this.acc.get(this.target.get()));
            ++this.depthCounter;
            if (this.map.get() == null) {
               this.map.set(ClassFactory.create(SingleMapNodeProperty.this.mapImplClass));
            }

            ((Map)this.map.get()).clear();
            state.setTarget(this.map.get());
         } catch (AccessorException var4) {
            handleGenericException(var4, true);
            state.setTarget(new HashMap());
         }

      }

      public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         super.leaveElement(state, ea);

         try {
            SingleMapNodeProperty.this.acc.set(this.target.get(), this.map.get());
            if (--this.depthCounter == 0) {
               this.target.remove();
               this.map.remove();
            }
         } catch (AccessorException var4) {
            handleGenericException(var4, true);
         }

      }

      public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         if (ea.matches(SingleMapNodeProperty.this.entryTag)) {
            state.setLoader(SingleMapNodeProperty.this.entryLoader);
         } else {
            super.childElement(state, ea);
         }

      }

      public Collection<QName> getExpectedChildElements() {
         return Collections.singleton(SingleMapNodeProperty.this.entryTag.toQName());
      }
   };
   private final Loader entryLoader = new Loader(false) {
      public void startElement(UnmarshallingContext.State state, TagName ea) {
         state.setTarget(new Object[2]);
      }

      public void leaveElement(UnmarshallingContext.State state, TagName ea) {
         Object[] keyValue = (Object[])((Object[])state.getTarget());
         Map map = (Map)state.getPrev().getTarget();
         map.put(keyValue[0], keyValue[1]);
      }

      public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         if (ea.matches(SingleMapNodeProperty.this.keyTag)) {
            state.setLoader(SingleMapNodeProperty.this.keyLoader);
            state.setReceiver(SingleMapNodeProperty.keyReceiver);
         } else if (ea.matches(SingleMapNodeProperty.this.valueTag)) {
            state.setLoader(SingleMapNodeProperty.this.valueLoader);
            state.setReceiver(SingleMapNodeProperty.valueReceiver);
         } else {
            super.childElement(state, ea);
         }
      }

      public Collection<QName> getExpectedChildElements() {
         return Arrays.asList(SingleMapNodeProperty.this.keyTag.toQName(), SingleMapNodeProperty.this.valueTag.toQName());
      }
   };
   private static final Receiver keyReceiver = new SingleMapNodeProperty.ReceiverImpl(0);
   private static final Receiver valueReceiver = new SingleMapNodeProperty.ReceiverImpl(1);

   public SingleMapNodeProperty(JAXBContextImpl context, RuntimeMapPropertyInfo prop) {
      super(context, prop);
      this.acc = prop.getAccessor().optimize(context);
      this.tagName = context.nameBuilder.createElementName(prop.getXmlName());
      this.entryTag = context.nameBuilder.createElementName("", "entry");
      this.keyTag = context.nameBuilder.createElementName("", "key");
      this.valueTag = context.nameBuilder.createElementName("", "value");
      this.nillable = prop.isCollectionNillable();
      this.keyBeanInfo = context.getOrCreate((RuntimeTypeInfo)prop.getKeyType());
      this.valueBeanInfo = context.getOrCreate((RuntimeTypeInfo)prop.getValueType());
      Class<ValueT> sig = (Class)Utils.REFLECTION_NAVIGATOR.erasure(prop.getRawType());
      this.mapImplClass = ClassFactory.inferImplClass(sig, knownImplClasses);
   }

   public void reset(BeanT bean) throws AccessorException {
      this.acc.set(bean, (Object)null);
   }

   public String getIdValue(BeanT bean) {
      return null;
   }

   public PropertyKind getKind() {
      return PropertyKind.MAP;
   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
      this.keyLoader = this.keyBeanInfo.getLoader(chain.context, true);
      this.valueLoader = this.valueBeanInfo.getLoader(chain.context, true);
      handlers.put((Name)this.tagName, new ChildLoader(this.itemsLoader, (Receiver)null));
   }

   public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
      ValueT v = (Map)this.acc.get(o);
      if (v != null) {
         this.bareStartTag(w, this.tagName, v);

         for(Iterator var5 = v.entrySet().iterator(); var5.hasNext(); w.endElement()) {
            Map.Entry e = (Map.Entry)var5.next();
            this.bareStartTag(w, this.entryTag, (Object)null);
            Object key = e.getKey();
            if (key != null) {
               w.startElement(this.keyTag, key);
               w.childAsXsiType(key, this.fieldName, this.keyBeanInfo, false);
               w.endElement();
            }

            Object value = e.getValue();
            if (value != null) {
               w.startElement(this.valueTag, value);
               w.childAsXsiType(value, this.fieldName, this.valueBeanInfo, false);
               w.endElement();
            }
         }

         w.endElement();
      } else if (this.nillable) {
         w.startElement(this.tagName, (Object)null);
         w.writeXsiNilTrue();
         w.endElement();
      }

   }

   private void bareStartTag(XMLSerializer w, Name tagName, Object peer) throws IOException, XMLStreamException, SAXException {
      w.startElement(tagName, peer);
      w.endNamespaceDecls(peer);
      w.endAttributes();
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      return this.tagName.equals(nsUri, localName) ? this.acc : null;
   }

   private static final class ReceiverImpl implements Receiver {
      private final int index;

      public ReceiverImpl(int index) {
         this.index = index;
      }

      public void receive(UnmarshallingContext.State state, Object o) {
         ((Object[])((Object[])state.getTarget()))[this.index] = o;
      }
   }
}
