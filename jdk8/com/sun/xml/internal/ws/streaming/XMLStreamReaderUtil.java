package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderUtil {
   private XMLStreamReaderUtil() {
   }

   public static void close(XMLStreamReader reader) {
      try {
         reader.close();
      } catch (XMLStreamException var2) {
         throw wrapException(var2);
      }
   }

   public static void readRest(XMLStreamReader reader) {
      try {
         while(reader.getEventType() != 8) {
            reader.next();
         }

      } catch (XMLStreamException var2) {
         throw wrapException(var2);
      }
   }

   public static int next(XMLStreamReader reader) {
      try {
         int readerEvent = reader.next();

         while(readerEvent != 8) {
            switch(readerEvent) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 12:
               return readerEvent;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               readerEvent = reader.next();
            }
         }

         return readerEvent;
      } catch (XMLStreamException var2) {
         throw wrapException(var2);
      }
   }

   public static int nextElementContent(XMLStreamReader reader) {
      int state = nextContent(reader);
      if (state == 4) {
         throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[]{reader.getText()});
      } else {
         return state;
      }
   }

   public static void toNextTag(XMLStreamReader reader, QName name) {
      if (reader.getEventType() != 1 && reader.getEventType() != 2) {
         nextElementContent(reader);
      }

      if (reader.getEventType() == 2 && name.equals(reader.getName())) {
         nextElementContent(reader);
      }

   }

   public static String nextWhiteSpaceContent(XMLStreamReader reader) {
      next(reader);
      return currentWhiteSpaceContent(reader);
   }

   public static String currentWhiteSpaceContent(XMLStreamReader reader) {
      StringBuilder whiteSpaces = null;

      while(true) {
         switch(reader.getEventType()) {
         case 1:
         case 2:
         case 8:
            return whiteSpaces == null ? null : whiteSpaces.toString();
         case 4:
            if (!reader.isWhiteSpace()) {
               throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[]{reader.getText()});
            }

            if (whiteSpaces == null) {
               whiteSpaces = new StringBuilder();
            }

            whiteSpaces.append(reader.getText());
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            next(reader);
         }
      }
   }

   public static int nextContent(XMLStreamReader reader) {
      while(true) {
         int state = next(reader);
         switch(state) {
         case 1:
         case 2:
         case 8:
            return state;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            break;
         case 4:
            if (!reader.isWhiteSpace()) {
               return 4;
            }
         }
      }
   }

   public static void skipElement(XMLStreamReader reader) {
      assert reader.getEventType() == 1;

      skipTags(reader, true);

      assert reader.getEventType() == 2;

   }

   public static void skipSiblings(XMLStreamReader reader, QName parent) {
      skipTags(reader, reader.getName().equals(parent));

      assert reader.getEventType() == 2;

   }

   private static void skipTags(XMLStreamReader reader, boolean exitCondition) {
      try {
         int tags = 0;

         int state;
         while((state = reader.next()) != 8) {
            if (state == 1) {
               ++tags;
            } else if (state == 2) {
               if (tags == 0 && exitCondition) {
                  return;
               }

               --tags;
            }
         }

      } catch (XMLStreamException var4) {
         throw wrapException(var4);
      }
   }

   public static String getElementText(XMLStreamReader reader) {
      try {
         return reader.getElementText();
      } catch (XMLStreamException var2) {
         throw wrapException(var2);
      }
   }

   public static QName getElementQName(XMLStreamReader reader) {
      try {
         String text = reader.getElementText().trim();
         String prefix = text.substring(0, text.indexOf(58));
         String namespaceURI = reader.getNamespaceContext().getNamespaceURI(prefix);
         if (namespaceURI == null) {
            namespaceURI = "";
         }

         String localPart = text.substring(text.indexOf(58) + 1, text.length());
         return new QName(namespaceURI, localPart);
      } catch (XMLStreamException var5) {
         throw wrapException(var5);
      }
   }

   public static Attributes getAttributes(XMLStreamReader reader) {
      return reader.getEventType() != 1 && reader.getEventType() != 10 ? null : new XMLStreamReaderUtil.AttributesImpl(reader);
   }

   public static void verifyReaderState(XMLStreamReader reader, int expectedState) {
      int state = reader.getEventType();
      if (state != expectedState) {
         throw new XMLStreamReaderException("xmlreader.unexpectedState", new Object[]{getStateName(expectedState), getStateName(state)});
      }
   }

   public static void verifyTag(XMLStreamReader reader, String namespaceURI, String localName) {
      if (!localName.equals(reader.getLocalName()) || !namespaceURI.equals(reader.getNamespaceURI())) {
         throw new XMLStreamReaderException("xmlreader.unexpectedState.tag", new Object[]{"{" + namespaceURI + "}" + localName, "{" + reader.getNamespaceURI() + "}" + reader.getLocalName()});
      }
   }

   public static void verifyTag(XMLStreamReader reader, QName name) {
      verifyTag(reader, name.getNamespaceURI(), name.getLocalPart());
   }

   public static String getStateName(XMLStreamReader reader) {
      return getStateName(reader.getEventType());
   }

   public static String getStateName(int state) {
      switch(state) {
      case 1:
         return "START_ELEMENT";
      case 2:
         return "END_ELEMENT";
      case 3:
         return "PROCESSING_INSTRUCTION";
      case 4:
         return "CHARACTERS";
      case 5:
         return "COMMENT";
      case 6:
         return "SPACE";
      case 7:
         return "START_DOCUMENT";
      case 8:
         return "END_DOCUMENT";
      case 9:
         return "ENTITY_REFERENCE";
      case 10:
         return "ATTRIBUTE";
      case 11:
         return "DTD";
      case 12:
         return "CDATA";
      case 13:
         return "NAMESPACE";
      case 14:
         return "NOTATION_DECLARATION";
      case 15:
         return "ENTITY_DECLARATION";
      default:
         return "UNKNOWN";
      }
   }

   private static XMLStreamReaderException wrapException(XMLStreamException e) {
      return new XMLStreamReaderException("xmlreader.ioException", new Object[]{e});
   }

   public static class AttributesImpl implements Attributes {
      static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
      XMLStreamReaderUtil.AttributesImpl.AttributeInfo[] atInfos;

      public AttributesImpl(XMLStreamReader reader) {
         if (reader == null) {
            this.atInfos = new XMLStreamReaderUtil.AttributesImpl.AttributeInfo[0];
         } else {
            int index = 0;
            int namespaceCount = reader.getNamespaceCount();
            int attributeCount = reader.getAttributeCount();
            this.atInfos = new XMLStreamReaderUtil.AttributesImpl.AttributeInfo[namespaceCount + attributeCount];

            int i;
            for(i = 0; i < namespaceCount; ++i) {
               String namespacePrefix = reader.getNamespacePrefix(i);
               if (namespacePrefix == null) {
                  namespacePrefix = "";
               }

               this.atInfos[index++] = new XMLStreamReaderUtil.AttributesImpl.AttributeInfo(new QName("http://www.w3.org/2000/xmlns/", namespacePrefix, "xmlns"), reader.getNamespaceURI(i));
            }

            for(i = 0; i < attributeCount; ++i) {
               this.atInfos[index++] = new XMLStreamReaderUtil.AttributesImpl.AttributeInfo(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
         }

      }

      public int getLength() {
         return this.atInfos.length;
      }

      public String getLocalName(int index) {
         return index >= 0 && index < this.atInfos.length ? this.atInfos[index].getLocalName() : null;
      }

      public QName getName(int index) {
         return index >= 0 && index < this.atInfos.length ? this.atInfos[index].getName() : null;
      }

      public String getPrefix(int index) {
         return index >= 0 && index < this.atInfos.length ? this.atInfos[index].getName().getPrefix() : null;
      }

      public String getURI(int index) {
         return index >= 0 && index < this.atInfos.length ? this.atInfos[index].getName().getNamespaceURI() : null;
      }

      public String getValue(int index) {
         return index >= 0 && index < this.atInfos.length ? this.atInfos[index].getValue() : null;
      }

      public String getValue(QName name) {
         int index = this.getIndex(name);
         return index != -1 ? this.atInfos[index].getValue() : null;
      }

      public String getValue(String localName) {
         int index = this.getIndex(localName);
         return index != -1 ? this.atInfos[index].getValue() : null;
      }

      public String getValue(String uri, String localName) {
         int index = this.getIndex(uri, localName);
         return index != -1 ? this.atInfos[index].getValue() : null;
      }

      public boolean isNamespaceDeclaration(int index) {
         return index >= 0 && index < this.atInfos.length ? this.atInfos[index].isNamespaceDeclaration() : false;
      }

      public int getIndex(QName name) {
         for(int i = 0; i < this.atInfos.length; ++i) {
            if (this.atInfos[i].getName().equals(name)) {
               return i;
            }
         }

         return -1;
      }

      public int getIndex(String localName) {
         for(int i = 0; i < this.atInfos.length; ++i) {
            if (this.atInfos[i].getName().getLocalPart().equals(localName)) {
               return i;
            }
         }

         return -1;
      }

      public int getIndex(String uri, String localName) {
         for(int i = 0; i < this.atInfos.length; ++i) {
            QName qName = this.atInfos[i].getName();
            if (qName.getNamespaceURI().equals(uri) && qName.getLocalPart().equals(localName)) {
               return i;
            }
         }

         return -1;
      }

      static class AttributeInfo {
         private QName name;
         private String value;

         public AttributeInfo(QName name, String value) {
            this.name = name;
            if (value == null) {
               this.value = "";
            } else {
               this.value = value;
            }

         }

         QName getName() {
            return this.name;
         }

         String getValue() {
            return this.value;
         }

         String getLocalName() {
            if (this.isNamespaceDeclaration()) {
               return this.name.getLocalPart().equals("") ? "xmlns" : "xmlns:" + this.name.getLocalPart();
            } else {
               return this.name.getLocalPart();
            }
         }

         boolean isNamespaceDeclaration() {
            return this.name.getNamespaceURI() == "http://www.w3.org/2000/xmlns/";
         }
      }
   }
}
