package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.ClassFactory;
import java.util.HashMap;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class Coordinator implements ErrorHandler, ValidationEventHandler {
   private final HashMap<Class<? extends XmlAdapter>, XmlAdapter> adapters = new HashMap();
   private static final ThreadLocal<Coordinator> activeTable = new ThreadLocal();
   private Coordinator old;

   public final XmlAdapter putAdapter(Class<? extends XmlAdapter> c, XmlAdapter a) {
      return a == null ? (XmlAdapter)this.adapters.remove(c) : (XmlAdapter)this.adapters.put(c, a);
   }

   public final <T extends XmlAdapter> T getAdapter(Class<T> key) {
      T v = (XmlAdapter)key.cast(this.adapters.get(key));
      if (v == null) {
         v = (XmlAdapter)ClassFactory.create(key);
         this.putAdapter(key, v);
      }

      return v;
   }

   public <T extends XmlAdapter> boolean containsAdapter(Class<T> type) {
      return this.adapters.containsKey(type);
   }

   protected final void pushCoordinator() {
      this.old = (Coordinator)activeTable.get();
      activeTable.set(this);
   }

   protected final void popCoordinator() {
      if (this.old != null) {
         activeTable.set(this.old);
      } else {
         activeTable.remove();
      }

      this.old = null;
   }

   public static Coordinator _getInstance() {
      return (Coordinator)activeTable.get();
   }

   protected abstract ValidationEventLocator getLocation();

   public final void error(SAXParseException exception) throws SAXException {
      this.propagateEvent(1, exception);
   }

   public final void warning(SAXParseException exception) throws SAXException {
      this.propagateEvent(0, exception);
   }

   public final void fatalError(SAXParseException exception) throws SAXException {
      this.propagateEvent(2, exception);
   }

   private void propagateEvent(int severity, SAXParseException saxException) throws SAXException {
      ValidationEventImpl ve = new ValidationEventImpl(severity, saxException.getMessage(), this.getLocation());
      Exception e = saxException.getException();
      if (e != null) {
         ve.setLinkedException(e);
      } else {
         ve.setLinkedException(saxException);
      }

      boolean result = this.handleEvent(ve);
      if (!result) {
         throw saxException;
      }
   }
}
