package com.example.demo.ml.service;

import com.example.demo.auth.service.JwtService;
import com.example.demo.ml.dto.STTResponse;
import com.example.demo.ml.dto.TTSRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service class for handling text-to-speech (TTS) and speech-to-text (STT) operations.
 * This implementation uses RestClient for synchronous HTTP calls.
 */
@Service
public class SpeechService {

    private static final Log logger = LogFactory.getLog(SpeechService.class);

    private final RestClient restClient;

    @Autowired
    public SpeechService(RestClient restClient) {
        this.restClient = restClient;
        System.out.println(">>> SpeechService is running in REST mode using RestClient (Synchronous) <<<");
    }

    public byte[] textToSpeech(TTSRequest request) throws JsonProcessingException {
        logger.info("Received request: "+ request);
       // TTSRequest request = new TTSRequest(text);
       // logger.info("Generated request: "+ request);

        // ... in your Java code
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(request);
        logger.info("JSON: "+ jsonString);


        // The RestClient call is already blocking. We just return its result directly.
        return restClient.post()
                .uri("/tts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(byte[].class);
    }

    public String speechToText(MultipartFile audioFile) throws IOException {
        logger.info("Received STT request: "+ audioFile);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        };
        body.add("file", resource);

        // The RestClient call is blocking. We get the response and extract the text.
        STTResponse response = restClient.post()
                .uri("/stt")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(STTResponse.class);

        logger.info("STT response: "+ response);

        return (response != null) ? response.getText() : null;
    }
}