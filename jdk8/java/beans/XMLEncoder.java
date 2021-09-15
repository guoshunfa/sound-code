package java.beans;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMLEncoder extends Encoder implements AutoCloseable {
   private final CharsetEncoder encoder;
   private final String charset;
   private final boolean declaration;
   private OutputStreamWriter out;
   private Object owner;
   private int indentation;
   private boolean internal;
   private Map<Object, XMLEncoder.ValueData> valueToExpression;
   private Map<Object, List<Statement>> targetToStatementList;
   private boolean preambleWritten;
   private NameGenerator nameGenerator;

   public XMLEncoder(OutputStream var1) {
      this(var1, "UTF-8", true, 0);
   }

   public XMLEncoder(OutputStream var1, String var2, boolean var3, int var4) {
      this.indentation = 0;
      this.internal = false;
      this.preambleWritten = false;
      if (var1 == null) {
         throw new IllegalArgumentException("the output stream cannot be null");
      } else if (var4 < 0) {
         throw new IllegalArgumentException("the indentation must be >= 0");
      } else {
         Charset var5 = Charset.forName(var2);
         this.encoder = var5.newEncoder();
         this.charset = var2;
         this.declaration = var3;
         this.indentation = var4;
         this.out = new OutputStreamWriter(var1, var5.newEncoder());
         this.valueToExpression = new IdentityHashMap();
         this.targetToStatementList = new IdentityHashMap();
         this.nameGenerator = new NameGenerator();
      }
   }

   public void setOwner(Object var1) {
      this.owner = var1;
      this.writeExpression(new Expression(this, "getOwner", new Object[0]));
   }

   public Object getOwner() {
      return this.owner;
   }

   public void writeObject(Object var1) {
      if (this.internal) {
         super.writeObject(var1);
      } else {
         this.writeStatement(new Statement(this, "writeObject", new Object[]{var1}));
      }

   }

   private List<Statement> statementList(Object var1) {
      Object var2 = (List)this.targetToStatementList.get(var1);
      if (var2 == null) {
         var2 = new ArrayList();
         this.targetToStatementList.put(var1, var2);
      }

      return (List)var2;
   }

   private void mark(Object var1, boolean var2) {
      if (var1 != null && var1 != this) {
         XMLEncoder.ValueData var3 = this.getValueData(var1);
         Expression var4 = var3.exp;
         if (var1.getClass() != String.class || var4 != null) {
            if (var2) {
               ++var3.refs;
            }

            if (!var3.marked) {
               var3.marked = true;
               Object var5 = var4.getTarget();
               this.mark(var4);
               if (!(var5 instanceof Class)) {
                  this.statementList(var5).add(var4);
                  ++var3.refs;
               }

            }
         }
      }
   }

   private void mark(Statement var1) {
      Object[] var2 = var1.getArguments();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Object var4 = var2[var3];
         this.mark(var4, true);
      }

      this.mark(var1.getTarget(), var1 instanceof Expression);
   }

   public void writeStatement(Statement var1) {
      boolean var2 = this.internal;
      this.internal = true;

      try {
         super.writeStatement(var1);
         this.mark(var1);
         Object var3 = var1.getTarget();
         if (var3 instanceof Field) {
            String var4 = var1.getMethodName();
            Object[] var5 = var1.getArguments();
            if (var4 != null && var5 != null) {
               if (var4.equals("get") && var5.length == 1) {
                  var3 = var5[0];
               } else if (var4.equals("set") && var5.length == 2) {
                  var3 = var5[0];
               }
            }
         }

         this.statementList(var3).add(var1);
      } catch (Exception var6) {
         this.getExceptionListener().exceptionThrown(new Exception("XMLEncoder: discarding statement " + var1, var6));
      }

      this.internal = var2;
   }

   public void writeExpression(Expression var1) {
      boolean var2 = this.internal;
      this.internal = true;
      Object var3 = this.getValue(var1);
      if (this.get(var3) == null || var3 instanceof String && !var2) {
         this.getValueData(var3).exp = var1;
         super.writeExpression(var1);
      }

      this.internal = var2;
   }

   public void flush() {
      if (!this.preambleWritten) {
         if (this.declaration) {
            this.writeln("<?xml version=" + this.quote("1.0") + " encoding=" + this.quote(this.charset) + "?>");
         }

         this.writeln("<java version=" + this.quote(System.getProperty("java.version")) + " class=" + this.quote(XMLDecoder.class.getName()) + ">");
         this.preambleWritten = true;
      }

      ++this.indentation;
      List var1 = this.statementList(this);

      Statement var2;
      while(!var1.isEmpty()) {
         var2 = (Statement)var1.remove(0);
         if ("writeObject".equals(var2.getMethodName())) {
            this.outputValue(var2.getArguments()[0], this, true);
         } else {
            this.outputStatement(var2, this, false);
         }
      }

      --this.indentation;

      for(var2 = this.getMissedStatement(); var2 != null; var2 = this.getMissedStatement()) {
         this.outputStatement(var2, this, false);
      }

      try {
         this.out.flush();
      } catch (IOException var4) {
         this.getExceptionListener().exceptionThrown(var4);
      }

      this.clear();
   }

   void clear() {
      super.clear();
      this.nameGenerator.clear();
      this.valueToExpression.clear();
      this.targetToStatementList.clear();
   }

   Statement getMissedStatement() {
      Iterator var1 = this.targetToStatementList.values().iterator();

      while(var1.hasNext()) {
         List var2 = (List)var1.next();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            if (Statement.class == ((Statement)var2.get(var3)).getClass()) {
               return (Statement)var2.remove(var3);
            }
         }
      }

      return null;
   }

   public void close() {
      this.flush();
      this.writeln("</java>");

      try {
         this.out.close();
      } catch (IOException var2) {
         this.getExceptionListener().exceptionThrown(var2);
      }

   }

   private String quote(String var1) {
      return "\"" + var1 + "\"";
   }

   private XMLEncoder.ValueData getValueData(Object var1) {
      XMLEncoder.ValueData var2 = (XMLEncoder.ValueData)this.valueToExpression.get(var1);
      if (var2 == null) {
         var2 = new XMLEncoder.ValueData();
         this.valueToExpression.put(var1, var2);
      }

      return var2;
   }

   private static boolean isValidCharCode(int var0) {
      return 32 <= var0 && var0 <= 55295 || 10 == var0 || 9 == var0 || 13 == var0 || 57344 <= var0 && var0 <= 65533 || 65536 <= var0 && var0 <= 1114111;
   }

   private void writeln(String var1) {
      try {
         StringBuilder var2 = new StringBuilder();

         for(int var3 = 0; var3 < this.indentation; ++var3) {
            var2.append(' ');
         }

         var2.append(var1);
         var2.append('\n');
         this.out.write(var2.toString());
      } catch (IOException var4) {
         this.getExceptionListener().exceptionThrown(var4);
      }

   }

   private void outputValue(Object var1, Object var2, boolean var3) {
      if (var1 == null) {
         this.writeln("<null/>");
      } else if (var1 instanceof Class) {
         this.writeln("<class>" + ((Class)var1).getName() + "</class>");
      } else {
         XMLEncoder.ValueData var4 = this.getValueData(var1);
         if (var4.exp != null) {
            Object var5 = var4.exp.getTarget();
            String var6 = var4.exp.getMethodName();
            if (var5 == null || var6 == null) {
               throw new NullPointerException((var5 == null ? "target" : "methodName") + " should not be null");
            }

            if (var3 && var5 instanceof Field && var6.equals("get")) {
               Field var10 = (Field)var5;
               this.writeln("<object class=" + this.quote(var10.getDeclaringClass().getName()) + " field=" + this.quote(var10.getName()) + "/>");
               return;
            }

            Class var7 = primitiveTypeFor(var1.getClass());
            if (var7 != null && var5 == var1.getClass() && var6.equals("new")) {
               String var8 = var7.getName();
               if (var7 == Character.TYPE) {
                  char var9 = (Character)var1;
                  if (!isValidCharCode(var9)) {
                     this.writeln(createString(var9));
                     return;
                  }

                  var1 = quoteCharCode(var9);
                  if (var1 == null) {
                     var1 = var9;
                  }
               }

               this.writeln("<" + var8 + ">" + var1 + "</" + var8 + ">");
               return;
            }
         } else if (var1 instanceof String) {
            this.writeln(this.createString((String)var1));
            return;
         }

         if (var4.name != null) {
            if (var3) {
               this.writeln("<object idref=" + this.quote(var4.name) + "/>");
            } else {
               this.outputXML("void", " idref=" + this.quote(var4.name), var1);
            }
         } else if (var4.exp != null) {
            this.outputStatement(var4.exp, var2, var3);
         }

      }
   }

   private static String quoteCharCode(int var0) {
      switch(var0) {
      case 13:
         return "&#13;";
      case 34:
         return "&quot;";
      case 38:
         return "&amp;";
      case 39:
         return "&apos;";
      case 60:
         return "&lt;";
      case 62:
         return "&gt;";
      default:
         return null;
      }
   }

   private static String createString(int var0) {
      return "<char code=\"#" + Integer.toString(var0, 16) + "\"/>";
   }

   private String createString(String var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("<string>");
      int var3 = 0;

      while(true) {
         while(var3 < var1.length()) {
            int var4 = var1.codePointAt(var3);
            int var5 = Character.charCount(var4);
            if (isValidCharCode(var4) && this.encoder.canEncode((CharSequence)var1.substring(var3, var3 + var5))) {
               String var6 = quoteCharCode(var4);
               if (var6 != null) {
                  var2.append(var6);
               } else {
                  var2.appendCodePoint(var4);
               }

               var3 += var5;
            } else {
               var2.append(createString(var1.charAt(var3)));
               ++var3;
            }
         }

         var2.append("</string>");
         return var2.toString();
      }
   }

   private void outputStatement(Statement var1, Object var2, boolean var3) {
      Object var4 = var1.getTarget();
      String var5 = var1.getMethodName();
      if (var4 != null && var5 != null) {
         Object[] var6 = var1.getArguments();
         boolean var7 = var1.getClass() == Expression.class;
         Object var8 = var7 ? this.getValue((Expression)var1) : null;
         String var9 = var7 && var3 ? "object" : "void";
         String var10 = "";
         XMLEncoder.ValueData var11 = this.getValueData(var8);
         if (var4 != var2) {
            if (var4 == Array.class && var5.equals("newInstance")) {
               var9 = "array";
               var10 = var10 + " class=" + this.quote(((Class)var6[0]).getName());
               var10 = var10 + " length=" + this.quote(var6[1].toString());
               var6 = new Object[0];
            } else {
               if (var4.getClass() != Class.class) {
                  var11.refs = 2;
                  if (var11.name == null) {
                     ++this.getValueData(var4).refs;
                     List var12 = this.statementList(var4);
                     if (!var12.contains(var1)) {
                        var12.add(var1);
                     }

                     this.outputValue(var4, var2, false);
                  }

                  if (var7) {
                     this.outputValue(var8, var2, var3);
                  }

                  return;
               }

               var10 = var10 + " class=" + this.quote(((Class)var4).getName());
            }
         }

         if (var7 && var11.refs > 1) {
            String var13 = this.nameGenerator.instanceName(var8);
            var11.name = var13;
            var10 = var10 + " id=" + this.quote(var13);
         }

         if ((var7 || !var5.equals("set") || var6.length != 2 || !(var6[0] instanceof Integer)) && (!var7 || !var5.equals("get") || var6.length != 1 || !(var6[0] instanceof Integer))) {
            if (!var7 && var5.startsWith("set") && var6.length == 1 || var7 && var5.startsWith("get") && var6.length == 0) {
               if (3 < var5.length()) {
                  var10 = var10 + " property=" + this.quote(Introspector.decapitalize(var5.substring(3)));
               }
            } else if (!var5.equals("new") && !var5.equals("newInstance")) {
               var10 = var10 + " method=" + this.quote(var5);
            }
         } else {
            var10 = var10 + " index=" + this.quote(var6[0].toString());
            var6 = var6.length == 1 ? new Object[0] : new Object[]{var6[1]};
         }

         this.outputXML(var9, var10, var8, var6);
      } else {
         throw new NullPointerException((var4 == null ? "target" : "methodName") + " should not be null");
      }
   }

   private void outputXML(String var1, String var2, Object var3, Object... var4) {
      List var5 = this.statementList(var3);
      if (var4.length == 0 && var5.size() == 0) {
         this.writeln("<" + var1 + var2 + "/>");
      } else {
         this.writeln("<" + var1 + var2 + ">");
         ++this.indentation;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            this.outputValue(var4[var6], (Object)null, true);
         }

         while(!var5.isEmpty()) {
            Statement var7 = (Statement)var5.remove(0);
            this.outputStatement(var7, var3, false);
         }

         --this.indentation;
         this.writeln("</" + var1 + ">");
      }
   }

   static Class primitiveTypeFor(Class var0) {
      if (var0 == Boolean.class) {
         return Boolean.TYPE;
      } else if (var0 == Byte.class) {
         return Byte.TYPE;
      } else if (var0 == Character.class) {
         return Character.TYPE;
      } else if (var0 == Short.class) {
         return Short.TYPE;
      } else if (var0 == Integer.class) {
         return Integer.TYPE;
      } else if (var0 == Long.class) {
         return Long.TYPE;
      } else if (var0 == Float.class) {
         return Float.TYPE;
      } else if (var0 == Double.class) {
         return Double.TYPE;
      } else {
         return var0 == Void.class ? Void.TYPE : null;
      }
   }

   private class ValueData {
      public int refs;
      public boolean marked;
      public String name;
      public Expression exp;

      private ValueData() {
         this.refs = 0;
         this.marked = false;
         this.name = null;
         this.exp = null;
      }

      // $FF: synthetic method
      ValueData(Object var2) {
         this();
      }
   }
}
