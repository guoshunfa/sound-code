package javax.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;
import sun.util.CoreResourceBundleControl;

public class UIDefaults extends Hashtable<Object, Object> {
   private static final Object PENDING = new Object();
   private SwingPropertyChangeSupport changeSupport;
   private Vector<String> resourceBundles;
   private Locale defaultLocale;
   private Map<Locale, Map<String, Object>> resourceCache;

   public UIDefaults() {
      this(700, 0.75F);
   }

   public UIDefaults(int var1, float var2) {
      super(var1, var2);
      this.defaultLocale = Locale.getDefault();
      this.resourceCache = new HashMap();
   }

   public UIDefaults(Object[] var1) {
      super(var1.length / 2);
      this.defaultLocale = Locale.getDefault();

      for(int var2 = 0; var2 < var1.length; var2 += 2) {
         super.put(var1[var2], var1[var2 + 1]);
      }

   }

   public Object get(Object var1) {
      Object var2 = this.getFromHashtable(var1);
      return var2 != null ? var2 : this.getFromResourceBundle(var1, (Locale)null);
   }

   private Object getFromHashtable(Object var1) {
      Object var2 = super.get(var1);
      if (var2 != PENDING && !(var2 instanceof UIDefaults.ActiveValue) && !(var2 instanceof UIDefaults.LazyValue)) {
         return var2;
      } else {
         synchronized(this) {
            var2 = super.get(var1);
            if (var2 == PENDING) {
               do {
                  try {
                     this.wait();
                  } catch (InterruptedException var17) {
                  }

                  var2 = super.get(var1);
               } while(var2 == PENDING);

               return var2;
            }

            if (var2 instanceof UIDefaults.LazyValue) {
               super.put(var1, PENDING);
            } else if (!(var2 instanceof UIDefaults.ActiveValue)) {
               return var2;
            }
         }

         if (var2 instanceof UIDefaults.LazyValue) {
            boolean var15 = false;

            try {
               var15 = true;
               var2 = ((UIDefaults.LazyValue)var2).createValue(this);
               var15 = false;
            } finally {
               if (var15) {
                  synchronized(this) {
                     if (var2 == null) {
                        super.remove(var1);
                     } else {
                        super.put(var1, var2);
                     }

                     this.notifyAll();
                  }
               }
            }

            synchronized(this) {
               if (var2 == null) {
                  super.remove(var1);
               } else {
                  super.put(var1, var2);
               }

               this.notifyAll();
            }
         } else {
            var2 = ((UIDefaults.ActiveValue)var2).createValue(this);
         }

         return var2;
      }
   }

   public Object get(Object var1, Locale var2) {
      Object var3 = this.getFromHashtable(var1);
      return var3 != null ? var3 : this.getFromResourceBundle(var1, var2);
   }

   private Object getFromResourceBundle(Object var1, Locale var2) {
      if (this.resourceBundles != null && !this.resourceBundles.isEmpty() && var1 instanceof String) {
         if (var2 == null) {
            if (this.defaultLocale == null) {
               return null;
            }

            var2 = this.defaultLocale;
         }

         synchronized(this) {
            return this.getResourceCache(var2).get(var1);
         }
      } else {
         return null;
      }
   }

   private Map<String, Object> getResourceCache(Locale var1) {
      Object var2 = (Map)this.resourceCache.get(var1);
      if (var2 == null) {
         var2 = new UIDefaults.TextAndMnemonicHashMap();

         for(int var3 = this.resourceBundles.size() - 1; var3 >= 0; --var3) {
            String var4 = (String)this.resourceBundles.get(var3);

            try {
               CoreResourceBundleControl var5 = CoreResourceBundleControl.getRBControlInstance(var4);
               ResourceBundle var6;
               if (var5 != null) {
                  var6 = ResourceBundle.getBundle(var4, var1, (ResourceBundle.Control)var5);
               } else {
                  var6 = ResourceBundle.getBundle(var4, var1, ClassLoader.getSystemClassLoader());
               }

               Enumeration var7 = var6.getKeys();

               while(var7.hasMoreElements()) {
                  String var8 = (String)var7.nextElement();
                  if (((Map)var2).get(var8) == null) {
                     Object var9 = var6.getObject(var8);
                     ((Map)var2).put(var8, var9);
                  }
               }
            } catch (MissingResourceException var10) {
            }
         }

         this.resourceCache.put(var1, var2);
      }

      return (Map)var2;
   }

