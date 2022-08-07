package xyz.thewind.netool.censys.model;

import org.junit.Test;
import xyz.thewind.music.netool.censys.model.RequestModel;
import xyz.thewind.music.util.GsonUtil;

public class RequestModelTest {

    @Test
    public void testSearch() {
        System.out.println(GsonUtil.toJson(RequestModel.searchJBLicenseServer()));
    }
}
