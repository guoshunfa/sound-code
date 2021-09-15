package java.beans;

import com.sun.beans.finder.PersistenceDelegateFinder;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Encoder {
   private final PersistenceDelegateFinder finder = new PersistenceDelegateFinder();
   private Map<Object, Expression> bindings = new IdentityHashMap();
   private ExceptionListener exceptionListener;
   boolean executeStatements = true;
   private Map<Object, Object> attributes;

   protected void writeObject(Object var1) {
      if (var1 != this) {
         PersistenceDelegate var2 = this.getPersistenceDelegate(var1 == null ? null : var1.getClass());
         var2.writeObject(var1, this);
      }
   }

   public void setExceptionListener(ExceptionListener var1) {
      this.exceptionListener = var1;
   }

   public ExceptionListener getExceptionListener() {
      return this.exceptionListener != null ? this.exceptionListener : Statement.defaultExceptionListener;
   }

   Object getValue(Expression var1) {
      try {
         return var1 == null ? null : var1.getValue();
      } catch (Exception var3) {
         this.getExceptionListener().exceptionThrown(var3);
         throw new RuntimeException("failed to evaluate: " + var1.toString());
      }
   }

   public PersistenceDelegate getPersistenceDelegate(Class<?> var1) {
      PersistenceDelegate var2 = this.finder.find(var1);
      if (var2 == null) {
         var2 = MetaData.getPersistenceDelegate(var1);
         if (var2 != null) {
            this.finder.register(var1, var2);
         }
      }

      return var2;
   }

   public void setPersistenceDelegate(Class<?> var1, PersistenceDelegate var2) {
      this.finder.register(var1, var2);
   }

   public Object remove(Object var1) {
      Expression var2 = (Expression)this.bindings.remove(var1);
      return this.getValue(var2);
   }

   public Object get(Object var1) {
      if (var1 != null && var1 != this && var1.getClass() != String.class) {
         Expression var2 = (Expression)this.bindings.get(var1);
         return this.getValue(var2);
      } else {
         return var1;
      }
   }

   private Object writeObject1(Object var1) {
      Object var2 = this.get(var1);
      if (var2 == null) {
         this.writeObject(var1);
         var2 = this.get(var1);
      }

      return var2;
   }

   private Statement cloneStatement(Statement var1) {
      Object var2 = var1.getTarget();
      Object var3 = this.writeObject1(var2);
      Object[] var4 = var1.getArguments();
      Object[] var5 = new Object[var4.length];

      for(int var6 = 0; var6 < var4.length; ++var6) {
         var5[var6] = this.writeObject1(var4[var6]);
      }

      Object var7 = Statement.class.equals(var1.getClass()) ? new Statement(var3, var1.getMethodName(), var5) : new Expression(var3, var1.getMethodName(), var5);
      ((Statement)var7).loader = var1.loader;
      return (Statement)var7;
   }

   public void writeStatement(Statement var1) {
      Statement var2 = this.cloneStatement(var1);
      if (var1.getTarget() != this && this.executeStatements) {
         try {
            var2.execute();
         } catch (Exception var4) {
            this.getExceptionListener().exceptionThrown(new Exception("Encoder: discarding statement " + var2, var4));
         }
      }

   }

   public void writeExpression(Expression var1) {
      Object var2 = this.getValue(var1);
      if (this.get(var2) == null) {
         this.bindings.put(var2, (Expression)this.cloneStatement(var1));
         this.writeObject(var2);
      }
   }

   void clear() {
      this.bindings.clear();
   }

   void setAttribute(Object var1, Object var2) {
      if (this.attributes == null) {
         this.attributes = new HashMap();
      }

      this.attributes.put(var1, var2);
   }

   Object getAttribute(Object var1) {
      return this.attributes == null ? null : this.attributes.get(var1);
   }
}
