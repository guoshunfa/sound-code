package com.sun.xml.internal.ws.org.objectweb.asm;

final class FieldWriter implements FieldVisitor {
   FieldWriter next;
   private final ClassWriter cw;
   private final int access;
   private final int name;
   private final int desc;
   private int signature;
   private int value;
   private AnnotationWriter anns;
   private AnnotationWriter ianns;
   private Attribute attrs;

   FieldWriter(ClassWriter cw, int access, String name, String desc, String signature, Object value) {
      if (cw.firstField == null) {
         cw.firstField = this;
      } else {
         cw.lastField.next = this;
      }

      cw.lastField = this;
      this.cw = cw;
      this.access = access;
      this.name = cw.newUTF8(name);
      this.desc = cw.newUTF8(desc);
      if (signature != null) {
         this.signature = cw.newUTF8(signature);
      }

      if (value != null) {
         this.value = cw.newConstItem(value).index;
      }

   }

   public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      ByteVector bv = new ByteVector();
      bv.putShort(this.cw.newUTF8(desc)).putShort(0);
      AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, 2);
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

   public void visitEnd() {
   }

   int getSize() {
      int size = 8;
      if (this.value != 0) {
         this.cw.newUTF8("ConstantValue");
         size += 8;
      }

      if ((this.access & 4096) != 0 && (this.cw.version & '\uffff') < 49) {
         this.cw.newUTF8("Synthetic");
         size += 6;
      }

      if ((this.access & 131072) != 0) {
         this.cw.newUTF8("Deprecated");
         size += 6;
      }

      if (this.signature != 0) {
         this.cw.newUTF8("Signature");
         size += 8;
      }

      if (this.anns != null) {
         this.cw.newUTF8("RuntimeVisibleAnnotations");
         size += 8 + this.anns.getSize();
      }

      if (this.ianns != null) {
         this.cw.newUTF8("RuntimeInvisibleAnnotations");
         size += 8 + this.ianns.getSize();
      }

      if (this.attrs != null) {
         size += this.attrs.getSize(this.cw, (byte[])null, 0, -1, -1);
      }

      return size;
   }

   void put(ByteVector out) {
      out.putShort(this.access).putShort(this.name).putShort(this.desc);
      int attributeCount = 0;
      if (this.value != 0) {
         ++attributeCount;
      }

      if ((this.access & 4096) != 0 && (this.cw.version & '\uffff') < 49) {
         ++attributeCount;
      }

      if ((this.access & 131072) != 0) {
         ++attributeCount;
      }

      if (this.signature != 0) {
         ++attributeCount;
      }

      if (this.anns != null) {
         ++attributeCount;
      }

      if (this.ianns != null) {
         ++attributeCount;
      }

      if (this.attrs != null) {
         attributeCount += this.attrs.getCount();
      }

      out.putShort(attributeCount);
      if (this.value != 0) {
         out.putShort(this.cw.newUTF8("ConstantValue"));
         out.putInt(2).putShort(this.value);
      }

      if ((this.access & 4096) != 0 && (this.cw.version & '\uffff') < 49) {
         out.putShort(this.cw.newUTF8("Synthetic")).putInt(0);
      }

      if ((this.access & 131072) != 0) {
         out.putShort(this.cw.newUTF8("Deprecated")).putInt(0);
      }

      if (this.signature != 0) {
         out.putShort(this.cw.newUTF8("Signature"));
         out.putInt(2).putShort(this.signature);
      }

      if (this.anns != null) {
         out.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
         this.anns.put(out);
      }

      if (this.ianns != null) {
         out.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
         this.ianns.put(out);
      }

      if (this.attrs != null) {
         this.attrs.put(this.cw, (byte[])null, 0, -1, -1, out);
      }

   }
}
