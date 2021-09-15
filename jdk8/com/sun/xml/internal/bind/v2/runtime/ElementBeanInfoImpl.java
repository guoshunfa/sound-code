package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Discarder;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ElementBeanInfoImpl extends JaxBeanInfo<JAXBElement> {
   private Loader loader;
   private final Property property;
   private final QName tagName;
   public final Class expectedType;
   private final Class scope;
   private final Constructor<? extends JAXBElement> constructor;

   ElementBeanInfoImpl(JAXBContextImpl grammar, RuntimeElementInfo rei) {
      super(grammar, rei, rei.getType(), true, false, true);
      this.property = PropertyFactory.create(grammar, rei.getProperty());
      this.tagName = rei.getElementName();
      this.expectedType = (Class)Utils.REFLECTION_NAVIGATOR.erasure(rei.getContentInMemoryType());
      this.scope = rei.getScope() == null ? JAXBElement.GlobalScope.class : (Class)rei.getScope().getClazz();
      Class type = (Class)Utils.REFLECTION_NAVIGATOR.erasure(rei.getType());
      if (type == JAXBElement.class) {
         this.constructor = null;
      } else {
         try {
            this.constructor = type.getConstructor(this.expectedType);
         } catch (NoSuchMethodException var6) {
            NoSuchMethodError x = new NoSuchMethodError("Failed to find the constructor for " + type + " with " + this.expectedType);
            x.initCause(var6);
            throw x;
         }
      }

   }

   protected ElementBeanInfoImpl(final JAXBContextImpl grammar) {
      super(grammar, (RuntimeTypeInfo)null, JAXBElement.class, true, false, true);
      this.tagName = null;
      this.expectedType = null;
      this.scope = null;
      this.constructor = null;
      this.property = new Property<JAXBElement>() {
         public void reset(JAXBElement o) {
            throw new UnsupportedOperationException();
         }

         public void serializeBody(JAXBElement e, XMLSerializer target, Object outerPeer) throws SAXException, IOException, XMLStreamException {
            Class scope = e.getScope();
            if (e.isGlobalScope()) {
               scope = null;
            }

            QName n = e.getName();
            ElementBeanInfoImpl bi = grammar.getElement(scope, n);
            if (bi == null) {
               JaxBeanInfo tbi;
               try {
                  tbi = grammar.getBeanInfo(e.getDeclaredType(), true);
               } catch (JAXBException var10) {
                  target.reportError((String)null, var10);
                  return;
               }

               Object value = e.getValue();
               target.startElement(n.getNamespaceURI(), n.getLocalPart(), n.getPrefix(), (Object)null);
               if (value == null) {
                  target.writeXsiNilTrue();
               } else {
                  target.childAsXsiType(value, "value", tbi, false);
               }

               target.endElement();
            } else {
               try {
                  bi.property.serializeBody(e, target, e);
               } catch (AccessorException var9) {
                  target.reportError((String)null, var9);
               }
            }

         }

         public void serializeURIs(JAXBElement o, XMLSerializer target) {
         }

         public boolean hasSerializeURIAction() {
            return false;
         }

         public String getIdValue(JAXBElement o) {
            return null;
         }

         public PropertyKind getKind() {
            return PropertyKind.ELEMENT;
         }

         public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
         }

         public Accessor getElementPropertyAccessor(String nsUri, String localName) {
            throw new UnsupportedOperationException();
         }

         public void wrapUp() {
         }

         public RuntimePropertyInfo getInfo() {
            return ElementBeanInfoImpl.this.property.getInfo();
         }

         public boolean isHiddenByOverride() {
            return false;
         }

         public void setHiddenByOverride(boolean hidden) {
            throw new UnsupportedOperationException("Not supported on jaxbelements.");
         }

         public String getFieldName() {
            return null;
         }
      };
   }

   public String getElementNamespaceURI(JAXBElement e) {
      return e.getName().getNamespaceURI();
   }

   public String getElementLocalName(JAXBElement e) {
      return e.getName().getLocalPart();
   }

   public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
      if (this.loader == null) {
         UnmarshallerChain c = new UnmarshallerChain(context);
         QNameMap<ChildLoader> result = new QNameMap();
         this.property.buildChildElementUnmarshallers(c, result);
         if (result.size() == 1) {
            this.loader = new ElementBeanInfoImpl.IntercepterLoader(((ChildLoader)result.getOne().getValue()).loader);
         } else {
            this.loader = Discarder.INSTANCE;
         }
      }

      return this.loader;
   }

   public final JAXBElement createInstance(UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      return this.createInstanceFromValue((Object)null);
   }

   public final JAXBElement createInstanceFromValue(Object o) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      return this.constructor == null ? new JAXBElement(this.tagName, this.expectedType, this.scope, o) : (JAXBElement)this.constructor.newInstance(o);
   }

   public boolean reset(JAXBElement e, UnmarshallingContext context) {
      e.setValue((Object)null);
      return true;
   }

   public String getId(JAXBElement e, XMLSerializer target) {
      Object o = e.getValue();
      return o instanceof String ? (String)o : null;
   }

   public void serializeBody(JAXBElement element, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      try {
         this.property.serializeBody(element, target, (Object)null);
      } catch (AccessorException var4) {
         target.reportError((String)null, var4);
      }

   }

   public void serializeRoot(JAXBElement e, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
      this.serializeBody(e, target);
   }

   public void serializeAttributes(JAXBElement e, XMLSerializer target) {
   }

   public void serializeURIs(JAXBElement e, XMLSerializer target) {
   }

   public final Transducer<JAXBElement> getTransducer() {
      return null;
   }

   public void wrapUp() {
      super.wrapUp();
      this.property.wrapUp();
   }

   public void link(JAXBContextImpl grammar) {
      super.link(grammar);
      this.getLoader(grammar, true);
   }

   private final class IntercepterLoader extends Loader implements Intercepter {
      private final Loader core;

      public IntercepterLoader(Loader core) {
         this.core = core;
      }

      public final void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         state.setLoader(this.core);
         state.setIntercepter(this);
         UnmarshallingContext context = state.getContext();
         Object child = context.getOuterPeer();
         if (child != null && ElementBeanInfoImpl.this.jaxbType != child.getClass()) {
            child = null;
         }

         if (child != null) {
            ElementBeanInfoImpl.this.reset((JAXBElement)child, context);
         }

         if (child == null) {
            child = context.createInstance((JaxBeanInfo)ElementBeanInfoImpl.this);
         }

         this.fireBeforeUnmarshal(ElementBeanInfoImpl.this, child, state);
         context.recordOuterPeer(child);
         UnmarshallingContext.State p = state.getPrev();
         p.setBackup(p.getTarget());
         p.setTarget(child);
         this.core.startElement(state, ea);
      }

      public Object intercept(UnmarshallingContext.State state, Object o) throws SAXException {
         JAXBElement e = (JAXBElement)state.getTarget();
         state.setTarget(state.getBackup());
         state.setBackup((Object)null);
         if (state.isNil()) {
            e.setNil(true);
            state.setNil(false);
         }

         if (o != null) {
            e.setValue(o);
         }

         this.fireAfterUnmarshal(ElementBeanInfoImpl.this, e, state);
         return e;
      }
   }
}
