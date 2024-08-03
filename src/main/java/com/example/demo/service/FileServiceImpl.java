package com.example.demo.service;

import com.example.demo.model.dto.DataFileDto;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final ExecutorService executorService;

    @Autowired
    public FileServiceImpl(ExecutorService executor) {
        this.executorService = executor;
    }


    @Override
    public List<DataFileDto> readFilesMulti(MultipartFile[] files) {
        List<CompletableFuture<List<DataFileDto>>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                CompletableFuture<List<DataFileDto>> future =
                        CompletableFuture.supplyAsync(() -> {
                                    log.info("Reading file in thread: " + Thread.currentThread().getName());
                                    return readFile(file);
                                }, executorService)
                                .thenApply(dataFileDtos -> {
                                    log.info("Processing file in thread: " + Thread.currentThread().getName());
                                    convertMayust(dataFileDtos);
                                    return dataFileDtos;
                                });
                futures.add(future);
            }
        }

        List<DataFileDto> dataFileDtos = new ArrayList<>();
        for (CompletableFuture<List<DataFileDto>> future : futures) {
            try {
                dataFileDtos.addAll(future.get());
            } catch (Exception e) {
                log.error("Error reading file: {}", e.getMessage());
            }
        }
        log.info("Rercords processed: {}", dataFileDtos.size());
        return dataFileDtos;

    }

    @Override
    public List<DataFileDto> readFilesSingle(MultipartFile[] files) {
        List<DataFileDto> response = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                log.info("Reading file in thread: " + Thread.currentThread().getName());
                log.info("Reading file " + file.getOriginalFilename());
                try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                    List<String[]> records = csvReader.readAll();
                    if (records.isEmpty()) continue;

                    String[] headers = records.get(0);

                    for (int i = 1; i < records.size(); i++) {
                        String[] row = records.get(i);

                        DataFileDto dto = new DataFileDto();

                        for (int j = 0; j < headers.length; j++) {
                            switch (headers[j].toLowerCase()) {
                                case "id":
                                    dto.setId(Integer.valueOf(row[j]));
                                    break;
                                case "first_name":
                                    dto.setName(row[j]);
                                    break;
                                case "last_name":
                                    dto.setLastName(row[j]);
                                    break;
                                case "email":
                                    dto.setEmail(row[j]);
                                    break;
                                case "gender":
                                    dto.setGender(row[j]);
                                    break;
                                case "ip_address":
                                    dto.setIpAddress(row[j]);
                                    break;
                                default:
                                    break;
                            }
                        }

                        response.add(dto);
                    }
                } catch (CsvException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Records processed: {}", response.size());
        convertMayust(response);
        return response;

    }

    private List<DataFileDto> convertMayust(List<DataFileDto> dataFileDtos) {
        dataFileDtos.forEach(dataFileDto -> {
            dataFileDto.setName(dataFileDto.getName().toUpperCase());
            dataFileDto.setLastName(dataFileDto.getLastName().toUpperCase());
            dataFileDto.setEmail(dataFileDto.getEmail().toUpperCase());
        });
        return dataFileDtos;
    }

    private List<DataFileDto> readFile(MultipartFile file) {
        log.info("Reading file " + file.getOriginalFilename());
        List<DataFileDto> dataFiles = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> records = csvReader.readAll();
            if (records.isEmpty())
                return dataFiles;

            String[] headers = records.get(0);

            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                DataFileDto dto = new DataFileDto();

                for (int j = 0; j < headers.length; j++) {
                    switch (headers[j].toLowerCase()) {
                        case "id":
                            dto.setId(Integer.valueOf(row[j]));
                            break;
                        case "first_name":
                            dto.setName(row[j]);
                            break;
                        case "last_name":
                            dto.setLastName(row[j]);
                            break;
                        case "email":
                            dto.setEmail(row[j]);
                            break;
                        case "gender":
                            dto.setGender(row[j]);
                            break;
                        case "ip_address":
                            dto.setIpAddress(row[j]);
                            break;
                        default:
                            break;
                    }
                }
                dataFiles.add(dto);
            }
        } catch (CsvException | IOException e) {
            log.info("Error processing".concat(e.getMessage()));
        }
        return dataFiles;
    }
}
