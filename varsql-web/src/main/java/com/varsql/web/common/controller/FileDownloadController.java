package com.varsql.web.common.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.varsql.web.model.entity.app.FileInfoEntity;
import com.varsql.web.repository.user.FileInfoEntityRepository;
import com.varsql.web.util.FileServiceUtils;
import com.vartech.common.app.beans.ParamMap;
import com.vartech.common.utils.HttpUtils;
import com.vartech.common.utils.StringUtils;

@Controller
@RequestMapping("/file")
public class FileDownloadController {
	private final static Logger logger = LoggerFactory.getLogger(FileDownloadController.class);
	
	private FileInfoEntityRepository fileInfoEntityRepository;
	
	public FileDownloadController(FileInfoEntityRepository fileInfoEntityRepository) {
		this.fileInfoEntityRepository = fileInfoEntityRepository; 
	}

	// 첨부파일 다운로드
	@RequestMapping(value = "/download")
	public void fileDownload(@RequestParam(value = "contId") String contId,
			@RequestParam(value = "fileId") String fileId, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		logger.debug("fileDownload");
		
		List<FileInfoEntity> fileList = new ArrayList<>();
		
		if(!StringUtils.isBlank(fileId)) {
			
			FileInfoEntity fie= fileInfoEntityRepository.findByFileId(fileId);
			if(fie != null) {
				fileList.add(fie);
			}
			
		}else if(!StringUtils.isBlank(contId)) {
			fileList = fileInfoEntityRepository.findByFileContId(fileId);
		}

		if (fileList.size() < 1) {
			res.setContentType("text/html");
			res.setStatus(HttpStatus.OK.value());
			try (PrintWriter out = res.getWriter()) {
				out.write("<script>alert('file not found')</script>");
			}
			return;
		}
		
		ParamMap param =HttpUtils.getServletRequestParam(req);

		String downFileName = "";
		int fileSize = fileList.size();
		if (fileSize == 1) {
			downFileName = fileList.get(0).getFileName();
		} else {
			downFileName = param.getString("downFileName", "downloadFile");
			downFileName = java.net.URLDecoder.decode(downFileName, "UTF-8");
			downFileName = downFileName + ".zip";
		}
		FileServiceUtils.fileDownload(req, res, downFileName, fileList.toArray(new FileInfoEntity[0]));
	}
}
