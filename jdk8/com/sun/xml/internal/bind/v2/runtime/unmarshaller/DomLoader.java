package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.SAXException;

public class DomLoader<ResultT extends Result> extends Loader {
   private final DomHandler<?, ResultT> dom;

   public DomLoader(DomHandler<?, ResultT> dom) {
      super(true);
      this.dom = dom;
   }

   public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
      UnmarshallingContext context = state.getContext();
      if (state.getTarget() == null) {
         state.setTarget(new DomLoader.State(context));
      }

      DomLoader.State s = (DomLoader.State)state.getTarget();

      try {
         s.declarePrefixes(context, context.getNewlyDeclaredPrefixes());
         s.handler.startElement(ea.uri, ea.local, ea.getQname(), ea.atts);
      } catch (SAXException var6) {
         context.handleError((Exception)var6);
         throw var6;
      }
   }

   public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
      state.setLoader(this);
      DomLoader<ResultT>.State s = (DomLoader.State)state.getPrev().getTarget();
      ++s.depth;
      state.setTarget(s);
   }

   public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
      if (text.length() != 0) {
         try {
            DomLoader<ResultT>.State s = (DomLoader.State)state.getTarget();
            s.handler.characters(text.toString().toCharArray(), 0, text.length());
         } catch (SAXException var4) {
            state.getContext().handleError((Exception)var4);
            throw var4;
         }
      }
   }

   public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
      DomLoader<ResultT>.State s = (DomLoader.State)state.getTarget();
      UnmarshallingContext context = state.getContext();

      try {
         s.handler.endElement(ea.uri, ea.local, ea.getQname());
         s.undeclarePrefixes(context.getNewlyDeclaredPrefixes());
      } catch (SAXException var7) {
         context.handleError((Exception)var7);
         throw var7;
      }

      if (--s.depth == 0) {
         try {
            s.undeclarePrefixes(context.getAllDeclaredPrefixes());
            s.handler.endDocument();
         } catch (SAXException var6) {
            context.handleError((Exception)var6);
            throw var6;
         }

         state.setTarget(s.getElement());
      }

   }

   private final class State {
      private TransformerHandler handler = null;
      private final ResultT result;
      int depth = 1;

      public State(UnmarshallingContext context) throws SAXException {
         this.handler = JAXBContextImpl.createTransformerHandler(context.getJAXBContext().disableSecurityProcessing);
         this.result = DomLoader.this.dom.createUnmarshaller(context);
         this.handler.setResult(this.result);

         try {
            this.handler.setDocumentLocator(context.getLocator());
            this.handler.startDocument();
            this.declarePrefixes(context, context.getAllDeclaredPrefixes());
         } catch (SAXException var4) {
            context.handleError((Exception)var4);
            throw var4;
         }
      }

      public Object getElement() {
         return DomLoader.this.dom.getElement(this.result);
      }

      private void declarePrefixes(UnmarshallingContext context, String[] prefixes) throws SAXException {
         for(int i = prefixes.length - 1; i >= 0; --i) {
            String nsUri = context.getNamespaceURI(prefixes[i]);
            if (nsUri == null) {
               throw new IllegalStateException("prefix '" + prefixes[i] + "' isn't bound");
            }

            this.handler.startPrefixMapping(prefixes[i], nsUri);
         }

      }

      private void undeclarePrefixes(String[] prefixes) throws SAXException {
         for(int i = prefixes.length - 1; i >= 0; --i) {
            this.handler.endPrefixMapping(prefixes[i]);
         }

      }
   }
}
