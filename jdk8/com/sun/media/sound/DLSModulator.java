package com.sun.media.sound;

public final class DLSModulator {
   public static final int CONN_DST_NONE = 0;
   public static final int CONN_DST_GAIN = 1;
   public static final int CONN_DST_PITCH = 3;
   public static final int CONN_DST_PAN = 4;
   public static final int CONN_DST_LFO_FREQUENCY = 260;
   public static final int CONN_DST_LFO_STARTDELAY = 261;
   public static final int CONN_DST_EG1_ATTACKTIME = 518;
   public static final int CONN_DST_EG1_DECAYTIME = 519;
   public static final int CONN_DST_EG1_RELEASETIME = 521;
   public static final int CONN_DST_EG1_SUSTAINLEVEL = 522;
   public static final int CONN_DST_EG2_ATTACKTIME = 778;
   public static final int CONN_DST_EG2_DECAYTIME = 779;
   public static final int CONN_DST_EG2_RELEASETIME = 781;
   public static final int CONN_DST_EG2_SUSTAINLEVEL = 782;
   public static final int CONN_DST_KEYNUMBER = 5;
   public static final int CONN_DST_LEFT = 16;
   public static final int CONN_DST_RIGHT = 17;
   public static final int CONN_DST_CENTER = 18;
   public static final int CONN_DST_LEFTREAR = 19;
   public static final int CONN_DST_RIGHTREAR = 20;
   public static final int CONN_DST_LFE_CHANNEL = 21;
   public static final int CONN_DST_CHORUS = 128;
   public static final int CONN_DST_REVERB = 129;
   public static final int CONN_DST_VIB_FREQUENCY = 276;
   public static final int CONN_DST_VIB_STARTDELAY = 277;
   public static final int CONN_DST_EG1_DELAYTIME = 523;
   public static final int CONN_DST_EG1_HOLDTIME = 524;
   public static final int CONN_DST_EG1_SHUTDOWNTIME = 525;
   public static final int CONN_DST_EG2_DELAYTIME = 783;
   public static final int CONN_DST_EG2_HOLDTIME = 784;
   public static final int CONN_DST_FILTER_CUTOFF = 1280;
   public static final int CONN_DST_FILTER_Q = 1281;
   public static final int CONN_SRC_NONE = 0;
   public static final int CONN_SRC_LFO = 1;
   public static final int CONN_SRC_KEYONVELOCITY = 2;
   public static final int CONN_SRC_KEYNUMBER = 3;
   public static final int CONN_SRC_EG1 = 4;
   public static final int CONN_SRC_EG2 = 5;
   public static final int CONN_SRC_PITCHWHEEL = 6;
   public static final int CONN_SRC_CC1 = 129;
   public static final int CONN_SRC_CC7 = 135;
   public static final int CONN_SRC_CC10 = 138;
   public static final int CONN_SRC_CC11 = 139;
   public static final int CONN_SRC_RPN0 = 256;
   public static final int CONN_SRC_RPN1 = 257;
   public static final int CONN_SRC_RPN2 = 258;
   public static final int CONN_SRC_POLYPRESSURE = 7;
   public static final int CONN_SRC_CHANNELPRESSURE = 8;
   public static final int CONN_SRC_VIBRATO = 9;
   public static final int CONN_SRC_MONOPRESSURE = 10;
   public static final int CONN_SRC_CC91 = 219;
   public static final int CONN_SRC_CC93 = 221;
   public static final int CONN_TRN_NONE = 0;
   public static final int CONN_TRN_CONCAVE = 1;
   public static final int CONN_TRN_CONVEX = 2;
   public static final int CONN_TRN_SWITCH = 3;
   public static final int DST_FORMAT_CB = 1;
   public static final int DST_FORMAT_CENT = 1;
   public static final int DST_FORMAT_TIMECENT = 2;
   public static final int DST_FORMAT_PERCENT = 3;
   int source;
   int control;
   int destination;
   int transform;
   int scale;
   int version = 1;

   public int getControl() {
      return this.control;
   }

   public void setControl(int var1) {
      this.control = var1;
   }

