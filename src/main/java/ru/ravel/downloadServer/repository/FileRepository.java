package ru.ravel.downloadServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ravel.downloadServer.model.DownloadFile;

@Repository
public interface FileRepository extends JpaRepository<DownloadFile, Long> {

	DownloadFile findByKey(String key);

}