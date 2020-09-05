package com.varsql.web.repository.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.sql.SQLQueryFactory;
import com.varsql.web.dto.user.UserConnectionResponseDTO;
import com.varsql.web.model.entity.db.DBBlockUserEntity;
import com.varsql.web.model.entity.db.DBConnectionEntity;
import com.varsql.web.model.entity.db.QDBBlockUserEntity;
import com.varsql.web.model.entity.db.QDBConnectionEntity;
import com.varsql.web.model.entity.db.QDBGroupEntity;
import com.varsql.web.model.entity.db.QDBGroupMappingDbEntity;
import com.varsql.web.model.entity.db.QDBGroupMappingUserEntity;
import com.varsql.web.model.entity.db.QDBManagerEntity;
import com.varsql.web.repository.DefaultJpaRepository;

@Repository
public interface UserDBConnectionEntityRepository extends DefaultJpaRepository, JpaRepository<DBConnectionEntity, String>, JpaSpecificationExecutor<DBConnectionEntity> ,UserDBConnectionEntityCustom {
	public DBConnectionEntity findByVconnid(String vconnid);



	public class UserDBConnectionEntityCustomImpl extends QuerydslRepositorySupport implements UserDBConnectionEntityCustom {

		@Autowired
	    private SQLQueryFactory queryFactory;

		public UserDBConnectionEntityCustomImpl() {
			super(DBConnectionEntity.class);
		}

		@Override
		public List<UserConnectionResponseDTO> userConnInfo(String viewid) {
			final QDBConnectionEntity conn = QDBConnectionEntity.dBConnectionEntity;
			final QDBGroupMappingDbEntity groupNDb = QDBGroupMappingDbEntity.dBGroupMappingDbEntity;
			final QDBGroupEntity dbGroup = QDBGroupEntity.dBGroupEntity;
			final QDBGroupMappingUserEntity groupNUser = QDBGroupMappingUserEntity.dBGroupMappingUserEntity;

			final QDBBlockUserEntity dbBlockUser = QDBBlockUserEntity.dBBlockUserEntity;

			final QDBManagerEntity manager = QDBManagerEntity.dBManagerEntity;

			List<UserConnectionResponseDTO> reval = new ArrayList<>();

			Set<String> dupChk = new HashSet<>();

			from(conn).innerJoin(manager).on(conn.vconnid.eq(manager.vconnid))
			.select(conn)
			.where(manager.viewid.eq(viewid).and(conn.useYn.eq("Y"))).fetch().forEach(item->{
				dupChk.add(item.getVconnid());
				reval.add(UserConnectionResponseDTO.builder()
					.vconnid(item.getVconnid())
					.vname(item.getVname())
					.manager(true)
					.blockYn(null)
					.build()
				);
			});

			from(conn).innerJoin(groupNDb).on(conn.vconnid.eq(groupNDb.vconnid))
			.innerJoin(dbGroup).on(groupNDb.groupId.eq(dbGroup.groupId))
			.innerJoin(groupNUser).on(groupNDb.groupId.eq(groupNUser.groupId))
			.leftJoin(dbBlockUser).on(conn.vconnid.eq(dbBlockUser.vconnid).and(groupNUser.viewid.eq(dbBlockUser.viewid)))
			.select(conn, dbBlockUser)
			.where(conn.useYn.eq("Y").and(groupNUser.viewid.eq(viewid)))
			.orderBy(conn.vname.asc()).fetch().forEach(item->{
				DBConnectionEntity connInfo= item.get(0, DBConnectionEntity.class);
				DBBlockUserEntity block= item.get(1, DBBlockUserEntity.class);
				String connid = connInfo.getVconnid();
				if(!dupChk.contains(connid)) {
					dupChk.add(connid);
					reval.add(UserConnectionResponseDTO.builder()
						.vconnid(connid)
						.vname(connInfo.getVname())
						.manager(false)
						.blockYn(block==null?null:block.getViewid())
						.build()
					);
				}
			});

			return reval;
		}
	}
}

interface UserDBConnectionEntityCustom {
	List<UserConnectionResponseDTO> userConnInfo(String viewid);

}