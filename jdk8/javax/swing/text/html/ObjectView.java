package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class ObjectView extends ComponentView {
   public ObjectView(Element var1) {
      super(var1);
   }

   protected Component createComponent() {
      AttributeSet var1 = this.getElement().getAttributes();
      String var2 = (String)var1.getAttribute(HTML.Attribute.CLASSID);

      try {
         ReflectUtil.checkPackageAccess(var2);
         Class var3 = Class.forName(var2, true, Thread.currentThread().getContextClassLoader());
         Object var4 = var3.newInstance();
         if (var4 instanceof Component) {
            Component var5 = (Component)var4;
            this.setParameters(var5, var1);
            return var5;
         }
      } catch (Throwable var6) {
      }

      return this.getUnloadableRepresentation();
   }

   Component getUnloadableRepresentation() {
      JLabel var1 = new JLabel("??");
      var1.setForeground(Color.red);
      return var1;
   }

   private void setParameters(Component var1, AttributeSet var2) {
      Class var3 = var1.getClass();

      BeanInfo var4;
      try {
         var4 = Introspector.getBeanInfo(var3);
      } catch (IntrospectionException var14) {
         System.err.println("introspector failed, ex: " + var14);
         return;
      }

      PropertyDescriptor[] var5 = var4.getPropertyDescriptors();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         Object var7 = var2.getAttribute(var5[var6].getName());
         if (var7 instanceof String) {
            String var8 = (String)var7;
            Method var9 = var5[var6].getWriteMethod();
            if (var9 == null) {
               return;
            }

            Class[] var10 = var9.getParameterTypes();
            if (var10.length != 1) {
               return;
            }

            Object[] var11 = new Object[]{var8};

            try {
               MethodUtil.invoke(var9, var1, var11);
            } catch (Exception var13) {
               System.err.println("Invocation failed");
            }
         }
      }

   }
}
