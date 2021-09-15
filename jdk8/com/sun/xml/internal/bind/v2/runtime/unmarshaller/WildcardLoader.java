package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import javax.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

public final class WildcardLoader extends ProxyLoader {
   private final DomLoader dom;
   private final WildcardMode mode;

   public WildcardLoader(DomHandler dom, WildcardMode mode) {
      this.dom = new DomLoader(dom);
      this.mode = mode;
   }

   protected Loader selectLoader(UnmarshallingContext.State state, TagName tag) throws SAXException {
      UnmarshallingContext context = state.getContext();
      if (this.mode.allowTypedObject) {
         Loader l = context.selectRootLoader(state, tag);
         if (l != null) {
            return l;
         }
      }

      return (Loader)(this.mode.allowDom ? this.dom : Discarder.INSTANCE);
   }
}
