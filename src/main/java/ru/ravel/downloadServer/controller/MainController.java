package ru.ravel.downloadServer.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ravel.downloadServer.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;


@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MainController {

	private final FileService fileService;


	@GetMapping("/{secretKey}")
	public void downloadFileBySecretKey(
			@PathVariable String secretKey,
			HttpServletResponse response
	) throws IOException {
		if ("favicon.ico".equals(secretKey)) return;
		Path file = fileService.getFileBySecretKey(secretKey);
		if (file == null || !Files.exists(file)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String filename = fileService.translateFileName(file);
		String contentType = Files.probeContentType(file);
		if (contentType == null || contentType.isBlank()) {
			contentType = "application/octet-stream";
		}
		response.setContentType(contentType);
		response.setContentLengthLong(Files.size(file));
		response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"%s\"; filename*=UTF-8''%s".formatted(filename, encode(filename, UTF_8))
		);
		try (var in = Files.newInputStream(file); var out = response.getOutputStream()) {
			in.transferTo(out);
			out.flush();
		}
	}


	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
		String key = fileService.saveFileAndGetRandomUrl(multipartFile);
		return ResponseEntity.ok(Map.of("key", key));
	}

}