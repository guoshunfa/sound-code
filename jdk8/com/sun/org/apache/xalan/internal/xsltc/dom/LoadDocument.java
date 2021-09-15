package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.FileNotFoundException;
import javax.xml.transform.stream.StreamSource;

public final class LoadDocument {
   private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";

   public static DTMAxisIterator documentF(Object arg1, DTMAxisIterator arg2, String xslURI, AbstractTranslet translet, DOM dom) throws TransletException {
      String baseURI = null;
      int arg2FirstNode = arg2.next();
      if (arg2FirstNode == -1) {
         return EmptyIterator.getInstance();
      } else {
         baseURI = dom.getDocumentURI(arg2FirstNode);
         if (!SystemIDResolver.isAbsoluteURI(baseURI)) {
            baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
         }

         try {
            if (arg1 instanceof String) {
               return ((String)arg1).length() == 0 ? document(xslURI, "", translet, dom) : document((String)arg1, baseURI, translet, dom);
            } else if (arg1 instanceof DTMAxisIterator) {
               return document((DTMAxisIterator)arg1, baseURI, translet, dom);
            } else {
               String err = "document(" + arg1.toString() + ")";
               throw new IllegalArgumentException(err);
            }
         } catch (Exception var8) {
            throw new TransletException(var8);
         }
      }
   }

   public static DTMAxisIterator documentF(Object arg, String xslURI, AbstractTranslet translet, DOM dom) throws TransletException {
      try {
         String baseURI;
         if (arg instanceof String) {
            if (xslURI == null) {
               xslURI = "";
            }

            baseURI = xslURI;
            if (!SystemIDResolver.isAbsoluteURI(xslURI)) {
               baseURI = SystemIDResolver.getAbsoluteURIFromRelative(xslURI);
            }

            String href = (String)arg;
            if (href.length() == 0) {
               href = "";
               TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
               DOM sdom = null;
               if (templates != null) {
                  sdom = templates.getStylesheetDOM();
               }

               return sdom != null ? document(sdom, translet, dom) : document(href, baseURI, translet, dom, true);
            } else {
               return document(href, baseURI, translet, dom);
            }
         } else if (arg instanceof DTMAxisIterator) {
            return document((DTMAxisIterator)((DTMAxisIterator)arg), (String)null, translet, dom);
         } else {
            baseURI = "document(" + arg.toString() + ")";
            throw new IllegalArgumentException(baseURI);
         }
      } catch (Exception var8) {
         throw new TransletException(var8);
      }
   }

   private static DTMAxisIterator document(String uri, String base, AbstractTranslet translet, DOM dom) throws Exception {
      return document(uri, base, translet, dom, false);
   }

   private static DTMAxisIterator document(String uri, String base, AbstractTranslet translet, DOM dom, boolean cacheDOM) throws Exception {
      try {
         String originalUri = uri;
         MultiDOM multiplexer = (MultiDOM)dom;
         if (base != null && !base.equals("")) {
            uri = SystemIDResolver.getAbsoluteURI(uri, base);
         }

         if (uri != null && !uri.equals("")) {
            int mask = multiplexer.getDocumentMask(uri);
            if (mask != -1) {
               DOM newDom = ((DOMAdapter)multiplexer.getDOMAdapter(uri)).getDOMImpl();
               if (newDom instanceof DOMEnhancedForDTM) {
                  return new SingletonIterator(((DOMEnhancedForDTM)newDom).getDocument(), true);
               }
            }

            DOMCache cache = translet.getDOMCache();
            mask = multiplexer.nextMask();
            Object newdom;
            if (cache != null) {
               newdom = cache.retrieveDocument(base, originalUri, translet);
               if (newdom == null) {
                  Exception e = new FileNotFoundException(originalUri);
                  throw new TransletException(e);
               }
            } else {
               String accessError = SecuritySupport.checkAccess(uri, translet.getAllowedProtocols(), "all");
               if (accessError != null) {
                  ErrorMsg msg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(uri), accessError);
                  throw new Exception(msg.toString());
               }

               XSLTCDTMManager dtmManager = (XSLTCDTMManager)multiplexer.getDTMManager();
               DOMEnhancedForDTM enhancedDOM = (DOMEnhancedForDTM)dtmManager.getDTM(new StreamSource(uri), false, (DTMWSFilter)null, true, false, translet.hasIdCall(), cacheDOM);
               newdom = enhancedDOM;
               if (cacheDOM) {
                  TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
                  if (templates != null) {
                     templates.setStylesheetDOM(enhancedDOM);
                  }
               }

               translet.prepassDocument(enhancedDOM);
               enhancedDOM.setDocumentURI(uri);
            }

            DOMAdapter domAdapter = translet.makeDOMAdapter((DOM)newdom);
            multiplexer.addDOMAdapter(domAdapter);
            translet.buildKeys(domAdapter, (DTMAxisIterator)null, (SerializationHandler)null, ((DOM)newdom).getDocument());
            return new SingletonIterator(((DOM)newdom).getDocument(), true);
         } else {
            return EmptyIterator.getInstance();
         }
      } catch (Exception var14) {
         throw var14;
      }
   }

   private static DTMAxisIterator document(DTMAxisIterator arg1, String baseURI, AbstractTranslet translet, DOM dom) throws Exception {
      UnionIterator union = new UnionIterator(dom);

      String uri;
      int node;
      for(boolean var5 = true; (node = arg1.next()) != -1; union.addIterator(document(uri, baseURI, translet, dom))) {
         uri = dom.getStringValueX(node);
         if (baseURI == null) {
            baseURI = dom.getDocumentURI(node);
            if (!SystemIDResolver.isAbsoluteURI(baseURI)) {
               baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
            }
         }
      }

      return union;
   }

   private static DTMAxisIterator document(DOM newdom, AbstractTranslet translet, DOM dom) throws Exception {
      DTMManager dtmManager = ((MultiDOM)dom).getDTMManager();
      if (dtmManager != null && newdom instanceof DTM) {
         ((DTM)newdom).migrateTo(dtmManager);
      }

      translet.prepassDocument(newdom);
      DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
      ((MultiDOM)dom).addDOMAdapter(domAdapter);
      translet.buildKeys(domAdapter, (DTMAxisIterator)null, (SerializationHandler)null, newdom.getDocument());
      return new SingletonIterator(newdom.getDocument(), true);
   }
}
