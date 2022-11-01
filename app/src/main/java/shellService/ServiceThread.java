package shellService;

import static shellService.Common.getProcessId;
import static shellService.Common.killMonkey;
import static shellService.Common.runShellCommand;

import android.text.TextUtils;

public class ServiceThread extends Thread {
    private static int ShellPORT = 3721;
    private static Thread mThread = null; //监控线程
    private static String tag = "";
    private static boolean isKeptProcessStarted = false; // 用来标志监控的进程是否已启动,如果检测的要监控的进程还未启动不执行重启操作

    @Override
    public void run() {
        LogUtil.d("",">>>>>>Shell服务端程序被调用<<<<<<");
        final String[] res = {""};
        new Service(new Service.ServiceGetText() {
            @Override
            public String getText(String text) {
                if (text.equals("###AreYouOK")){ // 验证连接状态
                    Monitor();
                    return "###IamOK#";

                } else if (text.equals("###testool")) {// 启动守护程序
                    setTag("testool");
                    Monitor();
                    return "###Testool is Keeping#";

                } else if (text.equals("###Remote")){ // 启动守护程序
                    setTag("Remote");
                    Monitor();
                    return "###Remote is Keeping#";
                } else if (text.equals("###testool.MyIntentService")){
                    setTag("testool.MyIntentService");
                    Monitor();
                    return "###MyIntentService is Keeping#";
                } else if (text.equals("###testool.MonitorService")){
                    setTag("testool.MonitorService");
                    Monitor();
                    return "###MonitorService is Keeping#";
                } else if (text.equals("###testool.uicrawler")){
                    setTag("testool.uicrawler");
                    Monitor();
                    return "###Monkey is Keeping#";
                } else if (text.equals("###testool.MonkeyService")){
                    setTag("testool.MonkeyService");
                    Monitor();
                    return "###MonkeyService is Keeping#";
                }

                try{
                    ServiceShellUtils.ServiceShellCommandResult sr =  ServiceShellUtils.execCommand(text, false);
                    if (sr.result == 0){
                        res[0] = sr.successMsg;
                    } else {
                        res[0] = sr.errorMsg;
                    }
                }catch (Exception e){
                    res[0] = e.toString();
                }
                return res[0];
            }
        }, ShellPORT);
    }

    private void Monitor(){
        if (mThread == null || !mThread.isAlive()) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServiceShellUtils.ServiceShellCommandResult sr;
                    String res = "";
                    while (true) {
                        try {
                            if (getTag().equals("Remote")) {
                                res = getProcessId("com.kevin.testool:RemoteService");

                            } else if (getTag().equals("testool.MonitorService")){
                                res = getProcessId("com.kevin.testool:MonitorService");

                            } else if (getTag().equals("testool.MyIntentService")){
                                res = getProcessId("com.kevin.testool:MyIntentService");

                            } else if (getTag().equals("testool.MonkeyService")) {
                                res = getProcessId("com.kevin.testool:MonkeyService");

                            } else if (getTag().equals("testool.uicrawler")) {
                                res = getProcessId("com.kevin.testool:uicrawler");

                            } else {
                                res = getProcessId("com.kevin.testool");
                            }
                            if (!TextUtils.isEmpty(res)) {
                                LogUtil.d("", getTag() + " is running");
                                if (!isKeptProcessStarted){
                                    isKeptProcessStarted = true;
                                }
                            } else {
                                if (isKeptProcessStarted) {
                                    LogUtil.d("", getTag() + " is stopped, restarting");
                                    if (!TextUtils.isEmpty(getProcessId("monkey"))) killMonkey();
                                    Thread.sleep(2000);
                                    ServiceShellUtils.execCommand("am start -S -n com.kevin.testool/.activity.MainActivity --es TASK " + getTag(), false);
                                }
                            }
                            Thread.sleep(15000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.d("", e.getMessage());
                        }
                    }

                }
            });
            mThread.start();
        }
//        return mThread;
    }

    private static void setTag(String _tag){
        LogUtil.d("", "setTag:" + _tag);
        if (!tag.equals(_tag)){ // tag变化
            isKeptProcessStarted = false;

        }
        tag = _tag;
    }

    private static String getTag(){
        return tag;
    }
}
