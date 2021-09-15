package java.beans;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.net.URL;

public class SimpleBeanInfo implements BeanInfo {
   public BeanDescriptor getBeanDescriptor() {
      return null;
   }

   public PropertyDescriptor[] getPropertyDescriptors() {
      return null;
   }

   public int getDefaultPropertyIndex() {
      return -1;
   }

   public EventSetDescriptor[] getEventSetDescriptors() {
      return null;
   }

   public int getDefaultEventIndex() {
      return -1;
   }

   public MethodDescriptor[] getMethodDescriptors() {
      return null;
   }

   public BeanInfo[] getAdditionalBeanInfo() {
      return null;
   }

   public Image getIcon(int var1) {
      return null;
   }

   public Image loadImage(String var1) {
      try {
         URL var2 = this.getClass().getResource(var1);
         if (var2 != null) {
            ImageProducer var3 = (ImageProducer)var2.getContent();
            if (var3 != null) {
               return Toolkit.getDefaultToolkit().createImage(var3);
            }
         }
      } catch (Exception var4) {
      }

      return null;
   }
}
