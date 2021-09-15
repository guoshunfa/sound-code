package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** @deprecated */
public class HTMLSerializer extends BaseMarkupSerializer {
   private boolean _xhtml;
   public static final String XHTMLNamespace = "http://www.w3.org/1999/xhtml";
   private String fUserXHTMLNamespace;

   protected HTMLSerializer(boolean xhtml, OutputFormat format) {
      super(format);
      this.fUserXHTMLNamespace = null;
      this._xhtml = xhtml;
   }

   public HTMLSerializer() {
      this(false, new OutputFormat("html", "ISO-8859-1", false));
   }

   public HTMLSerializer(OutputFormat format) {
      this(false, format != null ? format : new OutputFormat("html", "ISO-8859-1", false));
   }

   public HTMLSerializer(Writer writer, OutputFormat format) {
      this(false, format != null ? format : new OutputFormat("html", "ISO-8859-1", false));
      this.setOutputCharStream(writer);
   }

   public HTMLSerializer(OutputStream output, OutputFormat format) {
      this(false, format != null ? format : new OutputFormat("html", "ISO-8859-1", false));
      this.setOutputByteStream(output);
   }

   public void setOutputFormat(OutputFormat format) {
      super.setOutputFormat(format != null ? format : new OutputFormat("html", "ISO-8859-1", false));
   }

   public void setXHTMLNamespace(String newNamespace) {
      this.fUserXHTMLNamespace = newNamespace;
   }

   public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
      boolean addNSAttr = false;

