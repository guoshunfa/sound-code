package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ReflectUtil;

public class DescriptorSupport implements Descriptor {
   private static final long oldSerialVersionUID = 8071560848919417985L;
   private static final long newSerialVersionUID = -6292969195866300415L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("descriptor", HashMap.class), new ObjectStreamField("currClass", String.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("descriptor", HashMap.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static final String serialForm;
   private transient SortedMap<String, Object> descriptorMap;
   private static final String currClass = "DescriptorSupport";
   private static final String[] entities;
   private static final Map<String, Character> entityToCharMap;
   private static final String[] charToEntityMap;

   public DescriptorSupport() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "DescriptorSupport()", "Constructor");
      }

      this.init((Map)null);
   }

   public DescriptorSupport(int var1) throws MBeanException, RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields = " + var1 + ")", "Constructor");
      }

      if (var1 <= 0) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields)", "Illegal arguments: initNumFields <= 0");
         }

         String var2 = "Descriptor field limit invalid: " + var1;
         IllegalArgumentException var3 = new IllegalArgumentException(var2);
         throw new RuntimeOperationsException(var3, var2);
      } else {
         this.init((Map)null);
      }
   }

   public DescriptorSupport(DescriptorSupport var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(Descriptor)", "Constructor");
      }

      if (var1 == null) {
         this.init((Map)null);
      } else {
         this.init(var1.descriptorMap);
      }

   }

   public DescriptorSupport(String var1) throws MBeanException, RuntimeOperationsException, XMLParseException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = '" + var1 + "')", "Constructor");
      }

      if (var1 == null) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = null)", "Illegal arguments");
         }

         IllegalArgumentException var13 = new IllegalArgumentException("String in parameter is null");
         throw new RuntimeOperationsException(var13, "String in parameter is null");
      } else {
         String var2 = var1.toLowerCase();
         if (var2.startsWith("<descriptor>") && var2.endsWith("</descriptor>")) {
            this.init((Map)null);
            StringTokenizer var3 = new StringTokenizer(var1, "<> \t\n\r\f");
            boolean var4 = false;
            boolean var5 = false;
            String var6 = null;
            String var7 = null;

            while(var3.hasMoreTokens()) {
               String var8 = var3.nextToken();
               if (var8.equalsIgnoreCase("FIELD")) {
                  var4 = true;
               } else if (var8.equalsIgnoreCase("/FIELD")) {
                  if (var6 != null && var7 != null) {
                     var6 = var6.substring(var6.indexOf(34) + 1, var6.lastIndexOf(34));
                     Object var9 = parseQuotedFieldValue(var7);
                     this.setField(var6, var9);
                  }

                  var6 = null;
                  var7 = null;
                  var4 = false;
               } else if (var8.equalsIgnoreCase("DESCRIPTOR")) {
                  var5 = true;
               } else if (var8.equalsIgnoreCase("/DESCRIPTOR")) {
                  var5 = false;
                  var6 = null;
                  var7 = null;
                  var4 = false;
               } else if (var4 && var5) {
                  int var14 = var8.indexOf("=");
                  String var10;
                  if (var14 <= 0) {
                     var10 = "Expected `keyword=value', got `" + var8 + "'";
                     throw new XMLParseException(var10);
                  }

                  var10 = var8.substring(0, var14);
                  String var11 = var8.substring(var14 + 1);
                  if (var10.equalsIgnoreCase("NAME")) {
                     var6 = var11;
                  } else {
                     if (!var10.equalsIgnoreCase("VALUE")) {
                        String var12 = "Expected `name' or `value', got `" + var8 + "'";
                        throw new XMLParseException(var12);
                     }

                     var7 = var11;
                  }
               }
            }

            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(XMLString)", "Exit");
            }

         } else {
            throw new XMLParseException("No <descriptor>, </descriptor> pair");
         }
      }
   }

   public DescriptorSupport(String[] var1, Object[] var2) throws RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Constructor");
      }

      if (var1 != null && var2 != null && var1.length == var2.length) {
         this.init((Map)null);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            this.setField(var1[var3], var2[var3]);
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Exit");
         }

      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Illegal arguments");
         }

         IllegalArgumentException var4 = new IllegalArgumentException("Null or invalid fieldNames or fieldValues");
         throw new RuntimeOperationsException(var4, "Null or invalid fieldNames or fieldValues");
      }
   }

   public DescriptorSupport(String... var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Constructor");
      }

      this.init((Map)null);
      if (var1 != null && var1.length != 0) {
         this.init((Map)null);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] != null && !var1[var2].equals("")) {
               int var3 = var1[var2].indexOf("=");
               if (var3 < 0) {
                  if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: field does not have '=' as a name and value separator");
                  }

                  IllegalArgumentException var8 = new IllegalArgumentException("Field in invalid format: no equals sign");
                  throw new RuntimeOperationsException(var8, "Field in invalid format: no equals sign");
               }

               String var4 = var1[var2].substring(0, var3);
               String var5 = null;
               if (var3 < var1[var2].length()) {
                  var5 = var1[var2].substring(var3 + 1);
               }

               if (var4.equals("")) {
                  if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: fieldName is empty");
                  }

                  IllegalArgumentException var7 = new IllegalArgumentException("Field in invalid format: no fieldName");
                  throw new RuntimeOperationsException(var7, "Field in invalid format: no fieldName");
               }

               this.setField(var4, var5);
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Exit");
         }

      }
   }

   private void init(Map<String, ?> var1) {
      this.descriptorMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      if (var1 != null) {
         this.descriptorMap.putAll(var1);
      }

   }

   public synchronized Object getFieldValue(String var1) throws RuntimeOperationsException {
      if (var1 != null && !var1.equals("")) {
         Object var2 = this.descriptorMap.get(var1);
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName = " + var1 + ")", "Returns '" + var2 + "'");
         }

         return var2;
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName)", "Illegal arguments: null field name");
         }

         IllegalArgumentException var3 = new IllegalArgumentException("Fieldname requested is null");
         throw new RuntimeOperationsException(var3, "Fieldname requested is null");
      }
   }

   public synchronized void setField(String var1, Object var2) throws RuntimeOperationsException {
      IllegalArgumentException var4;
      if (var1 != null && !var1.equals("")) {
         if (!this.validateField(var1, var2)) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments");
            }

            String var3 = "Field value invalid: " + var1 + "=" + var2;
            var4 = new IllegalArgumentException(var3);
            throw new RuntimeOperationsException(var4, var3);
         } else {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Entry: setting '" + var1 + "' to '" + var2 + "'");
            }

            this.descriptorMap.put(var1, var2);
         }
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments: null or empty field name");
         }

         var4 = new IllegalArgumentException("Field name to be set is null or empty");
         throw new RuntimeOperationsException(var4, "Field name to be set is null or empty");
      }
   }

   public synchronized String[] getFields() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Entry");
      }

      int var1 = this.descriptorMap.size();
      String[] var2 = new String[var1];
      Set var3 = this.descriptorMap.entrySet();
      int var4 = 0;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Returning " + var1 + " fields");
      }

      for(Iterator var5 = var3.iterator(); var5.hasNext(); ++var4) {
         Map.Entry var6 = (Map.Entry)var5.next();
         if (var6 == null) {
            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Element is null");
            }
         } else {
            Object var7 = var6.getValue();
            if (var7 == null) {
               var2[var4] = (String)var6.getKey() + "=";
            } else if (var7 instanceof String) {
               var2[var4] = (String)var6.getKey() + "=" + var7.toString();
            } else {
               var2[var4] = (String)var6.getKey() + "=(" + var7.toString() + ")";
            }
         }
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Exit");
      }

      return var2;
   }

   public synchronized String[] getFieldNames() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Entry");
      }

      int var1 = this.descriptorMap.size();
      String[] var2 = new String[var1];
      Set var3 = this.descriptorMap.entrySet();
      int var4 = 0;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Returning " + var1 + " fields");
      }

      for(Iterator var5 = var3.iterator(); var5.hasNext(); ++var4) {
         Map.Entry var6 = (Map.Entry)var5.next();
         if (var6 != null && var6.getKey() != null) {
            var2[var4] = ((String)var6.getKey()).toString();
         } else if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Field is null");
         }
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Exit");
      }

      return var2;
   }

   public synchronized Object[] getFieldValues(String... var1) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Entry");
      }

      int var2 = var1 == null ? this.descriptorMap.size() : var1.length;
      Object[] var3 = new Object[var2];
      int var4 = 0;
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Returning " + var2 + " fields");
      }

      Object var6;
      if (var1 == null) {
         for(Iterator var5 = this.descriptorMap.values().iterator(); var5.hasNext(); var3[var4++] = var6) {
            var6 = var5.next();
         }
      } else {
         for(var4 = 0; var4 < var1.length; ++var4) {
            if (var1[var4] != null && !var1[var4].equals("")) {
               var3[var4] = this.getFieldValue(var1[var4]);
            } else {
               var3[var4] = null;
            }
         }
      }

      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Exit");
      }

      return var3;
   }

   public synchronized void setFields(String[] var1, Object[] var2) throws RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Entry");
      }

      if (var1 != null && var2 != null && var1.length == var2.length) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] == null || var1[var3].equals("")) {
               if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Null field name encountered at element " + var3);
               }

               IllegalArgumentException var5 = new IllegalArgumentException("fieldNames is null or invalid");
               throw new RuntimeOperationsException(var5, "fieldNames is null or invalid");
            }

            this.setField(var1[var3], var2[var3]);
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Exit");
         }

      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Illegal arguments");
         }

         IllegalArgumentException var4 = new IllegalArgumentException("fieldNames and fieldValues are null or invalid");
         throw new RuntimeOperationsException(var4, "fieldNames and fieldValues are null or invalid");
      }
   }

   public synchronized Object clone() throws RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "clone()", "Entry");
      }

      return new DescriptorSupport(this);
   }

   public synchronized void removeField(String var1) {
      if (var1 != null && !var1.equals("")) {
         this.descriptorMap.remove(var1);
      }
   }

   public synchronized boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Descriptor)) {
         return false;
      } else {
         return var1 instanceof ImmutableDescriptor ? var1.equals(this) : (new ImmutableDescriptor(this.descriptorMap)).equals(var1);
      }
   }

   public synchronized int hashCode() {
      int var1 = this.descriptorMap.size();
      return Util.hashCode((String[])this.descriptorMap.keySet().toArray(new String[var1]), this.descriptorMap.values().toArray(new Object[var1]));
   }

   public synchronized boolean isValid() throws RuntimeOperationsException {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Entry");
      }

      Set var1 = this.descriptorMap.entrySet();
      if (var1 == null) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns false (null set)");
         }

         return false;
      } else {
         String var2 = (String)((String)this.getFieldValue("name"));
         String var3 = (String)((String)this.getFieldValue("descriptorType"));
         if (var2 != null && var3 != null && !var2.equals("") && !var3.equals("")) {
            Iterator var4 = var1.iterator();

            Map.Entry var5;
            do {
               if (!var4.hasNext()) {
                  if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
                     JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns true");
                  }

                  return true;
               }

               var5 = (Map.Entry)var4.next();
            } while(var5 == null || var5.getValue() == null || this.validateField(((String)var5.getKey()).toString(), var5.getValue().toString()));

            if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Field " + (String)var5.getKey() + "=" + var5.getValue() + " is not valid");
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private boolean validateField(String var1, Object var2) {
      if (var1 != null && !var1.equals("")) {
         String var3 = "";
         boolean var4 = false;
         if (var2 != null && var2 instanceof String) {
            var3 = (String)var2;
            var4 = true;
         }

         boolean var5 = var1.equalsIgnoreCase("Name") || var1.equalsIgnoreCase("DescriptorType");
         if (!var5 && !var1.equalsIgnoreCase("SetMethod") && !var1.equalsIgnoreCase("GetMethod") && !var1.equalsIgnoreCase("Role") && !var1.equalsIgnoreCase("Class")) {
            long var6;
            if (var1.equalsIgnoreCase("visibility")) {
               if (var2 != null && var4) {
                  var6 = this.toNumeric(var3);
               } else {
                  if (!(var2 instanceof Integer)) {
                     return false;
                  }

                  var6 = (long)(Integer)var2;
               }

               return var6 >= 1L && var6 <= 4L;
            } else if (var1.equalsIgnoreCase("severity")) {
               if (var2 != null && var4) {
                  var6 = this.toNumeric(var3);
               } else {
                  if (!(var2 instanceof Integer)) {
                     return false;
                  }

                  var6 = (long)(Integer)var2;
               }

               return var6 >= 0L && var6 <= 6L;
            } else if (var1.equalsIgnoreCase("PersistPolicy")) {
               return var2 != null && var4 && (var3.equalsIgnoreCase("OnUpdate") || var3.equalsIgnoreCase("OnTimer") || var3.equalsIgnoreCase("NoMoreOftenThan") || var3.equalsIgnoreCase("Always") || var3.equalsIgnoreCase("Never") || var3.equalsIgnoreCase("OnUnregister"));
            } else if (!var1.equalsIgnoreCase("PersistPeriod") && !var1.equalsIgnoreCase("CurrencyTimeLimit") && !var1.equalsIgnoreCase("LastUpdatedTimeStamp") && !var1.equalsIgnoreCase("LastReturnedTimeStamp")) {
               if (!var1.equalsIgnoreCase("log")) {
                  return true;
               } else {
                  return var2 instanceof Boolean || var4 && (var3.equalsIgnoreCase("T") || var3.equalsIgnoreCase("true") || var3.equalsIgnoreCase("F") || var3.equalsIgnoreCase("false"));
               }
            } else {
               if (var2 != null && var4) {
                  var6 = this.toNumeric(var3);
               } else {
                  if (!(var2 instanceof Number)) {
                     return false;
                  }

                  var6 = ((Number)var2).longValue();
               }

               return var6 >= -1L;
            }
         } else if (var2 != null && var4) {
            return !var5 || !var3.equals("");
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public synchronized String toXMLString() {
      StringBuilder var1 = new StringBuilder("<Descriptor>");
      Set var2 = this.descriptorMap.entrySet();

      String var5;
      String var7;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); var1.append("<field name=\"").append(var5).append("\" value=\"").append(var7).append("\"></field>")) {
         Map.Entry var4 = (Map.Entry)var3.next();
         var5 = (String)var4.getKey();
         Object var6 = var4.getValue();
         var7 = null;
         if (var6 instanceof String) {
            String var8 = (String)var6;
            if (!var8.startsWith("(") || !var8.endsWith(")")) {
               var7 = quote(var8);
            }
         }

         if (var7 == null) {
            var7 = makeFieldValue(var6);
         }
      }

      var1.append("</Descriptor>");
      return var1.toString();
   }

   private static boolean isMagic(char var0) {
      return var0 < charToEntityMap.length && charToEntityMap[var0] != null;
   }

   private static String quote(String var0) {
      boolean var1 = false;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         if (isMagic(var0.charAt(var2))) {
            var1 = true;
            break;
         }
      }

      if (!var1) {
         return var0;
      } else {
         StringBuilder var5 = new StringBuilder();

         for(int var3 = 0; var3 < var0.length(); ++var3) {
            char var4 = var0.charAt(var3);
            if (isMagic(var4)) {
               var5.append(charToEntityMap[var4]);
            } else {
               var5.append(var4);
            }
         }

         return var5.toString();
      }
   }

   private static String unquote(String var0) throws XMLParseException {
      if (var0.startsWith("\"") && var0.endsWith("\"")) {
         StringBuilder var1 = new StringBuilder();
         int var2 = var0.length() - 1;

         for(int var3 = 1; var3 < var2; ++var3) {
            char var4 = var0.charAt(var3);
            int var5;
            Character var6;
            if (var4 == '&' && (var5 = var0.indexOf(59, var3 + 1)) >= 0 && (var6 = (Character)entityToCharMap.get(var0.substring(var3, var5 + 1))) != null) {
               var1.append((Object)var6);
               var3 = var5;
            } else {
               var1.append(var4);
            }
         }

         return var1.toString();
      } else {
         throw new XMLParseException("Value must be quoted: <" + var0 + ">");
      }
   }

   private static String makeFieldValue(Object var0) {
      if (var0 == null) {
         return "(null)";
      } else {
         Class var1 = var0.getClass();

         try {
            var1.getConstructor(String.class);
         } catch (NoSuchMethodException var5) {
            String var3 = "Class " + var1 + " does not have a public constructor with a single string arg";
            IllegalArgumentException var4 = new IllegalArgumentException(var3);
            throw new RuntimeOperationsException(var4, "Cannot make XML descriptor");
         } catch (SecurityException var6) {
         }

         String var2 = quote(var0.toString());
         return "(" + var1.getName() + "/" + var2 + ")";
      }
   }

   private static Object parseQuotedFieldValue(String var0) throws XMLParseException {
      var0 = unquote(var0);
      if (var0.equalsIgnoreCase("(null)")) {
         return null;
      } else if (var0.startsWith("(") && var0.endsWith(")")) {
         int var1 = var0.indexOf(47);
         if (var1 < 0) {
            return var0.substring(1, var0.length() - 1);
         } else {
            String var2 = var0.substring(1, var1);

            Constructor var3;
            try {
               ReflectUtil.checkPackageAccess(var2);
               ClassLoader var4 = Thread.currentThread().getContextClassLoader();
               Class var5 = Class.forName(var2, false, var4);
               var3 = var5.getConstructor(String.class);
            } catch (Exception var8) {
               throw new XMLParseException(var8, "Cannot parse value: <" + var0 + ">");
            }

            String var9 = var0.substring(var1 + 1, var0.length() - 1);

            try {
               return var3.newInstance(var9);
            } catch (Exception var7) {
               String var6 = "Cannot construct instance of " + var2 + " with arg: <" + var0 + ">";
               throw new XMLParseException(var7, var6);
            }
         }
      } else {
         return var0;
      }
   }

   public synchronized String toString() {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Entry");
      }

      String var1 = "";
      String[] var2 = this.getFields();
      if (var2 != null && var2.length != 0) {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Printing " + var2.length + " fields");
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var3 == var2.length - 1) {
               var1 = var1.concat(var2[var3]);
            } else {
               var1 = var1.concat(var2[var3] + ", ");
            }
         }

         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Exit returning " + var1);
         }

         return var1;
      } else {
         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Empty Descriptor");
         }

         return var1;
      }
   }

   private long toNumeric(String var1) {
      try {
         return Long.parseLong(var1);
      } catch (Exception var3) {
         return -2L;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Map var3 = (Map)Util.cast(var2.get("descriptor", (Object)null));
      this.init((Map)null);
      if (var3 != null) {
         this.descriptorMap.putAll(var3);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      boolean var3 = "1.0".equals(serialForm);
      if (var3) {
         var2.put("currClass", "DescriptorSupport");
      }

      Object var4 = this.descriptorMap;
      if (((SortedMap)var4).containsKey("targetObject")) {
         var4 = new TreeMap(this.descriptorMap);
         ((SortedMap)var4).remove("targetObject");
      }

      HashMap var5;
      if (!var3 && !"1.2.0".equals(serialForm) && !"1.2.1".equals(serialForm)) {
         var5 = new HashMap((Map)var4);
      } else {
         var5 = new HashMap();
         Iterator var6 = ((SortedMap)var4).entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            var5.put(((String)var7.getKey()).toLowerCase(), var7.getValue());
         }
      }

      var2.put("descriptor", var5);
      var1.writeFields();
   }

   static {
      String var0 = null;
      boolean var1 = false;

      try {
         GetPropertyAction var2 = new GetPropertyAction("jmx.serial.form");
         var0 = (String)AccessController.doPrivileged((PrivilegedAction)var2);
         var1 = "1.0".equals(var0);
      } catch (Exception var4) {
      }

      serialForm = var0;
      if (var1) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = 8071560848919417985L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -6292969195866300415L;
      }

      entities = new String[]{" &#32;", "\"&quot;", "<&lt;", ">&gt;", "&&amp;", "\r&#13;", "\t&#9;", "\n&#10;", "\f&#12;"};
      entityToCharMap = new HashMap();
      char var5 = 0;

      int var6;
      char var7;
      for(var6 = 0; var6 < entities.length; ++var6) {
         var7 = entities[var6].charAt(0);
         if (var7 > var5) {
            var5 = var7;
         }
      }

      charToEntityMap = new String[var5 + 1];

      for(var6 = 0; var6 < entities.length; ++var6) {
         var7 = entities[var6].charAt(0);
         String var3 = entities[var6].substring(1);
         charToEntityMap[var7] = var3;
         entityToCharMap.put(var3, var7);
      }

   }
}
