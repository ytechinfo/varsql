<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<!-- Page Heading -->
<div class="row">
    <div class="col-lg-12">
        <h1 class="page-header"><spring:message code="admin.menu.drivermgmt" text="JDBC Driver 관리"/></h1>
    </div>
    <!-- /.col-lg-12 -->
</div>
<div class="row display-off" id="varsqlVueArea">
	<div class="col-xs-5">
		<div class="panel panel-default">
			<!-- /.panel-heading -->
			<div class="panel-body">
				<div class="pull-right search-area">
					<div class="dataTables_filter">
						<div class="input-group floatright">
							<input type="text" v-model="searchVal" class="form-control " @keyup.enter="search()" autofocus="autofocus" placeholder="Search...">
							<span class="input-group-btn">
								<button class="btn btn-default" @click="search()" type="button">
									<span class="glyphicon glyphicon-search"></span>
								</button>
							</span>
						</div>
					</div>
				</div>
				<div class="table-responsive">
					<div id="dataTables-example_wrapper"
						class="dataTables_wrapper form-inline" role="grid">
						<table
							class="table table-striped table-bordered table-hover dataTable no-footer"
							id="dataTables-example" style="table-layout:fixed;">
							<colgroup>
								<col style="width:70px;">
								<col style="width:120px">
								<col style="width:*;">
								<col style="width:80px;">
							</colgroup>
							<thead>
								<tr role="row">
									<th class="text-center"><spring:message	code="admin.drivermgmt.db_type" /></th>
									<th class="text-center"><spring:message	code="admin.drivermgmt.provider_name" /></th>
									<th class="text-center"><spring:message	code="admin.drivermgmt.driver_class" /></th>
									<th class="text-center"><spring:message	code="admin.drivermgmt.path_type" /></th>
								</tr>
							</thead>

							<tbody class="dataTableContent">
								<tr v-for="(item,index) in gridData" class="gradeA cursor-pointer" :class="(index%2==0?'add':'even')" @click="itemView(item)">
									<td :title="item.dbType"><div class="text-ellipsis ellipsis0">{{item.dbType}}</div></td>
									<td :title="item.providerName"><div class="text-ellipsis ellipsis0">{{item.providerName}}</div></td>
									<td :title="item.driverClass"><div class="text-ellipsis ellipsis0">{{item.driverClass}}</div></td>
									<td :title="item.pathType"><div class="text-ellipsis ellipsis0">{{item.pathType}}</div></td>
								</tr>
								<tr v-if="gridData.length === 0">
									<td colspan="4"><div class="text-center"><spring:message code="msg.nodata"/></div></td>
								</tr>
							</tbody>
						</table>

						<page-navigation :page-info="pageInfo" callback="search"></page-navigation>
					</div>
				</div>
			</div>
			<!-- /.panel-body -->
		</div>
		<!-- /.panel -->
	</div>
	<!-- /.col-lg-4 -->
	<div class="col-xs-7" >
		<div class="panel panel-default detail_area_wrapper" >
			<div class="panel-heading"><spring:message code="admin.form.header" /><span id="selectDbInfo" style="margin-left:10px;font-weight:bold;">{{detailItem.providerName}}</span></div>
			
			<!-- /.panel-heading -->
			<div class="panel-body">
				<div class="form-group" style="height: 34px;margin-bottom:10px;">
					<div class="col-sm-12">
						<div class="pull-right">
							<button type="button" class="btn btn-default" @click="setDetailItem()"><spring:message code="btn.add"/></button>
							<button type="button" class="btn btn-default" @click="save()"><spring:message code="btn.save"/></button>

							<template v-if="detailFlag===true">
								<button type="button" class="btn btn-primary" @click="driverCheck()"><spring:message code="btn.driverclass.check"/></button>
								<button type="button" class="btn btn-danger"  @click="deleteInfo()"><spring:message code="btn.delete"/></button>
							</template>
						</div>
					</div>
				</div>

				<form id="addForm" name="addForm" class="form-horizontal" onsubmit="return false;">
					<div class="view-area" data-view-mode="view" :class="viewMode=='view'?'on':''">
						<div class="form-group" :class="errors.has('NAME') ? 'has-error' :''">
							<label class="col-sm-4 control-label"><spring:message code="admin.drivermgmt.provider_name" /></label>
							<div class="col-sm-8">
								<input type="text" v-model="detailItem.providerName" v-validate="'required'" name="NAME" class="form-control" />
								<div v-if="errors.has('NAME')" class="help-block">{{ errors.first('NAME') }}</div>
							</div>
						</div>
						
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="admin.drivermgmt.db_type" /></label>
							<div class="col-sm-8">
								<select class="form-control text required" v-model="detailItem.dbType" @change="loadDriverList(detailItem.dbType)">
									<option value="select">SELECT</option>
									<c:forEach items="${dbtype}" var="tmpInfo" varStatus="status">
										<option value="${tmpInfo.urlprefix}" i18n="${tmpInfo.langkey}">${tmpInfo.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group" :class="errors.has('driverclass') ? 'has-error' :''">
							<label class="col-sm-4 control-label"><spring:message code="admin.drivermgmt.driver_class" /></label>
							<div class="col-sm-8">
								<div><input type="checkbox" id="directYn" v-model="detailItem.directYn" true-value="Y" false-value="N" /><label for="directYn"><spring:message code="label.direct.input" /></label></div>
								<template v-if="detailItem.directYn != 'Y'">
									<select class="form-control text required" name="driverId" @change="changeDriverInfo($event)" v-model="detailItem.driverId">
										<option v-for="(item,index) in driverList" :value="item.driverId" :data-driver="item.dbdriver" selected="{{detailItem.driverId==item.driverId?true:(detailItem.driverId==''&& index==0?true:false)}}">
											{{item.driverDesc}}({{item.dbdriver}})
										</option>
									</select>
									<input type="hidden" v-model="detailItem.driverClass" v-validate="'required'" name="driverclass" class="form-control" />
								</template>
								<template v-else>
									<input type="text" v-model="detailItem.driverClass" v-validate="'required'" name="driverclass" class="form-control" />
									<div v-if="errors.has('driverclass')" class="help-block">{{ errors.first('driverclass') }}</div>
								</template>
							</div>
						</div>
						
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="admin.drivermgmt.driver" /></label>
							<div class="col-sm-8">
								<div>
									<label><input type="radio" name="pathtype" value="file" v-model="detailItem.pathType" checked>File Upload</label>
									<label><input type="radio" name="pathtype" value="path" v-model="detailItem.pathType" ><spring:message code="label.direct.input" /></label>
								</div>
								
								<div v-if="detailItem.pathType == 'file'">
									<div id="fileDropArea" class="add-file-upload-area" style="width: 100%;height: 36px;border: 1px solid #ccc;text-align: center;vertical-align: middle;line-height: 36px;cursor:pointer;">파일을 첨부 해주세요.</div>
									<div id="fileUploadPreview" class="file-upload-area"></div>
									<div><spring:message code="admin.drivermgmt.file.desc" /></div>
								</div>
								<div v-else>
									<input type="text" v-model="detailItem.driverPath" class="form-control" />
									<div><spring:message code="admin.drivermgmt.driver_path.desc" /></div>
								</div>
							</div>
						</div>
						
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="admin.drivermgmt.validation_query" /></label>
							<div class="col-sm-8">
								<input type="text" v-model="detailItem.validationQuery" class="form-control" />
							</div>
						</div>
						
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="admin.drivermgmt.driver_desc" /></label>
							<div class="col-sm-8">
								<textarea v-model="detailItem.driverDesc" rows="3" class="form-control" /></textarea>
							</div>
						</div>
					</div>
				</form>
			</div>
			<!-- /.panel-body -->
		</div>
		<!-- /.panel -->
	</div>
	<!-- /.col-lg-8 -->

</div>
<!-- /.row -->

<script>
VARSQL.loadResource(['fileupload']);
VarsqlAPP.vueServiceBean( {
	el: '#varsqlVueArea'
	,validateCheck : true
	,data: {
		list_count :10
		,detailFlag :false
		,searchVal : ''
		,pageInfo : {}
		,gridData :  []
		,detailItem :{}
		,driverList : []
		,viewMode : 'view'
		,fileUploadObj : {}
	}
	,watch :{
		'detailItem.pathType' : function (newVal, oldVal){
			
			if(newVal == 'path') return ;
			
			var _this = this; 
			
			_this.setFileInfo();
		}
	}
	,methods:{
		init : function(){
			this.setDetailItem('init');
		}
		,search : function(no){
			var _this = this;

			var param = {
				pageNo: (no?no:1)
				,rows: _this.list_count
				,'searchVal':_this.searchVal
			};

			this.$ajax({
				url : {type:VARSQL.uri.admin, url:'/driverMgmt/list'}
				,data : param
				,success: function(resData) {
					_this.gridData = resData.items;
					_this.pageInfo = resData.page;
				}
			})
		}
		,itemViewMode : function (mode){
			this.viewArea(mode);
		}
		,changeDriverInfo : function (evt){
			
			var selVal = evt ? evt.target.value : this.detailItem.driverId; 
			
			var selItem= {};
			for(var i =0 ;i< this.driverList.length;i++){
				var item = this.driverList[i];
				if(item.driverId == selVal){
					selItem = item; 
					break; 
				}
			}
			if(VARSQL.isBlank(this.detailItem.driverClass)){
				this.detailItem.driverClass = item.dbdriver;
			}
			
			if(VARSQL.isBlank(this.detailItem.validationQuery)){
				this.detailItem.validationQuery = item.validationQuery;
			}
		}
		// 상세보기
		,itemView : function(item){
			var _this = this;
			
			if(_this.detailItem.driverProviderId == item.driverProviderId){
				return ;
			}

			var param = {
				driverProviderId : item.driverProviderId
			}

			_this.errors.clear();

			this.$ajax({
				url : {type:VARSQL.uri.admin, url:'/driverMgmt/detail'}
				,data : param
				,loadSelector : '.detail_area_wrapper'
				,success: function(resData) {
					var item  =resData.item;

					if(item.dbType != _this.detailItem.dbType){
						_this.loadDriverList(item.dbType);
					}

					_this.setDetailItem(item);
				}
			})
		}
		,setDetailItem : function (item){

			if(item =='init' || VARSQL.isUndefined(item)){
				this.$validator.reset()
				this.viewMode = 'view';
				this.detailFlag = false;
				this.detailItem ={
					driverProviderId :''
					,driverId :''
					,providerName :''
					,dbType :''
					,directYn :'N'
					,driverClass :''
					,pathType :'file'
					,validationQuery :''
					,driverDesc :''
					,driverPath :''
					,filePath1 :''
					,filePath2 :''
					,filePath3 :''
					,filePath4 :''
					,filePath5 :''
					,fileList :[]
					,removeFiles : [] 
				}
			}else{
				this.detailFlag = true;
				item.removeFiles =[];
				this.detailItem = item;
			}
			
			var _this = this; 
			
			if(item !='init' && this.detailItem.pathType=='file'){
				_this.setFileInfo();
			}
			
		}
		,setFileInfo : function (){
			
			this.$nextTick(function (){
				var files = [];
				this.detailItem.fileList.forEach(function (item){
					files.push({
						name : item.fileName
						,size : item.fileSize
						,fileId : item.fileId
					})
				})
				
				var _this =this; 
				
				_this.fileUploadObj = VARSQLUI.file.create('#fileDropArea',{
					files: files
					,options : {
						url : VARSQL.url(VARSQL.uri.admin, '/driverMgmt/save')
						,params : {
							div : 'driver'
							, fileExtensions : ''
						}
						,previewsContainer :'#fileUploadPreview'
						,maxFiles :5
						,clickable : '.add-file-upload-area'
					}
					,autoDiscover: false
					,btnEnabled :false
					,extensions :'jar'
					,useDownloadBtn : true
					,duplicateIgnore : true
					,callback : {
						complete : function (file, resp){
							_this.search();
							_this.setDetailItem();
						}
						,removeFile : function (file){
							_this.detailItem.removeFiles.push(file.fileId);
						}
						,download : function (file){
							VARSQL.req.download({
								url : VARSQL.util.replaceParamUrl('<varsql:url type="fileDownload" />', file)
							});
						}
					}
					
				}, true);
			})
		}
		,save : function (mode){
			var _this = this;

			this.$validator.validateAll().then(function (result){
				if(result){
					var item = _this.getParamVal(); 
					item.removeFileIds = _this.detailItem.removeFiles.join(',');
					
					_this.fileUploadObj.save(item);
				}
			});
		}
		,getParamVal : function (item){
			return VARSQL.util.objectMerge({}, this.detailItem);
		}
		// 정보 삭제 .
		,deleteInfo : function (){
			var _this = this;

			if(typeof this.detailItem.driverProviderId ==='undefined'){
				VARSQLUI.toast.open(VARSQL.messageFormat('varsql.0004'));
				return ;
			}

			if(!confirm(VARSQL.messageFormat('varsql.0016'))){
				return ;
			}

			this.$ajax({
				url : {type:VARSQL.uri.admin, url:'/driverMgmt/delete'}
				,data: {
					driverProviderId : _this.detailItem.driverProviderId
				}
				,success:function (resData){
					_this.setDetailItem();
					_this.search();
				}
			});
		}
		// edit element, options 처리.
		,viewArea :function (mode){
			this.viewMode = mode;
		}
		,driverCheck : function (){
			var param = this.getParamVal();
			
			if(this.fileUploadObj.getAddFiles().length > 0){
				if(!confirm("driver file 추가시 정보 저장후 체크해주세요.")){
					return ; 
				}
			}
			
			this.$ajax({
				url : {type:VARSQL.uri.admin, url:'/driverMgmt/driverCheck'}
				,loadSelector : 'body'
				,data:param
				,success:function (resData){
					if(VARSQL.req.validationCheck(resData)){
						if(resData.resultCode == 200){
							VARSQLUI.toast.open(VARSQL.messageFormat('success'));
							return
						}else{
							alert(resData.messageCode  +'\n'+ resData.message);
						}
					}
				}
			});
		}
		// db driver list
		,loadDriverList : function (val){
			var _this = this;
			var param = {
				dbtype :val
			};

			this.$ajax({
				url : {type:VARSQL.uri.admin, url:'/driverMgmt/driverList'}
				,data : param
				,success:function (resData){

					var result = resData.items;
		    		var resultLen = result.length;

		    		if(resultLen==0){
		    			_this.driverList = [];
		    			return ;
		    		}
		    		
		    		_this.driverList = result;
					_this.detailItem.driverId = resData.items[0].driverId;
					_this.changeDriverInfo();

		    		return ;
				}
			});
		}
	}
});
</script>
