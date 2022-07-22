package xyz.thewind.music.music.impl.qq.bean;

import xyz.thewind.music.music.impl.qq.QqMusic;

import java.util.List;

public class DownloadRequestBean {

    public Common comm = new Common();
    public Queryvkey queryvkey = new Queryvkey();

    public Queryvkey getQueryvkey() {
        return queryvkey;
    }

    public void setQueryvkey(Queryvkey queryvkey) {
        this.queryvkey = queryvkey;
    }

    public  class Queryvkey {
        String method = "CgiGetVkey";
        String module = "vkey.GetVkeyServer";
        public Queryvkey.Param param = new Queryvkey.Param();

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public  class Param {
            int checklimit = 0;
            int ctx = 1;
            int downloadfrom = 0;
            List<String> filename;
            String guid = "810BA5E07103080839C18EE1FD2EE9B5";
            List<String> musicfile;
            String nettype = "";
            String referer = "y.qq.com";
            int scene = 0;
            List<String> songmid;
            List<Integer> songtype;

            String uin;

            public int getChecklimit() {
                return checklimit;
            }

            public void setChecklimit(int checklimit) {
                this.checklimit = checklimit;
            }

            public int getCtx() {
                return ctx;
            }

            public void setCtx(int ctx) {
                this.ctx = ctx;
            }

            public int getDownloadfrom() {
                return downloadfrom;
            }

            public void setDownloadfrom(int downloadfrom) {
                this.downloadfrom = downloadfrom;
            }

            public List<String> getFilename() {
                return filename;
            }

            public void setFilename(List<String> filename) {
                this.filename = filename;
            }

            public String getGuid() {
                return guid;
            }

            public void setGuid(String guid) {
                this.guid = guid;
            }

            public List<String> getMusicfile() {
                return musicfile;
            }

            public void setMusicfile(List<String> musicfile) {
                this.musicfile = musicfile;
            }

            public String getNettype() {
                return nettype;
            }

            public void setNettype(String nettype) {
                this.nettype = nettype;
            }

            public String getReferer() {
                return referer;
            }

            public void setReferer(String referer) {
                this.referer = referer;
            }

            public int getScene() {
                return scene;
            }

            public void setScene(int scene) {
                this.scene = scene;
            }

            public List<String> getSongmid() {
                return songmid;
            }

            public void setSongmid(List<String> songmid) {
                this.songmid = songmid;
            }

            public List<Integer> getSongtype() {
                return songtype;
            }

            public void setSongtype(List<Integer> songtype) {
                this.songtype = songtype;
            }

            public String getUin() {
                return uin;
            }

            public void setUin(String uin) {
                this.uin = uin;
            }
        }
    }

    public static class Common {
        public String authst;
        public String tmeAppID = "qqmusic";
        public int tmeLoginType = 1;
        public String uin;
        public String wid = "0";

        public String ct = "19";
        public String cv = "1873";

        public String getAuthst() {
            return authst;
        }

        public void setAuthst(String authst) {
            this.authst = authst;
        }

        public String getTmeAppID() {
            return tmeAppID;
        }

        public void setTmeAppID(String tmeAppID) {
            this.tmeAppID = tmeAppID;
        }

        public int getTmeLoginType() {
            return tmeLoginType;
        }

        public void setTmeLoginType(int tmeLoginType) {
            this.tmeLoginType = tmeLoginType;
        }

        public String getUin() {
            return uin;
        }

        public void setUin(String uin) {
            this.uin = uin;
        }

        public String getWid() {
            return wid;
        }

        public void setWid(String wid) {
            this.wid = wid;
        }

        public String getCt() {
            return ct;
        }

        public void setCt(String ct) {
            this.ct = ct;
        }

        public String getCv() {
            return cv;
        }

        public void setCv(String cv) {
            this.cv = cv;
        }
    }
}