   public Object put(Object var1, Object var2) {
      Object var3 = var2 == null ? super.remove(var1) : super.put(var1, var2);
      if (var1 instanceof String) {
         this.firePropertyChange((String)var1, var3, var2);
      }

      return var3;
   }

   public void putDefaults(Object[] var1) {
      int var2 = 0;

      for(int var3 = var1.length; var2 < var3; var2 += 2) {
         Object var4 = var1[var2 + 1];
         if (var4 == null) {
            super.remove(var1[var2]);
         } else {
            super.put(var1[var2], var4);
         }
      }

      this.firePropertyChange("UIDefaults", (Object)null, (Object)null);
   }

   public Font getFont(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Font ? (Font)var2 : null;
   }

   public Font getFont(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Font ? (Font)var3 : null;
   }

   public Color getColor(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Color ? (Color)var2 : null;
   }

   public Color getColor(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Color ? (Color)var3 : null;
   }

   public Icon getIcon(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Icon ? (Icon)var2 : null;
   }

   public Icon getIcon(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Icon ? (Icon)var3 : null;
   }

   public Border getBorder(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Border ? (Border)var2 : null;
   }

   public Border getBorder(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Border ? (Border)var3 : null;
   }

   public String getString(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof String ? (String)var2 : null;
   }

   public String getString(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof String ? (String)var3 : null;
   }

   public int getInt(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Integer ? (Integer)var2 : 0;
   }

   public int getInt(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Integer ? (Integer)var3 : 0;
   }

   public boolean getBoolean(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Boolean ? (Boolean)var2 : false;
   }

   public boolean getBoolean(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Boolean ? (Boolean)var3 : false;
   }

   public Insets getInsets(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Insets ? (Insets)var2 : null;
   }

   public Insets getInsets(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Insets ? (Insets)var3 : null;
   }

   public Dimension getDimension(Object var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Dimension ? (Dimension)var2 : null;
   }

   public Dimension getDimension(Object var1, Locale var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Dimension ? (Dimension)var3 : null;
   }

   public Class<? extends ComponentUI> getUIClass(String var1, ClassLoader var2) {
      try {
         String var3 = (String)this.get(var1);
         if (var3 != null) {
            ReflectUtil.checkPackageAccess(var3);
            Class var4 = (Class)this.get(var3);
            if (var4 == null) {
               if (var2 == null) {
                  var4 = SwingUtilities.loadSystemClass(var3);
               } else {
                  var4 = var2.loadClass(var3);
               }

               if (var4 != null) {
                  this.put(var3, var4);
               }
            }

            return var4;
         } else {
            return null;
         }
      } catch (ClassNotFoundException var5) {
         return null;
      } catch (ClassCastException var6) {
         return null;
      }
   }

   public Class<? extends ComponentUI> getUIClass(String var1) {
      return this.getUIClass(var1, (ClassLoader)null);
   }

   protected void getUIError(String var1) {
      System.err.println("UIDefaults.getUI() failed: " + var1);

      try {
         throw new Error();
      } catch (Throwable var3) {
         var3.printStackTrace();
      }
   }

   public ComponentUI getUI(JComponent var1) {
      Object var2 = this.get("ClassLoader");
      ClassLoader var3 = var2 != null ? (ClassLoader)var2 : var1.getClass().getClassLoader();
      Class var4 = this.getUIClass(var1.getUIClassID(), var3);
      Object var5 = null;
      if (var4 == null) {
         this.getUIError("no ComponentUI class for: " + var1);
      } else {
         try {
            Method var6 = (Method)this.get(var4);
            if (var6 == null) {
               var6 = var4.getMethod("createUI", JComponent.class);
               this.put(var4, var6);
            }

            var5 = MethodUtil.invoke(var6, (Object)null, new Object[]{var1});
         } catch (NoSuchMethodException var7) {
            this.getUIError("static createUI() method not found in " + var4);
         } catch (Exception var8) {
            this.getUIError("createUI() failed for " + var1 + " " + var8);
         }
      }

      return (ComponentUI)var5;
   }

