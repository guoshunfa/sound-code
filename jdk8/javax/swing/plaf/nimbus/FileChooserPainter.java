package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class FileChooserPainter extends AbstractRegionPainter {
   static final int BACKGROUND_ENABLED = 1;
   static final int FILEICON_ENABLED = 2;
   static final int DIRECTORYICON_ENABLED = 3;
   static final int UPFOLDERICON_ENABLED = 4;
   static final int NEWFOLDERICON_ENABLED = 5;
   static final int COMPUTERICON_ENABLED = 6;
   static final int HARDDRIVEICON_ENABLED = 7;
   static final int FLOPPYDRIVEICON_ENABLED = 8;
   static final int HOMEFOLDERICON_ENABLED = 9;
   static final int DETAILSVIEWICON_ENABLED = 10;
   static final int LISTVIEWICON_ENABLED = 11;
   private int state;
   private AbstractRegionPainter.PaintContext ctx;
   private Path2D path = new Path2D.Float();
   private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   private Color color1 = this.decodeColor("control", 0.0F, 0.0F, 0.0F, 0);
   private Color color2 = this.decodeColor("nimbusBlueGrey", 0.007936537F, -0.065654516F, -0.13333333F, 0);
   private Color color3 = new Color(97, 98, 102, 255);
   private Color color4 = this.decodeColor("nimbusBlueGrey", -0.032679737F, -0.043332636F, 0.24705881F, 0);
   private Color color5 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
   private Color color6 = this.decodeColor("nimbusBase", 0.0077680945F, -0.51781034F, 0.3490196F, 0);
   private Color color7 = this.decodeColor("nimbusBase", 0.013940871F, -0.599277F, 0.41960782F, 0);
   private Color color8 = this.decodeColor("nimbusBase", 0.004681647F, -0.4198052F, 0.14117646F, 0);
   private Color color9 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -127);
   private Color color10 = this.decodeColor("nimbusBlueGrey", 0.0F, 0.0F, -0.21F, -99);
   private Color color11 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.45978838F, 0.2980392F, 0);
   private Color color12 = this.decodeColor("nimbusBase", 0.0015952587F, -0.34848025F, 0.18823528F, 0);
   private Color color13 = this.decodeColor("nimbusBase", 0.0015952587F, -0.30844158F, 0.09803921F, 0);
   private Color color14 = this.decodeColor("nimbusBase", 0.0015952587F, -0.27329817F, 0.035294116F, 0);
   private Color color15 = this.decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
   private Color color16 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -125);
   private Color color17 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -50);
   private Color color18 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -100);
   private Color color19 = this.decodeColor("nimbusBase", 0.0012094378F, -0.23571429F, -0.0784314F, 0);
   private Color color20 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.115166366F, -0.2627451F, 0);
   private Color color21 = this.decodeColor("nimbusBase", 0.0027436614F, -0.335015F, 0.011764705F, 0);
   private Color color22 = this.decodeColor("nimbusBase", 0.0024294257F, -0.3857143F, 0.031372547F, 0);
   private Color color23 = this.decodeColor("nimbusBase", 0.0018081069F, -0.3595238F, -0.13725492F, 0);
   private Color color24 = new Color(255, 200, 0, 255);
   private Color color25 = this.decodeColor("nimbusBase", 0.004681647F, -0.44904763F, 0.039215684F, 0);
   private Color color26 = this.decodeColor("nimbusBase", 0.0015952587F, -0.43718487F, -0.015686274F, 0);
   private Color color27 = this.decodeColor("nimbusBase", 2.9569864E-4F, -0.39212453F, -0.24313727F, 0);
   private Color color28 = this.decodeColor("nimbusBase", 0.004681647F, -0.6117143F, 0.43137252F, 0);
   private Color color29 = this.decodeColor("nimbusBase", 0.0012094378F, -0.28015873F, -0.019607842F, 0);
   private Color color30 = this.decodeColor("nimbusBase", 0.00254488F, -0.07049692F, -0.2784314F, 0);
   private Color color31 = this.decodeColor("nimbusBase", 0.0015952587F, -0.28045115F, 0.04705882F, 0);
   private Color color32 = this.decodeColor("nimbusBlueGrey", 0.0F, 5.847961E-4F, -0.21568626F, 0);
   private Color color33 = this.decodeColor("nimbusBase", -0.0061469674F, 0.3642857F, 0.14509803F, 0);
   private Color color34 = this.decodeColor("nimbusBase", 0.0053939223F, 0.3642857F, -0.0901961F, 0);
   private Color color35 = this.decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
   private Color color36 = this.decodeColor("nimbusBase", -0.006044388F, -0.23963585F, 0.45098037F, 0);
   private Color color37 = this.decodeColor("nimbusBase", -0.0063245893F, 0.01592505F, 0.4078431F, 0);
   private Color color38 = this.decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -170);
   private Color color39 = this.decodeColor("nimbusOrange", -0.032758567F, -0.018273294F, 0.25098038F, 0);
   private Color color40 = new Color(255, 255, 255, 255);
   private Color color41 = new Color(252, 255, 92, 255);
   private Color color42 = new Color(253, 191, 4, 255);
   private Color color43 = new Color(160, 161, 163, 255);
   private Color color44 = new Color(0, 0, 0, 255);
   private Color color45 = new Color(239, 241, 243, 255);
   private Color color46 = new Color(197, 201, 205, 255);
   private Color color47 = new Color(105, 110, 118, 255);
   private Color color48 = new Color(63, 67, 72, 255);
   private Color color49 = new Color(56, 51, 25, 255);
   private Color color50 = new Color(144, 255, 0, 255);
   private Color color51 = new Color(243, 245, 246, 255);
   private Color color52 = new Color(208, 212, 216, 255);
   private Color color53 = new Color(191, 193, 194, 255);
   private Color color54 = new Color(170, 172, 175, 255);
   private Color color55 = new Color(152, 155, 158, 255);
   private Color color56 = new Color(59, 62, 66, 255);
   private Color color57 = new Color(46, 46, 46, 255);
   private Color color58 = new Color(64, 64, 64, 255);
   private Color color59 = new Color(43, 43, 43, 255);
   private Color color60 = new Color(164, 179, 206, 255);
   private Color color61 = new Color(97, 123, 170, 255);
   private Color color62 = new Color(53, 86, 146, 255);
   private Color color63 = new Color(48, 82, 144, 255);
   private Color color64 = new Color(71, 99, 150, 255);
   private Color color65 = new Color(224, 224, 224, 255);
   private Color color66 = new Color(232, 232, 232, 255);
   private Color color67 = new Color(231, 234, 237, 255);
   private Color color68 = new Color(205, 211, 215, 255);
   private Color color69 = new Color(149, 153, 156, 54);
   private Color color70 = new Color(255, 122, 101, 255);
   private Color color71 = new Color(54, 78, 122, 255);
   private Color color72 = new Color(51, 60, 70, 255);
   private Color color73 = new Color(228, 232, 237, 255);
   private Color color74 = new Color(27, 57, 87, 255);
   private Color color75 = new Color(75, 109, 137, 255);
   private Color color76 = new Color(77, 133, 185, 255);
   private Color color77 = new Color(81, 59, 7, 255);
   private Color color78 = new Color(97, 74, 18, 255);
   private Color color79 = new Color(137, 115, 60, 255);
   private Color color80 = new Color(174, 151, 91, 255);
   private Color color81 = new Color(114, 92, 13, 255);
   private Color color82 = new Color(64, 48, 0, 255);
   private Color color83 = new Color(244, 222, 143, 255);
   private Color color84 = new Color(160, 161, 162, 255);
   private Color color85 = new Color(226, 230, 233, 255);
   private Color color86 = new Color(221, 225, 230, 255);
   private Color color87 = this.decodeColor("nimbusBase", 0.004681647F, -0.48756614F, 0.19215685F, 0);
   private Color color88 = this.decodeColor("nimbusBase", 0.004681647F, -0.48399013F, 0.019607842F, 0);
   private Color color89 = this.decodeColor("nimbusBase", -0.0028941035F, -0.5906323F, 0.4078431F, 0);
   private Color color90 = this.decodeColor("nimbusBase", 0.004681647F, -0.51290727F, 0.34509802F, 0);
   private Color color91 = this.decodeColor("nimbusBase", 0.009583652F, -0.5642857F, 0.3843137F, 0);
   private Color color92 = this.decodeColor("nimbusBase", -0.0072231293F, -0.6074885F, 0.4235294F, 0);
   private Color color93 = this.decodeColor("nimbusBase", 7.13408E-4F, -0.52158386F, 0.17254901F, 0);
   private Color color94 = this.decodeColor("nimbusBase", 0.012257397F, -0.5775132F, 0.19215685F, 0);
   private Color color95 = this.decodeColor("nimbusBase", 0.08801502F, -0.6164835F, -0.14117649F, 0);
   private Color color96 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.5019608F, 0);
   private Color color97 = this.decodeColor("nimbusBase", -0.0036516786F, -0.555393F, 0.42745095F, 0);
   private Color color98 = this.decodeColor("nimbusBase", -0.0010654926F, -0.3634138F, 0.2862745F, 0);
   private Color color99 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.29803923F, 0);
   private Color color100 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, 0.12156862F, 0);
   private Color color101 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
   private Color color102 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.48627454F, 0);
   private Color color103 = this.decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.007843137F, 0);
   private Color color104 = this.decodeColor("nimbusBase", -0.0028941035F, -0.5408867F, -0.09411767F, 0);
   private Color color105 = this.decodeColor("nimbusBase", -0.011985004F, -0.54721874F, -0.10588238F, 0);
   private Color color106 = this.decodeColor("nimbusBase", -0.0022627711F, -0.4305861F, -0.0901961F, 0);
   private Color color107 = this.decodeColor("nimbusBase", -0.00573498F, -0.447479F, -0.21568629F, 0);
   private Color color108 = this.decodeColor("nimbusBase", 0.004681647F, -0.53271F, 0.36470586F, 0);
   private Color color109 = this.decodeColor("nimbusBase", 0.004681647F, -0.5276062F, -0.11372551F, 0);
   private Color color110 = this.decodeColor("nimbusBase", -8.738637E-4F, -0.5278006F, -0.0039215684F, 0);
   private Color color111 = this.decodeColor("nimbusBase", -0.0028941035F, -0.5338625F, -0.12549022F, 0);
   private Color color112 = this.decodeColor("nimbusBlueGrey", -0.03535354F, -0.008674465F, -0.32156864F, 0);
   private Color color113 = this.decodeColor("nimbusBlueGrey", -0.027777791F, -0.010526314F, -0.3529412F, 0);
   private Color color114 = this.decodeColor("nimbusBase", -0.0028941035F, -0.5234694F, -0.1647059F, 0);
   private Color color115 = this.decodeColor("nimbusBase", 0.004681647F, -0.53401935F, -0.086274534F, 0);
   private Color color116 = this.decodeColor("nimbusBase", 0.004681647F, -0.52077174F, -0.20784315F, 0);
   private Color color117 = new Color(108, 114, 120, 255);
   private Color color118 = new Color(77, 82, 87, 255);
   private Color color119 = this.decodeColor("nimbusBase", -0.004577577F, -0.52179027F, -0.2392157F, 0);
   private Color color120 = this.decodeColor("nimbusBase", -0.004577577F, -0.547479F, -0.14901963F, 0);
   private Color color121 = new Color(186, 186, 186, 50);
   private Color color122 = new Color(186, 186, 186, 40);
   private Object[] componentColors;

   public FileChooserPainter(AbstractRegionPainter.PaintContext var1, int var2) {
      this.state = var2;
      this.ctx = var1;
   }

   protected void doPaint(Graphics2D var1, JComponent var2, int var3, int var4, Object[] var5) {
      this.componentColors = var5;
      switch(this.state) {
      case 1:
         this.paintBackgroundEnabled(var1);
         break;
      case 2:
         this.paintfileIconEnabled(var1);
         break;
      case 3:
         this.paintdirectoryIconEnabled(var1);
         break;
      case 4:
         this.paintupFolderIconEnabled(var1);
         break;
      case 5:
         this.paintnewFolderIconEnabled(var1);
      case 6:
      default:
         break;
      case 7:
         this.painthardDriveIconEnabled(var1);
         break;
      case 8:
         this.paintfloppyDriveIconEnabled(var1);
         break;
      case 9:
         this.painthomeFolderIconEnabled(var1);
         break;
      case 10:
         this.paintdetailsViewIconEnabled(var1);
         break;
      case 11:
         this.paintlistViewIconEnabled(var1);
      }

   }

   protected final AbstractRegionPainter.PaintContext getPaintContext() {
      return this.ctx;
   }

   private void paintBackgroundEnabled(Graphics2D var1) {
      this.rect = this.decodeRect1();
      var1.setPaint(this.color1);
      var1.fill(this.rect);
   }

   private void paintfileIconEnabled(Graphics2D var1) {
      this.path = this.decodePath1();
      var1.setPaint(this.color2);
      var1.fill(this.path);
      this.rect = this.decodeRect2();
      var1.setPaint(this.color3);
      var1.fill(this.rect);
      this.path = this.decodePath2();
      var1.setPaint(this.decodeGradient1(this.path));
      var1.fill(this.path);
      this.path = this.decodePath3();
      var1.setPaint(this.decodeGradient2(this.path));
      var1.fill(this.path);
      this.path = this.decodePath4();
      var1.setPaint(this.color8);
      var1.fill(this.path);
      this.path = this.decodePath5();
      var1.setPaint(this.color9);
      var1.fill(this.path);
   }

   private void paintdirectoryIconEnabled(Graphics2D var1) {
      this.path = this.decodePath6();
      var1.setPaint(this.color10);
      var1.fill(this.path);
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color17);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color18);
      var1.fill(this.rect);
      this.path = this.decodePath9();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath10();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
      this.path = this.decodePath11();
      var1.setPaint(this.color24);
      var1.fill(this.path);
   }

   private void paintupFolderIconEnabled(Graphics2D var1) {
      this.path = this.decodePath12();
      var1.setPaint(this.decodeGradient7(this.path));
      var1.fill(this.path);
      this.path = this.decodePath13();
      var1.setPaint(this.decodeGradient8(this.path));
      var1.fill(this.path);
      this.path = this.decodePath14();
      var1.setPaint(this.decodeGradient9(this.path));
      var1.fill(this.path);
      this.path = this.decodePath15();
      var1.setPaint(this.decodeGradient10(this.path));
      var1.fill(this.path);
      this.path = this.decodePath16();
      var1.setPaint(this.color32);
      var1.fill(this.path);
      this.path = this.decodePath17();
      var1.setPaint(this.decodeGradient11(this.path));
      var1.fill(this.path);
      this.path = this.decodePath18();
      var1.setPaint(this.color35);
      var1.fill(this.path);
      this.path = this.decodePath19();
      var1.setPaint(this.decodeGradient12(this.path));
      var1.fill(this.path);
   }

   private void paintnewFolderIconEnabled(Graphics2D var1) {
      this.path = this.decodePath6();
      var1.setPaint(this.color10);
      var1.fill(this.path);
      this.path = this.decodePath7();
      var1.setPaint(this.decodeGradient3(this.path));
      var1.fill(this.path);
      this.path = this.decodePath8();
      var1.setPaint(this.decodeGradient4(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect3();
      var1.setPaint(this.color16);
      var1.fill(this.rect);
      this.rect = this.decodeRect4();
      var1.setPaint(this.color17);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color18);
      var1.fill(this.rect);
      this.path = this.decodePath9();
      var1.setPaint(this.decodeGradient5(this.path));
      var1.fill(this.path);
      this.path = this.decodePath10();
      var1.setPaint(this.decodeGradient6(this.path));
      var1.fill(this.path);
      this.path = this.decodePath11();
      var1.setPaint(this.color24);
      var1.fill(this.path);
      this.path = this.decodePath20();
      var1.setPaint(this.color38);
      var1.fill(this.path);
      this.path = this.decodePath21();
      var1.setPaint(this.color39);
      var1.fill(this.path);
      this.path = this.decodePath22();
      var1.setPaint(this.decodeRadial1(this.path));
      var1.fill(this.path);
   }

   private void painthardDriveIconEnabled(Graphics2D var1) {
      this.rect = this.decodeRect6();
      var1.setPaint(this.color43);
      var1.fill(this.rect);
      this.rect = this.decodeRect7();
      var1.setPaint(this.color44);
      var1.fill(this.rect);
      this.rect = this.decodeRect8();
      var1.setPaint(this.decodeGradient13(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath23();
      var1.setPaint(this.decodeGradient14(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect9();
      var1.setPaint(this.color49);
      var1.fill(this.rect);
      this.rect = this.decodeRect10();
      var1.setPaint(this.color49);
      var1.fill(this.rect);
      this.ellipse = this.decodeEllipse1();
      var1.setPaint(this.color50);
      var1.fill(this.ellipse);
      this.path = this.decodePath24();
      var1.setPaint(this.decodeGradient15(this.path));
      var1.fill(this.path);
      this.ellipse = this.decodeEllipse2();
      var1.setPaint(this.color53);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse3();
      var1.setPaint(this.color53);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse4();
      var1.setPaint(this.color54);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse5();
      var1.setPaint(this.color55);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse6();
      var1.setPaint(this.color55);
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse7();
      var1.setPaint(this.color55);
      var1.fill(this.ellipse);
      this.rect = this.decodeRect11();
      var1.setPaint(this.color56);
      var1.fill(this.rect);
      this.rect = this.decodeRect12();
      var1.setPaint(this.color56);
      var1.fill(this.rect);
      this.rect = this.decodeRect13();
      var1.setPaint(this.color56);
      var1.fill(this.rect);
   }

   private void paintfloppyDriveIconEnabled(Graphics2D var1) {
      this.path = this.decodePath25();
      var1.setPaint(this.decodeGradient16(this.path));
      var1.fill(this.path);
      this.path = this.decodePath26();
      var1.setPaint(this.decodeGradient17(this.path));
      var1.fill(this.path);
      this.path = this.decodePath27();
      var1.setPaint(this.decodeGradient18(this.path));
      var1.fill(this.path);
      this.path = this.decodePath28();
      var1.setPaint(this.decodeGradient19(this.path));
      var1.fill(this.path);
      this.path = this.decodePath29();
      var1.setPaint(this.color69);
      var1.fill(this.path);
      this.rect = this.decodeRect14();
      var1.setPaint(this.color70);
      var1.fill(this.rect);
      this.rect = this.decodeRect15();
      var1.setPaint(this.color40);
      var1.fill(this.rect);
      this.rect = this.decodeRect16();
      var1.setPaint(this.color67);
      var1.fill(this.rect);
      this.rect = this.decodeRect17();
      var1.setPaint(this.color71);
      var1.fill(this.rect);
      this.rect = this.decodeRect18();
      var1.setPaint(this.color44);
      var1.fill(this.rect);
   }

   private void painthomeFolderIconEnabled(Graphics2D var1) {
      this.path = this.decodePath30();
      var1.setPaint(this.color72);
      var1.fill(this.path);
      this.path = this.decodePath31();
      var1.setPaint(this.color73);
      var1.fill(this.path);
      this.rect = this.decodeRect19();
      var1.setPaint(this.decodeGradient20(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect20();
      var1.setPaint(this.color76);
      var1.fill(this.rect);
      this.path = this.decodePath32();
      var1.setPaint(this.decodeGradient21(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect21();
      var1.setPaint(this.decodeGradient22(this.rect));
      var1.fill(this.rect);
      this.path = this.decodePath33();
      var1.setPaint(this.decodeGradient23(this.path));
      var1.fill(this.path);
      this.path = this.decodePath34();
      var1.setPaint(this.color83);
      var1.fill(this.path);
      this.path = this.decodePath35();
      var1.setPaint(this.decodeGradient24(this.path));
      var1.fill(this.path);
      this.path = this.decodePath36();
      var1.setPaint(this.decodeGradient25(this.path));
      var1.fill(this.path);
   }

   private void paintdetailsViewIconEnabled(Graphics2D var1) {
      this.rect = this.decodeRect22();
      var1.setPaint(this.decodeGradient26(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect23();
      var1.setPaint(this.decodeGradient27(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect24();
      var1.setPaint(this.color93);
      var1.fill(this.rect);
      this.rect = this.decodeRect5();
      var1.setPaint(this.color93);
      var1.fill(this.rect);
      this.rect = this.decodeRect25();
      var1.setPaint(this.color93);
      var1.fill(this.rect);
      this.rect = this.decodeRect26();
      var1.setPaint(this.color94);
      var1.fill(this.rect);
      this.ellipse = this.decodeEllipse8();
      var1.setPaint(this.decodeGradient28(this.ellipse));
      var1.fill(this.ellipse);
      this.ellipse = this.decodeEllipse9();
      var1.setPaint(this.decodeRadial2(this.ellipse));
      var1.fill(this.ellipse);
      this.path = this.decodePath37();
      var1.setPaint(this.decodeGradient29(this.path));
      var1.fill(this.path);
      this.path = this.decodePath38();
      var1.setPaint(this.decodeGradient30(this.path));
      var1.fill(this.path);
      this.rect = this.decodeRect27();
      var1.setPaint(this.color104);
      var1.fill(this.rect);
      this.rect = this.decodeRect28();
      var1.setPaint(this.color105);
      var1.fill(this.rect);
      this.rect = this.decodeRect29();
      var1.setPaint(this.color106);
      var1.fill(this.rect);
      this.rect = this.decodeRect30();
      var1.setPaint(this.color107);
      var1.fill(this.rect);
   }

   private void paintlistViewIconEnabled(Graphics2D var1) {
      this.rect = this.decodeRect31();
      var1.setPaint(this.decodeGradient26(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect32();
      var1.setPaint(this.decodeGradient31(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect33();
      var1.setPaint(this.color109);
      var1.fill(this.rect);
      this.rect = this.decodeRect34();
      var1.setPaint(this.decodeGradient32(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect35();
      var1.setPaint(this.color111);
      var1.fill(this.rect);
      this.rect = this.decodeRect36();
      var1.setPaint(this.color112);
      var1.fill(this.rect);
      this.rect = this.decodeRect37();
      var1.setPaint(this.color113);
      var1.fill(this.rect);
      this.rect = this.decodeRect38();
      var1.setPaint(this.decodeGradient33(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect39();
      var1.setPaint(this.color116);
      var1.fill(this.rect);
      this.rect = this.decodeRect40();
      var1.setPaint(this.decodeGradient34(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect41();
      var1.setPaint(this.decodeGradient35(this.rect));
      var1.fill(this.rect);
      this.rect = this.decodeRect42();
      var1.setPaint(this.color119);
      var1.fill(this.rect);
      this.rect = this.decodeRect43();
      var1.setPaint(this.color121);
      var1.fill(this.rect);
      this.rect = this.decodeRect44();
      var1.setPaint(this.color121);
      var1.fill(this.rect);
      this.rect = this.decodeRect45();
      var1.setPaint(this.color121);
      var1.fill(this.rect);
      this.rect = this.decodeRect46();
      var1.setPaint(this.color122);
      var1.fill(this.rect);
      this.rect = this.decodeRect47();
      var1.setPaint(this.color121);
      var1.fill(this.rect);
      this.rect = this.decodeRect48();
      var1.setPaint(this.color122);
      var1.fill(this.rect);
      this.rect = this.decodeRect49();
      var1.setPaint(this.color122);
      var1.fill(this.rect);
      this.rect = this.decodeRect50();
      var1.setPaint(this.color121);
      var1.fill(this.rect);
      this.rect = this.decodeRect51();
      var1.setPaint(this.color122);
      var1.fill(this.rect);
      this.rect = this.decodeRect52();
      var1.setPaint(this.color122);
      var1.fill(this.rect);
   }

   private Rectangle2D decodeRect1() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.0F)), (double)(this.decodeY(2.0F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Path2D decodePath1() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.9197531F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.9F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(0.88888896F));
      this.path.lineTo((double)this.decodeX(1.9537036F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.0F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect2() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(2.8F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(3.0F) - this.decodeY(2.8F)));
      return this.rect;
   }

   private Path2D decodePath2() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath3() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath4() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.6234567F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.6296296F), (double)this.decodeY(1.2037038F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.2006173F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath5() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(0.4F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath6() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.4F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.4F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.4F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath7() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6037037F), (double)this.decodeY(1.8425925F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath8() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.40833336F), (double)this.decodeY(1.8645833F));
      this.path.lineTo((double)this.decodeX(0.79583335F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect3() {
      this.rect.setRect((double)this.decodeX(0.2F), (double)this.decodeY(0.6F), (double)(this.decodeX(0.4F) - this.decodeX(0.2F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect4() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(0.2F), (double)(this.decodeX(1.3333334F) - this.decodeX(0.6F)), (double)(this.decodeY(0.4F) - this.decodeY(0.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect5() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.4F) - this.decodeX(1.5F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Path2D decodePath9() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(3.0F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.5888889F), (double)this.decodeY(0.20370372F));
      this.path.lineTo((double)this.decodeX(0.5962963F), (double)this.decodeY(0.34814817F));
      this.path.lineTo((double)this.decodeX(0.34814817F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.774074F), (double)this.decodeY(1.1604939F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8925927F), (double)this.decodeY(1.1882716F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.65185183F));
      this.path.lineTo((double)this.decodeX(0.63703704F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.5925925F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.8F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath10() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.74814814F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.4037037F), (double)this.decodeY(1.8425925F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.5925926F), (double)this.decodeY(2.225926F));
      this.path.lineTo((double)this.decodeX(0.916F), (double)this.decodeY(0.996F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath11() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(2.2F), (double)this.decodeY(2.2F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(2.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath12() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath13() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.8F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath14() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.4F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.0F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath15() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.8F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath16() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.1702899F), (double)this.decodeY(1.2536231F));
      this.path.lineTo((double)this.decodeX(1.1666666F), (double)this.decodeY(1.0615941F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.0978261F));
      this.path.lineTo((double)this.decodeX(2.7782607F), (double)this.decodeY(1.25F));
      this.path.lineTo((double)this.decodeX(2.3913045F), (double)this.decodeY(1.3188406F));
      this.path.lineTo((double)this.decodeX(2.3826087F), (double)this.decodeY(1.7246377F));
      this.path.lineTo((double)this.decodeX(2.173913F), (double)this.decodeY(1.9347827F));
      this.path.lineTo((double)this.decodeX(1.8695652F), (double)this.decodeY(1.923913F));
      this.path.lineTo((double)this.decodeX(1.710145F), (double)this.decodeY(1.7246377F));
      this.path.lineTo((double)this.decodeX(1.710145F), (double)this.decodeY(1.3115941F));
      this.path.lineTo((double)this.decodeX(1.1702899F), (double)this.decodeY(1.2536231F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath17() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.1666666F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(1.1666666F), (double)this.decodeY(0.9130435F));
      this.path.lineTo((double)this.decodeX(1.9456522F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(2.0608697F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(2.9956522F), (double)this.decodeY(0.9130435F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(1.1666666F), (double)this.decodeY(1.1666666F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath18() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.2717391F), (double)this.decodeY(0.9956522F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.8652174F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.13043478F));
      this.path.lineTo((double)this.decodeX(1.2717391F), (double)this.decodeY(0.9956522F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath19() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.3913044F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.9963768F), (double)this.decodeY(0.25652176F));
      this.path.lineTo((double)this.decodeX(2.6608696F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.8333333F), (double)this.decodeY(1.6666667F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath20() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.22692308F), (double)this.decodeY(0.061538465F));
      this.path.lineTo((double)this.decodeX(0.75384617F), (double)this.decodeY(0.37692308F));
      this.path.lineTo((double)this.decodeX(0.91923076F), (double)this.decodeY(0.01923077F));
      this.path.lineTo((double)this.decodeX(1.2532052F), (double)this.decodeY(0.40769228F));
      this.path.lineTo((double)this.decodeX(1.7115386F), (double)this.decodeY(0.13846155F));
      this.path.lineTo((double)this.decodeX(1.6923077F), (double)this.decodeY(0.85F));
      this.path.lineTo((double)this.decodeX(2.169231F), (double)this.decodeY(0.9115385F));
      this.path.lineTo((double)this.decodeX(1.7852564F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.9166667F), (double)this.decodeY(1.9679487F));
      this.path.lineTo((double)this.decodeX(1.3685898F), (double)this.decodeY(1.8301282F));
      this.path.lineTo((double)this.decodeX(1.1314102F), (double)this.decodeY(2.2115386F));
      this.path.lineTo((double)this.decodeX(0.63076925F), (double)this.decodeY(1.8205128F));
      this.path.lineTo((double)this.decodeX(0.22692308F), (double)this.decodeY(1.9262822F));
      this.path.lineTo((double)this.decodeX(0.31153846F), (double)this.decodeY(1.4871795F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.1538461F));
      this.path.lineTo((double)this.decodeX(0.38461536F), (double)this.decodeY(0.68076926F));
      this.path.lineTo((double)this.decodeX(0.22692308F), (double)this.decodeY(0.061538465F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath21() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.23461537F), (double)this.decodeY(0.33076924F));
      this.path.lineTo((double)this.decodeX(0.32692307F), (double)this.decodeY(0.21538463F));
      this.path.lineTo((double)this.decodeX(0.9653846F), (double)this.decodeY(0.74615383F));
      this.path.lineTo((double)this.decodeX(1.0160257F), (double)this.decodeY(0.01923077F));
      this.path.lineTo((double)this.decodeX(1.1506411F), (double)this.decodeY(0.01923077F));
      this.path.lineTo((double)this.decodeX(1.2275641F), (double)this.decodeY(0.72307694F));
      this.path.lineTo((double)this.decodeX(1.6987178F), (double)this.decodeY(0.20769231F));
      this.path.lineTo((double)this.decodeX(1.8237178F), (double)this.decodeY(0.37692308F));
      this.path.lineTo((double)this.decodeX(1.3878205F), (double)this.decodeY(0.94230765F));
      this.path.lineTo((double)this.decodeX(1.9775641F), (double)this.decodeY(1.0256411F));
      this.path.lineTo((double)this.decodeX(1.9839742F), (double)this.decodeY(1.1474359F));
      this.path.lineTo((double)this.decodeX(1.4070512F), (double)this.decodeY(1.2083334F));
      this.path.lineTo((double)this.decodeX(1.7980769F), (double)this.decodeY(1.7307692F));
      this.path.lineTo((double)this.decodeX(1.7532051F), (double)this.decodeY(1.8269231F));
      this.path.lineTo((double)this.decodeX(1.2211539F), (double)this.decodeY(1.3365384F));
      this.path.lineTo((double)this.decodeX(1.1506411F), (double)this.decodeY(1.9839742F));
      this.path.lineTo((double)this.decodeX(1.0288461F), (double)this.decodeY(1.9775641F));
      this.path.lineTo((double)this.decodeX(0.95384616F), (double)this.decodeY(1.3429488F));
      this.path.lineTo((double)this.decodeX(0.28846154F), (double)this.decodeY(1.8012822F));
      this.path.lineTo((double)this.decodeX(0.20769231F), (double)this.decodeY(1.7371795F));
      this.path.lineTo((double)this.decodeX(0.75F), (double)this.decodeY(1.173077F));
      this.path.lineTo((double)this.decodeX(0.011538462F), (double)this.decodeY(1.1634616F));
      this.path.lineTo((double)this.decodeX(0.015384616F), (double)this.decodeY(1.0224359F));
      this.path.lineTo((double)this.decodeX(0.79615384F), (double)this.decodeY(0.94230765F));
      this.path.lineTo((double)this.decodeX(0.23461537F), (double)this.decodeY(0.33076924F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath22() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.58461535F), (double)this.decodeY(0.6615385F));
      this.path.lineTo((double)this.decodeX(0.68846154F), (double)this.decodeY(0.56923074F));
      this.path.lineTo((double)this.decodeX(0.9884615F), (double)this.decodeY(0.80769235F));
      this.path.lineTo((double)this.decodeX(1.0352564F), (double)this.decodeY(0.43076926F));
      this.path.lineTo((double)this.decodeX(1.1282052F), (double)this.decodeY(0.43846154F));
      this.path.lineTo((double)this.decodeX(1.1891025F), (double)this.decodeY(0.80769235F));
      this.path.lineTo((double)this.decodeX(1.4006411F), (double)this.decodeY(0.59615386F));
      this.path.lineTo((double)this.decodeX(1.4967948F), (double)this.decodeY(0.70384616F));
      this.path.lineTo((double)this.decodeX(1.3173077F), (double)this.decodeY(0.9384615F));
      this.path.lineTo((double)this.decodeX(1.625F), (double)this.decodeY(1.0256411F));
      this.path.lineTo((double)this.decodeX(1.6282051F), (double)this.decodeY(1.1346154F));
      this.path.lineTo((double)this.decodeX(1.2564102F), (double)this.decodeY(1.176282F));
      this.path.lineTo((double)this.decodeX(1.4711539F), (double)this.decodeY(1.3910257F));
      this.path.lineTo((double)this.decodeX(1.4070512F), (double)this.decodeY(1.4807693F));
      this.path.lineTo((double)this.decodeX(1.1858975F), (double)this.decodeY(1.2724359F));
      this.path.lineTo((double)this.decodeX(1.1474359F), (double)this.decodeY(1.6602564F));
      this.path.lineTo((double)this.decodeX(1.0416666F), (double)this.decodeY(1.6602564F));
      this.path.lineTo((double)this.decodeX(0.9769231F), (double)this.decodeY(1.2884616F));
      this.path.lineTo((double)this.decodeX(0.6923077F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(0.6423077F), (double)this.decodeY(1.3782052F));
      this.path.lineTo((double)this.decodeX(0.83076924F), (double)this.decodeY(1.176282F));
      this.path.lineTo((double)this.decodeX(0.46923074F), (double)this.decodeY(1.1474359F));
      this.path.lineTo((double)this.decodeX(0.48076925F), (double)this.decodeY(1.0064102F));
      this.path.lineTo((double)this.decodeX(0.8230769F), (double)this.decodeY(0.98461545F));
      this.path.lineTo((double)this.decodeX(0.58461535F), (double)this.decodeY(0.6615385F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect6() {
      this.rect.setRect((double)this.decodeX(0.2F), (double)this.decodeY(0.0F), (double)(this.decodeX(2.8F) - this.decodeX(0.2F)), (double)(this.decodeY(2.2F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect7() {
      this.rect.setRect((double)this.decodeX(0.2F), (double)this.decodeY(2.2F), (double)(this.decodeX(2.8F) - this.decodeX(0.2F)), (double)(this.decodeY(3.0F) - this.decodeY(2.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect8() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(0.2F), (double)(this.decodeX(2.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.2F) - this.decodeY(0.2F)));
      return this.rect;
   }

   private Path2D decodePath23() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.4F), (double)this.decodeY(2.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.2F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.2F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect9() {
      this.rect.setRect((double)this.decodeX(0.6F), (double)this.decodeY(2.8F), (double)(this.decodeX(1.6666667F) - this.decodeX(0.6F)), (double)(this.decodeY(3.0F) - this.decodeY(2.8F)));
      return this.rect;
   }

   private Rectangle2D decodeRect10() {
      this.rect.setRect((double)this.decodeX(1.8333333F), (double)this.decodeY(2.8F), (double)(this.decodeX(2.4F) - this.decodeX(1.8333333F)), (double)(this.decodeY(3.0F) - this.decodeY(2.8F)));
      return this.rect;
   }

   private Ellipse2D decodeEllipse1() {
      this.ellipse.setFrame((double)this.decodeX(0.6F), (double)this.decodeY(2.4F), (double)(this.decodeX(0.8F) - this.decodeX(0.6F)), (double)(this.decodeY(2.6F) - this.decodeY(2.4F)));
      return this.ellipse;
   }

   private Path2D decodePath24() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(0.4F));
      this.path.curveTo((double)this.decodeAnchorX(1.0F, 1.0F), (double)this.decodeAnchorY(0.4F, -1.0F), (double)this.decodeAnchorX(2.0F, -1.0F), (double)this.decodeAnchorY(0.4F, -1.0F), (double)this.decodeX(2.0F), (double)this.decodeY(0.4F));
      this.path.curveTo((double)this.decodeAnchorX(2.0F, 1.0F), (double)this.decodeAnchorY(0.4F, 1.0F), (double)this.decodeAnchorX(2.2F, 0.0F), (double)this.decodeAnchorY(1.0F, -1.0F), (double)this.decodeX(2.2F), (double)this.decodeY(1.0F));
      this.path.curveTo((double)this.decodeAnchorX(2.2F, 0.0F), (double)this.decodeAnchorY(1.0F, 1.0F), (double)this.decodeAnchorX(2.2F, 0.0F), (double)this.decodeAnchorY(1.5F, -2.0F), (double)this.decodeX(2.2F), (double)this.decodeY(1.5F));
      this.path.curveTo((double)this.decodeAnchorX(2.2F, 0.0F), (double)this.decodeAnchorY(1.5F, 2.0F), (double)this.decodeAnchorX(1.6666667F, 1.0F), (double)this.decodeAnchorY(1.8333333F, 0.0F), (double)this.decodeX(1.6666667F), (double)this.decodeY(1.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(1.6666667F, -1.0F), (double)this.decodeAnchorY(1.8333333F, 0.0F), (double)this.decodeAnchorX(1.3333334F, 1.0F), (double)this.decodeAnchorY(1.8333333F, 0.0F), (double)this.decodeX(1.3333334F), (double)this.decodeY(1.8333333F));
      this.path.curveTo((double)this.decodeAnchorX(1.3333334F, -1.0F), (double)this.decodeAnchorY(1.8333333F, 0.0F), (double)this.decodeAnchorX(0.8F, 0.0F), (double)this.decodeAnchorY(1.5F, 2.0F), (double)this.decodeX(0.8F), (double)this.decodeY(1.5F));
      this.path.curveTo((double)this.decodeAnchorX(0.8F, 0.0F), (double)this.decodeAnchorY(1.5F, -2.0F), (double)this.decodeAnchorX(0.8F, 0.0F), (double)this.decodeAnchorY(1.0F, 1.0F), (double)this.decodeX(0.8F), (double)this.decodeY(1.0F));
      this.path.curveTo((double)this.decodeAnchorX(0.8F, 0.0F), (double)this.decodeAnchorY(1.0F, -1.0F), (double)this.decodeAnchorX(1.0F, -1.0F), (double)this.decodeAnchorY(0.4F, 1.0F), (double)this.decodeX(1.0F), (double)this.decodeY(0.4F));
      this.path.closePath();
      return this.path;
   }

   private Ellipse2D decodeEllipse2() {
      this.ellipse.setFrame((double)this.decodeX(0.6F), (double)this.decodeY(0.2F), (double)(this.decodeX(0.8F) - this.decodeX(0.6F)), (double)(this.decodeY(0.4F) - this.decodeY(0.2F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse3() {
      this.ellipse.setFrame((double)this.decodeX(2.2F), (double)this.decodeY(0.2F), (double)(this.decodeX(2.4F) - this.decodeX(2.2F)), (double)(this.decodeY(0.4F) - this.decodeY(0.2F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse4() {
      this.ellipse.setFrame((double)this.decodeX(2.2F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.4F) - this.decodeX(2.2F)), (double)(this.decodeY(1.1666666F) - this.decodeY(1.0F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse5() {
      this.ellipse.setFrame((double)this.decodeX(2.2F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(2.4F) - this.decodeX(2.2F)), (double)(this.decodeY(1.8333333F) - this.decodeY(1.6666667F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse6() {
      this.ellipse.setFrame((double)this.decodeX(0.6F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(0.8F) - this.decodeX(0.6F)), (double)(this.decodeY(1.8333333F) - this.decodeY(1.6666667F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse7() {
      this.ellipse.setFrame((double)this.decodeX(0.6F), (double)this.decodeY(1.0F), (double)(this.decodeX(0.8F) - this.decodeX(0.6F)), (double)(this.decodeY(1.1666666F) - this.decodeY(1.0F)));
      return this.ellipse;
   }

   private Rectangle2D decodeRect11() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(2.2F), (double)(this.decodeX(1.0F) - this.decodeX(0.8F)), (double)(this.decodeY(2.6F) - this.decodeY(2.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect12() {
      this.rect.setRect((double)this.decodeX(1.1666666F), (double)this.decodeY(2.2F), (double)(this.decodeX(1.3333334F) - this.decodeX(1.1666666F)), (double)(this.decodeY(2.6F) - this.decodeY(2.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect13() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(2.2F), (double)(this.decodeX(1.6666667F) - this.decodeX(1.5F)), (double)(this.decodeY(2.6F) - this.decodeY(2.2F)));
      return this.rect;
   }

   private Path2D decodePath25() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath26() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(0.4F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath27() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.8F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.6666667F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath28() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.1666666F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.1666666F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.2F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.1666666F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath29() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.8F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.0F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(0.2F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect14() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(2.6F), (double)(this.decodeX(2.2F) - this.decodeX(0.8F)), (double)(this.decodeY(2.8F) - this.decodeY(2.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect15() {
      this.rect.setRect((double)this.decodeX(0.36153847F), (double)this.decodeY(2.3576922F), (double)(this.decodeX(0.63461536F) - this.decodeX(0.36153847F)), (double)(this.decodeY(2.6807692F) - this.decodeY(2.3576922F)));
      return this.rect;
   }

   private Rectangle2D decodeRect16() {
      this.rect.setRect((double)this.decodeX(2.376923F), (double)this.decodeY(2.3807693F), (double)(this.decodeX(2.6384616F) - this.decodeX(2.376923F)), (double)(this.decodeY(2.6846154F) - this.decodeY(2.3807693F)));
      return this.rect;
   }

   private Rectangle2D decodeRect17() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(2.4F), (double)(this.decodeX(0.6F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(2.4F)));
      return this.rect;
   }

   private Rectangle2D decodeRect18() {
      this.rect.setRect((double)this.decodeX(2.4F), (double)this.decodeY(2.4F), (double)(this.decodeX(2.6F) - this.decodeX(2.4F)), (double)(this.decodeY(2.6F) - this.decodeY(2.4F)));
      return this.rect;
   }

   private Path2D decodePath30() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.4F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(1.5F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath31() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(2.6F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.8F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(1.5F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect19() {
      this.rect.setRect((double)this.decodeX(1.6666667F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(2.2F) - this.decodeX(1.6666667F)), (double)(this.decodeY(2.2F) - this.decodeY(1.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect20() {
      this.rect.setRect((double)this.decodeX(1.8333333F), (double)this.decodeY(1.8333333F), (double)(this.decodeX(2.0F) - this.decodeX(1.8333333F)), (double)(this.decodeY(2.0F) - this.decodeY(1.8333333F)));
      return this.rect;
   }

   private Path2D decodePath32() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.0F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.1666666F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.8F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect21() {
      this.rect.setRect((double)this.decodeX(1.1666666F), (double)this.decodeY(1.8333333F), (double)(this.decodeX(1.3333334F) - this.decodeX(1.1666666F)), (double)(this.decodeY(2.6F) - this.decodeY(1.8333333F)));
      return this.rect;
   }

   private Path2D decodePath33() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(1.3974359F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.596154F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(2.6F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(3.0F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.0F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(1.3333334F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath34() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.2576923F), (double)this.decodeY(1.3717948F));
      this.path.lineTo((double)this.decodeX(0.2F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(0.3230769F), (double)this.decodeY(1.4711539F));
      this.path.lineTo((double)this.decodeX(1.4006411F), (double)this.decodeY(0.40384617F));
      this.path.lineTo((double)this.decodeX(1.5929487F), (double)this.decodeY(0.4F));
      this.path.lineTo((double)this.decodeX(2.6615386F), (double)this.decodeY(1.4615384F));
      this.path.lineTo((double)this.decodeX(2.8F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(2.7461538F), (double)this.decodeY(1.3653846F));
      this.path.lineTo((double)this.decodeX(1.6089742F), (double)this.decodeY(0.19615385F));
      this.path.lineTo((double)this.decodeX(1.4070512F), (double)this.decodeY(0.2F));
      this.path.lineTo((double)this.decodeX(0.2576923F), (double)this.decodeY(1.3717948F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath35() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.6F), (double)this.decodeY(1.5F));
      this.path.lineTo((double)this.decodeX(1.3333334F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(0.6F), (double)this.decodeY(1.5F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath36() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(1.6666667F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(0.6F));
      this.path.lineTo((double)this.decodeX(1.5F), (double)this.decodeY(1.1666666F));
      this.path.lineTo((double)this.decodeX(2.0F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.6666667F));
      this.path.lineTo((double)this.decodeX(2.4F), (double)this.decodeY(1.3333334F));
      this.path.lineTo((double)this.decodeX(1.6666667F), (double)this.decodeY(0.6F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect22() {
      this.rect.setRect((double)this.decodeX(0.2F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.2F)), (double)(this.decodeY(2.8F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect23() {
      this.rect.setRect((double)this.decodeX(0.4F), (double)this.decodeY(0.2F), (double)(this.decodeX(2.8F) - this.decodeX(0.4F)), (double)(this.decodeY(2.6F) - this.decodeY(0.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect24() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(0.6F), (double)(this.decodeX(1.3333334F) - this.decodeX(1.0F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect25() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(1.3333334F), (double)(this.decodeX(2.4F) - this.decodeX(1.5F)), (double)(this.decodeY(1.5F) - this.decodeY(1.3333334F)));
      return this.rect;
   }

   private Rectangle2D decodeRect26() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(2.0F), (double)(this.decodeX(2.4F) - this.decodeX(1.5F)), (double)(this.decodeY(2.2F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Ellipse2D decodeEllipse8() {
      this.ellipse.setFrame((double)this.decodeX(0.6F), (double)this.decodeY(0.8F), (double)(this.decodeX(2.2F) - this.decodeX(0.6F)), (double)(this.decodeY(2.4F) - this.decodeY(0.8F)));
      return this.ellipse;
   }

   private Ellipse2D decodeEllipse9() {
      this.ellipse.setFrame((double)this.decodeX(0.8F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(0.8F)), (double)(this.decodeY(2.2F) - this.decodeY(1.0F)));
      return this.ellipse;
   }

   private Path2D decodePath37() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(0.4F), (double)this.decodeY(3.0F));
      this.path.lineTo((double)this.decodeX(1.0F), (double)this.decodeY(2.2F));
      this.path.lineTo((double)this.decodeX(0.8F), (double)this.decodeY(1.8333333F));
      this.path.lineTo((double)this.decodeX(0.0F), (double)this.decodeY(2.8F));
      this.path.closePath();
      return this.path;
   }

   private Path2D decodePath38() {
      this.path.reset();
      this.path.moveTo((double)this.decodeX(0.1826087F), (double)this.decodeY(2.7217393F));
      this.path.lineTo((double)this.decodeX(0.2826087F), (double)this.decodeY(2.8217392F));
      this.path.lineTo((double)this.decodeX(1.0181159F), (double)this.decodeY(2.095652F));
      this.path.lineTo((double)this.decodeX(0.9130435F), (double)this.decodeY(1.9891305F));
      this.path.lineTo((double)this.decodeX(0.1826087F), (double)this.decodeY(2.7217393F));
      this.path.closePath();
      return this.path;
   }

   private Rectangle2D decodeRect27() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.3333334F), (double)(this.decodeX(1.3333334F) - this.decodeX(1.0F)), (double)(this.decodeY(1.5F) - this.decodeY(1.3333334F)));
      return this.rect;
   }

   private Rectangle2D decodeRect28() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(1.3333334F), (double)(this.decodeX(1.8333333F) - this.decodeX(1.5F)), (double)(this.decodeY(1.5F) - this.decodeY(1.3333334F)));
      return this.rect;
   }

   private Rectangle2D decodeRect29() {
      this.rect.setRect((double)this.decodeX(1.5F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(1.8333333F) - this.decodeX(1.5F)), (double)(this.decodeY(1.8333333F) - this.decodeY(1.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect30() {
      this.rect.setRect((double)this.decodeX(1.0F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(1.3333334F) - this.decodeX(1.0F)), (double)(this.decodeY(1.8333333F) - this.decodeY(1.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect31() {
      this.rect.setRect((double)this.decodeX(0.0F), (double)this.decodeY(0.0F), (double)(this.decodeX(3.0F) - this.decodeX(0.0F)), (double)(this.decodeY(2.8F) - this.decodeY(0.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect32() {
      this.rect.setRect((double)this.decodeX(0.2F), (double)this.decodeY(0.2F), (double)(this.decodeX(2.8F) - this.decodeX(0.2F)), (double)(this.decodeY(2.6F) - this.decodeY(0.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect33() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(0.6F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect34() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(0.6F), (double)(this.decodeX(2.2F) - this.decodeX(1.3333334F)), (double)(this.decodeY(0.8F) - this.decodeY(0.6F)));
      return this.rect;
   }

   private Rectangle2D decodeRect35() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(1.0F), (double)(this.decodeX(2.0F) - this.decodeX(1.3333334F)), (double)(this.decodeY(1.1666666F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect36() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(1.0F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(1.1666666F) - this.decodeY(1.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect37() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(1.3333334F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(1.5F) - this.decodeY(1.3333334F)));
      return this.rect;
   }

   private Rectangle2D decodeRect38() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(1.3333334F), (double)(this.decodeX(2.2F) - this.decodeX(1.3333334F)), (double)(this.decodeY(1.5F) - this.decodeY(1.3333334F)));
      return this.rect;
   }

   private Rectangle2D decodeRect39() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(1.8333333F) - this.decodeY(1.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect40() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(1.6666667F), (double)(this.decodeX(2.0F) - this.decodeX(1.3333334F)), (double)(this.decodeY(1.8333333F) - this.decodeY(1.6666667F)));
      return this.rect;
   }

   private Rectangle2D decodeRect41() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(2.0F), (double)(this.decodeX(2.2F) - this.decodeX(1.3333334F)), (double)(this.decodeY(2.2F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect42() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(2.0F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(2.2F) - this.decodeY(2.0F)));
      return this.rect;
   }

   private Rectangle2D decodeRect43() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(0.8F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(1.0F) - this.decodeY(0.8F)));
      return this.rect;
   }

   private Rectangle2D decodeRect44() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(0.8F), (double)(this.decodeX(2.2F) - this.decodeX(1.3333334F)), (double)(this.decodeY(1.0F) - this.decodeY(0.8F)));
      return this.rect;
   }

   private Rectangle2D decodeRect45() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(1.1666666F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(1.3333334F) - this.decodeY(1.1666666F)));
      return this.rect;
   }

   private Rectangle2D decodeRect46() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(1.1666666F), (double)(this.decodeX(2.0F) - this.decodeX(1.3333334F)), (double)(this.decodeY(1.3333334F) - this.decodeY(1.1666666F)));
      return this.rect;
   }

   private Rectangle2D decodeRect47() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(1.5F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(1.6666667F) - this.decodeY(1.5F)));
      return this.rect;
   }

   private Rectangle2D decodeRect48() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(1.5F), (double)(this.decodeX(2.2F) - this.decodeX(1.3333334F)), (double)(this.decodeY(1.6666667F) - this.decodeY(1.5F)));
      return this.rect;
   }

   private Rectangle2D decodeRect49() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(1.8333333F), (double)(this.decodeX(2.0F) - this.decodeX(1.3333334F)), (double)(this.decodeY(2.0F) - this.decodeY(1.8333333F)));
      return this.rect;
   }

   private Rectangle2D decodeRect50() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(1.8333333F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(2.0F) - this.decodeY(1.8333333F)));
      return this.rect;
   }

   private Rectangle2D decodeRect51() {
      this.rect.setRect((double)this.decodeX(0.8F), (double)this.decodeY(2.2F), (double)(this.decodeX(1.1666666F) - this.decodeX(0.8F)), (double)(this.decodeY(2.4F) - this.decodeY(2.2F)));
      return this.rect;
   }

   private Rectangle2D decodeRect52() {
      this.rect.setRect((double)this.decodeX(1.3333334F), (double)this.decodeY(2.2F), (double)(this.decodeX(2.2F) - this.decodeX(1.3333334F)), (double)(this.decodeY(2.4F) - this.decodeY(2.2F)));
      return this.rect;
   }

   private Paint decodeGradient1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.046296295F * var5 + var3, 0.9675926F * var6 + var4, 0.4861111F * var5 + var3, 0.5324074F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color4, this.decodeColor(this.color4, this.color5, 0.5F), this.color5});
   }

   private Paint decodeGradient2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color7, 0.5F), this.color7});
   }

   private Paint decodeGradient3(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.04191617F, 0.10329342F, 0.16467066F, 0.24550897F, 0.3263473F, 0.6631737F, 1.0F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color13, 0.5F), this.color13, this.decodeColor(this.color13, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient4(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color6, this.decodeColor(this.color6, this.color15, 0.5F), this.color15});
   }

   private Paint decodeGradient5(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color19, this.decodeColor(this.color19, this.color20, 0.5F), this.color20});
   }

   private Paint decodeGradient6(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.12724552F, 0.25449103F, 0.62724555F, 1.0F}, new Color[]{this.color21, this.decodeColor(this.color21, this.color22, 0.5F), this.color22, this.decodeColor(this.color22, this.color23, 0.5F), this.color23});
   }

   private Paint decodeGradient7(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.06392045F, 0.1278409F, 0.5213069F, 0.91477275F}, new Color[]{this.color25, this.decodeColor(this.color25, this.color26, 0.5F), this.color26, this.decodeColor(this.color26, this.color27, 0.5F), this.color27});
   }

   private Paint decodeGradient8(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.048295453F, 0.09659091F, 0.5482955F, 1.0F}, new Color[]{this.color28, this.decodeColor(this.color28, this.color6, 0.5F), this.color6, this.decodeColor(this.color6, this.color15, 0.5F), this.color15});
   }

   private Paint decodeGradient9(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color29, this.decodeColor(this.color29, this.color30, 0.5F), this.color30});
   }

   private Paint decodeGradient10(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.06534091F, 0.13068181F, 0.3096591F, 0.48863637F, 0.7443182F, 1.0F}, new Color[]{this.color11, this.decodeColor(this.color11, this.color12, 0.5F), this.color12, this.decodeColor(this.color12, this.color31, 0.5F), this.color31, this.decodeColor(this.color31, this.color14, 0.5F), this.color14});
   }

   private Paint decodeGradient11(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color33, this.decodeColor(this.color33, this.color34, 0.5F), this.color34});
   }

   private Paint decodeGradient12(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color36, this.decodeColor(this.color36, this.color37, 0.5F), this.color37});
   }

   private Paint decodeRadial1(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeRadialGradient(0.5F * var5 + var3, 1.0F * var6 + var4, 0.53913116F, new float[]{0.11290322F, 0.17419355F, 0.23548387F, 0.31129032F, 0.38709676F, 0.47903225F, 0.57096773F}, new Color[]{this.color40, this.decodeColor(this.color40, this.color41, 0.5F), this.color41, this.decodeColor(this.color41, this.color41, 0.5F), this.color41, this.decodeColor(this.color41, this.color42, 0.5F), this.color42});
   }

   private Paint decodeGradient13(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color45, this.decodeColor(this.color45, this.color46, 0.5F), this.color46});
   }

   private Paint decodeGradient14(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color47, this.decodeColor(this.color47, this.color48, 0.5F), this.color48});
   }

   private Paint decodeGradient15(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.3983871F, 0.7967742F, 0.8983871F, 1.0F}, new Color[]{this.color51, this.decodeColor(this.color51, this.color52, 0.5F), this.color52, this.decodeColor(this.color52, this.color51, 0.5F), this.color51});
   }

   private Paint decodeGradient16(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.061290324F, 0.12258065F, 0.5016129F, 0.88064516F, 0.9403226F, 1.0F}, new Color[]{this.color57, this.decodeColor(this.color57, this.color58, 0.5F), this.color58, this.decodeColor(this.color58, this.color59, 0.5F), this.color59, this.decodeColor(this.color59, this.color44, 0.5F), this.color44});
   }

   private Paint decodeGradient17(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.05F, 0.1F, 0.19193548F, 0.28387097F, 0.5209677F, 0.7580645F, 0.87903225F, 1.0F}, new Color[]{this.color60, this.decodeColor(this.color60, this.color61, 0.5F), this.color61, this.decodeColor(this.color61, this.color62, 0.5F), this.color62, this.decodeColor(this.color62, this.color63, 0.5F), this.color63, this.decodeColor(this.color63, this.color64, 0.5F), this.color64});
   }

   private Paint decodeGradient18(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.058064517F, 0.090322584F, 0.12258065F, 0.15645161F, 0.19032258F, 0.22741935F, 0.26451612F, 0.31290323F, 0.36129034F, 0.38225806F, 0.4032258F, 0.4596774F, 0.516129F, 0.54193544F, 0.56774193F, 0.61451614F, 0.66129035F, 0.70645165F, 0.7516129F}, new Color[]{this.color65, this.decodeColor(this.color65, this.color40, 0.5F), this.color40, this.decodeColor(this.color40, this.color40, 0.5F), this.color40, this.decodeColor(this.color40, this.color65, 0.5F), this.color65, this.decodeColor(this.color65, this.color65, 0.5F), this.color65, this.decodeColor(this.color65, this.color40, 0.5F), this.color40, this.decodeColor(this.color40, this.color40, 0.5F), this.color40, this.decodeColor(this.color40, this.color66, 0.5F), this.color66, this.decodeColor(this.color66, this.color66, 0.5F), this.color66, this.decodeColor(this.color66, this.color40, 0.5F), this.color40});
   }

   private Paint decodeGradient19(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color67, this.decodeColor(this.color67, this.color67, 0.5F), this.color67});
   }

   private Paint decodeGradient20(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color74, this.decodeColor(this.color74, this.color75, 0.5F), this.color75});
   }

   private Paint decodeGradient21(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color77, this.decodeColor(this.color77, this.color78, 0.5F), this.color78});
   }

   private Paint decodeGradient22(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color79, this.decodeColor(this.color79, this.color80, 0.5F), this.color80});
   }

   private Paint decodeGradient23(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color81, this.decodeColor(this.color81, this.color82, 0.5F), this.color82});
   }

   private Paint decodeGradient24(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.43076923F * var5 + var3, 0.37820512F * var6 + var4, 0.7076923F * var5 + var3, 0.6730769F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color84, this.decodeColor(this.color84, this.color85, 0.5F), this.color85});
   }

   private Paint decodeGradient25(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.63076925F * var5 + var3, 0.3621795F * var6 + var4, 0.28846154F * var5 + var3, 0.73397434F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color84, this.decodeColor(this.color84, this.color86, 0.5F), this.color86});
   }

   private Paint decodeGradient26(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color87, this.decodeColor(this.color87, this.color88, 0.5F), this.color88});
   }

   private Paint decodeGradient27(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.056818184F, 0.11363637F, 0.34232956F, 0.57102275F, 0.7855114F, 1.0F}, new Color[]{this.color89, this.decodeColor(this.color89, this.color90, 0.5F), this.color90, this.decodeColor(this.color90, this.color91, 0.5F), this.color91, this.decodeColor(this.color91, this.color92, 0.5F), this.color92});
   }

   private Paint decodeGradient28(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.5F, 1.0F}, new Color[]{this.color95, this.decodeColor(this.color95, this.color96, 0.5F), this.color96});
   }

   private Paint decodeRadial2(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeRadialGradient(0.49223602F * var5 + var3, 0.9751553F * var6 + var4, 0.73615754F, new float[]{0.0F, 0.40625F, 1.0F}, new Color[]{this.color97, this.decodeColor(this.color97, this.color98, 0.5F), this.color98});
   }

   private Paint decodeGradient29(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.0F * var6 + var4, 1.0F * var5 + var3, 1.0F * var6 + var4, new float[]{0.38352272F, 0.4190341F, 0.45454547F, 0.484375F, 0.51420456F}, new Color[]{this.color99, this.decodeColor(this.color99, this.color100, 0.5F), this.color100, this.decodeColor(this.color100, this.color101, 0.5F), this.color101});
   }

   private Paint decodeGradient30(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(1.0F * var5 + var3, 0.0F * var6 + var4, 0.0F * var5 + var3, 1.0F * var6 + var4, new float[]{0.12215909F, 0.16051137F, 0.19886364F, 0.2627841F, 0.32670453F, 0.43039775F, 0.53409094F}, new Color[]{this.color102, this.decodeColor(this.color102, this.color35, 0.5F), this.color35, this.decodeColor(this.color35, this.color35, 0.5F), this.color35, this.decodeColor(this.color35, this.color103, 0.5F), this.color103});
   }

   private Paint decodeGradient31(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.5F * var5 + var3, 0.0F * var6 + var4, 0.5F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.038352273F, 0.07670455F, 0.24289773F, 0.4090909F, 0.7045455F, 1.0F}, new Color[]{this.color89, this.decodeColor(this.color89, this.color90, 0.5F), this.color90, this.decodeColor(this.color90, this.color108, 0.5F), this.color108, this.decodeColor(this.color108, this.color92, 0.5F), this.color92});
   }

   private Paint decodeGradient32(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.0F * var6 + var4, 1.0F * var5 + var3, 1.0F * var6 + var4, new float[]{0.25F, 0.33522725F, 0.42045453F, 0.50142044F, 0.5823864F}, new Color[]{this.color109, this.decodeColor(this.color109, this.color110, 0.5F), this.color110, this.decodeColor(this.color110, this.color109, 0.5F), this.color109});
   }

   private Paint decodeGradient33(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.25F * var5 + var3, 0.0F * var6 + var4, 0.75F * var5 + var3, 1.0F * var6 + var4, new float[]{0.0F, 0.24147727F, 0.48295453F, 0.74147725F, 1.0F}, new Color[]{this.color114, this.decodeColor(this.color114, this.color115, 0.5F), this.color115, this.decodeColor(this.color115, this.color114, 0.5F), this.color114});
   }

   private Paint decodeGradient34(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.0F * var6 + var4, 1.0F * var5 + var3, 0.0F * var6 + var4, new float[]{0.0F, 0.21732955F, 0.4346591F}, new Color[]{this.color117, this.decodeColor(this.color117, this.color118, 0.5F), this.color118});
   }

   private Paint decodeGradient35(Shape var1) {
      Rectangle2D var2 = var1.getBounds2D();
      float var3 = (float)var2.getX();
      float var4 = (float)var2.getY();
      float var5 = (float)var2.getWidth();
      float var6 = (float)var2.getHeight();
      return this.decodeGradient(0.0F * var5 + var3, 0.0F * var6 + var4, 1.0F * var5 + var3, 0.0F * var6 + var4, new float[]{0.0F, 0.21448864F, 0.42897728F, 0.7144886F, 1.0F}, new Color[]{this.color119, this.decodeColor(this.color119, this.color120, 0.5F), this.color120, this.decodeColor(this.color120, this.color119, 0.5F), this.color119});
   }
}
