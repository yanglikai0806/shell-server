package shellService;
import android.os.SystemClock;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import static shellService.Common.getIP;


public class MyWebSocketServer extends WebSocketServer {

    private static String TAG = "WebSocketServer";
    private static boolean isOpening = false;
    private static int TEMP_BASE64_STR_LEN = 0; // 存储图片转base64后的字符串长度

    public MyWebSocketServer(InetSocketAddress host) {
        super(host);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        isOpening = true;
        TEMP_BASE64_STR_LEN = 0;
        LogUtil.d(TAG, "客户端连接成功：" + conn.getRemoteSocketAddress());

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        isOpening = false;
        TEMP_BASE64_STR_LEN = 0;
        LogUtil.d(TAG, "服务关闭");

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtil.d(TAG, "接受消息：" + message);
        try {
            JSONObject msgJson = new JSONObject(message);
            if (!msgJson.isNull("mode")){
                String mode = msgJson.getString("mode");
                LogUtil.d("", mode);

            }
        } catch (Exception e) {
            LogUtil.d("", e + "");
        }

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        // 接收到的是Byte数据，需要转成文本数据，根据你的客户端要求
        // 看看是string还是byte，工具类在下面贴出
        LogUtil.d(TAG, "onMessage()接收到ByteBuffer的数据->" + byteBufferToString(message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogUtil.d(TAG, ex.toString());

    }

    @Override
    public void onStart() {
        LogUtil.d(TAG, "onStart()-> WebSocket服务端启动成功");
    }


    private static MyWebSocketServer myWebSocketServer;
    // 实现方法，在服务中或者OnCreate()方法调用此方法
    public static void startMyWebsocketServer() {

        InetSocketAddress myHost = new InetSocketAddress(getIP(), 9999);
        if (myWebSocketServer != null) {
            try {
                myWebSocketServer.stop();
                SystemClock.sleep(3000);
                LogUtil.d(TAG, "重启websocket服务");
            } catch (Exception e) {
                LogUtil.d("", e + "");
            }
        }
        myWebSocketServer = new MyWebSocketServer(myHost);
        myWebSocketServer.start();
    }

    public static void stopMyWebsocketServer() {

        if (myWebSocketServer != null) {
            try {
                myWebSocketServer.stop();
                LogUtil.d(TAG, "停止websocket服务");
            } catch (Exception e) {
                LogUtil.d("", e + "");
            }
        }
    }


    public static String byteBufferToString(ByteBuffer buffer) {
        CharBuffer charBuffer = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (Exception e) {
            LogUtil.d("", e + "");
            return null;
        }
    }



}
