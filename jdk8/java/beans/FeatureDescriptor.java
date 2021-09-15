package java.beans;

import com.sun.beans.TypeResolver;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class FeatureDescriptor {
   private static final String TRANSIENT = "transient";
   private Reference<? extends Class<?>> classRef;
   private boolean expert;
   private boolean hidden;
   private boolean preferred;
   private String shortDescription;
   private String name;
   private String displayName;
   private Hashtable<String, Object> table;

   public FeatureDescriptor() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getDisplayName() {
      return this.displayName == null ? this.getName() : this.displayName;
   }

   public void setDisplayName(String var1) {
      this.displayName = var1;
   }

   public boolean isExpert() {
      return this.expert;
   }

   public void setExpert(boolean var1) {
      this.expert = var1;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      this.hidden = var1;
   }

   public boolean isPreferred() {
      return this.preferred;
   }

   public void setPreferred(boolean var1) {
      this.preferred = var1;
   }

   public String getShortDescription() {
      return this.shortDescription == null ? this.getDisplayName() : this.shortDescription;
   }

   public void setShortDescription(String var1) {
      this.shortDescription = var1;
   }

   public void setValue(String var1, Object var2) {
      this.getTable().put(var1, var2);
   }

   public Object getValue(String var1) {
      return this.table != null ? this.table.get(var1) : null;
   }

   public Enumeration<String> attributeNames() {
      return this.getTable().keys();
   }

   FeatureDescriptor(FeatureDescriptor var1, FeatureDescriptor var2) {
      this.expert = var1.expert | var2.expert;
      this.hidden = var1.hidden | var2.hidden;
      this.preferred = var1.preferred | var2.preferred;
      this.name = var2.name;
      this.shortDescription = var1.shortDescription;
      if (var2.shortDescription != null) {
         this.shortDescription = var2.shortDescription;
      }

      this.displayName = var1.displayName;
      if (var2.displayName != null) {
         this.displayName = var2.displayName;
      }

      this.classRef = var1.classRef;
      if (var2.classRef != null) {
         this.classRef = var2.classRef;
      }

      this.addTable(var1.table);
      this.addTable(var2.table);
   }

   FeatureDescriptor(FeatureDescriptor var1) {
      this.expert = var1.expert;
      this.hidden = var1.hidden;
      this.preferred = var1.preferred;
      this.name = var1.name;
      this.shortDescription = var1.shortDescription;
      this.displayName = var1.displayName;
      this.classRef = var1.classRef;
      this.addTable(var1.table);
   }

   private void addTable(Hashtable<String, Object> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.getTable().putAll(var1);
      }

   }

   private Hashtable<String, Object> getTable() {
      if (this.table == null) {
         this.table = new Hashtable();
      }

      return this.table;
   }

   void setTransient(Transient var1) {
      if (var1 != null && null == this.getValue("transient")) {
         this.setValue("transient", var1.value());
      }

   }

   boolean isTransient() {
      Object var1 = this.getValue("transient");
      return var1 instanceof Boolean ? (Boolean)var1 : false;
   }

   void setClass0(Class<?> var1) {
      this.classRef = getWeakReference(var1);
   }

   Class<?> getClass0() {
      return this.classRef != null ? (Class)this.classRef.get() : null;
   }

   static <T> Reference<T> getSoftReference(T var0) {
      return var0 != null ? new SoftReference(var0) : null;
   }

   static <T> Reference<T> getWeakReference(T var0) {
      return var0 != null ? new WeakReference(var0) : null;
   }

   static Class<?> getReturnType(Class<?> var0, Method var1) {
      if (var0 == null) {
         var0 = var1.getDeclaringClass();
      }

      return TypeResolver.erase(TypeResolver.resolveInClass(var0, var1.getGenericReturnType()));
   }

   static Class<?>[] getParameterTypes(Class<?> var0, Method var1) {
      if (var0 == null) {
         var0 = var1.getDeclaringClass();
      }

      return TypeResolver.erase(TypeResolver.resolveInClass(var0, var1.getGenericParameterTypes()));
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.getClass().getName());
      var1.append("[name=").append(this.name);
      appendTo(var1, "displayName", (Object)this.displayName);
      appendTo(var1, "shortDescription", (Object)this.shortDescription);
      appendTo(var1, "preferred", this.preferred);
      appendTo(var1, "hidden", this.hidden);
      appendTo(var1, "expert", this.expert);
      if (this.table != null && !this.table.isEmpty()) {
         var1.append("; values={");
         Iterator var2 = this.table.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            var1.append((String)var3.getKey()).append("=").append(var3.getValue()).append("; ");
         }

         var1.setLength(var1.length() - 2);
         var1.append("}");
      }

      this.appendTo(var1);
      return var1.append("]").toString();
   }

   void appendTo(StringBuilder var1) {
   }

   static void appendTo(StringBuilder var0, String var1, Reference<?> var2) {
      if (var2 != null) {
         appendTo(var0, var1, var2.get());
      }

   }

   static void appendTo(StringBuilder var0, String var1, Object var2) {
      if (var2 != null) {
         var0.append("; ").append(var1).append("=").append(var2);
      }

   }

   static void appendTo(StringBuilder var0, String var1, boolean var2) {
      if (var2) {
         var0.append("; ").append(var1);
      }

   }
}
