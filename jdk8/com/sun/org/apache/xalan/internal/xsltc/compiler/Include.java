package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.util.Iterator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

final class Include extends TopLevelElement {
   private Stylesheet _included = null;

   public Stylesheet getIncludedStylesheet() {
      return this._included;
   }

   public void parseContents(Parser parser) {
      XSLTC xsltc = parser.getXSLTC();
      Stylesheet context = parser.getCurrentStylesheet();
      String docToLoad = this.getAttribute("href");

      try {
         if (context.checkForLoop(docToLoad)) {
            ErrorMsg msg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", docToLoad, this);
            parser.reportError(2, msg);
            return;
         }

         InputSource input = null;
         XMLReader reader = null;
         String currLoadedDoc = context.getSystemId();
         SourceLoader loader = context.getSourceLoader();
         if (loader != null) {
            input = loader.loadSource(docToLoad, currLoadedDoc, xsltc);
            if (input != null) {
               docToLoad = input.getSystemId();
               reader = xsltc.getXMLReader();
            } else if (parser.errorsFound()) {
               return;
            }
         }

         if (input == null) {
            docToLoad = SystemIDResolver.getAbsoluteURI(docToLoad, currLoadedDoc);
            String accessError = SecuritySupport.checkAccess(docToLoad, (String)xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
            if (accessError != null) {
               ErrorMsg msg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(docToLoad), accessError, this);
               parser.reportError(2, msg);
               return;
            }

            input = new InputSource(docToLoad);
         }

         if (input == null) {
            ErrorMsg msg = new ErrorMsg("FILE_NOT_FOUND_ERR", docToLoad, this);
            parser.reportError(2, msg);
            return;
         }

         SyntaxTreeNode root;
         if (reader != null) {
            root = parser.parse(reader, input);
         } else {
            root = parser.parse(input);
         }

         if (root != null) {
            this._included = parser.makeStylesheet(root);
            if (this._included == null) {
               return;
            }

            this._included.setSourceLoader(loader);
            this._included.setSystemId(docToLoad);
            this._included.setParentStylesheet(context);
            this._included.setIncludingStylesheet(context);
            this._included.setTemplateInlining(context.getTemplateInlining());
            int precedence = context.getImportPrecedence();
            this._included.setImportPrecedence(precedence);
            parser.setCurrentStylesheet(this._included);
            this._included.parseContents(parser);
            Iterator<SyntaxTreeNode> elements = this._included.elements();
            Stylesheet topStylesheet = parser.getTopLevelStylesheet();

            while(elements.hasNext()) {
               SyntaxTreeNode element = (SyntaxTreeNode)elements.next();
               if (element instanceof TopLevelElement) {
                  if (element instanceof Variable) {
                     topStylesheet.addVariable((Variable)element);
                  } else if (element instanceof Param) {
                     topStylesheet.addParam((Param)element);
                  } else {
                     topStylesheet.addElement((TopLevelElement)element);
                  }
               }
            }

            return;
         }
      } catch (Exception var17) {
         var17.printStackTrace();
         return;
      } finally {
         parser.setCurrentStylesheet(context);
      }

   }

   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
      return Type.Void;
   }

   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
   }
}
