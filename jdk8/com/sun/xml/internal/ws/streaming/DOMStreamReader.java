package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMStreamReader implements XMLStreamReader, NamespaceContext {
   protected Node _current;
   private Node _start;
   private NamedNodeMap _namedNodeMap;
   protected String wholeText;
   private final FinalArrayList<Attr> _currentAttributes = new FinalArrayList();
   protected DOMStreamReader.Scope[] scopes = new DOMStreamReader.Scope[8];
   protected int depth = 0;
   protected int _state;

   public DOMStreamReader() {
   }

   public DOMStreamReader(Node node) {
      this.setCurrentNode(node);
   }

   public void setCurrentNode(Node node) {
      this.scopes[0] = new DOMStreamReader.Scope((DOMStreamReader.Scope)null);
      this.depth = 0;
      this._start = this._current = node;
      this._state = 7;
   }

   public void close() throws XMLStreamException {
   }

   protected void splitAttributes() {
      this._currentAttributes.clear();
      DOMStreamReader.Scope scope = this.allocateScope();
      this._namedNodeMap = this._current.getAttributes();
      int i;
      if (this._namedNodeMap != null) {
         i = this._namedNodeMap.getLength();

         for(int i = 0; i < i; ++i) {
            Attr attr = (Attr)this._namedNodeMap.item(i);
            String attrName = attr.getNodeName();
            if (!attrName.startsWith("xmlns:") && !attrName.equals("xmlns")) {
               this._currentAttributes.add(attr);
            } else {
               scope.currentNamespaces.add(attr);
            }
         }
      }

      this.ensureNs(this._current);

      for(i = this._currentAttributes.size() - 1; i >= 0; --i) {
         Attr a = (Attr)this._currentAttributes.get(i);
         if (fixNull(a.getNamespaceURI()).length() > 0) {
            this.ensureNs(a);
         }
      }

   }

   private void ensureNs(Node n) {
      String prefix = fixNull(n.getPrefix());
      String uri = fixNull(n.getNamespaceURI());
      DOMStreamReader.Scope scope = this.scopes[this.depth];
      String currentUri = scope.getNamespaceURI(prefix);
      if (prefix.length() == 0) {
         currentUri = fixNull(currentUri);
         if (currentUri.equals(uri)) {
            return;
         }
      } else if (currentUri != null && currentUri.equals(uri)) {
         return;
      }

      if (!prefix.equals("xml") && !prefix.equals("xmlns")) {
         scope.additionalNamespaces.add(prefix);
         scope.additionalNamespaces.add(uri);
      }
   }

   private DOMStreamReader.Scope allocateScope() {
      if (this.scopes.length == ++this.depth) {
         DOMStreamReader.Scope[] newBuf = new DOMStreamReader.Scope[this.scopes.length * 2];
         System.arraycopy(this.scopes, 0, newBuf, 0, this.scopes.length);
         this.scopes = newBuf;
      }

      DOMStreamReader.Scope scope = this.scopes[this.depth];
      if (scope == null) {
         scope = this.scopes[this.depth] = new DOMStreamReader.Scope(this.scopes[this.depth - 1]);
      } else {
         scope.reset();
      }

      return scope;
   }

   public int getAttributeCount() {
      if (this._state == 1) {
         return this._currentAttributes.size();
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeCount() called in illegal state");
      }
   }

   public String getAttributeLocalName(int index) {
      if (this._state == 1) {
         String localName = ((Attr)this._currentAttributes.get(index)).getLocalName();
         return localName != null ? localName : QName.valueOf(((Attr)this._currentAttributes.get(index)).getNodeName()).getLocalPart();
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeLocalName() called in illegal state");
      }
   }

   public QName getAttributeName(int index) {
      if (this._state == 1) {
         Node attr = (Node)this._currentAttributes.get(index);
         String localName = attr.getLocalName();
         if (localName != null) {
            String prefix = attr.getPrefix();
            String uri = attr.getNamespaceURI();
            return new QName(fixNull(uri), localName, fixNull(prefix));
         } else {
            return QName.valueOf(attr.getNodeName());
         }
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeName() called in illegal state");
      }
   }

   public String getAttributeNamespace(int index) {
      if (this._state == 1) {
         String uri = ((Attr)this._currentAttributes.get(index)).getNamespaceURI();
         return fixNull(uri);
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeNamespace() called in illegal state");
      }
   }

   public String getAttributePrefix(int index) {
      if (this._state == 1) {
         String prefix = ((Attr)this._currentAttributes.get(index)).getPrefix();
         return fixNull(prefix);
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributePrefix() called in illegal state");
      }
   }

   public String getAttributeType(int index) {
      if (this._state == 1) {
         return "CDATA";
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeType() called in illegal state");
      }
   }

   public String getAttributeValue(int index) {
      if (this._state == 1) {
         return ((Attr)this._currentAttributes.get(index)).getNodeValue();
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
      }
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      if (this._state == 1) {
         if (this._namedNodeMap != null) {
            Node attr = this._namedNodeMap.getNamedItemNS(namespaceURI, localName);
            return attr != null ? attr.getNodeValue() : null;
         } else {
            return null;
         }
      } else {
         throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
      }
   }

   public String getCharacterEncodingScheme() {
      return null;
   }

   public String getElementText() throws XMLStreamException {
      throw new RuntimeException("DOMStreamReader: getElementText() not implemented");
   }

   public String getEncoding() {
      return null;
   }

   public int getEventType() {
      return this._state;
   }

   public String getLocalName() {
      if (this._state != 1 && this._state != 2) {
         if (this._state == 9) {
            return this._current.getNodeName();
         } else {
            throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
         }
      } else {
         String localName = this._current.getLocalName();
         return localName != null ? localName : QName.valueOf(this._current.getNodeName()).getLocalPart();
      }
   }

   public Location getLocation() {
      return DummyLocation.INSTANCE;
   }

   public QName getName() {
      if (this._state != 1 && this._state != 2) {
         throw new IllegalStateException("DOMStreamReader: getName() called in illegal state");
      } else {
         String localName = this._current.getLocalName();
         if (localName != null) {
            String prefix = this._current.getPrefix();
            String uri = this._current.getNamespaceURI();
            return new QName(fixNull(uri), localName, fixNull(prefix));
         } else {
            return QName.valueOf(this._current.getNodeName());
         }
      }
   }

   public NamespaceContext getNamespaceContext() {
      return this;
   }

   private DOMStreamReader.Scope getCheckedScope() {
      if (this._state != 1 && this._state != 2) {
         throw new IllegalStateException("DOMStreamReader: neither on START_ELEMENT nor END_ELEMENT");
      } else {
         return this.scopes[this.depth];
      }
   }

   public int getNamespaceCount() {
      return this.getCheckedScope().getNamespaceCount();
   }

   public String getNamespacePrefix(int index) {
      return this.getCheckedScope().getNamespacePrefix(index);
   }

   public String getNamespaceURI(int index) {
      return this.getCheckedScope().getNamespaceURI(index);
   }

   public String getNamespaceURI() {
      if (this._state != 1 && this._state != 2) {
         return null;
      } else {
         String uri = this._current.getNamespaceURI();
         return fixNull(uri);
      }
   }

   public String getNamespaceURI(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException("DOMStreamReader: getNamespaceURI(String) call with a null prefix");
      } else if (prefix.equals("xml")) {
         return "http://www.w3.org/XML/1998/namespace";
      } else if (prefix.equals("xmlns")) {
         return "http://www.w3.org/2000/xmlns/";
      } else {
         String nsUri = this.scopes[this.depth].getNamespaceURI(prefix);
         if (nsUri != null) {
            return nsUri;
         } else {
            Node node = this.findRootElement();

            for(String nsDeclName = prefix.length() == 0 ? "xmlns" : "xmlns:" + prefix; node.getNodeType() != 9; node = node.getParentNode()) {
               NamedNodeMap namedNodeMap = node.getAttributes();
               Attr attr = (Attr)namedNodeMap.getNamedItem(nsDeclName);
               if (attr != null) {
                  return attr.getValue();
               }
            }

            return null;
         }
      }
   }

   public String getPrefix(String nsUri) {
      if (nsUri == null) {
         throw new IllegalArgumentException("DOMStreamReader: getPrefix(String) call with a null namespace URI");
      } else if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
         return "xml";
      } else if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
         return "xmlns";
      } else {
         String prefix = this.scopes[this.depth].getPrefix(nsUri);
         if (prefix != null) {
            return prefix;
         } else {
            for(Node node = this.findRootElement(); node.getNodeType() != 9; node = node.getParentNode()) {
               NamedNodeMap namedNodeMap = node.getAttributes();

               for(int i = namedNodeMap.getLength() - 1; i >= 0; --i) {
                  Attr attr = (Attr)namedNodeMap.item(i);
                  prefix = getPrefixForAttr(attr, nsUri);
                  if (prefix != null) {
                     return prefix;
                  }
               }
            }

            return null;
         }
      }
   }

   private Node findRootElement() {
      short type;
      Node node;
      for(node = this._start; (type = node.getNodeType()) != 9 && type != 1; node = node.getParentNode()) {
      }

      return node;
   }

   private static String getPrefixForAttr(Attr attr, String nsUri) {
      String attrName = attr.getNodeName();
      if (!attrName.startsWith("xmlns:") && !attrName.equals("xmlns")) {
         return null;
      } else if (attr.getValue().equals(nsUri)) {
         if (attrName.equals("xmlns")) {
            return "";
         } else {
            String localName = attr.getLocalName();
            return localName != null ? localName : QName.valueOf(attrName).getLocalPart();
         }
      } else {
         return null;
      }
   }

   public Iterator getPrefixes(String nsUri) {
      String prefix = this.getPrefix(nsUri);
      return prefix == null ? Collections.emptyList().iterator() : Collections.singletonList(prefix).iterator();
   }

   public String getPIData() {
      return this._state == 3 ? ((ProcessingInstruction)this._current).getData() : null;
   }

   public String getPITarget() {
      return this._state == 3 ? ((ProcessingInstruction)this._current).getTarget() : null;
   }

   public String getPrefix() {
      if (this._state != 1 && this._state != 2) {
         return null;
      } else {
         String prefix = this._current.getPrefix();
         return fixNull(prefix);
      }
   }

   public Object getProperty(String str) throws IllegalArgumentException {
      return null;
   }

   public String getText() {
      if (this._state == 4) {
         return this.wholeText;
      } else if (this._state != 12 && this._state != 5 && this._state != 9) {
         throw new IllegalStateException("DOMStreamReader: getTextLength() called in illegal state");
      } else {
         return this._current.getNodeValue();
      }
   }

   public char[] getTextCharacters() {
      return this.getText().toCharArray();
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int targetLength) throws XMLStreamException {
      String text = this.getText();
      int copiedSize = Math.min(targetLength, text.length() - sourceStart);
      text.getChars(sourceStart, sourceStart + copiedSize, target, targetStart);
      return copiedSize;
   }

   public int getTextLength() {
      return this.getText().length();
   }

   public int getTextStart() {
      if (this._state != 4 && this._state != 12 && this._state != 5 && this._state != 9) {
         throw new IllegalStateException("DOMStreamReader: getTextStart() called in illegal state");
      } else {
         return 0;
      }
   }

   public String getVersion() {
      return null;
   }

   public boolean hasName() {
      return this._state == 1 || this._state == 2;
   }

   public boolean hasNext() throws XMLStreamException {
      return this._state != 8;
   }

   public boolean hasText() {
      if (this._state != 4 && this._state != 12 && this._state != 5 && this._state != 9) {
         return false;
      } else {
         return this.getText().trim().length() > 0;
      }
   }

   public boolean isAttributeSpecified(int param) {
      return false;
   }

   public boolean isCharacters() {
      return this._state == 4;
   }

   public boolean isEndElement() {
      return this._state == 2;
   }

   public boolean isStandalone() {
      return true;
   }

   public boolean isStartElement() {
      return this._state == 1;
   }

   public boolean isWhiteSpace() {
      if (this._state != 4 && this._state != 12) {
         return false;
      } else {
         return this.getText().trim().length() == 0;
      }
   }

   private static int mapNodeTypeToState(int nodetype) {
      switch(nodetype) {
      case 1:
         return 1;
      case 2:
      case 9:
      case 10:
      case 11:
      default:
         throw new RuntimeException("DOMStreamReader: Unexpected node type");
      case 3:
         return 4;
      case 4:
         return 12;
      case 5:
         return 9;
      case 6:
         return 15;
      case 7:
         return 3;
      case 8:
         return 5;
      case 12:
         return 14;
      }
   }

   public int next() throws XMLStreamException {
      while(true) {
         int r = this._next();
         switch(r) {
         case 1:
            this.splitAttributes();
            return 1;
         case 4:
            Node prev = this._current.getPreviousSibling();
            if (prev != null && prev.getNodeType() == 3) {
               break;
            }

            Text t = (Text)this._current;
            this.wholeText = t.getWholeText();
            if (this.wholeText.length() == 0) {
               break;
            }

            return 4;
         default:
            return r;
         }
      }
   }

   protected int _next() throws XMLStreamException {
      Node child;
      switch(this._state) {
      case 1:
         child = this._current.getFirstChild();
         if (child == null) {
            return this._state = 2;
         }

         this._current = child;
         return this._state = mapNodeTypeToState(this._current.getNodeType());
      case 2:
         --this.depth;
      case 3:
      case 4:
      case 5:
      case 9:
      case 12:
         break;
      case 6:
      case 10:
      case 11:
      case 13:
      default:
         throw new RuntimeException("DOMStreamReader: Unexpected internal state");
      case 7:
         if (this._current.getNodeType() == 1) {
            return this._state = 1;
         }

         child = this._current.getFirstChild();
         if (child == null) {
            return this._state = 8;
         }

         this._current = child;
         return this._state = mapNodeTypeToState(this._current.getNodeType());
      case 8:
         throw new IllegalStateException("DOMStreamReader: Calling next() at END_DOCUMENT");
      }

      if (this._current == this._start) {
         return this._state = 8;
      } else {
         Node sibling = this._current.getNextSibling();
         if (sibling != null) {
            this._current = sibling;
            return this._state = mapNodeTypeToState(this._current.getNodeType());
         } else {
            this._current = this._current.getParentNode();
            this._state = this._current != null && this._current.getNodeType() != 9 ? 2 : 8;
            return this._state;
         }
      }
   }

   public int nextTag() throws XMLStreamException {
      int eventType;
      for(eventType = this.next(); eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {
      }

      if (eventType != 1 && eventType != 2) {
         throw new XMLStreamException2("DOMStreamReader: Expected start or end tag");
      } else {
         return eventType;
      }
   }

   public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
      if (type != this._state) {
         throw new XMLStreamException2("DOMStreamReader: Required event type not found");
      } else if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
         throw new XMLStreamException2("DOMStreamReader: Required namespaceURI not found");
      } else if (localName != null && !localName.equals(this.getLocalName())) {
         throw new XMLStreamException2("DOMStreamReader: Required localName not found");
      }
   }

   public boolean standaloneSet() {
      return true;
   }

   private static void displayDOM(Node node, OutputStream ostream) {
      try {
         System.out.println("\n====\n");
         XmlUtil.newTransformer().transform(new DOMSource(node), new StreamResult(ostream));
         System.out.println("\n====\n");
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private static void verifyDOMIntegrity(Node node) {
      switch(node.getNodeType()) {
      case 1:
      case 2:
         if (node.getLocalName() == null) {
            System.out.println("WARNING: DOM level 1 node found");
            System.out.println(" -> node.getNodeName() = " + node.getNodeName());
            System.out.println(" -> node.getNamespaceURI() = " + node.getNamespaceURI());
            System.out.println(" -> node.getLocalName() = " + node.getLocalName());
            System.out.println(" -> node.getPrefix() = " + node.getPrefix());
         }

         if (node.getNodeType() == 2) {
            return;
         }

         NamedNodeMap attrs = node.getAttributes();

         for(int i = 0; i < attrs.getLength(); ++i) {
            verifyDOMIntegrity(attrs.item(i));
         }
      case 9:
         NodeList children = node.getChildNodes();

         for(int i = 0; i < children.getLength(); ++i) {
            verifyDOMIntegrity(children.item(i));
         }
      }

   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   protected static final class Scope {
      final DOMStreamReader.Scope parent;
      final FinalArrayList<Attr> currentNamespaces = new FinalArrayList();
      final FinalArrayList<String> additionalNamespaces = new FinalArrayList();

      Scope(DOMStreamReader.Scope parent) {
         this.parent = parent;
      }

      void reset() {
         this.currentNamespaces.clear();
         this.additionalNamespaces.clear();
      }

      int getNamespaceCount() {
         return this.currentNamespaces.size() + this.additionalNamespaces.size() / 2;
      }

      String getNamespacePrefix(int index) {
         int sz = this.currentNamespaces.size();
         if (index < sz) {
            Attr attr = (Attr)this.currentNamespaces.get(index);
            String result = attr.getLocalName();
            if (result == null) {
               result = QName.valueOf(attr.getNodeName()).getLocalPart();
            }

            return result.equals("xmlns") ? null : result;
         } else {
            return (String)this.additionalNamespaces.get((index - sz) * 2);
         }
      }

      String getNamespaceURI(int index) {
         int sz = this.currentNamespaces.size();
         return index < sz ? ((Attr)this.currentNamespaces.get(index)).getValue() : (String)this.additionalNamespaces.get((index - sz) * 2 + 1);
      }

      String getPrefix(String nsUri) {
         for(DOMStreamReader.Scope sp = this; sp != null; sp = sp.parent) {
            int i;
            for(i = sp.currentNamespaces.size() - 1; i >= 0; --i) {
               String result = DOMStreamReader.getPrefixForAttr((Attr)sp.currentNamespaces.get(i), nsUri);
               if (result != null) {
                  return result;
               }
            }

            for(i = sp.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
               if (((String)sp.additionalNamespaces.get(i + 1)).equals(nsUri)) {
                  return (String)sp.additionalNamespaces.get(i);
               }
            }
         }

         return null;
      }

      String getNamespaceURI(@NotNull String prefix) {
         String nsDeclName = prefix.length() == 0 ? "xmlns" : "xmlns:" + prefix;

         for(DOMStreamReader.Scope sp = this; sp != null; sp = sp.parent) {
            int i;
            for(i = sp.currentNamespaces.size() - 1; i >= 0; --i) {
               Attr a = (Attr)sp.currentNamespaces.get(i);
               if (a.getNodeName().equals(nsDeclName)) {
                  return a.getValue();
               }
            }

            for(i = sp.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
               if (((String)sp.additionalNamespaces.get(i)).equals(prefix)) {
                  return (String)sp.additionalNamespaces.get(i + 1);
               }
            }
         }

         return null;
      }
   }
}
