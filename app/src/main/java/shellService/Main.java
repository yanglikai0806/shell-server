package shellService;
//https://github.com/gtf35/app_process-shell-use
public class Main {

    public static void main(String[] args){
        Thread thread;
        thread = new ServiceThread();
        thread.start();
        while (true){
            if (!thread.isAlive()){
                thread = new ServiceThread();
                thread.start();
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
