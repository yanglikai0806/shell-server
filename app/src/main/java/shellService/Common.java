package shellService;

import android.os.SystemClock;

import java.util.regex.Pattern;

public class Common {
    /**
     * 数字格式
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?\\d+(\\.\\d+)?");

    public static String runShellCommand(String cmd){
        ServiceShellUtils.ServiceShellCommandResult res = ServiceShellUtils.execCommand(cmd, false);
        LogUtil.d("", res.successMsg);
        return res.successMsg;
    }

    public static String getIP(){
        return runShellCommand("ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d \"addr:\"");
    }

    public static String getProcessId(String process){
        String pid = runShellCommand(String.format("ps -A| grep %s |awk '{print $2}'", process));
        if (!isNumeric(pid)) {
            pid = runShellCommand(String.format("ps | grep %s |awk '{print $2}'", process));
        }
        if (!isNumeric(pid)) {
            pid = runShellCommand(String.format("ps -A| grep %s", process));
            if (!isNumeric(pid)) {
                pid = runShellCommand(String.format("ps | grep %s", process));
            }
            String[] item = pid.split(" ");
            for (int i = 1; i < item.length; i++) {
                if (item[i].length() > 0) {
                    pid = item[i];
                    break;
                }
            }
        }
        LogUtil.d("", process+" 进程pid:"+pid);
        return isNumeric(pid) ? pid : "";
    }

    public static boolean killProcess(String process) {
        String pid = "";
        pid = getProcessId(process);

        if (pid.length() == 0) {
            LogUtil.d("", "killProcess: " + process + " 失败, 未找到该进程pid");
            return false;
        }
        LogUtil.d("", "killProcess: " + pid);
        runShellCommand("kill -2 " + pid);
        SystemClock.sleep(1000);
        return true;
    }

    public static boolean killMonkey(){
        return killProcess("com.android.commands.monkey");
    }

    /**
     * 是否为数字字符串
     *
     * @param origin
     * @return
     */
    public static boolean isNumeric(CharSequence origin) {
        if (origin == null || origin.length() == 0) {
            return false;
        }

        return NUMBER_PATTERN.matcher(origin).matches();
    }
}