   public synchronized void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport == null) {
         this.changeSupport = new SwingPropertyChangeSupport(this);
      }

      this.changeSupport.addPropertyChangeListener(var1);
   }

   public synchronized void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport != null) {
         this.changeSupport.removePropertyChangeListener(var1);
      }

   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (this.changeSupport != null) {
         this.changeSupport.firePropertyChange(var1, var2, var3);
      }

   }

   public synchronized void addResourceBundle(String var1) {
      if (var1 != null) {
         if (this.resourceBundles == null) {
            this.resourceBundles = new Vector(5);
         }

         if (!this.resourceBundles.contains(var1)) {
            this.resourceBundles.add(var1);
            this.resourceCache.clear();
         }

      }
   }

   public synchronized void removeResourceBundle(String var1) {
      if (this.resourceBundles != null) {
         this.resourceBundles.remove(var1);
      }

      this.resourceCache.clear();
   }

   public void setDefaultLocale(Locale var1) {
      this.defaultLocale = var1;
   }

   public Locale getDefaultLocale() {
      return this.defaultLocale;
   }

   private static class TextAndMnemonicHashMap extends HashMap<String, Object> {
      static final String AND_MNEMONIC = "AndMnemonic";
      static final String TITLE_SUFFIX = ".titleAndMnemonic";
      static final String TEXT_SUFFIX = ".textAndMnemonic";

      private TextAndMnemonicHashMap() {
      }

      public Object get(Object var1) {
         Object var2 = super.get(var1);
         if (var2 == null) {
            boolean var3 = false;
            String var4 = var1.toString();
            String var5 = null;
            if (var4.endsWith("AndMnemonic")) {
               return null;
            }

            if (var4.endsWith(".mnemonic")) {
               var5 = this.composeKey(var4, 9, ".textAndMnemonic");
            } else if (var4.endsWith("NameMnemonic")) {
               var5 = this.composeKey(var4, 12, ".textAndMnemonic");
            } else if (var4.endsWith("Mnemonic")) {
               var5 = this.composeKey(var4, 8, ".textAndMnemonic");
               var3 = true;
            }

            if (var5 != null) {
               var2 = super.get(var5);
               if (var2 == null && var3) {
                  var5 = this.composeKey(var4, 8, ".titleAndMnemonic");
                  var2 = super.get(var5);
               }

               return var2 == null ? null : this.getMnemonicFromProperty(var2.toString());
            }

            if (var4.endsWith("NameText")) {
               var5 = this.composeKey(var4, 8, ".textAndMnemonic");
            } else if (var4.endsWith(".nameText")) {
               var5 = this.composeKey(var4, 9, ".textAndMnemonic");
            } else if (var4.endsWith("Text")) {
               var5 = this.composeKey(var4, 4, ".textAndMnemonic");
            } else if (var4.endsWith("Title")) {
               var5 = this.composeKey(var4, 5, ".titleAndMnemonic");
            }

            if (var5 != null) {
               var2 = super.get(var5);
               return var2 == null ? null : this.getTextFromProperty(var2.toString());
            }

            if (var4.endsWith("DisplayedMnemonicIndex")) {
               var5 = this.composeKey(var4, 22, ".textAndMnemonic");
               var2 = super.get(var5);
               if (var2 == null) {
                  var5 = this.composeKey(var4, 22, ".titleAndMnemonic");
                  var2 = super.get(var5);
               }

               return var2 == null ? null : this.getIndexFromProperty(var2.toString());
            }
         }

         return var2;
      }

      String composeKey(String var1, int var2, String var3) {
         return var1.substring(0, var1.length() - var2) + var3;
      }

      String getTextFromProperty(String var1) {
         return var1.replace("&", "");
      }

      String getMnemonicFromProperty(String var1) {
         int var2 = var1.indexOf(38);
         if (0 <= var2 && var2 < var1.length() - 1) {
            char var3 = var1.charAt(var2 + 1);
            return Integer.toString(Character.toUpperCase(var3));
         } else {
            return null;
         }
      }

      String getIndexFromProperty(String var1) {
         int var2 = var1.indexOf(38);
         return var2 == -1 ? null : Integer.toString(var2);
      }

      // $FF: synthetic method
      TextAndMnemonicHashMap(Object var1) {
         this();
      }
   }

   public static class LazyInputMap implements UIDefaults.LazyValue {
      private Object[] bindings;

      public LazyInputMap(Object[] var1) {
         this.bindings = var1;
      }

      public Object createValue(UIDefaults var1) {
         if (this.bindings != null) {
            InputMap var2 = LookAndFeel.makeInputMap(this.bindings);
            return var2;
         } else {
            return null;
         }
      }
   }

   public static class ProxyLazyValue implements UIDefaults.LazyValue {
      private AccessControlContext acc;
      private String className;
      private String methodName;
      private Object[] args;

      public ProxyLazyValue(String var1) {
         this(var1, (String)null);
      }

      public ProxyLazyValue(String var1, String var2) {
         this(var1, var2, (Object[])null);
      }

      public ProxyLazyValue(String var1, Object[] var2) {
         this(var1, (String)null, var2);
      }

      public ProxyLazyValue(String var1, String var2, Object[] var3) {
         this.acc = AccessController.getContext();
         this.className = var1;
         this.methodName = var2;
         if (var3 != null) {
            this.args = (Object[])var3.clone();
         }

      }

      public Object createValue(final UIDefaults var1) {
         if (this.acc == null && System.getSecurityManager() != null) {
            throw new SecurityException("null AccessControlContext");
         } else {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  try {
                     Object var2;
                     if (var1 == null || !((var2 = var1.get("ClassLoader")) instanceof ClassLoader)) {
                        var2 = Thread.currentThread().getContextClassLoader();
                        if (var2 == null) {
                           var2 = ClassLoader.getSystemClassLoader();
                        }
                     }

                     ReflectUtil.checkPackageAccess(ProxyLazyValue.this.className);
                     Class var1x = Class.forName(ProxyLazyValue.this.className, true, (ClassLoader)var2);
                     SwingUtilities2.checkAccess(var1x.getModifiers());
                     Class[] var3;
                     if (ProxyLazyValue.this.methodName != null) {
                        var3 = ProxyLazyValue.this.getClassArray(ProxyLazyValue.this.args);
                        Method var6 = var1x.getMethod(ProxyLazyValue.this.methodName, var3);
                        return MethodUtil.invoke(var6, var1x, ProxyLazyValue.this.args);
                     } else {
                        var3 = ProxyLazyValue.this.getClassArray(ProxyLazyValue.this.args);
                        Constructor var4 = var1x.getConstructor(var3);
                        SwingUtilities2.checkAccess(var4.getModifiers());
                        return var4.newInstance(ProxyLazyValue.this.args);
                     }
                  } catch (Exception var5) {
                     return null;
                  }
               }
            }, this.acc);
         }
      }

      private Class[] getClassArray(Object[] var1) {
         Class[] var2 = null;
         if (var1 != null) {
            var2 = new Class[var1.length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
               if (var1[var3] instanceof Integer) {
                  var2[var3] = Integer.TYPE;
               } else if (var1[var3] instanceof Boolean) {
                  var2[var3] = Boolean.TYPE;
               } else if (var1[var3] instanceof ColorUIResource) {
                  var2[var3] = Color.class;
               } else {
                  var2[var3] = var1[var3].getClass();
               }
            }
         }

         return var2;
      }

      private String printArgs(Object[] var1) {
         String var2 = "{";
         if (var1 != null) {
            for(int var3 = 0; var3 < var1.length - 1; ++var3) {
               var2 = var2.concat(var1[var3] + ",");
            }

            var2 = var2.concat(var1[var1.length - 1] + "}");
         } else {
            var2 = var2.concat("}");
         }

         return var2;
      }
   }

   public interface ActiveValue {
      Object createValue(UIDefaults var1);
   }

   public interface LazyValue {
      Object createValue(UIDefaults var1);
   }
}
