package java.beans;

import java.applet.Applet;
import java.beans.beancontext.BeanContext;

public interface AppletInitializer {
   void initialize(Applet var1, BeanContext var2);

   void activate(Applet var1);
}
