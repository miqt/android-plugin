这是一个 android 方法hook的插件，在方法进入和方法退出时，将当前运行的所有参数回调到固定的接口中，利用这一点，可以进行方法切片式开发，通过配置Hook点，达到Hook监控的目的。

利用这个插件，可以实现：

1. Android 全埋点，页面浏览，点击，等无痕埋点，按需配置Hook点即可，配置方法见下文
2. Android 方法耗时，性能统计
3. 各种拦截器，例如，拦截某方法，在某方法执行前，先判断权限或进行准备工作
4. 在引用的第三方jar包中追加代码，增加创建线程，文件IO相关的监控等

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## 效果展示
例如日常开发中，我们免不了可能要进行一些点击事件埋点的任务，如果我们利用这个插件，可以直接配置一次统计所有的点击事件埋点，下面来看一下具体做法：

1. 先定义Hook点，这个hook点的目标是监控所有的点击事件。在 app module 中的 build.gradle 添加以下代码，详细字段含义会在下文说明。

```groovy
apply plugin: 'com.miqt.plugin.hookmethod'
hook_method {
    buildLog = true
    injectJar = false //是否hook引用的第三方jar包
    enable = true
    hookTargets {
        hook_onclick { // <-- 定义Hook点
            interfaces = "android/view/View\$OnClickListener"//<-- hook条件1，继承了这个接口
            methodName = "onClick"							//<-- hook条件2，方法名
            descriptor = "(Landroid/view/View;)V"			//<-- hook条件3，方法参数和返回值类型
            hookTiming = "Enter"							//<-- hook条件4，指定是在方法进入时进行Hook
        }
    }
}
```

hook_method 是对插件的整体配置，hookTargets 是Hook点位的配置，编译时，会按照定义的筛选条件来进行筛选，四个条件同时满足，才会进行插桩。

> 注意，这里由于是字节码操作，因此这里的`descriptor`定义都是**字段描述符**，和**字节码描述符**，不知道的可以百度了解下，非常简单。如果你不想了解也没事，我后面有介绍有一种方法可以直接查看某个方法的字节码信息。利用`@HookInfo`注解

进行过以上配置后，我们随便写一个按钮，然后添加个点击事件。

