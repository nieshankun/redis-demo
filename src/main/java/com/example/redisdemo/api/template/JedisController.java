package com.example.redisdemo.api.template;

import com.example.redisdemo.api.utils.Base64ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisCluster;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author nsk
 * 2018/12/16 11:43
 */
@RestController
@RequestMapping("/jedis")
public class JedisController {

    private final ConfigurableMimeFileTypeMap configurableMimeFileTypeMap = new ConfigurableMimeFileTypeMap();

    @Autowired
    private JedisCluster jedisCluster;

    @GetMapping("/set-string")
    public ResponseEntity setString() {
        long result = jedisCluster.setnx("weight", "62");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{key}")
    public ResponseEntity getValueByKey(@PathVariable String key) {
        String name = jedisCluster.get(key);
        return ResponseEntity.ok(name);
    }

    /**
     * 上传图片
     * @param file
     * @return
     */
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public ResponseEntity uploadImage(@RequestParam("file") MultipartFile file) {
        String id = UUID.randomUUID().toString();
        // 将图片编码为字符串
        String content = Base64ImageUtils.encodeImageToBase64(file);
        Map<String, String> criteria = new HashMap<>();
        criteria.put("content", content);
        criteria.put("fileName", file.getOriginalFilename());
        criteria.put("size", String.valueOf(file.getSize()));
        // 将图片信息以map的形式存储再redis
        jedisCluster.hmset(id, criteria);
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        return ResponseEntity.ok(map);
    }

    /**
     * 根据存储id下载图片
     * @param id
     * @return
     */
    @RequestMapping(path = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable String id) {
        // 获取图片信息
        Map<String, String> criteria = jedisCluster.hgetAll(id);
        return ResponseEntity.ok()
                .headers(httpHeaders(criteria.get("size"), criteria.get("fileName")))
                .cacheControl(CacheControl.maxAge(Long.MAX_VALUE, TimeUnit.HOURS).cachePublic())
                .body(body(criteria.get("content")));
    }


    /**
     * 将图片输入流转换为输入流资源
     * @param content
     * @return
     */
    private InputStreamResource body(String content) {
        return new InputStreamResource(Base64ImageUtils.decodeBase64ToImage(content));
    }

    /**
     * 设置响应的头部信息
     * @param size
     * @param name
     * @return
     */
    private HttpHeaders httpHeaders(String size, String name) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(configurableMimeFileTypeMap.getContentType(name)));
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=UTF-8''" + encode(name));
        httpHeaders.set(HttpHeaders.CONTENT_LENGTH, size);
        return httpHeaders;
    }

    /**
     * 文件名进行编码
     * @param name
     * @return
     */
    private String encode(String name) {
        try {
            return URLEncoder.encode(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
