package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA_2_3.portable.InputStream;

public class WrapperInputStream extends InputStream implements TypeCodeReader {
   private CDRInputStream stream;
   private Map typeMap = null;
   private int startPos = 0;

   public WrapperInputStream(CDRInputStream var1) {
      this.stream = var1;
      this.startPos = this.stream.getPosition();
   }

   public int read() throws IOException {
      return this.stream.read();
   }

   public int read(byte[] var1) throws IOException {
      return this.stream.read(var1);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.stream.read(var1, var2, var3);
   }

   public long skip(long var1) throws IOException {
      return this.stream.skip(var1);
   }

   public int available() throws IOException {
      return this.stream.available();
   }

   public void close() throws IOException {
      this.stream.close();
   }

   public void mark(int var1) {
      this.stream.mark(var1);
   }

   public void reset() {
      this.stream.reset();
   }

   public boolean markSupported() {
      return this.stream.markSupported();
   }

   public int getPosition() {
      return this.stream.getPosition();
   }

   public void consumeEndian() {
      this.stream.consumeEndian();
   }

   public boolean read_boolean() {
      return this.stream.read_boolean();
   }

   public char read_char() {
      return this.stream.read_char();
   }

   public char read_wchar() {
      return this.stream.read_wchar();
   }

   public byte read_octet() {
      return this.stream.read_octet();
   }

   public short read_short() {
      return this.stream.read_short();
   }

   public short read_ushort() {
      return this.stream.read_ushort();
   }

   public int read_long() {
      return this.stream.read_long();
   }

   public int read_ulong() {
      return this.stream.read_ulong();
   }

   public long read_longlong() {
      return this.stream.read_longlong();
   }

   public long read_ulonglong() {
      return this.stream.read_ulonglong();
   }

   public float read_float() {
      return this.stream.read_float();
   }

   public double read_double() {
      return this.stream.read_double();
   }

   public String read_string() {
      return this.stream.read_string();
   }

   public String read_wstring() {
      return this.stream.read_wstring();
   }

   public void read_boolean_array(boolean[] var1, int var2, int var3) {
      this.stream.read_boolean_array(var1, var2, var3);
   }

   public void read_char_array(char[] var1, int var2, int var3) {
      this.stream.read_char_array(var1, var2, var3);
   }

   public void read_wchar_array(char[] var1, int var2, int var3) {
      this.stream.read_wchar_array(var1, var2, var3);
   }

   public void read_octet_array(byte[] var1, int var2, int var3) {
      this.stream.read_octet_array(var1, var2, var3);
   }

   public void read_short_array(short[] var1, int var2, int var3) {
      this.stream.read_short_array(var1, var2, var3);
   }

   public void read_ushort_array(short[] var1, int var2, int var3) {
      this.stream.read_ushort_array(var1, var2, var3);
   }

   public void read_long_array(int[] var1, int var2, int var3) {
      this.stream.read_long_array(var1, var2, var3);
   }

   public void read_ulong_array(int[] var1, int var2, int var3) {
      this.stream.read_ulong_array(var1, var2, var3);
   }

   public void read_longlong_array(long[] var1, int var2, int var3) {
      this.stream.read_longlong_array(var1, var2, var3);
   }

   public void read_ulonglong_array(long[] var1, int var2, int var3) {
      this.stream.read_ulonglong_array(var1, var2, var3);
   }

   public void read_float_array(float[] var1, int var2, int var3) {
      this.stream.read_float_array(var1, var2, var3);
   }

   public void read_double_array(double[] var1, int var2, int var3) {
      this.stream.read_double_array(var1, var2, var3);
   }

   public Object read_Object() {
      return this.stream.read_Object();
   }

   public Serializable read_value() {
      return this.stream.read_value();
   }

   public TypeCode read_TypeCode() {
      return this.stream.read_TypeCode();
   }

   public Any read_any() {
      return this.stream.read_any();
   }

   public Principal read_Principal() {
      return this.stream.read_Principal();
   }

   public BigDecimal read_fixed() {
      return this.stream.read_fixed();
   }

   public Context read_Context() {
      return this.stream.read_Context();
   }

   public ORB orb() {
      return this.stream.orb();
   }

   public void addTypeCodeAtPosition(TypeCodeImpl var1, int var2) {
      if (this.typeMap == null) {
         this.typeMap = new HashMap(16);
      }

      this.typeMap.put(new Integer(var2), var1);
   }

   public TypeCodeImpl getTypeCodeAtPosition(int var1) {
      return this.typeMap == null ? null : (TypeCodeImpl)this.typeMap.get(new Integer(var1));
   }

   public void setEnclosingInputStream(InputStream var1) {
   }

   public TypeCodeReader getTopLevelStream() {
      return this;
   }

   public int getTopLevelPosition() {
      return this.getPosition() - this.startPos;
   }

   public void performORBVersionSpecificInit() {
      this.stream.performORBVersionSpecificInit();
   }

   public void resetCodeSetConverters() {
      this.stream.resetCodeSetConverters();
   }

   public void printTypeMap() {
      System.out.println("typeMap = {");
      ArrayList var1 = new ArrayList(this.typeMap.keySet());
      Collections.sort(var1);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Integer var3 = (Integer)var2.next();
         TypeCodeImpl var4 = (TypeCodeImpl)this.typeMap.get(var3);
         System.out.println("  key = " + var3 + ", value = " + var4.description());
      }

      System.out.println("}");
   }
}
