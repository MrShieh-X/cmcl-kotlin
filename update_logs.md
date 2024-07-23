# 更新日志
目前的最新版本为2.2.2，发布日期是2024年7月23日。

## 2.2.2（2024年7月23日）
- 移除了MCBBS下载源，但为感谢其曾对 Minecraft 所做出的贡献，将不会从特别鸣谢名单中将它除去。
- 为防止无法自动跳转至浏览器，登录微软账号时将会显示应使用浏览器打开的链接以供用户自行复制打开。（[Shapaper](https://github.com/Shapaper) 于 [Issue#34](https://github.com/MrShieh-X/console-minecraft-launcher/issues/34) 与 [BlockyDeer](https://github.com/BlockyDeer) 于 [Issue#40](https://github.com/MrShieh-X/console-minecraft-launcher/issues/40)）
- 优化了多线程下载器，当所需下载文件数小于或等于线程数时，将只会创建数量与文件数目相同的线程。
- 修复了一些问题。