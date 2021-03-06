<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
	// 0信息录入，1修改，2单个发卡，3信息发卡
	var cardOptions = {
		width : 600,
		height : 450,
		max : false,
		mask : true,
		mixable : false,
		minable : false,
		resizable : true,
		drawable : true,
		fresh : false
	};
	var deptAdjustOptions = {
		width : 900,
		height : 550,
		max : false,
		mask : true,
		mixable : false,
		minable : false,
		resizable : true,
		drawable : true,
		fresh : false
	};
	var batchCardOptions = {
		width : 850,
		height : 500,
		max : false,
		mask : true,
		mixable : false,
		minable : false,
		resizable : true,
		drawable : true,
		fresh : false
	};
	var userListOps = {
		menuStyle : {
			width : '120px'
		},
		bindings : {
			'info' : function(t, target) {
				if (selectedDeptId <= '0') {
					alertMsg.warn('请选择部门');
					return;
				}
				cardOptions.width=600;
				cardOptions.height=450;
				var url = '${base}/editUser.do?editType=0&opStatus=0&deptId='+ selectedDeptId + '&batchId=' + selectedBatchId;
				$.pdialog.open(url, 'dialog', '信息录入', cardOptions);
			},
			'edit' : function(t, target) {
				cardOptions.width=600;
				cardOptions.height=450;
				var url = '${base}/editUser.do?editType=1&userId='+ $(t).attr('userId')+'&opStatus='+$(t).attr('status');
				$.pdialog.open(url, 'dialog', '信息修改', cardOptions);
			},
			'import' : function(t, target) {
				if (selectedDeptId <= '0') {
					alertMsg.warn('请选择部门');
					return;
				}
				cardOptions.width=280;
				cardOptions.height=170;
				var url = '${base}/import.do?deptId='+ selectedDeptId;
				$.pdialog.open(url, 'dialog', '信息导入', cardOptions);
			},
			'dept' : function(t, target) {
				var userId = -1;
				if ($(t).attr('userId') != null) {
					userId = $(t).attr('userId');
				}
				var url = '${base}/deptAdjust.do?userId=' + userId
				$.pdialog.open(url, 'dialog', '部门调整', deptAdjustOptions);
			},
			'single' : function(t, target) {
				if (selectedDeptId <= '0') {
					alertMsg.warn('请选择部门！');
					return;
				}
				if (!checkDeviceSn()) {
					return;
				}
				cardOptions.width=600;
				cardOptions.height=450;
				var url = '${base}/editUser.do?editType=2&opStatus=0&deptId='+ selectedDeptId + '&batchId=' + selectedBatchId;
				$.pdialog.open(url, 'dialog', '单个发卡', cardOptions);
			},
			'infoCard' : function(t, target) {
				if (!checkDeviceSn()) {
					return;
				}
				var userId=$(t).attr('userId');
				if(userId==null){
					alertMsg.warn('请先选择人员！');
					return;
				}
				if ($(t).attr('status') == 0) {
					cardOptions.width=600;
					cardOptions.height=450;
					var url = '${base}/editUser.do?editType=3&deptId='+$(t).attr('deptId')+'&opStatus=0&userId='+ userId;
					$.pdialog.open(url, 'dialog', '信息发卡', cardOptions);
				} else {
					alertMsg.warn('该用户已发卡不能重复操作！');
				}
			},
			'batch' : function(t, target) {
				if (selectedDeptId <= '0') {
					alertMsg.warn('请选择部门！');
					return;
				}
				if (!checkDeviceSn()) {
					return;
				}
				cardOptions.width=900;
				cardOptions.height=450;
				var url = '${base}/editUser.do?editType=4&opStatus=0&deptId='+ selectedDeptId + '&batchId=' + selectedBatchId;
				$.pdialog.open(url, 'dialog', '批量发卡', batchCardOptions);
			},
			'readCard' : function(t, target) {
				if (!checkDeviceSn()) {
					return;
				}
				cardOptions.width=600;
				cardOptions.height=500;
				var url = '${base}/changeCard.do?editType=4';
				$.pdialog.open(url, 'dialog', '读卡修正', cardOptions);
			},
			'loss' : function(t, target) {
				var userId=$(t).attr('userId');
				if(userId==null){
					alertMsg.warn('请先选择人员！');
					return;
				}
				if ($(t).attr('status') == 241) {
					cardOptions.width=600;
					cardOptions.height=500;
					var url = '${base}/editUser.do?editType=5&userId='+ userId;
					$.pdialog.open(url, 'dialog', '挂失', cardOptions);
				} else {
					alertMsg.warn('该卡不是正常卡，不能进行挂失操作！');
				}
			},
			'unloss' : function(t, target) {
				if (!checkDeviceSn()) {
					return;
				}
				cardOptions.width=600;
				cardOptions.height=520;
				var url = '${base}/changeCard.do?editType=2';
				$.pdialog.open(url, 'dialog', '解挂', cardOptions);
			},
			'remakeCard' : function(t, target) {
				if (!checkDeviceSn()) {
					return;
				}
				var userId=$(t).attr('userId');
				if(userId==null){
					alertMsg.warn('请先选择人员！');
					return;
				}
				if ($(t).attr('status') == 243) {
					cardOptions.width=600;
					cardOptions.height=500;
					var url = '${base}/editUser.do?editType=6&userId='+ userId;
					$.pdialog.open(url, 'dialog', '补卡', cardOptions);
				} else {
					alertMsg.warn('该卡不是挂失卡，不能进行补卡操作！');
				}
			},
			'cardOff' : function(t, target) {
				if (!checkDeviceSn()) {
					return;
				}
				cardOptions.width=600;
				cardOptions.height=550;
				var url = '${base}/changeCard.do?editType=3';
				$.pdialog.open(url, 'dialog', '注销', cardOptions);
			},
			'charge' : function(t, target) {
				if (!checkDeviceSn()) {
					return;
				}
				cardOptions.width=600;
				cardOptions.height=400;
				var url = '${base}/charge.do';
				$.pdialog.open(url, 'dialog', '存取款', cardOptions);
			},
			'delete' : function(t, target) {
				var userIds = '';
				$('.userList input:checkbox').each(
						function() {
							if ($(this).attr('checked')
									&& $(this).attr('status') == 0) {
								userIds += $(this).val() + ',';
							}
						});
				if (userIds != '') {
					userIds = userIds.substring(0, userIds.length - 1);
					alertMsg.confirm('确定要删除未发卡人员吗？', {
						okCall : function() {
							$.post('${base}/delete.do?checkedUserIds='+ userIds, function(e) {
								refreshUserList();
							});
						}
					});
				} else {
					alertMsg.warn('请选择未发卡人员！');
				}
			}
		},
		onShowMenu : function(e, menu) {
			if (!$(e.target).parents('tbody').hasClass('userList')) {
				$('#edit', menu).remove();
			}
			return menu;
		}
	};

	function checkDeviceSn() {
		if (deviceSn == '') {
			alertMsg.warn('请先绑定读卡机再进行操作！');
			return false;
		}
		return true;
	}
	$(function() {
		$('#userList .search').click(function() {
			if($('#userDeptTree input[name=includeSub]').attr('checked')){
				$('#userList #pagerForm input[name=includeSub]').val(1);				
			}else{
				$('#userList #pagerForm input[name=includeSub]').val('');				
			}
			divSearch($('#userList #pagerForm'), 'userList');
		});
	});
