package com.sun.xml.internal.ws.org.objectweb.asm;

public class ClassWriter implements ClassVisitor {
   public static final int COMPUTE_MAXS = 1;
   public static final int COMPUTE_FRAMES = 2;
   static final int NOARG_INSN = 0;
   static final int SBYTE_INSN = 1;
   static final int SHORT_INSN = 2;
   static final int VAR_INSN = 3;
   static final int IMPLVAR_INSN = 4;
   static final int TYPE_INSN = 5;
   static final int FIELDORMETH_INSN = 6;
   static final int ITFMETH_INSN = 7;
   static final int LABEL_INSN = 8;
   static final int LABELW_INSN = 9;
   static final int LDC_INSN = 10;
   static final int LDCW_INSN = 11;
   static final int IINC_INSN = 12;
   static final int TABL_INSN = 13;
   static final int LOOK_INSN = 14;
   static final int MANA_INSN = 15;
   static final int WIDE_INSN = 16;
   static final byte[] TYPE;
   static final int CLASS = 7;
   static final int FIELD = 9;
   static final int METH = 10;
   static final int IMETH = 11;
   static final int STR = 8;
   static final int INT = 3;
   static final int FLOAT = 4;
   static final int LONG = 5;
   static final int DOUBLE = 6;
   static final int NAME_TYPE = 12;
   static final int UTF8 = 1;
   static final int TYPE_NORMAL = 13;
   static final int TYPE_UNINIT = 14;
   static final int TYPE_MERGED = 15;
   ClassReader cr;
   int version;
   int index;
   final ByteVector pool;
   Item[] items;
   int threshold;
   final Item key;
   final Item key2;
   final Item key3;
   Item[] typeTable;
   private short typeCount;
   private int access;
   private int name;
   String thisName;
   private int signature;
   private int superName;
   private int interfaceCount;
   private int[] interfaces;
   private int sourceFile;
   private ByteVector sourceDebug;
   private int enclosingMethodOwner;
   private int enclosingMethod;
   private AnnotationWriter anns;
   private AnnotationWriter ianns;
   private Attribute attrs;
   private int innerClassesCount;
   private ByteVector innerClasses;
   FieldWriter firstField;
   FieldWriter lastField;
   MethodWriter firstMethod;
   MethodWriter lastMethod;
   private final boolean computeMaxs;
   private final boolean computeFrames;
   boolean invalidFrames;

   public ClassWriter(int flags) {
      this.index = 1;
      this.pool = new ByteVector();
      this.items = new Item[256];
      this.threshold = (int)(0.75D * (double)this.items.length);
      this.key = new Item();
      this.key2 = new Item();
      this.key3 = new Item();
      this.computeMaxs = (flags & 1) != 0;
      this.computeFrames = (flags & 2) != 0;
   }

   public ClassWriter(ClassReader classReader, int flags) {
      this(flags);
      classReader.copyPool(this);
      this.cr = classReader;
   }

   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      this.version = version;
      this.access = access;
      this.name = this.newClass(name);
      this.thisName = name;
      if (signature != null) {
         this.signature = this.newUTF8(signature);
      }

