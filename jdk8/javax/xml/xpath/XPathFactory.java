package javax.xml.xpath;

public abstract class XPathFactory {
   public static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
   public static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";
   private static SecuritySupport ss = new SecuritySupport();

   protected XPathFactory() {
   }

   public static XPathFactory newInstance() {
      try {
         return newInstance("http://java.sun.com/jaxp/xpath/dom");
      } catch (XPathFactoryConfigurationException var1) {
         throw new RuntimeException("XPathFactory#newInstance() failed to create an XPathFactory for the default object model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: " + var1.toString());
      }
   }

   public static XPathFactory newInstance(String uri) throws XPathFactoryConfigurationException {
      if (uri == null) {
         throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
      } else if (uri.length() == 0) {
         throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
      } else {
         ClassLoader classLoader = ss.getContextClassLoader();
         if (classLoader == null) {
            classLoader = XPathFactory.class.getClassLoader();
         }

         XPathFactory xpathFactory = (new XPathFactoryFinder(classLoader)).newFactory(uri);
         if (xpathFactory == null) {
            throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + uri);
         } else {
            return xpathFactory;
         }
      }
   }

   public static XPathFactory newInstance(String uri, String factoryClassName, ClassLoader classLoader) throws XPathFactoryConfigurationException {
      ClassLoader cl = classLoader;
      if (uri == null) {
         throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
      } else if (uri.length() == 0) {
         throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
      } else {
         if (classLoader == null) {
            cl = ss.getContextClassLoader();
         }

         XPathFactory f = (new XPathFactoryFinder(cl)).createInstance(factoryClassName);
         if (f == null) {
            throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + uri);
         } else if (f.isObjectModelSupported(uri)) {
            return f;
         } else {
            throw new XPathFactoryConfigurationException("Factory " + factoryClassName + " doesn't support given " + uri + " object model");
         }
      }
   }

   public abstract boolean isObjectModelSupported(String var1);

   public abstract void setFeature(String var1, boolean var2) throws XPathFactoryConfigurationException;

   public abstract boolean getFeature(String var1) throws XPathFactoryConfigurationException;

   public abstract void setXPathVariableResolver(XPathVariableResolver var1);

   public abstract void setXPathFunctionResolver(XPathFunctionResolver var1);

   public abstract XPath newXPath();
}
