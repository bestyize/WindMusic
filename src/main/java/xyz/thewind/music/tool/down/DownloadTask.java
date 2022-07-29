package xyz.thewind.music.tool.down;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask implements Runnable {

    private DownloadParams params;

    private DownloadListener listener;

    public DownloadTask(DownloadParams params, DownloadListener listener) {
        this.params = params;
        this.listener = listener;
    }

    @Override
    public void run() {
        DownloadHelper.download(params, listener);
    }
}
