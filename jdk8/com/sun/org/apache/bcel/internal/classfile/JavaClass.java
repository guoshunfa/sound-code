package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.ClassQueue;
import com.sun.org.apache.bcel.internal.util.ClassVector;
import com.sun.org.apache.bcel.internal.util.Repository;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

public class JavaClass extends AccessFlags implements Cloneable, Node {
   private String file_name;
   private String package_name;
   private String source_file_name;
   private int class_name_index;
   private int superclass_name_index;
   private String class_name;
   private String superclass_name;
   private int major;
   private int minor;
   private ConstantPool constant_pool;
   private int[] interfaces;
   private String[] interface_names;
   private Field[] fields;
   private Method[] methods;
   private Attribute[] attributes;
   private byte source;
   public static final byte HEAP = 1;
   public static final byte FILE = 2;
   public static final byte ZIP = 3;
   static boolean debug = false;
   static char sep = '/';
   private transient Repository repository;

   public JavaClass(int class_name_index, int superclass_name_index, String file_name, int major, int minor, int access_flags, ConstantPool constant_pool, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes, byte source) {
      this.source_file_name = "<Unknown>";
      this.source = 1;
      this.repository = SyntheticRepository.getInstance();
      if (interfaces == null) {
         interfaces = new int[0];
      }

      if (attributes == null) {
         this.attributes = new Attribute[0];
      }

      if (fields == null) {
         fields = new Field[0];
      }

      if (methods == null) {
         methods = new Method[0];
      }

      this.class_name_index = class_name_index;
      this.superclass_name_index = superclass_name_index;
      this.file_name = file_name;
      this.major = major;
      this.minor = minor;
      this.access_flags = access_flags;
      this.constant_pool = constant_pool;
      this.interfaces = interfaces;
      this.fields = fields;
      this.methods = methods;
      this.attributes = attributes;
      this.source = source;

      int index;
      for(index = 0; index < attributes.length; ++index) {
         if (attributes[index] instanceof SourceFile) {
            this.source_file_name = ((SourceFile)attributes[index]).getSourceFileName();
            break;
         }
      }

      this.class_name = constant_pool.getConstantString(class_name_index, (byte)7);
      this.class_name = Utility.compactClassName(this.class_name, false);
      index = this.class_name.lastIndexOf(46);
      if (index < 0) {
         this.package_name = "";
      } else {
         this.package_name = this.class_name.substring(0, index);
      }

      if (superclass_name_index > 0) {
         this.superclass_name = constant_pool.getConstantString(superclass_name_index, (byte)7);
         this.superclass_name = Utility.compactClassName(this.superclass_name, false);
      } else {
         this.superclass_name = "java.lang.Object";
      }

      this.interface_names = new String[interfaces.length];

      for(int i = 0; i < interfaces.length; ++i) {
         String str = constant_pool.getConstantString(interfaces[i], (byte)7);
         this.interface_names[i] = Utility.compactClassName(str, false);
      }

   }

   public JavaClass(int class_name_index, int superclass_name_index, String file_name, int major, int minor, int access_flags, ConstantPool constant_pool, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes) {
      this(class_name_index, superclass_name_index, file_name, major, minor, access_flags, constant_pool, interfaces, fields, methods, attributes, (byte)1);
   }

   public void accept(Visitor v) {
      v.visitJavaClass(this);
   }

   static final void Debug(String str) {
      if (debug) {
         System.out.println(str);
      }

   }

   public void dump(File file) throws IOException {
      String parent = file.getParent();
      if (parent != null) {
         File dir = new File(parent);
         if (dir != null) {
            dir.mkdirs();
         }
      }

      this.dump(new DataOutputStream(new FileOutputStream(file)));
   }

   public void dump(String file_name) throws IOException {
      this.dump(new File(file_name));
   }

   public byte[] getBytes() {
      ByteArrayOutputStream s = new ByteArrayOutputStream();
      DataOutputStream ds = new DataOutputStream(s);

      try {
         this.dump(ds);
      } catch (IOException var12) {
         var12.printStackTrace();
      } finally {
         try {
            ds.close();
         } catch (IOException var11) {
            var11.printStackTrace();
         }

      }

      return s.toByteArray();
   }

   public void dump(OutputStream file) throws IOException {
      this.dump(new DataOutputStream(file));
   }

