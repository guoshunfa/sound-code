package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.util.Iterator;
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

public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {
   private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
   private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   private SortedSet<String> inclusiveNSSet;
   private final SortedSet<Attr> result;

   public Canonicalizer20010315Excl(boolean var1) {
      super(var1);
      this.result = new TreeSet(COMPARE);
   }

   public byte[] engineCanonicalizeSubTree(Node var1) throws CanonicalizationException {
      return this.engineCanonicalizeSubTree(var1, "", (Node)null);
   }

   public byte[] engineCanonicalizeSubTree(Node var1, String var2) throws CanonicalizationException {
      return this.engineCanonicalizeSubTree(var1, var2, (Node)null);
   }

   public byte[] engineCanonicalizeSubTree(Node var1, String var2, Node var3) throws CanonicalizationException {
      this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(var2);
      return super.engineCanonicalizeSubTree(var1, var3);
   }

   public byte[] engineCanonicalize(XMLSignatureInput var1, String var2) throws CanonicalizationException {
      this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(var2);
      return super.engineCanonicalize(var1);
   }

   public byte[] engineCanonicalizeXPathNodeSet(Set<Node> var1, String var2) throws CanonicalizationException {
      this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(var2);
      return super.engineCanonicalizeXPathNodeSet(var1);
   }

   protected Iterator<Attr> handleAttributesSubtree(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException {
      SortedSet var3 = this.result;
      var3.clear();
      TreeSet var4 = new TreeSet();
      if (this.inclusiveNSSet != null && !this.inclusiveNSSet.isEmpty()) {
         var4.addAll(this.inclusiveNSSet);
      }

      NamedNodeMap var5;
      Attr var8;
      if (var1.hasAttributes()) {
         var5 = var1.getAttributes();
         int var6 = var5.getLength();

         for(int var7 = 0; var7 < var6; ++var7) {
            var8 = (Attr)var5.item(var7);
            String var9 = var8.getLocalName();
            String var10 = var8.getNodeValue();
            if (!"http://www.w3.org/2000/xmlns/".equals(var8.getNamespaceURI())) {
               String var15 = var8.getPrefix();
               if (var15 != null && !var15.equals("xml") && !var15.equals("xmlns")) {
                  var4.add(var15);
               }

               var3.add(var8);
            } else if ((!"xml".equals(var9) || !"http://www.w3.org/XML/1998/namespace".equals(var10)) && var2.addMapping(var9, var10, var8) && C14nHelper.namespaceIsRelative(var10)) {
               Object[] var11 = new Object[]{var1.getTagName(), var9, var8.getNodeValue()};
               throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", var11);
            }
         }
      }

      var5 = null;
      String var12;
      if (var1.getNamespaceURI() != null && var1.getPrefix() != null && var1.getPrefix().length() != 0) {
         var12 = var1.getPrefix();
      } else {
         var12 = "xmlns";
      }

      var4.add(var12);
      Iterator var13 = var4.iterator();

      while(var13.hasNext()) {
         String var14 = (String)var13.next();
         var8 = var2.getMapping(var14);
         if (var8 != null) {
            var3.add(var8);
         }
      }

      return var3.iterator();
   }

   protected final Iterator<Attr> handleAttributes(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException {
      SortedSet var3 = this.result;
      var3.clear();
      TreeSet var4 = null;
      boolean var5 = this.isVisibleDO(var1, var2.getLevel()) == 1;
      if (var5) {
         var4 = new TreeSet();
         if (this.inclusiveNSSet != null && !this.inclusiveNSSet.isEmpty()) {
            var4.addAll(this.inclusiveNSSet);
         }
      }

      if (var1.hasAttributes()) {
         NamedNodeMap var6 = var1.getAttributes();
         int var7 = var6.getLength();

         for(int var8 = 0; var8 < var7; ++var8) {
            Attr var9 = (Attr)var6.item(var8);
            String var10 = var9.getLocalName();
            String var11 = var9.getNodeValue();
            if (!"http://www.w3.org/2000/xmlns/".equals(var9.getNamespaceURI())) {
               if (this.isVisible(var9) && var5) {
                  String var20 = var9.getPrefix();
                  if (var20 != null && !var20.equals("xml") && !var20.equals("xmlns")) {
                     var4.add(var20);
                  }

                  var3.add(var9);
               }
            } else if (var5 && !this.isVisible(var9) && !"xmlns".equals(var10)) {
               var2.removeMappingIfNotRender(var10);
            } else {
               if (!var5 && this.isVisible(var9) && this.inclusiveNSSet.contains(var10) && !var2.removeMappingIfRender(var10)) {
                  Node var12 = var2.addMappingAndRender(var10, var11, var9);
                  if (var12 != null) {
                     var3.add((Attr)var12);
                     if (C14nHelper.namespaceIsRelative(var9)) {
                        Object[] var13 = new Object[]{var1.getTagName(), var10, var9.getNodeValue()};
                        throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", var13);
                     }
                  }
               }

               if (var2.addMapping(var10, var11, var9) && C14nHelper.namespaceIsRelative(var11)) {
                  Object[] var19 = new Object[]{var1.getTagName(), var10, var9.getNodeValue()};
                  throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", var19);
               }
            }
         }
      }

      if (var5) {
         Attr var14 = var1.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
         if (var14 != null && !this.isVisible(var14)) {
            var2.addMapping("xmlns", "", this.getNullNode(var14.getOwnerDocument()));
         }

         String var15 = null;
         if (var1.getNamespaceURI() != null && var1.getPrefix() != null && var1.getPrefix().length() != 0) {
            var15 = var1.getPrefix();
         } else {
            var15 = "xmlns";
         }

         var4.add(var15);
         Iterator var16 = var4.iterator();

         while(var16.hasNext()) {
            String var17 = (String)var16.next();
            Attr var18 = var2.getMapping(var17);
            if (var18 != null) {
               var3.add(var18);
            }
         }
      }

      return var3.iterator();
   }

   protected void circumventBugIfNeeded(XMLSignatureInput var1) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
      if (var1.isNeedsToBeExpanded() && !this.inclusiveNSSet.isEmpty() && !this.inclusiveNSSet.isEmpty()) {
         Document var2 = null;
         if (var1.getSubNode() != null) {
            var2 = XMLUtils.getOwnerDocument(var1.getSubNode());
         } else {
            var2 = XMLUtils.getOwnerDocument(var1.getNodeSet());
         }

         XMLUtils.circumventBug2650(var2);
      }
   }
}