   public static int getDestinationFormat(int var0) {
      if (var0 == 1) {
         return 1;
      } else if (var0 == 3) {
         return 1;
      } else if (var0 == 4) {
         return 3;
      } else if (var0 == 260) {
         return 1;
      } else if (var0 == 261) {
         return 2;
      } else if (var0 == 518) {
         return 2;
      } else if (var0 == 519) {
         return 2;
      } else if (var0 == 521) {
         return 2;
      } else if (var0 == 522) {
         return 3;
      } else if (var0 == 778) {
         return 2;
      } else if (var0 == 779) {
         return 2;
      } else if (var0 == 781) {
         return 2;
      } else if (var0 == 782) {
         return 3;
      } else if (var0 == 5) {
         return 1;
      } else if (var0 == 16) {
         return 1;
      } else if (var0 == 17) {
         return 1;
      } else if (var0 == 18) {
         return 1;
      } else if (var0 == 19) {
         return 1;
      } else if (var0 == 20) {
         return 1;
      } else if (var0 == 21) {
         return 1;
      } else if (var0 == 128) {
         return 3;
      } else if (var0 == 129) {
         return 3;
      } else if (var0 == 276) {
         return 1;
      } else if (var0 == 277) {
         return 2;
      } else if (var0 == 523) {
         return 2;
      } else if (var0 == 524) {
         return 2;
      } else if (var0 == 525) {
         return 2;
      } else if (var0 == 783) {
         return 2;
      } else if (var0 == 784) {
         return 2;
      } else if (var0 == 1280) {
         return 1;
      } else {
         return var0 == 1281 ? 1 : -1;
      }
   }

   public static String getDestinationName(int var0) {
      if (var0 == 1) {
         return "gain";
      } else if (var0 == 3) {
         return "pitch";
      } else if (var0 == 4) {
         return "pan";
      } else if (var0 == 260) {
         return "lfo1.freq";
      } else if (var0 == 261) {
         return "lfo1.delay";
      } else if (var0 == 518) {
         return "eg1.attack";
      } else if (var0 == 519) {
         return "eg1.decay";
      } else if (var0 == 521) {
         return "eg1.release";
      } else if (var0 == 522) {
         return "eg1.sustain";
      } else if (var0 == 778) {
         return "eg2.attack";
      } else if (var0 == 779) {
         return "eg2.decay";
      } else if (var0 == 781) {
         return "eg2.release";
      } else if (var0 == 782) {
         return "eg2.sustain";
      } else if (var0 == 5) {
         return "keynumber";
      } else if (var0 == 16) {
         return "left";
      } else if (var0 == 17) {
         return "right";
      } else if (var0 == 18) {
         return "center";
      } else if (var0 == 19) {
         return "leftrear";
      } else if (var0 == 20) {
         return "rightrear";
      } else if (var0 == 21) {
         return "lfe_channel";
      } else if (var0 == 128) {
         return "chorus";
      } else if (var0 == 129) {
         return "reverb";
      } else if (var0 == 276) {
         return "vib.freq";
      } else if (var0 == 277) {
         return "vib.delay";
      } else if (var0 == 523) {
         return "eg1.delay";
      } else if (var0 == 524) {
         return "eg1.hold";
      } else if (var0 == 525) {
         return "eg1.shutdown";
      } else if (var0 == 783) {
         return "eg2.delay";
      } else if (var0 == 784) {
         return "eg.2hold";
      } else if (var0 == 1280) {
         return "filter.cutoff";
      } else {
         return var0 == 1281 ? "filter.q" : null;
      }
   }

   public static String getSourceName(int var0) {
      if (var0 == 0) {
         return "none";
      } else if (var0 == 1) {
         return "lfo";
      } else if (var0 == 2) {
         return "keyonvelocity";
      } else if (var0 == 3) {
         return "keynumber";
      } else if (var0 == 4) {
         return "eg1";
      } else if (var0 == 5) {
         return "eg2";
      } else if (var0 == 6) {
         return "pitchweel";
      } else if (var0 == 129) {
         return "cc1";
      } else if (var0 == 135) {
         return "cc7";
      } else if (var0 == 138) {
         return "c10";
      } else if (var0 == 139) {
         return "cc11";
      } else if (var0 == 7) {
         return "polypressure";
      } else if (var0 == 8) {
         return "channelpressure";
      } else if (var0 == 9) {
         return "vibrato";
      } else if (var0 == 10) {
         return "monopressure";
      } else if (var0 == 219) {
         return "cc91";
      } else {
         return var0 == 221 ? "cc93" : null;
      }
   }

   public int getDestination() {
      return this.destination;
   }

   public void setDestination(int var1) {
      this.destination = var1;
   }

   public int getScale() {
      return this.scale;
   }

   public void setScale(int var1) {
      this.scale = var1;
   }

   public int getSource() {
      return this.source;
   }

   public void setSource(int var1) {
      this.source = var1;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int var1) {
      this.version = var1;
   }

   public int getTransform() {
      return this.transform;
   }

   public void setTransform(int var1) {
      this.transform = var1;
   }
}
