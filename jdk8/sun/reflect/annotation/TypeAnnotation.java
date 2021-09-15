package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class TypeAnnotation {
   private final TypeAnnotation.TypeAnnotationTargetInfo targetInfo;
   private final TypeAnnotation.LocationInfo loc;
   private final Annotation annotation;
   private final AnnotatedElement baseDeclaration;

   public TypeAnnotation(TypeAnnotation.TypeAnnotationTargetInfo var1, TypeAnnotation.LocationInfo var2, Annotation var3, AnnotatedElement var4) {
      this.targetInfo = var1;
      this.loc = var2;
      this.annotation = var3;
      this.baseDeclaration = var4;
   }

   public TypeAnnotation.TypeAnnotationTargetInfo getTargetInfo() {
      return this.targetInfo;
   }

   public Annotation getAnnotation() {
      return this.annotation;
   }

   public AnnotatedElement getBaseDeclaration() {
      return this.baseDeclaration;
   }

   public TypeAnnotation.LocationInfo getLocationInfo() {
      return this.loc;
   }

   public static List<TypeAnnotation> filter(TypeAnnotation[] var0, TypeAnnotation.TypeAnnotationTarget var1) {
      ArrayList var2 = new ArrayList(var0.length);
      TypeAnnotation[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TypeAnnotation var6 = var3[var5];
         if (var6.getTargetInfo().getTarget() == var1) {
            var2.add(var6);
         }
      }

      var2.trimToSize();
      return var2;
   }

   public String toString() {
      return this.annotation.toString() + " with Targetnfo: " + this.targetInfo.toString() + " on base declaration: " + this.baseDeclaration.toString();
   }

   public static final class LocationInfo {
      private final int depth;
      private final TypeAnnotation.LocationInfo.Location[] locations;
      public static final TypeAnnotation.LocationInfo BASE_LOCATION = new TypeAnnotation.LocationInfo();

      private LocationInfo() {
         this(0, new TypeAnnotation.LocationInfo.Location[0]);
      }

      private LocationInfo(int var1, TypeAnnotation.LocationInfo.Location[] var2) {
         this.depth = var1;
         this.locations = var2;
      }

      public static TypeAnnotation.LocationInfo parseLocationInfo(ByteBuffer var0) {
         int var1 = var0.get() & 255;
         if (var1 == 0) {
            return BASE_LOCATION;
         } else {
            TypeAnnotation.LocationInfo.Location[] var2 = new TypeAnnotation.LocationInfo.Location[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
               byte var4 = var0.get();
               short var5 = (short)(var0.get() & 255);
               if (var4 != 0 && !(var4 == 1 | var4 == 2) && var4 != 3) {
                  throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
               }

               if (var4 != 3 && var5 != 0) {
                  throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
               }

               var2[var3] = new TypeAnnotation.LocationInfo.Location(var4, var5);
            }

            return new TypeAnnotation.LocationInfo(var1, var2);
         }
      }

      public TypeAnnotation.LocationInfo pushArray() {
         return this.pushLocation((byte)0, (short)0);
      }

      public TypeAnnotation.LocationInfo pushInner() {
         return this.pushLocation((byte)1, (short)0);
      }

      public TypeAnnotation.LocationInfo pushWildcard() {
         return this.pushLocation((byte)2, (short)0);
      }

      public TypeAnnotation.LocationInfo pushTypeArg(short var1) {
         return this.pushLocation((byte)3, var1);
      }

      public TypeAnnotation.LocationInfo pushLocation(byte var1, short var2) {
         int var3 = this.depth + 1;
         TypeAnnotation.LocationInfo.Location[] var4 = new TypeAnnotation.LocationInfo.Location[var3];
         System.arraycopy(this.locations, 0, var4, 0, this.depth);
         var4[var3 - 1] = new TypeAnnotation.LocationInfo.Location(var1, (short)(var2 & 255));
         return new TypeAnnotation.LocationInfo(var3, var4);
      }

      public TypeAnnotation[] filter(TypeAnnotation[] var1) {
         ArrayList var2 = new ArrayList(var1.length);
         TypeAnnotation[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            TypeAnnotation var6 = var3[var5];
            if (this.isSameLocationInfo(var6.getLocationInfo())) {
               var2.add(var6);
            }
         }

         return (TypeAnnotation[])var2.toArray(new TypeAnnotation[0]);
      }

      boolean isSameLocationInfo(TypeAnnotation.LocationInfo var1) {
         if (this.depth != var1.depth) {
            return false;
         } else {
            for(int var2 = 0; var2 < this.depth; ++var2) {
               if (!this.locations[var2].isSameLocation(var1.locations[var2])) {
                  return false;
               }
            }

            return true;
         }
      }

      public static final class Location {
         public final byte tag;
         public final short index;

         boolean isSameLocation(TypeAnnotation.LocationInfo.Location var1) {
            return this.tag == var1.tag && this.index == var1.index;
         }

         public Location(byte var1, short var2) {
            this.tag = var1;
            this.index = var2;
         }
      }
   }

   public static final class TypeAnnotationTargetInfo {
      private final TypeAnnotation.TypeAnnotationTarget target;
      private final int count;
      private final int secondaryIndex;
      private static final int UNUSED_INDEX = -2;

      public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget var1) {
         this(var1, -2, -2);
      }

      public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget var1, int var2) {
         this(var1, var2, -2);
      }

      public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget var1, int var2, int var3) {
         this.target = var1;
         this.count = var2;
         this.secondaryIndex = var3;
      }

      public TypeAnnotation.TypeAnnotationTarget getTarget() {
         return this.target;
      }

      public int getCount() {
         return this.count;
      }

      public int getSecondaryIndex() {
         return this.secondaryIndex;
      }

      public String toString() {
         return "" + this.target + ": " + this.count + ", " + this.secondaryIndex;
      }
   }

   public static enum TypeAnnotationTarget {
      CLASS_TYPE_PARAMETER,
      METHOD_TYPE_PARAMETER,
      CLASS_EXTENDS,
      CLASS_IMPLEMENTS,
      CLASS_TYPE_PARAMETER_BOUND,
      METHOD_TYPE_PARAMETER_BOUND,
      FIELD,
      METHOD_RETURN,
      METHOD_RECEIVER,
      METHOD_FORMAL_PARAMETER,
      THROWS;
   }
}
