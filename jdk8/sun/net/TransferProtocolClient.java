package sun.net;

import java.io.IOException;
import java.util.Vector;

public class TransferProtocolClient extends NetworkClient {
   static final boolean debug = false;
   protected Vector<String> serverResponse = new Vector(1);
   protected int lastReplyCode;

   public int readServerResponse() throws IOException {
      StringBuffer var1 = new StringBuffer(32);
      int var3 = -1;
      this.serverResponse.setSize(0);

      int var4;
      while(true) {
         String var5;
         while(true) {
            int var2;
            while((var2 = this.serverInput.read()) != -1) {
               if (var2 == 13 && (var2 = this.serverInput.read()) != 10) {
                  var1.append('\r');
               }

               var1.append((char)var2);
               if (var2 == 10) {
                  break;
               }
            }

            var5 = var1.toString();
            var1.setLength(0);
            if (var5.length() == 0) {
               var4 = -1;
               break;
            }

            try {
               var4 = Integer.parseInt(var5.substring(0, 3));
               break;
            } catch (NumberFormatException var7) {
               var4 = -1;
               break;
            } catch (StringIndexOutOfBoundsException var8) {
            }
         }

         this.serverResponse.addElement(var5);
         if (var3 != -1) {
            if (var4 == var3 && (var5.length() < 4 || var5.charAt(3) != '-')) {
               boolean var9 = true;
               break;
            }
         } else {
            if (var5.length() < 4 || var5.charAt(3) != '-') {
               break;
            }

            var3 = var4;
         }
      }

      return this.lastReplyCode = var4;
   }

   public void sendServer(String var1) {
      this.serverOutput.print(var1);
   }

   public String getResponseString() {
      return (String)this.serverResponse.elementAt(0);
   }

   public Vector<String> getResponseStrings() {
      return this.serverResponse;
   }

   public TransferProtocolClient(String var1, int var2) throws IOException {
      super(var1, var2);
   }

   public TransferProtocolClient() {
   }
}
