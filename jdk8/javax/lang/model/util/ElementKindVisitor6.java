package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ElementKindVisitor6<R, P> extends SimpleElementVisitor6<R, P> {
   protected ElementKindVisitor6() {
      super((Object)null);
   }

   protected ElementKindVisitor6(R var1) {
      super(var1);
   }

   public R visitPackage(PackageElement var1, P var2) {
      assert var1.getKind() == ElementKind.PACKAGE : "Bad kind on PackageElement";

      return this.defaultAction(var1, var2);
   }

   public R visitType(TypeElement var1, P var2) {
      ElementKind var3 = var1.getKind();
      switch(var3) {
      case ANNOTATION_TYPE:
         return this.visitTypeAsAnnotationType(var1, var2);
      case CLASS:
         return this.visitTypeAsClass(var1, var2);
      case ENUM:
         return this.visitTypeAsEnum(var1, var2);
      case INTERFACE:
         return this.visitTypeAsInterface(var1, var2);
      default:
         throw new AssertionError("Bad kind " + var3 + " for TypeElement" + var1);
      }
   }

   public R visitTypeAsAnnotationType(TypeElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitTypeAsClass(TypeElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitTypeAsEnum(TypeElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitTypeAsInterface(TypeElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariable(VariableElement var1, P var2) {
      ElementKind var3 = var1.getKind();
      switch(var3) {
      case ENUM_CONSTANT:
         return this.visitVariableAsEnumConstant(var1, var2);
      case EXCEPTION_PARAMETER:
         return this.visitVariableAsExceptionParameter(var1, var2);
      case FIELD:
         return this.visitVariableAsField(var1, var2);
      case LOCAL_VARIABLE:
         return this.visitVariableAsLocalVariable(var1, var2);
      case PARAMETER:
         return this.visitVariableAsParameter(var1, var2);
      case RESOURCE_VARIABLE:
         return this.visitVariableAsResourceVariable(var1, var2);
      default:
         throw new AssertionError("Bad kind " + var3 + " for VariableElement" + var1);
      }
   }

   public R visitVariableAsEnumConstant(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariableAsExceptionParameter(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariableAsField(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariableAsLocalVariable(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariableAsParameter(VariableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitVariableAsResourceVariable(VariableElement var1, P var2) {
      return this.visitUnknown(var1, var2);
   }

   public R visitExecutable(ExecutableElement var1, P var2) {
      ElementKind var3 = var1.getKind();
      switch(var3) {
      case CONSTRUCTOR:
         return this.visitExecutableAsConstructor(var1, var2);
      case INSTANCE_INIT:
         return this.visitExecutableAsInstanceInit(var1, var2);
      case METHOD:
         return this.visitExecutableAsMethod(var1, var2);
      case STATIC_INIT:
         return this.visitExecutableAsStaticInit(var1, var2);
      default:
         throw new AssertionError("Bad kind " + var3 + " for ExecutableElement" + var1);
      }
   }

   public R visitExecutableAsConstructor(ExecutableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitExecutableAsInstanceInit(ExecutableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitExecutableAsMethod(ExecutableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitExecutableAsStaticInit(ExecutableElement var1, P var2) {
      return this.defaultAction(var1, var2);
   }

   public R visitTypeParameter(TypeParameterElement var1, P var2) {
      assert var1.getKind() == ElementKind.TYPE_PARAMETER : "Bad kind on TypeParameterElement";

      return this.defaultAction(var1, var2);
   }
}
