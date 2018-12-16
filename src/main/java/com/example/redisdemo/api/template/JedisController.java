package com.example.redisdemo.api.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisCluster;

/**
 * @author nsk
 * 2018/12/16 11:43
 */
@RestController
@RequestMapping("/jedis")
public class JedisController {
    @Autowired
    private JedisCluster jedisCluster;

    @GetMapping("/set-string")
    public ResponseEntity setString(){
        long result = jedisCluster.setnx("height","168");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{key}")
    public ResponseEntity getValueByKey(@PathVariable String key){
        String name = jedisCluster.get(key);
        return ResponseEntity.ok(name);
    }
}
