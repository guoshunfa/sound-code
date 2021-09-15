package javax.script;

public abstract class CompiledScript {
   public abstract Object eval(ScriptContext var1) throws ScriptException;

   public Object eval(Bindings var1) throws ScriptException {
      Object var2 = this.getEngine().getContext();
      if (var1 != null) {
         SimpleScriptContext var3 = new SimpleScriptContext();
         var3.setBindings(var1, 100);
         var3.setBindings(((ScriptContext)var2).getBindings(200), 200);
         var3.setWriter(((ScriptContext)var2).getWriter());
         var3.setReader(((ScriptContext)var2).getReader());
         var3.setErrorWriter(((ScriptContext)var2).getErrorWriter());
         var2 = var3;
      }

      return this.eval((ScriptContext)var2);
   }

   public Object eval() throws ScriptException {
      return this.eval(this.getEngine().getContext());
   }

   public abstract ScriptEngine getEngine();
}
