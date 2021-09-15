package sun.security.krb5.internal.crypto;

import java.security.DigestException;
import java.security.MessageDigestSpi;
import sun.security.krb5.internal.Krb5;

public final class crc32 extends MessageDigestSpi implements Cloneable {
   private static final int CRC32_LENGTH = 4;
   private int seed;
   private static boolean DEBUG;
   private static int[] crc32Table;

   public crc32() {
      this.init();
   }

   public Object clone() {
      try {
         crc32 var1 = (crc32)super.clone();
         var1.init();
         return var1;
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   protected int engineGetDigestLength() {
      return 4;
   }

   protected byte[] engineDigest() {
      byte[] var1 = new byte[4];
      var1 = int2quad((long)this.seed);
      this.init();
      return var1;
   }

   protected int engineDigest(byte[] var1, int var2, int var3) throws DigestException {
      byte[] var4 = new byte[4];
      var4 = int2quad((long)this.seed);
      if (var3 < 4) {
         throw new DigestException("partial digests not returned");
      } else if (var1.length - var2 < 4) {
         throw new DigestException("insufficient space in the output buffer to store the digest");
      } else {
         System.arraycopy(var4, 0, var1, var2, 4);
         this.init();
         return 4;
      }
   }

   protected synchronized void engineUpdate(byte var1) {
      byte[] var2 = new byte[]{var1};
      this.engineUpdate(var2, this.seed, 1);
   }

   protected synchronized void engineUpdate(byte[] var1, int var2, int var3) {
      this.processData(var1, var2, var3);
   }

   protected void engineReset() {
      this.init();
   }

   public void init() {
      this.seed = 0;
   }

   private void processData(byte[] var1, int var2, int var3) {
      int var4 = this.seed;

      for(int var5 = 0; var5 < var3; ++var5) {
         var4 = var4 >>> 8 ^ crc32Table[(var4 ^ var1[var5]) & 255];
      }

      this.seed = var4;
   }

   public static int int2crc32(int var0) {
      int var1 = var0;

      for(int var2 = 8; var2 > 0; --var2) {
         if ((var1 & 1) != 0) {
            var1 = var1 >>> 1 ^ -306674912;
         } else {
            var1 >>>= 1;
         }
      }

      return var1;
   }

   public static void printcrc32Table() {
      String var1 = "00000000";
      System.out.print("\tpublic static int[] crc32Table = {");

      for(int var2 = 0; var2 < 256; ++var2) {
         if (var2 % 4 == 0) {
            System.out.print("\n\t\t");
         }

         String var0 = Integer.toHexString(int2crc32(var2));
         System.out.print("0x" + var1.substring(var0.length()) + var0);
         if (var2 != 255) {
            System.out.print(", ");
         }
      }

      System.out.println("\n\t};");
   }

   public static int byte2crc32sum(int var0, byte[] var1, int var2) {
      int var3 = var0;

      for(int var4 = 0; var4 < var2; ++var4) {
         var3 = var3 >>> 8 ^ crc32Table[(var3 ^ var1[var4]) & 255];
      }

      return var3;
   }

   public static int byte2crc32sum(int var0, byte[] var1) {
      return byte2crc32sum(var0, var1, var1.length);
   }

   public static int byte2crc32sum(byte[] var0) {
      return byte2crc32sum(0, var0);
   }

   public static int byte2crc32(byte[] var0) {
      return ~byte2crc32sum(-1, var0);
   }

   public static byte[] byte2crc32sum_bytes(byte[] var0) {
      int var1 = byte2crc32sum(var0);
      return int2quad((long)var1);
   }

   public static byte[] byte2crc32sum_bytes(byte[] var0, int var1) {
      int var2 = byte2crc32sum(0, var0, var1);
      if (DEBUG) {
         System.out.println(">>>crc32: " + Integer.toHexString(var2));
         System.out.println(">>>crc32: " + Integer.toBinaryString(var2));
      }

      return int2quad((long)var2);
   }

   public static byte[] int2quad(long var0) {
      byte[] var2 = new byte[4];

      for(int var3 = 0; var3 < 4; ++var3) {
         var2[var3] = (byte)((int)(var0 >>> var3 * 8 & 255L));
      }

      return var2;
   }

   static {
      DEBUG = Krb5.DEBUG;
      crc32Table = new int[]{0, 1996959894, -301047508, -1727442502, 124634137, 1886057615, -379345611, -1637575261, 249268274, 2044508324, -522852066, -1747789432, 162941995, 2125561021, -407360249, -1866523247, 498536548, 1789927666, -205950648, -2067906082, 450548861, 1843258603, -187386543, -2083289657, 325883990, 1684777152, -43845254, -1973040660, 335633487, 1661365465, -99664541, -1928851979, 997073096, 1281953886, -715111964, -1570279054, 1006888145, 1258607687, -770865667, -1526024853, 901097722, 1119000684, -608450090, -1396901568, 853044451, 1172266101, -589951537, -1412350631, 651767980, 1373503546, -925412992, -1076862698, 565507253, 1454621731, -809855591, -1195530993, 671266974, 1594198024, -972236366, -1324619484, 795835527, 1483230225, -1050600021, -1234817731, 1994146192, 31158534, -1731059524, -271249366, 1907459465, 112637215, -1614814043, -390540237, 2013776290, 251722036, -1777751922, -519137256, 2137656763, 141376813, -1855689577, -429695999, 1802195444, 476864866, -2056965928, -228458418, 1812370925, 453092731, -2113342271, -183516073, 1706088902, 314042704, -1950435094, -54949764, 1658658271, 366619977, -1932296973, -69972891, 1303535960, 984961486, -1547960204, -725929758, 1256170817, 1037604311, -1529756563, -740887301, 1131014506, 879679996, -1385723834, -631195440, 1141124467, 855842277, -1442165665, -586318647, 1342533948, 654459306, -1106571248, -921952122, 1466479909, 544179635, -1184443383, -832445281, 1591671054, 702138776, -1328506846, -942167884, 1504918807, 783551873, -1212326853, -1061524307, -306674912, -1698712650, 62317068, 1957810842, -355121351, -1647151185, 81470997, 1943803523, -480048366, -1805370492, 225274430, 2053790376, -468791541, -1828061283, 167816743, 2097651377, -267414716, -2029476910, 503444072, 1762050814, -144550051, -2140837941, 426522225, 1852507879, -19653770, -1982649376, 282753626, 1742555852, -105259153, -1900089351, 397917763, 1622183637, -690576408, -1580100738, 953729732, 1340076626, -776247311, -1497606297, 1068828381, 1219638859, -670225446, -1358292148, 906185462, 1090812512, -547295293, -1469587627, 829329135, 1181335161, -882789492, -1134132454, 628085408, 1382605366, -871598187, -1156888829, 570562233, 1426400815, -977650754, -1296233688, 733239954, 1555261956, -1026031705, -1244606671, 752459403, 1541320221, -1687895376, -328994266, 1969922972, 40735498, -1677130071, -351390145, 1913087877, 83908371, -1782625662, -491226604, 2075208622, 213261112, -1831694693, -438977011, 2094854071, 198958881, -2032938284, -237706686, 1759359992, 534414190, -2118248755, -155638181, 1873836001, 414664567, -2012718362, -15766928, 1711684554, 285281116, -1889165569, -127750551, 1634467795, 376229701, -1609899400, -686959890, 1308918612, 956543938, -1486412191, -799009033, 1231636301, 1047427035, -1362007478, -640263460, 1088359270, 936918000, -1447252397, -558129467, 1202900863, 817233897, -1111625188, -893730166, 1404277552, 615818150, -1160759803, -841546093, 1423857449, 601450431, -1285129682, -1000256840, 1567103746, 711928724, -1274298825, -1022587231, 1510334235, 755167117};
   }
}
