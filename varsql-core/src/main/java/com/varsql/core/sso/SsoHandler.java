package com.varsql.core.sso;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*
* @FileName  : SsoHandler.java
* @프로그램 설명 : sso hanlder
* @Date      : 2019. 11. 26.
* @작성자      : ytkim
* @변경이력 :
*/
public interface SsoHandler {
	// sso 파라미터 확인
	public boolean beforeSsoHandler(HttpServletRequest request,HttpServletResponse response);

	// user id 추출
	public String handler(HttpServletRequest request,HttpServletResponse response);

	// custom 사용자 정보 처리.
	public Map<String,Object> customUserInfo(HttpServletRequest request,HttpServletResponse response, Map userInfo);

	// sso 끝나고 난뒤 처리.
	public boolean afterSsoHandler(HttpServletRequest request,HttpServletResponse response, String ssoId);

	// 시용자 권한
	public List<String> userAuthorities(String userId, HttpServletRequest request, HttpServletResponse response);
}
