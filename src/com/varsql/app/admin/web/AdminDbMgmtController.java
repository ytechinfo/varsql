package com.varsql.app.admin.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.varsql.app.admin.beans.Vtconnection;
import com.varsql.app.admin.beans.VtconnectionOption;
import com.varsql.app.admin.service.AdminServiceImpl;
import com.varsql.app.common.beans.DataCommonVO;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.constants.ResultConst;
import com.vartech.common.utils.HttpUtils;



/**
*-----------------------------------------------------------------------------
* @PROJECT	: varsql
* @NAME		: AdminDbMgmtController.java
* @DESC		: admin db 관리
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
* 2018. 8. 24. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Controller
@RequestMapping("/admin/main")
public class AdminDbMgmtController{

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(AdminDbMgmtController.class);
	
	@Autowired
	AdminServiceImpl adminServiceImpl; 
	
	/**
	 * 
	 * @Method Name  : dblist
	 * @작성일   : 2014. 8. 18. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @Method 설명 : db 목록 보기
	 * @param searchVal
	 * @param page
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dblist")
	public @ResponseBody ResponseResult dblist(HttpServletRequest req) throws Exception {
		SearchParameter searchParameter = HttpUtils.getSearchParameter(req);
		
		return adminServiceImpl.selectDblist(searchParameter);
	}
	
	/**
	 * 
	 * @Method Name  : dbDriver
	 * @Method 설명 : select db driver
	 * @작성자   : ytkim
	 * @작성일   : 2017. 5. 25. 
	 * @변경이력  :
	 * @param dbtype
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbDriver")
	public @ResponseBody ResponseResult dbDriver(@RequestParam(value = "dbtype", required = true)  String dbtype
			
			) throws Exception {
		DataCommonVO paramMap = new DataCommonVO();
		
		paramMap.put("db_type", dbtype);
		
		return adminServiceImpl.selectDbDriverList(paramMap);
	}
	
	/**
	 *
	 * @Method Name  : dbDetail
	 * @작성일   : 2014. 8. 18. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @Method 설명 : db정보 상세보기
	 * @param vconnid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbDetail")
	public @ResponseBody ResponseResult dbDetail(@RequestParam(value = "vconnid") String vconnid) throws Exception {
		
		DataCommonVO dcv = new DataCommonVO();
		
		dcv.put("vconnid", vconnid);
		
		return adminServiceImpl.selectDetailObject(dcv);
	}
	
	/**
	 * 
	 * @Method Name  : dbConnectionCheck
	 * @작성일   : 2014. 8. 18. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @Method 설명 :커넥션 체크
	 * @param vconnid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbConnectionCheck")
	public @ResponseBody ResponseResult dbConnectionCheck(@Valid Vtconnection vtConnection, BindingResult result,HttpServletRequest req) throws Exception {
		ResponseResult resultObject = new ResponseResult();
		if(result.hasErrors()){
			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  AdminController dbConnectionCheck check {}",errorVal.toString());
			}
			resultObject.setResultCode(ResultConst.CODE.ERROR.toInt());
			resultObject.setMessageCode(ResultConst.ERROR_MESSAGE.VALID.toString());
			resultObject.setItemList(result.getAllErrors());
		}else{
			resultObject = adminServiceImpl.connectionCheck(vtConnection);
		}
		
		return resultObject;
	}
	
	/**
	 * 
	 * @Method Name  : dbSave
	 * @작성일   : 2014. 8. 18. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @Method 설명 : 정보 저장
	 * @param vconnid
	 * @param vname
	 * @param vurl
	 * @param vdriver
	 * @param vtype
	 * @param vid
	 * @param vpw
	 * @param vconnopt
	 * @param vpoolopt
	 * @param vquery
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbSave")
	public @ResponseBody ResponseResult dbSave(@Valid Vtconnection vtConnection, BindingResult result,HttpServletRequest req) throws Exception {
		ResponseResult resultObject = new ResponseResult();
		if(result.hasErrors()){
			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  AdminController dbSave {}",errorVal.toString());
			}
			resultObject.setResultCode(ResultConst.CODE.DATA_NOT_VALID.toInt());
			resultObject.setMessageCode(ResultConst.ERROR_MESSAGE.VALID.toString());
			resultObject.setItemList(result.getAllErrors());
		}else{
			resultObject = adminServiceImpl.saveVtconnectionInfo(vtConnection);
		}
		
		return  resultObject;
	}
	
	/**
	 * 
	 * @Method Name  : dbOptSave
	 * @Method 설명 : 옵션정보 저장.
	 * @작성자   : ytkim
	 * @작성일   : 2018. 2. 19. 
	 * @변경이력  :
	 * @param vtconnectionOption
	 * @param result
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbOptSave")
	public @ResponseBody ResponseResult dbOptSave(@Valid VtconnectionOption vtconnectionOption, BindingResult result,HttpServletRequest req) throws Exception {
		ResponseResult resultObject = new ResponseResult();
		if(result.hasErrors()){
			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  AdminController dbOptSave check {}",errorVal.toString());
			}
			resultObject.setResultCode(ResultConst.CODE.ERROR.toInt());
			resultObject.setMessageCode(ResultConst.ERROR_MESSAGE.VALID.toString());
			resultObject.setItemList(result.getAllErrors());
		}else{
			resultObject = adminServiceImpl.saveVtconnectionOptionInfo(vtconnectionOption);
		}
		
		return  resultObject;
	}
	
	
	/**
	 * 
	 * @Method Name  : dbDelete
	 * @작성일   : 2014. 8. 18. 
	 * @작성자   : ytkim
	 * @변경이력  :
	 * @Method 설명 : db 정보 삭제 
	 * @param vconnid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dbDelete")
	public @ResponseBody Map dbDelete(@RequestParam(value = "vconnid")  String vconnid) throws Exception {
		
		DataCommonVO dcv = new DataCommonVO();
		
		dcv.put("vconnid", vconnid);
		
		Map json = new HashMap();
		json.put("result", adminServiceImpl.deleteVtconnectionInfo(dcv));
		return json;
	}
}
