package javax.xml.transform;

public abstract class TransformerFactory {
   protected TransformerFactory() {
   }

   public static TransformerFactory newInstance() throws TransformerFactoryConfigurationError {
      return (TransformerFactory)FactoryFinder.find(TransformerFactory.class, "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
   }

   public static TransformerFactory newInstance(String factoryClassName, ClassLoader classLoader) throws TransformerFactoryConfigurationError {
      return (TransformerFactory)FactoryFinder.newInstance(TransformerFactory.class, factoryClassName, classLoader, false);
   }

   public abstract Transformer newTransformer(Source var1) throws TransformerConfigurationException;

   public abstract Transformer newTransformer() throws TransformerConfigurationException;

   public abstract Templates newTemplates(Source var1) throws TransformerConfigurationException;

   public abstract Source getAssociatedStylesheet(Source var1, String var2, String var3, String var4) throws TransformerConfigurationException;

   public abstract void setURIResolver(URIResolver var1);

   public abstract URIResolver getURIResolver();

   public abstract void setFeature(String var1, boolean var2) throws TransformerConfigurationException;

   public abstract boolean getFeature(String var1);

   public abstract void setAttribute(String var1, Object var2);

   public abstract Object getAttribute(String var1);

   public abstract void setErrorListener(ErrorListener var1);

   public abstract ErrorListener getErrorListener();
}
