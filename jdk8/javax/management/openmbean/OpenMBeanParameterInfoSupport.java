package javax.management.openmbean;

import java.util.Set;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanParameterInfo;

public class OpenMBeanParameterInfoSupport extends MBeanParameterInfo implements OpenMBeanParameterInfo {
   static final long serialVersionUID = -7235016873758443122L;
   private OpenType<?> openType;
   private Object defaultValue;
   private Set<?> legalValues;
   private Comparable<?> minValue;
   private Comparable<?> maxValue;
   private transient Integer myHashCode;
   private transient String myToString;

   public OpenMBeanParameterInfoSupport(String var1, String var2, OpenType<?> var3) {
      this(var1, var2, var3, (Descriptor)null);
   }

   public OpenMBeanParameterInfoSupport(String var1, String var2, OpenType<?> var3, Descriptor var4) {
      super(var1, var3 == null ? null : var3.getClassName(), var2, ImmutableDescriptor.union(var4, var3 == null ? null : var3.getDescriptor()));
      this.defaultValue = null;
      this.legalValues = null;
      this.minValue = null;
      this.maxValue = null;
      this.myHashCode = null;
      this.myToString = null;
      this.openType = var3;
      var4 = this.getDescriptor();
      this.defaultValue = OpenMBeanAttributeInfoSupport.valueFrom(var4, "defaultValue", var3);
      this.legalValues = OpenMBeanAttributeInfoSupport.valuesFrom(var4, "legalValues", var3);
      this.minValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(var4, "minValue", var3);
      this.maxValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(var4, "maxValue", var3);

      try {
         OpenMBeanAttributeInfoSupport.check(this);
      } catch (OpenDataException var6) {
         throw new IllegalArgumentException(var6.getMessage(), var6);
      }
   }

   public <T> OpenMBeanParameterInfoSupport(String var1, String var2, OpenType<T> var3, T var4) throws OpenDataException {
      this(var1, var2, var3, var4, (Object[])null);
   }

   public <T> OpenMBeanParameterInfoSupport(String var1, String var2, OpenType<T> var3, T var4, T[] var5) throws OpenDataException {
      this(var1, var2, var3, var4, var5, (Comparable)null, (Comparable)null);
   }

   public <T> OpenMBeanParameterInfoSupport(String var1, String var2, OpenType<T> var3, T var4, Comparable<T> var5, Comparable<T> var6) throws OpenDataException {
      this(var1, var2, var3, var4, (Object[])null, var5, var6);
   }

   private <T> OpenMBeanParameterInfoSupport(String var1, String var2, OpenType<T> var3, T var4, T[] var5, Comparable<T> var6, Comparable<T> var7) throws OpenDataException {
      super(var1, var3 == null ? null : var3.getClassName(), var2, OpenMBeanAttributeInfoSupport.makeDescriptor(var3, var4, var5, var6, var7));
      this.defaultValue = null;
      this.legalValues = null;
      this.minValue = null;
      this.maxValue = null;
      this.myHashCode = null;
      this.myToString = null;
      this.openType = var3;
      Descriptor var8 = this.getDescriptor();
      this.defaultValue = var4;
      this.minValue = var6;
      this.maxValue = var7;
      this.legalValues = (Set)var8.getFieldValue("legalValues");
      OpenMBeanAttributeInfoSupport.check(this);
   }

   private Object readResolve() {
      if (this.getDescriptor().getFieldNames().length == 0) {
         OpenType var1 = (OpenType)OpenMBeanAttributeInfoSupport.cast(this.openType);
         Set var2 = (Set)OpenMBeanAttributeInfoSupport.cast(this.legalValues);
         Comparable var3 = (Comparable)OpenMBeanAttributeInfoSupport.cast(this.minValue);
         Comparable var4 = (Comparable)OpenMBeanAttributeInfoSupport.cast(this.maxValue);
         return new OpenMBeanParameterInfoSupport(this.name, this.description, this.openType, OpenMBeanAttributeInfoSupport.makeDescriptor(var1, this.defaultValue, var2, var3, var4));
      } else {
         return this;
      }
   }

   public OpenType<?> getOpenType() {
      return this.openType;
   }

   public Object getDefaultValue() {
      return this.defaultValue;
   }

   public Set<?> getLegalValues() {
      return this.legalValues;
   }

   public Comparable<?> getMinValue() {
      return this.minValue;
   }

   public Comparable<?> getMaxValue() {
      return this.maxValue;
   }

   public boolean hasDefaultValue() {
      return this.defaultValue != null;
   }

   public boolean hasLegalValues() {
      return this.legalValues != null;
   }

   public boolean hasMinValue() {
      return this.minValue != null;
   }

   public boolean hasMaxValue() {
      return this.maxValue != null;
   }

   public boolean isValue(Object var1) {
      return OpenMBeanAttributeInfoSupport.isValue(this, var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof OpenMBeanParameterInfo)) {
         return false;
      } else {
         OpenMBeanParameterInfo var2 = (OpenMBeanParameterInfo)var1;
         return OpenMBeanAttributeInfoSupport.equal(this, var2);
      }
   }

   public int hashCode() {
      if (this.myHashCode == null) {
         this.myHashCode = OpenMBeanAttributeInfoSupport.hashCode(this);
      }

      return this.myHashCode;
   }

   public String toString() {
      if (this.myToString == null) {
         this.myToString = OpenMBeanAttributeInfoSupport.toString(this);
      }

      return this.myToString;
   }
}
