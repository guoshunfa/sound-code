package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class ArrayERProperty<BeanT, ListT, ItemT> extends ArrayProperty<BeanT, ListT, ItemT> {
   protected final Name wrapperTagName;
   protected final boolean isWrapperNillable;

   protected ArrayERProperty(JAXBContextImpl grammar, RuntimePropertyInfo prop, QName tagName, boolean isWrapperNillable) {
      super(grammar, prop);
      if (tagName == null) {
         this.wrapperTagName = null;
      } else {
         this.wrapperTagName = grammar.nameBuilder.createElementName(tagName);
      }

      this.isWrapperNillable = isWrapperNillable;
   }

   public final void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
      ListT list = this.acc.get(o);
      if (list != null) {
         if (this.wrapperTagName != null) {
            w.startElement(this.wrapperTagName, (Object)null);
            w.endNamespaceDecls(list);
            w.endAttributes();
         }

         this.serializeListBody(o, w, list);
         if (this.wrapperTagName != null) {
            w.endElement();
         }
      } else if (this.isWrapperNillable) {
         w.startElement(this.wrapperTagName, (Object)null);
         w.writeXsiNilTrue();
         w.endElement();
      }

   }

   protected abstract void serializeListBody(BeanT var1, XMLSerializer var2, ListT var3) throws IOException, XMLStreamException, SAXException, AccessorException;

   protected abstract void createBodyUnmarshaller(UnmarshallerChain var1, QNameMap<ChildLoader> var2);

   public final void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> loaders) {
      if (this.wrapperTagName != null) {
         UnmarshallerChain c = new UnmarshallerChain(chain.context);
         QNameMap<ChildLoader> m = new QNameMap();
         this.createBodyUnmarshaller(c, m);
         Loader loader = new ArrayERProperty.ItemsLoader(this.acc, this.lister, m);
         if (this.isWrapperNillable || chain.context.allNillable) {
            loader = new XsiNilLoader((Loader)loader);
         }

         loaders.put((Name)this.wrapperTagName, new ChildLoader((Loader)loader, (Receiver)null));
      } else {
         this.createBodyUnmarshaller(chain, loaders);
      }

   }

   protected final class ReceiverImpl implements Receiver {
      private final int offset;

      protected ReceiverImpl(int offset) {
         this.offset = offset;
      }

      public void receive(UnmarshallingContext.State state, Object o) throws SAXException {
         state.getContext().getScope(this.offset).add(ArrayERProperty.this.acc, ArrayERProperty.this.lister, o);
      }
   }

   private static final class ItemsLoader extends Loader {
      private final Accessor acc;
      private final Lister lister;
      private final QNameMap<ChildLoader> children;

      public ItemsLoader(Accessor acc, Lister lister, QNameMap<ChildLoader> children) {
         super(false);
         this.acc = acc;
         this.lister = lister;
         this.children = children;
      }

      public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         UnmarshallingContext context = state.getContext();
         context.startScope(1);
         state.setTarget(state.getPrev().getTarget());
         context.getScope(0).start(this.acc, this.lister);
      }

      public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         ChildLoader child = (ChildLoader)this.children.get(ea.uri, ea.local);
         if (child == null) {
            child = (ChildLoader)this.children.get(StructureLoaderBuilder.CATCH_ALL);
         }

         if (child == null) {
            super.childElement(state, ea);
         } else {
            state.setLoader(child.loader);
            state.setReceiver(child.receiver);
         }
      }

      public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
         state.getContext().endScope(1);
      }

      public Collection<QName> getExpectedChildElements() {
         return this.children.keySet();
      }
   }
}
