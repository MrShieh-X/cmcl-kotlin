# Update Logs
Currently, the latest version is 2.2.2, which was updated on July 23, 2024.

## 2.2.2 (Jul. 23, 2024)
- Removed the MCBBS download source, but it won't be removed from the special thanks list in order to thank it for its contribution to Minecraft.
- To prevent the automatic redirection to the browser from failing, when logging into a Microsoft account, the link that should be opened using the browser will be displayed for the user to copy and open. ([Shapaper](https://github.com/Shapaper) in [Issue#34](https://github.com/MrShieh-X/console-minecraft-launcher/issues/34) and [BlockyDeer](https://github.com/BlockyDeer) in [Issue#40](https://github.com/MrShieh-X/console-minecraft-launcher/issues/40))
- Optimized the multi-threaded downloader. When the number of files to be downloaded is less than or equal to the number of threads, only threads equal to the number of files will be created.
- Fixed some issues.