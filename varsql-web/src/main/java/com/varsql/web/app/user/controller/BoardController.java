package com.varsql.web.app.user.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.varsql.web.app.user.service.BoardService;
import com.varsql.web.common.controller.AbstractController;
import com.varsql.web.constants.VIEW_PAGE;
import com.varsql.web.constants.VarsqlParamConstants;
import com.varsql.web.dto.board.BoardCommentRequestDTO;
import com.varsql.web.dto.board.BoardRequestDTO;
import com.varsql.web.model.entity.FileBaseEntity;
import com.varsql.web.model.entity.board.BoardFileEntity;
import com.varsql.web.util.FileServiceUtils;
import com.varsql.web.util.VarsqlUtils;
import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.utils.HttpUtils;
import com.vartech.common.utils.VartechUtils;

@Controller
@RequestMapping("/board")
public class BoardController extends AbstractController {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(BoardController.class);

	@Autowired
	private BoardService boardService;

	@RequestMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}", method = RequestMethod.GET)
	public ModelAndView mainpage(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, HttpServletRequest req, ModelAndView mav) throws Exception {

		ModelMap model = mav.getModelMap();
		model.addAttribute("param", HttpUtils.getServletRequestParam(req));

		return getModelAndView("/boardList", VIEW_PAGE.BOARD, model);
	}

	/**
	 *
	 * @Method Name  : list
	 * @Method 설명 : 목록 데이터
	 * @작성자   : ytkim
	 * @작성일   : 2021. 6. 30.
	 * @변경이력  :
	 * @param boardCode
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/{"+VarsqlParamConstants.BOARD_CODE+"}/list")
	public @ResponseBody ResponseResult list(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return boardService.list( boardCode, HttpUtils.getSearchParameter(req));
	}

	/**
	 *
	 * @Method Name  : write
	 * @Method 설명 : 글작성
	 * @작성자   : ytkim
	 * @작성일   : 2021. 6. 30.
	 * @변경이력  :
	 * @param boardCode
	 * @param req
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}/write", method = RequestMethod.GET)
	public  ModelAndView write(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, HttpServletRequest req, ModelAndView mav) throws Exception {

		ModelMap model = mav.getModelMap();
		model.addAttribute("param", HttpUtils.getServletRequestParam(req));
		model.addAttribute("articleInfo", "{}");

		return getModelAndView("/boardWrite", VIEW_PAGE.BOARD, model);
	}

	@RequestMapping(value = "{"+VarsqlParamConstants.BOARD_CODE+"}/save", method = RequestMethod.POST)
	public @ResponseBody ResponseResult save(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, @Valid BoardRequestDTO boardRequestDTO
			, BindingResult result) throws Exception {

		ResponseResult resultObject = new ResponseResult();

		if(result.hasErrors()){

			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("### board save validation check {}",errorVal.toString());
			}
			return VarsqlUtils.getResponseResultValidItem(resultObject, result);
		}

		boardRequestDTO.setBoardCode(boardCode);
		return boardService.saveBoardInfo(boardRequestDTO);
	}

	/**
	 *
	 * @Method Name  : view
	 * @Method 설명 : 글 보기
	 * @작성자   : ytkim
	 * @작성일   : 2021. 7. 2.
	 * @변경이력  :
	 * @param boardCode
	 * @param articleId
	 * @param req
	 * @param mav
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}/view", method = RequestMethod.GET)
	public  ModelAndView view(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, @RequestParam(value = "articleId" , required = true) long articleId
			, HttpServletRequest req, ModelAndView mav) throws Exception {

		ModelMap model = mav.getModelMap();
		model.addAttribute("param", HttpUtils.getServletRequestParam(req));
		model.addAttribute("articleInfo", VartechUtils.objectToJsonString(boardService.viewBoardInfo(boardCode, articleId)));

		return getModelAndView("/boardDetail", VIEW_PAGE.BOARD, model);
	}

	@RequestMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}/delete", method = RequestMethod.DELETE)
	public @ResponseBody ResponseResult delete(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, @RequestParam(value = "articleId" , required = true)  long articleId
			, HttpServletRequest req
			, HttpServletResponse res) throws Exception {
		return boardService.deleteBoardInfo(boardCode, articleId);
	}

	@RequestMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}/modify", method = RequestMethod.GET)
	public  ModelAndView modify(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, @RequestParam(value = "articleId" , required = true)  long articleId
			, HttpServletRequest req
			, ModelAndView mav) throws Exception {
		ModelMap model = mav.getModelMap();
		model.addAttribute("param", HttpUtils.getServletRequestParam(req));
		model.addAttribute("articleInfo", VartechUtils.objectToJsonString(boardService.viewBoardInfo(boardCode, articleId)));

		return getModelAndView("/boardWrite", VIEW_PAGE.BOARD, model);
	}

	@RequestMapping(value = "{"+VarsqlParamConstants.BOARD_CODE+"}/commentSave", method = RequestMethod.POST)
	public @ResponseBody ResponseResult commentSave(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, @RequestParam(value = "articleId" , required = true)  long articleId
			, @Valid BoardCommentRequestDTO boardCommentRequestDTO
			, BindingResult result, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResponseResult resultObject = new ResponseResult();
		if(result.hasErrors()){

			for(ObjectError errorVal :result.getAllErrors()){
				logger.warn("###  comment save validation check {}",errorVal.toString());
			}
			return VarsqlUtils.getResponseResultValidItem(resultObject, result);
		}
		boardCommentRequestDTO.setBoardCode(boardCode);
		boardCommentRequestDTO.setArticleId(articleId);

		return boardService.commentSave(articleId, boardCommentRequestDTO);
	}

	@PostMapping("/{"+VarsqlParamConstants.BOARD_CODE+"}/commentList")
	public @ResponseBody ResponseResult commentList(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			,@RequestParam(value = "articleId" , required = true)  long articleId
			, HttpServletRequest req, HttpServletResponse res) throws Exception {

		return boardService.commentList(boardCode, articleId, HttpUtils.getSearchParameter(req));
	}

	@DeleteMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}/commentDelete")
	public @ResponseBody ResponseResult commentDelete(@PathVariable(required = true, name=VarsqlParamConstants.BOARD_CODE) String boardCode
			, @RequestParam(value = "articleId" , required = true)  long articleId
			,  @RequestParam(value = "commentId" , required = true) long commentId) throws Exception {
		return boardService.commentDelete(articleId, commentId);
	}

	//첨부파일 다운로드
	@RequestMapping(value = "/{"+VarsqlParamConstants.BOARD_CODE+"}/file")
	public void fileDownload(@RequestParam(value = "articleId" , required = true)  long articleId
			, @RequestParam(value = "fileId" , required = true)  String fileId
			, HttpServletRequest req, HttpServletResponse res) throws Exception {
		logger.debug("fileDownload");

		ParamMap param = HttpUtils.getServletRequestParam(req);

		List<BoardFileEntity>  fileList = boardService.findFileList(articleId, Long.parseLong(fileId));

		if(fileList.size() < 1) {
			res.setContentType("text/html");
			res.setStatus(HttpStatus.OK.value());
			try(PrintWriter out = res.getWriter()){
				out.write("<script>alert('file not found')</script>");
			}
			return ;
		}

		String downFileName = "";
		int fileSize = fileList.size();
		if(fileSize == 1) {
			downFileName = fileList.get(0).getFileName();
		}else {
			downFileName = param.getString("downFileName" ,"downloadFile");
			downFileName =java.net.URLDecoder.decode(downFileName,"UTF-8");
			downFileName = downFileName + ".zip";
		}

		FileServiceUtils.fileDownload(req, res, downFileName, fileList.toArray(new FileBaseEntity[0]));
	}

}
