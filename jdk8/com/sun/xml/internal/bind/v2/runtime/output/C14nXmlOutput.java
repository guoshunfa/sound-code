package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

public class C14nXmlOutput extends UTF8XmlOutput {
   private C14nXmlOutput.StaticAttribute[] staticAttributes = new C14nXmlOutput.StaticAttribute[8];
   private int len = 0;
   private int[] nsBuf = new int[8];
   private final FinalArrayList<C14nXmlOutput.DynamicAttribute> otherAttributes = new FinalArrayList();
   private final boolean namedAttributesAreOrdered;

   public C14nXmlOutput(OutputStream out, Encoded[] localNames, boolean namedAttributesAreOrdered, CharacterEscapeHandler escapeHandler) {
      super(out, localNames, escapeHandler);
      this.namedAttributesAreOrdered = namedAttributesAreOrdered;

      for(int i = 0; i < this.staticAttributes.length; ++i) {
         this.staticAttributes[i] = new C14nXmlOutput.StaticAttribute();
      }

   }

   public void attribute(Name name, String value) throws IOException {
      if (this.staticAttributes.length == this.len) {
         int newLen = this.len * 2;
         C14nXmlOutput.StaticAttribute[] newbuf = new C14nXmlOutput.StaticAttribute[newLen];
         System.arraycopy(this.staticAttributes, 0, newbuf, 0, this.len);

         for(int i = this.len; i < newLen; ++i) {
            this.staticAttributes[i] = new C14nXmlOutput.StaticAttribute();
         }

         this.staticAttributes = newbuf;
      }

      this.staticAttributes[this.len++].set(name, value);
   }

   public void attribute(int prefix, String localName, String value) throws IOException {
      this.otherAttributes.add(new C14nXmlOutput.DynamicAttribute(prefix, localName, value));
   }

   public void endStartTag() throws IOException {
      int i;
      if (this.otherAttributes.isEmpty()) {
         if (this.len != 0) {
            if (!this.namedAttributesAreOrdered) {
               Arrays.sort((Object[])this.staticAttributes, 0, this.len);
            }

            for(i = 0; i < this.len; ++i) {
               this.staticAttributes[i].write();
            }

            this.len = 0;
         }
      } else {
         for(i = 0; i < this.len; ++i) {
            this.otherAttributes.add(this.staticAttributes[i].toDynamicAttribute());
         }

         this.len = 0;
         Collections.sort(this.otherAttributes);
         i = this.otherAttributes.size();

         for(int i = 0; i < i; ++i) {
            C14nXmlOutput.DynamicAttribute a = (C14nXmlOutput.DynamicAttribute)this.otherAttributes.get(i);
            super.attribute(a.prefix, a.localName, a.value);
         }

         this.otherAttributes.clear();
      }

      super.endStartTag();
   }

   protected void writeNsDecls(int base) throws IOException {
      int count = this.nsContext.getCurrent().count();
      if (count != 0) {
         if (count > this.nsBuf.length) {
            this.nsBuf = new int[count];
         }

         int i;
         for(i = count - 1; i >= 0; --i) {
            this.nsBuf[i] = base + i;
         }

         for(i = 0; i < count; ++i) {
            for(int j = i + 1; j < count; ++j) {
               String p = this.nsContext.getPrefix(this.nsBuf[i]);
               String q = this.nsContext.getPrefix(this.nsBuf[j]);
               if (p.compareTo(q) > 0) {
                  int t = this.nsBuf[j];
                  this.nsBuf[j] = this.nsBuf[i];
                  this.nsBuf[i] = t;
               }
            }
         }

         for(i = 0; i < count; ++i) {
            this.writeNsDecl(this.nsBuf[i]);
         }

      }
   }

   final class DynamicAttribute implements Comparable<C14nXmlOutput.DynamicAttribute> {
      final int prefix;
      final String localName;
      final String value;

      public DynamicAttribute(int prefix, String localName, String value) {
         this.prefix = prefix;
         this.localName = localName;
         this.value = value;
      }

      private String getURI() {
         return this.prefix == -1 ? "" : C14nXmlOutput.this.nsContext.getNamespaceURI(this.prefix);
      }

      public int compareTo(C14nXmlOutput.DynamicAttribute that) {
         int r = this.getURI().compareTo(that.getURI());
         return r != 0 ? r : this.localName.compareTo(that.localName);
      }
   }

   final class StaticAttribute implements Comparable<C14nXmlOutput.StaticAttribute> {
      Name name;
      String value;

      public void set(Name name, String value) {
         this.name = name;
         this.value = value;
      }

      void write() throws IOException {
         C14nXmlOutput.super.attribute(this.name, this.value);
      }

      C14nXmlOutput.DynamicAttribute toDynamicAttribute() {
         int nsUriIndex = this.name.nsUriIndex;
         int prefix;
         if (nsUriIndex == -1) {
            prefix = -1;
         } else {
            prefix = C14nXmlOutput.this.nsUriIndex2prefixIndex[nsUriIndex];
         }

         return C14nXmlOutput.this.new DynamicAttribute(prefix, this.name.localName, this.value);
      }

      public int compareTo(C14nXmlOutput.StaticAttribute that) {
         return this.name.compareTo(that.name);
      }
   }
}
