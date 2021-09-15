package javax.swing.plaf.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;

class LazyActionMap extends ActionMapUIResource {
   private transient Object _loader;

   static void installLazyActionMap(JComponent var0, Class var1, String var2) {
      Object var3 = (ActionMap)UIManager.get(var2);
      if (var3 == null) {
         var3 = new LazyActionMap(var1);
         UIManager.getLookAndFeelDefaults().put(var2, var3);
      }

      SwingUtilities.replaceUIActionMap(var0, (ActionMap)var3);
   }

   static ActionMap getActionMap(Class var0, String var1) {
      Object var2 = (ActionMap)UIManager.get(var1);
      if (var2 == null) {
         var2 = new LazyActionMap(var0);
         UIManager.getLookAndFeelDefaults().put(var1, var2);
      }

      return (ActionMap)var2;
   }

   private LazyActionMap(Class var1) {
      this._loader = var1;
   }

   public void put(Action var1) {
      this.put(var1.getValue("Name"), var1);
   }

   public void put(Object var1, Action var2) {
      this.loadIfNecessary();
      super.put(var1, var2);
   }

   public Action get(Object var1) {
      this.loadIfNecessary();
      return super.get(var1);
   }

   public void remove(Object var1) {
      this.loadIfNecessary();
      super.remove(var1);
   }

   public void clear() {
      this.loadIfNecessary();
      super.clear();
   }

   public Object[] keys() {
      this.loadIfNecessary();
      return super.keys();
   }

   public int size() {
      this.loadIfNecessary();
      return super.size();
   }

   public Object[] allKeys() {
      this.loadIfNecessary();
      return super.allKeys();
   }

   public void setParent(ActionMap var1) {
      this.loadIfNecessary();
      super.setParent(var1);
   }

   private void loadIfNecessary() {
      if (this._loader != null) {
         Object var1 = this._loader;
         this._loader = null;
         Class var2 = (Class)var1;

         try {
            Method var3 = var2.getDeclaredMethod("loadActionMap", LazyActionMap.class);
            var3.invoke(var2, this);
         } catch (NoSuchMethodException var4) {
            assert false : "LazyActionMap unable to load actions " + var2;
         } catch (IllegalAccessException var5) {
            assert false : "LazyActionMap unable to load actions " + var5;
         } catch (InvocationTargetException var6) {
            assert false : "LazyActionMap unable to load actions " + var6;
         } catch (IllegalArgumentException var7) {
            assert false : "LazyActionMap unable to load actions " + var7;
         }
      }

   }
}
