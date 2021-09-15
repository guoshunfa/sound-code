package com.sun.jndi.ldap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.spi.DirStateFactory;
import javax.naming.spi.DirectoryManager;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

final class Obj {
   static VersionHelper helper = VersionHelper.getVersionHelper();
   static final String[] JAVA_ATTRIBUTES = new String[]{"objectClass", "javaSerializedData", "javaClassName", "javaFactory", "javaCodeBase", "javaReferenceAddress", "javaClassNames", "javaRemoteLocation"};
   static final int OBJECT_CLASS = 0;
   static final int SERIALIZED_DATA = 1;
   static final int CLASSNAME = 2;
   static final int FACTORY = 3;
   static final int CODEBASE = 4;
   static final int REF_ADDR = 5;
   static final int TYPENAME = 6;
   /** @deprecated */
   @Deprecated
   private static final int REMOTE_LOC = 7;
   static final String[] JAVA_OBJECT_CLASSES = new String[]{"javaContainer", "javaObject", "javaNamingReference", "javaSerializedObject", "javaMarshalledObject"};
   static final String[] JAVA_OBJECT_CLASSES_LOWER = new String[]{"javacontainer", "javaobject", "javanamingreference", "javaserializedobject", "javamarshalledobject"};
   static final int STRUCTURAL = 0;
   static final int BASE_OBJECT = 1;
   static final int REF_OBJECT = 2;
   static final int SER_OBJECT = 3;
   static final int MAR_OBJECT = 4;

   private Obj() {
   }

   private static Attributes encodeObject(char var0, Object var1, Attributes var2, Attribute var3, boolean var4) throws NamingException {
      boolean var5 = var3.size() == 0 || var3.size() == 1 && var3.contains("top");
      if (var5) {
         var3.add(JAVA_OBJECT_CLASSES[0]);
      }

      if (var1 instanceof Referenceable) {
         var3.add(JAVA_OBJECT_CLASSES[1]);
         var3.add(JAVA_OBJECT_CLASSES[2]);
         if (!var4) {
            var2 = (Attributes)var2.clone();
         }

         var2.put(var3);
         return encodeReference(var0, ((Referenceable)var1).getReference(), var2, var1);
      } else if (var1 instanceof Reference) {
         var3.add(JAVA_OBJECT_CLASSES[1]);
         var3.add(JAVA_OBJECT_CLASSES[2]);
         if (!var4) {
            var2 = (Attributes)var2.clone();
         }

         var2.put(var3);
         return encodeReference(var0, (Reference)var1, var2, (Object)null);
      } else {
         if (var1 instanceof Serializable) {
            var3.add(JAVA_OBJECT_CLASSES[1]);
            if (!var3.contains(JAVA_OBJECT_CLASSES[4]) && !var3.contains(JAVA_OBJECT_CLASSES_LOWER[4])) {
               var3.add(JAVA_OBJECT_CLASSES[3]);
            }

            if (!var4) {
               var2 = (Attributes)var2.clone();
            }

            var2.put(var3);
            var2.put(new BasicAttribute(JAVA_ATTRIBUTES[1], serializeObject(var1)));
            if (var2.get(JAVA_ATTRIBUTES[2]) == null) {
               var2.put(JAVA_ATTRIBUTES[2], var1.getClass().getName());
            }

            if (var2.get(JAVA_ATTRIBUTES[6]) == null) {
               Attribute var6 = LdapCtxFactory.createTypeNameAttr(var1.getClass());
               if (var6 != null) {
                  var2.put(var6);
               }
            }
         } else if (!(var1 instanceof DirContext)) {
            throw new IllegalArgumentException("can only bind Referenceable, Serializable, DirContext");
         }

         return var2;
      }
   }

