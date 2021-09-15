package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FuncHere extends Function {
   private static final long serialVersionUID = 1L;

   public XObject execute(XPathContext var1) throws TransformerException {
      Node var2 = (Node)var1.getOwnerObject();
      if (var2 == null) {
         return null;
      } else {
         int var3 = var1.getDTMHandleFromNode(var2);
         int var4 = var1.getCurrentNode();
         DTM var5 = var1.getDTM(var4);
         int var6 = var5.getDocument();
         if (-1 == var6) {
            this.error(var1, "ER_CONTEXT_HAS_NO_OWNERDOC", (Object[])null);
         }

         Document var7 = XMLUtils.getOwnerDocument(var5.getNode(var4));
         Document var8 = XMLUtils.getOwnerDocument(var2);
         if (var7 != var8) {
            throw new TransformerException(I18n.translate("xpath.funcHere.documentsDiffer"));
         } else {
            XNodeSet var10 = new XNodeSet(var1.getDTMManager());
            NodeSetDTM var11 = var10.mutableNodeset();
            boolean var9 = true;
            switch(var5.getNodeType(var3)) {
            case 2:
            case 7:
               var11.addNode(var3);
               break;
            case 3:
               int var12 = var5.getParent(var3);
               var11.addNode(var12);
            }

            var11.detach();
            return var10;
         }
      }
   }

   public void fixupVariables(Vector var1, int var2) {
   }
}
