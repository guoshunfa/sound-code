package sun.net.ftp.impl;

import sun.net.ftp.FtpClientProvider;

public class DefaultFtpClientProvider extends FtpClientProvider {
   public sun.net.ftp.FtpClient createFtpClient() {
      return FtpClient.create();
   }
}
