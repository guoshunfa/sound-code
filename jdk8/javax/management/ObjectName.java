package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class ObjectName implements Comparable<ObjectName>, QueryExp {
   private static final long oldSerialVersionUID = -5467795090068647408L;
   private static final long newSerialVersionUID = 1081892073854801359L;
   private static final ObjectStreamField[] oldSerialPersistentFields;
   private static final ObjectStreamField[] newSerialPersistentFields;
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat;
   private static final ObjectName.Property[] _Empty_property_array;
   private transient String _canonicalName;
   private transient ObjectName.Property[] _kp_array;
   private transient ObjectName.Property[] _ca_array;
   private transient int _domain_length = 0;
   private transient Map<String, String> _propertyList;
   private transient boolean _domain_pattern = false;
   private transient boolean _property_list_pattern = false;
   private transient boolean _property_value_pattern = false;
   public static final ObjectName WILDCARD;

   private void construct(String var1) throws MalformedObjectNameException {
      if (var1 == null) {
         throw new NullPointerException("name cannot be null");
      } else if (var1.length() == 0) {
         this._canonicalName = "*:*";
         this._kp_array = _Empty_property_array;
         this._ca_array = _Empty_property_array;
         this._domain_length = 1;
         this._propertyList = null;
         this._domain_pattern = true;
         this._property_list_pattern = true;
         this._property_value_pattern = false;
      } else {
         char[] var2 = var1.toCharArray();
         int var3 = var2.length;
         char[] var4 = new char[var3];
         boolean var5 = false;
         int var6 = 0;

         label180:
         while(true) {
            if (var6 < var3) {
               switch(var2[var6]) {
               case '\n':
                  throw new MalformedObjectNameException("Invalid character '\\n' in domain name");
               case '*':
               case '?':
                  this._domain_pattern = true;
                  ++var6;
                  continue;
               case ':':
                  this._domain_length = var6++;
                  break;
               case '=':
                  ++var6;
                  int var9 = var6;

                  do {
                     if (var9 >= var3 || var2[var9++] == ':') {
                        continue label180;
                     }
                  } while(var9 != var3);

                  throw new MalformedObjectNameException("Domain part must be specified");
               default:
                  ++var6;
                  continue;
               }
            }

            if (var6 == var3) {
               throw new MalformedObjectNameException("Key properties cannot be empty");
            }

            System.arraycopy(var2, 0, var4, 0, this._domain_length);
            var4[this._domain_length] = ':';
            int var22 = this._domain_length + 1;
            HashMap var10 = new HashMap();
            int var14 = 0;
            String[] var11 = new String[10];
            this._kp_array = new ObjectName.Property[10];
            this._property_list_pattern = false;
            this._property_value_pattern = false;

            while(true) {
               if (var6 < var3) {
                  char var7 = var2[var6];
                  if (var7 != '*') {
                     int var15 = var6;
                     if (var2[var6] == '=') {
                        throw new MalformedObjectNameException("Invalid key (empty)");
                     }

                     char var8;
                     while(var15 < var3 && (var8 = var2[var15++]) != '=') {
                        switch(var8) {
                        case '\n':
                        case '*':
                        case ',':
                        case ':':
                        case '?':
                           String var20 = var8 == '\n' ? "\\n" : "" + var8;
                           throw new MalformedObjectNameException("Invalid character '" + var20 + "' in key part of property");
                        }
                     }

                     if (var2[var15 - 1] != '=') {
                        throw new MalformedObjectNameException("Unterminated key property part");
                     }

                     int var18 = var15;
                     int var17 = var15 - var6 - 1;
                     boolean var24 = false;
                     boolean var13;
                     int var19;
                     if (var15 < var3 && var2[var15] == '"') {
                        var13 = true;

                        while(true) {
                           ++var15;
                           if (var15 >= var3 || (var8 = var2[var15]) == '"') {
                              if (var15 == var3) {
                                 throw new MalformedObjectNameException("Unterminated quoted value");
                              }

                              ++var15;
                              var19 = var15 - var18;
                              break;
                           }

                           if (var8 == '\\') {
                              ++var15;
                              if (var15 == var3) {
                                 throw new MalformedObjectNameException("Unterminated quoted value");
                              }

                              switch(var8 = var2[var15]) {
                              case '"':
                              case '*':
                              case '?':
                              case '\\':
                              case 'n':
                                 break;
                              default:
                                 throw new MalformedObjectNameException("Invalid escape sequence '\\" + var8 + "' in quoted value");
                              }
                           } else {
                              if (var8 == '\n') {
                                 throw new MalformedObjectNameException("Newline in quoted value");
                              }

                              switch(var8) {
                              case '*':
                              case '?':
                                 var24 = true;
                              }
                           }
                        }
                     } else {
                        var13 = false;

                        while(var15 < var3 && (var8 = var2[var15]) != ',') {
                           switch(var8) {
                           case '\n':
                           case '"':
                           case ':':
                           case '=':
                              String var21 = var8 == '\n' ? "\\n" : "" + var8;
                              throw new MalformedObjectNameException("Invalid character '" + var21 + "' in value part of property");
                           case '*':
                           case '?':
                              var24 = true;
                              ++var15;
                              break;
                           default:
                              ++var15;
                           }
                        }

                        var19 = var15 - var18;
                     }

                     if (var15 == var3 - 1) {
                        if (var13) {
                           throw new MalformedObjectNameException("Invalid ending character `" + var2[var15] + "'");
                        }

                        throw new MalformedObjectNameException("Invalid ending comma");
                     }

                     ++var15;
                     Object var23;
                     if (!var24) {
                        var23 = new ObjectName.Property(var6, var17, var19);
                     } else {
                        this._property_value_pattern = true;
                        var23 = new ObjectName.PatternProperty(var6, var17, var19);
                     }

                     String var12 = var1.substring(var6, var6 + var17);
                     if (var14 == var11.length) {
                        String[] var25 = new String[var14 + 10];
                        System.arraycopy(var11, 0, var25, 0, var14);
                        var11 = var25;
                     }

                     var11[var14] = var12;
                     this.addProperty((ObjectName.Property)var23, var14, var10, var12);
                     ++var14;
                     var6 = var15;
                     continue;
                  }

                  if (this._property_list_pattern) {
                     throw new MalformedObjectNameException("Cannot have several '*' characters in pattern property list");
                  }

                  this._property_list_pattern = true;
                  ++var6;
                  if (var6 < var3 && var2[var6] != ',') {
                     throw new MalformedObjectNameException("Invalid character found after '*': end of name or ',' expected");
                  }

                  if (var6 != var3) {
                     ++var6;
                     continue;
                  }

                  if (var14 == 0) {
                     this._kp_array = _Empty_property_array;
                     this._ca_array = _Empty_property_array;
                     this._propertyList = Collections.emptyMap();
                  }
               }

               this.setCanonicalName(var2, var4, var11, var10, var22, var14);
               return;
            }
         }
      }
   }

   private void construct(String var1, Map<String, String> var2) throws MalformedObjectNameException {
      if (var1 == null) {
         throw new NullPointerException("domain cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("key property list cannot be null");
      } else if (var2.isEmpty()) {
         throw new MalformedObjectNameException("key property list cannot be empty");
      } else if (!this.isDomain(var1)) {
         throw new MalformedObjectNameException("Invalid domain: " + var1);
      } else {
         StringBuilder var3 = new StringBuilder();
         var3.append(var1).append(':');
         this._domain_length = var1.length();
         int var4 = var2.size();
         this._kp_array = new ObjectName.Property[var4];
         String[] var5 = new String[var4];
         HashMap var6 = new HashMap();
         int var9 = 0;

         for(Iterator var10 = var2.entrySet().iterator(); var10.hasNext(); ++var9) {
            Map.Entry var11 = (Map.Entry)var10.next();
            if (var3.length() > 0) {
               var3.append(",");
            }

            String var12 = (String)var11.getKey();

            String var13;
            try {
               var13 = (String)var11.getValue();
            } catch (ClassCastException var15) {
               throw new MalformedObjectNameException(var15.getMessage());
            }

            int var8 = var3.length();
            checkKey(var12);
            var3.append(var12);
            var5[var9] = var12;
            var3.append("=");
            boolean var14 = checkValue(var13);
            var3.append(var13);
            Object var7;
            if (!var14) {
               var7 = new ObjectName.Property(var8, var12.length(), var13.length());
            } else {
               this._property_value_pattern = true;
               var7 = new ObjectName.PatternProperty(var8, var12.length(), var13.length());
            }

            this.addProperty((ObjectName.Property)var7, var9, var6, var12);
         }

         int var16 = var3.length();
         char[] var17 = new char[var16];
         var3.getChars(0, var16, var17, 0);
         char[] var18 = new char[var16];
         System.arraycopy(var17, 0, var18, 0, this._domain_length + 1);
         this.setCanonicalName(var17, var18, var5, var6, this._domain_length + 1, this._kp_array.length);
      }
   }

   private void addProperty(ObjectName.Property var1, int var2, Map<String, ObjectName.Property> var3, String var4) throws MalformedObjectNameException {
      if (var3.containsKey(var4)) {
         throw new MalformedObjectNameException("key `" + var4 + "' already defined");
      } else {
         if (var2 == this._kp_array.length) {
            ObjectName.Property[] var5 = new ObjectName.Property[var2 + 10];
            System.arraycopy(this._kp_array, 0, var5, 0, var2);
            this._kp_array = var5;
         }

         this._kp_array[var2] = var1;
         var3.put(var4, var1);
      }
   }

   private void setCanonicalName(char[] var1, char[] var2, String[] var3, Map<String, ObjectName.Property> var4, int var5, int var6) {
      if (this._kp_array != _Empty_property_array) {
         String[] var7 = new String[var6];
         ObjectName.Property[] var8 = new ObjectName.Property[var6];
         System.arraycopy(var3, 0, var7, 0, var6);
         Arrays.sort((Object[])var7);
         var3 = var7;
         System.arraycopy(this._kp_array, 0, var8, 0, var6);
         this._kp_array = var8;
         this._ca_array = new ObjectName.Property[var6];

         int var9;
         for(var9 = 0; var9 < var6; ++var9) {
            this._ca_array[var9] = (ObjectName.Property)var4.get(var3[var9]);
         }

         var9 = var6 - 1;

         for(int var12 = 0; var12 <= var9; ++var12) {
            ObjectName.Property var11 = this._ca_array[var12];
            int var10 = var11._key_length + var11._value_length + 1;
            System.arraycopy(var1, var11._key_index, var2, var5, var10);
            var11.setKeyIndex(var5);
            var5 += var10;
            if (var12 != var9) {
               var2[var5] = ',';
               ++var5;
            }
         }
      }

      if (this._property_list_pattern) {
         if (this._kp_array != _Empty_property_array) {
            var2[var5++] = ',';
         }

         var2[var5++] = '*';
      }

      this._canonicalName = (new String(var2, 0, var5)).intern();
   }

   private static int parseKey(char[] var0, int var1) throws MalformedObjectNameException {
      int var2 = var1;
      int var3 = var1;
      int var4 = var0.length;

      while(true) {
         if (var2 < var4) {
            char var5 = var0[var2++];
            switch(var5) {
            case '\n':
            case '*':
            case ',':
            case ':':
            case '?':
               String var6 = var5 == '\n' ? "\\n" : "" + var5;
               throw new MalformedObjectNameException("Invalid character in key: `" + var6 + "'");
            case '=':
               var3 = var2 - 1;
               break;
            default:
               if (var2 < var4) {
                  continue;
               }

               var3 = var2;
            }
         }

         return var3;
      }
   }

   private static int[] parseValue(char[] var0, int var1) throws MalformedObjectNameException {
      boolean var2 = false;
      int var3 = var1;
      int var4 = var1;
      int var5 = var0.length;
      char var6 = var0[var1];
      char var7;
      if (var6 == '"') {
         var3 = var1 + 1;
         if (var3 == var5) {
            throw new MalformedObjectNameException("Invalid quote");
         }

         while(var3 < var5) {
            var7 = var0[var3];
            if (var7 == '\\') {
               ++var3;
               if (var3 == var5) {
                  throw new MalformedObjectNameException("Invalid unterminated quoted character sequence");
               }

               var7 = var0[var3];
               switch(var7) {
               case '"':
                  if (var3 + 1 == var5) {
                     throw new MalformedObjectNameException("Missing termination quote");
                  }
               case '*':
               case '?':
               case '\\':
               case 'n':
                  break;
               default:
                  throw new MalformedObjectNameException("Invalid quoted character sequence '\\" + var7 + "'");
               }
            } else {
               if (var7 == '\n') {
                  throw new MalformedObjectNameException("Newline in quoted value");
               }

               if (var7 == '"') {
                  ++var3;
                  break;
               }

               switch(var7) {
               case '*':
               case '?':
                  var2 = true;
               }
            }

            ++var3;
            if (var3 >= var5 && var7 != '"') {
               throw new MalformedObjectNameException("Missing termination quote");
            }
         }

         var4 = var3;
         if (var3 < var5 && var0[var3++] != ',') {
            throw new MalformedObjectNameException("Invalid quote");
         }
      } else {
         while(var3 < var5) {
            var7 = var0[var3++];
            switch(var7) {
            case '\n':
            case ':':
            case '=':
               String var8 = var7 == '\n' ? "\\n" : "" + var7;
               throw new MalformedObjectNameException("Invalid character `" + var8 + "' in value");
            case '*':
            case '?':
               var2 = true;
               if (var3 >= var5) {
                  var4 = var3;
                  return new int[]{var4, var2 ? 1 : 0};
               }
               break;
            case ',':
               var4 = var3 - 1;
               return new int[]{var4, var2 ? 1 : 0};
            default:
               if (var3 >= var5) {
                  var4 = var3;
                  return new int[]{var4, var2 ? 1 : 0};
               }
            }
         }
      }

      return new int[]{var4, var2 ? 1 : 0};
   }

   private static boolean checkValue(String var0) throws MalformedObjectNameException {
      if (var0 == null) {
         throw new NullPointerException("Invalid value (null)");
      } else {
         int var1 = var0.length();
         if (var1 == 0) {
            return false;
         } else {
            char[] var2 = var0.toCharArray();
            int[] var3 = parseValue(var2, 0);
            int var4 = var3[0];
            boolean var5 = var3[1] == 1;
            if (var4 < var1) {
               throw new MalformedObjectNameException("Invalid character in value: `" + var2[var4] + "'");
            } else {
               return var5;
            }
         }
      }
   }

   private static void checkKey(String var0) throws MalformedObjectNameException {
      if (var0 == null) {
         throw new NullPointerException("Invalid key (null)");
      } else {
         int var1 = var0.length();
         if (var1 == 0) {
            throw new MalformedObjectNameException("Invalid key (empty)");
         } else {
            char[] var2 = var0.toCharArray();
            int var3 = parseKey(var2, 0);
            if (var3 < var1) {
               throw new MalformedObjectNameException("Invalid character in value: `" + var2[var3] + "'");
            }
         }
      }
   }

   private boolean isDomain(String var1) {
      if (var1 == null) {
         return true;
      } else {
         int var2 = var1.length();
         int var3 = 0;

         while(var3 < var2) {
            char var4 = var1.charAt(var3++);
            switch(var4) {
            case '\n':
            case ':':
               return false;
            case '*':
            case '?':
               this._domain_pattern = true;
            }
         }

         return true;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      String var2;
      if (compat) {
         ObjectInputStream.GetField var3 = var1.readFields();
         String var4 = (String)var3.get("propertyListString", "");
         boolean var5 = var3.get("propertyPattern", false);
         if (var5) {
            var4 = var4.length() == 0 ? "*" : var4 + ",*";
         }

         var2 = (String)var3.get("domain", "default") + ":" + var4;
      } else {
         var1.defaultReadObject();
         var2 = (String)var1.readObject();
      }

      try {
         this.construct(var2);
      } catch (NullPointerException var6) {
         throw new InvalidObjectException(var6.toString());
      } catch (MalformedObjectNameException var7) {
         throw new InvalidObjectException(var7.toString());
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("domain", this._canonicalName.substring(0, this._domain_length));
         var2.put("propertyList", this.getKeyPropertyList());
         var2.put("propertyListString", this.getKeyPropertyListString());
         var2.put("canonicalName", this._canonicalName);
         var2.put("pattern", this._domain_pattern || this._property_list_pattern);
         var2.put("propertyPattern", this._property_list_pattern);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
         var1.writeObject(this.getSerializedNameString());
      }

   }

   public static ObjectName getInstance(String var0) throws MalformedObjectNameException, NullPointerException {
      return new ObjectName(var0);
   }

   public static ObjectName getInstance(String var0, String var1, String var2) throws MalformedObjectNameException {
      return new ObjectName(var0, var1, var2);
   }

   public static ObjectName getInstance(String var0, Hashtable<String, String> var1) throws MalformedObjectNameException {
      return new ObjectName(var0, var1);
   }

   public static ObjectName getInstance(ObjectName var0) {
      return var0.getClass().equals(ObjectName.class) ? var0 : Util.newObjectName(var0.getSerializedNameString());
   }

   public ObjectName(String var1) throws MalformedObjectNameException {
      this.construct(var1);
   }

   public ObjectName(String var1, String var2, String var3) throws MalformedObjectNameException {
      Map var4 = Collections.singletonMap(var2, var3);
      this.construct(var1, var4);
   }

   public ObjectName(String var1, Hashtable<String, String> var2) throws MalformedObjectNameException {
      this.construct(var1, var2);
   }

   public boolean isPattern() {
      return this._domain_pattern || this._property_list_pattern || this._property_value_pattern;
   }

   public boolean isDomainPattern() {
      return this._domain_pattern;
   }

   public boolean isPropertyPattern() {
      return this._property_list_pattern || this._property_value_pattern;
   }

   public boolean isPropertyListPattern() {
      return this._property_list_pattern;
   }

   public boolean isPropertyValuePattern() {
      return this._property_value_pattern;
   }

   public boolean isPropertyValuePattern(String var1) {
      if (var1 == null) {
         throw new NullPointerException("key property can't be null");
      } else {
         for(int var2 = 0; var2 < this._ca_array.length; ++var2) {
            ObjectName.Property var3 = this._ca_array[var2];
            String var4 = var3.getKeyString(this._canonicalName);
            if (var4.equals(var1)) {
               return var3 instanceof ObjectName.PatternProperty;
            }
         }

         throw new IllegalArgumentException("key property not found");
      }
   }

   public String getCanonicalName() {
      return this._canonicalName;
   }

   public String getDomain() {
      return this._canonicalName.substring(0, this._domain_length);
   }

   public String getKeyProperty(String var1) {
      return (String)this._getKeyPropertyList().get(var1);
   }

   private Map<String, String> _getKeyPropertyList() {
      synchronized(this) {
         if (this._propertyList == null) {
            this._propertyList = new HashMap();
            int var2 = this._ca_array.length;

            for(int var4 = var2 - 1; var4 >= 0; --var4) {
               ObjectName.Property var3 = this._ca_array[var4];
               this._propertyList.put(var3.getKeyString(this._canonicalName), var3.getValueString(this._canonicalName));
            }
         }
      }

      return this._propertyList;
   }

   public Hashtable<String, String> getKeyPropertyList() {
      return new Hashtable(this._getKeyPropertyList());
   }

   public String getKeyPropertyListString() {
      if (this._kp_array.length == 0) {
         return "";
      } else {
         int var1 = this._canonicalName.length() - this._domain_length - 1 - (this._property_list_pattern ? 2 : 0);
         char[] var2 = new char[var1];
         char[] var3 = this._canonicalName.toCharArray();
         this.writeKeyPropertyListString(var3, var2, 0);
         return new String(var2);
      }
   }

   private String getSerializedNameString() {
      int var1 = this._canonicalName.length();
      char[] var2 = new char[var1];
      char[] var3 = this._canonicalName.toCharArray();
      int var4 = this._domain_length + 1;
      System.arraycopy(var3, 0, var2, 0, var4);
      int var5 = this.writeKeyPropertyListString(var3, var2, var4);
      if (this._property_list_pattern) {
         if (var5 == var4) {
            var2[var5] = '*';
         } else {
            var2[var5] = ',';
            var2[var5 + 1] = '*';
         }
      }

      return new String(var2);
   }

   private int writeKeyPropertyListString(char[] var1, char[] var2, int var3) {
      if (this._kp_array.length == 0) {
         return var3;
      } else {
         char[] var4 = var2;
         char[] var5 = var1;
         int var6 = var3;
         int var7 = this._kp_array.length;
         int var8 = var7 - 1;

         for(int var9 = 0; var9 < var7; ++var9) {
            ObjectName.Property var10 = this._kp_array[var9];
            int var11 = var10._key_length + var10._value_length + 1;
            System.arraycopy(var5, var10._key_index, var4, var6, var11);
            var6 += var11;
            if (var9 < var8) {
               var4[var6++] = ',';
            }
         }

         return var6;
      }
   }

   public String getCanonicalKeyPropertyListString() {
      if (this._ca_array.length == 0) {
         return "";
      } else {
         int var1 = this._canonicalName.length();
         if (this._property_list_pattern) {
            var1 -= 2;
         }

         return this._canonicalName.substring(this._domain_length + 1, var1);
      }
   }

   public String toString() {
      return this.getSerializedNameString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ObjectName)) {
         return false;
      } else {
         ObjectName var2 = (ObjectName)var1;
         String var3 = var2._canonicalName;
         return this._canonicalName == var3;
      }
   }

   public int hashCode() {
      return this._canonicalName.hashCode();
   }

   public static String quote(String var0) {
      StringBuilder var1 = new StringBuilder("\"");
      int var2 = var0.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var0.charAt(var3);
         switch(var4) {
         case '\n':
            var4 = 'n';
            var1.append('\\');
            break;
         case '"':
         case '*':
         case '?':
         case '\\':
            var1.append('\\');
         }

         var1.append(var4);
      }

      var1.append('"');
      return var1.toString();
   }

   public static String unquote(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = var0.length();
      if (var2 >= 2 && var0.charAt(0) == '"' && var0.charAt(var2 - 1) == '"') {
         for(int var3 = 1; var3 < var2 - 1; ++var3) {
            char var4 = var0.charAt(var3);
            if (var4 == '\\') {
               if (var3 == var2 - 2) {
                  throw new IllegalArgumentException("Trailing backslash");
               }

               ++var3;
               var4 = var0.charAt(var3);
               switch(var4) {
               case '"':
               case '*':
               case '?':
               case '\\':
                  break;
               case 'n':
                  var4 = '\n';
                  break;
               default:
                  throw new IllegalArgumentException("Bad character '" + var4 + "' after backslash");
               }
            } else {
               switch(var4) {
               case '\n':
               case '"':
               case '*':
               case '?':
                  throw new IllegalArgumentException("Invalid unescaped character '" + var4 + "' in the string to unquote");
               }
            }

            var1.append(var4);
         }

         return var1.toString();
      } else {
         throw new IllegalArgumentException("Argument not quoted");
      }
   }

   public boolean apply(ObjectName var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!var1._domain_pattern && !var1._property_list_pattern && !var1._property_value_pattern) {
         if (!this._domain_pattern && !this._property_list_pattern && !this._property_value_pattern) {
            return this._canonicalName.equals(var1._canonicalName);
         } else {
            return this.matchDomains(var1) && this.matchKeys(var1);
         }
      } else {
         return false;
      }
   }

   private final boolean matchDomains(ObjectName var1) {
      return this._domain_pattern ? Util.wildmatch(var1.getDomain(), this.getDomain()) : this.getDomain().equals(var1.getDomain());
   }

   private final boolean matchKeys(ObjectName var1) {
      if (this._property_value_pattern && !this._property_list_pattern && var1._ca_array.length != this._ca_array.length) {
         return false;
      } else if (!this._property_value_pattern && !this._property_list_pattern) {
         String var9 = var1.getCanonicalKeyPropertyListString();
         String var10 = this.getCanonicalKeyPropertyListString();
         return var9.equals(var10);
      } else {
         Map var2 = var1._getKeyPropertyList();
         ObjectName.Property[] var3 = this._ca_array;
         String var4 = this._canonicalName;

         for(int var5 = var3.length - 1; var5 >= 0; --var5) {
            ObjectName.Property var6 = var3[var5];
            String var7 = var6.getKeyString(var4);
            String var8 = (String)var2.get(var7);
            if (var8 == null) {
               return false;
            }

            if (this._property_value_pattern && var6 instanceof ObjectName.PatternProperty) {
               if (!Util.wildmatch(var8, var6.getValueString(var4))) {
                  return false;
               }
            } else if (!var8.equals(var6.getValueString(var4))) {
               return false;
            }
         }

         return true;
      }
   }

   public void setMBeanServer(MBeanServer var1) {
   }

   public int compareTo(ObjectName var1) {
      if (var1 == this) {
         return 0;
      } else {
         int var2 = this.getDomain().compareTo(var1.getDomain());
         if (var2 != 0) {
            return var2;
         } else {
            String var3 = this.getKeyProperty("type");
            String var4 = var1.getKeyProperty("type");
            if (var3 == null) {
               var3 = "";
            }

            if (var4 == null) {
               var4 = "";
            }

            int var5 = var3.compareTo(var4);
            return var5 != 0 ? var5 : this.getCanonicalName().compareTo(var1.getCanonicalName());
         }
      }
   }

   static {
      oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("domain", String.class), new ObjectStreamField("propertyList", Hashtable.class), new ObjectStreamField("propertyListString", String.class), new ObjectStreamField("canonicalName", String.class), new ObjectStreamField("pattern", Boolean.TYPE), new ObjectStreamField("propertyPattern", Boolean.TYPE)};
      newSerialPersistentFields = new ObjectStreamField[0];
      compat = false;

      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = -5467795090068647408L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 1081892073854801359L;
      }

      _Empty_property_array = new ObjectName.Property[0];
      WILDCARD = Util.newObjectName("*:*");
   }

   private static class PatternProperty extends ObjectName.Property {
      PatternProperty(int var1, int var2, int var3) {
         super(var1, var2, var3);
      }
   }

   private static class Property {
      int _key_index;
      int _key_length;
      int _value_length;

      Property(int var1, int var2, int var3) {
         this._key_index = var1;
         this._key_length = var2;
         this._value_length = var3;
      }

      void setKeyIndex(int var1) {
         this._key_index = var1;
      }

      String getKeyString(String var1) {
         return var1.substring(this._key_index, this._key_index + this._key_length);
      }

      String getValueString(String var1) {
         int var2 = this._key_index + this._key_length + 1;
         int var3 = var2 + this._value_length;
         return var1.substring(var2, var3);
      }
   }
}
