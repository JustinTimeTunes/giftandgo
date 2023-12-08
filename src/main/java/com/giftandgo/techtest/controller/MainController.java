package com.giftandgo.techtest.controller;

import com.giftandgo.techtest.service.FileParseService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
public class MainController {

    private final FileParseService fileParseService;

    public MainController(FileParseService fileParseService) {

        this.fileParseService = fileParseService;
    }

    @PostMapping("/uploadFile")
    public List<Object> uploadFile(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "skipValidation", required = false, defaultValue = "false") Boolean skipValidation,
                                   HttpServletResponse response,
                                   HttpServletRequest request) {

        List<Object> outcomeFile = fileParseService.parseFile(file, skipValidation);

        response.addHeader("Content-Disposition", "attachment; filename=\"OutcomeFile.json\"");
        return outcomeFile;
    }
}
