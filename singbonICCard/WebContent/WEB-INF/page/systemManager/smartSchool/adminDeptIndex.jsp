<!-- 管理员授权 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
	var list = $('#userDeptTree li');
	list.each(function() {
		var $this = $(this);
		var parentId = $this.attr('parentId');
		if (parentId == '0') {
			$('.expand').append($this);
		} else {
			var $li = $('li[deptId="' + parentId + '"]');
			if ($('>ul', $li).size() == 0) {
				$('<ul></ul>').appendTo($li);
			}
			$('>ul', $li).append($(this));
		}
	});
	function smartSchoolAdminDeptClick(tr) {
		$('#smartSchoolAdminDeptForm input:first').val(tr.attr('userId'));
		$('#userDept .indeterminate,#userDept .checked').removeClass('indeterminate').removeClass('checked').addClass('unchecked');
		var deptIds=tr.attr('deptIds');
		$('#userDept li:not(:has(li))').each(function() {
			var ckbox = $('.ckbox', this);
			if (deptIds.indexOf($('input', ckbox).val()) != -1) {
				ckbox.click();
			}
		});
	}
	$(function(){
		$('#smartSchoolAdminDeptForm .save').click(function(){
			var id=$('#smartSchoolAdminDeptForm input:first').val();
			if(id==''){
				alertMsg.warn('请选择管理员');
				return;
			}
			var deptIds = '';
			$('#userDept .indeterminate,#userDept .checked').each(function(i, e2) {
				var deptId=$(this).find('input').val();
				if(deptId!='on')
					deptIds += deptId + ',';
			});
			$('#smartSchoolAdminDeptForm input:last').val(deptIds);
			validateCallback($(this).parents('form'), function(e) {
				if (e == 1) {
					$('tr[userId='+id+']').attr('deptIds',deptIds);
					$('#smartSchoolAdminDeptForm input:first').val('');
					$('#smartSchoolAdminDeptForm input:last').val('');
					alertMsg.correct('授权成功！');
				} else {
					alertMsg.error('授权失败！');
				}
			}, null);
		});
	});
</script>

<style type="text/css">
</style>

<div id="userDept" style="float: left; display: block; overflow: auto; width: 300px;background-color: #fff;" layoutH="62">
	<div id="userDeptTree">
		<c:forEach items="${userDeptList }" var="d">
			<li deptId="${d.id }" parentId="${d.parentId }"><a href="javascript:;" deptId="${d.id }" tvalue="${d.id}">${d.deptName }</a></li>
		</c:forEach>
	</div>
	<ul class="tree collapse userDeptTree treeCheck">
		<li deptId="-1"><a href="javascript:;" deptId="-1">部门列表</a>
			<ul class="expand">
	
			</ul></li>
	</ul>
</div>
<div class="unitBox" style="margin-left: 305px;">
	<table class="table" width="99%" layoutH="160" rel="jbsxBox">
		<thead>
			<tr>
				<th width="100">管理员</th>
				<th width="100">备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${adminList }" var="s">
				<tr target="smartSchoolAdminDept" userId="${s.id }" deptIds="${s.deptIds}">
					<td>${s.loginName }</td>
					<td>${s.remark }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<security:authorize ifAnyGranted="ROLE_ADMIN">
	<div class="form">
		<form id="smartSchoolAdminDeptForm" method="post" action="${base }/saveAdmin.do"
			class="pageForm required-validate">
			<div class="formBar">
				<input type="hidden" name="id" />
				<input type="hidden" name="deptIds" />
				<div class="panelBar" style="border-style: none;">
					<ul class="toolBar">
						<li><a class="save" href="javascript:;"><span>保存</span></a></li>
					</ul>
				</div>
			</div>
		</form>
	</div>
	</security:authorize>
</div>
