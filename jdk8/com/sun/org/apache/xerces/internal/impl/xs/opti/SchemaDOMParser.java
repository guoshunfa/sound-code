package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import org.w3c.dom.Document;

public class SchemaDOMParser extends DefaultXMLDocumentHandler {
   public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   public static final String GENERATE_SYNTHETIC_ANNOTATION = "http://apache.org/xml/features/generate-synthetic-annotations";
   protected XMLLocator fLocator;
   protected NamespaceContext fNamespaceContext = null;
   SchemaDOM schemaDOM;
   XMLParserConfiguration config;
   private ElementImpl fCurrentAnnotationElement;
   private int fAnnotationDepth = -1;
   private int fInnerAnnotationDepth = -1;
   private int fDepth = -1;
   XMLErrorReporter fErrorReporter;
   private boolean fGenerateSyntheticAnnotation = false;
   private SchemaDOMParser.BooleanStack fHasNonSchemaAttributes = new SchemaDOMParser.BooleanStack();
   private SchemaDOMParser.BooleanStack fSawAnnotation = new SchemaDOMParser.BooleanStack();
   private XMLAttributes fEmptyAttr = new XMLAttributesImpl();

   public SchemaDOMParser(XMLParserConfiguration config) {
      this.config = config;
      config.setDocumentHandler(this);
      config.setDTDHandler(this);
      config.setDTDContentModelHandler(this);
   }

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.fErrorReporter = (XMLErrorReporter)this.config.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.fGenerateSyntheticAnnotation = this.config.getFeature("http://apache.org/xml/features/generate-synthetic-annotations");
      this.fHasNonSchemaAttributes.clear();
      this.fSawAnnotation.clear();
      this.schemaDOM = new SchemaDOM();
      this.fCurrentAnnotationElement = null;
      this.fAnnotationDepth = -1;
      this.fInnerAnnotationDepth = -1;
      this.fDepth = -1;
      this.fLocator = locator;
      this.fNamespaceContext = namespaceContext;
      this.schemaDOM.setDocumentURI(locator.getExpandedSystemId());
   }

   public void endDocument(Augmentations augs) throws XNIException {
   }

   public void comment(XMLString text, Augmentations augs) throws XNIException {
      if (this.fAnnotationDepth > -1) {
         this.schemaDOM.comment(text);
      }

   }

   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
      if (this.fAnnotationDepth > -1) {
         this.schemaDOM.processingInstruction(target, data);
      }

   }

   public void characters(XMLString text, Augmentations augs) throws XNIException {
      if (this.fInnerAnnotationDepth == -1) {
         for(int i = text.offset; i < text.offset + text.length; ++i) {
            if (!XMLChar.isSpace(text.ch[i])) {
               String txt = new String(text.ch, i, text.length + text.offset - i);
               this.fErrorReporter.reportError(this.fLocator, "http://www.w3.org/TR/xml-schema-1", "s4s-elt-character", new Object[]{txt}, (short)1);
               break;
            }
         }
      } else {
         this.schemaDOM.characters(text);
      }

   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      ++this.fDepth;
      if (this.fAnnotationDepth == -1) {
         if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart == SchemaSymbols.ELT_ANNOTATION) {
            if (this.fGenerateSyntheticAnnotation) {
               if (this.fSawAnnotation.size() > 0) {
                  this.fSawAnnotation.pop();
               }

               this.fSawAnnotation.push(true);
            }

            this.fAnnotationDepth = this.fDepth;
            this.schemaDOM.startAnnotation(element, attributes, this.fNamespaceContext);
            this.fCurrentAnnotationElement = this.schemaDOM.startElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
            return;
         }

         if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
            this.fSawAnnotation.push(false);
            this.fHasNonSchemaAttributes.push(this.hasNonSchemaAttributes(element, attributes));
         }
      } else {
         if (this.fDepth != this.fAnnotationDepth + 1) {
            this.schemaDOM.startAnnotationElement(element, attributes);
            return;
         }

         this.fInnerAnnotationDepth = this.fDepth;
         this.schemaDOM.startAnnotationElement(element, attributes);
      }

      this.schemaDOM.startElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
   }

   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      if (this.fGenerateSyntheticAnnotation && this.fAnnotationDepth == -1 && element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart != SchemaSymbols.ELT_ANNOTATION && this.hasNonSchemaAttributes(element, attributes)) {
         this.schemaDOM.startElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
         attributes.removeAllAttributes();
         String schemaPrefix = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
         String annRawName = schemaPrefix.length() == 0 ? SchemaSymbols.ELT_ANNOTATION : schemaPrefix + ':' + SchemaSymbols.ELT_ANNOTATION;
         this.schemaDOM.startAnnotation(annRawName, attributes, this.fNamespaceContext);
         String elemRawName = schemaPrefix.length() == 0 ? SchemaSymbols.ELT_DOCUMENTATION : schemaPrefix + ':' + SchemaSymbols.ELT_DOCUMENTATION;
         this.schemaDOM.startAnnotationElement(elemRawName, attributes);
         this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
         this.schemaDOM.endSyntheticAnnotationElement(elemRawName, false);
         this.schemaDOM.endSyntheticAnnotationElement(annRawName, true);
         this.schemaDOM.endElement();
      } else {
         if (this.fAnnotationDepth == -1) {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart == SchemaSymbols.ELT_ANNOTATION) {
               this.schemaDOM.startAnnotation(element, attributes, this.fNamespaceContext);
            }
         } else {
            this.schemaDOM.startAnnotationElement(element, attributes);
         }

         ElementImpl newElem = this.schemaDOM.emptyElement(element, attributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
         if (this.fAnnotationDepth == -1) {
            if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && element.localpart == SchemaSymbols.ELT_ANNOTATION) {
               this.schemaDOM.endAnnotation(element, newElem);
            }
         } else {
            this.schemaDOM.endAnnotationElement(element);
         }

      }
   }

   public void endElement(QName element, Augmentations augs) throws XNIException {
      if (this.fAnnotationDepth > -1) {
         if (this.fInnerAnnotationDepth == this.fDepth) {
            this.fInnerAnnotationDepth = -1;
            this.schemaDOM.endAnnotationElement(element);
            this.schemaDOM.endElement();
         } else if (this.fAnnotationDepth == this.fDepth) {
            this.fAnnotationDepth = -1;
            this.schemaDOM.endAnnotation(element, this.fCurrentAnnotationElement);
            this.schemaDOM.endElement();
         } else {
            this.schemaDOM.endAnnotationElement(element);
         }
      } else {
         if (element.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
            boolean value = this.fHasNonSchemaAttributes.pop();
            boolean sawann = this.fSawAnnotation.pop();
            if (value && !sawann) {
               String schemaPrefix = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
               String annRawName = schemaPrefix.length() == 0 ? SchemaSymbols.ELT_ANNOTATION : schemaPrefix + ':' + SchemaSymbols.ELT_ANNOTATION;
               this.schemaDOM.startAnnotation(annRawName, this.fEmptyAttr, this.fNamespaceContext);
               String elemRawName = schemaPrefix.length() == 0 ? SchemaSymbols.ELT_DOCUMENTATION : schemaPrefix + ':' + SchemaSymbols.ELT_DOCUMENTATION;
               this.schemaDOM.startAnnotationElement(elemRawName, this.fEmptyAttr);
               this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
               this.schemaDOM.endSyntheticAnnotationElement(elemRawName, false);
               this.schemaDOM.endSyntheticAnnotationElement(annRawName, true);
            }
         }

         this.schemaDOM.endElement();
      }

      --this.fDepth;
   }

   private boolean hasNonSchemaAttributes(QName element, XMLAttributes attributes) {
      int length = attributes.getLength();

      for(int i = 0; i < length; ++i) {
         String uri = attributes.getURI(i);
         if (uri != null && uri != SchemaSymbols.URI_SCHEMAFORSCHEMA && uri != NamespaceContext.XMLNS_URI && (uri != NamespaceContext.XML_URI || attributes.getQName(i) != SchemaSymbols.ATT_XML_LANG || element.localpart != SchemaSymbols.ELT_SCHEMA)) {
            return true;
         }
      }

      return false;
   }

   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
      if (this.fAnnotationDepth != -1) {
         this.schemaDOM.characters(text);
      }

   }

   public void startCDATA(Augmentations augs) throws XNIException {
      if (this.fAnnotationDepth != -1) {
         this.schemaDOM.startAnnotationCDATA();
      }

   }

   public void endCDATA(Augmentations augs) throws XNIException {
      if (this.fAnnotationDepth != -1) {
         this.schemaDOM.endAnnotationCDATA();
      }

   }

   public Document getDocument() {
      return this.schemaDOM;
   }

   public void setFeature(String featureId, boolean state) {
      this.config.setFeature(featureId, state);
   }

   public boolean getFeature(String featureId) {
      return this.config.getFeature(featureId);
   }

   public void setProperty(String propertyId, Object value) {
      this.config.setProperty(propertyId, value);
   }

   public Object getProperty(String propertyId) {
      return this.config.getProperty(propertyId);
   }

   public void setEntityResolver(XMLEntityResolver er) {
      this.config.setEntityResolver(er);
   }

   public void parse(XMLInputSource inputSource) throws IOException {
      this.config.parse(inputSource);
   }

   public void reset() {
      ((SchemaParsingConfig)this.config).reset();
   }

   public void resetNodePool() {
      ((SchemaParsingConfig)this.config).resetNodePool();
   }

   private static final class BooleanStack {
      private int fDepth;
      private boolean[] fData;

      public BooleanStack() {
      }

      public int size() {
         return this.fDepth;
      }

      public void push(boolean value) {
         this.ensureCapacity(this.fDepth + 1);
         this.fData[this.fDepth++] = value;
      }

      public boolean pop() {
         return this.fData[--this.fDepth];
      }

      public void clear() {
         this.fDepth = 0;
      }

      private void ensureCapacity(int size) {
         if (this.fData == null) {
            this.fData = new boolean[32];
         } else if (this.fData.length <= size) {
            boolean[] newdata = new boolean[this.fData.length * 2];
            System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
            this.fData = newdata;
         }

      }
   }
}
