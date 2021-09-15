package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizerSpi;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public abstract class CanonicalizerBase extends CanonicalizerSpi {
   public static final String XML = "xml";
   public static final String XMLNS = "xmlns";
   protected static final AttrCompare COMPARE = new AttrCompare();
   private static final byte[] END_PI = new byte[]{63, 62};
   private static final byte[] BEGIN_PI = new byte[]{60, 63};
   private static final byte[] END_COMM = new byte[]{45, 45, 62};
   private static final byte[] BEGIN_COMM = new byte[]{60, 33, 45, 45};
   private static final byte[] XA = new byte[]{38, 35, 120, 65, 59};
   private static final byte[] X9 = new byte[]{38, 35, 120, 57, 59};
   private static final byte[] QUOT = new byte[]{38, 113, 117, 111, 116, 59};
   private static final byte[] XD = new byte[]{38, 35, 120, 68, 59};
   private static final byte[] GT = new byte[]{38, 103, 116, 59};
   private static final byte[] LT = new byte[]{38, 108, 116, 59};
   private static final byte[] END_TAG = new byte[]{60, 47};
   private static final byte[] AMP = new byte[]{38, 97, 109, 112, 59};
   private static final byte[] EQUALS_STR = new byte[]{61, 34};
   protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
   protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
   protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
   private List<NodeFilter> nodeFilter;
   private boolean includeComments;
   private Set<Node> xpathNodeSet;
   private Node excludeNode;
   private OutputStream writer = new ByteArrayOutputStream();
   private Attr nullNode;

   public CanonicalizerBase(boolean var1) {
      this.includeComments = var1;
   }

   public byte[] engineCanonicalizeSubTree(Node var1) throws CanonicalizationException {
      return this.engineCanonicalizeSubTree(var1, (Node)null);
   }

   public byte[] engineCanonicalizeXPathNodeSet(Set<Node> var1) throws CanonicalizationException {
      this.xpathNodeSet = var1;
      return this.engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this.xpathNodeSet));
   }

   public byte[] engineCanonicalize(XMLSignatureInput var1) throws CanonicalizationException {
      try {
         if (var1.isExcludeComments()) {
            this.includeComments = false;
         }

         if (var1.isOctetStream()) {
            return this.engineCanonicalize(var1.getBytes());
         } else if (var1.isElement()) {
            return this.engineCanonicalizeSubTree(var1.getSubNode(), var1.getExcludeNode());
         } else if (var1.isNodeSet()) {
            this.nodeFilter = var1.getNodeFilters();
            this.circumventBugIfNeeded(var1);
            return var1.getSubNode() != null ? this.engineCanonicalizeXPathNodeSetInternal(var1.getSubNode()) : this.engineCanonicalizeXPathNodeSet(var1.getNodeSet());
         } else {
            return null;
         }
      } catch (CanonicalizationException var3) {
         throw new CanonicalizationException("empty", var3);
      } catch (ParserConfigurationException var4) {
         throw new CanonicalizationException("empty", var4);
      } catch (IOException var5) {
         throw new CanonicalizationException("empty", var5);
      } catch (SAXException var6) {
         throw new CanonicalizationException("empty", var6);
      }
   }

   public void setWriter(OutputStream var1) {
      this.writer = var1;
   }

   protected byte[] engineCanonicalizeSubTree(Node var1, Node var2) throws CanonicalizationException {
      this.excludeNode = var2;

      try {
         NameSpaceSymbTable var3 = new NameSpaceSymbTable();
         byte var4 = -1;
         if (var1 != null && 1 == var1.getNodeType()) {
            this.getParentNameSpaces((Element)var1, var3);
            var4 = 0;
         }

         this.canonicalizeSubTree(var1, var3, var1, var4);
         this.writer.flush();
         byte[] var5;
         if (this.writer instanceof ByteArrayOutputStream) {
            var5 = ((ByteArrayOutputStream)this.writer).toByteArray();
            if (this.reset) {
               ((ByteArrayOutputStream)this.writer).reset();
            } else {
               this.writer.close();
            }

            return var5;
         } else if (this.writer instanceof UnsyncByteArrayOutputStream) {
            var5 = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
            if (this.reset) {
               ((UnsyncByteArrayOutputStream)this.writer).reset();
            } else {
               this.writer.close();
            }

            return var5;
         } else {
            this.writer.close();
            return null;
         }
      } catch (UnsupportedEncodingException var6) {
         throw new CanonicalizationException("empty", var6);
      } catch (IOException var7) {
         throw new CanonicalizationException("empty", var7);
      }
   }

   protected final void canonicalizeSubTree(Node var1, NameSpaceSymbTable var2, Node var3, int var4) throws CanonicalizationException, IOException {
      if (this.isVisibleInt(var1) != -1) {
         Node var5 = null;
         Object var6 = null;
         OutputStream var7 = this.writer;
         Node var8 = this.excludeNode;
         boolean var9 = this.includeComments;
         HashMap var10 = new HashMap();

         while(true) {
            switch(var1.getNodeType()) {
            case 1:
               var4 = 0;
               if (var1 != var8) {
                  Element var11 = (Element)var1;
                  var2.outputNodePush();
                  var7.write(60);
                  String var12 = var11.getTagName();
                  UtfHelpper.writeByte(var12, var7, var10);
                  Iterator var13 = this.handleAttributesSubtree(var11, var2);
                  if (var13 != null) {
                     while(var13.hasNext()) {
                        Attr var14 = (Attr)var13.next();
                        outputAttrToWriter(var14.getNodeName(), var14.getNodeValue(), var7, var10);
                     }
                  }

                  var7.write(62);
                  var5 = var1.getFirstChild();
                  if (var5 == null) {
                     var7.write((byte[])END_TAG.clone());
                     UtfHelpper.writeStringToUtf8(var12, var7);
                     var7.write(62);
                     var2.outputNodePop();
                     if (var6 != null) {
                        var5 = var1.getNextSibling();
                     }
                  } else {
                     var6 = var11;
                  }
               }
               break;
            case 2:
            case 6:
            case 12:
               throw new CanonicalizationException("empty");
            case 3:
            case 4:
               outputTextToWriter(var1.getNodeValue(), var7);
            case 5:
            case 10:
            default:
               break;
            case 7:
               this.outputPItoWriter((ProcessingInstruction)var1, var7, var4);
               break;
            case 8:
               if (var9) {
                  this.outputCommentToWriter((Comment)var1, var7, var4);
               }
               break;
            case 9:
            case 11:
               var2.outputNodePush();
               var5 = var1.getFirstChild();
            }

            while(var5 == null && var6 != null) {
               var7.write((byte[])END_TAG.clone());
               UtfHelpper.writeByte(((Element)var6).getTagName(), var7, var10);
               var7.write(62);
               var2.outputNodePop();
               if (var6 == var3) {
                  return;
               }

               var5 = ((Node)var6).getNextSibling();
               var6 = ((Node)var6).getParentNode();
               if (var6 == null || 1 != ((Node)var6).getNodeType()) {
                  var4 = 1;
                  var6 = null;
               }
            }

            if (var5 == null) {
               return;
            }

            var1 = var5;
            var5 = var5.getNextSibling();
         }
      }
   }

   private byte[] engineCanonicalizeXPathNodeSetInternal(Node var1) throws CanonicalizationException {
      try {
         this.canonicalizeXPathNodeSet(var1, var1);
         this.writer.flush();
         byte[] var2;
         if (this.writer instanceof ByteArrayOutputStream) {
            var2 = ((ByteArrayOutputStream)this.writer).toByteArray();
            if (this.reset) {
               ((ByteArrayOutputStream)this.writer).reset();
            } else {
               this.writer.close();
            }

            return var2;
         } else if (this.writer instanceof UnsyncByteArrayOutputStream) {
            var2 = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
            if (this.reset) {
               ((UnsyncByteArrayOutputStream)this.writer).reset();
            } else {
               this.writer.close();
            }

            return var2;
         } else {
            this.writer.close();
            return null;
         }
      } catch (UnsupportedEncodingException var3) {
         throw new CanonicalizationException("empty", var3);
      } catch (IOException var4) {
         throw new CanonicalizationException("empty", var4);
      }
   }

   protected final void canonicalizeXPathNodeSet(Node var1, Node var2) throws CanonicalizationException, IOException {
      if (this.isVisibleInt(var1) != -1) {
         boolean var3 = false;
         NameSpaceSymbTable var4 = new NameSpaceSymbTable();
         if (var1 != null && 1 == var1.getNodeType()) {
            this.getParentNameSpaces((Element)var1, var4);
         }

         if (var1 != null) {
            Node var5 = null;
            Object var6 = null;
            OutputStream var7 = this.writer;
            byte var8 = -1;
            HashMap var9 = new HashMap();

            while(true) {
               switch(var1.getNodeType()) {
               case 1:
                  var8 = 0;
                  Element var15 = (Element)var1;
                  String var11 = null;
                  int var12 = this.isVisibleDO(var1, var4.getLevel());
                  if (var12 == -1) {
                     var5 = var1.getNextSibling();
                  } else {
                     var3 = var12 == 1;
                     if (var3) {
                        var4.outputNodePush();
                        var7.write(60);
                        var11 = var15.getTagName();
                        UtfHelpper.writeByte(var11, var7, var9);
                     } else {
                        var4.push();
                     }

                     Iterator var13 = this.handleAttributes(var15, var4);
                     if (var13 != null) {
                        while(var13.hasNext()) {
                           Attr var14 = (Attr)var13.next();
                           outputAttrToWriter(var14.getNodeName(), var14.getNodeValue(), var7, var9);
                        }
                     }

                     if (var3) {
                        var7.write(62);
                     }

                     var5 = var1.getFirstChild();
                     if (var5 == null) {
                        if (var3) {
                           var7.write((byte[])END_TAG.clone());
                           UtfHelpper.writeByte(var11, var7, var9);
                           var7.write(62);
                           var4.outputNodePop();
                        } else {
                           var4.pop();
                        }

                        if (var6 != null) {
                           var5 = var1.getNextSibling();
                        }
                     } else {
                        var6 = var15;
                     }
                  }
                  break;
               case 2:
               case 6:
               case 12:
                  throw new CanonicalizationException("empty");
               case 3:
               case 4:
                  if (this.isVisible(var1)) {
                     outputTextToWriter(var1.getNodeValue(), var7);

                     for(Node var10 = var1.getNextSibling(); var10 != null && (var10.getNodeType() == 3 || var10.getNodeType() == 4); var10 = var10.getNextSibling()) {
                        outputTextToWriter(var10.getNodeValue(), var7);
                        var5 = var10.getNextSibling();
                     }
                  }
               case 5:
               case 10:
               default:
                  break;
               case 7:
                  if (this.isVisible(var1)) {
                     this.outputPItoWriter((ProcessingInstruction)var1, var7, var8);
                  }
                  break;
               case 8:
                  if (this.includeComments && this.isVisibleDO(var1, var4.getLevel()) == 1) {
                     this.outputCommentToWriter((Comment)var1, var7, var8);
                  }
                  break;
               case 9:
               case 11:
                  var4.outputNodePush();
                  var5 = var1.getFirstChild();
               }

               while(var5 == null && var6 != null) {
                  if (this.isVisible((Node)var6)) {
                     var7.write((byte[])END_TAG.clone());
                     UtfHelpper.writeByte(((Element)var6).getTagName(), var7, var9);
                     var7.write(62);
                     var4.outputNodePop();
                  } else {
                     var4.pop();
                  }

                  if (var6 == var2) {
                     return;
                  }

                  var5 = ((Node)var6).getNextSibling();
                  var6 = ((Node)var6).getParentNode();
                  if (var6 == null || 1 != ((Node)var6).getNodeType()) {
                     var6 = null;
                     var8 = 1;
                  }
               }

               if (var5 == null) {
                  return;
               }

               var1 = var5;
               var5 = var5.getNextSibling();
            }
         }
      }
   }

   protected int isVisibleDO(Node var1, int var2) {
      if (this.nodeFilter != null) {
         Iterator var3 = this.nodeFilter.iterator();

         while(var3.hasNext()) {
            int var4 = ((NodeFilter)var3.next()).isNodeIncludeDO(var1, var2);
            if (var4 != 1) {
               return var4;
            }
         }
      }

      return this.xpathNodeSet != null && !this.xpathNodeSet.contains(var1) ? 0 : 1;
   }

   protected int isVisibleInt(Node var1) {
      if (this.nodeFilter != null) {
         Iterator var2 = this.nodeFilter.iterator();

         while(var2.hasNext()) {
            int var3 = ((NodeFilter)var2.next()).isNodeInclude(var1);
            if (var3 != 1) {
               return var3;
            }
         }
      }

      return this.xpathNodeSet != null && !this.xpathNodeSet.contains(var1) ? 0 : 1;
   }

   protected boolean isVisible(Node var1) {
      if (this.nodeFilter != null) {
         Iterator var2 = this.nodeFilter.iterator();

         while(var2.hasNext()) {
            if (((NodeFilter)var2.next()).isNodeInclude(var1) != 1) {
               return false;
            }
         }
      }

      return this.xpathNodeSet == null || this.xpathNodeSet.contains(var1);
   }

   protected void handleParent(Element var1, NameSpaceSymbTable var2) {
      if (var1.hasAttributes() || var1.getNamespaceURI() != null) {
         NamedNodeMap var3 = var1.getAttributes();
         int var4 = var3.getLength();

         String var7;
         for(int var5 = 0; var5 < var4; ++var5) {
            Attr var6 = (Attr)var3.item(var5);
            var7 = var6.getLocalName();
            String var8 = var6.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(var6.getNamespaceURI()) && (!"xml".equals(var7) || !"http://www.w3.org/XML/1998/namespace".equals(var8))) {
               var2.addMapping(var7, var8, var6);
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

   protected final void getParentNameSpaces(Element var1, NameSpaceSymbTable var2) {
      Node var3 = var1.getParentNode();
      if (var3 != null && 1 == var3.getNodeType()) {
         ArrayList var4 = new ArrayList();

         for(Node var5 = var3; var5 != null && 1 == var5.getNodeType(); var5 = var5.getParentNode()) {
            var4.add((Element)var5);
         }

         ListIterator var6 = var4.listIterator(var4.size());

         while(var6.hasPrevious()) {
            Element var7 = (Element)var6.previous();
            this.handleParent(var7, var2);
         }

         var4.clear();
         Attr var8;
         if ((var8 = var2.getMappingWithoutRendered("xmlns")) != null && "".equals(var8.getValue())) {
            var2.addMappingAndRender("xmlns", "", this.getNullNode(var8.getOwnerDocument()));
         }

      }
   }

   abstract Iterator<Attr> handleAttributes(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException;

   abstract Iterator<Attr> handleAttributesSubtree(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException;

   abstract void circumventBugIfNeeded(XMLSignatureInput var1) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException;

   protected static final void outputAttrToWriter(String var0, String var1, OutputStream var2, Map<String, byte[]> var3) throws IOException {
      var2.write(32);
      UtfHelpper.writeByte(var0, var2, var3);
      var2.write((byte[])EQUALS_STR.clone());
      int var5 = var1.length();
      int var6 = 0;

      while(true) {
         byte[] var4;
         label23:
         while(true) {
            if (var6 >= var5) {
               var2.write(34);
               return;
            }

            char var7 = var1.charAt(var6++);
            switch(var7) {
            case '\t':
               var4 = (byte[])X9.clone();
               break label23;
            case '\n':
               var4 = (byte[])XA.clone();
               break label23;
            case '\r':
               var4 = (byte[])XD.clone();
               break label23;
            case '"':
               var4 = (byte[])QUOT.clone();
               break label23;
            case '&':
               var4 = (byte[])AMP.clone();
               break label23;
            case '<':
               var4 = (byte[])LT.clone();
               break label23;
            default:
               if (var7 < 128) {
                  var2.write(var7);
               } else {
                  UtfHelpper.writeCharToUtf8(var7, var2);
               }
            }
         }

         var2.write(var4);
      }
   }

   protected void outputPItoWriter(ProcessingInstruction var1, OutputStream var2, int var3) throws IOException {
      if (var3 == 1) {
         var2.write(10);
      }

      var2.write((byte[])BEGIN_PI.clone());
      String var4 = var1.getTarget();
      int var5 = var4.length();

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var4.charAt(var6);
         if (var7 == '\r') {
            var2.write((byte[])XD.clone());
         } else if (var7 < 128) {
            var2.write(var7);
         } else {
            UtfHelpper.writeCharToUtf8(var7, var2);
         }
      }

      String var9 = var1.getData();
      var5 = var9.length();
      if (var5 > 0) {
         var2.write(32);

         for(int var10 = 0; var10 < var5; ++var10) {
            char var8 = var9.charAt(var10);
            if (var8 == '\r') {
               var2.write((byte[])XD.clone());
            } else {
               UtfHelpper.writeCharToUtf8(var8, var2);
            }
         }
      }

      var2.write((byte[])END_PI.clone());
      if (var3 == -1) {
         var2.write(10);
      }

   }

   protected void outputCommentToWriter(Comment var1, OutputStream var2, int var3) throws IOException {
      if (var3 == 1) {
         var2.write(10);
      }

      var2.write((byte[])BEGIN_COMM.clone());
      String var4 = var1.getData();
      int var5 = var4.length();

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var4.charAt(var6);
         if (var7 == '\r') {
            var2.write((byte[])XD.clone());
         } else if (var7 < 128) {
            var2.write(var7);
         } else {
            UtfHelpper.writeCharToUtf8(var7, var2);
         }
      }

      var2.write((byte[])END_COMM.clone());
      if (var3 == -1) {
         var2.write(10);
      }

   }

   protected static final void outputTextToWriter(String var0, OutputStream var1) throws IOException {
      int var2 = var0.length();

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var0.charAt(var4);
         byte[] var3;
         switch(var5) {
         case '\r':
            var3 = (byte[])XD.clone();
            break;
         case '&':
            var3 = (byte[])AMP.clone();
            break;
         case '<':
            var3 = (byte[])LT.clone();
            break;
         case '>':
            var3 = (byte[])GT.clone();
            break;
         default:
            if (var5 < 128) {
               var1.write(var5);
            } else {
               UtfHelpper.writeCharToUtf8(var5, var1);
            }
            continue;
         }

         var1.write(var3);
      }

   }

   protected Attr getNullNode(Document var1) {
      if (this.nullNode == null) {
         try {
            this.nullNode = var1.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
            this.nullNode.setValue("");
         } catch (Exception var3) {
            throw new RuntimeException("Unable to create nullNode: " + var3);
         }
      }

      return this.nullNode;
   }
}
