package com.varsql.web.app.user.join;

import org.springframework.stereotype.Repository;

import com.varsql.web.common.vo.DataCommonVO;
import com.varsql.web.dao.BaseDAO;


@Repository
public class JoinDAO extends BaseDAO{
	
	public Object selectUserDetail(DataCommonVO paramMap) {
		return getSqlSession().selectOne("userMapper.selectUserDetail", paramMap);
	}

	public String selectUserMaxVal() {
		return getSqlSession().selectOne("userMapper.selectUserMaxVal");
	}
	
	public int insertUserInfo(DataCommonVO paramMap){
		return getSqlSession().insert("userMapper.insertUserInfo", paramMap );
	}
	
	public int updateUserInfo(DataCommonVO paramMap){
		return getSqlSession().update("userMapper.updateUserInfo", paramMap);
	}

	public int selectIdCheck(DataCommonVO paramMap) {
		return getSqlSession().selectOne("userMapper.selectIdCheck", paramMap);
	}
}
