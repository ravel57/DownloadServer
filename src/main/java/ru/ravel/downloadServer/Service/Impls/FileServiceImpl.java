package ru.ravel.downloadServer.Service.Impls;

import com.ibm.icu.text.Transliterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ravel.downloadServer.Configs.FileDaoImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.*;
import java.io.*;

@Service
public class FileServiceImpl {

    @Autowired
    private FileDaoImpl fileDao;

    public Path getFileBySecretKey(String secretKey) {
        return Paths.get(fileDao.getFileNameBySecretKey(secretKey));
    }

    public String translateFileName(Path file) {
        return Transliterator
                .getInstance("Russian-Latin/BGN")
                .transliterate(file.getFileName().toString().replace(" ", "_"));
    }

    public String getIP() {
        try {
            return new BufferedReader(new InputStreamReader(
                    new URL("http://checkip.amazonaws.com").openStream())
            ).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public String randomUrl() {
        Random r = new Random();
        String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890$-_+!";
//        String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZБбвГгДдЁёЖжзИиЙйкЛлмнПптФфЦцЧчШшЩщЪъЫыЭэЮюЯя1234567890!@#$%^&*()_-+=\\/'\";:`~";
        StringBuilder outStr = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            outStr.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
        }
        return outStr.toString();
    }
}
