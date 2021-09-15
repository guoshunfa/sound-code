package com.sun.management;

import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import jdk.Exported;
import sun.management.GcInfoBuilder;
import sun.management.GcInfoCompositeData;

@Exported
public class GcInfo implements CompositeData, CompositeDataView {
   private final long index;
   private final long startTime;
   private final long endTime;
   private final Map<String, MemoryUsage> usageBeforeGc;
   private final Map<String, MemoryUsage> usageAfterGc;
   private final Object[] extAttributes;
   private final CompositeData cdata;
   private final GcInfoBuilder builder;

   private GcInfo(GcInfoBuilder var1, long var2, long var4, long var6, MemoryUsage[] var8, MemoryUsage[] var9, Object[] var10) {
      this.builder = var1;
      this.index = var2;
      this.startTime = var4;
      this.endTime = var6;
      String[] var11 = var1.getPoolNames();
      this.usageBeforeGc = new HashMap(var11.length);
      this.usageAfterGc = new HashMap(var11.length);

      for(int var12 = 0; var12 < var11.length; ++var12) {
         this.usageBeforeGc.put(var11[var12], var8[var12]);
         this.usageAfterGc.put(var11[var12], var9[var12]);
      }

      this.extAttributes = var10;
      this.cdata = new GcInfoCompositeData(this, var1, var10);
   }

   private GcInfo(CompositeData var1) {
      GcInfoCompositeData.validateCompositeData(var1);
      this.index = GcInfoCompositeData.getId(var1);
      this.startTime = GcInfoCompositeData.getStartTime(var1);
      this.endTime = GcInfoCompositeData.getEndTime(var1);
      this.usageBeforeGc = GcInfoCompositeData.getMemoryUsageBeforeGc(var1);
      this.usageAfterGc = GcInfoCompositeData.getMemoryUsageAfterGc(var1);
      this.extAttributes = null;
      this.builder = null;
      this.cdata = var1;
   }

   public long getId() {
      return this.index;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public long getDuration() {
      return this.endTime - this.startTime;
   }

   public Map<String, MemoryUsage> getMemoryUsageBeforeGc() {
      return Collections.unmodifiableMap(this.usageBeforeGc);
   }

   public Map<String, MemoryUsage> getMemoryUsageAfterGc() {
      return Collections.unmodifiableMap(this.usageAfterGc);
   }

   public static GcInfo from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof GcInfoCompositeData ? ((GcInfoCompositeData)var0).getGcInfo() : new GcInfo(var0);
      }
   }

   public boolean containsKey(String var1) {
      return this.cdata.containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      return this.cdata.containsValue(var1);
   }

   public boolean equals(Object var1) {
      return this.cdata.equals(var1);
   }

   public Object get(String var1) {
      return this.cdata.get(var1);
   }

   public Object[] getAll(String[] var1) {
      return this.cdata.getAll(var1);
   }

   public CompositeType getCompositeType() {
      return this.cdata.getCompositeType();
   }

   public int hashCode() {
      return this.cdata.hashCode();
   }

   public String toString() {
      return this.cdata.toString();
   }

   public Collection values() {
      return this.cdata.values();
   }

   public CompositeData toCompositeData(CompositeType var1) {
      return this.cdata;
   }
}