</script>
<div class="contextMenu" id="menu" style="display: none;">
	<ul>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_INFO">
			<li id="info">信息录入</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_EDIT">
			<li id="edit">信息修改</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_IMPORT">
			<li id="import">信息导入</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_DEPT">
			<li id="dept">部门调整</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_INFO,ROLE_MAINCARD_EDIT,ROLE_MAINCARD_DEPT">
			<li class="divide" />
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_SINGLE">
			<li id="single">单个发卡</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_INFOCARD">
			<li id="infoCard">信息发卡</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_BATCH">
			<li id="batch">批量发卡</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_SINGLE,ROLE_MAINCARD_INFOCARD,ROLE_MAINCARD_BATCH">
			<li class="divide" />
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_READCARD">
			<li id="readCard">读卡</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_LOSS">
			<li id="loss">挂失</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_UNLOSS">
			<li id="unloss">解挂</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_REMAKECARD">
			<li id="remakeCard">补卡</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_OFFWITHCARD">
			<li id="cardOff">卡注销</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_CHARGE">
			<li id="charge">存、取款</li>
		</security:authorize>
		<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD_DELETE">
			<li id="delete">删除选中未发卡人员</li>
		</security:authorize>
	</ul>
</div>
<div class="pageHeader" style="border: 1px #B8D0D6 solid">
	<form action="${base}/list.do" id="pagerForm">
		<input type="hidden" name="deptId" value="${deptId}" /> 
		<input type="hidden" name="pageNum" value="${pageNum}" /> 
		<input type="hidden" name="numPerPage" value="${numPerPage}" />
		<input type="hidden" name="totalCount" value="${totalCount}" />
		<input type="hidden" name="includeSub" value="${includeSub}" />
		<div class="searchBar">
			<table border="0">
				<tr align="right">
					<td>用户信息：</td>
					<td>
						<input type="text" name="nameStr" size="10" value="${nameStr }" />
					</td>
					<td style="padding-left: 10px;">
						<div class="buttonActive">
							<div class="buttonContent">
								<button type="button" class="search">&nbsp;&nbsp;查询&nbsp;&nbsp;</button>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</form>
</div>
<div class="pageContent"
	style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid">
	<table class="list" width="99%" layoutH="125">
		<thead>
			<tr>
				<th width="10"><input type="checkbox" group="userIds" class="checkboxCtrl"></th>
				<th width="80">序号</th>
				<th width="100">编号</th>
				<th width="100">姓名</th>
				<th width="120">性别</th>
				<th width="80">卡类别</th>
				<th width="80">状态</th>
				<th width="200">身份证号</th>
			</tr>
		</thead>
		<tbody class="userList">
			<c:forEach var="user" items="${list}" varStatus="status">
				<tr userId="${user.userId }" status="${user.status }" cardNO="${user.cardNO}" deptId="${user.deptId}">
					<td><input name="userIds" value="${user.userId }" status="${user.status}" type="checkbox"></td>
					<td>${status.index+1}</td>
					<td>${user.userNO}</td>
					<td>${user.username}</td>
					<td>${user.sexDesc}</td>
					<td>${user.cardTypeId}类卡</td>
					<td>${user.statusDesc}</td>
					<td>${user.cardID}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'userList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="userList" totalCount="${totalCount }"
			numPerPage="${numPerPage }" pageNumShown="10"
			currentPage="${pageNum }"></div>
	</div>
</div>
