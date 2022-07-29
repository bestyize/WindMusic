package xyz.thewind.music.tool.down;

public interface DownloadListener {
    void onStart(DownloadParams params);
    default void onProcess(DownloadParams params, long downLen, long totalLen) {};
    void onSuccess(DownloadParams params);
    void onFailed(DownloadParams params);
    default void onPause(DownloadParams params){};

}
