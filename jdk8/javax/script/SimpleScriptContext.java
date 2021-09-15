package javax.script;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleScriptContext implements ScriptContext {
   protected Writer writer;
   protected Writer errorWriter;
   protected Reader reader;
   protected Bindings engineScope = new SimpleBindings();
   protected Bindings globalScope = null;
   private static List<Integer> scopes = new ArrayList(2);

   public SimpleScriptContext() {
      this.reader = new InputStreamReader(System.in);
      this.writer = new PrintWriter(System.out, true);
      this.errorWriter = new PrintWriter(System.err, true);
   }

   public void setBindings(Bindings var1, int var2) {
      switch(var2) {
      case 100:
         if (var1 == null) {
            throw new NullPointerException("Engine scope cannot be null.");
         }

         this.engineScope = var1;
         break;
      case 200:
         this.globalScope = var1;
         break;
      default:
         throw new IllegalArgumentException("Invalid scope value.");
      }

   }

   public Object getAttribute(String var1) {
      this.checkName(var1);
      if (this.engineScope.containsKey(var1)) {
         return this.getAttribute(var1, 100);
      } else {
         return this.globalScope != null && this.globalScope.containsKey(var1) ? this.getAttribute(var1, 200) : null;
      }
   }

   public Object getAttribute(String var1, int var2) {
      this.checkName(var1);
      switch(var2) {
      case 100:
         return this.engineScope.get(var1);
      case 200:
         if (this.globalScope != null) {
            return this.globalScope.get(var1);
         }

         return null;
      default:
         throw new IllegalArgumentException("Illegal scope value.");
      }
   }

   public Object removeAttribute(String var1, int var2) {
      this.checkName(var1);
      switch(var2) {
      case 100:
         if (this.getBindings(100) != null) {
            return this.getBindings(100).remove(var1);
         }

         return null;
      case 200:
         if (this.getBindings(200) != null) {
            return this.getBindings(200).remove(var1);
         }

         return null;
      default:
         throw new IllegalArgumentException("Illegal scope value.");
      }
   }

   public void setAttribute(String var1, Object var2, int var3) {
      this.checkName(var1);
      switch(var3) {
      case 100:
         this.engineScope.put(var1, var2);
         return;
      case 200:
         if (this.globalScope != null) {
            this.globalScope.put(var1, var2);
         }

         return;
      default:
         throw new IllegalArgumentException("Illegal scope value.");
      }
   }

   public Writer getWriter() {
      return this.writer;
   }

   public Reader getReader() {
      return this.reader;
   }

   public void setReader(Reader var1) {
      this.reader = var1;
   }

   public void setWriter(Writer var1) {
      this.writer = var1;
   }

   public Writer getErrorWriter() {
      return this.errorWriter;
   }

   public void setErrorWriter(Writer var1) {
      this.errorWriter = var1;
   }

   public int getAttributesScope(String var1) {
      this.checkName(var1);
      if (this.engineScope.containsKey(var1)) {
         return 100;
      } else {
         return this.globalScope != null && this.globalScope.containsKey(var1) ? 200 : -1;
      }
   }

   public Bindings getBindings(int var1) {
      if (var1 == 100) {
         return this.engineScope;
      } else if (var1 == 200) {
         return this.globalScope;
      } else {
         throw new IllegalArgumentException("Illegal scope value.");
      }
   }

   public List<Integer> getScopes() {
      return scopes;
   }

   private void checkName(String var1) {
      Objects.requireNonNull(var1);
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("name cannot be empty");
      }
   }

   static {
      scopes.add(100);
      scopes.add(200);
      scopes = Collections.unmodifiableList(scopes);
   }
}
