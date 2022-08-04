package xyz.thewind.music.netool.censys.bean.search;

public class SearchHostResponse {
    public int code;
    public SearchHostResult result = new SearchHostResult();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SearchHostResult getResult() {
        return result;
    }

    public void setResult(SearchHostResult result) {
        this.result = result;
    }
}
