package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

interface DOMDocumentHandler extends XMLDocumentHandler {
   void setDOMResult(DOMResult var1);

   void doctypeDecl(DocumentType var1) throws XNIException;

   void characters(Text var1) throws XNIException;

   void cdata(CDATASection var1) throws XNIException;

   void comment(Comment var1) throws XNIException;

   void processingInstruction(ProcessingInstruction var1) throws XNIException;

   void setIgnoringCharacters(boolean var1);
}
