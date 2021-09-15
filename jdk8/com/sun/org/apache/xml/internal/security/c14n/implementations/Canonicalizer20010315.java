package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer20010315 extends CanonicalizerBase {
   private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
   private boolean firstCall = true;
   private final SortedSet<Attr> result;
   private Canonicalizer20010315.XmlAttrStack xmlattrStack;

   public Canonicalizer20010315(boolean var1) {
      super(var1);
      this.result = new TreeSet(COMPARE);
      this.xmlattrStack = new Canonicalizer20010315.XmlAttrStack();
   }

   public byte[] engineCanonicalizeXPathNodeSet(Set<Node> var1, String var2) throws CanonicalizationException {
      throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
   }

   public byte[] engineCanonicalizeSubTree(Node var1, String var2) throws CanonicalizationException {
      throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
   }

   protected Iterator<Attr> handleAttributesSubtree(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException {
      if (!var1.hasAttributes() && !this.firstCall) {
         return null;
      } else {
         SortedSet var3 = this.result;
         var3.clear();
         if (var1.hasAttributes()) {
            NamedNodeMap var4 = var1.getAttributes();
            int var5 = var4.getLength();

            for(int var6 = 0; var6 < var5; ++var6) {
               Attr var7 = (Attr)var4.item(var6);
               String var8 = var7.getNamespaceURI();
               String var9 = var7.getLocalName();
               String var10 = var7.getValue();
               if (!"http://www.w3.org/2000/xmlns/".equals(var8)) {
                  var3.add(var7);
               } else if (!"xml".equals(var9) || !"http://www.w3.org/XML/1998/namespace".equals(var10)) {
                  Node var11 = var2.addMappingAndRender(var9, var10, var7);
                  if (var11 != null) {
                     var3.add((Attr)var11);
                     if (C14nHelper.namespaceIsRelative(var7)) {
                        Object[] var12 = new Object[]{var1.getTagName(), var9, var7.getNodeValue()};
                        throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", var12);
                     }
                  }
               }
            }
         }

         if (this.firstCall) {
            var2.getUnrenderedNodes(var3);
            this.xmlattrStack.getXmlnsAttr(var3);
            this.firstCall = false;
         }

         return var3.iterator();
      }
   }

   protected Iterator<Attr> handleAttributes(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException {
      this.xmlattrStack.push(var2.getLevel());
      boolean var3 = this.isVisibleDO(var1, var2.getLevel()) == 1;
      SortedSet var4 = this.result;
      var4.clear();
      if (var1.hasAttributes()) {
         NamedNodeMap var5 = var1.getAttributes();
         int var6 = var5.getLength();

         for(int var7 = 0; var7 < var6; ++var7) {
            Attr var8 = (Attr)var5.item(var7);
            String var9 = var8.getNamespaceURI();
            String var10 = var8.getLocalName();
            String var11 = var8.getValue();
            if (!"http://www.w3.org/2000/xmlns/".equals(var9)) {
               if ("http://www.w3.org/XML/1998/namespace".equals(var9)) {
                  this.xmlattrStack.addXmlnsAttr(var8);
               } else if (var3) {
                  var4.add(var8);
               }
            } else if (!"xml".equals(var10) || !"http://www.w3.org/XML/1998/namespace".equals(var11)) {
               if (this.isVisible(var8)) {
                  if (var3 || !var2.removeMappingIfRender(var10)) {
                     Node var12 = var2.addMappingAndRender(var10, var11, var8);
                     if (var12 != null) {
                        var4.add((Attr)var12);
                        if (C14nHelper.namespaceIsRelative(var8)) {
                           Object[] var13 = new Object[]{var1.getTagName(), var10, var8.getNodeValue()};
                           throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", var13);
                        }
                     }
                  }
               } else if (var3 && !"xmlns".equals(var10)) {
                  var2.removeMapping(var10);
               } else {
                  var2.addMapping(var10, var11, var8);
               }
            }
         }
      }

      if (var3) {
         Attr var14 = var1.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
         Object var15 = null;
         if (var14 == null) {
            var15 = var2.getMapping("xmlns");
         } else if (!this.isVisible(var14)) {
            var15 = var2.addMappingAndRender("xmlns", "", this.getNullNode(var14.getOwnerDocument()));
         }

         if (var15 != null) {
            var4.add((Attr)var15);
         }

         this.xmlattrStack.getXmlnsAttr(var4);
         var2.getUnrenderedNodes(var4);
      }

      return var4.iterator();
   }

   protected void circumventBugIfNeeded(XMLSignatureInput var1) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
      if (var1.isNeedsToBeExpanded()) {
         Document var2 = null;
         if (var1.getSubNode() != null) {
            var2 = XMLUtils.getOwnerDocument(var1.getSubNode());
         } else {
            var2 = XMLUtils.getOwnerDocument(var1.getNodeSet());
         }

         XMLUtils.circumventBug2650(var2);
      }
   }

   protected void handleParent(Element var1, NameSpaceSymbTable var2) {
      if (var1.hasAttributes() || var1.getNamespaceURI() != null) {
         this.xmlattrStack.push(-1);
         NamedNodeMap var3 = var1.getAttributes();
         int var4 = var3.getLength();

         String var7;
         for(int var5 = 0; var5 < var4; ++var5) {
            Attr var6 = (Attr)var3.item(var5);
            var7 = var6.getLocalName();
            String var8 = var6.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(var6.getNamespaceURI())) {
               if (!"xml".equals(var7) || !"http://www.w3.org/XML/1998/namespace".equals(var8)) {
                  var2.addMapping(var7, var8, var6);
               }
            } else if ("http://www.w3.org/XML/1998/namespace".equals(var6.getNamespaceURI())) {
               this.xmlattrStack.addXmlnsAttr(var6);
            }
         }

         if (var1.getNamespaceURI() != null) {
            String var9 = var1.getPrefix();
            String var10 = var1.getNamespaceURI();
            if (var9 != null && !var9.equals("")) {
               var7 = "xmlns:" + var9;
            } else {
               var9 = "xmlns";
               var7 = "xmlns";
            }

            Attr var11 = var1.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", var7);
            var11.setValue(var10);
            var2.addMapping(var9, var10, var11);
         }

      }
   }

   private static class XmlAttrStack {
      int currentLevel;
      int lastlevel;
      Canonicalizer20010315.XmlAttrStack.XmlsStackElement cur;
      List<Canonicalizer20010315.XmlAttrStack.XmlsStackElement> levels;

      private XmlAttrStack() {
         this.currentLevel = 0;
         this.lastlevel = 0;
         this.levels = new ArrayList();
      }

      void push(int var1) {
         this.currentLevel = var1;
         if (this.currentLevel != -1) {
            int var2;
            for(this.cur = null; this.lastlevel >= this.currentLevel; this.lastlevel = ((Canonicalizer20010315.XmlAttrStack.XmlsStackElement)this.levels.get(var2 - 1)).level) {
               this.levels.remove(this.levels.size() - 1);
               var2 = this.levels.size();
               if (var2 == 0) {
                  this.lastlevel = 0;
                  return;
               }
            }

         }
      }

      void addXmlnsAttr(Attr var1) {
         if (this.cur == null) {
            this.cur = new Canonicalizer20010315.XmlAttrStack.XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.levels.add(this.cur);
            this.lastlevel = this.currentLevel;
         }

         this.cur.nodes.add(var1);
      }

      void getXmlnsAttr(Collection<Attr> var1) {
         int var2 = this.levels.size() - 1;
         if (this.cur == null) {
            this.cur = new Canonicalizer20010315.XmlAttrStack.XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.lastlevel = this.currentLevel;
            this.levels.add(this.cur);
         }

         boolean var3 = false;
         Canonicalizer20010315.XmlAttrStack.XmlsStackElement var4 = null;
         if (var2 == -1) {
            var3 = true;
         } else {
            var4 = (Canonicalizer20010315.XmlAttrStack.XmlsStackElement)this.levels.get(var2);
            if (var4.rendered && var4.level + 1 == this.currentLevel) {
               var3 = true;
            }
         }

         if (var3) {
            var1.addAll(this.cur.nodes);
            this.cur.rendered = true;
         } else {
            HashMap var5;
            for(var5 = new HashMap(); var2 >= 0; --var2) {
               var4 = (Canonicalizer20010315.XmlAttrStack.XmlsStackElement)this.levels.get(var2);
               Iterator var6 = var4.nodes.iterator();

               while(var6.hasNext()) {
                  Attr var7 = (Attr)var6.next();
                  if (!var5.containsKey(var7.getName())) {
                     var5.put(var7.getName(), var7);
                  }
               }
            }

            this.cur.rendered = true;
            var1.addAll(var5.values());
         }
      }

      // $FF: synthetic method
      XmlAttrStack(Object var1) {
         this();
      }

      static class XmlsStackElement {
         int level;
         boolean rendered = false;
         List<Attr> nodes = new ArrayList();
      }
   }
}
