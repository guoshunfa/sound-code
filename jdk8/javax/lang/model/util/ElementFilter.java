package javax.lang.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ElementFilter {
   private static final Set<ElementKind> CONSTRUCTOR_KIND;
   private static final Set<ElementKind> FIELD_KINDS;
   private static final Set<ElementKind> METHOD_KIND;
   private static final Set<ElementKind> PACKAGE_KIND;
   private static final Set<ElementKind> TYPE_KINDS;

   private ElementFilter() {
   }

   public static List<VariableElement> fieldsIn(Iterable<? extends Element> var0) {
      return listFilter(var0, FIELD_KINDS, VariableElement.class);
   }

   public static Set<VariableElement> fieldsIn(Set<? extends Element> var0) {
      return setFilter(var0, FIELD_KINDS, VariableElement.class);
   }

   public static List<ExecutableElement> constructorsIn(Iterable<? extends Element> var0) {
      return listFilter(var0, CONSTRUCTOR_KIND, ExecutableElement.class);
   }

   public static Set<ExecutableElement> constructorsIn(Set<? extends Element> var0) {
      return setFilter(var0, CONSTRUCTOR_KIND, ExecutableElement.class);
   }

   public static List<ExecutableElement> methodsIn(Iterable<? extends Element> var0) {
      return listFilter(var0, METHOD_KIND, ExecutableElement.class);
   }

   public static Set<ExecutableElement> methodsIn(Set<? extends Element> var0) {
      return setFilter(var0, METHOD_KIND, ExecutableElement.class);
   }

   public static List<TypeElement> typesIn(Iterable<? extends Element> var0) {
      return listFilter(var0, TYPE_KINDS, TypeElement.class);
   }

   public static Set<TypeElement> typesIn(Set<? extends Element> var0) {
      return setFilter(var0, TYPE_KINDS, TypeElement.class);
   }

   public static List<PackageElement> packagesIn(Iterable<? extends Element> var0) {
      return listFilter(var0, PACKAGE_KIND, PackageElement.class);
   }

   public static Set<PackageElement> packagesIn(Set<? extends Element> var0) {
      return setFilter(var0, PACKAGE_KIND, PackageElement.class);
   }

   private static <E extends Element> List<E> listFilter(Iterable<? extends Element> var0, Set<ElementKind> var1, Class<E> var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var0.iterator();

      while(var4.hasNext()) {
         Element var5 = (Element)var4.next();
         if (var1.contains(var5.getKind())) {
            var3.add(var2.cast(var5));
         }
      }

      return var3;
   }

   private static <E extends Element> Set<E> setFilter(Set<? extends Element> var0, Set<ElementKind> var1, Class<E> var2) {
      LinkedHashSet var3 = new LinkedHashSet();
      Iterator var4 = var0.iterator();

      while(var4.hasNext()) {
         Element var5 = (Element)var4.next();
         if (var1.contains(var5.getKind())) {
            var3.add(var2.cast(var5));
         }
      }

      return var3;
   }

   static {
      CONSTRUCTOR_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.CONSTRUCTOR));
      FIELD_KINDS = Collections.unmodifiableSet(EnumSet.of(ElementKind.FIELD, (Enum)ElementKind.ENUM_CONSTANT));
      METHOD_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.METHOD));
      PACKAGE_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.PACKAGE));
      TYPE_KINDS = Collections.unmodifiableSet(EnumSet.of(ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE, ElementKind.ANNOTATION_TYPE));
   }
}
