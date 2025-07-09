package com.myaws.controller;

import com.myaws.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contains method to create bucket and upload file in bucket on AWS S3
 */
@RestController
@RequestMapping("/v1/s3bucket")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService service;


    @GetMapping("/add/{bucketName}")
    public ResponseEntity<String> createBucket(@PathVariable String bucketName) {
        return new ResponseEntity<>(service.createBucket(bucketName), HttpStatus.OK);
    }

    @PostMapping(path = "/uploadFile/{bucketName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file,
                                             @PathVariable String bucketName) {
        return new ResponseEntity<>(service.uploadFile(file, bucketName), HttpStatus.OK);
    }


    @DeleteMapping(path = "/deleteFile/{bucketName}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(bucketName, fileName), HttpStatus.OK);
    }

    @DeleteMapping("/deleteBucket/{bucketName}")
    public ResponseEntity<String> deleteBucket(@PathVariable String bucketName) {
        return new ResponseEntity<>(service.deleteBucket(bucketName), HttpStatus.OK);
    }

}
