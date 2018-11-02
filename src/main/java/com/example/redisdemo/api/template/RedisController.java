package com.example.redisdemo.api.template;

import com.example.redisdemo.api.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nsk
 * 2018/10/30 22:49
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/set-string")
    public ResponseEntity setString(){
        boolean result = redisUtils.set("nsk","handsome",1000);
        return ResponseEntity.ok(result);
    }
}