```java
public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

点击同步按钮，编译一下项目，这时候，插件就会自动生成该点击事件的hook转发handler处理类：

类似这样的：[HookHandler](https://github.com/miqt/android-plugin/blob/master/app/src/main/java/com/miqt/hookplugin/HookHandler.java)

这时候我们打开Apk反编译查看，就可以看到插桩代码了：

```java
public class MainActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            /* class com.asm.code.MainActivity.AnonymousClass1 */

            public void onClick(View view) {
                HookHandler.hook_onclickEnter(this, "com/asm/code/MainActivity$1", "onClick", "[android.view.View]", "void", new Object[]{view});//<--插桩的代码
                Toast.makeText(MainActivity.this, "onClick", 0).show();
            }
        });
    }
}
```

到了这里，我们只需要在`hook_onclickEnter`这个方法中编写全局点击事件监控处理逻辑就可以了，所有的这一切都是插件自动完成的哦，我们只需要配置Hook规则就可以了，是不是非常简单。

示例配置传送门：https://github.com/miqt/android-plugin/blob/master/app/build.gradle

**以此类推，该插件还可以通过自定义Hook规则，Hook项目中的任意方法的代码，达到切面编程的目的。**

## 使用配置方法

1. **添加maven仓库**

   ```groovy
   maven { url 'https://raw.githubusercontent.com/miqt/maven/master' }
   maven { url 'https://gitee.com/miqt/maven/raw/master' }
   ```

   还拉取不到库？

   ```groovy
   maven { url 'https://raw.fastgit.org/miqt/maven/master' }
   ```

   或者，去我的[仓库git地址](https://github.com/miqt/maven)下载下来，本地依赖。

   > 最开始我使用 `jcenter()` ，结果后来这个库官方弃用了，不过现在 0.3.5版本 仍然可以从这个库上拉取下来，之后版本我都使用gitee和github作为仓库存储地址了。

2. **添加插件依赖**

    项目根目录：build.gradle 添加以下代码

    ```groovy
    dependencies {
        classpath 'com.miqt:hook-method-plugin:0.4.1'
    }
    ```

    对应 module 中启用插件，可以是`application`也可以是`library`

    ```groovy
    apply plugin: 'com.miqt.plugin.hookmethod'
    hook_method {
        buildLog = true
        injectJar = false //是否hook引用的第三方jar包
        enable = true
        hookTargets {
            hook_onclick { // <-- 自定义Hook点
                interfaces = "android/view/View\$OnClickListener"	//<-- hook条件1，继承了这个接口
                methodName = "onClick"	//<-- hook条件2，方法名
                descriptor = "(Landroid/view/View;)V"	//<-- hook条件3，方法参数和返回值类型
                hookTiming = "Enter"	//<-- hook条件4，指定是在方法进入时进行Hook
            }
        }
    }
    ```

3. **对应 module 中添加类库依赖**

    ```groovy
    dependencies {
        implementation 'com.miqt:hook-method-lib:0.4.1'
    }
    ```

    项目 clean 然后运行，logcat 过滤 `MethodHookHandler` 就可以看到打印结果啦！



## 插件配置参数说明

**hook_method {} 中的参数**

这个里面是定义插件相关的配置。

| 参数       | 是否必填 | 类型   | 含义                                                         | 默认值                            |
| ---------- | -------- | :----- | ------------------------------------------------------------ | --------------------------------- |
| enable     | 否       | bool   | 是否启用                                                     | true                              |
| runVariant | 否       | 字符串 | 插件执行环境，有的时候我们只想在debug环境进行插桩，release环境不需要，这时候可以设置为：DEBUG，表示仅DEBUG执行时，运行插件，这个参数有四个枚举值：<br>DEBUG: 仅Debug运行时插桩<br>RELEASE: 仅RELEASE运行时插桩<br/>ALWAYS: 所有运行情况 （默认）<br/>NEVER: 从不插桩，等同于enable = false | ALWAYS                            |
| injectJar  | 否       | bool   | 是否对引用的所有第三方依赖jar，aar进行按照规则插桩hook       | false                             |
| buildLog   | 否       | bool   | 是否在.\app\build\plugin 文件夹输出插件编译日志，编译有错误时，请打开 | true                              |
| handler    | 否       | 字符串 | 插桩Hook转发的Handler接收类，也就是插桩处理类，例如传xxx.xxx.A，则编译后会自动生成A.java这个类，并自动生成方法，类似这种： | `com.miqt.hookplugin.HookHandler` |
| handlerDir | 否       | 字符串 | 插桩Hook转发的Handler接收类在那个路径生成，默认是` .\src\main\java ` 一般不需要自定义 | .\src\main\java                   |

**hookTargets {} 中的参数**

结构：

```groovy
hook_method {
    hookTargets {
        name1 {
        	条件1 = value1
        	条件2 = value2
        	条件3 = value3
        }
        name2 {
        	条件1 = value1
        	条件2 = value2
        	条件3 = value3
        }
    }
}
```

这里是定义hood点位筛选条件的，逻辑是，必须满足参数 1，2，3设置的条件，才会插桩

**注意：如果设置：`hook_All{ } ` 这种无条件参数的，意思就是所有的方法都插桩。**

| 参数       | 是否必填 | 类型   | 含义                                                         | 默认值 |
| ---------- | -------- | ------ | ------------------------------------------------------------ | ------ |
| name       | 是       | 字符串 | 上文的name1，name2位置，意思是hook点命名，这个命名最终会是这个点位的Hook接收方法名，可以自己随便定义 | null  |
| access     | 否       | int    | 限定条件之：方法访问修饰符，比如private，public，static，final，使用`Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC`这种形式来定义 | -1     |
| interfaces | 否       | 字符串 | 限定条件之：方法所在类必须实现了什么接口，例如OnClickListener接口就这么写：`interfaces = "android/view/View\$OnClickListener"`这里不用点，用/表示这是字节码标识符。给方法或类加@HookInfo注解，插件会自动在编译控制台和编译Log日志文件中展示出对应的字节码标识符。 | null |
| superName  | 否       | 字符串 | 限定条件之：方法所在类必须是superName的子类                  | null |
| className  | 否       | 字符串 | 限定条件之：方法所在类名称，这里是全称，例如com/xxx/A，**支持正则限定** | null |
| methodName | 否       | 字符串 | 限定条件之：方法名，**支持正则限定**                         | null |
| descriptor | 否       | 字符串 | 限定条件之：方法**字段描述符**，不知道咋写的用@HookInfo标记看下输出就知道了。 | null |
| annotation | 否       | 字符串 | 限定条件之：方法或所在类必须包含这个注解                     | null |
| signature  | 否       | 字符串 | 限定条件之：方法或所在类必须包含这个泛型                     | null |
| exceptions | 否       | 字符串 | 限定条件之：方法必须符合抛出某异常的形式                     | null |
| hookTiming | 否       | 字符串 | 限定条件之：在方法进入时插桩还是退出时<br>`Enter`:方法进入时<br>`Return`:方法退出时<br>`Enter|Return`:进入和退出时（默认） |Enter\|Return |

## 内置注解

**@HookInfo： ** 输出对应字节码限制条件信息，可复制打印出的对应条件用于Hook同类型方法  
**@HookMethod： ** 标记hook方法，不需要写hookTargets，可以理解为内置的  
**@HookMethodInherited：** 标记hook方法，有类继承性，不需要写hookTargets，可以理解为内置的  
**@IgnoreMethodHook：** 标记忽略方法，优先级最高   

#### 读都读到这里了，如果觉得还不错就帮我点个star吧，你的肯定是我持续维护的动力！

#### 如果有使用问题欢迎提交Issues ，或者加我微信一起交流，微信号在我的主页。

最后贴个Demo中的示例：

```java
apply plugin: 'com.miqt.plugin.hookmethod'
hook_method {
    buildLog = true
    injectJar = true
    enable = true
    hookTargets {
        hook_onclick {
            //<-- 所有点击事件
            interfaces = "android/view/View\$OnClickListener"
            methodName = "onClick"
            descriptor = "(Landroid/view/View;)V"
            hookTiming = "Enter|Return"
        }
        hook_activity_lifeCycle {
            //<-- 所有activity生命周期方法
            access = 4
            superName = "android/app/Activity"
            //这里是个正则表达式
            methodName = "(onCreate)|(onResume)|(onPause)|(onStart)|(onDestroy)|(onStop)"
            hookTiming = "Enter|Return"
        }
        hook_thread_run{
            //<-- 线程run方法
            access = 1
            superName = "java/lang/Thread"
            methodName = "run"
            descriptor = "()V"
        }
        hook_Runnable_run{
            //<-- 线程run方法
            access = 1
            interfaces = "java/lang/Runnable"
            methodName = "run"
            descriptor = "()V"
        }
    }
}
```

demo打印结果：

```
I/MethodHookHandler: ┌com/asm/code/MainActivity@efc0c18.onPause():[main]
I/MethodHookHandler: └com/asm/code/MainActivity@efc0c18.onPause():[0]
I/MethodHookHandler: ┌com/asm/code/MainActivity@efc0c18.onStop():[main]
I/MethodHookHandler: └com/asm/code/MainActivity@efc0c18.onStop():[0]
I/MethodHookHandler: ┌com/asm/code/MainActivity@efc0c18.onStart():[main]
I/MethodHookHandler: └com/asm/code/MainActivity@efc0c18.onStart():[0]
I/MethodHookHandler: ┌com/asm/code/MainActivity@efc0c18.onResume():[main]
I/MethodHookHandler: └com/asm/code/MainActivity@efc0c18.onResume():[3]
I/MethodHookHandler: ┌com/asm/code/MainActivity$2@f7ef0fe.run():[Thread-5]
I/MethodHookHandler: └com/asm/code/MainActivity$2@f7ef0fe.run():[1]
I/MethodHookHandler: ┌com/asm/code/MainActivity@efc0c18.onPause():[main]
I/MethodHookHandler: └com/asm/code/MainActivity@efc0c18.onPause():[0]
I/MethodHookHandler: ┌com/asm/code/MainActivity@efc0c18.onStop():[main]
I/MethodHookHandler: └com/asm/code/MainActivity@efc0c18.onStop():[0]
```

## 致谢

>  这个插件是借鉴了很多大佬的代码，并结合自己的想法进行了一些调整，在此感谢他们付出的努力。
>
> https://github.com/novoda/bintray-release  
> https://github.com/JeasonWong/CostTime  
> https://github.com/MegatronKing/StringFog  
