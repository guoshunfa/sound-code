package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class XMLEntityStorage {
   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
   protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
   protected boolean fWarnDuplicateEntityDef;
   protected Map<String, Entity> fEntities = new HashMap();
   protected Entity.ScannedEntity fCurrentEntity;
   private XMLEntityManager fEntityManager;
   protected XMLErrorReporter fErrorReporter;
   protected PropertyManager fPropertyManager;
   protected boolean fInExternalSubset = false;
   private static String gUserDir;
   private static String gEscapedUserDir;
   private static boolean[] gNeedEscaping = new boolean[128];
   private static char[] gAfterEscaping1 = new char[128];
   private static char[] gAfterEscaping2 = new char[128];
   private static char[] gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public XMLEntityStorage(PropertyManager propertyManager) {
      this.fPropertyManager = propertyManager;
   }

   public XMLEntityStorage(XMLEntityManager entityManager) {
      this.fEntityManager = entityManager;
   }

   public void reset(PropertyManager propertyManager) {
      this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.fEntities.clear();
      this.fCurrentEntity = null;
   }

   public void reset() {
      this.fEntities.clear();
      this.fCurrentEntity = null;
   }

   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
      this.fWarnDuplicateEntityDef = componentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
      this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      this.fEntities.clear();
      this.fCurrentEntity = null;
   }

   public Entity getEntity(String name) {
      return (Entity)this.fEntities.get(name);
   }

   public boolean hasEntities() {
      return this.fEntities != null;
   }

   public int getEntitySize() {
      return this.fEntities.size();
   }

   public Enumeration getEntityKeys() {
      return Collections.enumeration(this.fEntities.keySet());
   }

   public void addInternalEntity(String name, String text) {
      if (!this.fEntities.containsKey(name)) {
         Entity entity = new Entity.InternalEntity(name, text, this.fInExternalSubset);
         this.fEntities.put(name, entity);
      } else if (this.fWarnDuplicateEntityDef) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[]{name}, (short)0);
      }

   }

   public void addExternalEntity(String name, String publicId, String literalSystemId, String baseSystemId) {
      if (!this.fEntities.containsKey(name)) {
         if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
            baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
         }

         this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
         Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandSystemId(literalSystemId, baseSystemId)), (String)null, this.fInExternalSubset);
         this.fEntities.put(name, entity);
      } else if (this.fWarnDuplicateEntityDef) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[]{name}, (short)0);
      }

   }

   public boolean isExternalEntity(String entityName) {
      Entity entity = (Entity)this.fEntities.get(entityName);
      return entity == null ? false : entity.isExternal();
   }

   public boolean isEntityDeclInExternalSubset(String entityName) {
      Entity entity = (Entity)this.fEntities.get(entityName);
      return entity == null ? false : entity.isEntityDeclInExternalSubset();
   }

   public void addUnparsedEntity(String name, String publicId, String systemId, String baseSystemId, String notation) {
      this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
      if (!this.fEntities.containsKey(name)) {
         Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, systemId, baseSystemId, (String)null), notation, this.fInExternalSubset);
         this.fEntities.put(name, entity);
      } else if (this.fWarnDuplicateEntityDef) {
         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[]{name}, (short)0);
      }

   }

   public boolean isUnparsedEntity(String entityName) {
      Entity entity = (Entity)this.fEntities.get(entityName);
      return entity == null ? false : entity.isUnparsed();
   }

   public boolean isDeclaredEntity(String entityName) {
      Entity entity = (Entity)this.fEntities.get(entityName);
      return entity != null;
   }

   public static String expandSystemId(String systemId) {
      return expandSystemId(systemId, (String)null);
   }

   private static synchronized String getUserDir() {
      String userDir = "";

      try {
         userDir = SecuritySupport.getSystemProperty("user.dir");
      } catch (SecurityException var10) {
      }

      if (userDir.length() == 0) {
         return "";
      } else if (userDir.equals(gUserDir)) {
         return gEscapedUserDir;
      } else {
         gUserDir = userDir;
         char separator = File.separatorChar;
         userDir = userDir.replace(separator, '/');
         int len = userDir.length();
         StringBuffer buffer = new StringBuffer(len * 3);
         char ch;
         if (len >= 2 && userDir.charAt(1) == ':') {
            ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 'A' && ch <= 'Z') {
               buffer.append('/');
            }
         }

         int i;
         for(i = 0; i < len; ++i) {
            ch = userDir.charAt(i);
            if (ch >= 128) {
               break;
            }

            if (gNeedEscaping[ch]) {
               buffer.append('%');
               buffer.append(gAfterEscaping1[ch]);
               buffer.append(gAfterEscaping2[ch]);
            } else {
               buffer.append((char)ch);
            }
         }

         if (i < len) {
            Object var6 = null;

            byte[] bytes;
            try {
               bytes = userDir.substring(i).getBytes("UTF-8");
            } catch (UnsupportedEncodingException var9) {
               return userDir;
            }

            len = bytes.length;

            for(i = 0; i < len; ++i) {
               byte b = bytes[i];
               if (b < 0) {
                  int ch = b + 256;
                  buffer.append('%');
                  buffer.append(gHexChs[ch >> 4]);
                  buffer.append(gHexChs[ch & 15]);
               } else if (gNeedEscaping[b]) {
                  buffer.append('%');
                  buffer.append(gAfterEscaping1[b]);
                  buffer.append(gAfterEscaping2[b]);
               } else {
                  buffer.append((char)b);
               }
            }
         }

         if (!userDir.endsWith("/")) {
            buffer.append('/');
         }

         gEscapedUserDir = buffer.toString();
         return gEscapedUserDir;
      }
   }

   public static String expandSystemId(String systemId, String baseSystemId) {
      if (systemId != null && systemId.length() != 0) {
         try {
            new URI(systemId);
            return systemId;
         } catch (URI.MalformedURIException var9) {
            String id = fixURI(systemId);
            URI base = null;
            URI uri = null;

            try {
               if (baseSystemId != null && baseSystemId.length() != 0 && !baseSystemId.equals(systemId)) {
                  try {
                     base = new URI(fixURI(baseSystemId));
                  } catch (URI.MalformedURIException var7) {
                     if (baseSystemId.indexOf(58) != -1) {
                        base = new URI("file", "", fixURI(baseSystemId), (String)null, (String)null);
                     } else {
                        String dir = getUserDir();
                        dir = dir + fixURI(baseSystemId);
                        base = new URI("file", "", dir, (String)null, (String)null);
                     }
                  }
               } else {
                  String dir = getUserDir();
                  base = new URI("file", "", dir, (String)null, (String)null);
               }

               uri = new URI(base, id);
            } catch (Exception var8) {
            }

            return uri == null ? systemId : uri.toString();
         }
      } else {
         return systemId;
      }
   }

   protected static String fixURI(String str) {
      str = str.replace(File.separatorChar, '/');
      if (str.length() >= 2) {
         char ch1 = str.charAt(1);
         if (ch1 == ':') {
            char ch0 = Character.toUpperCase(str.charAt(0));
            if (ch0 >= 'A' && ch0 <= 'Z') {
               str = "/" + str;
            }
         } else if (ch1 == '/' && str.charAt(0) == '/') {
            str = "file:" + str;
         }
      }

      return str;
   }

   public void startExternalSubset() {
      this.fInExternalSubset = true;
   }

   public void endExternalSubset() {
      this.fInExternalSubset = false;
   }

   static {
      for(int i = 0; i <= 31; ++i) {
         gNeedEscaping[i] = true;
         gAfterEscaping1[i] = gHexChs[i >> 4];
         gAfterEscaping2[i] = gHexChs[i & 15];
      }

      gNeedEscaping[127] = true;
      gAfterEscaping1[127] = '7';
      gAfterEscaping2[127] = 'F';
      char[] escChs = new char[]{' ', '<', '>', '#', '%', '"', '{', '}', '|', '\\', '^', '~', '[', ']', '`'};
      int len = escChs.length;

      for(int i = 0; i < len; ++i) {
         char ch = escChs[i];
         gNeedEscaping[ch] = true;
         gAfterEscaping1[ch] = gHexChs[ch >> 4];
         gAfterEscaping2[ch] = gHexChs[ch & 15];
      }

   }
}
