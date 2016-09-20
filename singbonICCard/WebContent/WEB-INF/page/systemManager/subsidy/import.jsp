<!-- 信息导入 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	$(function(){   
		$("#importForm .import").click(function(){     
			var form = $('#importForm');
			var filename= $("#fileToUpload", form).val();
			if(filename=='' || filename=='未选择文件'){
				alertMsg.warn('请选择文件！');
				return;
			}
			if(filename.indexOf('.xls')==-1 || filename.length-4!=filename.indexOf('.xls')){
				alertMsg.warn('只能导入xls格式文件！');
				return;
			}
			$.post('${base}/selectStatus.do', function(e) {
				var e2 = eval('(' + e + ')');
				if (e2.subsidyVersion!=0) {
					alertMsg.confirm('当前已有补助生成，是否继续导入补助金额？', {
						okCall : function() {
							$('#importForm input[name=subsidyVersion]').val(e2.subsidyVersion);
							$('#importForm input[name=invalidDate]').val(e2.invalidDate);
							$('#importForm input[name=status]').val(1);
							form.submit();
						}
					});
				}else {
					form.submit();
				}
			});
		}) 
	})
	
	function show(e,err){
		if(e==1){
			searchSubsidyUserList();
			alertMsg.correct('导入完成');			
		}else{
			alertMsg.warn(err);						
		}
	}
</script>
<style type="text/css">
	.dialog .dialogContent{
		padding: 1px;
	}
	.dialog .pageFormContent{
		border-style: none;
	}
	#cardReader #result div{
		color: #15428b;
		font-size: 13px;
		font-weight: bold;
		margin: 0 0 15px 60px;
	}
	#cardReader #result img{
		float: right;
		display: none;
	}
</style>
<div id="importUser">
	<form id="importForm" method="post" action="${base }/doImport.do" target="upload" enctype="multipart/form-data" class="pageForm required-validate">
		<input type="hidden" name="subsidyVersion" value="0"/>
		<input type="hidden" name="invalidDate" value=""/>
		<input type="hidden" name="status" value="0"/>
		<div style="margin: 10px 0 20px 10px;">
			<div style="margin-bottom: 30px;">
				<a href="/补助信息导入模板.xls">模板下载<a>
			</div>
			<input id="fileToUpload" type="file" size="20" name="file" class="input">
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent import">
							<button type="button">导入</button>
						</div>
					</div></li>
			</ul>
		</div>
	</form>
</div>
<iframe name="upload" style="display: none;"></iframe>