      try {
         if (this._printer == null) {
            throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", (Object[])null));
         } else {
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
               if (!this._started) {
                  this.startDocument(localName != null && localName.length() != 0 ? localName : rawName);
               }
            } else {
               if (state.empty) {
                  this._printer.printText('>');
               }

               if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement)) {
                  this._printer.breakLine();
               }
            }

            boolean preserveSpace = state.preserveSpace;
            boolean hasNamespaceURI = namespaceURI != null && namespaceURI.length() != 0;
            if (rawName == null || rawName.length() == 0) {
               rawName = localName;
               if (hasNamespaceURI) {
                  String prefix = this.getPrefix(namespaceURI);
                  if (prefix != null && prefix.length() != 0) {
                     rawName = prefix + ":" + localName;
                  }
               }

               addNSAttr = true;
            }

            String htmlName;
            if (!hasNamespaceURI) {
               htmlName = rawName;
            } else if (!namespaceURI.equals("http://www.w3.org/1999/xhtml") && (this.fUserXHTMLNamespace == null || !this.fUserXHTMLNamespace.equals(namespaceURI))) {
               htmlName = null;
            } else {
               htmlName = localName;
            }

            this._printer.printText('<');
            if (this._xhtml) {
               this._printer.printText(rawName.toLowerCase(Locale.ENGLISH));
            } else {
               this._printer.printText(rawName);
            }

            this._printer.indent();
            String name;
            String value;
            if (attrs != null) {
               for(int i = 0; i < attrs.getLength(); ++i) {
                  this._printer.printSpace();
                  name = attrs.getQName(i).toLowerCase(Locale.ENGLISH);
                  value = attrs.getValue(i);
                  if (!this._xhtml && !hasNamespaceURI) {
                     if (value == null) {
                        value = "";
                     }

                     if (!this._format.getPreserveEmptyAttributes() && value.length() == 0) {
                        this._printer.printText(name);
                     } else if (HTMLdtd.isURI(rawName, name)) {
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this._printer.printText(this.escapeURI(value));
                        this._printer.printText('"');
                     } else if (HTMLdtd.isBoolean(rawName, name)) {
                        this._printer.printText(name);
                     } else {
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this.printEscaped(value);
                        this._printer.printText('"');
                     }
                  } else if (value == null) {
                     this._printer.printText(name);
                     this._printer.printText("=\"\"");
                  } else {
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  }
               }
            }

            if (htmlName != null && HTMLdtd.isPreserveSpace(htmlName)) {
               preserveSpace = true;
            }

            if (addNSAttr) {
               Iterator var16 = this._prefixes.entrySet().iterator();

               while(var16.hasNext()) {
                  Map.Entry<String, String> entry = (Map.Entry)var16.next();
                  this._printer.printSpace();
                  value = (String)entry.getKey();
                  name = (String)entry.getValue();
                  if (name.length() == 0) {
                     this._printer.printText("xmlns=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  } else {
                     this._printer.printText("xmlns:");
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  }
               }
            }

            state = this.enterElementState(namespaceURI, localName, rawName, preserveSpace);
            if (htmlName != null && (htmlName.equalsIgnoreCase("A") || htmlName.equalsIgnoreCase("TD"))) {
               state.empty = false;
               this._printer.printText('>');
            }

            if (htmlName != null && (rawName.equalsIgnoreCase("SCRIPT") || rawName.equalsIgnoreCase("STYLE"))) {
               if (this._xhtml) {
                  state.doCData = true;
               } else {
                  state.unescaped = true;
               }
            }

         }
      } catch (IOException var15) {
         throw new SAXException(var15);
      }
   }

   public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
      try {
         this.endElementIO(namespaceURI, localName, rawName);
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   public void endElementIO(String namespaceURI, String localName, String rawName) throws IOException {
      this._printer.unindent();
      ElementState state = this.getElementState();
      String htmlName;
      if (state.namespaceURI != null && state.namespaceURI.length() != 0) {
         if (!state.namespaceURI.equals("http://www.w3.org/1999/xhtml") && (this.fUserXHTMLNamespace == null || !this.fUserXHTMLNamespace.equals(state.namespaceURI))) {
            htmlName = null;
         } else {
            htmlName = state.localName;
         }
      } else {
         htmlName = state.rawName;
      }

      if (this._xhtml) {
         if (state.empty) {
            this._printer.printText(" />");
         } else {
            if (state.inCData) {
               this._printer.printText("]]>");
            }

            this._printer.printText("</");
            this._printer.printText(state.rawName.toLowerCase(Locale.ENGLISH));
            this._printer.printText('>');
         }
      } else {
         if (state.empty) {
            this._printer.printText('>');
         }

         if (htmlName == null || !HTMLdtd.isOnlyOpening(htmlName)) {
            if (this._indenting && !state.preserveSpace && state.afterElement) {
               this._printer.breakLine();
            }

            if (state.inCData) {
               this._printer.printText("]]>");
            }

            this._printer.printText("</");
            this._printer.printText(state.rawName);
            this._printer.printText('>');
         }
      }

      state = this.leaveElementState();
      if (htmlName == null || !htmlName.equalsIgnoreCase("A") && !htmlName.equalsIgnoreCase("TD")) {
         state.afterElement = true;
      }

      state.empty = false;
      if (this.isDocumentState()) {
         this._printer.flush();
      }

   }

   public void characters(char[] chars, int start, int length) throws SAXException {
      try {
         ElementState state = this.content();
         state.doCData = false;
         super.characters(chars, start, length);
      } catch (IOException var6) {
         throw new SAXException(var6);
      }
   }

   public void startElement(String tagName, AttributeList attrs) throws SAXException {
      try {
         if (this._printer == null) {
            throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", (Object[])null));
         } else {
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
               if (!this._started) {
                  this.startDocument(tagName);
               }
            } else {
               if (state.empty) {
                  this._printer.printText('>');
               }

               if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement)) {
                  this._printer.breakLine();
               }
            }

            boolean preserveSpace = state.preserveSpace;
            this._printer.printText('<');
            if (this._xhtml) {
               this._printer.printText(tagName.toLowerCase(Locale.ENGLISH));
            } else {
               this._printer.printText(tagName);
            }

            this._printer.indent();
            if (attrs != null) {
               for(int i = 0; i < attrs.getLength(); ++i) {
                  this._printer.printSpace();
                  String name = attrs.getName(i).toLowerCase(Locale.ENGLISH);
                  String value = attrs.getValue(i);
                  if (this._xhtml) {
                     if (value == null) {
                        this._printer.printText(name);
                        this._printer.printText("=\"\"");
                     } else {
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this.printEscaped(value);
                        this._printer.printText('"');
                     }
                  } else {
                     if (value == null) {
                        value = "";
                     }

                     if (!this._format.getPreserveEmptyAttributes() && value.length() == 0) {
                        this._printer.printText(name);
                     } else if (HTMLdtd.isURI(tagName, name)) {
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this._printer.printText(this.escapeURI(value));
                        this._printer.printText('"');
                     } else if (HTMLdtd.isBoolean(tagName, name)) {
                        this._printer.printText(name);
                     } else {
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this.printEscaped(value);
                        this._printer.printText('"');
                     }
                  }
               }
            }

            if (HTMLdtd.isPreserveSpace(tagName)) {
               preserveSpace = true;
            }

            state = this.enterElementState((String)null, (String)null, tagName, preserveSpace);
            if (tagName.equalsIgnoreCase("A") || tagName.equalsIgnoreCase("TD")) {
               state.empty = false;
               this._printer.printText('>');
            }

            if (tagName.equalsIgnoreCase("SCRIPT") || tagName.equalsIgnoreCase("STYLE")) {
               if (this._xhtml) {
                  state.doCData = true;
               } else {
                  state.unescaped = true;
               }
            }

         }
      } catch (IOException var9) {
         throw new SAXException(var9);
      }
   }

   public void endElement(String tagName) throws SAXException {
      this.endElement((String)null, (String)null, tagName);
   }

   protected void startDocument(String rootTagName) throws IOException {
      this._printer.leaveDTD();
      if (!this._started) {
         if (this._docTypePublicId == null && this._docTypeSystemId == null) {
            if (this._xhtml) {
               this._docTypePublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
               this._docTypeSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
            } else {
               this._docTypePublicId = "-//W3C//DTD HTML 4.01//EN";
               this._docTypeSystemId = "http://www.w3.org/TR/html4/strict.dtd";
            }
         }

         if (!this._format.getOmitDocumentType()) {
            if (this._docTypePublicId == null || this._xhtml && this._docTypeSystemId == null) {
               if (this._docTypeSystemId != null) {
                  if (this._xhtml) {
                     this._printer.printText("<!DOCTYPE html SYSTEM ");
                  } else {
                     this._printer.printText("<!DOCTYPE HTML SYSTEM ");
                  }

                  this.printDoctypeURL(this._docTypeSystemId);
                  this._printer.printText('>');
                  this._printer.breakLine();
               }
            } else {
               if (this._xhtml) {
                  this._printer.printText("<!DOCTYPE html PUBLIC ");
               } else {
                  this._printer.printText("<!DOCTYPE HTML PUBLIC ");
               }

               this.printDoctypeURL(this._docTypePublicId);
               if (this._docTypeSystemId != null) {
                  if (this._indenting) {
                     this._printer.breakLine();
                     this._printer.printText("                      ");
                  } else {
                     this._printer.printText(' ');
                  }

                  this.printDoctypeURL(this._docTypeSystemId);
               }

               this._printer.printText('>');
               this._printer.breakLine();
            }
         }
      }

      this._started = true;
      this.serializePreRoot();
   }

   protected void serializeElement(Element elem) throws IOException {
      String tagName = elem.getTagName();
      ElementState state = this.getElementState();
      if (this.isDocumentState()) {
         if (!this._started) {
            this.startDocument(tagName);
         }
      } else {
         if (state.empty) {
            this._printer.printText('>');
         }

         if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement)) {
            this._printer.breakLine();
         }
      }

      boolean preserveSpace = state.preserveSpace;
      this._printer.printText('<');
      if (this._xhtml) {
         this._printer.printText(tagName.toLowerCase(Locale.ENGLISH));
      } else {
         this._printer.printText(tagName);
      }

      this._printer.indent();
      NamedNodeMap attrMap = elem.getAttributes();
      if (attrMap != null) {
         for(int i = 0; i < attrMap.getLength(); ++i) {
            Attr attr = (Attr)attrMap.item(i);
            String name = attr.getName().toLowerCase(Locale.ENGLISH);
            String value = attr.getValue();
            if (attr.getSpecified()) {
               this._printer.printSpace();
               if (this._xhtml) {
                  if (value == null) {
                     this._printer.printText(name);
                     this._printer.printText("=\"\"");
                  } else {
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  }
               } else {
                  if (value == null) {
                     value = "";
                  }

                  if (!this._format.getPreserveEmptyAttributes() && value.length() == 0) {
                     this._printer.printText(name);
                  } else if (HTMLdtd.isURI(tagName, name)) {
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this._printer.printText(this.escapeURI(value));
                     this._printer.printText('"');
                  } else if (HTMLdtd.isBoolean(tagName, name)) {
                     this._printer.printText(name);
                  } else {
                     this._printer.printText(name);
                     this._printer.printText("=\"");
                     this.printEscaped(value);
                     this._printer.printText('"');
                  }
               }
            }
         }
      }

      if (HTMLdtd.isPreserveSpace(tagName)) {
         preserveSpace = true;
      }

      if (!elem.hasChildNodes() && HTMLdtd.isEmptyTag(tagName)) {
         this._printer.unindent();
         if (this._xhtml) {
            this._printer.printText(" />");
         } else {
            this._printer.printText('>');
         }

         state.afterElement = true;
         state.empty = false;
         if (this.isDocumentState()) {
            this._printer.flush();
         }
      } else {
         state = this.enterElementState((String)null, (String)null, tagName, preserveSpace);
         if (tagName.equalsIgnoreCase("A") || tagName.equalsIgnoreCase("TD")) {
            state.empty = false;
            this._printer.printText('>');
         }

         if (tagName.equalsIgnoreCase("SCRIPT") || tagName.equalsIgnoreCase("STYLE")) {
            if (this._xhtml) {
               state.doCData = true;
            } else {
               state.unescaped = true;
            }
         }

         for(Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
            this.serializeNode(child);
         }

         this.endElementIO((String)null, (String)null, tagName);
      }

   }

   protected void characters(String text) throws IOException {
      ElementState state = this.content();
      super.characters(text);
   }

   protected String getEntityRef(int ch) {
      return HTMLdtd.fromChar(ch);
   }

   protected String escapeURI(String uri) {
      int index = uri.indexOf("\"");
      return index >= 0 ? uri.substring(0, index) : uri;
   }
}
