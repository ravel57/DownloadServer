package ru.ravel.downloadServer.controller;

import jakarta.servlet.http.HttpServletResponse;
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


	@GetMapping("/{secretKey}")
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
		String contentType = Files.probeContentType(file);
		MediaType mediaType = (contentType == null || contentType.isBlank())
				? MediaType.APPLICATION_OCTET_STREAM
				: MediaType.parseMediaType(contentType);
		String filenameStar = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
		String contentDisposition = "attachment; filename=\"%s\"; filename*=UTF-8''%s".formatted(filename.replace("\"", "'"), filenameStar);
		StreamingResponseBody body = outputStream -> {
			try (var in = Files.newInputStream(file)) {
				in.transferTo(outputStream);
			}
		};
		return ResponseEntity.ok()
				.contentType(mediaType)
				.contentLength(size)
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				.header(HttpHeaders.ACCEPT_RANGES, "none")
				.body(body);
	}


	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
		String key = fileService.saveFileAndGetRandomUrl(multipartFile);
		return ResponseEntity.ok(Map.of("key", key));
	}

}