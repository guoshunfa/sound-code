package java.awt;

import java.util.Locale;

public final class PageAttributes implements Cloneable {
   private PageAttributes.ColorType color;
   private PageAttributes.MediaType media;
   private PageAttributes.OrientationRequestedType orientationRequested;
   private PageAttributes.OriginType origin;
   private PageAttributes.PrintQualityType printQuality;
   private int[] printerResolution;

   public PageAttributes() {
      this.setColor(PageAttributes.ColorType.MONOCHROME);
      this.setMediaToDefault();
      this.setOrientationRequestedToDefault();
      this.setOrigin(PageAttributes.OriginType.PHYSICAL);
      this.setPrintQualityToDefault();
      this.setPrinterResolutionToDefault();
   }

   public PageAttributes(PageAttributes var1) {
      this.set(var1);
   }

   public PageAttributes(PageAttributes.ColorType var1, PageAttributes.MediaType var2, PageAttributes.OrientationRequestedType var3, PageAttributes.OriginType var4, PageAttributes.PrintQualityType var5, int[] var6) {
      this.setColor(var1);
      this.setMedia(var2);
      this.setOrientationRequested(var3);
      this.setOrigin(var4);
      this.setPrintQuality(var5);
      this.setPrinterResolution(var6);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public void set(PageAttributes var1) {
      this.color = var1.color;
      this.media = var1.media;
      this.orientationRequested = var1.orientationRequested;
      this.origin = var1.origin;
      this.printQuality = var1.printQuality;
      this.printerResolution = var1.printerResolution;
   }

   public PageAttributes.ColorType getColor() {
      return this.color;
   }

   public void setColor(PageAttributes.ColorType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute color");
      } else {
         this.color = var1;
      }
   }

   public PageAttributes.MediaType getMedia() {
      return this.media;
   }

