package com.sun.beans.decoder;

import com.sun.beans.finder.ClassFinder;
import java.beans.ExceptionListener;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.misc.SharedSecrets;

public final class DocumentHandler extends DefaultHandler {
   private final AccessControlContext acc = AccessController.getContext();
   private final Map<String, Class<? extends ElementHandler>> handlers = new HashMap();
   private final Map<String, Object> environment = new HashMap();
   private final List<Object> objects = new ArrayList();
   private Reference<ClassLoader> loader;
   private ExceptionListener listener;
   private Object owner;
   private ElementHandler handler;

   public DocumentHandler() {
      this.setElementHandler("java", JavaElementHandler.class);
      this.setElementHandler("null", NullElementHandler.class);
      this.setElementHandler("array", ArrayElementHandler.class);
      this.setElementHandler("class", ClassElementHandler.class);
      this.setElementHandler("string", StringElementHandler.class);
      this.setElementHandler("object", ObjectElementHandler.class);
      this.setElementHandler("void", VoidElementHandler.class);
      this.setElementHandler("char", CharElementHandler.class);
      this.setElementHandler("byte", ByteElementHandler.class);
      this.setElementHandler("short", ShortElementHandler.class);
      this.setElementHandler("int", IntElementHandler.class);
      this.setElementHandler("long", LongElementHandler.class);
      this.setElementHandler("float", FloatElementHandler.class);
      this.setElementHandler("double", DoubleElementHandler.class);
      this.setElementHandler("boolean", BooleanElementHandler.class);
      this.setElementHandler("new", NewElementHandler.class);
      this.setElementHandler("var", VarElementHandler.class);
      this.setElementHandler("true", TrueElementHandler.class);
      this.setElementHandler("false", FalseElementHandler.class);
      this.setElementHandler("field", FieldElementHandler.class);
      this.setElementHandler("method", MethodElementHandler.class);
      this.setElementHandler("property", PropertyElementHandler.class);
   }

   public ClassLoader getClassLoader() {
      return this.loader != null ? (ClassLoader)this.loader.get() : null;
   }

   public void setClassLoader(ClassLoader var1) {
      this.loader = new WeakReference(var1);
   }

   public ExceptionListener getExceptionListener() {
      return this.listener;
   }

   public void setExceptionListener(ExceptionListener var1) {
      this.listener = var1;
   }

   public Object getOwner() {
      return this.owner;
   }

   public void setOwner(Object var1) {
      this.owner = var1;
   }

   public Class<? extends ElementHandler> getElementHandler(String var1) {
      Class var2 = (Class)this.handlers.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Unsupported element: " + var1);
      } else {
         return var2;
      }
   }

   public void setElementHandler(String var1, Class<? extends ElementHandler> var2) {
      this.handlers.put(var1, var2);
   }

   public boolean hasVariable(String var1) {
      return this.environment.containsKey(var1);
   }

   public Object getVariable(String var1) {
      if (!this.environment.containsKey(var1)) {
         throw new IllegalArgumentException("Unbound variable: " + var1);
      } else {
         return this.environment.get(var1);
      }
   }

   public void setVariable(String var1, Object var2) {
      this.environment.put(var1, var2);
   }

   public Object[] getObjects() {
      return this.objects.toArray();
   }

   void addObject(Object var1) {
      this.objects.add(var1);
   }

   public InputSource resolveEntity(String var1, String var2) {
      return new InputSource(new StringReader(""));
   }

   public void startDocument() {
      this.objects.clear();
      this.handler = null;
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      ElementHandler var5 = this.handler;

      try {
         this.handler = (ElementHandler)this.getElementHandler(var3).newInstance();
         this.handler.setOwner(this);
         this.handler.setParent(var5);
      } catch (Exception var10) {
         throw new SAXException(var10);
      }

      for(int var6 = 0; var6 < var4.getLength(); ++var6) {
         try {
            String var7 = var4.getQName(var6);
            String var8 = var4.getValue(var6);
            this.handler.addAttribute(var7, var8);
         } catch (RuntimeException var9) {
            this.handleException(var9);
         }
      }

      this.handler.startElement();
   }

   public void endElement(String var1, String var2, String var3) {
      try {
         this.handler.endElement();
      } catch (RuntimeException var8) {
         this.handleException(var8);
      } finally {
         this.handler = this.handler.getParent();
      }

   }

   public void characters(char[] var1, int var2, int var3) {
      if (this.handler != null) {
         try {
            while(0 < var3--) {
               this.handler.addCharacter(var1[var2++]);
            }
         } catch (RuntimeException var5) {
            this.handleException(var5);
         }
      }

   }

   public void handleException(Exception var1) {
      if (this.listener == null) {
         throw new IllegalStateException(var1);
      } else {
         this.listener.exceptionThrown(var1);
      }
   }

   public void parse(final InputSource var1) {
      if (this.acc == null && null != System.getSecurityManager()) {
         throw new SecurityException("AccessControlContext is not set");
      } else {
         AccessControlContext var2 = AccessController.getContext();
         SharedSecrets.getJavaSecurityAccess().doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  SAXParserFactory.newInstance().newSAXParser().parse((InputSource)var1, (DefaultHandler)DocumentHandler.this);
               } catch (ParserConfigurationException var3) {
                  DocumentHandler.this.handleException(var3);
               } catch (SAXException var4) {
                  Object var2 = var4.getException();
                  if (var2 == null) {
                     var2 = var4;
                  }

                  DocumentHandler.this.handleException((Exception)var2);
               } catch (IOException var5) {
                  DocumentHandler.this.handleException(var5);
               }

               return null;
            }
         }, var2, this.acc);
      }
   }

   public Class<?> findClass(String var1) {
      try {
         return ClassFinder.resolveClass(var1, this.getClassLoader());
      } catch (ClassNotFoundException var3) {
         this.handleException(var3);
         return null;
      }
   }
}
