package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;

public class DOMErrorImpl implements DOMError {
   public short fSeverity = 1;
   public String fMessage = null;
   public DOMLocatorImpl fLocator = new DOMLocatorImpl();
   public Exception fException = null;
   public String fType;
   public Object fRelatedData;

   public DOMErrorImpl() {
   }

   public DOMErrorImpl(short severity, XMLParseException exception) {
      this.fSeverity = severity;
      this.fException = exception;
      this.fLocator = this.createDOMLocator(exception);
   }

   public short getSeverity() {
      return this.fSeverity;
   }

   public String getMessage() {
      return this.fMessage;
   }

   public DOMLocator getLocation() {
      return this.fLocator;
   }

   private DOMLocatorImpl createDOMLocator(XMLParseException exception) {
      return new DOMLocatorImpl(exception.getLineNumber(), exception.getColumnNumber(), exception.getCharacterOffset(), exception.getExpandedSystemId());
   }

   public Object getRelatedException() {
      return this.fException;
   }

   public void reset() {
      this.fSeverity = 1;
      this.fException = null;
   }

   public String getType() {
      return this.fType;
   }

   public Object getRelatedData() {
      return this.fRelatedData;
   }
}
