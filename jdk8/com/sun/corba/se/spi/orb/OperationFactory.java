package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.StringTokenizer;
import sun.corba.SharedSecrets;

public abstract class OperationFactory {
   private static Operation suffixActionImpl = new OperationFactory.SuffixAction();
   private static Operation valueActionImpl = new OperationFactory.ValueAction();
   private static Operation identityActionImpl = new OperationFactory.IdentityAction();
   private static Operation booleanActionImpl = new OperationFactory.BooleanAction();
   private static Operation integerActionImpl = new OperationFactory.IntegerAction();
   private static Operation stringActionImpl = new OperationFactory.StringAction();
   private static Operation classActionImpl = new OperationFactory.ClassAction();
   private static Operation setFlagActionImpl = new OperationFactory.SetFlagAction();
   private static Operation URLActionImpl = new OperationFactory.URLAction();
   private static Operation convertIntegerToShortImpl = new OperationFactory.ConvertIntegerToShort();

   private OperationFactory() {
   }

   private static String getString(Object var0) {
      if (var0 instanceof String) {
         return (String)var0;
      } else {
         throw new Error("String expected");
      }
   }

   private static Object[] getObjectArray(Object var0) {
      if (var0 instanceof Object[]) {
         return (Object[])((Object[])var0);
      } else {
         throw new Error("Object[] expected");
      }
   }

   private static StringPair getStringPair(Object var0) {
      if (var0 instanceof StringPair) {
         return (StringPair)var0;
      } else {
         throw new Error("StringPair expected");
      }
   }

   public static Operation maskErrorAction(Operation var0) {
      return new OperationFactory.MaskErrorAction(var0);
   }

   public static Operation indexAction(int var0) {
      return new OperationFactory.IndexAction(var0);
   }

   public static Operation identityAction() {
      return identityActionImpl;
   }

   public static Operation suffixAction() {
      return suffixActionImpl;
   }

   public static Operation valueAction() {
      return valueActionImpl;
   }

   public static Operation booleanAction() {
      return booleanActionImpl;
   }

   public static Operation integerAction() {
      return integerActionImpl;
   }

   public static Operation stringAction() {
      return stringActionImpl;
   }

   public static Operation classAction() {
      return classActionImpl;
   }

   public static Operation setFlagAction() {
      return setFlagActionImpl;
   }

   public static Operation URLAction() {
      return URLActionImpl;
   }

   public static Operation integerRangeAction(int var0, int var1) {
      return new OperationFactory.IntegerRangeAction(var0, var1);
   }

   public static Operation listAction(String var0, Operation var1) {
      return new OperationFactory.ListAction(var0, var1);
   }

   public static Operation sequenceAction(String var0, Operation[] var1) {
      return new OperationFactory.SequenceAction(var0, var1);
   }

   public static Operation compose(Operation var0, Operation var1) {
      return new OperationFactory.ComposeAction(var0, var1);
   }

   public static Operation mapAction(Operation var0) {
      return new OperationFactory.MapAction(var0);
   }

   public static Operation mapSequenceAction(Operation[] var0) {
      return new OperationFactory.MapSequenceAction(var0);
   }

   public static Operation convertIntegerToShort() {
      return convertIntegerToShortImpl;
   }

   private static class ConvertIntegerToShort extends OperationFactory.OperationBase {
      private ConvertIntegerToShort() {
         super(null);
      }

      public Object operate(Object var1) {
         Integer var2 = (Integer)var1;
         return new Short(var2.shortValue());
      }

      public String toString() {
         return "ConvertIntegerToShort";
      }

      // $FF: synthetic method
      ConvertIntegerToShort(Object var1) {
         this();
      }
   }

   private static class MapSequenceAction extends OperationFactory.OperationBase {
      private Operation[] op;

      public MapSequenceAction(Operation[] var1) {
         super(null);
         this.op = var1;
      }

      public Object operate(Object var1) {
         Object[] var2 = (Object[])((Object[])var1);
         Object[] var3 = new Object[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = this.op[var4].operate(var2[var4]);
         }

         return var3;
      }

