package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListTransducedAccessorImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ListElementProperty<BeanT, ListT, ItemT> extends ArrayProperty<BeanT, ListT, ItemT> {
   private final Name tagName;
   private final String defaultValue;
   private final TransducedAccessor<BeanT> xacc;

   public ListElementProperty(JAXBContextImpl grammar, RuntimeElementPropertyInfo prop) {
      super(grammar, prop);

      assert prop.isValueList();

      assert prop.getTypes().size() == 1;

      RuntimeTypeRef ref = (RuntimeTypeRef)prop.getTypes().get(0);
      this.tagName = grammar.nameBuilder.createElementName(ref.getTagName());
      this.defaultValue = ref.getDefaultValue();
      Transducer xducer = ref.getTransducer();
      this.xacc = new ListTransducedAccessorImpl(xducer, this.acc, this.lister);
   }

   public PropertyKind getKind() {
      return PropertyKind.ELEMENT;
   }

   public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
      Loader l = new LeafPropertyLoader(this.xacc);
      Loader l = new DefaultValueLoaderDecorator(l, this.defaultValue);
      handlers.put((Name)this.tagName, new ChildLoader(l, (Receiver)null));
   }

   public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
      ListT list = this.acc.get(o);
      if (list != null) {
         if (this.xacc.useNamespace()) {
            w.startElement(this.tagName, (Object)null);
            this.xacc.declareNamespace(o, w);
            w.endNamespaceDecls(list);
            w.endAttributes();
            this.xacc.writeText(w, o, this.fieldName);
            w.endElement();
         } else {
            this.xacc.writeLeafElement(w, this.tagName, o, this.fieldName);
         }
      }

   }

   public Accessor getElementPropertyAccessor(String nsUri, String localName) {
      return this.tagName != null && this.tagName.equals(nsUri, localName) ? this.acc : null;
   }
}
