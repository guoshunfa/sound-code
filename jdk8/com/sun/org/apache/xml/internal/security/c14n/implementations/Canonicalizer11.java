package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer11 extends CanonicalizerBase {
   private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
   private static Logger log = Logger.getLogger(Canonicalizer11.class.getName());
   private final SortedSet<Attr> result;
   private boolean firstCall;
   private Canonicalizer11.XmlAttrStack xmlattrStack;

   public Canonicalizer11(boolean var1) {
      super(var1);
      this.result = new TreeSet(COMPARE);
      this.firstCall = true;
      this.xmlattrStack = new Canonicalizer11.XmlAttrStack();
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
                  if (var10.equals("id")) {
                     if (var3) {
                        var4.add(var8);
                     }
                  } else {
                     this.xmlattrStack.addXmlnsAttr(var8);
                  }
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
            } else if (!"id".equals(var7) && "http://www.w3.org/XML/1998/namespace".equals(var6.getNamespaceURI())) {
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

   private static String joinURI(String var0, String var1) throws URISyntaxException {
      String var2 = null;
      String var3 = null;
      String var4 = "";
      String var5 = null;
      URI var6;
      if (var0 != null) {
         if (var0.endsWith("..")) {
            var0 = var0 + "/";
         }

         var6 = new URI(var0);
         var2 = var6.getScheme();
         var3 = var6.getAuthority();
         var4 = var6.getPath();
         var5 = var6.getQuery();
      }

      var6 = new URI(var1);
      String var7 = var6.getScheme();
      String var8 = var6.getAuthority();
      String var9 = var6.getPath();
      String var10 = var6.getQuery();
      if (var7 != null && var7.equals(var2)) {
         var7 = null;
      }

      String var11;
      String var12;
      String var13;
      String var14;
      if (var7 != null) {
         var11 = var7;
         var12 = var8;
         var13 = removeDotSegments(var9);
         var14 = var10;
      } else {
         if (var8 != null) {
            var12 = var8;
            var13 = removeDotSegments(var9);
            var14 = var10;
         } else {
            if (var9.length() == 0) {
               var13 = var4;
               if (var10 != null) {
                  var14 = var10;
               } else {
                  var14 = var5;
               }
            } else {
               if (var9.startsWith("/")) {
                  var13 = removeDotSegments(var9);
               } else {
                  if (var3 != null && var4.length() == 0) {
                     var13 = "/" + var9;
                  } else {
                     int var15 = var4.lastIndexOf(47);
                     if (var15 == -1) {
                        var13 = var9;
                     } else {
                        var13 = var4.substring(0, var15 + 1) + var9;
                     }
                  }

                  var13 = removeDotSegments(var13);
               }

               var14 = var10;
            }

            var12 = var3;
         }

         var11 = var2;
      }

      return (new URI(var11, var12, var13, var14, (String)null)).toString();
   }

   private static String removeDotSegments(String var0) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "STEP   OUTPUT BUFFER\t\tINPUT BUFFER");
      }

      String var1;
      for(var1 = var0; var1.indexOf("//") > -1; var1 = var1.replaceAll("//", "/")) {
      }

      StringBuilder var2 = new StringBuilder();
      if (var1.charAt(0) == '/') {
         var2.append("/");
         var1 = var1.substring(1);
      }

      printStep("1 ", var2.toString(), var1);

      while(var1.length() != 0) {
         if (var1.startsWith("./")) {
            var1 = var1.substring(2);
            printStep("2A", var2.toString(), var1);
         } else if (var1.startsWith("../")) {
            var1 = var1.substring(3);
            if (!var2.toString().equals("/")) {
               var2.append("../");
            }

            printStep("2A", var2.toString(), var1);
         } else if (var1.startsWith("/./")) {
            var1 = var1.substring(2);
            printStep("2B", var2.toString(), var1);
         } else if (var1.equals("/.")) {
            var1 = var1.replaceFirst("/.", "/");
            printStep("2B", var2.toString(), var1);
         } else {
            int var3;
            if (var1.startsWith("/../")) {
               var1 = var1.substring(3);
               if (var2.length() == 0) {
                  var2.append("/");
               } else if (var2.toString().endsWith("../")) {
                  var2.append("..");
               } else if (var2.toString().endsWith("..")) {
                  var2.append("/..");
               } else {
                  var3 = var2.lastIndexOf("/");
                  if (var3 == -1) {
                     var2 = new StringBuilder();
                     if (var1.charAt(0) == '/') {
                        var1 = var1.substring(1);
                     }
                  } else {
                     var2 = var2.delete(var3, var2.length());
                  }
               }

               printStep("2C", var2.toString(), var1);
            } else if (var1.equals("/..")) {
               var1 = var1.replaceFirst("/..", "/");
               if (var2.length() == 0) {
                  var2.append("/");
               } else if (var2.toString().endsWith("../")) {
                  var2.append("..");
               } else if (var2.toString().endsWith("..")) {
                  var2.append("/..");
               } else {
                  var3 = var2.lastIndexOf("/");
                  if (var3 == -1) {
                     var2 = new StringBuilder();
                     if (var1.charAt(0) == '/') {
                        var1 = var1.substring(1);
                     }
                  } else {
                     var2 = var2.delete(var3, var2.length());
                  }
               }

               printStep("2C", var2.toString(), var1);
            } else if (var1.equals(".")) {
               var1 = "";
               printStep("2D", var2.toString(), var1);
            } else if (var1.equals("..")) {
               if (!var2.toString().equals("/")) {
                  var2.append("..");
               }

               var1 = "";
               printStep("2D", var2.toString(), var1);
            } else {
               boolean var6 = true;
               int var4 = var1.indexOf(47);
               if (var4 == 0) {
                  var3 = var1.indexOf(47, 1);
               } else {
                  var3 = var4;
                  var4 = 0;
               }

               String var5;
               if (var3 == -1) {
                  var5 = var1.substring(var4);
                  var1 = "";
               } else {
                  var5 = var1.substring(var4, var3);
                  var1 = var1.substring(var3);
               }

               var2.append(var5);
               printStep("2E", var2.toString(), var1);
            }
         }
      }

      if (var2.toString().endsWith("..")) {
         var2.append("/");
         printStep("3 ", var2.toString(), var1);
      }

      return var2.toString();
   }

   private static void printStep(String var0, String var1, String var2) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, " " + var0 + ":   " + var1);
         if (var1.length() == 0) {
            log.log(Level.FINE, "\t\t\t\t" + var2);
         } else {
            log.log(Level.FINE, "\t\t\t" + var2);
         }
      }

   }

   private static class XmlAttrStack {
      int currentLevel;
      int lastlevel;
      Canonicalizer11.XmlAttrStack.XmlsStackElement cur;
      List<Canonicalizer11.XmlAttrStack.XmlsStackElement> levels;

      private XmlAttrStack() {
         this.currentLevel = 0;
         this.lastlevel = 0;
         this.levels = new ArrayList();
      }

      void push(int var1) {
         this.currentLevel = var1;
         if (this.currentLevel != -1) {
            int var2;
            for(this.cur = null; this.lastlevel >= this.currentLevel; this.lastlevel = ((Canonicalizer11.XmlAttrStack.XmlsStackElement)this.levels.get(var2 - 1)).level) {
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
            this.cur = new Canonicalizer11.XmlAttrStack.XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.levels.add(this.cur);
            this.lastlevel = this.currentLevel;
         }

         this.cur.nodes.add(var1);
      }

      void getXmlnsAttr(Collection<Attr> var1) {
         int var2 = this.levels.size() - 1;
         if (this.cur == null) {
            this.cur = new Canonicalizer11.XmlAttrStack.XmlsStackElement();
            this.cur.level = this.currentLevel;
            this.lastlevel = this.currentLevel;
            this.levels.add(this.cur);
         }

         boolean var3 = false;
         Canonicalizer11.XmlAttrStack.XmlsStackElement var4 = null;
         if (var2 == -1) {
            var3 = true;
         } else {
            var4 = (Canonicalizer11.XmlAttrStack.XmlsStackElement)this.levels.get(var2);
            if (var4.rendered && var4.level + 1 == this.currentLevel) {
               var3 = true;
            }
         }

         if (var3) {
            var1.addAll(this.cur.nodes);
            this.cur.rendered = true;
         } else {
            HashMap var5 = new HashMap();
            ArrayList var6 = new ArrayList();

            Iterator var8;
            label100:
            for(boolean var7 = true; var2 >= 0; --var2) {
               var4 = (Canonicalizer11.XmlAttrStack.XmlsStackElement)this.levels.get(var2);
               if (var4.rendered) {
                  var7 = false;
               }

               var8 = var4.nodes.iterator();

               while(true) {
                  while(true) {
                     if (!var8.hasNext() || !var7) {
                        continue label100;
                     }

                     Attr var9 = (Attr)var8.next();
                     if (var9.getLocalName().equals("base") && !var4.rendered) {
                        var6.add(var9);
                     } else if (!var5.containsKey(var9.getName())) {
                        var5.put(var9.getName(), var9);
                     }
                  }
               }
            }

            if (!var6.isEmpty()) {
               var8 = var1.iterator();
               String var14 = null;
               Attr var10 = null;

               Attr var11;
               while(var8.hasNext()) {
                  var11 = (Attr)var8.next();
                  if (var11.getLocalName().equals("base")) {
                     var14 = var11.getValue();
                     var10 = var11;
                     break;
                  }
               }

               var8 = var6.iterator();

               while(true) {
                  while(var8.hasNext()) {
                     var11 = (Attr)var8.next();
                     if (var14 == null) {
                        var14 = var11.getValue();
                        var10 = var11;
                     } else {
                        try {
                           var14 = Canonicalizer11.joinURI(var11.getValue(), var14);
                        } catch (URISyntaxException var13) {
                           if (Canonicalizer11.log.isLoggable(Level.FINE)) {
                              Canonicalizer11.log.log(Level.FINE, (String)var13.getMessage(), (Throwable)var13);
                           }
                        }
                     }
                  }

                  if (var14 != null && var14.length() != 0) {
                     var10.setValue(var14);
                     var1.add(var10);
                  }
                  break;
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
