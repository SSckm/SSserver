package org.sms.project.comments.controller;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.sms.SysConstants;
import org.sms.project.base.Result;
import org.sms.project.comments.entity.Comments;
import org.sms.project.comments.service.CommentsService;
import org.sms.project.common.ResultAdd;
import org.sms.project.page.Page;
import org.sms.project.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Sunny
 */
@Controller
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    
//    @ResponseBody
//    @RequestMapping(value = "/list", method = RequestMethod.POST)
//    public Result<Comments> list(Model model, HttpServletRequest request) {
//        String pageNumberStr = request.getParameter("pageNumber");
//        String pageSizeStr = request.getParameter("pageSize");
//        if (Objects.isNull(pageNumberStr) || Objects.isNull(pageSizeStr)) {
//            return null;
//        }
//        Integer pageNumber = Integer.parseInt(pageNumberStr);
//        Integer pageSize = Integer.parseInt(pageSizeStr);
//        Page page = new Page(pageNumber, pageSize);
//        List<Comments> comments = commentsService.queryByCondition(page);
//        page.setRecordCount(0);
//        Result<Comments> res = new Result<Comments>();
//        res.setCode(1);
//        res.setPage(page);
//        res.setList(comments);
//        return res;
//    }
    
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<Comments> list(Model model, HttpServletRequest request) {
        String pageNumberStr = request.getParameter("pageNumber");
        String pageSizeStr = request.getParameter("pageSize");
        String blogIdStr = request.getParameter("blogId");
        if (Objects.isNull(pageNumberStr) || Objects.isNull(pageSizeStr) || Objects.isNull(blogIdStr)) {
            return null;
        }
        Integer pageNumber = Integer.parseInt(pageNumberStr);
        Integer pageSize = Integer.parseInt(pageSizeStr);
        Long blogId = Long.parseLong(blogIdStr);
        if (pageSize >= 20) {
            pageSize = 20;
        }
        Page page = new Page(pageNumber, pageSize);
        List<Comments> comments = commentsService.queryByCondition(page, blogId);
        page.setRecordCount(0);
        Result<Comments> res = new Result<Comments>();
        res.setCode(1);
        res.setPage(page);
        res.setList(comments);
        return res;
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultAdd add(Model model, @Valid Comments comments, HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        return commentsService.insert(comments, cookies);
    }
    
    @ResponseBody
    @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
    public ResultAdd sendEmail(Model model, HttpServletRequest request) {
        String emailAddress = request.getParameter("email");
        ResultAdd res = commentsService.sendEmail(emailAddress);
        return res;
    }
    
    @ResponseBody
    @RequestMapping(value = "/validationEmail", method = RequestMethod.POST)
    public ResultAdd validationEmail(Model model, HttpServletRequest request, HttpServletResponse response) {
        String ticket = request.getParameter(SysConstants.USER_ID_KEY);
        String ip = HttpUtil.getIp(request);
        ResultAdd res = commentsService.validationEmail(ticket, ip, response);
        return res;
    }
}