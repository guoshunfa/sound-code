package javax.xml.bind;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import org.w3c.dom.Node;

public abstract class JAXBContext {
   public static final String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";

   protected JAXBContext() {
   }

   public static JAXBContext newInstance(String contextPath) throws JAXBException {
      return newInstance(contextPath, getContextClassLoader());
   }

   public static JAXBContext newInstance(String contextPath, ClassLoader classLoader) throws JAXBException {
      return newInstance(contextPath, classLoader, Collections.emptyMap());
   }

   public static JAXBContext newInstance(String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
      return ContextFinder.find("javax.xml.bind.context.factory", contextPath, classLoader, properties);
   }

   public static JAXBContext newInstance(Class... classesToBeBound) throws JAXBException {
      return newInstance(classesToBeBound, Collections.emptyMap());
   }

   public static JAXBContext newInstance(Class[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
      if (classesToBeBound == null) {
         throw new IllegalArgumentException();
      } else {
         for(int i = classesToBeBound.length - 1; i >= 0; --i) {
            if (classesToBeBound[i] == null) {
               throw new IllegalArgumentException();
            }
         }

         return ContextFinder.find(classesToBeBound, properties);
      }
   }

   public abstract Unmarshaller createUnmarshaller() throws JAXBException;

   public abstract Marshaller createMarshaller() throws JAXBException;

   /** @deprecated */
   public abstract Validator createValidator() throws JAXBException;

   public <T> Binder<T> createBinder(Class<T> domType) {
      throw new UnsupportedOperationException();
   }

   public Binder<Node> createBinder() {
      return this.createBinder(Node.class);
   }

   public JAXBIntrospector createJAXBIntrospector() {
      throw new UnsupportedOperationException();
   }

   public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
      throw new UnsupportedOperationException();
   }

   private static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }
}
