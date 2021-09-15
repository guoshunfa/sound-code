package sun.reflect.generics.parser;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.List;
import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.BaseType;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.ReturnType;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public class SignatureParser {
   private char[] input;
   private int index = 0;
   private static final char EOI = ':';
   private static final boolean DEBUG = false;

   private SignatureParser() {
   }

   private char getNext() {
      assert this.index <= this.input.length;

      try {
         return this.input[this.index++];
      } catch (ArrayIndexOutOfBoundsException var2) {
         return ':';
      }
   }

   private char current() {
      assert this.index <= this.input.length;

      try {
         return this.input[this.index];
      } catch (ArrayIndexOutOfBoundsException var2) {
         return ':';
      }
   }

   private void advance() {
      assert this.index <= this.input.length;

      ++this.index;
   }

   private String remainder() {
      return new String(this.input, this.index, this.input.length - this.index);
   }

   private boolean matches(char var1, char... var2) {
      char[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var3[var5];
         if (var1 == var6) {
            return true;
         }
      }

      return false;
   }

   private Error error(String var1) {
      return new GenericSignatureFormatError("Signature Parse error: " + var1 + "\n\tRemaining input: " + this.remainder());
   }

   private void progress(int var1) {
      if (this.index <= var1) {
         throw this.error("Failure to make progress!");
      }
   }

   public static SignatureParser make() {
      return new SignatureParser();
   }

   public ClassSignature parseClassSig(String var1) {
      this.input = var1.toCharArray();
      return this.parseClassSignature();
   }

   public MethodTypeSignature parseMethodSig(String var1) {
      this.input = var1.toCharArray();
      return this.parseMethodTypeSignature();
   }

   public TypeSignature parseTypeSig(String var1) {
      this.input = var1.toCharArray();
      return this.parseTypeSignature();
   }

   private ClassSignature parseClassSignature() {
      assert this.index == 0;

      return ClassSignature.make(this.parseZeroOrMoreFormalTypeParameters(), this.parseClassTypeSignature(), this.parseSuperInterfaces());
   }

   private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters() {
      return this.current() == '<' ? this.parseFormalTypeParameters() : new FormalTypeParameter[0];
   }

   private FormalTypeParameter[] parseFormalTypeParameters() {
      ArrayList var1 = new ArrayList(3);

      assert this.current() == '<';

      if (this.current() != '<') {
         throw this.error("expected '<'");
      } else {
         this.advance();
         var1.add(this.parseFormalTypeParameter());

         while(this.current() != '>') {
            int var2 = this.index;
            var1.add(this.parseFormalTypeParameter());
            this.progress(var2);
         }

         this.advance();
         return (FormalTypeParameter[])var1.toArray(new FormalTypeParameter[var1.size()]);
      }
   }

   private FormalTypeParameter parseFormalTypeParameter() {
      String var1 = this.parseIdentifier();
      FieldTypeSignature[] var2 = this.parseBounds();
      return FormalTypeParameter.make(var1, var2);
   }

   private String parseIdentifier() {
      StringBuilder var1 = new StringBuilder();

      while(!Character.isWhitespace(this.current())) {
         char var2 = this.current();
         switch(var2) {
         case '.':
         case '/':
         case ':':
         case ';':
         case '<':
         case '>':
         case '[':
            return var1.toString();
         default:
            var1.append(var2);
            this.advance();
         }
      }

      return var1.toString();
   }

   private FieldTypeSignature parseFieldTypeSignature() {
      return this.parseFieldTypeSignature(true);
   }

   private FieldTypeSignature parseFieldTypeSignature(boolean var1) {
      switch(this.current()) {
      case 'L':
         return this.parseClassTypeSignature();
      case 'T':
         return this.parseTypeVariableSignature();
      case '[':
         if (var1) {
            return this.parseArrayTypeSignature();
         }

         throw this.error("Array signature not allowed here.");
      default:
         throw this.error("Expected Field Type Signature");
      }
   }

   private ClassTypeSignature parseClassTypeSignature() {
      assert this.current() == 'L';

      if (this.current() != 'L') {
         throw this.error("expected a class type");
      } else {
         this.advance();
         ArrayList var1 = new ArrayList(5);
         var1.add(this.parsePackageNameAndSimpleClassTypeSignature());
         this.parseClassTypeSignatureSuffix(var1);
         if (this.current() != ';') {
            throw this.error("expected ';' got '" + this.current() + "'");
         } else {
            this.advance();
            return ClassTypeSignature.make(var1);
         }
      }
   }

   private SimpleClassTypeSignature parsePackageNameAndSimpleClassTypeSignature() {
      String var1 = this.parseIdentifier();
      if (this.current() == '/') {
         StringBuilder var2 = new StringBuilder(var1);

         while(this.current() == '/') {
            this.advance();
            var2.append(".");
            var2.append(this.parseIdentifier());
         }

         var1 = var2.toString();
      }

      switch(this.current()) {
      case ';':
         return SimpleClassTypeSignature.make(var1, false, new TypeArgument[0]);
      case '<':
         return SimpleClassTypeSignature.make(var1, false, this.parseTypeArguments());
      default:
         throw this.error("expected '<' or ';' but got " + this.current());
      }
   }

   private SimpleClassTypeSignature parseSimpleClassTypeSignature(boolean var1) {
      String var2 = this.parseIdentifier();
      char var3 = this.current();
      switch(var3) {
      case '.':
      case ';':
         return SimpleClassTypeSignature.make(var2, var1, new TypeArgument[0]);
      case '<':
         return SimpleClassTypeSignature.make(var2, var1, this.parseTypeArguments());
      default:
         throw this.error("expected '<' or ';' or '.', got '" + var3 + "'.");
      }
   }

   private void parseClassTypeSignatureSuffix(List<SimpleClassTypeSignature> var1) {
      while(this.current() == '.') {
         this.advance();
         var1.add(this.parseSimpleClassTypeSignature(true));
      }

   }

   private TypeArgument[] parseTypeArgumentsOpt() {
      return this.current() == '<' ? this.parseTypeArguments() : new TypeArgument[0];
   }

   private TypeArgument[] parseTypeArguments() {
      ArrayList var1 = new ArrayList(3);

      assert this.current() == '<';

      if (this.current() != '<') {
         throw this.error("expected '<'");
      } else {
         this.advance();
         var1.add(this.parseTypeArgument());

         while(this.current() != '>') {
            var1.add(this.parseTypeArgument());
         }

         this.advance();
         return (TypeArgument[])var1.toArray(new TypeArgument[var1.size()]);
      }
   }

   private TypeArgument parseTypeArgument() {
      FieldTypeSignature[] var1 = new FieldTypeSignature[1];
      FieldTypeSignature[] var2 = new FieldTypeSignature[1];
      TypeArgument[] var3 = new TypeArgument[0];
      char var4 = this.current();
      switch(var4) {
      case '*':
         this.advance();
         var1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, var3);
         var2[0] = BottomSignature.make();
         return Wildcard.make(var1, var2);
      case '+':
         this.advance();
         var1[0] = this.parseFieldTypeSignature();
         var2[0] = BottomSignature.make();
         return Wildcard.make(var1, var2);
      case ',':
      default:
         return this.parseFieldTypeSignature();
      case '-':
         this.advance();
         var2[0] = this.parseFieldTypeSignature();
         var1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, var3);
         return Wildcard.make(var1, var2);
      }
   }

   private TypeVariableSignature parseTypeVariableSignature() {
      assert this.current() == 'T';

      if (this.current() != 'T') {
         throw this.error("expected a type variable usage");
      } else {
         this.advance();
         TypeVariableSignature var1 = TypeVariableSignature.make(this.parseIdentifier());
         if (this.current() != ';') {
            throw this.error("; expected in signature of type variable named" + var1.getIdentifier());
         } else {
            this.advance();
            return var1;
         }
      }
   }

   private ArrayTypeSignature parseArrayTypeSignature() {
      if (this.current() != '[') {
         throw this.error("expected array type signature");
      } else {
         this.advance();
         return ArrayTypeSignature.make(this.parseTypeSignature());
      }
   }

   private TypeSignature parseTypeSignature() {
      switch(this.current()) {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'Z':
         return this.parseBaseType();
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      default:
         return this.parseFieldTypeSignature();
      }
   }

   private BaseType parseBaseType() {
      switch(this.current()) {
      case 'B':
         this.advance();
         return ByteSignature.make();
      case 'C':
         this.advance();
         return CharSignature.make();
      case 'D':
         this.advance();
         return DoubleSignature.make();
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      default:
         assert false;

         throw this.error("expected primitive type");
      case 'F':
         this.advance();
         return FloatSignature.make();
      case 'I':
         this.advance();
         return IntSignature.make();
      case 'J':
         this.advance();
         return LongSignature.make();
      case 'S':
         this.advance();
         return ShortSignature.make();
      case 'Z':
         this.advance();
         return BooleanSignature.make();
      }
   }

   private FieldTypeSignature[] parseBounds() {
      ArrayList var1 = new ArrayList(3);
      if (this.current() == ':') {
         this.advance();
         switch(this.current()) {
         case ':':
            break;
         default:
            var1.add(this.parseFieldTypeSignature());
         }

         while(this.current() == ':') {
            this.advance();
            var1.add(this.parseFieldTypeSignature());
         }
      } else {
         this.error("Bound expected");
      }

      return (FieldTypeSignature[])var1.toArray(new FieldTypeSignature[var1.size()]);
   }

   private ClassTypeSignature[] parseSuperInterfaces() {
      ArrayList var1 = new ArrayList(5);

      while(this.current() == 'L') {
         var1.add(this.parseClassTypeSignature());
      }

      return (ClassTypeSignature[])var1.toArray(new ClassTypeSignature[var1.size()]);
   }

   private MethodTypeSignature parseMethodTypeSignature() {
      assert this.index == 0;

      return MethodTypeSignature.make(this.parseZeroOrMoreFormalTypeParameters(), this.parseFormalParameters(), this.parseReturnType(), this.parseZeroOrMoreThrowsSignatures());
   }

   private TypeSignature[] parseFormalParameters() {
      if (this.current() != '(') {
         throw this.error("expected '('");
      } else {
         this.advance();
         TypeSignature[] var1 = this.parseZeroOrMoreTypeSignatures();
         if (this.current() != ')') {
            throw this.error("expected ')'");
         } else {
            this.advance();
            return var1;
         }
      }
   }

   private TypeSignature[] parseZeroOrMoreTypeSignatures() {
      ArrayList var1 = new ArrayList();
      boolean var2 = false;

      while(!var2) {
         switch(this.current()) {
         case 'B':
         case 'C':
         case 'D':
         case 'F':
         case 'I':
         case 'J':
         case 'L':
         case 'S':
         case 'T':
         case 'Z':
         case '[':
            var1.add(this.parseTypeSignature());
            break;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         default:
            var2 = true;
         }
      }

      return (TypeSignature[])var1.toArray(new TypeSignature[var1.size()]);
   }

   private ReturnType parseReturnType() {
      if (this.current() == 'V') {
         this.advance();
         return VoidDescriptor.make();
      } else {
         return this.parseTypeSignature();
      }
   }

   private FieldTypeSignature[] parseZeroOrMoreThrowsSignatures() {
      ArrayList var1 = new ArrayList(3);

      while(this.current() == '^') {
         var1.add(this.parseThrowsSignature());
      }

      return (FieldTypeSignature[])var1.toArray(new FieldTypeSignature[var1.size()]);
   }

   private FieldTypeSignature parseThrowsSignature() {
      assert this.current() == '^';

      if (this.current() != '^') {
         throw this.error("expected throws signature");
      } else {
         this.advance();
         return this.parseFieldTypeSignature(false);
      }
   }
}
