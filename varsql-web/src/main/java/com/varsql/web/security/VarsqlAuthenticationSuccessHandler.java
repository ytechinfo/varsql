package com.varsql.web.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.varsql.core.auth.AuthorityType;
import com.varsql.core.auth.User;
import com.varsql.core.common.constants.LocaleConstants;
import com.varsql.core.common.util.CommUtil;
import com.varsql.core.common.util.SecurityUtil;

/**
 *
 * @FileName  : VarsqlAuthenticationSuccessHandler.java
 * @프로그램 설명 : 로그인 성공시
 * @Date      : 2019. 6. 14.
 * @작성자      : ytkim
 * @변경이력 :
 */
@Component
public class VarsqlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(VarsqlAuthenticationSuccessHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Autowired
	@Qualifier("authDao")
	private AuthDAO authDao;
	
	
	@Autowired
	@Qualifier("varsqlRequestCache")
	private RequestCache requestCache;

	public VarsqlAuthenticationSuccessHandler() {
		super();
		super.setUseReferer(true);
	}

	public void onAuthenticationSuccess(final HttpServletRequest request,
			final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {
		
		User userInfo = SecurityUtil.loginUser();
		final String targetUrl = userRedirectTargetUrl(request ,response, userInfo, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to "
					+ targetUrl);
			return;
		}

		authDao.addLog(userInfo , "login", CommUtil.getClientPcInfo(request));
		
		super.clearAuthenticationAttributes(request);
		
		if(userInfo.isLoginRememberMe()) {
			
			SavedRequest savedRequest = requestCache.getRequest(request, response);
		    if(savedRequest != null) {
		    	redirectStrategy.sendRedirect(request, response, savedRequest.getRedirectUrl());
		    }else {
		    	redirectStrategy.sendRedirect(request, response, targetUrl);
		    }
		    return ; 
		}else {
			redirectStrategy.sendRedirect(request, response, targetUrl);
		}
	}

	private String userRedirectTargetUrl(HttpServletRequest request, HttpServletResponse response,User userInfo , final Authentication authentication) {

		AuthorityType topAuthority = userInfo.getTopAuthority();

		List<AuthorityType> userScreen = new ArrayList<AuthorityType>();
		for(AuthorityType auth : AuthorityType.values()){
			if(!AuthorityType.GUEST.equals(auth) &&  topAuthority.getPriority() >=auth.getPriority()){
				userScreen.add(auth);
			}
		}
		String lang = request.getParameter("lang");

		if(lang !=null && !"".equals(lang)){
			Locale userLacle= LocaleConstants.parseLocaleString(lang);
			if( userLacle != null) {
				userInfo.setUserLocale(userLacle);
			}
		}

		request.getSession().setAttribute("var.user.screen", userScreen);

		return userInfo.getTopAuthority().mainPage();
	}
}