package com.demo.note.controller;


import com.demo.note.entity.Note;
import com.demo.note.entity.User;
import com.demo.note.service.Noteservice;
import com.demo.note.service.Userservice;
import com.demo.note.utils.Html2PlainText;

import com.demo.note.utils.HtmlToPdfUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;



@Controller

public class Notecontroller {
    @Autowired
    Noteservice noteservice;
    @Autowired
    Userservice userservice;

    @RequestMapping("/note")
    public String note(){
        return "note";
    }

    @RequestMapping("/note/save")
    public String notesave(HttpServletRequest request){
        String uname= (String) request.getSession().getAttribute("user");
        if (null==uname){
            return "login";
        }else {
            String content=request.getParameter("content");
            Note note=new Note();
            note.setInformation(content);
            User user=userservice.find(uname);
            int uid=user.getId();
            //System.out.println(uid);
            note.setUid(uid);
            String title= Html2PlainText.convert(content);
            //System.out.println(title);
            title = title.substring(0, title.indexOf("\n"));
            //html处理为纯文本
            //System.out.println(title);
            note.setTitle(title);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime= LocalDateTime.now();
            String time=localDateTime.format(formatter);
            note.setSdate(time);
            noteservice.addnote(note);
            return "note";

        }
    }

    @RequestMapping(value = {"upload", "upload/"},method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadimg(@RequestParam("upload") MultipartFile multipartFile,HttpServletRequest request){
        //固定值
        /**String originalName = multipartFile.getOriginalFilename();
        // generate file name
        String localFileName = System.currentTimeMillis() + "-" + originalName;
        String imageContextPath = request.getContextPath() + "/uploadImage" + "/" + localFileName;
        Map<String, String> map = Maps.newConcurrentMap();
        map.put("uploaded", "1");
        map.put("url", imageContextPath);**/
        //必须这样{"uploaded":"1","url", "图片URL"} 或者自己修改CKEditor5 js源代码
        // System.out.println(map);+
        return noteservice.ckEditorUploadImage(multipartFile,request);
        //return map;
    }

    @RequestMapping("/note/delete")
    @Transactional
    public String del(Model m,HttpServletRequest request){
        int nid= Integer.parseInt(request.getParameter("nid"));
        //System.out.println(nid);
        noteservice.delnote(nid);
        return "redirect:/note/show";
    }

    @RequestMapping("/note/modify")
    public String modify(Model model,HttpServletRequest request){
        int nid= Integer.parseInt(request.getParameter("nid"));
        Note note=noteservice.findbynid(nid);
        model.addAttribute("minfo",note);
        return "modify";
    }

    @RequestMapping("/note/change")
    public String change(HttpServletRequest request){
        int nid = Integer.parseInt(request.getParameter("nid"));
        String content=request.getParameter("content");
        Note note=noteservice.findbynid(nid);
        note.setInformation(content);
        String title= Html2PlainText.convert(content);
        title = title.substring(0, title.indexOf("\n"));
        //html处理为纯文本
        note.setTitle(title);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime= LocalDateTime.now();
        String time=localDateTime.format(formatter);
        note.setSdate(time);
        noteservice.addnote(note);
        return "redirect:/note/show";
    }

    @RequestMapping("/note/select")
    public String select(Model model,HttpServletRequest request){
        String uname= (String) request.getSession().getAttribute("user");
        int uid = userservice.find(uname).getId();
        String keyword=request.getParameter("keyword");
        List <Note> notes=noteservice.selnote("%"+keyword+"%",uid);
        //System.out.println(notes);
        if (CollectionUtils.isEmpty(notes)){
            String message="无搜索结果";
            System.out.println(message);
            model.addAttribute("nothing",message);
        }else {
            model.addAttribute("infos",notes);
        }
        return "select";
    }

    @RequestMapping("/note/show")
    public String show(Model model,HttpServletRequest request,@RequestParam(name = "page", defaultValue = "1", required = false) int page, @RequestParam(name = "size", defaultValue = "5", required = false) int size) {
        /**String uname= (String) request.getSession().getAttribute("user");
        if (null==uname){
            return "login";
        }else {
            //String uname = (String) request.getSession().getAttribute("user");
            int uid = userservice.find(uname).getId();
            //System.out.println(uid);
            List<Note> notes = noteservice.find(uid);
            model.addAttribute("infos", notes);
            return "show";
        }**/
        String uname= (String) request.getSession().getAttribute("user");
        if (null==uname){
            return "login";
        }else {
            int uid = userservice.find(uname).getId();
            PageInfo<Note> pageInfo = noteservice.getpage(uid,page,size);
            //Page<Note> notes=noteservice.getpage(, );
            model.addAttribute("infos",pageInfo);
            return "show";
        }

    }

    @RequestMapping("/note/share")
    public void share(HttpServletRequest request, HttpServletResponse response) {
        int nid = Integer.parseInt(request.getParameter("nid"));
        Note note = noteservice.findbynid(nid);
        String fileContent = note.getInformation();
        System.out.println(fileContent);
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + new Date().getTime() + ".pdf");
            //将生成的pdf文件获取过来
            File pdfFile = HtmlToPdfUtil.createFile(fileContent);
            //读写操作，将文件传到前台导出
            OutputStream os = response.getOutputStream();
            InputStream is = new FileInputStream(pdfFile);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            os.write(buffer);
            os.close();
            //完成后把文件删除
            pdfFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

    /**@RequestMapping("/note/prompt")
    public String prompt(Model model,HttpServletRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(request.getParameter("promptime"));
        String ptime = localDateTime.format(formatter);
        System.out.println(ptime);
        LocalDateTime localdatetime = LocalDateTime.now();
        String time = localdatetime.format(formatter);
        System.out.println(time);
        while (time.equals(ptime)) {
            model.addAttribute("pmessage", "您设定的提示已生效");
            System.out.println("您设定的提示已生效");
            return "redirect:/note/show";
        }
        return "redirect:/note/show";
    }

    }**/




