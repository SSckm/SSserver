package org.sms.project.listener;

import java.net.UnknownHostException;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sms.opensaml.service.impl.SamlServiceImpl;
import org.sms.project.elasticsearch.client.SearchClient;
import org.sms.project.init.SysConfig;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author Sunny
 */
public class SysContextLoaderListener extends ContextLoaderListener {

  private Logger logger = LoggerFactory.getLogger(SamlServiceImpl.class.getName());
  
  public void contextInitialized(ServletContextEvent sce) {
    logger.debug("初始换数据开始");
    SysConfig.INSTANCE.loadSysConfig();
    SysConfig.INSTANCE.loadHtmlTemplate();
    SysConfig.INSTANCE.loadCommentsName();
    SysConfig.INSTANCE.loadElasticsearchConf();
    SysConfig.INSTANCE.loadMailConf();
    try {
		SearchClient.INSTANCE.init();
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
  }
}
