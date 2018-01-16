package org.sms.project.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PublicKey;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.xml.util.Base64;
import org.sms.SysConstants;
import org.sms.project.encrypt.rsa.RSACoder;
import org.sms.project.helper.CertificateHelper;
import org.sms.project.helper.SessionHelper;
import org.sms.project.helper.TicketHelper;
import org.sms.project.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * @author Sunny
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
  
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//    final Artifact idpArtifact = samlService.buildArtifact();
//    Object url = session.getAttribute(SysConstants.ACTION_KEY);
//    if (url == null) {
//      url = SysConstants.DEFAULT_SP_RECEIVESPARTIFACT_URL;
//    }
//    final Response samlResponse = samlService.buildResponse(UUIDFactory.INSTANCE.getUUID());
    User user = (User) SessionHelper.get(request, SysConstants.LOGIN_USER);
//    samlService.addAttribute(samlResponse, user);
//    SSOHelper.INSTANCE.put(idpArtifact.getArtifact(), samlResponse);
    
    /**
     * 制作Ticket
     */
    String ticket = TicketHelper.getTicket(user);
    /**
     * 验证成功以后需要向客户端记录Cookie
     */
    String cookieTicket = encryptTicket(ticket);
    this.addSSOCookie(response, cookieTicket);
    PrintWriter write = response.getWriter();
    response.setCharacterEncoding("utf-8");
    response.setContentType("application/json;charset=utf-8");
    write.println("{\"code\":1}");
    write.flush();
    write.close();
  }
  
  private String encryptTicket(String ticket) {
    PublicKey publicKey = CertificateHelper.getRSAPublicKey();
    try {
      byte[] encryptArray = RSACoder.INSTANCE.encryptByPublicKey(publicKey, ticket.getBytes());
      return Base64.encodeBytes(encryptArray);
    } catch (Exception e) {
      throw new RuntimeException("加密数据错误");
    }
  }
  
  private void addSSOCookie(HttpServletResponse response, String string) throws UnsupportedEncodingException {
    Cookie cookie = new Cookie(SysConstants.IDP_TICKET, URLEncoder.encode(string, SysConstants.CHARSET));
    cookie.setDomain(SysConstants.DOMAIN);
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60);
    response.addCookie(cookie);
  }
}