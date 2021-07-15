这是一个 android 方法hook的插件，在方法进入和方法退出时，将当前运行的所有参数回调到固定的接口中，利用这一点，可以进行方法切片式开发，也可以进行一些耗时统计等性能优化相关的统计。

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/miqingtang/maven/pluginSrc/images/download.svg)](https://bintray.com/miqingtang/maven/pluginSrc)

## 效果展示
原始代码：
```java
@HookMethod
public int add(int num1, int num2) throws InterruptedException {
    int a = num1 + num2;
    Thread.sleep(a);
    return a;
}
```

实际编译插桩后代码：

```java
public int add(int num1, int num2) throws InterruptedException {
    MethodHookHandler.enter(this,"com.miqt.plugindemo.Hello","add","[int, int]","int",num1,num2);
    int a = num1 + num2;
    Thread.sleep(a);
    MethodHookHandler.exit(a,this,"com.miqt.plugindemo.Hello","add","[int, int]","int",num1,num2);
    return a;
}
```

稍作开发就可以实现一个方法出入日志打印功能：

示例1：

```
┌com.miqt.datacontrol.MainActivity$1@dce8636.onClick():[main]
|┌com.miqt.multiprogresskv.DataControl@197c937.putString():[main]
||┌com.miqt.multiprogresskv.DataControl@197c937.put():[main]
|||┌com.miqt.multiprogresskv.DataContentProvider@c5ae32e.update():[main]
||||┌com.miqt.multiprogresskv.helper.DBHelper.getInstance():[main]
||||└com.miqt.multiprogresskv.helper.DBHelper.getInstance():[0]
||||┌com.miqt.multiprogresskv.helper.DBHelper@4799ea4.put():[main]
||||└com.miqt.multiprogresskv.helper.DBHelper@4799ea4.put():[6]
|||└com.miqt.multiprogresskv.DataContentProvider@c5ae32e.update():[6]
||└com.miqt.multiprogresskv.DataControl@197c937.put():[7]
|└com.miqt.multiprogresskv.DataControl@197c937.putString():[7]
└com.miqt.datacontrol.MainActivity$1@dce8636.onClick():[7]
```

示例2：

```
╔======================================================================================
║[Thread]:Thread-3
║[Class]:com.miqt.plugindemo.Hello
║[Method]:add
║[This]:com.miqt.plugindemo.Hello@c65e5c0
║[ArgsType]:[int, int]
║[ArgsValue]:[100,200]
║[Return]:300
║[ReturnType]:int
║[Time]:301 ms
╚======================================================================================
```

可以看出，这样的话方法名，运行线程，当前对象，入/出参数和耗时情况就都一目了然啦。当然还可以做一些别的事情，例如hook点击事件等等。

## 使用方法

项目根目录：build.gradle 添加以下代码

```groovy
dependencies {
    classpath 'com.miqt:hook-method-plugin:0.3.5'
}
```

对应 module 中启用插件，可以是`application`也可以是`library`

```groovy
apply plugin: 'com.miqt.plugin.hookmethod'
hook_method {
    //插桩白名单正则 .* 匹配所有
    classWhiteListRegex = [".*"]
    //是否关注jar包
    injectJar = true
    //是否启用
    enable = true
    //是否只是在debug运行时启用
    justDebug = false
    //自定义 hook method 接受类
    //示例1，--> 默认
    //impl = "com.miqt.pluginlib.tools.MethodHookPrint"
    //示例2
    impl = "com.miqt.pluginlib.tools.SampleMethodHook"
}
```

添加类库依赖：

```groovy
dependencies {
    implementation 'com.miqt:hook-method-lib:0.3.5'
}
```

如果编译不通过，请添加maven仓库：

```
maven {url 'https://dl.bintray.com/miqingtang/maven'}
jcenter()//最近提示已经过期，截止2021年7月15日我试的还能用，后面看看迁移到别的仓库
```

接入完成build后可以查看：build\plugin 目录找到对应的日志输出。有问题欢迎提交 issues.

>  这个插件是借鉴了很多大佬的代码，并结合自己的想法进行了一些调整，在此感谢他们付出的努力。
>
> https://github.com/novoda/bintray-release  
> https://github.com/JeasonWong/CostTime  
> https://github.com/MegatronKing/StringFog  
