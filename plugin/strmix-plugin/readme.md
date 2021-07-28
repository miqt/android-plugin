# 字符串混淆插件工具

提供打包时，对项目中的字符串String进行打乱，混淆。

支持：

- 通过配置设置是否混淆字符串
- 通过配置设置字符串替换

使用方法：
项目 build.gradle 添加

```
classpath 'com.miqt:strmix-plugin:0.3.10'
```
对应模块添加依赖
```
implementation 'com.miqt:strmix-plugin-lib:0.3.10'
```
对应模块配置混淆参数
```groovy
apply plugin: 'com.miqt.plugin.strmix'
str_mix {
    buildLog = true
    injectJar = true
    enable = true
    enableMix = true
    enableRep = true
    key = "hello"
    repTable= [
            "str1": "str2",
    ]
}
```
编译时控制台会输出以下类似日志
```
> Task :app:transformClassesWithStrMixForDebug
┌---------------------------------------------
|The plugin [StrMix] --> Start!
|项目主页:https://github.com/miqt/android-plugin
|联系作者:miqingtang@163.com
└---------------------------------------------
┌---------------------------------------------
|The plugin [StrMix] --> Done!
|log:.\app\build/plugin/StrMix.log
└---------------------------------------------
```

