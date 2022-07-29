package xyz.thewind.music.music.impl;

import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.down.MusicDownloadManager;
import xyz.thewind.music.music.impl.mg.MgMusicModel;
import xyz.thewind.music.tool.down.DownloadListener;
import xyz.thewind.music.tool.down.DownloadParams;

import java.util.List;

public class MusicDownloadTest {

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onStart(DownloadParams params) {
            System.out.println("onStart, name = " + params.saveName);
        }

        @Override
        public void onProcess(DownloadParams params, long downLen, long totalLen) {

        }

        @Override
        public void onSuccess(DownloadParams params) {
            System.out.println("onSuccess, name = " + params.saveName);
        }

        @Override
        public void onFailed(DownloadParams params) {
            System.out.println("onFailed, name = " + params.saveName);
        }

        @Override
        public void onPause(DownloadParams params) {

        }
    };

    @Test
    public void testDownload() {
        MusicDownloadManager.getInstance().setSavePath("/home/read/Music");
        List<SongBean> songBeans = new MgMusicModel().searchWithDownload("周杰伦", 10);
        MusicDownloadManager.getInstance().downloadHighestQuality(songBeans, downloadListener);
    }
}
