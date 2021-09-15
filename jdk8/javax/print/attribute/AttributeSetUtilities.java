package javax.print.attribute;

import java.io.Serializable;

public final class AttributeSetUtilities {
   private AttributeSetUtilities() {
   }

   public static AttributeSet unmodifiableView(AttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.UnmodifiableAttributeSet(var0);
      }
   }

   public static DocAttributeSet unmodifiableView(DocAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.UnmodifiableDocAttributeSet(var0);
      }
   }

   public static PrintRequestAttributeSet unmodifiableView(PrintRequestAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.UnmodifiablePrintRequestAttributeSet(var0);
      }
   }

   public static PrintJobAttributeSet unmodifiableView(PrintJobAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.UnmodifiablePrintJobAttributeSet(var0);
      }
   }

   public static PrintServiceAttributeSet unmodifiableView(PrintServiceAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.UnmodifiablePrintServiceAttributeSet(var0);
      }
   }

   public static AttributeSet synchronizedView(AttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.SynchronizedAttributeSet(var0);
      }
   }

   public static DocAttributeSet synchronizedView(DocAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.SynchronizedDocAttributeSet(var0);
      }
   }

   public static PrintRequestAttributeSet synchronizedView(PrintRequestAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.SynchronizedPrintRequestAttributeSet(var0);
      }
   }

   public static PrintJobAttributeSet synchronizedView(PrintJobAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.SynchronizedPrintJobAttributeSet(var0);
      }
   }

   public static PrintServiceAttributeSet synchronizedView(PrintServiceAttributeSet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new AttributeSetUtilities.SynchronizedPrintServiceAttributeSet(var0);
      }
   }

   public static Class<?> verifyAttributeCategory(Object var0, Class<?> var1) {
      Class var2 = (Class)var0;
      if (var1.isAssignableFrom(var2)) {
         return var2;
      } else {
         throw new ClassCastException();
      }
   }

   public static Attribute verifyAttributeValue(Object var0, Class<?> var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1.isInstance(var0)) {
         return (Attribute)var0;
      } else {
         throw new ClassCastException();
      }
   }

   public static void verifyCategoryForValue(Class<?> var0, Attribute var1) {
      if (!var0.equals(var1.getCategory())) {
         throw new IllegalArgumentException();
      }
   }

   private static class SynchronizedPrintServiceAttributeSet extends AttributeSetUtilities.SynchronizedAttributeSet implements PrintServiceAttributeSet, Serializable {
      public SynchronizedPrintServiceAttributeSet(PrintServiceAttributeSet var1) {
         super(var1);
      }
   }

   private static class SynchronizedPrintJobAttributeSet extends AttributeSetUtilities.SynchronizedAttributeSet implements PrintJobAttributeSet, Serializable {
      public SynchronizedPrintJobAttributeSet(PrintJobAttributeSet var1) {
         super(var1);
      }
   }

   private static class SynchronizedPrintRequestAttributeSet extends AttributeSetUtilities.SynchronizedAttributeSet implements PrintRequestAttributeSet, Serializable {
      public SynchronizedPrintRequestAttributeSet(PrintRequestAttributeSet var1) {
         super(var1);
      }
   }

   private static class SynchronizedDocAttributeSet extends AttributeSetUtilities.SynchronizedAttributeSet implements DocAttributeSet, Serializable {
      public SynchronizedDocAttributeSet(DocAttributeSet var1) {
         super(var1);
      }
   }

   private static class SynchronizedAttributeSet implements AttributeSet, Serializable {
      private AttributeSet attrset;

      public SynchronizedAttributeSet(AttributeSet var1) {
         this.attrset = var1;
      }

      public synchronized Attribute get(Class<?> var1) {
         return this.attrset.get(var1);
      }

      public synchronized boolean add(Attribute var1) {
         return this.attrset.add(var1);
      }

      public synchronized boolean remove(Class<?> var1) {
         return this.attrset.remove(var1);
      }

      public synchronized boolean remove(Attribute var1) {
         return this.attrset.remove(var1);
      }

      public synchronized boolean containsKey(Class<?> var1) {
         return this.attrset.containsKey(var1);
      }

      public synchronized boolean containsValue(Attribute var1) {
         return this.attrset.containsValue(var1);
      }

      public synchronized boolean addAll(AttributeSet var1) {
         return this.attrset.addAll(var1);
      }

      public synchronized int size() {
         return this.attrset.size();
      }

      public synchronized Attribute[] toArray() {
         return this.attrset.toArray();
      }

      public synchronized void clear() {
         this.attrset.clear();
      }

      public synchronized boolean isEmpty() {
         return this.attrset.isEmpty();
      }

      public synchronized boolean equals(Object var1) {
         return this.attrset.equals(var1);
      }

      public synchronized int hashCode() {
         return this.attrset.hashCode();
      }
   }

   private static class UnmodifiablePrintServiceAttributeSet extends AttributeSetUtilities.UnmodifiableAttributeSet implements PrintServiceAttributeSet, Serializable {
      public UnmodifiablePrintServiceAttributeSet(PrintServiceAttributeSet var1) {
         super(var1);
      }
   }

   private static class UnmodifiablePrintJobAttributeSet extends AttributeSetUtilities.UnmodifiableAttributeSet implements PrintJobAttributeSet, Serializable {
      public UnmodifiablePrintJobAttributeSet(PrintJobAttributeSet var1) {
         super(var1);
      }
   }

   private static class UnmodifiablePrintRequestAttributeSet extends AttributeSetUtilities.UnmodifiableAttributeSet implements PrintRequestAttributeSet, Serializable {
      public UnmodifiablePrintRequestAttributeSet(PrintRequestAttributeSet var1) {
         super(var1);
      }
   }

   private static class UnmodifiableDocAttributeSet extends AttributeSetUtilities.UnmodifiableAttributeSet implements DocAttributeSet, Serializable {
      public UnmodifiableDocAttributeSet(DocAttributeSet var1) {
         super(var1);
      }
   }

   private static class UnmodifiableAttributeSet implements AttributeSet, Serializable {
      private AttributeSet attrset;

      public UnmodifiableAttributeSet(AttributeSet var1) {
         this.attrset = var1;
      }

      public Attribute get(Class<?> var1) {
         return this.attrset.get(var1);
      }

      public boolean add(Attribute var1) {
         throw new UnmodifiableSetException();
      }

      public synchronized boolean remove(Class<?> var1) {
         throw new UnmodifiableSetException();
      }

      public boolean remove(Attribute var1) {
         throw new UnmodifiableSetException();
      }

      public boolean containsKey(Class<?> var1) {
         return this.attrset.containsKey(var1);
      }

      public boolean containsValue(Attribute var1) {
         return this.attrset.containsValue(var1);
      }

      public boolean addAll(AttributeSet var1) {
         throw new UnmodifiableSetException();
      }

      public int size() {
         return this.attrset.size();
      }

      public Attribute[] toArray() {
         return this.attrset.toArray();
      }

      public void clear() {
         throw new UnmodifiableSetException();
      }

      public boolean isEmpty() {
         return this.attrset.isEmpty();
      }

      public boolean equals(Object var1) {
         return this.attrset.equals(var1);
      }

      public int hashCode() {
         return this.attrset.hashCode();
      }
   }
}
