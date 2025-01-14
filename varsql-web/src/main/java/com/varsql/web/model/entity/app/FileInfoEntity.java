package com.varsql.web.model.entity.app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.varsql.web.model.entity.FileBaseEntity;
import com.varsql.web.model.entity.db.DBTypeDriverProviderEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = "VTFILE")
public class FileInfoEntity extends FileBaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String _TB_NAME = "VTFILE";

	@Id
	@Column(name = "FILE_ID")
	private String fileId;

	@Column(name = "FILE_DIV")
	private String fileDiv;

	@Column(name = "FILE_CONT_ID")
	private String fileContId;

	@Column(name = "CONT_GROUP_ID")
	private String contGroupId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILE_CONT_ID", referencedColumnName = "DRIVER_PROVIDER_ID", insertable = false, updatable = false)
	private DBTypeDriverProviderEntity driverProviderFiles;

	@Builder
	public FileInfoEntity(String fileId, String fileDiv, String fileContId, String fileFieldName, String fileName,
			String filePath, String fileExt, long fileSize, String contGroupId) {
		setFileId(fileId);
		setFileDiv(fileDiv);
		setFileContId(fileContId);
		setFileFieldName(fileFieldName);
		setFileName(fileName);
		setFilePath(filePath);
		setFileExt(fileExt);
		setFileSize(fileSize);
		setContGroupId(contGroupId);
	}

	public static final String FILE_ID = "fileId";

	public static final String FILE_DIV = "fileDiv";

	public static final String FILE_CONT_ID = "fileContId";
	
	public static final String CONT_GROUP_ID = "contGroupId";

	public static final String FILE_FIELD_NAME = "fileFieldName";

	public static final String FILE_SIZE = "fileSize";
}
