# shell-server
参考 https://github.com/gtf35/app_process-shell-use 实现

## 如何使用：
- 将工程build后，生成apk文件
- 将apk文件改名成.zip文件后解压
- 将解压包中的classes.dex文件push到手机的指定目录下
- 先通过adb shell进入设备的shell，再输入命令：nohup app_process -Djava.class.path=/sdcard/autotest/shellserver.dex /system/bin --nice-name=shellServer shellService.Main > /dev/null 2>&1 & ，最后执行exit退出shell。（其中/sdcard/autotest/shellserver.dex 为你push到手机的路径）
