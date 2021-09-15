package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;

public final class FastInfosetStreamReaderRecyclable extends StAXDocumentParser implements XMLStreamReaderFactory.RecycleAware {
   private static final FastInfosetStreamReaderFactory READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();

   public FastInfosetStreamReaderRecyclable() {
   }

   public FastInfosetStreamReaderRecyclable(InputStream in) {
      super(in);
   }

   public void onRecycled() {
      READER_FACTORY.doRecycle(this);
   }
}
