package com.demo.note.service;

import com.demo.note.dao.Notedao;
import com.demo.note.entity.Note;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Component
public class Noteservice  {
    @Autowired
    Notedao notedao;

    private static final String CK_IMAGE_PATH = File.separator + "uploadImage";

    public Note addnote(Note note){
        return notedao.save(note);
    }


    public void delnote(int nid){
         notedao.deleteByNid(nid);
    }

    public List<Note> selnote(String title,int uid){
        return notedao.findByTitleLikeAndUid(title,uid);
    }

    public Map<String, String> ckEditorUploadImage(MultipartFile file, HttpServletRequest request) {
        if(file==null || "".equals(file.getOriginalFilename().trim())) {
            return generateResult(false, "#");
        }
        String originalName = file.getOriginalFilename();
        // generate file name
        String localFileName = System.currentTimeMillis() + "-" + originalName;
        // get project path
        String projectRealPath = request.getSession().getServletContext().getRealPath("");
        // get the real path to store received images
        String realPath = projectRealPath + CK_IMAGE_PATH;
        File imageDir = new File(realPath);
        if(!imageDir.exists()) {
            imageDir.mkdirs();
        }

        String localFilePath = realPath + File.separator + localFileName;
        try {
            file.transferTo(new File(localFilePath));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // log here
        } catch (IOException e) {
            e.printStackTrace();
            // log here
        }
        String imageContextPath = request.getContextPath() + "/uploadImage" + "/" + localFileName;
        //System.out.println(imageContextPath);
        // log here +
        System.out.println("received file original name: " + originalName);
        System.out.println("stored local file name: " + localFileName);
        System.out.println("file stored path: " + localFilePath);
        System.out.println("returned url: " + imageContextPath);
        // log here -
        return generateResult(true, imageContextPath);
    }

    private Map<String, String> generateResult(boolean uploaded, String relativeUrl){
        Map<String, String> result = new HashMap<String, String>();
        result.put("uploaded", uploaded + "");
        result.put("url", relativeUrl);

        return result;
    }
    //ckeditor使用ckfind实现图片上传

    public List<Note> find(int uid){
        return notedao.findAllByUid(uid);
    }

    public Note findbynid(int nid){
        return notedao.findByNid(nid);
    }

    /**public List<Note> findall(Pageable pageable){
        return notedao.findAll();
    }**/

    /**public Page<Note> getpage(Integer pageNum,Integer pageSize){
        Sort sort=Sort.by(Sort.Direction.DESC,"nid");
        //nid倒序排列
        Pageable pageable= PageRequest.of(pageNum,pageSize,sort);
        Page<Note> notes=notedao.findAll(pageable);
        return notes;
        //分页
    }**/

    public PageInfo<Note> getpage(int uid,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Note> notes= notedao.findAllByUid(uid);
        PageInfo<Note> pageInfo = new PageInfo<>(notes);
       //System.out.println(pageInfo);
        return pageInfo;

    }







}
