package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.HashMap;

public class StAXManager {
   protected static final String STAX_NOTATIONS = "javax.xml.stream.notations";
   protected static final String STAX_ENTITIES = "javax.xml.stream.entities";
   HashMap features = new HashMap();
   public static final int CONTEXT_READER = 1;
   public static final int CONTEXT_WRITER = 2;

   public StAXManager() {
   }

   public StAXManager(int context) {
      switch(context) {
      case 1:
         this.initConfigurableReaderProperties();
         break;
      case 2:
         this.initWriterProps();
      }

   }

   public StAXManager(StAXManager manager) {
      HashMap properties = manager.getProperties();
      this.features.putAll(properties);
   }

   private HashMap getProperties() {
      return this.features;
   }

   private void initConfigurableReaderProperties() {
      this.features.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
      this.features.put("javax.xml.stream.isValidating", Boolean.FALSE);
      this.features.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
      this.features.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
      this.features.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
      this.features.put("javax.xml.stream.supportDTD", Boolean.FALSE);
      this.features.put("javax.xml.stream.reporter", (Object)null);
      this.features.put("javax.xml.stream.resolver", (Object)null);
      this.features.put("javax.xml.stream.allocator", (Object)null);
      this.features.put("javax.xml.stream.notations", (Object)null);
   }

   private void initWriterProps() {
      this.features.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
   }

   public boolean containsProperty(String property) {
      return this.features.containsKey(property);
   }

   public Object getProperty(String name) {
      this.checkProperty(name);
      return this.features.get(name);
   }

   public void setProperty(String name, Object value) {
      this.checkProperty(name);
      if (name.equals("javax.xml.stream.isValidating") && Boolean.TRUE.equals(value)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.validationNotSupported") + CommonResourceBundle.getInstance().getString("support_validation"));
      } else if (name.equals("javax.xml.stream.isSupportingExternalEntities") && Boolean.TRUE.equals(value)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.externalEntities") + CommonResourceBundle.getInstance().getString("resolve_external_entities_"));
      } else {
         this.features.put(name, value);
      }
   }

   public void checkProperty(String name) {
      if (!this.features.containsKey(name)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{name}));
      }
   }

   public String toString() {
      return this.features.toString();
   }
}
