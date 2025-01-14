package com.varsql.web.tags;

import javax.servlet.http.HttpServletRequest;

import com.varsql.web.constants.WebStaticResourceVersion;
import com.varsql.web.util.VarsqlUtils;
import com.vartech.common.utils.DateUtils;
import com.vartech.common.utils.VartechUtils;

/**
 * -----------------------------------------------------------------------------
* @fileName		: VarsqlFn.java
* @desc		: varsql custom tag function
* @author	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 4. 24. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
public final class VarsqlFn{

	public static String objectToJson(Object json) {
		return VartechUtils.objectToJsonString(json);
	}

	public static String currentDate(String foramt) {
		return DateUtils.getCurrentDate(foramt);
	}

	public static boolean isRuntimelocal(String foramt) {
		return VarsqlUtils.isRuntimelocal();
	}

	public static long randomVal(Integer val) {
		return java.lang.Math.round(java.lang.Math.random() * val);
	}

	public static String pubJsVersion() {
		if(VarsqlUtils.isRuntimelocal()) {
			return randomVal(10000)+"";
		}else {
			return WebStaticResourceVersion.PUB_JS;
		}
	}

	public static String staticResourceVersion(String type) {
		if("codemirror".equals(type)) {
			return WebStaticResourceVersion.CODE_MIRROR;
		}else if("css".equals(type)) {
			return WebStaticResourceVersion.VARSQL_CSS;
		}else if("prettify".equals(type)) {
			return WebStaticResourceVersion.PRETTIFY;
		}else {
			if(VarsqlUtils.isRuntimelocal()) {
				return randomVal(10000)+"";
			}else {
				return WebStaticResourceVersion.STATIC_RESOURCE;
			}
		}
	}

	public static String contextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	public static String loginUrl(HttpServletRequest request) {
		return request.getContextPath()+"/login_check";
	}

	public static String logoutUrl(HttpServletRequest request) {
		return request.getContextPath()+"/logout";
	}
}
