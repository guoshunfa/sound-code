package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import org.xml.sax.SAXException;

public class LeafPropertyLoader extends Loader {
   private final TransducedAccessor xacc;

   public LeafPropertyLoader(TransducedAccessor xacc) {
      super(true);
      this.xacc = xacc;
   }

   public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
      try {
         this.xacc.parse(state.getPrev().getTarget(), text);
      } catch (AccessorException var4) {
         handleGenericException(var4, true);
      } catch (RuntimeException var5) {
         handleParseConversionException(state, var5);
      }

   }
}