      public String toString() {
         return "mapSequenceAction(" + Arrays.toString((Object[])this.op) + ")";
      }
   }

   private static class MapAction extends OperationFactory.OperationBase {
      Operation op;

      MapAction(Operation var1) {
         super(null);
         this.op = var1;
      }

      public Object operate(Object var1) {
         Object[] var2 = (Object[])((Object[])var1);
         Object[] var3 = new Object[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = this.op.operate(var2[var4]);
         }

         return var3;
      }

      public String toString() {
         return "mapAction(" + this.op + ")";
      }
   }

   private static class ComposeAction extends OperationFactory.OperationBase {
      private Operation op1;
      private Operation op2;

      ComposeAction(Operation var1, Operation var2) {
         super(null);
         this.op1 = var1;
         this.op2 = var2;
      }

      public Object operate(Object var1) {
         return this.op2.operate(this.op1.operate(var1));
      }

      public String toString() {
         return "composition(" + this.op1 + "," + this.op2 + ")";
      }
   }

   private static class SequenceAction extends OperationFactory.OperationBase {
      private String sep;
      private Operation[] actions;

      SequenceAction(String var1, Operation[] var2) {
         super(null);
         this.sep = var1;
         this.actions = var2;
      }

      public Object operate(Object var1) {
         StringTokenizer var2 = new StringTokenizer(OperationFactory.getString(var1), this.sep);
         int var3 = var2.countTokens();
         if (var3 != this.actions.length) {
            throw new Error("Number of tokens and number of actions do not match");
         } else {
            int var4 = 0;

            Object[] var5;
            Operation var6;
            String var7;
            for(var5 = new Object[var3]; var2.hasMoreTokens(); var5[var4++] = var6.operate(var7)) {
               var6 = this.actions[var4];
               var7 = var2.nextToken();
            }

            return var5;
         }
      }

      public String toString() {
         return "sequenceAction(separator=\"" + this.sep + "\",actions=" + Arrays.toString((Object[])this.actions) + ")";
      }
   }

   private static class ListAction extends OperationFactory.OperationBase {
      private String sep;
      private Operation act;

      ListAction(String var1, Operation var2) {
         super(null);
         this.sep = var1;
         this.act = var2;
      }

      public Object operate(Object var1) {
         StringTokenizer var2 = new StringTokenizer(OperationFactory.getString(var1), this.sep);
         int var3 = var2.countTokens();
         Object var4 = null;

         Object var7;
         for(int var5 = 0; var2.hasMoreTokens(); Array.set(var4, var5++, var7)) {
            String var6 = var2.nextToken();
            var7 = this.act.operate(var6);
            if (var4 == null) {
               var4 = Array.newInstance(var7.getClass(), var3);
            }
         }

         return var4;
      }

      public String toString() {
         return "listAction(separator=\"" + this.sep + "\",action=" + this.act + ")";
      }
   }

   private static class IntegerRangeAction extends OperationFactory.OperationBase {
      private int min;
      private int max;

      IntegerRangeAction(int var1, int var2) {
         super(null);
         this.min = var1;
         this.max = var2;
      }

      public Object operate(Object var1) {
         int var2 = Integer.parseInt(OperationFactory.getString(var1));
         if (var2 >= this.min && var2 <= this.max) {
            return new Integer(var2);
         } else {
            throw new IllegalArgumentException("Property value " + var2 + " is not in the range " + this.min + " to " + this.max);
         }
      }

      public String toString() {
         return "integerRangeAction(" + this.min + "," + this.max + ")";
      }
   }

   private static class URLAction extends OperationFactory.OperationBase {
      private URLAction() {
         super(null);
      }

      public Object operate(Object var1) {
         String var2 = (String)var1;

         try {
            return new URL(var2);
         } catch (MalformedURLException var5) {
            ORBUtilSystemException var4 = ORBUtilSystemException.get("orb.lifecycle");
            throw var4.badUrl((Throwable)var5, var2);
         }
      }

