package org.sms.project.security;

import java.util.Collection;
import java.util.Objects;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author Sunny
 */
public class SysDecisionManager implements AccessDecisionManager {

  @Override
  public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
      if (Objects.isNull(configAttributes) || configAttributes.size() <= 0) {
          return;
      }
      for (ConfigAttribute allConfigAttribute : configAttributes) {
      final String needPermission = allConfigAttribute.getAttribute().trim();
      for (GrantedAuthority ga : authentication.getAuthorities()) {
        if (needPermission.equals(ga.getAuthority())) {
          return;
        }
      }
    }
    throw new AccessDeniedException("您访问的资源没有权限！！");
  }

  @Override
  public boolean supports(ConfigAttribute configAttribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }
}