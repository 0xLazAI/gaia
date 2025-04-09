package com.lazai.controller;

import com.lazai.annotation.ResultLog;
import com.lazai.biz.service.ScoreBalanceService;
import com.lazai.core.common.JsonApiResponse;
import com.lazai.enums.MethodTypeEnum;
import com.lazai.request.ScoreAddRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreBalanceService scoreBalanceService;

    @GetMapping("/searchByUser")
    @ResultLog(name = "ScoreController.searchByUser", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> searchByUser(@RequestParam String ethAddress, HttpServletRequest request){
        return JsonApiResponse.success(scoreBalanceService.searchByUser(ethAddress));
    }

    @PostMapping("/addUserScore")
    @ResultLog(name = "ScoreController.addUserScore", methodType = MethodTypeEnum.UPPER)
    public JsonApiResponse<Object> addUserScore(@RequestBody ScoreAddRequest bizRequest, HttpServletRequest request){
        scoreBalanceService.addUserScore(bizRequest);
        return JsonApiResponse.success(true);
    }

}
