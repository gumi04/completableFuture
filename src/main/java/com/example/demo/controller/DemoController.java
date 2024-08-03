package com.example.demo.controller;

import com.example.demo.model.dto.DataFileDto;
import com.example.demo.service.FileService;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {


    private final FileService fileService;

    @Autowired
    public DemoController(FileService fileService){
        this.fileService = fileService;
    }

    @RequestMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> helloWorld() {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Hello, World!");
        return ResponseEntity.ok(map);
    }


    @PostMapping(value = "/multi", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DataFileDto>> multi(@RequestParam("files")MultipartFile[] files) {
        return ResponseEntity.ok(fileService.readFilesMulti(files));
    }

    @PostMapping(value = "/single", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DataFileDto>> single(@RequestParam("files")MultipartFile[] files) {
        return ResponseEntity.ok(fileService.readFilesSingle(files));
    }
}
