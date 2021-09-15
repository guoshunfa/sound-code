package java.beans;

import com.sun.beans.decoder.DocumentHandler;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDecoder implements AutoCloseable {
   private final AccessControlContext acc;
   private final DocumentHandler handler;
   private final InputSource input;
   private Object owner;
   private Object[] array;
   private int index;

   public XMLDecoder(InputStream var1) {
      this(var1, (Object)null);
   }

   public XMLDecoder(InputStream var1, Object var2) {
      this(var1, var2, (ExceptionListener)null);
   }

   public XMLDecoder(InputStream var1, Object var2, ExceptionListener var3) {
      this((InputStream)var1, var2, var3, (ClassLoader)null);
   }

   public XMLDecoder(InputStream var1, Object var2, ExceptionListener var3, ClassLoader var4) {
      this(new InputSource(var1), var2, var3, var4);
   }

   public XMLDecoder(InputSource var1) {
      this((InputSource)var1, (Object)null, (ExceptionListener)null, (ClassLoader)null);
   }

   private XMLDecoder(InputSource var1, Object var2, ExceptionListener var3, ClassLoader var4) {
      this.acc = AccessController.getContext();
      this.handler = new DocumentHandler();
      this.input = var1;
      this.owner = var2;
      this.setExceptionListener(var3);
      this.handler.setClassLoader(var4);
      this.handler.setOwner(this);
   }

   public void close() {
      if (this.parsingComplete()) {
         this.close(this.input.getCharacterStream());
         this.close(this.input.getByteStream());
      }

   }

   private void close(Closeable var1) {
      if (var1 != null) {
         try {
            var1.close();
         } catch (IOException var3) {
            this.getExceptionListener().exceptionThrown(var3);
         }
      }

   }

   private boolean parsingComplete() {
      if (this.input == null) {
         return false;
      } else {
         if (this.array == null) {
            if (this.acc == null && null != System.getSecurityManager()) {
               throw new SecurityException("AccessControlContext is not set");
            }

            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  XMLDecoder.this.handler.parse(XMLDecoder.this.input);
                  return null;
               }
            }, this.acc);
            this.array = this.handler.getObjects();
         }

         return true;
      }
   }

   public void setExceptionListener(ExceptionListener var1) {
      if (var1 == null) {
         var1 = Statement.defaultExceptionListener;
      }

      this.handler.setExceptionListener(var1);
   }

   public ExceptionListener getExceptionListener() {
      return this.handler.getExceptionListener();
   }

   public Object readObject() {
      return this.parsingComplete() ? this.array[this.index++] : null;
   }

   public void setOwner(Object var1) {
      this.owner = var1;
   }

   public Object getOwner() {
      return this.owner;
   }

   public static DefaultHandler createHandler(Object var0, ExceptionListener var1, ClassLoader var2) {
      DocumentHandler var3 = new DocumentHandler();
      var3.setOwner(var0);
      var3.setExceptionListener(var1);
      var3.setClassLoader(var2);
      return var3;
   }
}
