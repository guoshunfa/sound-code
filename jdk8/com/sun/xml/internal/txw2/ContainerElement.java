package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlCDATA;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.annotation.XmlValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.xml.namespace.QName;

final class ContainerElement implements InvocationHandler, TypedXmlWriter {
   final Document document;
   StartTag startTag;
   final EndTag endTag = new EndTag();
   private final String nsUri;
   private Content tail;
   private ContainerElement prevOpen;
   private ContainerElement nextOpen;
   private final ContainerElement parent;
   private ContainerElement lastOpenChild;
   private boolean blocked;

   public ContainerElement(Document document, ContainerElement parent, String nsUri, String localName) {
      this.parent = parent;
      this.document = document;
      this.nsUri = nsUri;
      this.startTag = new StartTag(this, nsUri, localName);
      this.tail = this.startTag;
      if (this.isRoot()) {
         document.setFirstContent(this.startTag);
      }

   }

   private boolean isRoot() {
      return this.parent == null;
   }

   private boolean isCommitted() {
      return this.tail == null;
   }

   public Document getDocument() {
      return this.document;
   }

   boolean isBlocked() {
      return this.blocked && !this.isCommitted();
   }

   public void block() {
      this.blocked = true;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass() != TypedXmlWriter.class && method.getDeclaringClass() != Object.class) {
         XmlAttribute xa = (XmlAttribute)method.getAnnotation(XmlAttribute.class);
         XmlValue xv = (XmlValue)method.getAnnotation(XmlValue.class);
         XmlElement xe = (XmlElement)method.getAnnotation(XmlElement.class);
         if (xa != null) {
            if (xv == null && xe == null) {
               this.addAttribute(xa, method, args);
               return proxy;
            } else {
               throw new IllegalAnnotationException(method.toString());
            }
         } else if (xv != null) {
            if (xe != null) {
               throw new IllegalAnnotationException(method.toString());
            } else {
               this._pcdata(args);
               return proxy;
            }
         } else {
            return this.addElement(xe, method, args);
         }
      } else {
         try {
            return method.invoke(this, args);
         } catch (InvocationTargetException var7) {
            throw var7.getTargetException();
         }
      }
   }

   private void addAttribute(XmlAttribute xa, Method method, Object[] args) {
      assert xa != null;

      this.checkStartTag();
      String localName = xa.value();
      if (xa.value().length() == 0) {
         localName = method.getName();
      }

      this._attribute(xa.ns(), localName, args);
   }

   private void checkStartTag() {
      if (this.startTag == null) {
         throw new IllegalStateException("start tag has already been written");
      }
   }

   private Object addElement(XmlElement e, Method method, Object[] args) {
      Class<?> rt = method.getReturnType();
      String nsUri = "##default";
      String localName = method.getName();
      if (e != null) {
         if (e.value().length() != 0) {
            localName = e.value();
         }

         nsUri = e.ns();
      }

      if (nsUri.equals("##default")) {
         Class<?> c = method.getDeclaringClass();
         XmlElement ce = (XmlElement)c.getAnnotation(XmlElement.class);
         if (ce != null) {
            nsUri = ce.ns();
         }

         if (nsUri.equals("##default")) {
            nsUri = this.getNamespace(c.getPackage());
         }
      }

      if (rt == Void.TYPE) {
         boolean isCDATA = method.getAnnotation(XmlCDATA.class) != null;
         StartTag st = new StartTag(this.document, nsUri, localName);
         this.addChild(st);
         Object[] var9 = args;
         int var10 = args.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Object arg = var9[var11];
            Object text;
            if (isCDATA) {
               text = new Cdata(this.document, st, arg);
            } else {
               text = new Pcdata(this.document, st, arg);
            }

            this.addChild((Content)text);
         }

         this.addChild(new EndTag());
         return null;
      } else if (TypedXmlWriter.class.isAssignableFrom(rt)) {
         return this._element(nsUri, localName, rt);
      } else {
         throw new IllegalSignatureException("Illegal return type: " + rt);
      }
   }

   private String getNamespace(Package pkg) {
      if (pkg == null) {
         return "";
      } else {
         XmlNamespace ns = (XmlNamespace)pkg.getAnnotation(XmlNamespace.class);
         String nsUri;
         if (ns != null) {
            nsUri = ns.value();
         } else {
            nsUri = "";
         }

         return nsUri;
      }
   }

   private void addChild(Content child) {
      this.tail.setNext(this.document, child);
      this.tail = child;
   }

   public void commit() {
      this.commit(true);
   }

   public void commit(boolean includingAllPredecessors) {
      this._commit(includingAllPredecessors);
      this.document.flush();
   }

   private void _commit(boolean includingAllPredecessors) {
      if (!this.isCommitted()) {
         this.addChild(this.endTag);
         if (this.isRoot()) {
            this.addChild(new EndDocument());
         }

         this.tail = null;
         if (includingAllPredecessors) {
            for(ContainerElement e = this; e != null; e = e.parent) {
               while(e.prevOpen != null) {
                  e.prevOpen._commit(false);
               }
            }
         }

         while(this.lastOpenChild != null) {
            this.lastOpenChild._commit(false);
         }

         if (this.parent != null) {
            if (this.parent.lastOpenChild == this) {
               assert this.nextOpen == null : "this must be the last one";

               this.parent.lastOpenChild = this.prevOpen;
            } else {
               assert this.nextOpen.prevOpen == this;

               this.nextOpen.prevOpen = this.prevOpen;
            }

            if (this.prevOpen != null) {
               assert this.prevOpen.nextOpen == this;

               this.prevOpen.nextOpen = this.nextOpen;
            }
         }

         this.nextOpen = null;
         this.prevOpen = null;
      }
   }

   public void _attribute(String localName, Object value) {
      this._attribute("", localName, value);
   }

   public void _attribute(String nsUri, String localName, Object value) {
      this.checkStartTag();
      this.startTag.addAttribute(nsUri, localName, value);
   }

   public void _attribute(QName attributeName, Object value) {
      this._attribute(attributeName.getNamespaceURI(), attributeName.getLocalPart(), value);
   }

   public void _namespace(String uri) {
      this._namespace(uri, false);
   }

   public void _namespace(String uri, String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkStartTag();
         this.startTag.addNamespaceDecl(uri, prefix, false);
      }
   }

   public void _namespace(String uri, boolean requirePrefix) {
      this.checkStartTag();
      this.startTag.addNamespaceDecl(uri, (String)null, requirePrefix);
   }

   public void _pcdata(Object value) {
      this.addChild(new Pcdata(this.document, this.startTag, value));
   }

   public void _cdata(Object value) {
      this.addChild(new Cdata(this.document, this.startTag, value));
   }

   public void _comment(Object value) throws UnsupportedOperationException {
      this.addChild(new Comment(this.document, this.startTag, value));
   }

   public <T extends TypedXmlWriter> T _element(String localName, Class<T> contentModel) {
      return this._element(this.nsUri, localName, contentModel);
   }

   public <T extends TypedXmlWriter> T _element(QName tagName, Class<T> contentModel) {
      return this._element(tagName.getNamespaceURI(), tagName.getLocalPart(), contentModel);
   }

   public <T extends TypedXmlWriter> T _element(Class<T> contentModel) {
      return this._element(TXW.getTagName(contentModel), contentModel);
   }

   public <T extends TypedXmlWriter> T _cast(Class<T> facadeType) {
      return (TypedXmlWriter)facadeType.cast(Proxy.newProxyInstance(facadeType.getClassLoader(), new Class[]{facadeType}, this));
   }

   public <T extends TypedXmlWriter> T _element(String nsUri, String localName, Class<T> contentModel) {
      ContainerElement child = new ContainerElement(this.document, this, nsUri, localName);
      this.addChild(child.startTag);
      this.tail = child.endTag;
      if (this.lastOpenChild != null) {
         assert this.lastOpenChild.parent == this;

         assert child.nextOpen == null;

         assert child.nextOpen == null;

         child.prevOpen = this.lastOpenChild;

         assert this.lastOpenChild.nextOpen == null;

         this.lastOpenChild.nextOpen = child;
      }

      this.lastOpenChild = child;
      return child._cast(contentModel);
   }
}
