package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.io.PrintWriter;

class XPointerErrorHandler implements XMLErrorHandler {
   protected PrintWriter fOut;

   public XPointerErrorHandler() {
      this(new PrintWriter(System.err));
   }

   public XPointerErrorHandler(PrintWriter out) {
      this.fOut = out;
   }

   public void warning(String domain, String key, XMLParseException ex) throws XNIException {
      this.printError("Warning", ex);
   }

   public void error(String domain, String key, XMLParseException ex) throws XNIException {
      this.printError("Error", ex);
   }

   public void fatalError(String domain, String key, XMLParseException ex) throws XNIException {
      this.printError("Fatal Error", ex);
      throw ex;
   }

   private void printError(String type, XMLParseException ex) {
      this.fOut.print("[");
      this.fOut.print(type);
      this.fOut.print("] ");
      String systemId = ex.getExpandedSystemId();
      if (systemId != null) {
         int index = systemId.lastIndexOf(47);
         if (index != -1) {
            systemId = systemId.substring(index + 1);
         }

         this.fOut.print(systemId);
      }

      this.fOut.print(':');
      this.fOut.print(ex.getLineNumber());
      this.fOut.print(':');
      this.fOut.print(ex.getColumnNumber());
      this.fOut.print(": ");
      this.fOut.print(ex.getMessage());
      this.fOut.println();
      this.fOut.flush();
   }
}
