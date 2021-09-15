package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;

public abstract class Loader {
   protected boolean expectText;

   protected Loader(boolean expectText) {
      this.expectText = expectText;
   }

   protected Loader() {
   }

   public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
   }

   public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
      this.reportUnexpectedChildElement(ea, true);
      state.setLoader(Discarder.INSTANCE);
      state.setReceiver((Receiver)null);
   }

   protected final void reportUnexpectedChildElement(TagName ea, boolean canRecover) throws SAXException {
      if (canRecover) {
         UnmarshallingContext context = UnmarshallingContext.getInstance();
         if (!context.parent.hasEventHandler() || !context.shouldErrorBeReported()) {
            return;
         }
      }

      if (ea.uri == ea.uri.intern() && ea.local == ea.local.intern()) {
         reportError(Messages.UNEXPECTED_ELEMENT.format(ea.uri, ea.local, this.computeExpectedElements()), canRecover);
      } else {
         reportError(Messages.UNINTERNED_STRINGS.format(), canRecover);
      }

   }

   public Collection<QName> getExpectedChildElements() {
      return Collections.emptyList();
   }

   public Collection<QName> getExpectedAttributes() {
      return Collections.emptyList();
   }

   public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
      CharSequence text = text.toString().replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').trim();
      reportError(Messages.UNEXPECTED_TEXT.format(text), true);
   }

   public final boolean expectText() {
      return this.expectText;
   }

   public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
   }

   private String computeExpectedElements() {
      StringBuilder r = new StringBuilder();

      QName n;
      for(Iterator var2 = this.getExpectedChildElements().iterator(); var2.hasNext(); r.append("<{").append(n.getNamespaceURI()).append('}').append(n.getLocalPart()).append('>')) {
         n = (QName)var2.next();
         if (r.length() != 0) {
            r.append(',');
         }
      }

      if (r.length() == 0) {
         return "(none)";
      } else {
         return r.toString();
      }
   }

   protected final void fireBeforeUnmarshal(JaxBeanInfo beanInfo, Object child, UnmarshallingContext.State state) throws SAXException {
      if (beanInfo.lookForLifecycleMethods()) {
         UnmarshallingContext context = state.getContext();
         Unmarshaller.Listener listener = context.parent.getListener();
         if (beanInfo.hasBeforeUnmarshalMethod()) {
            beanInfo.invokeBeforeUnmarshalMethod(context.parent, child, state.getPrev().getTarget());
         }

         if (listener != null) {
            listener.beforeUnmarshal(child, state.getPrev().getTarget());
         }
      }

   }

   protected final void fireAfterUnmarshal(JaxBeanInfo beanInfo, Object child, UnmarshallingContext.State state) throws SAXException {
      if (beanInfo.lookForLifecycleMethods()) {
         UnmarshallingContext context = state.getContext();
         Unmarshaller.Listener listener = context.parent.getListener();
         if (beanInfo.hasAfterUnmarshalMethod()) {
            beanInfo.invokeAfterUnmarshalMethod(context.parent, child, state.getTarget());
         }

         if (listener != null) {
            listener.afterUnmarshal(child, state.getTarget());
         }
      }

   }

   protected static void handleGenericException(Exception e) throws SAXException {
      handleGenericException(e, false);
   }

   public static void handleGenericException(Exception e, boolean canRecover) throws SAXException {
      reportError(e.getMessage(), e, canRecover);
   }

   public static void handleGenericError(Error e) throws SAXException {
      reportError(e.getMessage(), false);
   }

   protected static void reportError(String msg, boolean canRecover) throws SAXException {
      reportError(msg, (Exception)null, canRecover);
   }

   public static void reportError(String msg, Exception nested, boolean canRecover) throws SAXException {
      UnmarshallingContext context = UnmarshallingContext.getInstance();
      context.handleEvent(new ValidationEventImpl(canRecover ? 1 : 2, msg, context.getLocator().getLocation(), nested), canRecover);
   }

   protected static void handleParseConversionException(UnmarshallingContext.State state, Exception e) throws SAXException {
      state.getContext().handleError(e);
   }
}
