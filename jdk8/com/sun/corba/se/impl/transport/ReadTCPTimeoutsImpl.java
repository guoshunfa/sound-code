package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.transport.ReadTimeouts;

public class ReadTCPTimeoutsImpl implements ReadTimeouts {
   private int initial_time_to_wait;
   private int max_time_to_wait;
   private int max_giop_header_time_to_wait;
   private double backoff_factor;

   public ReadTCPTimeoutsImpl(int var1, int var2, int var3, int var4) {
      this.initial_time_to_wait = var1;
      this.max_time_to_wait = var2;
      this.max_giop_header_time_to_wait = var3;
      this.backoff_factor = 1.0D + (double)var4 / 100.0D;
   }

   public int get_initial_time_to_wait() {
      return this.initial_time_to_wait;
   }

   public int get_max_time_to_wait() {
      return this.max_time_to_wait;
   }

   public double get_backoff_factor() {
      return this.backoff_factor;
   }

   public int get_max_giop_header_time_to_wait() {
      return this.max_giop_header_time_to_wait;
   }
}
