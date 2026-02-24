package ru.ravel.downloadServer.service;

import com.ibm.icu.text.Transliterator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ravel.downloadServer.model.DownloadFile;
import ru.ravel.downloadServer.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FileService {

	private static final String ALPHABET = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890$-_+!";

	@Value("${files-path}")
	private String filesPath;

	private final FileRepository fileRepository;


	public Path getFileBySecretKey(String secretKey) {
		return Paths.get(fileRepository.findByKey(secretKey).getPath());
	}


	public String translateFileName(Path file) {
		return Transliterator.getInstance("Russian-Latin/BGN")
				.transliterate(file.getFileName().toString().replace(" ", "_"));
	}


	public String saveFileAndGetRandomUrl(MultipartFile multipartFile) throws IOException {
		File directory = new File(filesPath);
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new IOException("Не удалось создать директорию: %s".formatted(filesPath));
			}
		}
		File file = new File(directory, Objects.requireNonNull(multipartFile.getOriginalFilename()));
		multipartFile.transferTo(file);
		String key = new Random().ints(10, 0, ALPHABET.length())
				.mapToObj(ALPHABET::charAt)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		fileRepository.save(new DownloadFile(null, file.getPath(), key));
		return key;
	}

}