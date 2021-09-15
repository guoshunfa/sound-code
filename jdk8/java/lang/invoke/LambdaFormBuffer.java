package java.lang.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

final class LambdaFormBuffer {
   private int arity;
   private int length;
   private LambdaForm.Name[] names;
   private LambdaForm.Name[] originalNames;
   private byte flags;
   private int firstChange;
   private LambdaForm.Name resultName;
   private String debugName;
   private ArrayList<LambdaForm.Name> dups;
   private static final int F_TRANS = 16;
   private static final int F_OWNED = 3;

   LambdaFormBuffer(LambdaForm var1) {
      this.arity = var1.arity;
      this.setNames(var1.names);
      int var2 = var1.result;
      if (var2 == -2) {
         var2 = this.length - 1;
      }

      if (var2 >= 0 && var1.names[var2].type != LambdaForm.BasicType.V_TYPE) {
         this.resultName = var1.names[var2];
      }

      this.debugName = var1.debugName;

      assert var1.nameRefsAreLegal();

   }

   private LambdaForm lambdaForm() {
      assert !this.inTrans();

      return new LambdaForm(this.debugName, this.arity, this.nameArray(), this.resultIndex());
   }

   LambdaForm.Name name(int var1) {
      assert var1 < this.length;

      return this.names[var1];
   }

   LambdaForm.Name[] nameArray() {
      return (LambdaForm.Name[])Arrays.copyOf((Object[])this.names, this.length);
   }

   int resultIndex() {
      if (this.resultName == null) {
         return -1;
      } else {
         int var1 = indexOf(this.resultName, this.names);

         assert var1 >= 0;

         return var1;
      }
   }

   void setNames(LambdaForm.Name[] var1) {
      this.names = this.originalNames = var1;
      this.length = var1.length;
      this.flags = 0;
   }

   private boolean verifyArity() {
      int var1;
      for(var1 = 0; var1 < this.arity && var1 < this.firstChange; ++var1) {
         assert this.names[var1].isParam() : "#" + var1 + "=" + this.names[var1];
      }

      for(var1 = this.arity; var1 < this.length; ++var1) {
         assert !this.names[var1].isParam() : "#" + var1 + "=" + this.names[var1];
      }

      for(var1 = this.length; var1 < this.names.length; ++var1) {
         assert this.names[var1] == null : "#" + var1 + "=" + this.names[var1];
      }

      if (this.resultName != null) {
         assert this.names[var1] == this.resultName;

         assert this.names[var1] == this.resultName;
      }

      return true;
   }

   private boolean verifyFirstChange() {
      assert this.inTrans();

      for(int var1 = 0; var1 < this.length; ++var1) {
         if (this.names[var1] != this.originalNames[var1]) {
            assert this.firstChange == var1 : Arrays.asList(this.firstChange, var1, this.originalNames[var1].exprString(), Arrays.asList(this.names));

            return true;
         }
      }

      assert this.firstChange == this.length : Arrays.asList(this.firstChange, Arrays.asList(this.names));

      return true;
   }