   private static String[] getCodebases(Attribute var0) throws NamingException {
      if (var0 == null) {
         return null;
      } else {
         StringTokenizer var1 = new StringTokenizer((String)var0.get());
         Vector var2 = new Vector(10);

         while(var1.hasMoreTokens()) {
            var2.addElement(var1.nextToken());
         }

         String[] var3 = new String[var2.size()];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = (String)var2.elementAt(var4);
         }

         return var3;
      }
   }

   static Object decodeObject(Attributes var0) throws NamingException {
      String[] var2 = getCodebases(var0.get(JAVA_ATTRIBUTES[4]));

      try {
         Attribute var1;
         if ((var1 = var0.get(JAVA_ATTRIBUTES[1])) != null) {
            ClassLoader var3 = helper.getURLClassLoader(var2);
            return deserializeObject((byte[])((byte[])var1.get()), var3);
         } else if ((var1 = var0.get(JAVA_ATTRIBUTES[7])) != null) {
            return decodeRmiObject((String)var0.get(JAVA_ATTRIBUTES[2]).get(), (String)var1.get(), var2);
         } else {
            var1 = var0.get(JAVA_ATTRIBUTES[0]);
            return var1 == null || !var1.contains(JAVA_OBJECT_CLASSES[2]) && !var1.contains(JAVA_OBJECT_CLASSES_LOWER[2]) ? null : decodeReference(var0, var2);
         }
      } catch (IOException var5) {
         NamingException var4 = new NamingException();
         var4.setRootCause(var5);
         throw var4;
      }
   }

   private static Attributes encodeReference(char var0, Reference var1, Attributes var2, Object var3) throws NamingException {
      if (var1 == null) {
         return var2;
      } else {
         String var4;
         if ((var4 = var1.getClassName()) != null) {
            var2.put(new BasicAttribute(JAVA_ATTRIBUTES[2], var4));
         }

         if ((var4 = var1.getFactoryClassName()) != null) {
            var2.put(new BasicAttribute(JAVA_ATTRIBUTES[3], var4));
         }

         if ((var4 = var1.getFactoryClassLocation()) != null) {
            var2.put(new BasicAttribute(JAVA_ATTRIBUTES[4], var4));
         }

         if (var3 != null && var2.get(JAVA_ATTRIBUTES[6]) != null) {
            Attribute var5 = LdapCtxFactory.createTypeNameAttr(var3.getClass());
            if (var5 != null) {
               var2.put(var5);
            }
         }

         int var10 = var1.size();
         if (var10 > 0) {
            BasicAttribute var6 = new BasicAttribute(JAVA_ATTRIBUTES[5]);
            BASE64Encoder var8 = null;

            for(int var9 = 0; var9 < var10; ++var9) {
               RefAddr var7 = var1.get(var9);
               if (var7 instanceof StringRefAddr) {
                  var6.add("" + var0 + var9 + var0 + var7.getType() + var0 + var7.getContent());
               } else {
                  if (var8 == null) {
                     var8 = new BASE64Encoder();
                  }

                  var6.add("" + var0 + var9 + var0 + var7.getType() + var0 + var0 + var8.encodeBuffer(serializeObject(var7)));
               }
            }

            var2.put(var6);
         }

         return var2;
      }
   }

   private static Object decodeRmiObject(String var0, String var1, String[] var2) throws NamingException {
      return new Reference(var0, new StringRefAddr("URL", var1));
   }

   private static Reference decodeReference(Attributes var0, String[] var1) throws NamingException, IOException {
      String var4 = null;
      Attribute var2;
      if ((var2 = var0.get(JAVA_ATTRIBUTES[2])) == null) {
         throw new InvalidAttributesException(JAVA_ATTRIBUTES[2] + " attribute is required");
      } else {
         String var3 = (String)var2.get();
         if ((var2 = var0.get(JAVA_ATTRIBUTES[3])) != null) {
            var4 = (String)var2.get();
         }

         Reference var5 = new Reference(var3, var4, var1 != null ? var1[0] : null);
         if ((var2 = var0.get(JAVA_ATTRIBUTES[5])) != null) {
            BASE64Decoder var13 = null;
            ClassLoader var14 = helper.getURLClassLoader(var1);
            Vector var15 = new Vector();
            var15.setSize(var2.size());
            NamingEnumeration var16 = var2.getAll();

            while(var16.hasMore()) {
               String var6 = (String)var16.next();
               if (var6.length() == 0) {
                  throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty attribute value");
               }

               char var9 = var6.charAt(0);
               byte var10 = 1;
               int var11;
               if ((var11 = var6.indexOf(var9, var10)) < 0) {
                  throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - separator '" + var9 + "'not found");
               }

               String var7;
               if ((var7 = var6.substring(var10, var11)) == null) {
                  throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr position");
               }

               int var12;
               try {
                  var12 = Integer.parseInt(var7);
               } catch (NumberFormatException var18) {
                  throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - RefAddr position not an integer");
               }

               int var19 = var11 + 1;
               if ((var11 = var6.indexOf(var9, var19)) < 0) {
                  throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - RefAddr type not found");
               }

               String var8;
               if ((var8 = var6.substring(var19, var11)) == null) {
                  throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr type");
               }

               var19 = var11 + 1;
               if (var19 == var6.length()) {
                  var15.setElementAt(new StringRefAddr(var8, (String)null), var12);
               } else if (var6.charAt(var19) == var9) {
                  ++var19;
                  if (var13 == null) {
                     var13 = new BASE64Decoder();
                  }

                  RefAddr var17 = (RefAddr)deserializeObject(var13.decodeBuffer(var6.substring(var19)), var14);
                  var15.setElementAt(var17, var12);
               } else {
                  var15.setElementAt(new StringRefAddr(var8, var6.substring(var19)), var12);
               }
            }

            for(int var20 = 0; var20 < var15.size(); ++var20) {
               var5.add((RefAddr)var15.elementAt(var20));
            }
         }

         return var5;
      }
   }

   private static byte[] serializeObject(Object var0) throws NamingException {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         ObjectOutputStream var16 = new ObjectOutputStream(var1);
         Throwable var3 = null;

         try {
            var16.writeObject(var0);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var16 != null) {
               if (var3 != null) {
                  try {
                     var16.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var16.close();
               }
            }

         }

         return var1.toByteArray();
      } catch (IOException var15) {
         NamingException var2 = new NamingException();
         var2.setRootCause(var15);
         throw var2;
      }
   }

   private static Object deserializeObject(byte[] var0, ClassLoader var1) throws NamingException {
      try {
         ByteArrayInputStream var2 = new ByteArrayInputStream(var0);

         try {
            Object var20 = var1 == null ? new ObjectInputStream(var2) : new Obj.LoaderInputStream(var2, var1);
            Throwable var21 = null;

            Object var5;
            try {
               var5 = ((ObjectInputStream)var20).readObject();
            } catch (Throwable var16) {
               var21 = var16;
               throw var16;
            } finally {
               if (var20 != null) {
                  if (var21 != null) {
                     try {
                        ((ObjectInputStream)var20).close();
                     } catch (Throwable var15) {
                        var21.addSuppressed(var15);
                     }
                  } else {
                     ((ObjectInputStream)var20).close();
                  }
               }

            }

            return var5;
         } catch (ClassNotFoundException var18) {
            NamingException var4 = new NamingException();
            var4.setRootCause(var18);
            throw var4;
         }
      } catch (IOException var19) {
         NamingException var3 = new NamingException();
         var3.setRootCause(var19);
         throw var3;
      }
   }

   static Attributes determineBindAttrs(char var0, Object var1, Attributes var2, boolean var3, Name var4, Context var5, Hashtable<?, ?> var6) throws NamingException {
      DirStateFactory.Result var7 = DirectoryManager.getStateToBind(var1, var4, var5, var6, var2);
      var1 = var7.getObject();
      Object var10 = var7.getAttributes();
      if (var1 == null) {
         return (Attributes)var10;
      } else {
         if (var10 == null && var1 instanceof DirContext) {
            var3 = true;
            var10 = ((DirContext)var1).getAttributes("");
         }

         boolean var8 = false;
         Object var9;
         if (var10 != null && ((Attributes)var10).size() != 0) {
            var9 = ((Attributes)var10).get("objectClass");
            if (var9 == null && !((Attributes)var10).isCaseIgnored()) {
               var9 = ((Attributes)var10).get("objectclass");
            }

            if (var9 == null) {
               var9 = new BasicAttribute("objectClass", "top");
            } else if (var8 || !var3) {
               var9 = (Attribute)((Attribute)var9).clone();
            }
         } else {
            var10 = new BasicAttributes(true);
            var3 = true;
            var9 = new BasicAttribute("objectClass", "top");
         }

         var2 = encodeObject(var0, var1, (Attributes)var10, (Attribute)var9, var3);
         return var2;
      }
   }

   private static final class LoaderInputStream extends ObjectInputStream {
      private ClassLoader classLoader;

      LoaderInputStream(InputStream var1, ClassLoader var2) throws IOException {
         super(var1);
         this.classLoader = var2;
      }

      protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
         try {
            return this.classLoader.loadClass(var1.getName());
         } catch (ClassNotFoundException var3) {
            return super.resolveClass(var1);
         }
      }

      protected Class<?> resolveProxyClass(String[] var1) throws IOException, ClassNotFoundException {
         ClassLoader var2 = null;
         boolean var3 = false;
         Class[] var4 = new Class[var1.length];

         for(int var5 = 0; var5 < var1.length; ++var5) {
            Class var6 = Class.forName(var1[var5], false, this.classLoader);
            if ((var6.getModifiers() & 1) == 0) {
               if (var3) {
                  if (var2 != var6.getClassLoader()) {
                     throw new IllegalAccessError("conflicting non-public interface class loaders");
                  }
               } else {
                  var2 = var6.getClassLoader();
                  var3 = true;
               }
            }

            var4[var5] = var6;
         }

         try {
            return Proxy.getProxyClass(var3 ? var2 : this.classLoader, var4);
         } catch (IllegalArgumentException var7) {
            throw new ClassNotFoundException((String)null, var7);
         }
      }
   }
}
