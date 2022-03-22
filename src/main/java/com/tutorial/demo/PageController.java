package com.tutorial.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author saintrivers
 */
@Controller
public class PageController {

    @GetMapping("/success")
    public String success(){
        return "success";
    }

    @GetMapping("/login-me")
    public ModelAndView login(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        return mv;
    }
}
