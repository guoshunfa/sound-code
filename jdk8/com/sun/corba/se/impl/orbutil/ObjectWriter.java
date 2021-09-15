package com.sun.corba.se.impl.orbutil;

import java.util.Arrays;

public abstract class ObjectWriter {
   protected StringBuffer result = new StringBuffer();

   public static ObjectWriter make(boolean var0, int var1, int var2) {
      return (ObjectWriter)(var0 ? new ObjectWriter.IndentingObjectWriter(var1, var2) : new ObjectWriter.SimpleObjectWriter());
   }

   public abstract void startObject(Object var1);

   public abstract void startElement();

   public abstract void endElement();

   public abstract void endObject(String var1);

   public abstract void endObject();

   public String toString() {
      return this.result.toString();
   }

   public void append(boolean var1) {
      this.result.append(var1);
   }

   public void append(char var1) {
      this.result.append(var1);
   }

   public void append(short var1) {
      this.result.append((int)var1);
   }

   public void append(int var1) {
      this.result.append(var1);
   }

   public void append(long var1) {
      this.result.append(var1);
   }

   public void append(float var1) {
      this.result.append(var1);
   }

   public void append(double var1) {
      this.result.append(var1);
   }

   public void append(String var1) {
      this.result.append(var1);
   }

   protected ObjectWriter() {
   }

   protected void appendObjectHeader(Object var1) {
      this.result.append(var1.getClass().getName());
      this.result.append("<");
      this.result.append(System.identityHashCode(var1));
      this.result.append(">");
      Class var2 = var1.getClass().getComponentType();
      if (var2 != null) {
         this.result.append("[");
         if (var2 == Boolean.TYPE) {
            boolean[] var3 = (boolean[])((boolean[])var1);
            this.result.append(var3.length);
            this.result.append("]");
         } else if (var2 == Byte.TYPE) {
            byte[] var7 = (byte[])((byte[])var1);
            this.result.append(var7.length);
            this.result.append("]");
         } else if (var2 == Short.TYPE) {
            short[] var8 = (short[])((short[])var1);
            this.result.append(var8.length);
            this.result.append("]");
         } else if (var2 == Integer.TYPE) {
            int[] var9 = (int[])((int[])var1);
            this.result.append(var9.length);
            this.result.append("]");
         } else if (var2 == Long.TYPE) {
            long[] var10 = (long[])((long[])var1);
            this.result.append(var10.length);
            this.result.append("]");
         } else if (var2 == Character.TYPE) {
            char[] var11 = (char[])((char[])var1);
            this.result.append(var11.length);
            this.result.append("]");
         } else if (var2 == Float.TYPE) {
            float[] var4 = (float[])((float[])var1);
            this.result.append(var4.length);
            this.result.append("]");
         } else if (var2 == Double.TYPE) {
            double[] var5 = (double[])((double[])var1);
            this.result.append(var5.length);
            this.result.append("]");
         } else {
            Object[] var6 = (Object[])((Object[])var1);
            this.result.append(var6.length);
            this.result.append("]");
         }
      }

      this.result.append("(");
   }

   private static class SimpleObjectWriter extends ObjectWriter {
      private SimpleObjectWriter() {
      }

      public void startObject(Object var1) {
         this.appendObjectHeader(var1);
         this.result.append(" ");
      }

      public void startElement() {
         this.result.append(" ");
      }

      public void endObject(String var1) {
         this.result.append(var1);
         this.result.append(")");
      }

      public void endElement() {
      }

      public void endObject() {
         this.result.append(")");
      }

      // $FF: synthetic method
      SimpleObjectWriter(Object var1) {
         this();
      }
   }

   private static class IndentingObjectWriter extends ObjectWriter {
      private int level;
      private int increment;

      public IndentingObjectWriter(int var1, int var2) {
         this.level = var1;
         this.increment = var2;
         this.startLine();
      }

      private void startLine() {
         char[] var1 = new char[this.level * this.increment];
         Arrays.fill(var1, ' ');
         this.result.append(var1);
      }

      public void startObject(Object var1) {
         this.appendObjectHeader(var1);
         ++this.level;
      }

      public void startElement() {
         this.result.append("\n");
         this.startLine();
      }

      public void endElement() {
      }

      public void endObject(String var1) {
         --this.level;
         this.result.append(var1);
         this.result.append(")");
      }

      public void endObject() {
         --this.level;
         this.result.append("\n");
         this.startLine();
         this.result.append(")");
      }
   }
}
