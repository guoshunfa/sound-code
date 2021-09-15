package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class AttributeProperty<BeanT> extends PropertyImpl<BeanT> implements Comparable<AttributeProperty> {
   public final Name attName;
   public final TransducedAccessor<BeanT> xacc;
   private final Accessor acc;

   public AttributeProperty(JAXBContextImpl context, RuntimeAttributePropertyInfo prop) {
      super(context, prop);
      this.attName = context.nameBuilder.createAttributeName(prop.getXmlName());
      this.xacc = TransducedAccessor.get(context, prop);
      this.acc = prop.getAccessor();
   }

   public void serializeAttributes(BeanT o, XMLSerializer w) throws SAXException, AccessorException, IOException, XMLStreamException {
      CharSequence value = this.xacc.print(o);
      if (value != null) {
         w.attribute(this.attName, value.toString());
      }

   }

   public void serializeURIs(BeanT o, XMLSerializer w) throws AccessorException, SAXException {
      this.xacc.declareNamespace(o, w);
   }

   public boolean hasSerializeURIAction() {
      return this.xacc.useNamespace();
   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chainElem, QNameMap<ChildLoader> handlers) {
      throw new IllegalStateException();
   }

   public PropertyKind getKind() {
      return PropertyKind.ATTRIBUTE;
   }

   public void reset(BeanT o) throws AccessorException {
      this.acc.set(o, (Object)null);
   }

   public String getIdValue(BeanT bean) throws AccessorException, SAXException {
      return this.xacc.print(bean).toString();
   }

   public int compareTo(AttributeProperty that) {
      return this.attName.compareTo(that.attName);
   }
}
