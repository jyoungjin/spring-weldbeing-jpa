package com.onj.weldbeing.web;

import com.onj.weldbeing.domain.pqr.PqrInfo;
import com.onj.weldbeing.service.pqr.PqrInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PqrApiController {

    private final PqrInfoService pqrInfoService;

    @GetMapping("/getPqrInfoList")
    public List<PqrInfo> getPqrInfos(){
        return pqrInfoService.getPqrInfoList();
    }

}
