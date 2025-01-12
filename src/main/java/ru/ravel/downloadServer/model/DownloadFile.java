package ru.ravel.downloadServer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String path;

	String key;
}
