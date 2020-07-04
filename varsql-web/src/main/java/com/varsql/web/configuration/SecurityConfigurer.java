package com.varsql.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.varsql.core.auth.AuthorityType;
import com.varsql.core.configuration.VarsqlWebConfig;
import com.varsql.web.security.RememberMeTokenRepository;
import com.varsql.web.security.RememberMeUserService;
import com.varsql.web.security.UserService;
import com.varsql.web.security.VarsqlAccessDeniedHandler;
import com.varsql.web.security.VarsqlAuthenticationFailHandler;
import com.varsql.web.security.VarsqlAuthenticationLogoutHandler;
import com.varsql.web.security.VarsqlAuthenticationLogoutSuccessHandler;
import com.varsql.web.security.VarsqlAuthenticationProvider;
import com.varsql.web.security.VarsqlAuthenticationSuccessHandler;

/**
 * -----------------------------------------------------------------------------
* @fileName		: SecurityConfig.java
* @desc		: security configuration
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 21. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	final static private String REMEMBERME_KEY = "varsqlRememberKey";
	final static private String REMEMBERME_PARAMETER = "varsqlRememberMe";
	final static private String REMEMBERME_COOKIENAME = "varsql-remember-me-ck";

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
	private VarsqlAuthenticationProvider varsqlAuthenticationProvider;

	@Autowired
	private VarsqlAuthenticationSuccessHandler varsqlAuthenticationSuccessHandler;

	@Autowired
	private VarsqlAuthenticationFailHandler varsqlAuthenticationFailHandler;

	@Autowired
	private VarsqlAuthenticationLogoutHandler varsqlAuthenticationLogoutHandler;

	@Autowired
	private VarsqlAuthenticationLogoutSuccessHandler varsqlAuthenticationLogoutSuccessHandler;

	@Autowired
	private UserService userService;

	@Autowired
	private RememberMeTokenRepository rememberMeTokenRepository;

	@Autowired
	private RememberMeUserService rememberMeUserService;

	@Override
    public void configure(WebSecurity web) throws Exception {

		// 404 error 처리 하기위해서 추가.
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedDoubleSlash(true);

        web.ignoring()
            .antMatchers("/webstatic/**","/error/**","/favicon.ico")
         .and().httpFirewall(firewall);

    }

	@Override
    protected void configure(HttpSecurity http) throws Exception {
		configureRememberMe(http);
		configureHttpSecurity(http);
    }

	private void configureHttpSecurity(HttpSecurity http) throws Exception {

		http.headers()
			.frameOptions().sameOrigin().httpStrictTransportSecurity()
			.disable()
		.and()
			.requestCache().requestCache(requestCache())
		.and()
			.csrf()
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.ignoringAntMatchers("/login/**","/logout","/webstatic/**","/error/**","/favicon.ico")
			.requireCsrfProtectionMatcher(new CsrfRequestMatcher())
		.and()
			//.addFilterBefore(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
			.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
		.and() //session
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
			//.maximumSessions(1)	// 중복 로그인 카운트
			.sessionFixation().changeSessionId()	// session 공격시 session id 변경.
		.and() // login
			.formLogin()
	        .loginPage("/login")
	        .loginProcessingUrl("/login_check")
	        .usernameParameter("vsql_login_id")
	        .passwordParameter("vsql_login_password")
	        .successHandler(varsqlAuthenticationSuccessHandler)
	        .failureHandler(varsqlAuthenticationFailHandler)
	        .permitAll()
	    .and() // auth
		    .authorizeRequests()
     		.antMatchers("/admin/**").hasAuthority(AuthorityType.ADMIN.name())
     		.antMatchers("/manage/**").hasAnyAuthority(AuthorityType.ADMIN.name(),AuthorityType.MANAGER.name())
     		.antMatchers("/user/**","/database/**").hasAnyAuthority(AuthorityType.ADMIN.name(),AuthorityType.MANAGER.name(),AuthorityType.USER.name())
     		.antMatchers("/guest/**").hasAuthority(AuthorityType.GUEST.name())
     		.antMatchers("/login","/join/**").anonymous()
     		.antMatchers("/login_check","/index.jsp").permitAll()
     		.antMatchers("/**").authenticated()
     		.anyRequest().authenticated()
     	.and()
     		.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
     	.and() //log out
	     	.logout()
	        .logoutUrl("/logout")
	        .logoutSuccessUrl("/login")
	        .addLogoutHandler(varsqlAuthenticationLogoutHandler)
	        .logoutSuccessHandler(varsqlAuthenticationLogoutSuccessHandler)
	        .invalidateHttpSession(true)
	        .deleteCookies("JSESSIONID" ).permitAll()
	        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

		.and()
			.httpBasic();
	}
	
	@Bean("varsqlRequestCache")
	public RequestCache requestCache() {
	   return new HttpSessionRequestCache();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}

	@Bean("varsqlPasswordEncoder")
    public PasswordEncoder varsqlPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected UserDetailsService userDetailsService() {
    	if(userService==null) {
    		userService = new UserService();
    	}
    	return userService;
    }

    @Bean
    public VarsqlAccessDeniedHandler accessDeniedHandler() {
    	return new VarsqlAccessDeniedHandler(VarsqlWebConfig.newIntance().getPage403());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	 auth.authenticationProvider(varsqlAuthenticationProvider);
    }

    private void configureRememberMe(HttpSecurity http) throws Exception {
        http.rememberMe()
               .key(REMEMBERME_KEY)
               .rememberMeParameter(REMEMBERME_PARAMETER)
               .rememberMeCookieName(REMEMBERME_COOKIENAME)
               .tokenValiditySeconds(60 * 60 * 24 * 7)
               .authenticationSuccessHandler(varsqlAuthenticationSuccessHandler)
               .alwaysRemember(false)
               .tokenRepository(rememberMeTokenRepository)
               .userDetailsService(rememberMeUserService).and();
	}
}
