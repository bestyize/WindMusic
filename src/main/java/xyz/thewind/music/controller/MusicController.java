package xyz.thewind.music.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.NumberHelper;

import java.util.List;

@RestController
public class MusicController {
    private static final MusicDispatcher musicDispatcher=new MusicDispatcher();
    @RequestMapping("/api/download")
    public ResponseEntity<String> download(String songid,String quality,String src,String batch){
        String response;
        HttpHeaders headers=new HttpHeaders();
        headers.add("Content-Type","application/json");
        if(batch==null){
            SongBean songBean=musicDispatcher.downloadDispatcher(src,songid,quality);
            response=GsonConverter.toJson(songBean);
        }else {
            List<SongBean> songBeanList=musicDispatcher.downloadDispatcher(src,batch);
            response=GsonConverter.toJson(songBeanList);
        }
        return new ResponseEntity<>(response,headers, HttpStatus.OK);
    }
    @RequestMapping("/api/search")
    public ResponseEntity<List<SongBean>> search(String keyword,String num,String src){

        return new ResponseEntity<>(musicDispatcher.searchDispatcher(src,keyword, NumberHelper.parseNumber(num,10)),HttpStatus.OK);
    }

    @RequestMapping("/api/searchdown")
    public ResponseEntity<List<SongBean>> searchDown(String keyword,String num,String src){
        return new ResponseEntity<>(musicDispatcher.searchDownDispatcher(src,keyword,NumberHelper.parseNumber(num,10)),HttpStatus.OK);
    }

    @RequestMapping("/api/configupdate")
    public ResponseEntity<String> configUpdate(){
        return new ResponseEntity<>("",HttpStatus.OK);
    }
}
