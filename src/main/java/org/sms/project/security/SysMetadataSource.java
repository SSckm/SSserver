package org.sms.project.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import org.sms.project.resource.entity.ResourceMapping;
import org.sms.project.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

/**
 * 资源角色管理器
 * 
 * @author Sunny
 */
@Service("sysMetadataSource")
public class SysMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private ResourceService resourceService;

    private Map<String, List<ConfigAttribute>> configAttributes;

    private RequestMatcher requestMatcher = null;

    public SysMetadataSource() {
        super();
    }

    public final ResourceService getResourceService() {
        return resourceService;
    }

    public final void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        if (null == configAttributes) {
            this.load();
        }
        for (Map.Entry<String, List<ConfigAttribute>> entry : configAttributes.entrySet()) {
            requestMatcher = new AntPathRequestMatcher(entry.getKey());
            if (requestMatcher.matches(request)) {
                return configAttributes.get(entry.getKey());
            }
        }
        return null;
    }

    /**
     * 加载所有的url访问时需要的角色
     */
    private void load() {
        List<ResourceMapping> resourceMappings = resourceService.getResourceMappings();
        this.configAttributes = new HashMap<String, List<ConfigAttribute>>();
        if (Objects.isNull(resourceMappings) || resourceMappings.size() <= 0) {
            return;
        }

        resourceMappings.forEach(resourceMapping -> {
            final String url = resourceMapping.getUrl();
            final String role_name = resourceMapping.getName();
            if (null != url && role_name != null) {
                ConfigAttribute configAttribute = new SecurityConfig(role_name);
                final List<ConfigAttribute> isExit = configAttributes.get(url);
                if (isExit == null) {
                    List<ConfigAttribute> list = new ArrayList<ConfigAttribute>();
                    list.add(configAttribute);
                    configAttributes.put(url, list);
                } else if (!isExit.contains(configAttribute)) {
                    isExit.add(configAttribute);
                }
            }
        });
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }
}