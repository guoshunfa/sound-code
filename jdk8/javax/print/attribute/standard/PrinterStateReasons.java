package javax.print.attribute.standard;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;

public final class PrinterStateReasons extends HashMap<PrinterStateReason, Severity> implements PrintServiceAttribute {
   private static final long serialVersionUID = -3731791085163619457L;

   public PrinterStateReasons() {
   }

   public PrinterStateReasons(int var1) {
      super(var1);
   }

   public PrinterStateReasons(int var1, float var2) {
      super(var1, var2);
   }

   public PrinterStateReasons(Map<PrinterStateReason, Severity> var1) {
      this();
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.put((PrinterStateReason)var3.getKey(), (Severity)var3.getValue());
      }

   }

   public Severity put(PrinterStateReason var1, Severity var2) {
      if (var1 == null) {
         throw new NullPointerException("reason is null");
      } else if (var2 == null) {
         throw new NullPointerException("severity is null");
      } else {
         return (Severity)super.put(var1, var2);
      }
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterStateReasons.class;
   }

   public final String getName() {
      return "printer-state-reasons";
   }

   public Set<PrinterStateReason> printerStateReasonSet(Severity var1) {
      if (var1 == null) {
         throw new NullPointerException("severity is null");
      } else {
         return new PrinterStateReasons.PrinterStateReasonSet(var1, this.entrySet());
      }
   }

   private class PrinterStateReasonSetIterator implements Iterator {
      private Severity mySeverity;
      private Iterator myIterator;
      private Map.Entry myEntry;

      public PrinterStateReasonSetIterator(Severity var2, Iterator var3) {
         this.mySeverity = var2;
         this.myIterator = var3;
         this.goToNext();
      }

      private void goToNext() {
         this.myEntry = null;

         while(this.myEntry == null && this.myIterator.hasNext()) {
            this.myEntry = (Map.Entry)this.myIterator.next();
            if ((Severity)this.myEntry.getValue() != this.mySeverity) {
               this.myEntry = null;
            }
         }

      }

      public boolean hasNext() {
         return this.myEntry != null;
      }

      public Object next() {
         if (this.myEntry == null) {
            throw new NoSuchElementException();
         } else {
            Object var1 = this.myEntry.getKey();
            this.goToNext();
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private class PrinterStateReasonSet extends AbstractSet<PrinterStateReason> {
      private Severity mySeverity;
      private Set myEntrySet;

      public PrinterStateReasonSet(Severity var2, Set var3) {
         this.mySeverity = var2;
         this.myEntrySet = var3;
      }

      public int size() {
         int var1 = 0;

         for(Iterator var2 = this.iterator(); var2.hasNext(); ++var1) {
            var2.next();
         }

         return var1;
      }

      public Iterator iterator() {
         return PrinterStateReasons.this.new PrinterStateReasonSetIterator(this.mySeverity, this.myEntrySet.iterator());
      }
   }
}
