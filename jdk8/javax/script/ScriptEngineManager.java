package javax.script;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ScriptEngineManager {
   private static final boolean DEBUG = false;
   private HashSet<ScriptEngineFactory> engineSpis;
   private HashMap<String, ScriptEngineFactory> nameAssociations;
   private HashMap<String, ScriptEngineFactory> extensionAssociations;
   private HashMap<String, ScriptEngineFactory> mimeTypeAssociations;
   private Bindings globalScope;

   public ScriptEngineManager() {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();
      this.init(var1);
   }

   public ScriptEngineManager(ClassLoader var1) {
      this.init(var1);
   }

   private void init(ClassLoader var1) {
      this.globalScope = new SimpleBindings();
      this.engineSpis = new HashSet();
      this.nameAssociations = new HashMap();
      this.extensionAssociations = new HashMap();
      this.mimeTypeAssociations = new HashMap();
      this.initEngines(var1);
   }

   private ServiceLoader<ScriptEngineFactory> getServiceLoader(ClassLoader var1) {
      return var1 != null ? ServiceLoader.load(ScriptEngineFactory.class, var1) : ServiceLoader.loadInstalled(ScriptEngineFactory.class);
   }

   private void initEngines(final ClassLoader var1) {
      Iterator var2 = null;

      try {
         ServiceLoader var3 = (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<ScriptEngineFactory>>() {
            public ServiceLoader<ScriptEngineFactory> run() {
               return ScriptEngineManager.this.getServiceLoader(var1);
            }
         });
         var2 = var3.iterator();
      } catch (ServiceConfigurationError var5) {
         System.err.println("Can't find ScriptEngineFactory providers: " + var5.getMessage());
         return;
      }

      try {
         while(var2.hasNext()) {
            try {
               ScriptEngineFactory var7 = (ScriptEngineFactory)var2.next();
               this.engineSpis.add(var7);
            } catch (ServiceConfigurationError var4) {
               System.err.println("ScriptEngineManager providers.next(): " + var4.getMessage());
            }
         }

      } catch (ServiceConfigurationError var6) {
         System.err.println("ScriptEngineManager providers.hasNext(): " + var6.getMessage());
      }
   }

   public void setBindings(Bindings var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Global scope cannot be null.");
      } else {
         this.globalScope = var1;
      }
   }

   public Bindings getBindings() {
      return this.globalScope;
   }

   public void put(String var1, Object var2) {
      this.globalScope.put(var1, var2);
   }

   public Object get(String var1) {
      return this.globalScope.get(var1);
   }

   public ScriptEngine getEngineByName(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         if (null != (var2 = this.nameAssociations.get(var1))) {
            ScriptEngineFactory var3 = (ScriptEngineFactory)var2;

            try {
               ScriptEngine var13 = var3.getScriptEngine();
               var13.setBindings(this.getBindings(), 200);
               return var13;
            } catch (Exception var11) {
            }
         }

         Iterator var12 = this.engineSpis.iterator();

         label50:
         while(var12.hasNext()) {
            ScriptEngineFactory var4 = (ScriptEngineFactory)var12.next();
            List var5 = null;

            try {
               var5 = var4.getNames();
            } catch (Exception var9) {
            }

            if (var5 != null) {
               Iterator var6 = var5.iterator();

               while(true) {
                  String var7;
                  do {
                     if (!var6.hasNext()) {
                        continue label50;
                     }

                     var7 = (String)var6.next();
                  } while(!var1.equals(var7));

                  try {
                     ScriptEngine var8 = var4.getScriptEngine();
                     var8.setBindings(this.getBindings(), 200);
                     return var8;
                  } catch (Exception var10) {
                  }
               }
            }
         }

         return null;
      }
   }

   public ScriptEngine getEngineByExtension(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         if (null != (var2 = this.extensionAssociations.get(var1))) {
            ScriptEngineFactory var3 = (ScriptEngineFactory)var2;

            try {
               ScriptEngine var13 = var3.getScriptEngine();
               var13.setBindings(this.getBindings(), 200);
               return var13;
            } catch (Exception var11) {
            }
         }

         Iterator var12 = this.engineSpis.iterator();

         label51:
         while(var12.hasNext()) {
            ScriptEngineFactory var4 = (ScriptEngineFactory)var12.next();
            List var5 = null;

            try {
               var5 = var4.getExtensions();
            } catch (Exception var9) {
            }

            if (var5 != null) {
               Iterator var6 = var5.iterator();

               while(true) {
                  String var7;
                  do {
                     if (!var6.hasNext()) {
                        continue label51;
                     }

                     var7 = (String)var6.next();
                  } while(!var1.equals(var7));

                  try {
                     ScriptEngine var8 = var4.getScriptEngine();
                     var8.setBindings(this.getBindings(), 200);
                     return var8;
                  } catch (Exception var10) {
                  }
               }
            }
         }

         return null;
      }
   }

   public ScriptEngine getEngineByMimeType(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         if (null != (var2 = this.mimeTypeAssociations.get(var1))) {
            ScriptEngineFactory var3 = (ScriptEngineFactory)var2;

            try {
               ScriptEngine var13 = var3.getScriptEngine();
               var13.setBindings(this.getBindings(), 200);
               return var13;
            } catch (Exception var11) {
            }
         }

         Iterator var12 = this.engineSpis.iterator();

         label51:
         while(var12.hasNext()) {
            ScriptEngineFactory var4 = (ScriptEngineFactory)var12.next();
            List var5 = null;

            try {
               var5 = var4.getMimeTypes();
            } catch (Exception var9) {
            }

            if (var5 != null) {
               Iterator var6 = var5.iterator();

               while(true) {
                  String var7;
                  do {
                     if (!var6.hasNext()) {
                        continue label51;
                     }

                     var7 = (String)var6.next();
                  } while(!var1.equals(var7));

                  try {
                     ScriptEngine var8 = var4.getScriptEngine();
                     var8.setBindings(this.getBindings(), 200);
                     return var8;
                  } catch (Exception var10) {
                  }
               }
            }
         }

         return null;
      }
   }

   public List<ScriptEngineFactory> getEngineFactories() {
      ArrayList var1 = new ArrayList(this.engineSpis.size());
      Iterator var2 = this.engineSpis.iterator();

      while(var2.hasNext()) {
         ScriptEngineFactory var3 = (ScriptEngineFactory)var2.next();
         var1.add(var3);
      }

      return Collections.unmodifiableList(var1);
   }

   public void registerEngineName(String var1, ScriptEngineFactory var2) {
      if (var1 != null && var2 != null) {
         this.nameAssociations.put(var1, var2);
      } else {
         throw new NullPointerException();
      }
   }

   public void registerEngineMimeType(String var1, ScriptEngineFactory var2) {
      if (var1 != null && var2 != null) {
         this.mimeTypeAssociations.put(var1, var2);
      } else {
         throw new NullPointerException();
      }
   }

   public void registerEngineExtension(String var1, ScriptEngineFactory var2) {
      if (var1 != null && var2 != null) {
         this.extensionAssociations.put(var1, var2);
      } else {
         throw new NullPointerException();
      }
   }
}
