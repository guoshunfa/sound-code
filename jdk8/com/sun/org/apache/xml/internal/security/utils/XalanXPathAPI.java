package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.transforms.implementations.FuncHere;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XalanXPathAPI implements XPathAPI {
   private static Logger log = Logger.getLogger(XalanXPathAPI.class.getName());
   private String xpathStr = null;
   private XPath xpath = null;
   private static FunctionTable funcTable = null;
   private static boolean installed;
   private XPathContext context;

   public NodeList selectNodeList(Node var1, Node var2, String var3, Node var4) throws TransformerException {
      XObject var5 = this.eval(var1, var2, var3, var4);
      return var5.nodelist();
   }

   public boolean evaluate(Node var1, Node var2, String var3, Node var4) throws TransformerException {
      XObject var5 = this.eval(var1, var2, var3, var4);
      return var5.bool();
   }

   public void clear() {
      this.xpathStr = null;
      this.xpath = null;
      this.context = null;
   }

   public static synchronized boolean isInstalled() {
      return installed;
   }

   private XObject eval(Node var1, Node var2, String var3, Node var4) throws TransformerException {
      if (this.context == null) {
         this.context = new XPathContext(var2);
         this.context.setSecureProcessing(true);
      }

      Object var5 = var4.getNodeType() == 9 ? ((Document)var4).getDocumentElement() : var4;
      PrefixResolverDefault var6 = new PrefixResolverDefault((Node)var5);
      if (!var3.equals(this.xpathStr)) {
         if (var3.indexOf("here()") > 0) {
            this.context.reset();
         }

         this.xpath = this.createXPath(var3, var6);
         this.xpathStr = var3;
      }

      int var7 = this.context.getDTMHandleFromNode(var1);
      return this.xpath.execute(this.context, var7, var6);
   }

   private XPath createXPath(String var1, PrefixResolver var2) throws TransformerException {
      XPath var3 = null;
      Class[] var4 = new Class[]{String.class, SourceLocator.class, PrefixResolver.class, Integer.TYPE, ErrorListener.class, FunctionTable.class};
      Object[] var5 = new Object[]{var1, null, var2, 0, null, funcTable};

      try {
         Constructor var6 = XPath.class.getConstructor(var4);
         var3 = (XPath)var6.newInstance(var5);
      } catch (Exception var7) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var7.getMessage(), (Throwable)var7);
         }
      }

      if (var3 == null) {
         var3 = new XPath(var1, (SourceLocator)null, var2, 0, (ErrorListener)null);
      }

      return var3;
   }

   private static synchronized void fixupFunctionTable() {
      installed = false;
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Registering Here function");
      }

      Class[] var0;
      Method var1;
      Object[] var2;
      try {
         var0 = new Class[]{String.class, Expression.class};
         var1 = FunctionTable.class.getMethod("installFunction", var0);
         if ((var1.getModifiers() & 8) != 0) {
            var2 = new Object[]{"here", new FuncHere()};
            var1.invoke((Object)null, var2);
            installed = true;
         }
      } catch (Exception var4) {
         log.log(Level.FINE, (String)"Error installing function using the static installFunction method", (Throwable)var4);
      }

      if (!installed) {
         try {
            funcTable = new FunctionTable();
            var0 = new Class[]{String.class, Class.class};
            var1 = FunctionTable.class.getMethod("installFunction", var0);
            var2 = new Object[]{"here", FuncHere.class};
            var1.invoke(funcTable, var2);
            installed = true;
         } catch (Exception var3) {
            log.log(Level.FINE, (String)"Error installing function using the static installFunction method", (Throwable)var3);
         }
      }

      if (log.isLoggable(Level.FINE)) {
         if (installed) {
            log.log(Level.FINE, "Registered class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
         } else {
            log.log(Level.FINE, "Unable to register class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
         }
      }

   }

   static {
      fixupFunctionTable();
   }
}
