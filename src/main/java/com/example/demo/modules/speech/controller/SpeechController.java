package com.example.demo.modules.speech.controller;

import com.example.demo.modules.speech.dto.TTSRequest;
import com.example.demo.modules.speech.service.SpeechService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/speech")
public class SpeechController {

    @Autowired
    private final SpeechService speechService;

    /**
     * Endpoint for converting text to speech.
     *
     * @param request the TTSRequest containing the text to convert
     * @return a ResponseEntity containing the audio data
     * @throws JsonProcessingException if there is an error processing JSON
     */
    @PostMapping("/tts")
    public ResponseEntity<byte[]> convertTextToSpeech(@RequestBody TTSRequest request) throws JsonProcessingException {
        // The service call is now blocking. The thread will wait here for the result.
        byte[] audioBytes = speechService.textToSpeech(request);

        // Once the result is ready, build and return the response.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/wav")
                .body(audioBytes);
    }

    /**
     * Endpoint for converting speech to text.
     *
     * @param file the audio file to transcribe
     * @return a ResponseEntity containing the transcribed text
     * @throws IOException if there is an error reading the file
     */
    @PostMapping(value = "/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertSpeechToText(@RequestParam("file") MultipartFile file) throws IOException {
        // The service call blocks until the transcription is complete.
        String transcribedText = speechService.speechToText(file);

        return ResponseEntity.ok(transcribedText);
    }
}
