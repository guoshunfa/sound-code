package javax.script;

import java.io.Reader;

public abstract class AbstractScriptEngine implements ScriptEngine {
   protected ScriptContext context;

   public AbstractScriptEngine() {
      this.context = new SimpleScriptContext();
   }

   public AbstractScriptEngine(Bindings var1) {
      this();
      if (var1 == null) {
         throw new NullPointerException("n is null");
      } else {
         this.context.setBindings(var1, 100);
      }
   }

   public void setContext(ScriptContext var1) {
      if (var1 == null) {
         throw new NullPointerException("null context");
      } else {
         this.context = var1;
      }
   }

   public ScriptContext getContext() {
      return this.context;
   }

   public Bindings getBindings(int var1) {
      if (var1 == 200) {
         return this.context.getBindings(200);
      } else if (var1 == 100) {
         return this.context.getBindings(100);
      } else {
         throw new IllegalArgumentException("Invalid scope value.");
      }
   }

   public void setBindings(Bindings var1, int var2) {
      if (var2 == 200) {
         this.context.setBindings(var1, 200);
      } else {
         if (var2 != 100) {
            throw new IllegalArgumentException("Invalid scope value.");
         }

         this.context.setBindings(var1, 100);
      }

   }

   public void put(String var1, Object var2) {
      Bindings var3 = this.getBindings(100);
      if (var3 != null) {
         var3.put(var1, var2);
      }

   }

   public Object get(String var1) {
      Bindings var2 = this.getBindings(100);
      return var2 != null ? var2.get(var1) : null;
   }

   public Object eval(Reader var1, Bindings var2) throws ScriptException {
      ScriptContext var3 = this.getScriptContext(var2);
      return this.eval((Reader)var1, (ScriptContext)var3);
   }

   public Object eval(String var1, Bindings var2) throws ScriptException {
      ScriptContext var3 = this.getScriptContext(var2);
      return this.eval((String)var1, (ScriptContext)var3);
   }

   public Object eval(Reader var1) throws ScriptException {
      return this.eval((Reader)var1, (ScriptContext)this.context);
   }

   public Object eval(String var1) throws ScriptException {
      return this.eval((String)var1, (ScriptContext)this.context);
   }

   protected ScriptContext getScriptContext(Bindings var1) {
      SimpleScriptContext var2 = new SimpleScriptContext();
      Bindings var3 = this.getBindings(200);
      if (var3 != null) {
         var2.setBindings(var3, 200);
      }

      if (var1 != null) {
         var2.setBindings(var1, 100);
         var2.setReader(this.context.getReader());
         var2.setWriter(this.context.getWriter());
         var2.setErrorWriter(this.context.getErrorWriter());
         return var2;
      } else {
         throw new NullPointerException("Engine scope Bindings may not be null.");
      }
   }
}
