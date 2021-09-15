package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class InfoFromMemberName implements MethodHandleInfo {
   private final MemberName member;
   private final int referenceKind;

   InfoFromMemberName(MethodHandles.Lookup var1, MemberName var2, byte var3) {
      assert var2.isResolved() || var2.isMethodHandleInvoke();

      assert var2.referenceKindIsConsistentWith(var3);

      this.member = var2;
      this.referenceKind = var3;
   }

   public Class<?> getDeclaringClass() {
      return this.member.getDeclaringClass();
   }

   public String getName() {
      return this.member.getName();
   }

   public MethodType getMethodType() {
      return this.member.getMethodOrFieldType();
   }

   public int getModifiers() {
      return this.member.getModifiers();
   }

   public int getReferenceKind() {
      return this.referenceKind;
   }

   public String toString() {
      return MethodHandleInfo.toString(this.getReferenceKind(), this.getDeclaringClass(), this.getName(), this.getMethodType());
   }

   public <T extends Member> T reflectAs(Class<T> var1, MethodHandles.Lookup var2) {
      if (this.member.isMethodHandleInvoke() && !this.member.isVarargs()) {
         throw new IllegalArgumentException("cannot reflect signature polymorphic method");
      } else {
         Member var3 = (Member)AccessController.doPrivileged(new PrivilegedAction<Member>() {
            public Member run() {
               try {
                  return InfoFromMemberName.this.reflectUnchecked();
               } catch (ReflectiveOperationException var2) {
                  throw new IllegalArgumentException(var2);
               }
            }
         });

         try {
            Class var4 = this.getDeclaringClass();
            byte var5 = (byte)this.getReferenceKind();
            var2.checkAccess(var5, var4, convertToMemberName(var5, var3));
         } catch (IllegalAccessException var6) {
            throw new IllegalArgumentException(var6);
         }

         return (Member)var1.cast(var3);
      }
   }

   private Member reflectUnchecked() throws ReflectiveOperationException {
      byte var1 = (byte)this.getReferenceKind();
      Class var2 = this.getDeclaringClass();
      boolean var3 = Modifier.isPublic(this.getModifiers());
      if (MethodHandleNatives.refKindIsMethod(var1)) {
         return var3 ? var2.getMethod(this.getName(), this.getMethodType().parameterArray()) : var2.getDeclaredMethod(this.getName(), this.getMethodType().parameterArray());
      } else if (MethodHandleNatives.refKindIsConstructor(var1)) {
         return var3 ? var2.getConstructor(this.getMethodType().parameterArray()) : var2.getDeclaredConstructor(this.getMethodType().parameterArray());
      } else if (MethodHandleNatives.refKindIsField(var1)) {
         return var3 ? var2.getField(this.getName()) : var2.getDeclaredField(this.getName());
      } else {
         throw new IllegalArgumentException("referenceKind=" + var1);
      }
   }

   private static MemberName convertToMemberName(byte var0, Member var1) throws IllegalAccessException {
      boolean var2;
      if (var1 instanceof Method) {
         var2 = var0 == 7;
         return new MemberName((Method)var1, var2);
      } else if (var1 instanceof Constructor) {
         return new MemberName((Constructor)var1);
      } else if (!(var1 instanceof Field)) {
         throw new InternalError(var1.getClass().getName());
      } else {
         var2 = var0 == 3 || var0 == 4;
         return new MemberName((Field)var1, var2);
      }
   }
}