      this.superName = superName == null ? 0 : this.newClass(superName);
      if (interfaces != null && interfaces.length > 0) {
         this.interfaceCount = interfaces.length;
         this.interfaces = new int[this.interfaceCount];

         for(int i = 0; i < this.interfaceCount; ++i) {
            this.interfaces[i] = this.newClass(interfaces[i]);
         }
      }

   }

   public void visitSource(String file, String debug) {
      if (file != null) {
         this.sourceFile = this.newUTF8(file);
      }

      if (debug != null) {
         this.sourceDebug = (new ByteVector()).putUTF8(debug);
      }

   }

   public void visitOuterClass(String owner, String name, String desc) {
      this.enclosingMethodOwner = this.newClass(owner);
      if (name != null && desc != null) {
         this.enclosingMethod = this.newNameType(name, desc);
      }

   }

   public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      ByteVector bv = new ByteVector();
      bv.putShort(this.newUTF8(desc)).putShort(0);
      AnnotationWriter aw = new AnnotationWriter(this, true, bv, bv, 2);
      if (visible) {
         aw.next = this.anns;
         this.anns = aw;
      } else {
         aw.next = this.ianns;
         this.ianns = aw;
      }

      return aw;
   }

   public void visitAttribute(Attribute attr) {
      attr.next = this.attrs;
      this.attrs = attr;
   }

   public void visitInnerClass(String name, String outerName, String innerName, int access) {
      if (this.innerClasses == null) {
         this.innerClasses = new ByteVector();
      }

      ++this.innerClassesCount;
      this.innerClasses.putShort(name == null ? 0 : this.newClass(name));
      this.innerClasses.putShort(outerName == null ? 0 : this.newClass(outerName));
      this.innerClasses.putShort(innerName == null ? 0 : this.newUTF8(innerName));
      this.innerClasses.putShort(access);
   }

   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
      return new FieldWriter(this, access, name, desc, signature, value);
   }

   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      return new MethodWriter(this, access, name, desc, signature, exceptions, this.computeMaxs, this.computeFrames);
   }

   public void visitEnd() {
   }

   public byte[] toByteArray() {
      int size = 24 + 2 * this.interfaceCount;
      int nbFields = 0;

      FieldWriter fb;
      for(fb = this.firstField; fb != null; fb = fb.next) {
         ++nbFields;
         size += fb.getSize();
      }

      int nbMethods = 0;

      MethodWriter mb;
      for(mb = this.firstMethod; mb != null; mb = mb.next) {
         ++nbMethods;
         size += mb.getSize();
      }

      int attributeCount = 0;
      if (this.signature != 0) {
         ++attributeCount;
         size += 8;
         this.newUTF8("Signature");
      }

      if (this.sourceFile != 0) {
         ++attributeCount;
         size += 8;
         this.newUTF8("SourceFile");
      }

      if (this.sourceDebug != null) {
         ++attributeCount;
         size += this.sourceDebug.length + 4;
         this.newUTF8("SourceDebugExtension");
      }

      if (this.enclosingMethodOwner != 0) {
         ++attributeCount;
         size += 10;
         this.newUTF8("EnclosingMethod");
      }

      if ((this.access & 131072) != 0) {
         ++attributeCount;
         size += 6;
         this.newUTF8("Deprecated");
      }

      if ((this.access & 4096) != 0 && (this.version & '\uffff') < 49) {
         ++attributeCount;
         size += 6;
         this.newUTF8("Synthetic");
      }

      if (this.innerClasses != null) {
         ++attributeCount;
         size += 8 + this.innerClasses.length;
         this.newUTF8("InnerClasses");
      }

      if (this.anns != null) {
         ++attributeCount;
         size += 8 + this.anns.getSize();
         this.newUTF8("RuntimeVisibleAnnotations");
      }

      if (this.ianns != null) {
         ++attributeCount;
         size += 8 + this.ianns.getSize();
         this.newUTF8("RuntimeInvisibleAnnotations");
      }

      if (this.attrs != null) {
         attributeCount += this.attrs.getCount();
         size += this.attrs.getSize(this, (byte[])null, 0, -1, -1);
      }

      size += this.pool.length;
      ByteVector out = new ByteVector(size);
      out.putInt(-889275714).putInt(this.version);
      out.putShort(this.index).putByteArray(this.pool.data, 0, this.pool.length);
      out.putShort(this.access).putShort(this.name).putShort(this.superName);
      out.putShort(this.interfaceCount);

      int len;
      for(len = 0; len < this.interfaceCount; ++len) {
         out.putShort(this.interfaces[len]);
      }

      out.putShort(nbFields);

      for(fb = this.firstField; fb != null; fb = fb.next) {
         fb.put(out);
      }

      out.putShort(nbMethods);

      for(mb = this.firstMethod; mb != null; mb = mb.next) {
         mb.put(out);
      }

      out.putShort(attributeCount);
      if (this.signature != 0) {
         out.putShort(this.newUTF8("Signature")).putInt(2).putShort(this.signature);
      }

      if (this.sourceFile != 0) {
         out.putShort(this.newUTF8("SourceFile")).putInt(2).putShort(this.sourceFile);
      }

      if (this.sourceDebug != null) {
         len = this.sourceDebug.length - 2;
         out.putShort(this.newUTF8("SourceDebugExtension")).putInt(len);
         out.putByteArray(this.sourceDebug.data, 2, len);
      }

      if (this.enclosingMethodOwner != 0) {
         out.putShort(this.newUTF8("EnclosingMethod")).putInt(4);
         out.putShort(this.enclosingMethodOwner).putShort(this.enclosingMethod);
      }

      if ((this.access & 131072) != 0) {
         out.putShort(this.newUTF8("Deprecated")).putInt(0);
      }

      if ((this.access & 4096) != 0 && (this.version & '\uffff') < 49) {
         out.putShort(this.newUTF8("Synthetic")).putInt(0);
      }

      if (this.innerClasses != null) {
         out.putShort(this.newUTF8("InnerClasses"));
         out.putInt(this.innerClasses.length + 2).putShort(this.innerClassesCount);
         out.putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
      }

      if (this.anns != null) {
         out.putShort(this.newUTF8("RuntimeVisibleAnnotations"));
         this.anns.put(out);
      }

      if (this.ianns != null) {
         out.putShort(this.newUTF8("RuntimeInvisibleAnnotations"));
         this.ianns.put(out);
      }

      if (this.attrs != null) {
         this.attrs.put(this, (byte[])null, 0, -1, -1, out);
      }

      if (this.invalidFrames) {
         ClassWriter cw = new ClassWriter(2);
         (new ClassReader(out.data)).accept(cw, 4);
         return cw.toByteArray();
      } else {
         return out.data;
      }
   }

   Item newConstItem(Object cst) {
      int val;
      if (cst instanceof Integer) {
         val = (Integer)cst;
         return this.newInteger(val);
      } else if (cst instanceof Byte) {
         val = ((Byte)cst).intValue();
         return this.newInteger(val);
      } else if (cst instanceof Character) {
         int val = (Character)cst;
         return this.newInteger(val);
      } else if (cst instanceof Short) {
         val = ((Short)cst).intValue();
         return this.newInteger(val);
      } else if (cst instanceof Boolean) {
         val = (Boolean)cst ? 1 : 0;
         return this.newInteger(val);
      } else if (cst instanceof Float) {
         float val = (Float)cst;
         return this.newFloat(val);
      } else if (cst instanceof Long) {
         long val = (Long)cst;
         return this.newLong(val);
      } else if (cst instanceof Double) {
         double val = (Double)cst;
         return this.newDouble(val);
      } else if (cst instanceof String) {
         return this.newString((String)cst);
      } else if (cst instanceof Type) {
         Type t = (Type)cst;
         return this.newClassItem(t.getSort() == 10 ? t.getInternalName() : t.getDescriptor());
      } else {
         throw new IllegalArgumentException("value " + cst);
      }
   }

   public int newConst(Object cst) {
      return this.newConstItem(cst).index;
   }

   public int newUTF8(String value) {
      this.key.set(1, value, (String)null, (String)null);
      Item result = this.get(this.key);
      if (result == null) {
         this.pool.putByte(1).putUTF8(value);
         result = new Item(this.index++, this.key);
         this.put(result);
      }

      return result.index;
   }

   Item newClassItem(String value) {
      this.key2.set(7, value, (String)null, (String)null);
      Item result = this.get(this.key2);
      if (result == null) {
         this.pool.put12(7, this.newUTF8(value));
         result = new Item(this.index++, this.key2);
         this.put(result);
      }

      return result;
   }

   public int newClass(String value) {
      return this.newClassItem(value).index;
   }

   Item newFieldItem(String owner, String name, String desc) {
      this.key3.set(9, owner, name, desc);
      Item result = this.get(this.key3);
      if (result == null) {
         this.put122(9, this.newClass(owner), this.newNameType(name, desc));
         result = new Item(this.index++, this.key3);
         this.put(result);
      }

      return result;
   }

   public int newField(String owner, String name, String desc) {
      return this.newFieldItem(owner, name, desc).index;
   }

   Item newMethodItem(String owner, String name, String desc, boolean itf) {
      int type = itf ? 11 : 10;
      this.key3.set(type, owner, name, desc);
      Item result = this.get(this.key3);
      if (result == null) {
         this.put122(type, this.newClass(owner), this.newNameType(name, desc));
         result = new Item(this.index++, this.key3);
         this.put(result);
      }

      return result;
   }

   public int newMethod(String owner, String name, String desc, boolean itf) {
      return this.newMethodItem(owner, name, desc, itf).index;
   }

   Item newInteger(int value) {
      this.key.set(value);
      Item result = this.get(this.key);
      if (result == null) {
         this.pool.putByte(3).putInt(value);
         result = new Item(this.index++, this.key);
         this.put(result);
      }

      return result;
   }

   Item newFloat(float value) {
      this.key.set(value);
      Item result = this.get(this.key);
      if (result == null) {
         this.pool.putByte(4).putInt(this.key.intVal);
         result = new Item(this.index++, this.key);
         this.put(result);
      }

      return result;
   }

   Item newLong(long value) {
      this.key.set(value);
      Item result = this.get(this.key);
      if (result == null) {
         this.pool.putByte(5).putLong(value);
         result = new Item(this.index, this.key);
         this.put(result);
         this.index += 2;
      }

      return result;
   }

   Item newDouble(double value) {
      this.key.set(value);
      Item result = this.get(this.key);
      if (result == null) {
         this.pool.putByte(6).putLong(this.key.longVal);
         result = new Item(this.index, this.key);
         this.put(result);
         this.index += 2;
      }

      return result;
   }

   private Item newString(String value) {
      this.key2.set(8, value, (String)null, (String)null);
      Item result = this.get(this.key2);
      if (result == null) {
         this.pool.put12(8, this.newUTF8(value));
         result = new Item(this.index++, this.key2);
         this.put(result);
      }

      return result;
   }

   public int newNameType(String name, String desc) {
      this.key2.set(12, name, desc, (String)null);
      Item result = this.get(this.key2);
      if (result == null) {
         this.put122(12, this.newUTF8(name), this.newUTF8(desc));
         result = new Item(this.index++, this.key2);
         this.put(result);
      }

      return result.index;
   }

   int addType(String type) {
      this.key.set(13, type, (String)null, (String)null);
      Item result = this.get(this.key);
      if (result == null) {
         result = this.addType(this.key);
      }

      return result.index;
   }

   int addUninitializedType(String type, int offset) {
      this.key.type = 14;
      this.key.intVal = offset;
      this.key.strVal1 = type;
      this.key.hashCode = Integer.MAX_VALUE & 14 + type.hashCode() + offset;
      Item result = this.get(this.key);
      if (result == null) {
         result = this.addType(this.key);
      }

      return result.index;
   }

   private Item addType(Item item) {
      ++this.typeCount;
      Item result = new Item(this.typeCount, this.key);
      this.put(result);
      if (this.typeTable == null) {
         this.typeTable = new Item[16];
      }

      if (this.typeCount == this.typeTable.length) {
         Item[] newTable = new Item[2 * this.typeTable.length];
         System.arraycopy(this.typeTable, 0, newTable, 0, this.typeTable.length);
         this.typeTable = newTable;
      }

      this.typeTable[this.typeCount] = result;
      return result;
   }

   int getMergedType(int type1, int type2) {
      this.key2.type = 15;
      this.key2.longVal = (long)type1 | (long)type2 << 32;
      this.key2.hashCode = Integer.MAX_VALUE & 15 + type1 + type2;
      Item result = this.get(this.key2);
      if (result == null) {
         String t = this.typeTable[type1].strVal1;
         String u = this.typeTable[type2].strVal1;
         this.key2.intVal = this.addType(this.getCommonSuperClass(t, u));
         result = new Item(0, this.key2);
         this.put(result);
      }

      return result.intVal;
   }

   protected String getCommonSuperClass(String type1, String type2) {
      Class c;
      Class d;
      try {
         c = Class.forName(type1.replace('/', '.'));
         d = Class.forName(type2.replace('/', '.'));
      } catch (Exception var6) {
         throw new RuntimeException(var6.toString());
      }

      if (c.isAssignableFrom(d)) {
         return type1;
      } else if (d.isAssignableFrom(c)) {
         return type2;
      } else if (!c.isInterface() && !d.isInterface()) {
         do {
            c = c.getSuperclass();
         } while(!c.isAssignableFrom(d));

         return c.getName().replace('.', '/');
      } else {
         return "java/lang/Object";
      }
   }

   private Item get(Item key) {
      Item i;
      for(i = this.items[key.hashCode % this.items.length]; i != null && !key.isEqualTo(i); i = i.next) {
      }

      return i;
   }

   private void put(Item i) {
      int ll;
      if (this.index > this.threshold) {
         ll = this.items.length;
         int nl = ll * 2 + 1;
         Item[] newItems = new Item[nl];

         Item k;
         for(int l = ll - 1; l >= 0; --l) {
            for(Item j = this.items[l]; j != null; j = k) {
               int index = j.hashCode % newItems.length;
               k = j.next;
               j.next = newItems[index];
               newItems[index] = j;
            }
         }

         this.items = newItems;
         this.threshold = (int)((double)nl * 0.75D);
      }

      ll = i.hashCode % this.items.length;
      i.next = this.items[ll];
      this.items[ll] = i;
   }

   private void put122(int b, int s1, int s2) {
      this.pool.put12(b, s1).putShort(s2);
   }

   static {
      byte[] b = new byte[220];
      String s = "AAAAAAAAAAAAAAAABCKLLDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIDNOAAAAAAGGGGGGGHAFBFAAFFAAQPIIJJIIIIIIIIIIIIIIIIII";

      for(int i = 0; i < b.length; ++i) {
         b[i] = (byte)(s.charAt(i) - 65);
      }

      TYPE = b;
   }
}
