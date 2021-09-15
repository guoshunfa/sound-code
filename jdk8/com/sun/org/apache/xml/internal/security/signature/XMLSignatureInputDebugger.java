package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class XMLSignatureInputDebugger {
   private Set<Node> xpathNodeSet;
   private Set<String> inclusiveNamespaces;
   private Document doc;
   private Writer writer;
   static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n";
   static final String HTMLSuffix = "</pre></body></html>";
   static final String HTMLExcludePrefix = "<span class=\"EXCLUDED\">";
   static final String HTMLIncludePrefix = "<span class=\"INCLUDED\">";
   static final String HTMLIncludeOrExcludeSuffix = "</span>";
   static final String HTMLIncludedInclusiveNamespacePrefix = "<span class=\"INCLUDEDINCLUSIVENAMESPACE\">";
   static final String HTMLExcludedInclusiveNamespacePrefix = "<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">";
   private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
   private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
   private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
   static final AttrCompare ATTR_COMPARE = new AttrCompare();

   public XMLSignatureInputDebugger(XMLSignatureInput var1) {
      this.doc = null;
      this.writer = null;
      if (!var1.isNodeSet()) {
         this.xpathNodeSet = null;
      } else {
         this.xpathNodeSet = var1.getInputNodeSet();
      }

   }

   public XMLSignatureInputDebugger(XMLSignatureInput var1, Set<String> var2) {
      this(var1);
      this.inclusiveNamespaces = var2;
   }

   public String getHTMLRepresentation() throws XMLSignatureException {
      if (this.xpathNodeSet != null && this.xpathNodeSet.size() != 0) {
         Node var1 = (Node)this.xpathNodeSet.iterator().next();
         this.doc = XMLUtils.getOwnerDocument(var1);

         String var2;
         try {
            this.writer = new StringWriter();
            this.canonicalizeXPathNodeSet(this.doc);
            this.writer.close();
            var2 = this.writer.toString();
         } catch (IOException var6) {
            throw new XMLSignatureException("empty", var6);
         } finally {
            this.xpathNodeSet = null;
            this.doc = null;
            this.writer = null;
         }

         return var2;
      } else {
         return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n<blink>no node set, sorry</blink></pre></body></html>";
      }
   }

   private void canonicalizeXPathNodeSet(Node var1) throws XMLSignatureException, IOException {
      short var2 = var1.getNodeType();
      int var13;
      switch(var2) {
      case 1:
         Element var14 = (Element)var1;
         if (this.xpathNodeSet.contains(var1)) {
            this.writer.write("<span class=\"INCLUDED\">");
         } else {
            this.writer.write("<span class=\"EXCLUDED\">");
         }

         this.writer.write("&lt;");
         this.writer.write(var14.getTagName());
         this.writer.write("</span>");
         NamedNodeMap var5 = var14.getAttributes();
         int var6 = var5.getLength();
         Attr[] var7 = new Attr[var6];

         for(int var8 = 0; var8 < var6; ++var8) {
            var7[var8] = (Attr)var5.item(var8);
         }

         Arrays.sort(var7, ATTR_COMPARE);
         Attr[] var15 = var7;

         for(int var9 = 0; var9 < var6; ++var9) {
            Attr var10 = (Attr)var15[var9];
            boolean var11 = this.xpathNodeSet.contains(var10);
            boolean var12 = this.inclusiveNamespaces.contains(var10.getName());
            if (var11) {
               if (var12) {
                  this.writer.write("<span class=\"INCLUDEDINCLUSIVENAMESPACE\">");
               } else {
                  this.writer.write("<span class=\"INCLUDED\">");
               }
            } else if (var12) {
               this.writer.write("<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">");
            } else {
               this.writer.write("<span class=\"EXCLUDED\">");
            }

            this.outputAttrToWriter(var10.getNodeName(), var10.getNodeValue());
            this.writer.write("</span>");
         }

         if (this.xpathNodeSet.contains(var1)) {
            this.writer.write("<span class=\"INCLUDED\">");
         } else {
            this.writer.write("<span class=\"EXCLUDED\">");
         }

         this.writer.write("&gt;");
         this.writer.write("</span>");

         for(Node var16 = var1.getFirstChild(); var16 != null; var16 = var16.getNextSibling()) {
            this.canonicalizeXPathNodeSet(var16);
         }

         if (this.xpathNodeSet.contains(var1)) {
            this.writer.write("<span class=\"INCLUDED\">");
         } else {
            this.writer.write("<span class=\"EXCLUDED\">");
         }

         this.writer.write("&lt;/");
         this.writer.write(var14.getTagName());
         this.writer.write("&gt;");
         this.writer.write("</span>");
         break;
      case 2:
      case 6:
      case 11:
      case 12:
         throw new XMLSignatureException("empty");
      case 3:
      case 4:
         if (this.xpathNodeSet.contains(var1)) {
            this.writer.write("<span class=\"INCLUDED\">");
         } else {
            this.writer.write("<span class=\"EXCLUDED\">");
         }

         this.outputTextToWriter(var1.getNodeValue());

         for(Node var4 = var1.getNextSibling(); var4 != null && (var4.getNodeType() == 3 || var4.getNodeType() == 4); var4 = var4.getNextSibling()) {
            this.outputTextToWriter(var4.getNodeValue());
         }

         this.writer.write("</span>");
      case 5:
      case 10:
      default:
         break;
      case 7:
         if (this.xpathNodeSet.contains(var1)) {
            this.writer.write("<span class=\"INCLUDED\">");
         } else {
            this.writer.write("<span class=\"EXCLUDED\">");
         }

         var13 = this.getPositionRelativeToDocumentElement(var1);
         if (var13 == 1) {
            this.writer.write("\n");
         }

         this.outputPItoWriter((ProcessingInstruction)var1);
         if (var13 == -1) {
            this.writer.write("\n");
         }

         this.writer.write("</span>");
         break;
      case 8:
         if (this.xpathNodeSet.contains(var1)) {
            this.writer.write("<span class=\"INCLUDED\">");
         } else {
            this.writer.write("<span class=\"EXCLUDED\">");
         }

         var13 = this.getPositionRelativeToDocumentElement(var1);
         if (var13 == 1) {
            this.writer.write("\n");
         }

         this.outputCommentToWriter((Comment)var1);
         if (var13 == -1) {
            this.writer.write("\n");
         }

         this.writer.write("</span>");
         break;
      case 9:
         this.writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Caninical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n");

         for(Node var3 = var1.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
            this.canonicalizeXPathNodeSet(var3);
         }

         this.writer.write("</pre></body></html>");
      }

   }

   private int getPositionRelativeToDocumentElement(Node var1) {
      if (var1 == null) {
         return 0;
      } else {
         Document var2 = var1.getOwnerDocument();
         if (var1.getParentNode() != var2) {
            return 0;
         } else {
            Element var3 = var2.getDocumentElement();
            if (var3 == null) {
               return 0;
            } else if (var3 == var1) {
               return 0;
            } else {
               for(Node var4 = var1; var4 != null; var4 = var4.getNextSibling()) {
                  if (var4 == var3) {
                     return -1;
                  }
               }

               return 1;
            }
         }
      }
   }

   private void outputAttrToWriter(String var1, String var2) throws IOException {
      this.writer.write(" ");
      this.writer.write(var1);
      this.writer.write("=\"");
      int var3 = var2.length();

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var2.charAt(var4);
         switch(var5) {
         case '\t':
            this.writer.write("&amp;#x9;");
            break;
         case '\n':
            this.writer.write("&amp;#xA;");
            break;
         case '\r':
            this.writer.write("&amp;#xD;");
            break;
         case '"':
            this.writer.write("&amp;quot;");
            break;
         case '&':
            this.writer.write("&amp;amp;");
            break;
         case '<':
            this.writer.write("&amp;lt;");
            break;
         default:
            this.writer.write(var5);
         }
      }

      this.writer.write("\"");
   }

   private void outputPItoWriter(ProcessingInstruction var1) throws IOException {
      if (var1 != null) {
         this.writer.write("&lt;?");
         String var2 = var1.getTarget();
         int var3 = var2.length();

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var2.charAt(var4);
            switch(var5) {
            case '\n':
               this.writer.write("&para;\n");
               break;
            case '\r':
               this.writer.write("&amp;#xD;");
               break;
            case ' ':
               this.writer.write("&middot;");
               break;
            default:
               this.writer.write(var5);
            }
         }

         String var7 = var1.getData();
         var3 = var7.length();
         if (var3 > 0) {
            this.writer.write(" ");

            for(int var8 = 0; var8 < var3; ++var8) {
               char var6 = var7.charAt(var8);
               switch(var6) {
               case '\r':
                  this.writer.write("&amp;#xD;");
                  break;
               default:
                  this.writer.write(var6);
               }
            }
         }

         this.writer.write("?&gt;");
      }
   }

   private void outputCommentToWriter(Comment var1) throws IOException {
      if (var1 != null) {
         this.writer.write("&lt;!--");
         String var2 = var1.getData();
         int var3 = var2.length();

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var2.charAt(var4);
            switch(var5) {
            case '\n':
               this.writer.write("&para;\n");
               break;
            case '\r':
               this.writer.write("&amp;#xD;");
               break;
            case ' ':
               this.writer.write("&middot;");
               break;
            default:
               this.writer.write(var5);
            }
         }

         this.writer.write("--&gt;");
      }
   }

   private void outputTextToWriter(String var1) throws IOException {
      if (var1 != null) {
         int var2 = var1.length();

         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1.charAt(var3);
            switch(var4) {
            case '\n':
               this.writer.write("&para;\n");
               break;
            case '\r':
               this.writer.write("&amp;#xD;");
               break;
            case ' ':
               this.writer.write("&middot;");
               break;
            case '&':
               this.writer.write("&amp;amp;");
               break;
            case '<':
               this.writer.write("&amp;lt;");
               break;
            case '>':
               this.writer.write("&amp;gt;");
               break;
            default:
               this.writer.write(var4);
            }
         }

      }
   }
}
