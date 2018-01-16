package org.sms.project.comments.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.opensaml.xml.util.Base64;
import org.sms.SysConstants;
import org.sms.project.comments.dao.CommentsDao;
import org.sms.project.comments.entity.Comments;
import org.sms.project.comments.entity.ResValidationEmail;
import org.sms.project.comments.service.CommentsService;
import org.sms.project.common.ResultAdd;
import org.sms.project.encrypt.des.DESCoder;
import org.sms.project.helper.mail.McMailService;
import org.sms.project.init.SysConfig;
import org.sms.project.page.Page;
import org.sms.project.user.entity.User;
import org.sms.project.user.service.UserService;
import org.sms.project.util.AccountValidatorUtil;
import org.sms.project.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("commentsService")
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentsDao commentsDao;

    @Autowired
    private UserService userService;

    private String[] checkForm(Comments comments, Cookie[] cookies) {
        if (Objects.isNull(comments) || Objects.isNull(cookies)) {
            return null;
        }

        Long blogId = comments.getBlogId();
        String content = comments.getContent();

        if (Objects.isNull(blogId) || Objects.isNull(content)) {
            return null;
        }

        if (comments.getContent().trim() == "") {
            return null;
        }

        String commentsTicket = "";
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (SysConstants.USER_ID_KEY.equals(name.trim())) {
                commentsTicket = cookie.getValue().trim();
                break;
            }
        }

        if ("".equals(commentsTicket)) {
            return null;
        }

        String ticketStr;
        try {
            ticketStr = this.decryptTicket(URLDecoder.decode(commentsTicket, SysConstants.CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (Objects.isNull(ticketStr)) {
            return null;
        }
        return ticketStr.split(",");
    }

    private String encryptTicket(String ticket) {
        byte[] afterCipher = DESCoder.encrypt(SysConstants.CIPHER_KEY, ticket);
        return afterCipher == null ? null : Base64.encodeBytes(afterCipher);
    }

    private String decryptTicket(String ticket) {
        byte[] afterBase = Base64.decode(ticket);
        return afterBase == null ? null : new String(DESCoder.decrypt(SysConstants.CIPHER_KEY, afterBase));
    }

    public ResultAdd insert(Comments comments, Cookie[] cookies) {
        ResultAdd res = new ResultAdd();
        String[] checked = checkForm(comments, cookies);
        if (Objects.isNull(checked)) {
            res.setCode(0);
            res.setError("没有验证邮箱或者评论为空");
            return res;
        }

        String userIdStr = checked[0];
        long userId = Long.valueOf(userIdStr);
        User user = userService.findById(userId);
        comments.setAuthor(user.getName().split("_")[0]);
        comments.setCreateDate(new Date());
        comments.setCreateUserId(user.getId());
        comments.setUsableStatus(0);
        commentsDao.insert(comments);
        res.setCode(1);
        res.setError("评论成功");
        return res;
    }

    public int update(Comments comments) {
        return commentsDao.update(comments);
    }

    public int delete(long id) {
        return commentsDao.delete(id);
    }

    public Comments findById(long id) {
        return commentsDao.findById(id);
    }

    @Override
    public ResultAdd validationEmail(String ticket, String ip, HttpServletResponse response) {
        ResValidationEmail res = new ResValidationEmail();
        if (Objects.isNull(ticket)) {
            res.setCode(0);
            res.setError("验证数据为空");
            return res;
        }

        String decrypt;
        try {
            decrypt = this.decryptTicket(URLDecoder.decode(ticket, SysConstants.CHARSET));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            res.setCode(0);
            res.setError("验证数据错误");
            return res;
        }
        if (Objects.isNull(decrypt)) {
            res.setCode(0);
            res.setError("验证数据错误");
            return res;
        }

        String[] deArray = decrypt.split(",");
        String expireTime = deArray[1];
        long now = System.currentTimeMillis();
        long expireTimeL = Long.parseLong(expireTime);
        if (expireTimeL <= now) {
            res.setCode(0);
            res.setError("验证数据已经过期");
            return res;
        }
        String email = deArray[0];
        User u = userService.findUserByEmail(email);

        String afterCipher = "";
        if (null != u) {
            String cipherStr = u.getId() + "," + u.getName() + u.getEmail();
            afterCipher = this.encryptTicket(cipherStr);
            res.setName(u.getName().split("_")[0]);
        } else {
            List<String> names = SysConfig.INSTANCE.getCommentsNames();
            int random = (int) (Math.random() * names.size());
            if (random == 0) {
                random = 1;
            }
            res.setName(names.get(random - 1) );
            String name = names.get(random - 1) + "_" + email;
            User user = new User();
            user.setCreateDate(new Date());
            user.setEmail(email);
            user.setUsableStatus(1);
            user.setLastLoginDate(new Date());
            user.setLoginType(0);
            user.setPassword(String.valueOf(now));
            user.setLastLoginIp(ip);
            user.setLoginSum(0);
            user.setName(name);
            ResultAdd insertUser = userService.insert(user);
            if (insertUser.getCode() == 0) {
                res.setCode(0);
                res.setError("服务器数据错误，请联系管理员解决！");
                return res;
            }
            String cipherStr = user.getId() + "," + user.getName() + user.getEmail();
            afterCipher = this.encryptTicket(cipherStr);
        }
        
        if (afterCipher == "" || afterCipher == null) {
            res.setCode(0);
            res.setError("数据错误");
            return res;
        }
        // 验证成功后写入Cookie
        try {
            HttpUtil.addCookie(response, SysConstants.USER_ID_KEY, URLEncoder.encode(afterCipher, SysConstants.CHARSET));
            HttpUtil.addCookie(response, SysConstants.COMMENTS_NAME, res.getName() == null ? "" : Base64.encodeBytes(res.getName().getBytes()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            res.setCode(0);
            res.setError("数据错误");
            return res;
        }
        res.setCode(1);
        res.setError("验证成功");
        return res;
    }

    @Override
    public ResultAdd sendEmail(String emailAddress) {
        boolean isEmail = AccountValidatorUtil.isEmail(emailAddress);
        ResultAdd res = new ResultAdd();
        if (!isEmail) {
            res.setCode(0);
            res.setError("邮箱格式不正确");
        }
        long now = System.currentTimeMillis();
        long expireTime = now + 30 * 60 * 1000;
        String cipherStr = emailAddress + "," + expireTime;
        String afterCipher = this.encryptTicket(cipherStr);
        if (Objects.isNull(afterCipher)) {
            res.setCode(0);
            res.setError("数据错误");
            return res;
        }
        McMailService mcMailService = new McMailService();
        mcMailService.setMailFrom(SysConfig.INSTANCE.getCacheDate("mail.host"));
        mcMailService.setMailSmtpHost(SysConfig.INSTANCE.getCacheDate("mail.smtp.host"));
        mcMailService.setSendMailPassword(SysConfig.INSTANCE.getCacheDate("mail.password"));
        mcMailService.setSendMailUser("soaer_service");
        try {
            mcMailService.send(emailAddress, "验证码", URLEncoder.encode(afterCipher, SysConstants.CHARSET), null, null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            res.setCode(0);
            res.setError("数据错误");
            return res;
        }
        res.setCode(1);
        return res;
    }
    
    @Override
    public List<Comments> queryByCondition(Page page, Long blogId) {
        return commentsDao.queryByCondition(page, blogId);
    }

    @Override
    public List<Comments> queryByCondition(Page page) {
        return this.queryByCondition(page);
    }
}