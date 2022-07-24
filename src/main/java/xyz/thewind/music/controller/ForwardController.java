package xyz.thewind.music.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {
    @RequestMapping("/")
    public String index() {
        return "music";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/player")
    public String play() {
        return "player";
    }

    @RequestMapping("/manager")
    public String manage() {
        return "manager";
    }

    @RequestMapping("/upload")
    public String upload() {
        return "upload";
    }
}
