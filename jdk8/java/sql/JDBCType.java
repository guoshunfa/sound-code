package java.sql;

public enum JDBCType implements SQLType {
   BIT(-7),
   TINYINT(-6),
   SMALLINT(5),
   INTEGER(4),
   BIGINT(-5),
   FLOAT(6),
   REAL(7),
   DOUBLE(8),
   NUMERIC(2),
   DECIMAL(3),
   CHAR(1),
   VARCHAR(12),
   LONGVARCHAR(-1),
   DATE(91),
   TIME(92),
   TIMESTAMP(93),
   BINARY(-2),
   VARBINARY(-3),
   LONGVARBINARY(-4),
   NULL(0),
   OTHER(1111),
   JAVA_OBJECT(2000),
   DISTINCT(2001),
   STRUCT(2002),
   ARRAY(2003),
   BLOB(2004),
   CLOB(2005),
   REF(2006),
   DATALINK(70),
   BOOLEAN(16),
   ROWID(-8),
   NCHAR(-15),
   NVARCHAR(-9),
   LONGNVARCHAR(-16),
   NCLOB(2011),
   SQLXML(2009),
   REF_CURSOR(2012),
   TIME_WITH_TIMEZONE(2013),
   TIMESTAMP_WITH_TIMEZONE(2014);

   private Integer type;

   private JDBCType(Integer var3) {
      this.type = var3;
   }

   public String getName() {
      return this.name();
   }

   public String getVendor() {
      return "java.sql";
   }

   public Integer getVendorTypeNumber() {
      return this.type;
   }

   public static JDBCType valueOf(int var0) {
      JDBCType[] var1 = (JDBCType[])JDBCType.class.getEnumConstants();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         JDBCType var4 = var1[var3];
         if (var0 == var4.type) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Type:" + var0 + " is not a valid Types.java value.");
   }
}
