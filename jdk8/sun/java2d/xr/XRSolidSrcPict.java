package sun.java2d.xr;

public class XRSolidSrcPict {
   XRBackend con;
   XRSurfaceData srcPict;
   XRColor xrCol;
   int curPixVal;

   public XRSolidSrcPict(XRBackend var1, int var2) {
      this.con = var1;
      this.xrCol = new XRColor();
      this.curPixVal = -16777216;
      int var3 = var1.createPixmap(var2, 32, 1, 1);
      int var4 = var1.createPicture(var3, 0);
      var1.setPictureRepeat(var4, 1);
      var1.renderRectangle(var4, (byte)1, XRColor.FULL_ALPHA, 0, 0, 1, 1);
      this.srcPict = new XRSurfaceData.XRInternalSurfaceData(var1, var4);
   }

   public XRSurfaceData prepareSrcPict(int var1) {
      if (var1 != this.curPixVal) {
         this.xrCol.setColorValues(var1, false);
         this.con.renderRectangle(this.srcPict.picture, (byte)1, this.xrCol, 0, 0, 1, 1);
         this.curPixVal = var1;
      }

      return this.srcPict;
   }
}
