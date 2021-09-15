package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;

public final class TemplatesImpl implements Templates, Serializable {
   static final long serialVersionUID = 673094361519270707L;
   public static final String DESERIALIZE_TRANSLET = "jdk.xml.enableTemplatesImplDeserialization";
   private static String ABSTRACT_TRANSLET = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
   private String _name = null;
   private byte[][] _bytecodes = (byte[][])null;
   private Class[] _class = null;
   private int _transletIndex = -1;
   private transient Map<String, Class<?>> _auxClasses = null;
   private Properties _outputProperties;
   private int _indentNumber;
   private transient URIResolver _uriResolver = null;
   private transient ThreadLocal _sdom = new ThreadLocal();
   private transient TransformerFactoryImpl _tfactory = null;
   private transient boolean _overrideDefaultParser;
   private transient String _accessExternalStylesheet = "all";
   private static final ObjectStreamField[] serialPersistentFields;

   protected TemplatesImpl(byte[][] bytecodes, String transletName, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory) {
      this._bytecodes = bytecodes;
      this.init(transletName, outputProperties, indentNumber, tfactory);
   }

   protected TemplatesImpl(Class[] transletClasses, String transletName, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory) {
      this._class = transletClasses;
      this._transletIndex = 0;
      this.init(transletName, outputProperties, indentNumber, tfactory);
   }

   private void init(String transletName, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory) {
      this._name = transletName;
      this._outputProperties = outputProperties;
      this._indentNumber = indentNumber;
      this._tfactory = tfactory;
      this._overrideDefaultParser = tfactory.overrideDefaultParser();
      this._accessExternalStylesheet = (String)tfactory.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet");
   }

   public TemplatesImpl() {
   }

   private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
      SecurityManager security = System.getSecurityManager();
      if (security != null) {
         String temp = SecuritySupport.getSystemProperty("jdk.xml.enableTemplatesImplDeserialization");
         if (temp == null || temp.length() != 0 && !temp.equalsIgnoreCase("true")) {
            ErrorMsg err = new ErrorMsg("DESERIALIZE_TEMPLATES_ERR");
            throw new UnsupportedOperationException(err.toString());
         }
      }

      ObjectInputStream.GetField gf = is.readFields();
      this._name = (String)gf.get("_name", (Object)null);
      this._bytecodes = (byte[][])((byte[][])gf.get("_bytecodes", (Object)null));
      this._class = (Class[])((Class[])gf.get("_class", (Object)null));
      this._transletIndex = gf.get("_transletIndex", (int)-1);
      this._outputProperties = (Properties)gf.get("_outputProperties", (Object)null);
      this._indentNumber = gf.get("_indentNumber", (int)0);
      if (is.readBoolean()) {
         this._uriResolver = (URIResolver)is.readObject();
      }

