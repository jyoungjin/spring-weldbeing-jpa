package com.onj.weldbeing.web;

import com.onj.weldbeing.service.pqr.PqrInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PqrInfoService pqrInfoService;

    @GetMapping("/")
    public String index(Model model){

        model.addAttribute("pqrInfoList", pqrInfoService.getPqrInfoList());

        return "index";
    }

}
