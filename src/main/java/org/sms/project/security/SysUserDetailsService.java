package org.sms.project.security;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.sms.project.role.service.RoleService;
import org.sms.project.user.entity.User;
import org.sms.project.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Sunny
 */
public class SysUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService sysUserService;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = sysUserService.findUserByEmail(email);
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户不存在" + email);
        }
        this.buildAuths(user);
        return user;
    }

    private void buildAuths(User user) {
        Set<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        List<String> roles = roleService.getRoleNameById(user.getId());
        if (Objects.isNull(roles)) {
            return;
        }
        roles.forEach(role -> {
            auths.add(new SimpleGrantedAuthority(role));
        });
        user.setAuthorities(auths);
    }
}