   public void setMedia(PageAttributes.MediaType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute media");
      } else {
         this.media = var1;
      }
   }

   public void setMediaToDefault() {
      String var1 = Locale.getDefault().getCountry();
      if (var1 == null || !var1.equals(Locale.US.getCountry()) && !var1.equals(Locale.CANADA.getCountry())) {
         this.setMedia(PageAttributes.MediaType.ISO_A4);
      } else {
         this.setMedia(PageAttributes.MediaType.NA_LETTER);
      }

   }

   public PageAttributes.OrientationRequestedType getOrientationRequested() {
      return this.orientationRequested;
   }

   public void setOrientationRequested(PageAttributes.OrientationRequestedType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute orientationRequested");
      } else {
         this.orientationRequested = var1;
      }
   }

   public void setOrientationRequested(int var1) {
      switch(var1) {
      case 3:
         this.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
         break;
      case 4:
         this.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
         break;
      default:
         this.setOrientationRequested((PageAttributes.OrientationRequestedType)null);
      }

   }

   public void setOrientationRequestedToDefault() {
      this.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
   }

   public PageAttributes.OriginType getOrigin() {
      return this.origin;
   }

   public void setOrigin(PageAttributes.OriginType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute origin");
      } else {
         this.origin = var1;
      }
   }

   public PageAttributes.PrintQualityType getPrintQuality() {
      return this.printQuality;
   }

   public void setPrintQuality(PageAttributes.PrintQualityType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute printQuality");
      } else {
         this.printQuality = var1;
      }
   }

   public void setPrintQuality(int var1) {
      switch(var1) {
      case 3:
         this.setPrintQuality(PageAttributes.PrintQualityType.DRAFT);
         break;
      case 4:
         this.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
         break;
      case 5:
         this.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
         break;
      default:
         this.setPrintQuality((PageAttributes.PrintQualityType)null);
      }

   }

   public void setPrintQualityToDefault() {
      this.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
   }

   public int[] getPrinterResolution() {
      int[] var1 = new int[]{this.printerResolution[0], this.printerResolution[1], this.printerResolution[2]};
      return var1;
   }

   public void setPrinterResolution(int[] var1) {
      if (var1 != null && var1.length == 3 && var1[0] > 0 && var1[1] > 0 && (var1[2] == 3 || var1[2] == 4)) {
         int[] var2 = new int[]{var1[0], var1[1], var1[2]};
         this.printerResolution = var2;
      } else {
         throw new IllegalArgumentException("Invalid value for attribute printerResolution");
      }
   }

   public void setPrinterResolution(int var1) {
      this.setPrinterResolution(new int[]{var1, var1, 3});
   }

   public void setPrinterResolutionToDefault() {
      this.setPrinterResolution(72);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof PageAttributes)) {
         return false;
      } else {
         PageAttributes var2 = (PageAttributes)var1;
         return this.color == var2.color && this.media == var2.media && this.orientationRequested == var2.orientationRequested && this.origin == var2.origin && this.printQuality == var2.printQuality && this.printerResolution[0] == var2.printerResolution[0] && this.printerResolution[1] == var2.printerResolution[1] && this.printerResolution[2] == var2.printerResolution[2];
      }
   }

   public int hashCode() {
      return this.color.hashCode() << 31 ^ this.media.hashCode() << 24 ^ this.orientationRequested.hashCode() << 23 ^ this.origin.hashCode() << 22 ^ this.printQuality.hashCode() << 20 ^ this.printerResolution[2] >> 2 << 19 ^ this.printerResolution[1] << 10 ^ this.printerResolution[0];
   }

   public String toString() {
      return "color=" + this.getColor() + ",media=" + this.getMedia() + ",orientation-requested=" + this.getOrientationRequested() + ",origin=" + this.getOrigin() + ",print-quality=" + this.getPrintQuality() + ",printer-resolution=[" + this.printerResolution[0] + "," + this.printerResolution[1] + "," + this.printerResolution[2] + "]";
   }

   public static final class PrintQualityType extends AttributeValue {
      private static final int I_HIGH = 0;
      private static final int I_NORMAL = 1;
      private static final int I_DRAFT = 2;
      private static final String[] NAMES = new String[]{"high", "normal", "draft"};
      public static final PageAttributes.PrintQualityType HIGH = new PageAttributes.PrintQualityType(0);
      public static final PageAttributes.PrintQualityType NORMAL = new PageAttributes.PrintQualityType(1);
      public static final PageAttributes.PrintQualityType DRAFT = new PageAttributes.PrintQualityType(2);

      private PrintQualityType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class OriginType extends AttributeValue {
      private static final int I_PHYSICAL = 0;
      private static final int I_PRINTABLE = 1;
      private static final String[] NAMES = new String[]{"physical", "printable"};
      public static final PageAttributes.OriginType PHYSICAL = new PageAttributes.OriginType(0);
      public static final PageAttributes.OriginType PRINTABLE = new PageAttributes.OriginType(1);

      private OriginType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class OrientationRequestedType extends AttributeValue {
      private static final int I_PORTRAIT = 0;
      private static final int I_LANDSCAPE = 1;
      private static final String[] NAMES = new String[]{"portrait", "landscape"};
      public static final PageAttributes.OrientationRequestedType PORTRAIT = new PageAttributes.OrientationRequestedType(0);
      public static final PageAttributes.OrientationRequestedType LANDSCAPE = new PageAttributes.OrientationRequestedType(1);

      private OrientationRequestedType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class MediaType extends AttributeValue {
      private static final int I_ISO_4A0 = 0;
      private static final int I_ISO_2A0 = 1;
      private static final int I_ISO_A0 = 2;
      private static final int I_ISO_A1 = 3;
      private static final int I_ISO_A2 = 4;
      private static final int I_ISO_A3 = 5;
      private static final int I_ISO_A4 = 6;
      private static final int I_ISO_A5 = 7;
      private static final int I_ISO_A6 = 8;
      private static final int I_ISO_A7 = 9;
      private static final int I_ISO_A8 = 10;
      private static final int I_ISO_A9 = 11;
      private static final int I_ISO_A10 = 12;
      private static final int I_ISO_B0 = 13;
      private static final int I_ISO_B1 = 14;
      private static final int I_ISO_B2 = 15;
      private static final int I_ISO_B3 = 16;
      private static final int I_ISO_B4 = 17;
      private static final int I_ISO_B5 = 18;
      private static final int I_ISO_B6 = 19;
      private static final int I_ISO_B7 = 20;
      private static final int I_ISO_B8 = 21;
      private static final int I_ISO_B9 = 22;
      private static final int I_ISO_B10 = 23;
      private static final int I_JIS_B0 = 24;
      private static final int I_JIS_B1 = 25;
      private static final int I_JIS_B2 = 26;
      private static final int I_JIS_B3 = 27;
      private static final int I_JIS_B4 = 28;
      private static final int I_JIS_B5 = 29;
      private static final int I_JIS_B6 = 30;
      private static final int I_JIS_B7 = 31;
      private static final int I_JIS_B8 = 32;
      private static final int I_JIS_B9 = 33;
      private static final int I_JIS_B10 = 34;
      private static final int I_ISO_C0 = 35;
      private static final int I_ISO_C1 = 36;
      private static final int I_ISO_C2 = 37;
      private static final int I_ISO_C3 = 38;
      private static final int I_ISO_C4 = 39;
      private static final int I_ISO_C5 = 40;
      private static final int I_ISO_C6 = 41;
      private static final int I_ISO_C7 = 42;
      private static final int I_ISO_C8 = 43;
      private static final int I_ISO_C9 = 44;
      private static final int I_ISO_C10 = 45;
      private static final int I_ISO_DESIGNATED_LONG = 46;
      private static final int I_EXECUTIVE = 47;
      private static final int I_FOLIO = 48;
      private static final int I_INVOICE = 49;
      private static final int I_LEDGER = 50;
      private static final int I_NA_LETTER = 51;
      private static final int I_NA_LEGAL = 52;
      private static final int I_QUARTO = 53;
      private static final int I_A = 54;
      private static final int I_B = 55;
      private static final int I_C = 56;
      private static final int I_D = 57;
      private static final int I_E = 58;
      private static final int I_NA_10X15_ENVELOPE = 59;
      private static final int I_NA_10X14_ENVELOPE = 60;
      private static final int I_NA_10X13_ENVELOPE = 61;
      private static final int I_NA_9X12_ENVELOPE = 62;
      private static final int I_NA_9X11_ENVELOPE = 63;
      private static final int I_NA_7X9_ENVELOPE = 64;
      private static final int I_NA_6X9_ENVELOPE = 65;
      private static final int I_NA_NUMBER_9_ENVELOPE = 66;
      private static final int I_NA_NUMBER_10_ENVELOPE = 67;
      private static final int I_NA_NUMBER_11_ENVELOPE = 68;
      private static final int I_NA_NUMBER_12_ENVELOPE = 69;
      private static final int I_NA_NUMBER_14_ENVELOPE = 70;
      private static final int I_INVITE_ENVELOPE = 71;
      private static final int I_ITALY_ENVELOPE = 72;
      private static final int I_MONARCH_ENVELOPE = 73;
      private static final int I_PERSONAL_ENVELOPE = 74;
      private static final String[] NAMES = new String[]{"iso-4a0", "iso-2a0", "iso-a0", "iso-a1", "iso-a2", "iso-a3", "iso-a4", "iso-a5", "iso-a6", "iso-a7", "iso-a8", "iso-a9", "iso-a10", "iso-b0", "iso-b1", "iso-b2", "iso-b3", "iso-b4", "iso-b5", "iso-b6", "iso-b7", "iso-b8", "iso-b9", "iso-b10", "jis-b0", "jis-b1", "jis-b2", "jis-b3", "jis-b4", "jis-b5", "jis-b6", "jis-b7", "jis-b8", "jis-b9", "jis-b10", "iso-c0", "iso-c1", "iso-c2", "iso-c3", "iso-c4", "iso-c5", "iso-c6", "iso-c7", "iso-c8", "iso-c9", "iso-c10", "iso-designated-long", "executive", "folio", "invoice", "ledger", "na-letter", "na-legal", "quarto", "a", "b", "c", "d", "e", "na-10x15-envelope", "na-10x14-envelope", "na-10x13-envelope", "na-9x12-envelope", "na-9x11-envelope", "na-7x9-envelope", "na-6x9-envelope", "na-number-9-envelope", "na-number-10-envelope", "na-number-11-envelope", "na-number-12-envelope", "na-number-14-envelope", "invite-envelope", "italy-envelope", "monarch-envelope", "personal-envelope"};
      public static final PageAttributes.MediaType ISO_4A0 = new PageAttributes.MediaType(0);
      public static final PageAttributes.MediaType ISO_2A0 = new PageAttributes.MediaType(1);
      public static final PageAttributes.MediaType ISO_A0 = new PageAttributes.MediaType(2);
      public static final PageAttributes.MediaType ISO_A1 = new PageAttributes.MediaType(3);
      public static final PageAttributes.MediaType ISO_A2 = new PageAttributes.MediaType(4);
      public static final PageAttributes.MediaType ISO_A3 = new PageAttributes.MediaType(5);
      public static final PageAttributes.MediaType ISO_A4 = new PageAttributes.MediaType(6);
      public static final PageAttributes.MediaType ISO_A5 = new PageAttributes.MediaType(7);
      public static final PageAttributes.MediaType ISO_A6 = new PageAttributes.MediaType(8);
      public static final PageAttributes.MediaType ISO_A7 = new PageAttributes.MediaType(9);
      public static final PageAttributes.MediaType ISO_A8 = new PageAttributes.MediaType(10);
      public static final PageAttributes.MediaType ISO_A9 = new PageAttributes.MediaType(11);
      public static final PageAttributes.MediaType ISO_A10 = new PageAttributes.MediaType(12);
      public static final PageAttributes.MediaType ISO_B0 = new PageAttributes.MediaType(13);
      public static final PageAttributes.MediaType ISO_B1 = new PageAttributes.MediaType(14);
      public static final PageAttributes.MediaType ISO_B2 = new PageAttributes.MediaType(15);
      public static final PageAttributes.MediaType ISO_B3 = new PageAttributes.MediaType(16);
      public static final PageAttributes.MediaType ISO_B4 = new PageAttributes.MediaType(17);
      public static final PageAttributes.MediaType ISO_B5 = new PageAttributes.MediaType(18);
      public static final PageAttributes.MediaType ISO_B6 = new PageAttributes.MediaType(19);
      public static final PageAttributes.MediaType ISO_B7 = new PageAttributes.MediaType(20);
      public static final PageAttributes.MediaType ISO_B8 = new PageAttributes.MediaType(21);
      public static final PageAttributes.MediaType ISO_B9 = new PageAttributes.MediaType(22);
      public static final PageAttributes.MediaType ISO_B10 = new PageAttributes.MediaType(23);
      public static final PageAttributes.MediaType JIS_B0 = new PageAttributes.MediaType(24);
      public static final PageAttributes.MediaType JIS_B1 = new PageAttributes.MediaType(25);
      public static final PageAttributes.MediaType JIS_B2 = new PageAttributes.MediaType(26);
      public static final PageAttributes.MediaType JIS_B3 = new PageAttributes.MediaType(27);
      public static final PageAttributes.MediaType JIS_B4 = new PageAttributes.MediaType(28);
      public static final PageAttributes.MediaType JIS_B5 = new PageAttributes.MediaType(29);
      public static final PageAttributes.MediaType JIS_B6 = new PageAttributes.MediaType(30);
      public static final PageAttributes.MediaType JIS_B7 = new PageAttributes.MediaType(31);
      public static final PageAttributes.MediaType JIS_B8 = new PageAttributes.MediaType(32);
      public static final PageAttributes.MediaType JIS_B9 = new PageAttributes.MediaType(33);
      public static final PageAttributes.MediaType JIS_B10 = new PageAttributes.MediaType(34);
      public static final PageAttributes.MediaType ISO_C0 = new PageAttributes.MediaType(35);
      public static final PageAttributes.MediaType ISO_C1 = new PageAttributes.MediaType(36);
      public static final PageAttributes.MediaType ISO_C2 = new PageAttributes.MediaType(37);
      public static final PageAttributes.MediaType ISO_C3 = new PageAttributes.MediaType(38);
      public static final PageAttributes.MediaType ISO_C4 = new PageAttributes.MediaType(39);
      public static final PageAttributes.MediaType ISO_C5 = new PageAttributes.MediaType(40);
      public static final PageAttributes.MediaType ISO_C6 = new PageAttributes.MediaType(41);
      public static final PageAttributes.MediaType ISO_C7 = new PageAttributes.MediaType(42);
      public static final PageAttributes.MediaType ISO_C8 = new PageAttributes.MediaType(43);
      public static final PageAttributes.MediaType ISO_C9 = new PageAttributes.MediaType(44);
      public static final PageAttributes.MediaType ISO_C10 = new PageAttributes.MediaType(45);
      public static final PageAttributes.MediaType ISO_DESIGNATED_LONG = new PageAttributes.MediaType(46);
      public static final PageAttributes.MediaType EXECUTIVE = new PageAttributes.MediaType(47);
      public static final PageAttributes.MediaType FOLIO = new PageAttributes.MediaType(48);
      public static final PageAttributes.MediaType INVOICE = new PageAttributes.MediaType(49);
      public static final PageAttributes.MediaType LEDGER = new PageAttributes.MediaType(50);
      public static final PageAttributes.MediaType NA_LETTER = new PageAttributes.MediaType(51);
      public static final PageAttributes.MediaType NA_LEGAL = new PageAttributes.MediaType(52);
      public static final PageAttributes.MediaType QUARTO = new PageAttributes.MediaType(53);
      public static final PageAttributes.MediaType A = new PageAttributes.MediaType(54);
      public static final PageAttributes.MediaType B = new PageAttributes.MediaType(55);
      public static final PageAttributes.MediaType C = new PageAttributes.MediaType(56);
      public static final PageAttributes.MediaType D = new PageAttributes.MediaType(57);
      public static final PageAttributes.MediaType E = new PageAttributes.MediaType(58);
      public static final PageAttributes.MediaType NA_10X15_ENVELOPE = new PageAttributes.MediaType(59);
      public static final PageAttributes.MediaType NA_10X14_ENVELOPE = new PageAttributes.MediaType(60);
      public static final PageAttributes.MediaType NA_10X13_ENVELOPE = new PageAttributes.MediaType(61);
      public static final PageAttributes.MediaType NA_9X12_ENVELOPE = new PageAttributes.MediaType(62);
      public static final PageAttributes.MediaType NA_9X11_ENVELOPE = new PageAttributes.MediaType(63);
      public static final PageAttributes.MediaType NA_7X9_ENVELOPE = new PageAttributes.MediaType(64);
      public static final PageAttributes.MediaType NA_6X9_ENVELOPE = new PageAttributes.MediaType(65);
      public static final PageAttributes.MediaType NA_NUMBER_9_ENVELOPE = new PageAttributes.MediaType(66);
      public static final PageAttributes.MediaType NA_NUMBER_10_ENVELOPE = new PageAttributes.MediaType(67);
      public static final PageAttributes.MediaType NA_NUMBER_11_ENVELOPE = new PageAttributes.MediaType(68);
      public static final PageAttributes.MediaType NA_NUMBER_12_ENVELOPE = new PageAttributes.MediaType(69);
      public static final PageAttributes.MediaType NA_NUMBER_14_ENVELOPE = new PageAttributes.MediaType(70);
      public static final PageAttributes.MediaType INVITE_ENVELOPE = new PageAttributes.MediaType(71);
      public static final PageAttributes.MediaType ITALY_ENVELOPE = new PageAttributes.MediaType(72);
      public static final PageAttributes.MediaType MONARCH_ENVELOPE = new PageAttributes.MediaType(73);
      public static final PageAttributes.MediaType PERSONAL_ENVELOPE = new PageAttributes.MediaType(74);
      public static final PageAttributes.MediaType A0;
      public static final PageAttributes.MediaType A1;
      public static final PageAttributes.MediaType A2;
      public static final PageAttributes.MediaType A3;
      public static final PageAttributes.MediaType A4;
      public static final PageAttributes.MediaType A5;
      public static final PageAttributes.MediaType A6;
      public static final PageAttributes.MediaType A7;
      public static final PageAttributes.MediaType A8;
      public static final PageAttributes.MediaType A9;
      public static final PageAttributes.MediaType A10;
      public static final PageAttributes.MediaType B0;
      public static final PageAttributes.MediaType B1;
      public static final PageAttributes.MediaType B2;
      public static final PageAttributes.MediaType B3;
      public static final PageAttributes.MediaType B4;
      public static final PageAttributes.MediaType ISO_B4_ENVELOPE;
      public static final PageAttributes.MediaType B5;
      public static final PageAttributes.MediaType ISO_B5_ENVELOPE;
      public static final PageAttributes.MediaType B6;
      public static final PageAttributes.MediaType B7;
      public static final PageAttributes.MediaType B8;
      public static final PageAttributes.MediaType B9;
      public static final PageAttributes.MediaType B10;
      public static final PageAttributes.MediaType C0;
      public static final PageAttributes.MediaType ISO_C0_ENVELOPE;
      public static final PageAttributes.MediaType C1;
      public static final PageAttributes.MediaType ISO_C1_ENVELOPE;
      public static final PageAttributes.MediaType C2;
      public static final PageAttributes.MediaType ISO_C2_ENVELOPE;
      public static final PageAttributes.MediaType C3;
      public static final PageAttributes.MediaType ISO_C3_ENVELOPE;
      public static final PageAttributes.MediaType C4;
      public static final PageAttributes.MediaType ISO_C4_ENVELOPE;
      public static final PageAttributes.MediaType C5;
      public static final PageAttributes.MediaType ISO_C5_ENVELOPE;
      public static final PageAttributes.MediaType C6;
      public static final PageAttributes.MediaType ISO_C6_ENVELOPE;
      public static final PageAttributes.MediaType C7;
      public static final PageAttributes.MediaType ISO_C7_ENVELOPE;
      public static final PageAttributes.MediaType C8;
      public static final PageAttributes.MediaType ISO_C8_ENVELOPE;
      public static final PageAttributes.MediaType C9;
      public static final PageAttributes.MediaType ISO_C9_ENVELOPE;
      public static final PageAttributes.MediaType C10;
      public static final PageAttributes.MediaType ISO_C10_ENVELOPE;
      public static final PageAttributes.MediaType ISO_DESIGNATED_LONG_ENVELOPE;
      public static final PageAttributes.MediaType STATEMENT;
      public static final PageAttributes.MediaType TABLOID;
      public static final PageAttributes.MediaType LETTER;
      public static final PageAttributes.MediaType NOTE;
      public static final PageAttributes.MediaType LEGAL;
      public static final PageAttributes.MediaType ENV_10X15;
      public static final PageAttributes.MediaType ENV_10X14;
      public static final PageAttributes.MediaType ENV_10X13;
      public static final PageAttributes.MediaType ENV_9X12;
      public static final PageAttributes.MediaType ENV_9X11;
      public static final PageAttributes.MediaType ENV_7X9;
      public static final PageAttributes.MediaType ENV_6X9;
      public static final PageAttributes.MediaType ENV_9;
      public static final PageAttributes.MediaType ENV_10;
      public static final PageAttributes.MediaType ENV_11;
      public static final PageAttributes.MediaType ENV_12;
      public static final PageAttributes.MediaType ENV_14;
      public static final PageAttributes.MediaType ENV_INVITE;
      public static final PageAttributes.MediaType ENV_ITALY;
      public static final PageAttributes.MediaType ENV_MONARCH;
      public static final PageAttributes.MediaType ENV_PERSONAL;
      public static final PageAttributes.MediaType INVITE;
      public static final PageAttributes.MediaType ITALY;
      public static final PageAttributes.MediaType MONARCH;
      public static final PageAttributes.MediaType PERSONAL;

      private MediaType(int var1) {
         super(var1, NAMES);
      }

      static {
         A0 = ISO_A0;
         A1 = ISO_A1;
         A2 = ISO_A2;
         A3 = ISO_A3;
         A4 = ISO_A4;
         A5 = ISO_A5;
         A6 = ISO_A6;
         A7 = ISO_A7;
         A8 = ISO_A8;
         A9 = ISO_A9;
         A10 = ISO_A10;
         B0 = ISO_B0;
         B1 = ISO_B1;
         B2 = ISO_B2;
         B3 = ISO_B3;
         B4 = ISO_B4;
         ISO_B4_ENVELOPE = ISO_B4;
         B5 = ISO_B5;
         ISO_B5_ENVELOPE = ISO_B5;
         B6 = ISO_B6;
         B7 = ISO_B7;
         B8 = ISO_B8;
         B9 = ISO_B9;
         B10 = ISO_B10;
         C0 = ISO_C0;
         ISO_C0_ENVELOPE = ISO_C0;
         C1 = ISO_C1;
         ISO_C1_ENVELOPE = ISO_C1;
         C2 = ISO_C2;
         ISO_C2_ENVELOPE = ISO_C2;
         C3 = ISO_C3;
         ISO_C3_ENVELOPE = ISO_C3;
         C4 = ISO_C4;
         ISO_C4_ENVELOPE = ISO_C4;
         C5 = ISO_C5;
         ISO_C5_ENVELOPE = ISO_C5;
         C6 = ISO_C6;
         ISO_C6_ENVELOPE = ISO_C6;
         C7 = ISO_C7;
         ISO_C7_ENVELOPE = ISO_C7;
         C8 = ISO_C8;
         ISO_C8_ENVELOPE = ISO_C8;
         C9 = ISO_C9;
         ISO_C9_ENVELOPE = ISO_C9;
         C10 = ISO_C10;
         ISO_C10_ENVELOPE = ISO_C10;
         ISO_DESIGNATED_LONG_ENVELOPE = ISO_DESIGNATED_LONG;
         STATEMENT = INVOICE;
         TABLOID = LEDGER;
         LETTER = NA_LETTER;
         NOTE = NA_LETTER;
         LEGAL = NA_LEGAL;
         ENV_10X15 = NA_10X15_ENVELOPE;
         ENV_10X14 = NA_10X14_ENVELOPE;
         ENV_10X13 = NA_10X13_ENVELOPE;
         ENV_9X12 = NA_9X12_ENVELOPE;
         ENV_9X11 = NA_9X11_ENVELOPE;
         ENV_7X9 = NA_7X9_ENVELOPE;
         ENV_6X9 = NA_6X9_ENVELOPE;
         ENV_9 = NA_NUMBER_9_ENVELOPE;
         ENV_10 = NA_NUMBER_10_ENVELOPE;
         ENV_11 = NA_NUMBER_11_ENVELOPE;
         ENV_12 = NA_NUMBER_12_ENVELOPE;
         ENV_14 = NA_NUMBER_14_ENVELOPE;
         ENV_INVITE = INVITE_ENVELOPE;
         ENV_ITALY = ITALY_ENVELOPE;
         ENV_MONARCH = MONARCH_ENVELOPE;
         ENV_PERSONAL = PERSONAL_ENVELOPE;
         INVITE = INVITE_ENVELOPE;
         ITALY = ITALY_ENVELOPE;
         MONARCH = MONARCH_ENVELOPE;
         PERSONAL = PERSONAL_ENVELOPE;
      }
   }

   public static final class ColorType extends AttributeValue {
      private static final int I_COLOR = 0;
      private static final int I_MONOCHROME = 1;
      private static final String[] NAMES = new String[]{"color", "monochrome"};
      public static final PageAttributes.ColorType COLOR = new PageAttributes.ColorType(0);
      public static final PageAttributes.ColorType MONOCHROME = new PageAttributes.ColorType(1);

      private ColorType(int var1) {
         super(var1, NAMES);
      }
   }
}
