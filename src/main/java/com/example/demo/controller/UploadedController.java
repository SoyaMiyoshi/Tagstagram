package com.example.demo.controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import java.util.LinkedHashMap;
import java.util.List;

import com.example.demo.domein.Uploaded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import com.example.demo.repository.UploadedRepository;
import com.example.demo.service.UploadedService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Properties;


@Controller
@RequestMapping("")
public class UploadedController {

    private String CLIENT_ID = "";
    private String CLIENT_SECRET = "";
    private String STORAGE_PATH = "src/main/resources/saved/";
    private String login;

    @Autowired
    private UploadedService uploadedService;

    @Autowired
    UploadedRepository uploadedRepository;

    public void myInit () {
        try {
            InputStream input = new FileInputStream("src/main/resources/application.properties");
            Properties prop = new Properties();
            prop.load(input);
            CLIENT_ID = prop.getProperty("github.clientid");
            CLIENT_SECRET = prop.getProperty("github.clientsecret");

        } catch (IOException ex) {
            System.out.println("Could n toread prperties file!");
        }

    }

    public String myGitHubApiCall ( Map<String,Object> params,  String url, String method) throws IOException {

        URL myurl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
        conn.setRequestMethod(method);

        if (method.equals("POST")) {
            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        return(in.lines().collect(Collectors.joining()));
    }

    public void myAutoTaggerCall (String image_id) throws IOException{
        String virtual_python = "src/main/resources/tagger/venv/bin/python3";
        String python_ex = "src/main/resources/tagger/mytagger.py";
        Runtime.getRuntime().exec(virtual_python + " " + python_ex + " " + image_id + " &");
    }

    @GetMapping("/")
    public RedirectView home() {
        myInit();
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID);
        return redirectView;
    }

    @GetMapping("/view_mine")
    public String home(Model model) {
        List<Uploaded> uploadeds = uploadedService.finduploadeds(login);
        model.addAttribute("uploadeds", uploadeds);
        return "/uploadeds/view_mine";
    }

    @GetMapping("/view_all")
    public String home1(Model model) {
        List<Uploaded> uploadeds = uploadedService.findAll();
        model.addAttribute("uploadeds", uploadeds);
        return "/uploadeds/view_all";
    }

    @GetMapping("/uploadnew")
    public String newUpload() {
        return "/uploadeds/uploadnew";
    }

    @GetMapping("/callback0")
    public String home0(HttpServletRequest request) throws IOException {
        myInit();
        String code = request.getParameter("code");
        String url = "https://github.com/login/oauth/access_token";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        String token = myGitHubApiCall(params, url, "POST").split("&", 2)[0].split("=", 2)[1];
        Map<String,Object> params2 = new LinkedHashMap<>();
        String res = myGitHubApiCall(params2, "https://api.github.com/user?access_token=" + token, "GET" );
        login = res.split(",", 2)[0].split(":", 2)[1].replace("\"", "");
        System.out.println(login);
        return "/uploadeds/uploadnew";
    }

    @PostMapping("/uploadnew")
    public String handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("caption") String caption) throws IOException {

        if (!file.isEmpty() && login != null && !login.isEmpty()) {
            Uploaded uploaded = new Uploaded();
            uploaded.setOwner(login);
            uploaded.setCaption(caption);
            uploadedService.save(uploaded);
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            File destination = new File( STORAGE_PATH + uploaded.getId().toString() + ".jpg");
            ImageIO.write(src, "jpg", destination);
            myAutoTaggerCall(uploaded.getId().toString());

        }

        return "redirect:/view_mine";
    }

}
