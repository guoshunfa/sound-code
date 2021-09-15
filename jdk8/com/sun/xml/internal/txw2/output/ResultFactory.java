package com.sun.xml.internal.txw2.output;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

public abstract class ResultFactory {
   private ResultFactory() {
   }

   public static XmlSerializer createSerializer(Result result) {
      if (result instanceof SAXResult) {
         return new SaxSerializer((SAXResult)result);
      } else if (result instanceof DOMResult) {
         return new DomSerializer((DOMResult)result);
      } else if (result instanceof StreamResult) {
         return new StreamSerializer((StreamResult)result);
      } else if (result instanceof TXWResult) {
         return new TXWSerializer(((TXWResult)result).getWriter());
      } else {
         throw new UnsupportedOperationException("Unsupported Result type: " + result.getClass().getName());
      }
   }
}
