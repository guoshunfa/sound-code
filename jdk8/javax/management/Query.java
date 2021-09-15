package javax.management;

public class Query {
   public static final int GT = 0;
   public static final int LT = 1;
   public static final int GE = 2;
   public static final int LE = 3;
   public static final int EQ = 4;
   public static final int PLUS = 0;
   public static final int MINUS = 1;
   public static final int TIMES = 2;
   public static final int DIV = 3;

   public static QueryExp and(QueryExp var0, QueryExp var1) {
      return new AndQueryExp(var0, var1);
   }

   public static QueryExp or(QueryExp var0, QueryExp var1) {
      return new OrQueryExp(var0, var1);
   }

   public static QueryExp gt(ValueExp var0, ValueExp var1) {
      return new BinaryRelQueryExp(0, var0, var1);
   }

   public static QueryExp geq(ValueExp var0, ValueExp var1) {
      return new BinaryRelQueryExp(2, var0, var1);
   }

   public static QueryExp leq(ValueExp var0, ValueExp var1) {
      return new BinaryRelQueryExp(3, var0, var1);
   }

   public static QueryExp lt(ValueExp var0, ValueExp var1) {
      return new BinaryRelQueryExp(1, var0, var1);
   }

   public static QueryExp eq(ValueExp var0, ValueExp var1) {
      return new BinaryRelQueryExp(4, var0, var1);
   }

   public static QueryExp between(ValueExp var0, ValueExp var1, ValueExp var2) {
      return new BetweenQueryExp(var0, var1, var2);
   }

   public static QueryExp match(AttributeValueExp var0, StringValueExp var1) {
      return new MatchQueryExp(var0, var1);
   }

   public static AttributeValueExp attr(String var0) {
      return new AttributeValueExp(var0);
   }

   public static AttributeValueExp attr(String var0, String var1) {
      return new QualifiedAttributeValueExp(var0, var1);
   }

   public static AttributeValueExp classattr() {
      return new ClassAttributeValueExp();
   }

   public static QueryExp not(QueryExp var0) {
      return new NotQueryExp(var0);
   }

   public static QueryExp in(ValueExp var0, ValueExp[] var1) {
      return new InQueryExp(var0, var1);
   }

   public static StringValueExp value(String var0) {
      return new StringValueExp(var0);
   }

   public static ValueExp value(Number var0) {
      return new NumericValueExp(var0);
   }

   public static ValueExp value(int var0) {
      return new NumericValueExp((long)var0);
   }

   public static ValueExp value(long var0) {
      return new NumericValueExp(var0);
   }

   public static ValueExp value(float var0) {
      return new NumericValueExp((double)var0);
   }

   public static ValueExp value(double var0) {
      return new NumericValueExp(var0);
   }

   public static ValueExp value(boolean var0) {
      return new BooleanValueExp(var0);
   }

   public static ValueExp plus(ValueExp var0, ValueExp var1) {
      return new BinaryOpValueExp(0, var0, var1);
   }

   public static ValueExp times(ValueExp var0, ValueExp var1) {
      return new BinaryOpValueExp(2, var0, var1);
   }

   public static ValueExp minus(ValueExp var0, ValueExp var1) {
      return new BinaryOpValueExp(1, var0, var1);
   }

   public static ValueExp div(ValueExp var0, ValueExp var1) {
      return new BinaryOpValueExp(3, var0, var1);
   }

   public static QueryExp initialSubString(AttributeValueExp var0, StringValueExp var1) {
      return new MatchQueryExp(var0, new StringValueExp(escapeString(var1.getValue()) + "*"));
   }

   public static QueryExp anySubString(AttributeValueExp var0, StringValueExp var1) {
      return new MatchQueryExp(var0, new StringValueExp("*" + escapeString(var1.getValue()) + "*"));
   }

   public static QueryExp finalSubString(AttributeValueExp var0, StringValueExp var1) {
      return new MatchQueryExp(var0, new StringValueExp("*" + escapeString(var1.getValue())));
   }

   public static QueryExp isInstanceOf(StringValueExp var0) {
      return new InstanceOfQueryExp(var0);
   }

   private static String escapeString(String var0) {
      if (var0 == null) {
         return null;
      } else {
         var0 = var0.replace("\\", "\\\\");
         var0 = var0.replace("*", "\\*");
         var0 = var0.replace("?", "\\?");
         var0 = var0.replace("[", "\\[");
         return var0;
      }
   }
}
