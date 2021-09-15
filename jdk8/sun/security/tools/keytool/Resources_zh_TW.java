package sun.security.tools.keytool;

import java.util.ListResourceBundle;

public class Resources_zh_TW extends ListResourceBundle {
   private static final Object[][] contents = new Object[][]{{"NEWLINE", "\n"}, {"STAR", "*******************************************"}, {"STARNN", "*******************************************\n\n"}, {".OPTION.", " [OPTION]..."}, {"Options.", "選項:"}, {"Use.keytool.help.for.all.available.commands", "使用 \"keytool -help\" 取得所有可用的命令"}, {"Key.and.Certificate.Management.Tool", "金鑰與憑證管理工具"}, {"Commands.", "命令:"}, {"Use.keytool.command.name.help.for.usage.of.command.name", "使用 \"keytool -command_name -help\" 取得 command_name 的用法"}, {"Generates.a.certificate.request", "產生憑證要求"}, {"Changes.an.entry.s.alias", "變更項目的別名"}, {"Deletes.an.entry", "刪除項目"}, {"Exports.certificate", "匯出憑證"}, {"Generates.a.key.pair", "產生金鑰組"}, {"Generates.a.secret.key", "產生秘密金鑰"}, {"Generates.certificate.from.a.certificate.request", "從憑證要求產生憑證"}, {"Generates.CRL", "產生 CRL"}, {"Generated.keyAlgName.secret.key", "已產生 {0} 秘密金鑰"}, {"Generated.keysize.bit.keyAlgName.secret.key", "已產生 {0} 位元 {1} 秘密金鑰"}, {"Imports.entries.from.a.JDK.1.1.x.style.identity.database", "從 JDK 1.1.x-style 識別資料庫匯入項目"}, {"Imports.a.certificate.or.a.certificate.chain", "匯入憑證或憑證鏈"}, {"Imports.a.password", "匯入密碼"}, {"Imports.one.or.all.entries.from.another.keystore", "從其他金鑰儲存庫匯入一個或全部項目"}, {"Clones.a.key.entry", "複製金鑰項目"}, {"Changes.the.key.password.of.an.entry", "變更項目的金鑰密碼"}, {"Lists.entries.in.a.keystore", "列示金鑰儲存庫中的項目"}, {"Prints.the.content.of.a.certificate", "列印憑證的內容"}, {"Prints.the.content.of.a.certificate.request", "列印憑證要求的內容"}, {"Prints.the.content.of.a.CRL.file", "列印 CRL 檔案的內容"}, {"Generates.a.self.signed.certificate", "產生自行簽署的憑證"}, {"Changes.the.store.password.of.a.keystore", "變更金鑰儲存庫的儲存密碼"}, {"alias.name.of.the.entry.to.process", "要處理項目的別名名稱"}, {"destination.alias", "目的地別名"}, {"destination.key.password", "目的地金鑰密碼"}, {"destination.keystore.name", "目的地金鑰儲存庫名稱"}, {"destination.keystore.password.protected", "目的地金鑰儲存庫密碼保護"}, {"destination.keystore.provider.name", "目的地金鑰儲存庫提供者名稱"}, {"destination.keystore.password", "目的地金鑰儲存庫密碼"}, {"destination.keystore.type", "目的地金鑰儲存庫類型"}, {"distinguished.name", "辨別名稱"}, {"X.509.extension", "X.509 擴充套件"}, {"output.file.name", "輸出檔案名稱"}, {"input.file.name", "輸入檔案名稱"}, {"key.algorithm.name", "金鑰演算法名稱"}, {"key.password", "金鑰密碼"}, {"key.bit.size", "金鑰位元大小"}, {"keystore.name", "金鑰儲存庫名稱"}, {"new.password", "新密碼"}, {"do.not.prompt", "不要提示"}, {"password.through.protected.mechanism", "經由保護機制的密碼"}, {"provider.argument", "提供者引數"}, {"provider.class.name", "提供者類別名稱"}, {"provider.name", "提供者名稱"}, {"provider.classpath", "提供者類別路徑"}, {"output.in.RFC.style", "以 RFC 樣式輸出"}, {"signature.algorithm.name", "簽章演算法名稱"}, {"source.alias", "來源別名"}, {"source.key.password", "來源金鑰密碼"}, {"source.keystore.name", "來源金鑰儲存庫名稱"}, {"source.keystore.password.protected", "來源金鑰儲存庫密碼保護"}, {"source.keystore.provider.name", "來源金鑰儲存庫提供者名稱"}, {"source.keystore.password", "來源金鑰儲存庫密碼"}, {"source.keystore.type", "來源金鑰儲存庫類型"}, {"SSL.server.host.and.port", "SSL 伺服器主機與連接埠"}, {"signed.jar.file", "簽署的 jar 檔案"}, {"certificate.validity.start.date.time", "憑證有效性開始日期/時間"}, {"keystore.password", "金鑰儲存庫密碼"}, {"keystore.type", "金鑰儲存庫類型"}, {"trust.certificates.from.cacerts", "來自 cacerts 的信任憑證"}, {"verbose.output", "詳細資訊輸出"}, {"validity.number.of.days", "有效性日數"}, {"Serial.ID.of.cert.to.revoke", "要撤銷憑證的序列 ID"}, {"keytool.error.", "金鑰工具錯誤: "}, {"Illegal.option.", "無效的選項:"}, {"Illegal.value.", "無效值: "}, {"Unknown.password.type.", "不明的密碼類型: "}, {"Cannot.find.environment.variable.", "找不到環境變數: "}, {"Cannot.find.file.", "找不到檔案: "}, {"Command.option.flag.needs.an.argument.", "命令選項 {0} 需要引數。"}, {"Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value.", "警告: PKCS12 金鑰儲存庫不支援不同的儲存庫和金鑰密碼。忽略使用者指定的 {0} 值。"}, {".keystore.must.be.NONE.if.storetype.is.{0}", "如果 -storetype 為 {0}，則 -keystore 必須為 NONE"}, {"Too.many.retries.program.terminated", "重試次數太多，程式已終止"}, {".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}", "如果 -storetype 為 {0}，則不支援 -storepasswd 和 -keypasswd 命令"}, {".keypasswd.commands.not.supported.if.storetype.is.PKCS12", "如果 -storetype 為 PKCS12，則不支援 -keypasswd 命令"}, {".keypass.and.new.can.not.be.specified.if.storetype.is.{0}", "如果 -storetype 為 {0}，則不能指定 -keypass 和 -new"}, {"if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified", "如果指定 -protected，則不能指定 -storepass、-keypass 和 -new"}, {"if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified", "如果指定 -srcprotected，則不能指定 -srcstorepass 和 -srckeypass"}, {"if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified", "如果金鑰儲存庫不受密碼保護，則不能指定 -storepass、-keypass 和 -new"}, {"if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified", "如果來源金鑰儲存庫不受密碼保護，則不能指定 -srcstorepass 和 -srckeypass"}, {"Illegal.startdate.value", "無效的 startdate 值"}, {"Validity.must.be.greater.than.zero", "有效性必須大於零"}, {"provName.not.a.provider", "{0} 不是一個提供者"}, {"Usage.error.no.command.provided", "用法錯誤: 未提供命令"}, {"Source.keystore.file.exists.but.is.empty.", "來源金鑰儲存庫檔案存在，但為空: "}, {"Please.specify.srckeystore", "請指定 -srckeystore"}, {"Must.not.specify.both.v.and.rfc.with.list.command", " 'list' 命令不能同時指定 -v 及 -rfc"}, {"Key.password.must.be.at.least.6.characters", "金鑰密碼必須至少為 6 個字元"}, {"New.password.must.be.at.least.6.characters", "新的密碼必須至少為 6 個字元"}, {"Keystore.file.exists.but.is.empty.", "金鑰儲存庫檔案存在，但為空白: "}, {"Keystore.file.does.not.exist.", "金鑰儲存庫檔案不存在: "}, {"Must.specify.destination.alias", "必須指定目的地別名"}, {"Must.specify.alias", "必須指定別名"}, {"Keystore.password.must.be.at.least.6.characters", "金鑰儲存庫密碼必須至少為 6 個字元"}, {"Enter.the.password.to.be.stored.", "輸入要儲存的密碼:  "}, {"Enter.keystore.password.", "輸入金鑰儲存庫密碼:  "}, {"Enter.source.keystore.password.", "請輸入來源金鑰儲存庫密碼: "}, {"Enter.destination.keystore.password.", "請輸入目的地金鑰儲存庫密碼: "}, {"Keystore.password.is.too.short.must.be.at.least.6.characters", "金鑰儲存庫密碼太短 - 必須至少為 6 個字元"}, {"Unknown.Entry.Type", "不明的項目類型"}, {"Too.many.failures.Alias.not.changed", "太多錯誤。未變更別名"}, {"Entry.for.alias.alias.successfully.imported.", "已成功匯入別名 {0} 的項目。"}, {"Entry.for.alias.alias.not.imported.", "未匯入別名 {0} 的項目。"}, {"Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported.", "匯入別名 {0} 的項目時出現問題: {1}。\n未匯入別名 {0} 的項目。"}, {"Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled", "已完成匯入命令: 成功匯入 {0} 個項目，{1} 個項目失敗或已取消"}, {"Warning.Overwriting.existing.alias.alias.in.destination.keystore", "警告: 正在覆寫目的地金鑰儲存庫中的現有別名 {0}"}, {"Existing.entry.alias.alias.exists.overwrite.no.", "現有項目別名 {0} 存在，是否覆寫？[否]:  "}, {"Too.many.failures.try.later", "太多錯誤 - 請稍後再試"}, {"Certification.request.stored.in.file.filename.", "認證要求儲存在檔案 <{0}>"}, {"Submit.this.to.your.CA", "將此送出至您的 CA"}, {"if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified", "如果未指定別名，則不能指定 destalias 和 srckeypass"}, {"The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified.", "目的地 pkcs12 金鑰儲存庫的 storepass 和 keypass 不同。請重新以 -destkeypass 指定。"}, {"Certificate.stored.in.file.filename.", "憑證儲存在檔案 <{0}>"}, {"Certificate.reply.was.installed.in.keystore", "憑證回覆已安裝在金鑰儲存庫中"}, {"Certificate.reply.was.not.installed.in.keystore", "憑證回覆未安裝在金鑰儲存庫中"}, {"Certificate.was.added.to.keystore", "憑證已新增至金鑰儲存庫中"}, {"Certificate.was.not.added.to.keystore", "憑證未新增至金鑰儲存庫中"}, {".Storing.ksfname.", "[儲存 {0}]"}, {"alias.has.no.public.key.certificate.", "{0} 沒有公開金鑰 (憑證)"}, {"Cannot.derive.signature.algorithm", "無法取得簽章演算法"}, {"Alias.alias.does.not.exist", "別名 <{0}> 不存在"}, {"Alias.alias.has.no.certificate", "別名 <{0}> 沒有憑證"}, {"Key.pair.not.generated.alias.alias.already.exists", "沒有建立金鑰組，別名 <{0}> 已經存在"}, {"Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for", "針對 {4} 產生有效期 {3} 天的 {0} 位元 {1} 金鑰組以及自我簽署憑證 ({2})\n\t"}, {"Enter.key.password.for.alias.", "輸入 <{0}> 的金鑰密碼"}, {".RETURN.if.same.as.keystore.password.", "\t(RETURN 如果和金鑰儲存庫密碼相同):  "}, {"Key.password.is.too.short.must.be.at.least.6.characters", "金鑰密碼太短 - 必須至少為 6 個字元"}, {"Too.many.failures.key.not.added.to.keystore", "太多錯誤 - 金鑰未新增至金鑰儲存庫"}, {"Destination.alias.dest.already.exists", "目的地別名 <{0}> 已經存在"}, {"Password.is.too.short.must.be.at.least.6.characters", "密碼太短 - 必須至少為 6 個字元"}, {"Too.many.failures.Key.entry.not.cloned", "太多錯誤。未複製金鑰項目"}, {"key.password.for.alias.", "<{0}> 的金鑰密碼"}, {"Keystore.entry.for.id.getName.already.exists", "<{0}> 的金鑰儲存庫項目已經存在"}, {"Creating.keystore.entry.for.id.getName.", "建立 <{0}> 的金鑰儲存庫項目..."}, {"No.entries.from.identity.database.added", "沒有新增來自識別資料庫的項目"}, {"Alias.name.alias", "別名名稱: {0}"}, {"Creation.date.keyStore.getCreationDate.alias.", "建立日期: {0,date}"}, {"alias.keyStore.getCreationDate.alias.", "{0}, {1,date}, "}, {"alias.", "{0}, "}, {"Entry.type.type.", "項目類型: {0}"}, {"Certificate.chain.length.", "憑證鏈長度: "}, {"Certificate.i.1.", "憑證 [{0,number,integer}]:"}, {"Certificate.fingerprint.SHA1.", "憑證指紋 (SHA1): "}, {"Keystore.type.", "金鑰儲存庫類型: "}, {"Keystore.provider.", "金鑰儲存庫提供者: "}, {"Your.keystore.contains.keyStore.size.entry", "您的金鑰儲存庫包含 {0,number,integer} 項目"}, {"Your.keystore.contains.keyStore.size.entries", "您的金鑰儲存庫包含 {0,number,integer} 項目"}, {"Failed.to.parse.input", "無法剖析輸入"}, {"Empty.input", "空輸入"}, {"Not.X.509.certificate", "非 X.509 憑證"}, {"alias.has.no.public.key", "{0} 無公開金鑰"}, {"alias.has.no.X.509.certificate", "{0} 無 X.509 憑證"}, {"New.certificate.self.signed.", "新憑證 (自我簽署): "}, {"Reply.has.no.certificates", "回覆不含憑證"}, {"Certificate.not.imported.alias.alias.already.exists", "憑證未輸入，別名 <{0}> 已經存在"}, {"Input.not.an.X.509.certificate", "輸入的不是 X.509 憑證"}, {"Certificate.already.exists.in.keystore.under.alias.trustalias.", "金鑰儲存庫中的 <{0}> 別名之下，憑證已經存在"}, {"Do.you.still.want.to.add.it.no.", "您仍然想要將之新增嗎？ [否]:  "}, {"Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias.", "整個系統 CA 金鑰儲存庫中的 <{0}> 別名之下，憑證已經存在"}, {"Do.you.still.want.to.add.it.to.your.own.keystore.no.", "您仍然想要將之新增至自己的金鑰儲存庫嗎？ [否]:  "}, {"Trust.this.certificate.no.", "信任這個憑證？ [否]:  "}, {"YES", "是"}, {"New.prompt.", "新 {0}: "}, {"Passwords.must.differ", "必須是不同的密碼"}, {"Re.enter.new.prompt.", "重新輸入新 {0}: "}, {"Re.enter.password.", "重新輸入密碼:"}, {"Re.enter.new.password.", "重新輸入新密碼: "}, {"They.don.t.match.Try.again", "它們不相符。請重試"}, {"Enter.prompt.alias.name.", "輸入 {0} 別名名稱:  "}, {"Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry.", "請輸入新的別名名稱\t(RETURN 以取消匯入此項目):"}, {"Enter.alias.name.", "輸入別名名稱:  "}, {".RETURN.if.same.as.for.otherAlias.", "\t(RETURN 如果和 <{0}> 的相同)"}, {"What.is.your.first.and.last.name.", "您的名字與姓氏為何？"}, {"What.is.the.name.of.your.organizational.unit.", "您的組織單位名稱為何？"}, {"What.is.the.name.of.your.organization.", "您的組織名稱為何？"}, {"What.is.the.name.of.your.City.or.Locality.", "您所在的城市或地區名稱為何？"}, {"What.is.the.name.of.your.State.or.Province.", "您所在的州及省份名稱為何？"}, {"What.is.the.two.letter.country.code.for.this.unit.", "此單位的兩個字母國別代碼為何？"}, {"Is.name.correct.", "{0} 正確嗎？"}, {"no", "否"}, {"yes", "是"}, {"y", "y"}, {".defaultValue.", "  [{0}]:  "}, {"Alias.alias.has.no.key", "別名 <{0}> 沒有金鑰"}, {"Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key", "別名 <{0}> 所參照的項目不是私密金鑰類型。-keyclone 命令僅支援私密金鑰項目的複製"}, {".WARNING.WARNING.WARNING.", "*****************  WARNING WARNING WARNING  *****************"}, {"Signer.d.", "簽署者 #%d:"}, {"Timestamp.", "時戳:"}, {"Signature.", "簽章:"}, {"CRLs.", "CRL:"}, {"Certificate.owner.", "憑證擁有者: "}, {"Not.a.signed.jar.file", "不是簽署的 jar 檔案"}, {"No.certificate.from.the.SSL.server", "沒有來自 SSL 伺服器的憑證"}, {".The.integrity.of.the.information.stored.in.your.keystore.", "* 尚未驗證儲存於金鑰儲存庫中資訊  *\n* 的完整性！若要驗證其完整性，    *\n* 您必須提供您的金鑰儲存庫密碼。  *"}, {".The.integrity.of.the.information.stored.in.the.srckeystore.", "* 尚未驗證儲存於 srckeystore 中資訊  *\n* 的完整性！若要驗證其完整性，您必須 *\n* 提供 srckeystore 密碼。            *"}, {"Certificate.reply.does.not.contain.public.key.for.alias.", "憑證回覆並未包含 <{0}> 的公開金鑰"}, {"Incomplete.certificate.chain.in.reply", "回覆時的憑證鏈不完整"}, {"Certificate.chain.in.reply.does.not.verify.", "回覆時的憑證鏈未驗證: "}, {"Top.level.certificate.in.reply.", "回覆時的最高級憑證:\n"}, {".is.not.trusted.", "... 是不被信任的。"}, {"Install.reply.anyway.no.", "還是要安裝回覆？ [否]:  "}, {"NO", "否"}, {"Public.keys.in.reply.and.keystore.don.t.match", "回覆時的公開金鑰與金鑰儲存庫不符"}, {"Certificate.reply.and.certificate.in.keystore.are.identical", "憑證回覆與金鑰儲存庫中的憑證是相同的"}, {"Failed.to.establish.chain.from.reply", "無法從回覆中將鏈建立起來"}, {"n", "n"}, {"Wrong.answer.try.again", "錯誤的答案，請再試一次"}, {"Secret.key.not.generated.alias.alias.already.exists", "未產生秘密金鑰，別名 <{0}> 已存在"}, {"Please.provide.keysize.for.secret.key.generation", "請提供 -keysize 以產生秘密金鑰"}, {"warning.not.verified.make.sure.keystore.is.correct", "警告: 未驗證。請確定 -keystore 正確。"}, {"Extensions.", "擴充套件: "}, {".Empty.value.", "(空白值)"}, {"Extension.Request.", "擴充套件要求:"}, {"Unknown.keyUsage.type.", "不明的 keyUsage 類型: "}, {"Unknown.extendedkeyUsage.type.", "不明的 extendedkeyUsage 類型: "}, {"Unknown.AccessDescription.type.", "不明的 AccessDescription 類型: "}, {"Unrecognized.GeneralName.type.", "無法辨識的 GeneralName 類型: "}, {"This.extension.cannot.be.marked.as.critical.", "此擴充套件無法標示為關鍵。"}, {"Odd.number.of.hex.digits.found.", "找到十六進位數字的奇數: "}, {"Unknown.extension.type.", "不明的擴充套件類型: "}, {"command.{0}.is.ambiguous.", "命令 {0} 不明確:"}, {"the.certificate.request", "憑證要求"}, {"the.issuer", "發行人"}, {"the.generated.certificate", "產生的憑證"}, {"the.generated.crl", "產生的 CRL"}, {"the.generated.certificate.request", "產生的憑證要求"}, {"the.certificate", "憑證"}, {"the.crl", "CRL"}, {"the.tsa.certificate", "TSA 憑證"}, {"the.input", "輸入"}, {"reply", "回覆"}, {"one.in.many", "%1$s #%2$d / %3$d"}, {"alias.in.cacerts", "cacerts 中的發行人 <%s>"}, {"alias.in.keystore", "發行人 <%s>"}, {"with.weak", "%s (低強度)"}, {"key.bit", "%1$d 位元的 %2$s 金鑰"}, {"key.bit.weak", "%1$d 位元的 %2$s 金鑰 (低強度)"}, {".PATTERN.printX509Cert.with.weak", "擁有者: {0}\n發行人: {1}\n序號: {2}\n有效期自: {3} 到: {4}\n憑證指紋:\n\t MD5:  {5}\n\t SHA1: {6}\n\t SHA256: {7}\n簽章演算法名稱: {8}\n主體公開金鑰演算法: {9}\n版本: {10}"}, {"PKCS.10.with.weak", "PKCS #10 憑證要求 (版本 1.0)\n主體: %1$s\n格式: %2$s\n公用金鑰: %3$s\n簽章演算法: %4$s\n"}, {"verified.by.s.in.s.weak", "由 %2$s 中的 %1$s 以 %3$s 驗證"}, {"whose.sigalg.risk", "%1$s 使用的 %2$s 簽章演算法存在安全風險。"}, {"whose.key.risk", "%1$s 使用的 %2$s 存在安全風險。"}, {"jks.storetype.warning", "%1$s 金鑰儲存庫使用專有格式。建議您使用 \"keytool -importkeystore -srckeystore %2$s -destkeystore %2$s -deststoretype pkcs12\" 移轉成為使用 PKCS12 (業界標準格式)。"}, {"migrate.keystore.warning", "已將 \"%1$s\" 移轉成為 %4$s。%2$s 金鑰儲存庫已備份為 \"%3$s\"。"}, {"backup.keystore.warning", "原始的金鑰儲存庫 \"%1$s\" 已備份為 \"%3$s\"..."}, {"importing.keystore.status", "正在將金鑰儲存庫 %1$s 匯入 %2$s..."}};

   public Object[][] getContents() {
      return contents;
   }
}
