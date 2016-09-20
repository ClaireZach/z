<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	$(function() {
		

			$("#tablesOrder").tablesorter();
	
		$('.exportExcel').click(
				function() {
					$('#personStatisticList #pagerForm input[name=export]')
							.val(1);
					$('#personStatisticList #pagerForm input[name=exportType]')
							.val($(this).attr('exportType'));
					$('#personStatisticList #pagerForm').submit();
					$('#personStatisticList #pagerForm input[name=export]')
							.val('');
					$('#personStatisticList #pagerForm input[name=exportType]')
							.val('');
				});
	});

	$('#personStatisticList .search').click(
			function() {
				 var pattern = new RegExp("[~'‘’!@#$%^&*.。()-+_=:]");  
		            if($("#name").val() != "" && $("#name").val() != null){  
		                if(pattern.test($("#name").val())){  
		                    alert("用户信息输入非法字符，请重新输"); 
		                    $("#name").attr("value","");  
		                    $("#name").focus();  
		                    return false;  
		                }  
		            }  

				var endDate = $('#endDate').val();
				var beginDate = $('#beginDate').val();
				if (beginDate != "" && endDate != "") {
					if (beginDate >= endDate) {
						alertMsg.error('结束时间必须大于起始时间');
					} else {
						divSearch($('#personStatisticList #pagerForm'),
								'personStatisticList');
					}
				}
				else{
				divSearch($('#personStatisticList #pagerForm'),
						'personStatisticList');
				}
			});
</script>
<style type="text/css">
.inputDateButton {
	float: right;
}
</style>
<div class="pageHeader" style="border: 1px #B8D0D6 solid">
	<form action="${base}/list.do" id="pagerForm">
		<input type="hidden" name="export" /> <input type="hidden"
			name="exportType" /> <input type="hidden" name="pageNum"
			value="${pageNum}" /> <input type="hidden" name="numPerPage"
			value="${numPerPage}" /> <input type="hidden" name="totalCount"
			value="${totalCount}" />
		<div class="searchBar">
			<table border="0">
				<tr align="right">
					<td>用户信息：</td>
					<td><input type="text" name="nameStr" size="10" id="name"
						value="${nameStr }" maxlength="32"/></td>
					<td width="50">部门：</td>
					<td><input type="hidden" name="deptId" value="${deptId}" /> <input
						type="text" name="deptName" value="${deptName}" size="10"
						readonly="readonly" /></td>
					<td><a class="btnLook" width="300" maxable="false"
						resizable="true" title="选择部门"  href="/selectUserDeptTree.do"
						lookupgroup="district"></a></td>
					<td><input type="checkbox" name="includeSub"
						style="width: 13px; top: 2px;"
						<c:if test="${includeSub=='on'}">checked</c:if> />不包含下级</td>
						<td>&nbsp;&nbsp;选择日期：</td>
					<td align="left"><input type="text" name="beginDate"
						value="${beginDate}" id="beginDate" readonly="readonly"
						datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="18" /><a
						class="inputDateButton" href="javascript:;">选择</a></td>
					<td align="left">&nbsp;- <input type="text" name="endDate"
						value="${endDate}" id="endDate" readonly="readonly" class="date"
						datefmt="yyyy-MM-dd HH:mm:ss" size="18" /><a
						class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left"><input type="checkbox" name="includeOff"
						<c:if test="${includeOff=='on'}">checked</c:if> /> 包含注销用户</td>
				</tr>
				<tr>

					<td colspan="12">
						<div class="buttonActive">
							<div class="buttonContent">
								<button type="button" class="search">&nbsp;&nbsp;查询&nbsp;&nbsp;</button>
							</div>
						</div>
						<div class="buttonActive" style="margin: 0 10px;">
							<div class="buttonContent">
								<button type="button" class="exportExcel" exportType="0">导出当前</button>
							</div>
						</div>
						<div class="buttonActive">
							<div class="buttonContent">
								<button type="button" class="exportExcel" exportType="1">导出全部</button>
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
	<table class="list" width="99%" layoutH="148" id="tablesOrder">
		<thead>
			<tr>
				<th width="auto;">序号</th>
				<th width="auto;">卡号</th>
				<th width="auto;">学号</th>
				<th width="auto;">姓名</th>
				<th width="auto;">部门</th>
				<th width="auto;">消费次数</th>
				<th width="auto;">总消费金额</th>
				<th width="auto;">宏冲次数</th>
				<th width="auto;">宏冲金额</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach var="d" items="${list}" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${d.cardNO}</td>
					<td>${d.userNO}</td>
					<td>${d.username}</td>
					<td>${d.deptName}</td>
					<td>${d.consumeCount}</td>
					<td>${d.consumeFare}</td>
					<td>${d.subsidyConsumeCount}</td>
					<td>${d.subsidyConsumeFare}</td>
				</tr>
			</c:forEach>
			<tfoot bgcolor="red">
				<td>汇总</td>
				<td>${beginDate}</td>
				<td>${endDate}</td>
				<td></td>
				<td></td>
				<td>${totalConsumeCount}</td>
				<td>${totalConsumeFare}</td>
				<td>${totalSubsidyConsumeCount}</td>
				<td>${totalSubsidyConsumeFare}</td>

			</tfoot>
		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'personStatisticList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="personStatisticList"
			totalCount="${totalCount }" numPerPage="${numPerPage }"
			pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>
