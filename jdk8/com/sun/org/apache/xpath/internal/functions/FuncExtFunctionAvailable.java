package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class FuncExtFunctionAvailable extends FunctionOneArg {
   static final long serialVersionUID = 5118814314918592241L;
   private transient FunctionTable m_functionTable = null;

   public XObject execute(XPathContext xctxt) throws TransformerException {
      String fullName = this.m_arg0.execute(xctxt).str();
      int indexOfNSSep = fullName.indexOf(58);
      String prefix;
      String namespace;
      String methName;
      if (indexOfNSSep < 0) {
         prefix = "";
         namespace = "http://www.w3.org/1999/XSL/Transform";
         methName = fullName;
      } else {
         prefix = fullName.substring(0, indexOfNSSep);
         namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
         if (null == namespace) {
            return XBoolean.S_FALSE;
         }

         methName = fullName.substring(indexOfNSSep + 1);
      }

      if (namespace.equals("http://www.w3.org/1999/XSL/Transform")) {
         try {
            if (null == this.m_functionTable) {
               this.m_functionTable = new FunctionTable();
            }

            return this.m_functionTable.functionAvailable(methName) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
         } catch (Exception var8) {
            return XBoolean.S_FALSE;
         }
      } else {
         ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();
         return extProvider.functionAvailable(namespace, methName) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
   }

   public void setFunctionTable(FunctionTable aTable) {
      this.m_functionTable = aTable;
   }
}
