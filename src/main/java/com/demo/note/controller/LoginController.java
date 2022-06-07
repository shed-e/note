package com.demo.note.controller;


import com.demo.note.entity.User;
import com.demo.note.service.Userservice;
import com.sun.xml.txw2.output.ResultFactory;
import net.bytebuddy.asm.Advice;
import org.apache.coyote.OutputBuffer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.type.ReferenceType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;

@Controller
public class LoginController {
    @Autowired
    Userservice userservice;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/user/reg")
    public String reg() {
        return "reg";
    }

    @RequestMapping("/user/doreg")
    public String doreg(HttpServletRequest request, HttpSession session) {
        String uname = request.getParameter("uname");
        String password = request.getParameter("password");
        User user = userservice.find(uname);
        if (user == null) {
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();//默认16位
            int times = 2;
            //hash次数
            String encodepassword = new SimpleHash("md5", password, salt, times).toString();
            //加盐加密
            User newuser = new User();
            newuser.setUsername(uname);
            newuser.setPassword(encodepassword);
            newuser.setSalt(salt);
            userservice.add(newuser);
            String message = "注册成功";
            session.setAttribute("mess", message);
            return "login";
        } else {
            String message = "用户名已存在，请重试";
            session.setAttribute("mess", message);
            return "reg";
        }
    }

    @RequestMapping("/user/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/user/dologin")
    public String dologin(HttpServletRequest request, HttpSession session,Model model) {
        String name = request.getParameter("uname");
        String password = request.getParameter("password");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(name, password);
        try {
            subject.login(usernamePasswordToken);
            session.setAttribute("user", name);
            return "note";
        } catch (AuthenticationException e) {
            model.addAttribute("msg","用户名或密码错误，请重试");
            return "login";
        }

    }

    @RequestMapping("/user/logout")
    public String logout(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        //String message="成功退出";
        return "index";
    }

    @RequestMapping("/user/forget")
    public String forget(){
        return "changepwd";
    }

    @RequestMapping("/user/check")
    public String check(Model model, HttpServletRequest request) {
        String uname = request.getParameter("uname");
        if (null == userservice.find(uname)) {
            model.addAttribute("msg", "用户名不存在");
            return "changepwd";
        } else {
            return "reset";
        }

    }

    @RequestMapping("/user/reset")
    public String reset(Model model ,HttpServletRequest request) {
        String uname = request.getParameter("uname");
        String newpassword = request.getParameter("newpassword");
        String repassword = request.getParameter("repassword");
        //System.out.println(newpassword);
        //System.out.println(repassword);
        if (newpassword.equals(repassword)) {
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            int times = 2;
            String encodepassword = new SimpleHash("md5", newpassword, salt, times).toString();
            //加盐加密
            User newuser = userservice.find(uname);
            newuser.setPassword(encodepassword);
            newuser.setSalt(salt);
            userservice.add(newuser);
            model.addAttribute("msg", "修改成功，请登录");
            return "login";
        } else {
            model.addAttribute("msg", "两个输入的密码不一致，请重新输入");
            return "reset";
        }
    }







}
