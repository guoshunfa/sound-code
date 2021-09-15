package javax.xml.parsers;

import javax.xml.validation.Schema;

public abstract class DocumentBuilderFactory {
   private boolean validating = false;
   private boolean namespaceAware = false;
   private boolean whitespace = false;
   private boolean expandEntityRef = true;
   private boolean ignoreComments = false;
   private boolean coalescing = false;

   protected DocumentBuilderFactory() {
   }

   public static DocumentBuilderFactory newInstance() {
      return (DocumentBuilderFactory)FactoryFinder.find(DocumentBuilderFactory.class, "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
   }

   public static DocumentBuilderFactory newInstance(String factoryClassName, ClassLoader classLoader) {
      return (DocumentBuilderFactory)FactoryFinder.newInstance(DocumentBuilderFactory.class, factoryClassName, classLoader, false);
   }

   public abstract DocumentBuilder newDocumentBuilder() throws ParserConfigurationException;

   public void setNamespaceAware(boolean awareness) {
      this.namespaceAware = awareness;
   }

   public void setValidating(boolean validating) {
      this.validating = validating;
   }

   public void setIgnoringElementContentWhitespace(boolean whitespace) {
      this.whitespace = whitespace;
   }

   public void setExpandEntityReferences(boolean expandEntityRef) {
      this.expandEntityRef = expandEntityRef;
   }

   public void setIgnoringComments(boolean ignoreComments) {
      this.ignoreComments = ignoreComments;
   }

   public void setCoalescing(boolean coalescing) {
      this.coalescing = coalescing;
   }

   public boolean isNamespaceAware() {
      return this.namespaceAware;
   }

   public boolean isValidating() {
      return this.validating;
   }

   public boolean isIgnoringElementContentWhitespace() {
      return this.whitespace;
   }

   public boolean isExpandEntityReferences() {
      return this.expandEntityRef;
   }

   public boolean isIgnoringComments() {
      return this.ignoreComments;
   }

   public boolean isCoalescing() {
      return this.coalescing;
   }

   public abstract void setAttribute(String var1, Object var2) throws IllegalArgumentException;

   public abstract Object getAttribute(String var1) throws IllegalArgumentException;

   public abstract void setFeature(String var1, boolean var2) throws ParserConfigurationException;

   public abstract boolean getFeature(String var1) throws ParserConfigurationException;

   public Schema getSchema() {
      throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }

   public void setSchema(Schema schema) {
      throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }

   public void setXIncludeAware(boolean state) {
      if (state) {
         throw new UnsupportedOperationException(" setXIncludeAware is not supported on this JAXP implementation or earlier: " + this.getClass());
      }
   }

   public boolean isXIncludeAware() {
      throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
   }
}