      public String toString() {
         return "URLAction";
      }

      // $FF: synthetic method
      URLAction(Object var1) {
         this();
      }
   }

   private static class SetFlagAction extends OperationFactory.OperationBase {
      private SetFlagAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return Boolean.TRUE;
      }

      public String toString() {
         return "setFlagAction";
      }

      // $FF: synthetic method
      SetFlagAction(Object var1) {
         this();
      }
   }

   private static class ClassAction extends OperationFactory.OperationBase {
      private ClassAction() {
         super(null);
      }

      public Object operate(Object var1) {
         String var2 = OperationFactory.getString(var1);

         try {
            Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
            return var3;
         } catch (Exception var5) {
            ORBUtilSystemException var4 = ORBUtilSystemException.get("orb.lifecycle");
            throw var4.couldNotLoadClass((Throwable)var5, var2);
         }
      }

      public String toString() {
         return "classAction";
      }

      // $FF: synthetic method
      ClassAction(Object var1) {
         this();
      }
   }

   private static class StringAction extends OperationFactory.OperationBase {
      private StringAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return var1;
      }

      public String toString() {
         return "stringAction";
      }

      // $FF: synthetic method
      StringAction(Object var1) {
         this();
      }
   }

   private static class IntegerAction extends OperationFactory.OperationBase {
      private IntegerAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return new Integer(OperationFactory.getString(var1));
      }

      public String toString() {
         return "integerAction";
      }

      // $FF: synthetic method
      IntegerAction(Object var1) {
         this();
      }
   }

   private static class BooleanAction extends OperationFactory.OperationBase {
      private BooleanAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return new Boolean(OperationFactory.getString(var1));
      }

      public String toString() {
         return "booleanAction";
      }

      // $FF: synthetic method
      BooleanAction(Object var1) {
         this();
      }
   }

   private static class IdentityAction extends OperationFactory.OperationBase {
      private IdentityAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return var1;
      }

      public String toString() {
         return "identityAction";
      }

      // $FF: synthetic method
      IdentityAction(Object var1) {
         this();
      }
   }

   private static class ValueAction extends OperationFactory.OperationBase {
      private ValueAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return OperationFactory.getStringPair(var1).getSecond();
      }

      public String toString() {
         return "valueAction";
      }

      // $FF: synthetic method
      ValueAction(Object var1) {
         this();
      }
   }

   private static class SuffixAction extends OperationFactory.OperationBase {
      private SuffixAction() {
         super(null);
      }

      public Object operate(Object var1) {
         return OperationFactory.getStringPair(var1).getFirst();
      }

      public String toString() {
         return "suffixAction";
      }

      // $FF: synthetic method
      SuffixAction(Object var1) {
         this();
      }
   }

   private static class IndexAction extends OperationFactory.OperationBase {
      private int index;

      public IndexAction(int var1) {
         super(null);
         this.index = var1;
      }

      public Object operate(Object var1) {
         return OperationFactory.getObjectArray(var1)[this.index];
      }

      public String toString() {
         return "indexAction(" + this.index + ")";
      }
   }

   private static class MaskErrorAction extends OperationFactory.OperationBase {
      private Operation op;

      public MaskErrorAction(Operation var1) {
         super(null);
         this.op = var1;
      }

      public Object operate(Object var1) {
         try {
            return this.op.operate(var1);
         } catch (Exception var3) {
            return null;
         }
      }

      public String toString() {
         return "maskErrorAction(" + this.op + ")";
      }
   }

   private abstract static class OperationBase implements Operation {
      private OperationBase() {
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof OperationFactory.OperationBase)) {
            return false;
         } else {
            OperationFactory.OperationBase var2 = (OperationFactory.OperationBase)var1;
            return this.toString().equals(var2.toString());
         }
      }

      public int hashCode() {
         return this.toString().hashCode();
      }

      // $FF: synthetic method
      OperationBase(Object var1) {
         this();
      }
   }
}
