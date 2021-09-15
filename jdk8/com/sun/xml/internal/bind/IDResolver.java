package com.sun.xml.internal.bind;

import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

public abstract class IDResolver {
   public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
   }

   public void endDocument() throws SAXException {
   }

   public abstract void bind(String var1, Object var2) throws SAXException;

   public abstract Callable<?> resolve(String var1, Class var2) throws SAXException;
}
