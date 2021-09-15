package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public class CanonicalizerPhysical extends CanonicalizerBase {
   private final SortedSet<Attr> result;

   public CanonicalizerPhysical() {
      super(true);
      this.result = new TreeSet(COMPARE);
   }

   public byte[] engineCanonicalizeXPathNodeSet(Set<Node> var1, String var2) throws CanonicalizationException {
      throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
   }

   public byte[] engineCanonicalizeSubTree(Node var1, String var2) throws CanonicalizationException {
      throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
   }

   protected Iterator<Attr> handleAttributesSubtree(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException {
      if (!var1.hasAttributes()) {
         return null;
      } else {
         SortedSet var3 = this.result;
         var3.clear();
         if (var1.hasAttributes()) {
            NamedNodeMap var4 = var1.getAttributes();
            int var5 = var4.getLength();

            for(int var6 = 0; var6 < var5; ++var6) {
               Attr var7 = (Attr)var4.item(var6);
               var3.add(var7);
            }
         }

         return var3.iterator();
      }
   }

   protected Iterator<Attr> handleAttributes(Element var1, NameSpaceSymbTable var2) throws CanonicalizationException {
      throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
   }

   protected void circumventBugIfNeeded(XMLSignatureInput var1) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
   }

   protected void handleParent(Element var1, NameSpaceSymbTable var2) {
   }

   public final String engineGetURI() {
      return "http://santuario.apache.org/c14n/physical";
   }

   public final boolean engineGetIncludeComments() {
      return true;
   }

   protected void outputPItoWriter(ProcessingInstruction var1, OutputStream var2, int var3) throws IOException {
      super.outputPItoWriter(var1, var2, 0);
   }

   protected void outputCommentToWriter(Comment var1, OutputStream var2, int var3) throws IOException {
      super.outputCommentToWriter(var1, var2, 0);
   }
}
