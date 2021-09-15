package com.sun.xml.internal.txw2;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;

public interface DatatypeWriter<DT> {
   List<DatatypeWriter<?>> BUILTIN = Collections.unmodifiableList(new AbstractList() {
      private DatatypeWriter<?>[] BUILTIN_ARRAY = new DatatypeWriter[]{new DatatypeWriter<String>() {
         public Class<String> getType() {
            return String.class;
         }

         public void print(String s, NamespaceResolver resolver, StringBuilder buf) {
            buf.append(s);
         }
      }, new DatatypeWriter<Integer>() {
         public Class<Integer> getType() {
            return Integer.class;
         }

         public void print(Integer i, NamespaceResolver resolver, StringBuilder buf) {
            buf.append((Object)i);
         }
      }, new DatatypeWriter<Float>() {
         public Class<Float> getType() {
            return Float.class;
         }

         public void print(Float f, NamespaceResolver resolver, StringBuilder buf) {
            buf.append((Object)f);
         }
      }, new DatatypeWriter<Double>() {
         public Class<Double> getType() {
            return Double.class;
         }

         public void print(Double d, NamespaceResolver resolver, StringBuilder buf) {
            buf.append((Object)d);
         }
      }, new DatatypeWriter<QName>() {
         public Class<QName> getType() {
            return QName.class;
         }

         public void print(QName qn, NamespaceResolver resolver, StringBuilder buf) {
            String p = resolver.getPrefix(qn.getNamespaceURI());
            if (p.length() != 0) {
               buf.append(p).append(':');
            }

            buf.append(qn.getLocalPart());
         }
      }};

      public DatatypeWriter<?> get(int n) {
         return this.BUILTIN_ARRAY[n];
      }

      public int size() {
         return this.BUILTIN_ARRAY.length;
      }
   });

   Class<DT> getType();

   void print(DT var1, NamespaceResolver var2, StringBuilder var3);
}
