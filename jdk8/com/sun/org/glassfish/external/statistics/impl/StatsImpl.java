package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.Statistic;
import com.sun.org.glassfish.external.statistics.Stats;
import java.util.ArrayList;

public final class StatsImpl implements Stats {
   private final StatisticImpl[] statArray;

   protected StatsImpl(StatisticImpl[] statisticArray) {
      this.statArray = statisticArray;
   }

   public synchronized Statistic getStatistic(String statisticName) {
      Statistic stat = null;
      StatisticImpl[] var3 = this.statArray;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Statistic s = var3[var5];
         if (s.getName().equals(statisticName)) {
            stat = s;
            break;
         }
      }

      return stat;
   }

   public synchronized String[] getStatisticNames() {
      ArrayList list = new ArrayList();
      StatisticImpl[] var2 = this.statArray;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Statistic s = var2[var4];
         list.add(s.getName());
      }

      String[] strArray = new String[list.size()];
      return (String[])((String[])list.toArray(strArray));
   }

   public synchronized Statistic[] getStatistics() {
      return this.statArray;
   }

   public synchronized void reset() {
      StatisticImpl[] var1 = this.statArray;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         StatisticImpl s = var1[var3];
         s.reset();
      }

   }
}