   private static int indexOf(LambdaForm.NamedFunction var0, LambdaForm.NamedFunction[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] == var0) {
            return var2;
         }
      }

      return -1;
   }

   private static int indexOf(LambdaForm.Name var0, LambdaForm.Name[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] == var0) {
            return var2;
         }
      }

      return -1;
   }

   boolean inTrans() {
      return (this.flags & 16) != 0;
   }

   int ownedCount() {
      return this.flags & 3;
   }

   void growNames(int var1, int var2) {
      int var3 = this.length;
      int var4 = var3 + var2;
      int var5 = this.ownedCount();
      if (var5 == 0 || var4 > this.names.length) {
         this.names = (LambdaForm.Name[])Arrays.copyOf((Object[])this.names, (this.names.length + var2) * 5 / 4);
         if (var5 == 0) {
            ++this.flags;
            ++var5;

            assert this.ownedCount() == var5;
         }
      }

      if (this.originalNames != null && this.originalNames.length < this.names.length) {
         this.originalNames = (LambdaForm.Name[])Arrays.copyOf((Object[])this.originalNames, this.names.length);
         if (var5 == 1) {
            ++this.flags;
            ++var5;

            assert this.ownedCount() == var5;
         }
      }

      if (var2 != 0) {
         int var6 = var1 + var2;
         int var7 = var3 - var1;
         System.arraycopy(this.names, var1, this.names, var6, var7);
         Arrays.fill(this.names, var1, var6, (Object)null);
         if (this.originalNames != null) {
            System.arraycopy(this.originalNames, var1, this.originalNames, var6, var7);
            Arrays.fill(this.originalNames, var1, var6, (Object)null);
         }

         this.length = var4;
         if (this.firstChange >= var1) {
            this.firstChange += var2;
         }

      }
   }

   int lastIndexOf(LambdaForm.Name var1) {
      int var2 = -1;

      for(int var3 = 0; var3 < this.length; ++var3) {
         if (this.names[var3] == var1) {
            var2 = var3;
         }
      }

      return var2;
   }

   private void noteDuplicate(int var1, int var2) {
      LambdaForm.Name var3 = this.names[var1];

      assert var3 == this.names[var2];

      assert this.originalNames[var1] != null;

      assert this.originalNames[var2] == null || this.originalNames[var2] == var3;

      if (this.dups == null) {
         this.dups = new ArrayList();
      }

      this.dups.add(var3);
   }

   private void clearDuplicatesAndNulls() {
      if (this.dups != null) {
         assert this.ownedCount() >= 1;

         Iterator var1 = this.dups.iterator();

         while(true) {
            while(var1.hasNext()) {
               LambdaForm.Name var2 = (LambdaForm.Name)var1.next();

               for(int var3 = this.firstChange; var3 < this.length; ++var3) {
                  if (this.names[var3] == var2 && this.originalNames[var3] != var2) {
                     this.names[var3] = null;

                     assert Arrays.asList(this.names).contains(var2);
                     break;
                  }
               }
            }

            this.dups.clear();
            break;
         }
      }

      int var4 = this.length;

      for(int var5 = this.firstChange; var5 < this.length; ++var5) {
         if (this.names[var5] == null) {
            System.arraycopy(this.names, var5 + 1, this.names, var5, --this.length - var5);
            --var5;
         }
      }

      if (this.length < var4) {
         Arrays.fill(this.names, this.length, var4, (Object)null);
      }

      assert !Arrays.asList(this.names).subList(0, this.length).contains((Object)null);

   }

   void startEdit() {
      assert this.verifyArity();

      int var1 = this.ownedCount();

      assert !this.inTrans();

      this.flags = (byte)(this.flags | 16);
      LambdaForm.Name[] var2 = this.names;
      LambdaForm.Name[] var3 = var1 == 2 ? this.originalNames : null;

      assert var3 != var2;

      if (var3 != null && var3.length >= this.length) {
         this.names = this.copyNamesInto(var3);
      } else {
         this.names = (LambdaForm.Name[])Arrays.copyOf((Object[])var2, Math.max(this.length + 2, var2.length));
         if (var1 < 2) {
            ++this.flags;
         }

         assert this.ownedCount() == var1 + 1;
      }

      this.originalNames = var2;

      assert this.originalNames != this.names;

      this.firstChange = this.length;

      assert this.inTrans();

   }

   private void changeName(int var1, LambdaForm.Name var2) {
      assert this.inTrans();

      assert var1 < this.length;

      LambdaForm.Name var3 = this.names[var1];

      assert var3 == this.originalNames[var1];

      assert this.verifyFirstChange();

      if (this.ownedCount() == 0) {
         this.growNames(0, 0);
      }

      this.names[var1] = var2;
      if (this.firstChange > var1) {
         this.firstChange = var1;
      }

      if (this.resultName != null && this.resultName == var3) {
         this.resultName = var2;
      }

   }

   void setResult(LambdaForm.Name var1) {
      assert var1 == null || this.lastIndexOf(var1) >= 0;

      this.resultName = var1;
   }

   LambdaForm endEdit() {
      assert this.verifyFirstChange();

      for(int var1 = Math.max(this.firstChange, this.arity); var1 < this.length; ++var1) {
         LambdaForm.Name var2 = this.names[var1];
         if (var2 != null) {
            LambdaForm.Name var3 = var2.replaceNames(this.originalNames, this.names, this.firstChange, var1);
            if (var3 != var2) {
               this.names[var1] = var3;
               if (this.resultName == var2) {
                  this.resultName = var3;
               }
            }
         }
      }

      assert this.inTrans();

      this.flags &= -17;
      this.clearDuplicatesAndNulls();
      this.originalNames = null;
      if (this.firstChange < this.arity) {
         LambdaForm.Name[] var6 = new LambdaForm.Name[this.arity - this.firstChange];
         int var7 = this.firstChange;
         int var8 = 0;

         for(int var4 = this.firstChange; var4 < this.arity; ++var4) {
            LambdaForm.Name var5 = this.names[var4];
            if (var5.isParam()) {
               this.names[var7++] = var5;
            } else {
               var6[var8++] = var5;
            }
         }

         assert var8 == this.arity - var7;

         System.arraycopy(var6, 0, this.names, var7, var8);
         this.arity -= var8;
      }

      assert this.verifyArity();

      return this.lambdaForm();
   }

   private LambdaForm.Name[] copyNamesInto(LambdaForm.Name[] var1) {
      System.arraycopy(this.names, 0, var1, 0, this.length);
      Arrays.fill(var1, this.length, var1.length, (Object)null);
      return var1;
   }

   LambdaFormBuffer replaceFunctions(LambdaForm.NamedFunction[] var1, LambdaForm.NamedFunction[] var2, Object... var3) {
      assert this.inTrans();

      if (var1.length == 0) {
         return this;
      } else {
         for(int var4 = this.arity; var4 < this.length; ++var4) {
            LambdaForm.Name var5 = this.names[var4];
            int var6 = indexOf(var5.function, var1);
            if (var6 >= 0 && Arrays.equals(var5.arguments, var3)) {
               this.changeName(var4, new LambdaForm.Name(var2[var6], var5.arguments));
            }
         }

         return this;
      }
   }

   private void replaceName(int var1, LambdaForm.Name var2) {
      assert this.inTrans();

      assert this.verifyArity();

      assert var1 < this.arity;

      LambdaForm.Name var3 = this.names[var1];

      assert var3.isParam();

      assert var3.type == var2.type;

      this.changeName(var1, var2);
   }

   LambdaFormBuffer renameParameter(int var1, LambdaForm.Name var2) {
      assert var2.isParam();

      this.replaceName(var1, var2);
      return this;
   }

   LambdaFormBuffer replaceParameterByNewExpression(int var1, LambdaForm.Name var2) {
      assert !var2.isParam();

      assert this.lastIndexOf(var2) < 0;

      this.replaceName(var1, var2);
      return this;
   }

   LambdaFormBuffer replaceParameterByCopy(int var1, int var2) {
      assert var1 != var2;

      this.replaceName(var1, this.names[var2]);
      this.noteDuplicate(var1, var2);
      return this;
   }

   private void insertName(int var1, LambdaForm.Name var2, boolean var3) {
      assert this.inTrans();

      assert this.verifyArity();

      if (!$assertionsDisabled) {
         label38: {
            if (var3) {
               if (var1 <= this.arity) {
                  break label38;
               }
            } else if (var1 >= this.arity) {
               break label38;
            }

            throw new AssertionError();
         }
      }

      this.growNames(var1, 1);
      if (var3) {
         ++this.arity;
      }

      this.changeName(var1, var2);
   }

   LambdaFormBuffer insertExpression(int var1, LambdaForm.Name var2) {
      assert !var2.isParam();

      this.insertName(var1, var2, false);
      return this;
   }

   LambdaFormBuffer insertParameter(int var1, LambdaForm.Name var2) {
      assert var2.isParam();

      this.insertName(var1, var2, true);
      return this;
   }
}
