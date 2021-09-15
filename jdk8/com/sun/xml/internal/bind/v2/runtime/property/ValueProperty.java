package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ValuePropertyLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ValueProperty<BeanT> extends PropertyImpl<BeanT> {
   private final TransducedAccessor<BeanT> xacc;
   private final Accessor<BeanT, ?> acc;

   public ValueProperty(JAXBContextImpl context, RuntimeValuePropertyInfo prop) {
      super(context, prop);
      this.xacc = TransducedAccessor.get(context, prop);
      this.acc = prop.getAccessor();
   }

   public final void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
      if (this.xacc.hasValue(o)) {
         this.xacc.writeText(w, o, this.fieldName);
      }

   }

   public void serializeURIs(BeanT o, XMLSerializer w) throws SAXException, AccessorException {
      this.xacc.declareNamespace(o, w);
   }

   public boolean hasSerializeURIAction() {
      return this.xacc.useNamespace();
   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chainElem, QNameMap<ChildLoader> handlers) {
      handlers.put((QName)StructureLoaderBuilder.TEXT_HANDLER, new ChildLoader(new ValuePropertyLoader(this.xacc), (Receiver)null));
   }

   public PropertyKind getKind() {
      return PropertyKind.VALUE;
   }

   public void reset(BeanT o) throws AccessorException {
      this.acc.set(o, (Object)null);
   }

   public String getIdValue(BeanT bean) throws AccessorException, SAXException {
      return this.xacc.print(bean).toString();
   }
}
