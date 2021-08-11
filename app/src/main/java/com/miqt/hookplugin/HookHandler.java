package com.miqt.hookplugin;

import com.miqt.pluginlib.tools.IMethodHookHandler;
import com.miqt.pluginlib.tools.MethodHookPrint;

import java.lang.Object;
import java.lang.String;

/**
 * This class generate by Hook Method Plugin.
 * Its function is to receive the forwarding of the intercepted method. 
 * You can add processing logic to the generated method.
 * Have fun!
 * Each time you add a hook point, manually merge the new method in the latest ‘xxx.java.new’ file after rebuild
 *
 * @see <a href="https://github.com/miqt/android-plugin">miqt/android-plugin</a>
 * @author miqingtang@163.com
 *
 * handler method list:
 * 	annotationHookMethodEnter
 * 	annotationHookMethodReturn
 * 	annotationHookMethodInheritedEnter
 * 	annotationHookMethodInheritedReturn
 * 	hook_activityEnter
 * 	hook_activityReturn
 * 	hook_onclickEnter
 * 	hook_onclickReturn
 */
public final class HookHandler {
  static IMethodHookHandler methodHookHandler = new MethodHookPrint("HookHandler");
  /**
   * This method generate with:
   * 	annotationHookMethod{
   * 		access=-1
   * 		annotation=Lcom/miqt/pluginlib/annotation/HookMethod;
   * 		hookTiming=Enter|Return
   * 	}
   * Generate Time:2021-08-10 14:53:18
   */
  public static void annotationHookMethodEnter(Object thisObj, String className, String methodName,
      String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodEnter(thisObj,className,methodName,argsType,returnType,args);
  }

  /**
   * This method generate with:
   * 	annotationHookMethod{
   * 		access=-1
   * 		annotation=Lcom/miqt/pluginlib/annotation/HookMethod;
   * 		hookTiming=Enter|Return
   * 	}
   * Generate Time:2021-08-10 14:53:18
   */
  public static void annotationHookMethodReturn(Object returnObj, Object thisObj, String className,
      String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodReturn(returnObj,thisObj,className,methodName,argsType,returnType,args);
  }

  /**
   * This method generate with:
   * 	annotationHookMethodInherited{
   * 		access=-1
   * 		annotation=Lcom/miqt/pluginlib/annotation/HookMethodInherited;
   * 		hookTiming=Enter|Return
   * 	}
   * Generate Time:2021-08-10 14:53:18
   */
  public static void annotationHookMethodInheritedEnter(Object thisObj, String className,
      String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodEnter(thisObj,className,methodName,argsType,returnType,args);
  }

  /**
   * This method generate with:
   * 	annotationHookMethodInherited{
   * 		access=-1
   * 		annotation=Lcom/miqt/pluginlib/annotation/HookMethodInherited;
   * 		hookTiming=Enter|Return
   * 	}
   * Generate Time:2021-08-10 14:53:18
   */
  public static void annotationHookMethodInheritedReturn(Object returnObj, Object thisObj,
      String className, String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodReturn(returnObj,thisObj,className,methodName,argsType,returnType,args);
  }



  /**
   * This method generate with:
   * 	hook_onclick{
   * 		access=-1
   * 		interfaces=android/view/View$OnClickListener
   * 		methodName=onClick
   * 		descriptor=(Landroid/view/View;)V
   * 		hookTiming=Enter|Return
   * 	}
   * Argument:
   * 	Landroid/view/View;
   *
   * Return Type:
   * 	V
   * Generate Time:2021-08-10 14:53:18
   */
  public static void hook_onclickEnter(Object thisObj, String className, String methodName,
      String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodEnter(thisObj,className,methodName,argsType,returnType,args);
  }

  /**
   * This method generate with:
   * 	hook_onclick{
   * 		access=-1
   * 		interfaces=android/view/View$OnClickListener
   * 		methodName=onClick
   * 		descriptor=(Landroid/view/View;)V
   * 		hookTiming=Enter|Return
   * 	}
   * Argument:
   * 	Landroid/view/View;
   *
   * Return Type:
   * 	V
   * Generate Time:2021-08-10 14:53:18
   */
  public static void hook_onclickReturn(Object returnObj, Object thisObj, String className,
      String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodReturn(returnObj,thisObj,className,methodName,argsType,returnType,args);
  }

  /**
   * This method generate with:
   * 	hook_thread_run{
   * 		access=1
   * 		superName="java/lang/Thread"
   * 		methodName="run"
   * 		descriptor="()V"
   * 		hookTiming="Enter|Return"
   * 	}
   * Argument:
   * Return Type:
   * 	V
   * Generate Time:2021-08-11 11:06:23
   */
  public static void hook_thread_runEnter(Object thisObj, String className, String methodName,
                                          String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodEnter(thisObj,className,methodName,argsType,returnType,args);

  }

  /**
   * This method generate with:
   * 	hook_thread_run{
   * 		access=1
   * 		superName="java/lang/Thread"
   * 		methodName="run"
   * 		descriptor="()V"
   * 		hookTiming="Enter|Return"
   * 	}
   * Argument:
   * Return Type:
   * 	V
   * Generate Time:2021-08-11 11:06:23
   */
  public static void hook_thread_runReturn(Object returnObj, Object thisObj, String className,
                                           String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodReturn(returnObj,thisObj,className,methodName,argsType,returnType,args);

  }

  /**
   * This method generate with:
   * 	hook_Runnable_run{
   * 		access=1
   * 		interfaces="java/lang/Runnable"
   * 		methodName="run"
   * 		descriptor="()V"
   * 		hookTiming="Enter|Return"
   * 	}
   * Argument:
   * Return Type:
   * 	V
   * Generate Time:2021-08-11 11:06:23
   */
  public static void hook_Runnable_runEnter(Object thisObj, String className, String methodName,
                                            String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodEnter(thisObj,className,methodName,argsType,returnType,args);

  }

  /**
   * This method generate with:
   * 	hook_Runnable_run{
   * 		access=1
   * 		interfaces="java/lang/Runnable"
   * 		methodName="run"
   * 		descriptor="()V"
   * 		hookTiming="Enter|Return"
   * 	}
   * Argument:
   * Return Type:
   * 	V
   * Generate Time:2021-08-11 11:06:23
   */
  public static void hook_Runnable_runReturn(Object returnObj, Object thisObj, String className,
                                             String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodReturn(returnObj,thisObj,className,methodName,argsType,returnType,args);
  }


  /**
   * This method generate with:
   * 	hook_activity_lifeCycle{
   * 		access=4
   * 		superName="android/app/Activity"
   * 		methodName="(onCreate)|(onResume)|(onPause)|(onStart)|(onDestroy)|(onStop)"
   * 		hookTiming="Enter|Return"
   * 	}
   * Generate Time:2021-08-11 11:21:54
   */
  public static void hook_activity_lifeCycleEnter(Object thisObj, String className,
                                                  String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodEnter(thisObj,className,methodName,argsType,returnType,args);
  }

  /**
   * This method generate with:
   * 	hook_activity_lifeCycle{
   * 		access=4
   * 		superName="android/app/Activity"
   * 		methodName="(onCreate)|(onResume)|(onPause)|(onStart)|(onDestroy)|(onStop)"
   * 		hookTiming="Enter|Return"
   * 	}
   * Generate Time:2021-08-11 11:21:54
   */
  public static void hook_activity_lifeCycleReturn(Object returnObj, Object thisObj,
                                                   String className, String methodName, String argsType, String returnType, Object[] args) {
    methodHookHandler.onMethodReturn(returnObj,thisObj,className,methodName,argsType,returnType,args);

  }
}
