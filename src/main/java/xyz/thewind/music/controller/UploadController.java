package xyz.thewind.music.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.thewind.music.auth.TokenHelper;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.Token;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @RequestMapping(value = "/img", method = RequestMethod.POST)
    public ResponseEntity<CommonResp> uploadFile(MultipartFile[] files, HttpServletRequest request) throws Exception {
        Token token = TokenHelper.parseToken(request.getCookies());
        CommonResp commonResp = new CommonResp();
        if (files == null || files.length == 0) {
            commonResp.code(404);
            commonResp.msg("upload");
            commonResp.obj("未选择文件");
        } else {
            for (MultipartFile file : files) {
                String fileDir = "D:/wind/img/";
                String fileName = file.getOriginalFilename();
                File fd = new File(fileDir);
                fd.mkdirs();
                String saveName = UUID.randomUUID().toString();
                if (fileName.contains(".")) {
                    saveName += fileName.substring(fileName.lastIndexOf("."));
                }
                File dest = new File(fileDir + saveName);
                file.transferTo(dest);
            }
            commonResp.code(0);
            commonResp.msg("upload");
            commonResp.obj("上传成功");
        }
        return new ResponseEntity<>(commonResp, HttpStatus.OK);

    }
}
