package java.sql;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class SQLException extends Exception implements Iterable<Throwable> {
   private String SQLState;
   private int vendorCode;
   private volatile SQLException next;
   private static final AtomicReferenceFieldUpdater<SQLException, SQLException> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(SQLException.class, SQLException.class, "next");
   private static final long serialVersionUID = 2135244094396331484L;

   public SQLException(String var1, String var2, int var3) {
      super(var1);
      this.SQLState = var2;
      this.vendorCode = var3;
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         DriverManager.println("SQLState(" + var2 + ") vendor code(" + var3 + ")");
         this.printStackTrace(DriverManager.getLogWriter());
      }

   }

   public SQLException(String var1, String var2) {
      super(var1);
      this.SQLState = var2;
      this.vendorCode = 0;
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         this.printStackTrace(DriverManager.getLogWriter());
         DriverManager.println("SQLException: SQLState(" + var2 + ")");
      }

   }

   public SQLException(String var1) {
      super(var1);
      this.SQLState = null;
      this.vendorCode = 0;
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         this.printStackTrace(DriverManager.getLogWriter());
      }

   }

   public SQLException() {
      this.SQLState = null;
      this.vendorCode = 0;
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         this.printStackTrace(DriverManager.getLogWriter());
      }

   }

   public SQLException(Throwable var1) {
      super(var1);
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         this.printStackTrace(DriverManager.getLogWriter());
      }

   }

   public SQLException(String var1, Throwable var2) {
      super(var1, var2);
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         this.printStackTrace(DriverManager.getLogWriter());
      }

   }

   public SQLException(String var1, String var2, Throwable var3) {
      super(var1, var3);
      this.SQLState = var2;
      this.vendorCode = 0;
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         this.printStackTrace(DriverManager.getLogWriter());
         DriverManager.println("SQLState(" + this.SQLState + ")");
      }

   }

   public SQLException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var4);
      this.SQLState = var2;
      this.vendorCode = var3;
      if (!(this instanceof SQLWarning) && DriverManager.getLogWriter() != null) {
         DriverManager.println("SQLState(" + this.SQLState + ") vendor code(" + var3 + ")");
         this.printStackTrace(DriverManager.getLogWriter());
      }

   }

   public String getSQLState() {
      return this.SQLState;
   }

   public int getErrorCode() {
      return this.vendorCode;
   }

   public SQLException getNextException() {
      return this.next;
   }

   public void setNextException(SQLException var1) {
      SQLException var2 = this;

      while(true) {
         while(true) {
            SQLException var3 = var2.next;
            if (var3 != null) {
               var2 = var3;
            } else {
               if (nextUpdater.compareAndSet(var2, (Object)null, var1)) {
                  return;
               }

               var2 = var2.next;
            }
         }
      }
   }

   public Iterator<Throwable> iterator() {
      return new Iterator<Throwable>() {
         SQLException firstException = SQLException.this;
         SQLException nextException;
         Throwable cause;

         {
            this.nextException = this.firstException.getNextException();
            this.cause = this.firstException.getCause();
         }

         public boolean hasNext() {
            return this.firstException != null || this.nextException != null || this.cause != null;
         }

         public Throwable next() {
            Object var1 = null;
            if (this.firstException != null) {
               var1 = this.firstException;
               this.firstException = null;
            } else if (this.cause != null) {
               var1 = this.cause;
               this.cause = this.cause.getCause();
            } else {
               if (this.nextException == null) {
                  throw new NoSuchElementException();
               }

               var1 = this.nextException;
               this.cause = this.nextException.getCause();
               this.nextException = this.nextException.getNextException();
            }

            return (Throwable)var1;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }
}
