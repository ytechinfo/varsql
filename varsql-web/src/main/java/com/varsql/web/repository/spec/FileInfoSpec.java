package com.varsql.web.repository.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.varsql.web.constants.UploadFileType;
import com.varsql.web.model.entity.app.FileInfoEntity;
import com.vartech.common.app.beans.SearchParameter;

//TODO 2022
public class FileInfoSpec extends DefaultSpec {
	public static Specification<FileInfoEntity> fileTypeSearch(UploadFileType fileType, String viewId, SearchParameter param) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			predicates.add(cb.and(cb.equal(root.get(FileInfoEntity.REG_ID), viewId),cb.equal(root.get(FileInfoEntity.FILE_DIV), fileType.getDiv())));
			
			fileNameSearch(predicates, root, cb, param);
			
			query.orderBy(cb.desc(root.get(FileInfoEntity.REG_DT)));
			
			return cb.and(predicates.<Predicate>toArray(new Predicate[0]));
		};
	}

	private static void fileNameSearch(List<Predicate> predicates, Root<?> root, CriteriaBuilder cb,
			SearchParameter param) {
		String keyword = param.getKeyword();
		if (keyword != null && !"".equals(keyword))
			predicates.add(cb.like(root.get(FileInfoEntity.FILE_NAME), contains(keyword)));
	}
}
