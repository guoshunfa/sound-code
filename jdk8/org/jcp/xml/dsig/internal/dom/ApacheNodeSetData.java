package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.NodeSetData;
import org.w3c.dom.Node;

public class ApacheNodeSetData implements ApacheData, NodeSetData {
   private XMLSignatureInput xi;

   public ApacheNodeSetData(XMLSignatureInput var1) {
      this.xi = var1;
   }

   public Iterator iterator() {
      if (this.xi.getNodeFilters() != null && !this.xi.getNodeFilters().isEmpty()) {
         return Collections.unmodifiableSet(this.getNodeSet(this.xi.getNodeFilters())).iterator();
      } else {
         try {
            return Collections.unmodifiableSet(this.xi.getNodeSet()).iterator();
         } catch (Exception var2) {
            throw new RuntimeException("unrecoverable error retrieving nodeset", var2);
         }
      }
   }

   public XMLSignatureInput getXMLSignatureInput() {
      return this.xi;
   }

   private Set<Node> getNodeSet(List<NodeFilter> var1) {
      if (this.xi.isNeedsToBeExpanded()) {
         XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.xi.getSubNode()));
      }

      LinkedHashSet var2 = new LinkedHashSet();
      XMLUtils.getSet(this.xi.getSubNode(), var2, (Node)null, !this.xi.isExcludeComments());
      LinkedHashSet var3 = new LinkedHashSet();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Node var5 = (Node)var4.next();
         Iterator var6 = var1.iterator();
         boolean var7 = false;

         while(var6.hasNext() && !var7) {
            NodeFilter var8 = (NodeFilter)var6.next();
            if (var8.isNodeInclude(var5) != 1) {
               var7 = true;
            }
         }

         if (!var7) {
            var3.add(var5);
         }
      }

      return var3;
   }
}
