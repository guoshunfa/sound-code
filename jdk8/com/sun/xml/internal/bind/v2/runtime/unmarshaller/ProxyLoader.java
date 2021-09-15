package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public abstract class ProxyLoader extends Loader {
   public ProxyLoader() {
      super(false);
   }

   public final void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
      Loader loader = this.selectLoader(state, ea);
      state.setLoader(loader);
      loader.startElement(state, ea);
   }

   protected abstract Loader selectLoader(UnmarshallingContext.State var1, TagName var2) throws SAXException;

   public final void leaveElement(UnmarshallingContext.State state, TagName ea) {
      throw new IllegalStateException();
   }
}
