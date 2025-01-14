<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<div id="<varsql:namespace/>" class="wh100">
	<div class="menu-tools-nav col-xs-3">
		<step-navigation :step.sync="step" :items="navItems" @callback="selectStep" ></step-navigation>
	</div>
	
	<div class="menu-tools-body col-xs-9 scroll-y">
		<div class="process-step" :class="step==1?'active':''">
			<div class="col-xs-12">
				<div class="process-title">내보내기 설정</div>
			</div>
			
			<div style="padding-top: 15px;" class="clearboth">
				<label class="col-xs-3 control-label">File Name</label>
				<div class="col-xs-9 padding0">
					<input type="text" class="form-control text required input-sm" v-model="exportName" value="1000">
				</div>
			</div>
			
			<div style="padding-top: 15px;" class="clearboth">
				<label class="col-xs-3 control-label">Limit Count</label>
				<div class="col-xs-9 padding0">
					<input type="number" class="form-control text required input-sm" v-model="exportCount" value="1000">
				</div>
			</div>
			
			<label class="col-xs-12 control-label">Export Type</label>
			
			<div class="col-xs-6">
				<ul class="process-type-select">
					<li>
						<label class="checkbox-container">INSERT
						  <input type="radio" v-model="exportType" value="sql" checked="checked">
						  <span class="radiomark"></span>
						</label>
						<div class="checkbox-container-desc">SQL 파일로 내보내기</div>
					</li>
					<li>
						<label class="checkbox-container">XML
						  <input type="radio" v-model="exportType" value="xml">
						  <span class="radiomark"></span>
						</label>
						<div class="checkbox-container-desc">XML 파일로 내보내기</div>
					</li>
				</ul>
			</div>
			<div class="col-xs-6">
				<ul>
					<li>
						<label class="checkbox-container">JSON
						  <input type="radio" v-model="exportType" value="json">
						  <span class="radiomark"></span>
						</label>
						<div class="checkbox-container-desc">JSON 파일로 내보내기</div>
					</li>
					<li>
						<label class="checkbox-container">CSV
						  <input type="radio" v-model="exportType" value="csv">
						  <span class="radiomark"></span>
						</label>
						<div class="checkbox-container-desc">CSV 파일로 내보내기</div>
					</li>
				</ul>
			</div>
		</div>
		
		<div class="process-step" :class="step==2?'active':''">
			<div class="col-xs-12">
				<div class="process-title"><spring:message code="msg.export.spec.step2" /></div>

				<c:if test="${schemaInfo ne ''}">
					<div style="padding: 5px 0px 0px;">
						<label class="control-label" style="font-weight: bold;margin-right:5px;"><spring:message code="label.schema" /> : </label>
						<select v-model="selectSchema" @change="getTableList()" style="width: calc(100% - 80px);">
							<c:forEach var="item" items="${schemaList}" begin="0" varStatus="status">
								<option value="${item}">${item}</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				<div class="process-desc" style="padding: 5px 0px 5px;"><spring:message code="msg.table.dbclick.move" /></div>
			</div>
			<div class="wh100-relative table-select-area" style="float: left;">
				<div class="col-xs-12">
					<div class="top-select mbottom-10 fb tl mRight-20"><spring:message code="label.table" /></div>
					<div id="source"  style="height: 200px;width: 100%;"></div>
				</div>
			</div>
		</div>
		
		<div class="process-step" :class="step==3?'active':''">
			<div class="col-xs-12">
				<div class="process-title clearboth">내보내기</div>
				<template v-if="downloadStatus != 'complete'">
					<div id="exportItemEl" style="margin-top: 15px;border: 1px solid #ddd;padding: 10px;height: 200px;width: 100%; overflow:auto;">
						<template v-for="(item,index) in exportItems">
							<div style="height:20px;line-height:20px;"><span>{{item.name}}</span><span class="pull-right">{{item.status?'완료':item.exportCount}}</span></div>
						</template>
					</div>
					
					<button type="button" class="btn-md varsql-btn-info pull-right" style="margin-top:10px;" @click="exportInfo();">내보내기</button>
				</template>
				<template v-else>
					<div style="margin-top: 15px;border: 1px solid #ddd;padding: 10px;height: 200px;width: 100%;">
						다운로드 항목은 <span style="font-weight: bold;">환경설정 -> 파일 </span> 에서 이력을 조회 할수 있습니다. 
					</div>
				</template>
			</div>
		</div>
		
		<step-button :step.sync="step" :end-step="endStep" ref="stepButton" :disable-complete-btn="true"></step-button>
	</div>
</div>


<script>

