package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import org.xml.sax.SAXException;

public class TextLoader extends Loader {
   private final Transducer xducer;

   public TextLoader(Transducer xducer) {
      super(true);
      this.xducer = xducer;
   }

   public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
      try {
         state.setTarget(this.xducer.parse(text));
      } catch (AccessorException var4) {
         handleGenericException(var4, true);
      } catch (RuntimeException var5) {
         handleParseConversionException(state, var5);
      }

   }
}