   public void dump(DataOutputStream file) throws IOException {
      file.writeInt(-889275714);
      file.writeShort(this.minor);
      file.writeShort(this.major);
      this.constant_pool.dump(file);
      file.writeShort(this.access_flags);
      file.writeShort(this.class_name_index);
      file.writeShort(this.superclass_name_index);
      file.writeShort(this.interfaces.length);

      int i;
      for(i = 0; i < this.interfaces.length; ++i) {
         file.writeShort(this.interfaces[i]);
      }

      file.writeShort(this.fields.length);

      for(i = 0; i < this.fields.length; ++i) {
         this.fields[i].dump(file);
      }

      file.writeShort(this.methods.length);

      for(i = 0; i < this.methods.length; ++i) {
         this.methods[i].dump(file);
      }

      if (this.attributes != null) {
         file.writeShort(this.attributes.length);

         for(i = 0; i < this.attributes.length; ++i) {
            this.attributes[i].dump(file);
         }
      } else {
         file.writeShort(0);
      }

      file.close();
   }

   public Attribute[] getAttributes() {
      return this.attributes;
   }

   public String getClassName() {
      return this.class_name;
   }

   public String getPackageName() {
      return this.package_name;
   }

   public int getClassNameIndex() {
      return this.class_name_index;
   }

   public ConstantPool getConstantPool() {
      return this.constant_pool;
   }

   public Field[] getFields() {
      return this.fields;
   }

   public String getFileName() {
      return this.file_name;
   }

   public String[] getInterfaceNames() {
      return this.interface_names;
   }

   public int[] getInterfaceIndices() {
      return this.interfaces;
   }

   public int getMajor() {
      return this.major;
   }

   public Method[] getMethods() {
      return this.methods;
   }

   public Method getMethod(java.lang.reflect.Method m) {
      for(int i = 0; i < this.methods.length; ++i) {
         Method method = this.methods[i];
         if (m.getName().equals(method.getName()) && m.getModifiers() == method.getModifiers() && Type.getSignature(m).equals(method.getSignature())) {
            return method;
         }
      }

      return null;
   }

   public int getMinor() {
      return this.minor;
   }

   public String getSourceFileName() {
      return this.source_file_name;
   }

   public String getSuperclassName() {
      return this.superclass_name;
   }

   public int getSuperclassNameIndex() {
      return this.superclass_name_index;
   }

   public void setAttributes(Attribute[] attributes) {
      this.attributes = attributes;
   }

   public void setClassName(String class_name) {
      this.class_name = class_name;
   }

   public void setClassNameIndex(int class_name_index) {
      this.class_name_index = class_name_index;
   }

   public void setConstantPool(ConstantPool constant_pool) {
      this.constant_pool = constant_pool;
   }

   public void setFields(Field[] fields) {
      this.fields = fields;
   }

   public void setFileName(String file_name) {
      this.file_name = file_name;
   }

   public void setInterfaceNames(String[] interface_names) {
      this.interface_names = interface_names;
   }

   public void setInterfaces(int[] interfaces) {
      this.interfaces = interfaces;
   }

   public void setMajor(int major) {
      this.major = major;
   }

   public void setMethods(Method[] methods) {
      this.methods = methods;
   }

   public void setMinor(int minor) {
      this.minor = minor;
   }

   public void setSourceFileName(String source_file_name) {
      this.source_file_name = source_file_name;
   }

   public void setSuperclassName(String superclass_name) {
      this.superclass_name = superclass_name;
   }

   public void setSuperclassNameIndex(int superclass_name_index) {
      this.superclass_name_index = superclass_name_index;
   }

   public String toString() {
      String access = Utility.accessToString(this.access_flags, true);
      access = access.equals("") ? "" : access + " ";
      StringBuffer buf = new StringBuffer(access + Utility.classOrInterface(this.access_flags) + " " + this.class_name + " extends " + Utility.compactClassName(this.superclass_name, false) + '\n');
      int size = this.interfaces.length;
      int i;
      if (size > 0) {
         buf.append("implements\t\t");

         for(i = 0; i < size; ++i) {
            buf.append(this.interface_names[i]);
            if (i < size - 1) {
               buf.append(", ");
            }
         }

         buf.append('\n');
      }

      buf.append("filename\t\t" + this.file_name + '\n');
      buf.append("compiled from\t\t" + this.source_file_name + '\n');
      buf.append("compiler version\t" + this.major + "." + this.minor + '\n');
      buf.append("access flags\t\t" + this.access_flags + '\n');
      buf.append("constant pool\t\t" + this.constant_pool.getLength() + " entries\n");
      buf.append("ACC_SUPER flag\t\t" + this.isSuper() + "\n");
      if (this.attributes.length > 0) {
         buf.append("\nAttribute(s):\n");

         for(i = 0; i < this.attributes.length; ++i) {
            buf.append(indent(this.attributes[i]));
         }
      }

      if (this.fields.length > 0) {
         buf.append("\n" + this.fields.length + " fields:\n");

         for(i = 0; i < this.fields.length; ++i) {
            buf.append("\t" + this.fields[i] + '\n');
         }
      }

      if (this.methods.length > 0) {
         buf.append("\n" + this.methods.length + " methods:\n");

         for(i = 0; i < this.methods.length; ++i) {
            buf.append("\t" + this.methods[i] + '\n');
         }
      }

      return buf.toString();
   }

