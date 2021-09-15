package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA_2_3.portable.InputStream;

public class ProxyInputStream extends InputStream {
   protected final org.omg.CORBA.portable.InputStream in;

   public ProxyInputStream(org.omg.CORBA.portable.InputStream var1) {
      this.in = var1;
   }

   public boolean read_boolean() {
      return this.in.read_boolean();
   }

   public char read_char() {
      return this.in.read_char();
   }

   public char read_wchar() {
      return this.in.read_wchar();
   }

   public byte read_octet() {
      return this.in.read_octet();
   }

   public short read_short() {
      return this.in.read_short();
   }

   public short read_ushort() {
      return this.in.read_ushort();
   }

   public int read_long() {
      return this.in.read_long();
   }

   public int read_ulong() {
      return this.in.read_ulong();
   }

   public long read_longlong() {
      return this.in.read_longlong();
   }

   public long read_ulonglong() {
      return this.in.read_ulonglong();
   }

   public float read_float() {
      return this.in.read_float();
   }

   public double read_double() {
      return this.in.read_double();
   }

   public String read_string() {
      return this.in.read_string();
   }

   public String read_wstring() {
      return this.in.read_wstring();
   }

   public void read_boolean_array(boolean[] var1, int var2, int var3) {
      this.in.read_boolean_array(var1, var2, var3);
   }

   public void read_char_array(char[] var1, int var2, int var3) {
      this.in.read_char_array(var1, var2, var3);
   }

   public void read_wchar_array(char[] var1, int var2, int var3) {
      this.in.read_wchar_array(var1, var2, var3);
   }

   public void read_octet_array(byte[] var1, int var2, int var3) {
      this.in.read_octet_array(var1, var2, var3);
   }

   public void read_short_array(short[] var1, int var2, int var3) {
      this.in.read_short_array(var1, var2, var3);
   }

   public void read_ushort_array(short[] var1, int var2, int var3) {
      this.in.read_ushort_array(var1, var2, var3);
   }

   public void read_long_array(int[] var1, int var2, int var3) {
      this.in.read_long_array(var1, var2, var3);
   }

   public void read_ulong_array(int[] var1, int var2, int var3) {
      this.in.read_ulong_array(var1, var2, var3);
   }

   public void read_longlong_array(long[] var1, int var2, int var3) {
      this.in.read_longlong_array(var1, var2, var3);
   }

   public void read_ulonglong_array(long[] var1, int var2, int var3) {
      this.in.read_ulonglong_array(var1, var2, var3);
   }

   public void read_float_array(float[] var1, int var2, int var3) {
      this.in.read_float_array(var1, var2, var3);
   }

   public void read_double_array(double[] var1, int var2, int var3) {
      this.in.read_double_array(var1, var2, var3);
   }

   public Object read_Object() {
      return this.in.read_Object();
   }

   public TypeCode read_TypeCode() {
      return this.in.read_TypeCode();
   }

   public Any read_any() {
      return this.in.read_any();
   }

   /** @deprecated */
   @Deprecated
   public Principal read_Principal() {
      return this.in.read_Principal();
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public BigDecimal read_fixed() {
      return this.in.read_fixed();
   }

   public Context read_Context() {
      return this.in.read_Context();
   }

   public Object read_Object(Class var1) {
      return this.in.read_Object(var1);
   }

   public ORB orb() {
      return this.in.orb();
   }

   public Serializable read_value() {
      return this.narrow().read_value();
   }

   public Serializable read_value(Class var1) {
      return this.narrow().read_value(var1);
   }

   public Serializable read_value(BoxedValueHelper var1) {
      return this.narrow().read_value(var1);
   }

   public Serializable read_value(String var1) {
      return this.narrow().read_value(var1);
   }

   public Serializable read_value(Serializable var1) {
      return this.narrow().read_value(var1);
   }

   public java.lang.Object read_abstract_interface() {
      return this.narrow().read_abstract_interface();
   }

   public java.lang.Object read_abstract_interface(Class var1) {
      return this.narrow().read_abstract_interface(var1);
   }

   protected InputStream narrow() {
      if (this.in instanceof InputStream) {
         return (InputStream)this.in;
      } else {
         throw new NO_IMPLEMENT();
      }
   }

   public org.omg.CORBA.portable.InputStream getProxiedInputStream() {
      return this.in;
   }
}
