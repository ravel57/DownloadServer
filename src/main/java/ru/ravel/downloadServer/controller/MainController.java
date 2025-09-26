package ru.ravel.downloadServer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.ravel.downloadServer.service.FileService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MainController {

	private final FileService fileService;

	@GetMapping(value = "/{secretKey}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> downloadFileBySecretKey(@PathVariable String secretKey) throws IOException {
		if ("favicon.ico".equals(secretKey)) {
			return ResponseEntity.notFound().build();
		}
		Path file = fileService.getFileBySecretKey(secretKey);
		if (file == null || !Files.exists(file)) {
			return ResponseEntity.notFound().build();
		}
		long size = Files.size(file);
		String filename = fileService.translateFileName(file);
		String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
		StreamingResponseBody body = outputStream -> Files.copy(file, outputStream);
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(size)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(encoded))
				.header(HttpHeaders.CONNECTION, "close")
				.header(HttpHeaders.ACCEPT_RANGES, "none")
				.body(body);
	}


	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
		String key = fileService.saveFileAndGetRandomUrl(multipartFile);
		return ResponseEntity.ok(Map.of("key", key));
	}
}