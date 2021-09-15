package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TypeKindVisitor6<R, P> extends SimpleTypeVisitor6<R, P> {
   protected TypeKindVisitor6() {
      super((Object)null);
   }

   protected TypeKindVisitor6(R var1) {
      super(var1);
   }

   public R visitPrimitive(PrimitiveType var1, P var2) {
      TypeKind var3 = var1.getKind();
      switch(var3) {
      case BOOLEAN:
         return this.visitPrimitiveAsBoolean(var1, var2);
      case BYTE:
         return this.visitPrimitiveAsByte(var1, var2);
      case SHORT:
         return this.visitPrimitiveAsShort(var1, var2);
      case INT:
         return this.visitPrimitiveAsInt(var1, var2);
      case LONG:
         return this.visitPrimitiveAsLong(var1, var2);
      case CHAR:
         return this.visitPrimitiveAsChar(var1, var2);
      case FLOAT:
         return this.visitPrimitiveAsFloat(var1, var2);
      case DOUBLE:
         return this.visitPrimitiveAsDouble(var1, var2);
      default:
         throw new AssertionError("Bad kind " + var3 + " for PrimitiveType" + var1);
      }
   }

   public R visitPrimitiveAsBoolean(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsByte(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsShort(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsInt(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsLong(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsChar(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsFloat(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitPrimitiveAsDouble(PrimitiveType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitNoType(NoType var1, P var2) {
      TypeKind var3 = var1.getKind();
      switch(var3) {
      case VOID:
         return this.visitNoTypeAsVoid(var1, var2);
      case PACKAGE:
         return this.visitNoTypeAsPackage(var1, var2);
      case NONE:
         return this.visitNoTypeAsNone(var1, var2);
      default:
         throw new AssertionError("Bad kind " + var3 + " for NoType" + var1);
      }
   }

   public R visitNoTypeAsVoid(NoType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitNoTypeAsPackage(NoType var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitNoTypeAsNone(NoType var1, P var2) {
      return this.defaultAction(var1, var2);
   }
}
