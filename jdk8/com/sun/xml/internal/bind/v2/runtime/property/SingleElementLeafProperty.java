package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyXsiLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementLeafProperty<BeanT> extends PropertyImpl<BeanT> {
   private final Name tagName;
   private final boolean nillable;
   private final Accessor acc;
   private final String defaultValue;
   private final TransducedAccessor<BeanT> xacc;
   private final boolean improvedXsiTypeHandling;
   private final boolean idRef;

   public SingleElementLeafProperty(JAXBContextImpl context, RuntimeElementPropertyInfo prop) {
      super(context, prop);
      RuntimeTypeRef ref = (RuntimeTypeRef)prop.getTypes().get(0);
      this.tagName = context.nameBuilder.createElementName(ref.getTagName());

      assert this.tagName != null;

      this.nillable = ref.isNillable();
      this.defaultValue = ref.getDefaultValue();
      this.acc = prop.getAccessor().optimize(context);
      this.xacc = TransducedAccessor.get(context, ref);

      assert this.xacc != null;

      this.improvedXsiTypeHandling = context.improvedXsiTypeHandling;
      this.idRef = ref.getSource().id() == ID.IDREF;
   }

   public void reset(BeanT o) throws AccessorException {
      this.acc.set(o, (Object)null);
   }

   public String getIdValue(BeanT bean) throws AccessorException, SAXException {
      return this.xacc.print(bean).toString();
   }

   public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
      boolean hasValue = this.xacc.hasValue(o);
      Object obj = null;

      try {
         obj = this.acc.getUnadapted(o);
      } catch (AccessorException var7) {
      }

      Class valueType = this.acc.getValueType();
      if (this.xsiTypeNeeded(o, w, obj, valueType)) {
         w.startElement(this.tagName, outerPeer);
         w.childAsXsiType(obj, this.fieldName, w.grammar.getBeanInfo(valueType), false);
         w.endElement();
      } else if (hasValue) {
         this.xacc.writeLeafElement(w, this.tagName, o, this.fieldName);
      } else if (this.nillable) {
         w.startElement(this.tagName, (Object)null);
         w.writeXsiNilTrue();
         w.endElement();
      }

   }

   private boolean xsiTypeNeeded(BeanT bean, XMLSerializer w, Object value, Class valueTypeClass) {
      if (!this.improvedXsiTypeHandling) {
         return false;
      } else if (this.acc.isAdapted()) {
         return false;
      } else if (value == null) {
         return false;
      } else if (value.getClass().equals(valueTypeClass)) {
         return false;
      } else if (this.idRef) {
         return false;
      } else if (valueTypeClass.isPrimitive()) {
         return false;
      } else {
         return this.acc.isValueTypeAbstractable() || this.isNillableAbstract(bean, w.grammar, value, valueTypeClass);
      }
   }

   private boolean isNillableAbstract(BeanT bean, JAXBContextImpl context, Object value, Class valueTypeClass) {
      if (!this.nillable) {
         return false;
      } else if (valueTypeClass != Object.class) {
         return false;
      } else if (bean.getClass() != JAXBElement.class) {
         return false;
      } else {
         JAXBElement jaxbElement = (JAXBElement)bean;
         Class valueClass = value.getClass();
         Class declaredTypeClass = jaxbElement.getDeclaredType();
         if (declaredTypeClass.equals(valueClass)) {
            return false;
         } else if (!declaredTypeClass.isAssignableFrom(valueClass)) {
            return false;
         } else {
            return !Modifier.isAbstract(declaredTypeClass.getModifiers()) ? false : this.acc.isAbstractable(declaredTypeClass);
         }
      }
   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
      Loader l = new LeafPropertyLoader(this.xacc);
      if (this.defaultValue != null) {
         l = new DefaultValueLoaderDecorator((Loader)l, this.defaultValue);
      }

      if (this.nillable || chain.context.allNillable) {
         l = new XsiNilLoader.Single((Loader)l, this.acc);
      }

      if (this.improvedXsiTypeHandling) {
         l = new LeafPropertyXsiLoader((Loader)l, this.xacc, this.acc);
      }

      handlers.put((Name)this.tagName, new ChildLoader((Loader)l, (Receiver)null));
   }

   public PropertyKind getKind() {
      return PropertyKind.ELEMENT;
   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      return this.tagName.equals(nsUri, localName) ? this.acc : null;
   }
}
