package xyz.thewind.music.tool.down;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadHelper {

    public static void download(DownloadParams params, DownloadListener listener) {
        URL url ;
        HttpURLConnection conn = null;
        try {
            url = new URL(params.downloadLink);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DownloadConfig.REQ_METHOD);
            conn.setConnectTimeout(DownloadConfig.TIME_OUT);
            conn.setReadTimeout(DownloadConfig.TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (Exception e) {
            conn.disconnect();
            listener.onFailed(params);
            return;
        }
        long fileLen = conn.getContentLengthLong();
        File file = new File(params.saveName);
        long downLen = 0;
        if (file.exists() && (downLen = file.length()) == fileLen) {
            if (listener != null) {
                listener.onSuccess(params);
            }
            conn.disconnect();
            return;
        }
        if (listener != null) {
            listener.onStart(params);
        }
        BufferedInputStream reader = null;
        try {
            reader = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            if (listener != null) {
                listener.onFailed(params);
            }
            try {
                reader.close();
            } catch (IOException ignored) {

            }
            conn.disconnect();
            return;
        }

        byte[] b = new byte[8192];
        try (RandomAccessFile raf = new RandomAccessFile(new File(params.saveName), "rwd")) {
            raf.seek(downLen);
            int len;
            while ((len = reader.read(b)) != -1) {
                raf.write(b, 0, len);
                downLen += len;
                if (listener != null) {
                    listener.onProcess(params, downLen, fileLen);
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailed(params);
            }
        }
        try {
            reader.close();
        } catch (Exception ignored) {}
        conn.disconnect();
        listener.onSuccess(params);

    }
}