VarsqlAPP.vueServiceBean({
	el: '#<varsql:namespace/>'
	,data: {
		exportType : 'sql'
		,exportName : 'table-data-export'
		, step : 1
		, endStep : 3
		, downloadStatus : 'start'
		, selectSchema : ''
		, selectTableObj : {}
		, exportCount : 1000
		, userSetting : VARSQL.util.objectMerge({schema:'${schemaInfo}', tables:[]},${userSettingInfo})
		, detailItem :{}
		, navItems :['내보내기 설정','<spring:message code="msg.export.spec.step2" />', VARSQL.messageFormat('complete')]
		, exportItems : []
	}
	,methods:{
		init : function (){
			this.selectSchema = this.userSetting.schema;
			this.setUserConfigInfo();
		}
		,selectStep : function (step){
			this.$refs.stepButton.move(step);
		}
		//step 선택
		,moveStep : function (step){
			this.downloadStatus = '';
			
			this.step = step;
			if(this.step == 3 && this.selectTableObj.getTargetItem().length < 1){
				this.step=2;
    			alert('<spring:message code="msg.table.select" />');
    			return false;
    		}
			
			if(step==3){
				this.getExportItems();
			}
		}
		,getExportItems : function (){
			var _self = this;
			_self.exportItems = [];
			this.selectTableObj.getTargetItem().forEach(function (item){
				_self.exportItems.push({
					name : item.name
					,status : false
					,exportCount : 0
					,column:[]
					,condition : ''
				});
			})
		}
		,exportInfo : function (){
			var _self = this;

			var info = $("#firstConfigForm").serializeJSON();
			
			this.getExportItems();

			var prefVal = {
				requid : VARSQL.generateUUID()
				,fileName : _self.exportName
				,conuid : '${param.conuid}'
				,schema : _self.selectSchema
				,limit: _self.exportCount
				,exportType : _self.exportType
				,items : _self.exportItems
			};
		
			var beforeCurrIdx = 0;	
			function processBar(){
				VARSQL.req.ajax({
					url : {type:VARSQL.uri.database, url:'/tools/export/progressInfo'}
					,data: {
						requid : prefVal.requid
						,type : 'progress'
					}
					,success:function (resData){
						var item = resData.item; 
						
						if(item == 'complete'){
							_self.downloadStatus = 'complete'; 
							for(var i =beforeCurrIdx; i < _self.exportItems.length; i++){
								_self.exportItems[i].status= true;
							}				
						}else{
							
							if(item != null){
								var currIdx = item.itemIdx-1;
								for(var i =beforeCurrIdx;i < item.itemIdx; i++){
									if(i == currIdx){
										_self.exportItems[i].exportCount = item.progressContentLength;
									}else{
										_self.exportItems[i].status= true;
									}
								}	
								beforeCurrIdx = currIdx;
								
								$($('#exportItemEl').children()[currIdx]).attr('tabindex','-1').focus().removeAttr('tabindex');
							}
							
							setTimeout(function() {
								processBar();
							}, 500);
						}
					}
				});
			}
			
			this.downloadStatus = 'start';
			VARSQL.req.download({
				type: 'post'
				,url: {type:VARSQL.uri.database, url:'/tools/export/downloadTableData'}
				,params : {
					prefVal : JSON.stringify(prefVal)
					,schema : _self.selectSchema
					,conuid : '${param.conuid}'
				}
			});
			
			processBar();
			
			/*
			VARSQL.req.ajax({
				url : {type:VARSQL.uri.database, url:'/tools/export/downloadTableData'}
				,data: {
					prefVal : JSON.stringify(prefVal)
					,schema : _self.selectSchema
					,conuid : '${param.conuid}'
				}
				,loadSelector : '.table-select-area'
				,success:function (resData){
					
				}
			});
			*/
			
		}
		,setUserConfigInfo : function (){
			var _self = this;
			
			_self.exportName = _self.userSetting.exportName || _self.exportName;

			_self.setTableSelect();
			_self.getTableList();

		}
		,getTableList : function(){
			var _self = this;

			var param = {
				conuid : '${param.conuid}'
				,schema : this.selectSchema
			}

			VARSQL.req.ajax({
				url : {type:VARSQL.uri.database, url:'/tools/export/specMain/tableList'}
				,data: param
				,loadSelector : '.table-select-area'
				,success:function (resData){
					var list = resData.items;

					_self.selectTableObj.setSourceItem(list);

					var beforeTableList = [];
					if(_self.selectSchema == _self.userSetting.schema){
						var beforeTables = _self.userSetting.tables;
						var beforeTablelen = beforeTables.length;
						if(beforeTablelen > 0){
							var tableNameObj ={};
							for(var i =0; i <beforeTablelen; i++){
								var item = beforeTables[i];
								tableNameObj[item.name] = i;
							}

							for(var i =0,len = list.length; i <len; i++){
								var item = list[i];
								var beforeTableIdx = tableNameObj[item.name];
								if(!VARSQL.isUndefined(beforeTableIdx)){
									beforeTableList.push(beforeTables[beforeTableIdx]);
								}
							}
						}
						_self.selectTableObj.setTargetItem(beforeTableList);
					}else{
						_self.selectTableObj.setTargetItem([]);
					}
				}
			});
		}
		// table select info
		,setTableSelect: function (){
			var _self = this;

			var tmpSourceItem = [] , paramSourceItem=[];

			_self.selectTableObj= $.pubMultiselect('#source', {
				duplicateCheck : true
				,source : {
					idKey : 'name'
					,nameKey : 'name'
					,emptyMessage : '<spring:message code="msg.export.spec.schema.select" />'
					,items : paramSourceItem
				}
				,target : {
					idKey : 'name'
					,nameKey : 'name'
					,items : []
					,click: function (e, sItem){
						console.log(sItem);
						
						_self.detailItem = sItem;
					}
				}
			});
		}
	}
});

</script>