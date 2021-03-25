package ru.ravel.downloadServer.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.ravel.downloadServer.Service.Impls.FileServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/")
public class Main {

    @Autowired
    FileServiceImpl fileService;

    @RequestMapping("/{secretKey}")
    public void downloadFileBySecretKey(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String secretKey
            /*@RequestHeader String referer*/
    ) {
        //Check the renderer
        //if (referer != null && !referer.isEmpty()) {
        //    //do nothing or send error
        //}
        //If user is not authorized - he should be thrown out from here itself

        //Authorized user will download the file
        //https://howtodoinjava.com/spring-mvc/spring-mvc-download-file-controller-example/

        try {
            Path file = fileService.getFileBySecretKey(secretKey);
            if (file != null & Files.exists(file)) {
                response.setContentType("application/other");
                response.setContentLengthLong(Files.size(file));
                response.addHeader(
                        "Content-Disposition",
                        "attachment; filename=" + fileService.translateFileName(file)
                );
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
//              response.getOutputStream().write(bytes);
            }
        } catch (IOException e) {
            e.toString();
        }
    }

    @GetMapping(value = "/")
    String randUrl(HttpSession httpSession) {
        httpSession.setAttribute("url", "http://" + fileService.getIP() + ":8000/download/" + fileService.randomUrl());
//        httpSession.setAttribute("url", fileImpl.randomUrl());
        return "main";
    }
}