   private static final String indent(Object obj) {
      StringTokenizer tok = new StringTokenizer(obj.toString(), "\n");
      StringBuffer buf = new StringBuffer();

      while(tok.hasMoreTokens()) {
         buf.append("\t" + tok.nextToken() + "\n");
      }

      return buf.toString();
   }

   public JavaClass copy() {
      JavaClass c = null;

      try {
         c = (JavaClass)this.clone();
      } catch (CloneNotSupportedException var3) {
      }

      c.constant_pool = this.constant_pool.copy();
      c.interfaces = (int[])((int[])this.interfaces.clone());
      c.interface_names = (String[])((String[])this.interface_names.clone());
      c.fields = new Field[this.fields.length];

      int i;
      for(i = 0; i < this.fields.length; ++i) {
         c.fields[i] = this.fields[i].copy(c.constant_pool);
      }

      c.methods = new Method[this.methods.length];

      for(i = 0; i < this.methods.length; ++i) {
         c.methods[i] = this.methods[i].copy(c.constant_pool);
      }

      c.attributes = new Attribute[this.attributes.length];

      for(i = 0; i < this.attributes.length; ++i) {
         c.attributes[i] = this.attributes[i].copy(c.constant_pool);
      }

      return c;
   }

   public final boolean isSuper() {
      return (this.access_flags & 32) != 0;
   }

   public final boolean isClass() {
      return (this.access_flags & 512) == 0;
   }

   public final byte getSource() {
      return this.source;
   }

   public Repository getRepository() {
      return this.repository;
   }

   public void setRepository(Repository repository) {
      this.repository = repository;
   }

   public final boolean instanceOf(JavaClass super_class) {
      if (this.equals(super_class)) {
         return true;
      } else {
         JavaClass[] super_classes = this.getSuperClasses();

         for(int i = 0; i < super_classes.length; ++i) {
            if (super_classes[i].equals(super_class)) {
               return true;
            }
         }

         if (super_class.isInterface()) {
            return this.implementationOf(super_class);
         } else {
            return false;
         }
      }
   }

   public boolean implementationOf(JavaClass inter) {
      if (!inter.isInterface()) {
         throw new IllegalArgumentException(inter.getClassName() + " is no interface");
      } else if (this.equals(inter)) {
         return true;
      } else {
         JavaClass[] super_interfaces = this.getAllInterfaces();

         for(int i = 0; i < super_interfaces.length; ++i) {
            if (super_interfaces[i].equals(inter)) {
               return true;
            }
         }

         return false;
      }
   }

   public JavaClass getSuperClass() {
      if ("java.lang.Object".equals(this.getClassName())) {
         return null;
      } else {
         try {
            return this.repository.loadClass(this.getSuperclassName());
         } catch (ClassNotFoundException var2) {
            System.err.println((Object)var2);
            return null;
         }
      }
   }

   public JavaClass[] getSuperClasses() {
      ClassVector vec = new ClassVector();

      for(JavaClass clazz = this.getSuperClass(); clazz != null; clazz = clazz.getSuperClass()) {
         vec.addElement(clazz);
      }

      return vec.toArray();
   }

   public JavaClass[] getInterfaces() {
      String[] interfaces = this.getInterfaceNames();
      JavaClass[] classes = new JavaClass[interfaces.length];

      try {
         for(int i = 0; i < interfaces.length; ++i) {
            classes[i] = this.repository.loadClass(interfaces[i]);
         }

         return classes;
      } catch (ClassNotFoundException var4) {
         System.err.println((Object)var4);
         return null;
      }
   }

   public JavaClass[] getAllInterfaces() {
      ClassQueue queue = new ClassQueue();
      ClassVector vec = new ClassVector();
      queue.enqueue(this);

      while(!queue.empty()) {
         JavaClass clazz = queue.dequeue();
         JavaClass souper = clazz.getSuperClass();
         JavaClass[] interfaces = clazz.getInterfaces();
         if (clazz.isInterface()) {
            vec.addElement(clazz);
         } else if (souper != null) {
            queue.enqueue(souper);
         }

         for(int i = 0; i < interfaces.length; ++i) {
            queue.enqueue(interfaces[i]);
         }
      }

      return vec.toArray();
   }

   static {
      String debug = null;
      String sep = null;

      try {
         debug = SecuritySupport.getSystemProperty("JavaClass.debug");
         sep = SecuritySupport.getSystemProperty("file.separator");
      } catch (SecurityException var4) {
      }

      if (debug != null) {
         JavaClass.debug = new Boolean(debug);
      }

      if (sep != null) {
         try {
            JavaClass.sep = sep.charAt(0);
         } catch (StringIndexOutOfBoundsException var3) {
         }
      }

   }
}
