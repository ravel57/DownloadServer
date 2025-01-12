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

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MainController {

	private final FileService fileService;


	@GetMapping("/{secretKey}")
	public ResponseEntity<Object> downloadFileBySecretKey(
			@PathVariable String secretKey,
			HttpServletResponse response
	) {
		try {
			Path file = fileService.getFileBySecretKey(secretKey);
			if (file != null && Files.exists(file)) {
				response.setContentType("application/other");
				response.setContentLengthLong(Files.size(file));
				response.setCharacterEncoding("UTF-8");
				response.addHeader(
						"Content-Disposition",
						"attachment; filename=%s".formatted(fileService.translateFileName(file))
				);
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
			}
			return ResponseEntity.ok().build();
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/upload")
	ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
		try {
			String key = fileService.saveFileAndGetRandomUrl(multipartFile);
			return ResponseEntity.ok().body(Map.of("key", key));
		} catch (IOException e) {
			return ResponseEntity.badRequest().build();
		}
	}

}