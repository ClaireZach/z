<!-- 系统升级首页 -->
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	$(function(){   
		$("#upgradeForm .add").click(function(){     
			var form = $(this).parents('form');
			var filename= $("#upgradeForm #fileToUpload").val().trim();
			if(filename=='' || filename=='未选择文件'){
				alertMsg.warn('请选择文件！');
				return;
			}
			filename=filename.substring(filename.lastIndexOf('\\')+1);
			var filelist=['BS消费机.hex','BS水控机.hex','BS中转通信器.hex','BS门禁机.hex','BS考勤门禁机.hex','BS中转消费机.hex','BS中转水控机.hex','BS中转门禁机.hex','BS中转考勤门禁机.hex','BS读卡机.hex'];
			var valid=0;
			for(var i=0;i<filelist.length;i++){
				if(filename==filelist[i]){
					valid=1;
					break;
				}
			}
			if(valid==0){
				alertMsg.warn('文件名不合法！');
				return;				
			}
			form.submit();
		}) 
	})
	function show(e){
		if(e==0){
			alertMsg.warn('文件内容为空！');			
		}else if(e==1){
			refreshList();
		}
	}
	function refreshList(){
		$('#upgradeList').loadUrl('${base}/list.do',{},function(){
			$('#upgradeList').find('[layoutH]').layoutH();
		});
	}
</script>
<style type="text/css">

</style>

<div style="background: #fff;">
	<div id="upgradeList" layoutH="150">
		<jsp:include page="${base}/list.do"/>
	</div>
	<div class="form upgradeForm">
		<form id="upgradeForm" method="post" action="${base }/add.do" target="upload" enctype="multipart/form-data" class="pageForm required-validate">
			<div class="pageFormContent" style="overflow: auto;width: 1200px;">
				<dl style="width: 1024px;height: 30px;">
					<dt style="width: 820px;">说明：上传文件只能是以下文件名（BS消费机.hex，BS水控机.hex，BS中转通信器.hex，BS门禁机.hex，BS考勤门禁机.hex，BS中转消费机.hex，BS中转水控机.hex，BS中转门禁机.hex，BS中转考勤门禁机.hex，BS读卡机.hex）</dt>
					<dd style="width: 150px;">
						<input id="fileToUpload" type="file" size="20" name="file" class="input">
					</dd>
				</dl>
			</div>
			<div class="formBar">
				<div class="panelBar" style="border-style: none;">
					<ul class="toolBar">
						<li><a class="add" href="javascript:;"><span>上传</span></a></li>
					</ul>
				</div>
			</div>
		</form>
	</div>
</div>
<iframe name="upload" style="display: none;"></iframe>