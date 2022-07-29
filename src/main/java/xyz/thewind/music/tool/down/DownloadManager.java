package xyz.thewind.music.tool.down;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {

    private static final int DEFAULT_THREAD_COUNT = 3;

    private final ExecutorService downloadService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);

    private static class Inner {
        public static final DownloadManager INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return Inner.INSTANCE;
    }

    public void start(List<DownloadParams> downloadList, DownloadListener listener) {
        if (downloadList == null) {
            return;
        }
        for (DownloadParams params : downloadList) {
            start(params, listener);
        }
    }

    public void start(DownloadParams params, DownloadListener listener) {
        downloadService.execute(new DownloadTask(params, listener));
    }

    public void startCurrThread(DownloadParams params, DownloadListener listener) {
        DownloadHelper.download(params, listener);
    }


}
