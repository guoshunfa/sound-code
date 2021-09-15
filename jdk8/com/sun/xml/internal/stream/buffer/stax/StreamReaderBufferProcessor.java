package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class StreamReaderBufferProcessor extends AbstractProcessor implements XMLStreamReaderEx {
   private static final int CACHE_SIZE = 16;
   protected StreamReaderBufferProcessor.ElementStackEntry[] _stack;
   protected StreamReaderBufferProcessor.ElementStackEntry _stackTop;
   protected int _depth;
   protected String[] _namespaceAIIsPrefix;
   protected String[] _namespaceAIIsNamespaceName;
   protected int _namespaceAIIsEnd;
   protected StreamReaderBufferProcessor.InternalNamespaceContext _nsCtx;
   protected int _eventType;
   protected AttributesHolder _attributeCache;
   protected CharSequence _charSequence;
   protected char[] _characters;
   protected int _textOffset;
   protected int _textLen;
   protected String _piTarget;
   protected String _piData;
   private static final int PARSING = 1;
   private static final int PENDING_END_DOCUMENT = 2;
   private static final int COMPLETED = 3;
   private int _completionState;

   public StreamReaderBufferProcessor() {
      this._stack = new StreamReaderBufferProcessor.ElementStackEntry[16];
      this._namespaceAIIsPrefix = new String[16];
      this._namespaceAIIsNamespaceName = new String[16];
      this._nsCtx = new StreamReaderBufferProcessor.InternalNamespaceContext();

      for(int i = 0; i < this._stack.length; ++i) {
         this._stack[i] = new StreamReaderBufferProcessor.ElementStackEntry();
      }

      this._attributeCache = new AttributesHolder();
   }

   public StreamReaderBufferProcessor(XMLStreamBuffer buffer) throws XMLStreamException {
      this();
      this.setXMLStreamBuffer(buffer);
   }

   public void setXMLStreamBuffer(XMLStreamBuffer buffer) throws XMLStreamException {
      this.setBuffer(buffer, buffer.isFragment());
      this._completionState = 1;
      this._namespaceAIIsEnd = 0;
      this._characters = null;
      this._charSequence = null;
      this._eventType = 7;
   }

   public XMLStreamBuffer nextTagAndMark() throws XMLStreamException {
      while(true) {
         int s = this.peekStructure();
         if ((s & 240) != 32) {
            if ((s & 240) == 16) {
               this.readStructure();
               XMLStreamBufferMark mark = new XMLStreamBufferMark(new HashMap(this._namespaceAIIsEnd), this);
               this.next();
               return mark;
            }

            if (this.next() != 2) {
               continue;
            }

            return null;
         }

         Map<String, String> inscope = new HashMap(this._namespaceAIIsEnd);

         for(int i = 0; i < this._namespaceAIIsEnd; ++i) {
            inscope.put(this._namespaceAIIsPrefix[i], this._namespaceAIIsNamespaceName[i]);
         }

         XMLStreamBufferMark mark = new XMLStreamBufferMark(inscope, this);
         this.next();
         return mark;
      }
   }

   public Object getProperty(String name) {
      return null;
   }

   public int next() throws XMLStreamException {
      switch(this._completionState) {
      case 2:
         this._namespaceAIIsEnd = 0;
         this._completionState = 3;
         return this._eventType = 8;
      case 3:
         throw new XMLStreamException("Invalid State");
      default:
         switch(this._eventType) {
         case 2:
            if (this._depth > 1) {
               --this._depth;
               this.popElementStack(this._depth);
            } else if (this._depth == 1) {
               --this._depth;
            }
         default:
            this._characters = null;
            this._charSequence = null;

            while(true) {
               int eiiState = this.readEiiState();
               switch(eiiState) {
               case 1:
                  break;
               case 2:
               default:
                  throw new XMLStreamException("Internal XSB error: Invalid State=" + eiiState);
               case 3:
                  String uri = this.readStructureString();
                  String localName = this.readStructureString();
                  String prefix = this.getPrefixFromQName(this.readStructureString());
                  this.processElement(prefix, uri, localName, this.isInscope(this._depth));
                  return this._eventType = 1;
               case 4:
                  this.processElement(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.isInscope(this._depth));
                  return this._eventType = 1;
               case 5:
                  this.processElement((String)null, this.readStructureString(), this.readStructureString(), this.isInscope(this._depth));
                  return this._eventType = 1;
               case 6:
                  this.processElement((String)null, (String)null, this.readStructureString(), this.isInscope(this._depth));
                  return this._eventType = 1;
               case 7:
                  this._textLen = this.readStructure();
                  this._textOffset = this.readContentCharactersBuffer(this._textLen);
                  this._characters = this._contentCharactersBuffer;
                  return this._eventType = 4;
               case 8:
                  this._textLen = this.readStructure16();
                  this._textOffset = this.readContentCharactersBuffer(this._textLen);
                  this._characters = this._contentCharactersBuffer;
                  return this._eventType = 4;
               case 9:
                  this._characters = this.readContentCharactersCopy();
                  this._textLen = this._characters.length;
                  this._textOffset = 0;
                  return this._eventType = 4;
               case 10:
                  this._eventType = 4;
                  this._charSequence = this.readContentString();
                  return this._eventType = 4;
               case 11:
                  this._eventType = 4;
                  this._charSequence = (CharSequence)this.readContentObject();
                  return this._eventType = 4;
               case 12:
                  this._textLen = this.readStructure();
                  this._textOffset = this.readContentCharactersBuffer(this._textLen);
                  this._characters = this._contentCharactersBuffer;
                  return this._eventType = 5;
               case 13:
                  this._textLen = this.readStructure16();
                  this._textOffset = this.readContentCharactersBuffer(this._textLen);
                  this._characters = this._contentCharactersBuffer;
                  return this._eventType = 5;
               case 14:
                  this._characters = this.readContentCharactersCopy();
                  this._textLen = this._characters.length;
                  this._textOffset = 0;
                  return this._eventType = 5;
               case 15:
                  this._charSequence = this.readContentString();
                  return this._eventType = 5;
               case 16:
                  this._piTarget = this.readStructureString();
                  this._piData = this.readStructureString();
                  return this._eventType = 3;
               case 17:
                  if (this._depth > 1) {
                     return this._eventType = 2;
                  }

                  if (this._depth == 1) {
                     if (this._fragmentMode && --this._treeCount == 0) {
                        this._completionState = 2;
                     }

                     return this._eventType = 2;
                  }

                  this._namespaceAIIsEnd = 0;
                  this._completionState = 3;
                  return this._eventType = 8;
               }
            }
         }
      }
   }

   public final void require(int type, String namespaceURI, String localName) throws XMLStreamException {
      if (type != this._eventType) {
         throw new XMLStreamException("");
      } else if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
         throw new XMLStreamException("");
      } else if (localName != null && !localName.equals(this.getLocalName())) {
         throw new XMLStreamException("");
      }
   }

   public final String getElementTextTrim() throws XMLStreamException {
      return this.getElementText().trim();
   }

   public final String getElementText() throws XMLStreamException {
      if (this._eventType != 1) {
         throw new XMLStreamException("");
      } else {
         this.next();
         return this.getElementText(true);
      }
   }

   public final String getElementText(boolean startElementRead) throws XMLStreamException {
      if (!startElementRead) {
         throw new XMLStreamException("");
      } else {
         int eventType = this.getEventType();

         StringBuilder content;
         for(content = new StringBuilder(); eventType != 2; eventType = this.next()) {
            if (eventType != 4 && eventType != 12 && eventType != 6 && eventType != 9) {
               if (eventType != 3 && eventType != 5) {
                  if (eventType == 8) {
                     throw new XMLStreamException("");
                  }

                  if (eventType == 1) {
                     throw new XMLStreamException("");
                  }

                  throw new XMLStreamException("");
               }
            } else {
               content.append(this.getText());
            }
         }

         return content.toString();
      }
   }

   public final int nextTag() throws XMLStreamException {
      this.next();
      return this.nextTag(true);
   }

   public final int nextTag(boolean currentTagRead) throws XMLStreamException {
      int eventType = this.getEventType();
      if (!currentTagRead) {
         eventType = this.next();
      }

      while(eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5) {
         eventType = this.next();
      }

      if (eventType != 1 && eventType != 2) {
         throw new XMLStreamException("");
      } else {
         return eventType;
      }
   }

   public final boolean hasNext() {
      return this._eventType != 8;
   }

   public void close() throws XMLStreamException {
   }

   public final boolean isStartElement() {
      return this._eventType == 1;
   }

   public final boolean isEndElement() {
      return this._eventType == 2;
   }

   public final boolean isCharacters() {
      return this._eventType == 4;
   }

   public final boolean isWhiteSpace() {
      if (!this.isCharacters() && this._eventType != 12) {
         return false;
      } else {
         char[] ch = this.getTextCharacters();
         int start = this.getTextStart();
         int length = this.getTextLength();

         for(int i = start; i < length; ++i) {
            char c = ch[i];
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
               return false;
            }
         }

         return true;
      }
   }

   public final String getAttributeValue(String namespaceURI, String localName) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         if (namespaceURI == null) {
            namespaceURI = "";
         }

         return this._attributeCache.getValue(namespaceURI, localName);
      }
   }

   public final int getAttributeCount() {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         return this._attributeCache.getLength();
      }
   }

   public final QName getAttributeName(int index) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         String prefix = this._attributeCache.getPrefix(index);
         String localName = this._attributeCache.getLocalName(index);
         String uri = this._attributeCache.getURI(index);
         return new QName(uri, localName, prefix);
      }
   }

   public final String getAttributeNamespace(int index) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         return fixEmptyString(this._attributeCache.getURI(index));
      }
   }

   public final String getAttributeLocalName(int index) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         return this._attributeCache.getLocalName(index);
      }
   }

   public final String getAttributePrefix(int index) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         return fixEmptyString(this._attributeCache.getPrefix(index));
      }
   }

   public final String getAttributeType(int index) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         return this._attributeCache.getType(index);
      }
   }

   public final String getAttributeValue(int index) {
      if (this._eventType != 1) {
         throw new IllegalStateException("");
      } else {
         return this._attributeCache.getValue(index);
      }
   }

   public final boolean isAttributeSpecified(int index) {
      return false;
   }

   public final int getNamespaceCount() {
      if (this._eventType != 1 && this._eventType != 2) {
         throw new IllegalStateException("");
      } else {
         return this._stackTop.namespaceAIIsEnd - this._stackTop.namespaceAIIsStart;
      }
   }

   public final String getNamespacePrefix(int index) {
      if (this._eventType != 1 && this._eventType != 2) {
         throw new IllegalStateException("");
      } else {
         return this._namespaceAIIsPrefix[this._stackTop.namespaceAIIsStart + index];
      }
   }

   public final String getNamespaceURI(int index) {
      if (this._eventType != 1 && this._eventType != 2) {
         throw new IllegalStateException("");
      } else {
         return this._namespaceAIIsNamespaceName[this._stackTop.namespaceAIIsStart + index];
      }
   }

   public final String getNamespaceURI(String prefix) {
      return this._nsCtx.getNamespaceURI(prefix);
   }

   public final NamespaceContextEx getNamespaceContext() {
      return this._nsCtx;
   }

   public final int getEventType() {
      return this._eventType;
   }

   public final String getText() {
      if (this._characters != null) {
         String s = new String(this._characters, this._textOffset, this._textLen);
         this._charSequence = s;
         return s;
      } else if (this._charSequence != null) {
         return this._charSequence.toString();
      } else {
         throw new IllegalStateException();
      }
   }

   public final char[] getTextCharacters() {
      if (this._characters != null) {
         return this._characters;
      } else if (this._charSequence != null) {
         this._characters = this._charSequence.toString().toCharArray();
         this._textLen = this._characters.length;
         this._textOffset = 0;
         return this._characters;
      } else {
         throw new IllegalStateException();
      }
   }

   public final int getTextStart() {
      if (this._characters != null) {
         return this._textOffset;
      } else if (this._charSequence != null) {
         return 0;
      } else {
         throw new IllegalStateException();
      }
   }

   public final int getTextLength() {
      if (this._characters != null) {
         return this._textLen;
      } else if (this._charSequence != null) {
         return this._charSequence.length();
      } else {
         throw new IllegalStateException();
      }
   }

   public final int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
      if (this._characters == null) {
         if (this._charSequence == null) {
            throw new IllegalStateException("");
         }

         this._characters = this._charSequence.toString().toCharArray();
         this._textLen = this._characters.length;
         this._textOffset = 0;
      }

      try {
         int remaining = this._textLen - sourceStart;
         int len = remaining > length ? length : remaining;
         sourceStart += this._textOffset;
         System.arraycopy(this._characters, sourceStart, target, targetStart, len);
         return len;
      } catch (IndexOutOfBoundsException var7) {
         throw new XMLStreamException(var7);
      }
   }

   public final CharSequence getPCDATA() {
      if (this._characters != null) {
         return new StreamReaderBufferProcessor.CharSequenceImpl(this._textOffset, this._textLen);
      } else if (this._charSequence != null) {
         return this._charSequence;
      } else {
         throw new IllegalStateException();
      }
   }

   public final String getEncoding() {
      return "UTF-8";
   }

   public final boolean hasText() {
      return this._characters != null || this._charSequence != null;
   }

   public final Location getLocation() {
      return new StreamReaderBufferProcessor.DummyLocation();
   }

   public final boolean hasName() {
      return this._eventType == 1 || this._eventType == 2;
   }

   public final QName getName() {
      return this._stackTop.getQName();
   }

   public final String getLocalName() {
      return this._stackTop.localName;
   }

   public final String getNamespaceURI() {
      return this._stackTop.uri;
   }

   public final String getPrefix() {
      return this._stackTop.prefix;
   }

   public final String getVersion() {
      return "1.0";
   }

   public final boolean isStandalone() {
      return false;
   }

   public final boolean standaloneSet() {
      return false;
   }

   public final String getCharacterEncodingScheme() {
      return "UTF-8";
   }

   public final String getPITarget() {
      if (this._eventType == 3) {
         return this._piTarget;
      } else {
         throw new IllegalStateException("");
      }
   }

   public final String getPIData() {
      if (this._eventType == 3) {
         return this._piData;
      } else {
         throw new IllegalStateException("");
      }
   }

   protected void processElement(String prefix, String uri, String localName, boolean inscope) {
      this.pushElementStack();
      this._stackTop.set(prefix, uri, localName);
      this._attributeCache.clear();
      int item = this.peekStructure();
      if ((item & 240) == 64 || inscope) {
         item = this.processNamespaceAttributes(item, inscope);
      }

      if ((item & 240) == 48) {
         this.processAttributes(item);
      }

   }

   private boolean isInscope(int depth) {
      return this._buffer.getInscopeNamespaces().size() > 0 && depth == 0;
   }

   private void resizeNamespaceAttributes() {
      String[] namespaceAIIsPrefix = new String[this._namespaceAIIsEnd * 2];
      System.arraycopy(this._namespaceAIIsPrefix, 0, namespaceAIIsPrefix, 0, this._namespaceAIIsEnd);
      this._namespaceAIIsPrefix = namespaceAIIsPrefix;
      String[] namespaceAIIsNamespaceName = new String[this._namespaceAIIsEnd * 2];
      System.arraycopy(this._namespaceAIIsNamespaceName, 0, namespaceAIIsNamespaceName, 0, this._namespaceAIIsEnd);
      this._namespaceAIIsNamespaceName = namespaceAIIsNamespaceName;
   }

   private int processNamespaceAttributes(int item, boolean inscope) {
      this._stackTop.namespaceAIIsStart = this._namespaceAIIsEnd;

      Object prefixSet;
      for(prefixSet = inscope ? new HashSet() : Collections.emptySet(); (item & 240) == 64; item = this.peekStructure()) {
         if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length) {
            this.resizeNamespaceAttributes();
         }

         switch(getNIIState(item)) {
         case 1:
            this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
            if (inscope) {
               ((Set)prefixSet).add("");
            }
            break;
         case 2:
            this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this.readStructureString();
            if (inscope) {
               ((Set)prefixSet).add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]);
            }

            this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = "";
            break;
         case 3:
            this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = this.readStructureString();
            if (inscope) {
               ((Set)prefixSet).add(this._namespaceAIIsPrefix[this._namespaceAIIsEnd]);
            }

            this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = this.readStructureString();
            break;
         case 4:
            this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = "";
            if (inscope) {
               ((Set)prefixSet).add("");
            }

            this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = this.readStructureString();
         }

         this.readStructure();
      }

      if (inscope) {
         Iterator var4 = this._buffer.getInscopeNamespaces().entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, String> e = (Map.Entry)var4.next();
            String key = fixNull((String)e.getKey());
            if (!((Set)prefixSet).contains(key)) {
               if (this._namespaceAIIsEnd == this._namespaceAIIsPrefix.length) {
                  this.resizeNamespaceAttributes();
               }

               this._namespaceAIIsPrefix[this._namespaceAIIsEnd] = key;
               this._namespaceAIIsNamespaceName[this._namespaceAIIsEnd++] = (String)e.getValue();
            }
         }
      }

      this._stackTop.namespaceAIIsEnd = this._namespaceAIIsEnd;
      return item;
   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   private void processAttributes(int item) {
      do {
         switch(getAIIState(item)) {
         case 1:
            String uri = this.readStructureString();
            String localName = this.readStructureString();
            String prefix = this.getPrefixFromQName(this.readStructureString());
            this._attributeCache.addAttributeWithPrefix(prefix, uri, localName, this.readStructureString(), this.readContentString());
            break;
         case 2:
            this._attributeCache.addAttributeWithPrefix(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
            break;
         case 3:
            this._attributeCache.addAttributeWithPrefix("", this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
            break;
         case 4:
            this._attributeCache.addAttributeWithPrefix("", "", this.readStructureString(), this.readStructureString(), this.readContentString());
            break;
         default:
            assert false : "Internal XSB Error: wrong attribute state, Item=" + item;
         }

         this.readStructure();
         item = this.peekStructure();
      } while((item & 240) == 48);

   }

   private void pushElementStack() {
      if (this._depth == this._stack.length) {
         StreamReaderBufferProcessor.ElementStackEntry[] tmp = this._stack;
         this._stack = new StreamReaderBufferProcessor.ElementStackEntry[this._stack.length * 3 / 2 + 1];
         System.arraycopy(tmp, 0, this._stack, 0, tmp.length);

         for(int i = tmp.length; i < this._stack.length; ++i) {
            this._stack[i] = new StreamReaderBufferProcessor.ElementStackEntry();
         }
      }

      this._stackTop = this._stack[this._depth++];
   }

   private void popElementStack(int depth) {
      this._stackTop = this._stack[depth - 1];
      this._namespaceAIIsEnd = this._stack[depth].namespaceAIIsStart;
   }

   private static String fixEmptyString(String s) {
      return s.length() == 0 ? null : s;
   }

   private class DummyLocation implements Location {
      private DummyLocation() {
      }

      public int getLineNumber() {
         return -1;
      }

      public int getColumnNumber() {
         return -1;
      }

      public int getCharacterOffset() {
         return -1;
      }

      public String getPublicId() {
         return null;
      }

      public String getSystemId() {
         return StreamReaderBufferProcessor.this._buffer.getSystemId();
      }

      // $FF: synthetic method
      DummyLocation(Object x1) {
         this();
      }
   }

   private final class InternalNamespaceContext implements NamespaceContextEx {
      private InternalNamespaceContext() {
      }

      public String getNamespaceURI(String prefix) {
         if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
         } else {
            int i;
            if (StreamReaderBufferProcessor.this._stringInterningFeature) {
               prefix = prefix.intern();

               for(i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; --i) {
                  if (prefix == StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]) {
                     return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i];
                  }
               }
            } else {
               for(i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1; i >= 0; --i) {
                  if (prefix.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i])) {
                     return StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[i];
                  }
               }
            }

            if (prefix.equals("xml")) {
               return "http://www.w3.org/XML/1998/namespace";
            } else {
               return prefix.equals("xmlns") ? "http://www.w3.org/2000/xmlns/" : null;
            }
         }
      }

      public String getPrefix(String namespaceURI) {
         Iterator i = this.getPrefixes(namespaceURI);
         return i.hasNext() ? (String)i.next() : null;
      }

      public Iterator getPrefixes(final String namespaceURI) {
         if (namespaceURI == null) {
            throw new IllegalArgumentException("NamespaceURI cannot be null");
         } else if (namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return Collections.singletonList("xml").iterator();
         } else {
            return namespaceURI.equals("http://www.w3.org/2000/xmlns/") ? Collections.singletonList("xmlns").iterator() : new Iterator() {
               private int i;
               private boolean requireFindNext;
               private String p;

               {
                  this.i = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1;
                  this.requireFindNext = true;
               }

               private String findNext() {
                  while(this.i >= 0) {
                     if (namespaceURI.equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i]) && InternalNamespaceContext.this.getNamespaceURI(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i]).equals(StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.i])) {
                        return this.p = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.i];
                     }

                     --this.i;
                  }

                  return this.p = null;
               }

               public boolean hasNext() {
                  if (this.requireFindNext) {
                     this.findNext();
                     this.requireFindNext = false;
                  }

                  return this.p != null;
               }

               public Object next() {
                  if (this.requireFindNext) {
                     this.findNext();
                  }

                  this.requireFindNext = true;
                  if (this.p == null) {
                     throw new NoSuchElementException();
                  } else {
                     return this.p;
                  }
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      }

      public Iterator<NamespaceContextEx.Binding> iterator() {
         return new Iterator<NamespaceContextEx.Binding>() {
            private final int end;
            private int current;
            private boolean requireFindNext;
            private NamespaceContextEx.Binding namespace;

            {
               this.end = StreamReaderBufferProcessor.this._namespaceAIIsEnd - 1;
               this.current = this.end;
               this.requireFindNext = true;
            }

            private NamespaceContextEx.Binding findNext() {
               String prefix;
               int i;
               do {
                  if (this.current < 0) {
                     return this.namespace = null;
                  }

                  prefix = StreamReaderBufferProcessor.this._namespaceAIIsPrefix[this.current];

                  for(i = this.end; i > this.current && !prefix.equals(StreamReaderBufferProcessor.this._namespaceAIIsPrefix[i]); --i) {
                  }
               } while(i != this.current--);

               return this.namespace = InternalNamespaceContext.this.new BindingImpl(prefix, StreamReaderBufferProcessor.this._namespaceAIIsNamespaceName[this.current]);
            }

            public boolean hasNext() {
               if (this.requireFindNext) {
                  this.findNext();
                  this.requireFindNext = false;
               }

               return this.namespace != null;
            }

            public NamespaceContextEx.Binding next() {
               if (this.requireFindNext) {
                  this.findNext();
               }

               this.requireFindNext = true;
               if (this.namespace == null) {
                  throw new NoSuchElementException();
               } else {
                  return this.namespace;
               }
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      // $FF: synthetic method
      InternalNamespaceContext(Object x1) {
         this();
      }

      private class BindingImpl implements NamespaceContextEx.Binding {
         final String _prefix;
         final String _namespaceURI;

         BindingImpl(String prefix, String namespaceURI) {
            this._prefix = prefix;
            this._namespaceURI = namespaceURI;
         }

         public String getPrefix() {
            return this._prefix;
         }

         public String getNamespaceURI() {
            return this._namespaceURI;
         }
      }
   }

   private final class ElementStackEntry {
      String prefix;
      String uri;
      String localName;
      QName qname;
      int namespaceAIIsStart;
      int namespaceAIIsEnd;

      private ElementStackEntry() {
      }

      public void set(String prefix, String uri, String localName) {
         this.prefix = prefix;
         this.uri = uri;
         this.localName = localName;
         this.qname = null;
         this.namespaceAIIsStart = this.namespaceAIIsEnd = StreamReaderBufferProcessor.this._namespaceAIIsEnd;
      }

      public QName getQName() {
         if (this.qname == null) {
            this.qname = new QName(this.fixNull(this.uri), this.localName, this.fixNull(this.prefix));
         }

         return this.qname;
      }

      private String fixNull(String s) {
         return s == null ? "" : s;
      }

      // $FF: synthetic method
      ElementStackEntry(Object x1) {
         this();
      }
   }

   private class CharSequenceImpl implements CharSequence {
      private final int _offset;
      private final int _length;

      CharSequenceImpl(int offset, int length) {
         this._offset = offset;
         this._length = length;
      }

      public int length() {
         return this._length;
      }

      public char charAt(int index) {
         if (index >= 0 && index < StreamReaderBufferProcessor.this._textLen) {
            return StreamReaderBufferProcessor.this._characters[StreamReaderBufferProcessor.this._textOffset + index];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public CharSequence subSequence(int start, int end) {
         int length = end - start;
         if (end >= 0 && start >= 0 && end <= length && start <= end) {
            return StreamReaderBufferProcessor.this.new CharSequenceImpl(this._offset + start, length);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public String toString() {
         return new String(StreamReaderBufferProcessor.this._characters, this._offset, this._length);
      }
   }
}
