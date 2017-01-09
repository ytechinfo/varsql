package com.varsql.web.app.database.setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/preferences")
public class PreferencesController {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(PreferencesController.class);
	
	@Autowired
	private PreferencesServiceImpl preferencesServiceImpl;

	@RequestMapping("/main")
	public ModelAndView main(@RequestParam(value = "vconnid", required = true, defaultValue = "" )  String vconnid, ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();
		return  new ModelAndView("/database/preferences/main",model);
	}
	
	/**
	* @Method	: generalSetting
	* @Method설명	: 일반 설정. 
	* @작성일		: 2017. 1. 9.
	* @AUTHOR	: ytkim
	* @변경이력	: 
	* @param vconnid
	* @param mav
	* @return
	* @throws Exception
	 */
	@RequestMapping("/generalSetting")
	public ModelAndView generalSetting(@RequestParam(value = "vconnid", required = true, defaultValue = "" )  String vconnid, ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();
		return  new ModelAndView("/database/setting/generalSetting",model);
	}
	
	/**
	* @Method	: sqlFormatSetting
	* @Method설명	: sql 자동생성 포켓 설정. 
	* @작성일		: 2017. 1. 9.
	* @AUTHOR	: ytkim
	* @변경이력	: 
	* @param vconnid
	* @param mav
	* @return
	* @throws Exception
	 */
	@RequestMapping("/sqlFormatSetting")
	public ModelAndView sqlFormatSetting(@RequestParam(value = "vconnid", required = true, defaultValue = "" )  String vconnid, ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();
		return  new ModelAndView("/database/setting/sqlFormatSetting",model);
	}
}
