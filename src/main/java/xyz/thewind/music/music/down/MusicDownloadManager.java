package xyz.thewind.music.music.down;


import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.common.MusicCommonConfig;
import xyz.thewind.music.music.impl.qq.bean.SearchResponse;
import xyz.thewind.music.tool.down.DownloadHelper;
import xyz.thewind.music.tool.down.DownloadListener;
import xyz.thewind.music.tool.down.DownloadParams;

import java.io.File;
import java.util.*;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;

public class MusicDownloadManager {

    private static final Map<String, String> QAULITY_EXT_MAPPER = new HashMap<>() {{
        put(MusicCommonConfig.QUALITY_EQ, "eq.flac");
        put(QUALITY_ZQ, "zq.flac");
        put(QUALITY_SQ, "sq.flac");
        put(MusicCommonConfig.QUALITY_HQ, "hq.mp3");
        put(MusicCommonConfig.QUALITY_PQ, "pq.mp3");
        put(MusicCommonConfig.QUALITY_LQ, "lq.mp3");
    }};

    private String savePath;

    private MusicDownloadManager() {}

    private static class Inner {
        public static final MusicDownloadManager INSTANCE = new MusicDownloadManager();
    }

    public static MusicDownloadManager getInstance() {
        return Inner.INSTANCE;
    }

    public void setSavePath(String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.savePath = savePath;
    }


    public void download(SongBean songBean, DownloadListener listener) {
        Map<String, String> downloadLinkMap = songBean.getDownloadLinkMap();
        for (String quality : downloadLinkMap.keySet()) {
            String downloadLink = downloadLinkMap.get(quality);
            if (!downloadLink.contains(".mp3") && !downloadLink.contains(".flac")) {
                continue;
            }
            DownloadParams params = new DownloadParams.Builder()
                    .downloadLink(downloadLink)
                    .saveName(generateSongFileName(songBean, quality))
                    .build();
            DownloadHelper.download(params, listener);
        }
    }

    public void downloadHighestQuality(List<SongBean> songBeanList, DownloadListener listener) {
        for (SongBean bean : songBeanList) {
            downloadHighestQuality(bean, listener);
        }
    }

    public void downloadHighestQuality(SongBean songBean, DownloadListener listener) {
        Map<String, String> downloadLinkMap = songBean.getDownloadLinkMap();
        String downloadLink = downloadLinkMap.get(QUALITY_ZQ);
        String maxQuality = QUALITY_ZQ;
        if (downloadLink == null) {
            downloadLink = downloadLinkMap.get(QUALITY_SQ);
            maxQuality =QUALITY_SQ;
        }
        if (downloadLink == null) {
            downloadLink = downloadLinkMap.get(QUALITY_HQ);
            maxQuality = QUALITY_HQ;
        }

        if (downloadLink == null) {
            downloadLink = downloadLinkMap.get(QUALITY_PQ);
            maxQuality =QUALITY_PQ;
        }

        if (downloadLink == null || !downloadLink.contains(".mp3") && !downloadLink.contains(".flac")) {
            if (listener != null) {
                listener.onFailed(null);
            }
        }
        DownloadParams params = new DownloadParams.Builder()
                .downloadLink(downloadLink)
                .saveName(generateSongFileNameAuto(songBean, maxQuality))
                .build();
        DownloadHelper.download(params, listener);
    }

    public String generateSongFileName(SongBean songBean, String quality) {
        String singerName = songBean.getSingersName().get(0);
        if (songBean.getSingersName().size() > 1) {
            singerName = singerName + "&" +songBean.getSingersName().get(1);
        }
        return savePath + File.separator + songBean.getSongName() + "-" + singerName + "-" + songBean.getAlbumName() + "-" + QAULITY_EXT_MAPPER.get(quality);
    }

    public String generateSongFileNameAuto(SongBean songBean, String quality) {
        String singerName = songBean.getSingersName().get(0);
        if (songBean.getSingersName().size() > 1) {
            singerName = singerName + "&" +songBean.getSingersName().get(1);
        }
        String folder = savePath + File.separator + songBean.getSingersName().get(0);
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return folder + File.separator + songBean.getSongName() + "-" + singerName + "-" + songBean.getAlbumName() + "-" + QAULITY_EXT_MAPPER.get(quality);
    }
}
