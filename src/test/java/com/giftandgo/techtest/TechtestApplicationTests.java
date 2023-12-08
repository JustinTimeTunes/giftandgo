package com.giftandgo.techtest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.io.InputStream;
import java.io.IOException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TechtestApplicationTests {

    private static final String CORRECT_RESULT = "[{name=John Smith, transport=Rides A Bike, topSpeed=12.1}, {name=Mike Smith, transport=Drives an SUV, topSpeed=95.5}, {name=Jenny Walters, transport=Rides A Scooter, topSpeed=15.3}]";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSuccessfulFileUpload() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("EntryFile.txt"),
                                                                        List.class,
                                                                        "false");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().size()).isEqualTo(3);
        assertThat(responseEntity.getBody().toString()).isEqualTo(CORRECT_RESULT);
    }

    @Test
    public void testSuccessfulFileUploadNoValidation() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("EntryFile.txt"),
                                                                        List.class,
                                                                        "true");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().size()).isEqualTo(3);
        assertThat(responseEntity.getBody().toString()).isEqualTo(CORRECT_RESULT);
    }

    @Test
    public void testInvalidNoOfFields() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("InvalidNoOfFields-EntryFile.txt"),
                                                                        List.class,
                                                                        "false");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        List<Object> list = responseEntity.getBody();
        Map<String, Object> map = (Map<String, Object>) list.get(1);

        assertThat(map.get("errorMsg")).isEqualTo("Invalid number of fields!");
        assertThat(map.get("rowNumber")).isEqualTo(2);
    }

    @Test
    public void testInvalidNoOfFieldsNoValidation() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("InvalidNoOfFields-EntryFile.txt"),
                                                                        List.class,
                                                                        "true");

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        List<Object> responseList = responseEntity.getBody();
        Map<String, Object> entryMap = (Map<String, Object>) responseList.get(1);

        assertThat(entryMap.get("errorMsg")).isNull();
        assertThat(entryMap.get("name")).isEqualTo("Mike Smith");
    }

    @Test
    public void testInvalidNumber() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("InvalidNumber-EntryFile.txt"),
                                                                        List.class,
                                                                        "false");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        List<Object> responseList = responseEntity.getBody();
        Map<String, Object> entryMap = (Map<String, Object>) responseList.get(0);

        assertThat(entryMap.get("errorMsg")).isEqualTo("Invalid number!");
        assertThat(entryMap.get("rowNumber")).isEqualTo(1);
    }

    @Test
    public void testInvalidNumberNoValidation() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("InvalidNumber-EntryFile.txt"),
                                                                        List.class,
                                                                        "true");

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        List<Object> responseList = responseEntity.getBody();
        Map<String, Object> entryMap = (Map<String, Object>) responseList.get(0);

        assertThat(entryMap.get("errorMsg")).isNull();
        assertThat(entryMap.get("name")).isEqualTo("John Smith");
    }

    @Test
    public void testInvalidUUID() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("InvalidUUID-EntryFile.txt"),
                                                                        List.class,
                                                                        "false");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        List<Object> responseList = responseEntity.getBody();
        Map<String, Object> entryMap = (Map<String, Object>) responseList.get(1);

        assertThat(entryMap.get("errorMsg")).isEqualTo("Invalid UUID!");
        assertThat(entryMap.get("rowNumber")).isEqualTo(2);
    }

    @Test
    public void testInvalidUUIDNoValidation() throws Exception {

        ResponseEntity<List> responseEntity = restTemplate.postForEntity(getUploadUrl(),
                                                                        getRequestEntity("InvalidUUID-EntryFile.txt"),
                                                                        List.class,
                                                                        "true");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        List<Object> responseList = responseEntity.getBody();
        Map<String, Object> entryMap = (Map<String, Object>) responseList.get(1);

        assertThat(entryMap.get("errorMsg")).isNull();
        assertThat(entryMap.get("name")).isEqualTo("Mike Smith");
        assertThat(entryMap.get("transport")).isEqualTo("Drives an SUV");
    }

    private String getUploadUrl() {
        return "http://localhost:" + port + "/uploadFile?skipValidation={skipValidation}";
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> getRequestEntity(String resourceFile) throws IOException {

        Resource resource = new ClassPathResource(resourceFile);

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(resource.getInputStream(), resource.getFilename()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        return requestEntity;
    }

    private class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }
    }
}
