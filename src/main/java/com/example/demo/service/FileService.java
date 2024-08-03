package com.example.demo.service;

import com.example.demo.model.dto.DataFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    List<DataFileDto> readFilesMulti(MultipartFile[] files);

    List<DataFileDto> readFilesSingle(MultipartFile[] files);
}
