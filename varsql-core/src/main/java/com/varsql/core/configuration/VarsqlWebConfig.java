package com.varsql.core.configuration;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.varsql.core.common.util.ResourceUtils;
import com.varsql.core.configuration.beans.web.PageBean;
import com.varsql.core.configuration.beans.web.SsoBean;
import com.varsql.core.configuration.beans.web.VarsqlWebConfigBean;
import com.varsql.core.exception.ConfigurationException;
import com.varsql.core.exception.ConfigurationLoadException;
import com.vartech.common.utils.VartechUtils;

/**
 *
 * @FileName  : VarsqlWebConfig.java
 * @프로그램 설명 : varsql web config
 * @Date      : 2016. 11. 13.
 * @작성자      : ytkim
 * @변경이력 :
 */
public class VarsqlWebConfig{

	private final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private final String WEB_CONFIG_FILE = "config/varsql-web-config.xml";

	private VarsqlWebConfigBean webConfigBean;

	private static class ConfigurationHolder{
        private static final VarsqlWebConfig instance = new VarsqlWebConfig();
    }

	public static VarsqlWebConfig getInstance() {
		return ConfigurationHolder.instance;
    }

	private VarsqlWebConfig(){
		initialize();
	}

	protected void initialize() {
		try{
			initConfig();
		}catch(Exception e){
			logger.error(this.getClass().getName(), e);
			throw new ConfigurationLoadException(e);
		}
	}

	/**
	 * initialize config file
	 * @throws IOException
	 * @throws ConfigurationException
	 * @Method Name  : initConfig
	 * @Author : ytkim
	 * @Method desc :
	 * @History :
	 */
	private void initConfig() throws IOException {

		logger.debug("default preferences template file path : {} ", WEB_CONFIG_FILE);

		Resource resources = ResourceUtils.getInstallPathResource(WEB_CONFIG_FILE);

		if(resources== null || !resources.exists()) {
			throw new ConfigurationLoadException(String.format("web config file not found : %s" ,WEB_CONFIG_FILE));
		}

		String xml = ResourceUtils.getResourceString(resources);

		webConfigBean = VartechUtils.xmlToBean(xml, VarsqlWebConfigBean.class);

		logger.debug("web config info");
		logger.debug("sso config : {}" , webConfigBean.getSso());
		logger.debug("web page config : {}" , webConfigBean.getPage());
		logger.debug("web config info");

	}

	public SsoBean getSsoConfig() {
		return webConfigBean.getSso();
	}

	public PageBean getPageConfig() {
		return webConfigBean.getPage();
	}
}