      this._tfactory = new TransformerFactoryImpl();
   }

   private void writeObject(ObjectOutputStream os) throws IOException, ClassNotFoundException {
      if (this._auxClasses != null) {
         throw new NotSerializableException("com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable");
      } else {
         ObjectOutputStream.PutField pf = os.putFields();
         pf.put("_name", this._name);
         pf.put("_bytecodes", this._bytecodes);
         pf.put("_class", this._class);
         pf.put("_transletIndex", this._transletIndex);
         pf.put("_outputProperties", this._outputProperties);
         pf.put("_indentNumber", this._indentNumber);
         os.writeFields();
         if (this._uriResolver instanceof Serializable) {
            os.writeBoolean(true);
            os.writeObject((Serializable)this._uriResolver);
         } else {
            os.writeBoolean(false);
         }

      }
   }

   public boolean overrideDefaultParser() {
      return this._overrideDefaultParser;
   }

   public synchronized void setURIResolver(URIResolver resolver) {
      this._uriResolver = resolver;
   }

   private synchronized void setTransletBytecodes(byte[][] bytecodes) {
      this._bytecodes = bytecodes;
   }

   private synchronized byte[][] getTransletBytecodes() {
      return this._bytecodes;
   }

   private synchronized Class[] getTransletClasses() {
      try {
         if (this._class == null) {
            this.defineTransletClasses();
         }
      } catch (TransformerConfigurationException var2) {
      }

      return this._class;
   }

   public synchronized int getTransletIndex() {
      try {
         if (this._class == null) {
            this.defineTransletClasses();
         }
      } catch (TransformerConfigurationException var2) {
      }

      return this._transletIndex;
   }

   protected synchronized void setTransletName(String name) {
      this._name = name;
   }

   protected synchronized String getTransletName() {
      return this._name;
   }

   private void defineTransletClasses() throws TransformerConfigurationException {
      if (this._bytecodes == null) {
         ErrorMsg err = new ErrorMsg("NO_TRANSLET_CLASS_ERR");
         throw new TransformerConfigurationException(err.toString());
      } else {
         TemplatesImpl.TransletClassLoader loader = (TemplatesImpl.TransletClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return new TemplatesImpl.TransletClassLoader(ObjectFactory.findClassLoader(), TemplatesImpl.this._tfactory.getExternalExtensionsMap());
            }
         });

         ErrorMsg err;
         try {
            int classCount = this._bytecodes.length;
            this._class = new Class[classCount];
            if (classCount > 1) {
               this._auxClasses = new HashMap();
            }

            for(int i = 0; i < classCount; ++i) {
               this._class[i] = loader.defineClass(this._bytecodes[i]);
               Class superClass = this._class[i].getSuperclass();
               if (superClass.getName().equals(ABSTRACT_TRANSLET)) {
                  this._transletIndex = i;
               } else {
                  this._auxClasses.put(this._class[i].getName(), this._class[i]);
               }
            }

            if (this._transletIndex < 0) {
               err = new ErrorMsg("NO_MAIN_TRANSLET_ERR", this._name);
               throw new TransformerConfigurationException(err.toString());
            }
         } catch (ClassFormatError var5) {
            err = new ErrorMsg("TRANSLET_CLASS_ERR", this._name);
            throw new TransformerConfigurationException(err.toString());
         } catch (LinkageError var6) {
            err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
            throw new TransformerConfigurationException(err.toString());
         }
      }
   }

   private Translet getTransletInstance() throws TransformerConfigurationException {
      ErrorMsg err;
      try {
         if (this._name == null) {
            return null;
         } else {
            if (this._class == null) {
               this.defineTransletClasses();
            }

            AbstractTranslet translet = (AbstractTranslet)this._class[this._transletIndex].newInstance();
            translet.postInitialization();
            translet.setTemplates(this);
            translet.setOverrideDefaultParser(this._overrideDefaultParser);
            translet.setAllowedProtocols(this._accessExternalStylesheet);
            if (this._auxClasses != null) {
               translet.setAuxiliaryClasses(this._auxClasses);
            }

            return translet;
         }
      } catch (InstantiationException var3) {
         err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
         throw new TransformerConfigurationException(err.toString());
      } catch (IllegalAccessException var4) {
         err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
         throw new TransformerConfigurationException(err.toString());
      }
   }

   public synchronized Transformer newTransformer() throws TransformerConfigurationException {
      TransformerImpl transformer = new TransformerImpl(this.getTransletInstance(), this._outputProperties, this._indentNumber, this._tfactory);
      if (this._uriResolver != null) {
         transformer.setURIResolver(this._uriResolver);
      }

      if (this._tfactory.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
         transformer.setSecureProcessing(true);
      }

      return transformer;
   }

   public synchronized Properties getOutputProperties() {
      try {
         return this.newTransformer().getOutputProperties();
      } catch (TransformerConfigurationException var2) {
         return null;
      }
   }

   public DOM getStylesheetDOM() {
      return (DOM)this._sdom.get();
   }

   public void setStylesheetDOM(DOM sdom) {
      this._sdom.set(sdom);
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("_name", String.class), new ObjectStreamField("_bytecodes", byte[][].class), new ObjectStreamField("_class", Class[].class), new ObjectStreamField("_transletIndex", Integer.TYPE), new ObjectStreamField("_outputProperties", Properties.class), new ObjectStreamField("_indentNumber", Integer.TYPE)};
   }

   static final class TransletClassLoader extends ClassLoader {
      private final Map<String, Class> _loadedExternalExtensionFunctions;

      TransletClassLoader(ClassLoader parent) {
         super(parent);
         this._loadedExternalExtensionFunctions = null;
      }

      TransletClassLoader(ClassLoader parent, Map<String, Class> mapEF) {
         super(parent);
         this._loadedExternalExtensionFunctions = mapEF;
      }

      public Class<?> loadClass(String name) throws ClassNotFoundException {
         Class<?> ret = null;
         if (this._loadedExternalExtensionFunctions != null) {
            ret = (Class)this._loadedExternalExtensionFunctions.get(name);
         }

         if (ret == null) {
            ret = super.loadClass(name);
         }

         return ret;
      }

      Class defineClass(byte[] b) {
         return this.defineClass((String)null, b, 0, b.length);
      }
   }
}
