<!-- 系统用户管理 -->
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
	$(function() {
		$('#smartSchoolAdminForm .add').click(function() {
			$('#smartSchoolAdminForm input').eq(0).val('');
			var form = $(this).parents('form');
			validateCallback(form, function(e) {
				if (e == 1) {
					alertMsg.correct('添加成功！');					
					form.clearForm();
					refreshsmartSchoolAdminList();
				} else if(e==2){
					alertMsg.error('已存在该登录名！');
				} else {
					alertMsg.error('添加失败！');
				}
			}, null);
		});
	
		$('#smartSchoolAdminForm .edit').click(function() {
			var form = $(this).parents('form');
			validateCallback(form, function(e) {
				if (e == 1) {
					alertMsg.correct('修改成功！');
					form.clearForm();
					refreshsmartSchoolAdminList();
				} else if(e==2){
					alertMsg.error('已存在该登录名！');
				} else {
					alertMsg.error('修改失败！');
				}
			}, null);
		});
		$('#smartSchoolAdminForm .delete').click(function(){
			var form = $(this).parents('form');
			var id = $('#smartSchoolAdminForm input[name=id]').val();
			if(id==''){
				alertMsg.error('请选择用户！');				
			}else{
				var loginName = $('#smartSchoolAdminForm input[name=loginName]').val();
				if(loginName=='admin'){
					alertMsg.warn('系统用户不允许删除！');
					return;
				}
				alertMsg.confirm('确定要删除'+loginName+'吗？', {
					okCall : function() {
						$.post('${base }/deleteAdmin.do?id=' + id,function(e){
							if (e == 1) {
								alertMsg.correct('删除成功！');
								form.clearForm();
								refreshsmartSchoolAdminList();
							} else {
								alertMsg.warn('删除失败！');
							} 
						});
					}
				});
			}
		});
	});
	
	function refreshsmartSchoolAdminList() {
		$('#smartSchoolAdminList').loadUrl('${base}/adminList.do',{},function(){
			$('#smartSchoolAdminList').find('[layoutH]').layoutH();
		});
	}
	
	//选择用户
	function smartSchoolAdminClick(tr) {
		$('#smartSchoolAdminForm input').eq(0).val(tr.attr('userId'));
		$('#smartSchoolAdminForm input').eq(1).val(tr.attr('loginName'));
		$('#smartSchoolAdminForm input').eq(2).val(tr.attr('adminName'));
		$('#smartSchoolAdminForm input').eq(3).val(tr.attr('loginPwd'));
		$('#smartSchoolAdminForm input').eq(6).val(tr.attr('weixinLimit'));
		$('#smartSchoolAdminForm input').eq(7).val(tr.attr('remainingWeixin'));
		$('#smartSchoolAdminForm input').eq(8).val(tr.attr('shortLimit'));
		$('#smartSchoolAdminForm input').eq(9).val(tr.attr('remainingShort'));
		$('#smartSchoolAdminForm input').eq(10).val(tr.attr('remark'));
		var isAdmin=tr.attr('isAdmin');
		$('#smartSchoolAdminForm input[name=isAdmin]').attr('checked', false);
		$('#smartSchoolAdminForm input[name=isAdmin][value='+isAdmin+']').attr('checked', true);
	};
</script>
<link href="themes/css/custom.css" rel="stylesheet" type="text/css"/>
<style type="text/css">
	#smartSchoolAdminForm dt{
		width: 75px;
	}
	#smartSchoolAdminForm dd{
		width: 160px;
	}
</style>

<div>
	<div style="float: left; display: block; overflow: auto; width: 240px; border: solid 1px #CCC; line-height: 21px; background: #fff">
		<div layoutH="598"></div>
		<div class="form">
			<form id="smartSchoolAdminForm" method="post" action="${base }/saveAdmin.do"
				class="pageForm required-validate">
				<div class="pageFormContent">
					<dl style="margin: 10px 0;">
						<dt>登录名：</dt>
						<dd>
							<input type="hidden" name="id" />
							<input type="text" name="loginName" maxlength="20" class="required" />
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>姓名：</dt>
						<dd>
							<input type="text" name="adminName" maxlength="20" class="required" />
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>登录密码：</dt>
						<dd>
							<input type="text" name="loginPwd" maxlength="20" class="required" />
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>超级管理员：</dt>
						<dd>
							<input type="radio" name="isAdmin" value="1" style="width: 15px;"/>是
							<input type="radio" name="isAdmin" checked="checked" value="0" style="width: 15px;"/>否
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>天微信限次：</dt>
						<dd>
							<input type="text" name="weixinLimit" class="required digits" value="0"/>
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>天微信剩余：</dt>
						<dd>
							<input type="text" name="remainingWeixin" class="required digits" value="0"/>
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>天短信限次：</dt>
						<dd>
							<input type="text" name="shortLimit" class="required digits" value="0"/>
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>天短信剩余：</dt>
						<dd>
							<input type="text" name="remainingShort" class="required digits" value="0"/>
						</dd>
					</dl>
					<dl style="margin: 10px 0;">
						<dt>备注：</dt>
						<dd>
							<input type="text" name="remark" maxlength="20"/>
						</dd>
					</dl>
				</div>
				<security:authorize ifAnyGranted="ROLE_ADMIN">
				<div class="formBar">
					<div class="panelBar" style="border-style: none;">
						<ul class="toolBar">
							<li><a class="add" href="javascript:;"><span>添加</span></a></li>
							<li><a class="edit" href="javascript:;"><span>修改</span></a></li>
							<li><a class="delete" href="javascript:;"><span>删除</span></a></li>
						</ul>
					</div>
				</div>
				</security:authorize>
			</form>
		</div>
	</div>

	<div id="smartSchoolAdminList" class="unitBox" style="margin-left: 246px;">
		<jsp:include page="${base }/adminList.do" />
	</div>
</div>
