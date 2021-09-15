package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import javax.xml.stream.XMLStreamException;

public final class StAXExStreamWriterOutput extends XMLStreamWriterOutput {
   private final XMLStreamWriterEx out;

   public StAXExStreamWriterOutput(XMLStreamWriterEx out) {
      super(out, NoEscapeHandler.theInstance);
      this.out = out;
   }

   public void text(Pcdata value, boolean needsSeparatingWhitespace) throws XMLStreamException {
      if (needsSeparatingWhitespace) {
         this.out.writeCharacters(" ");
      }

      if (!(value instanceof Base64Data)) {
         this.out.writeCharacters(value.toString());
      } else {
         Base64Data v = (Base64Data)value;
         this.out.writeBinary(v.getDataHandler());
      }

   }
}
