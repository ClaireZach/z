<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">

	$(function() {
		$("#tablesOrder").tablesorter();
		$('.exportExcel').click(
				function() {
					$('#subsidyRecordList #pagerForm input[name=export]')
							.val(1);
					$('#subsidyRecordList #pagerForm input[name=exportType]')
							.val($(this).attr('exportType'));
					$('#subsidyRecordList #pagerForm').submit();
					$('#subsidyRecordList #pagerForm input[name=export]').val(
							'');
					$('#subsidyRecordList #pagerForm input[name=exportType]')
							.val('');
				});
	});
	$('#subsidyRecordList .search').click(
			function() {
				 var pattern = new RegExp("[~’‘'!@#$%^&*.。()-+_=:]");  
		            if($("#name").val() != "" && $("#name").val() != null){  
		                if(pattern.test($("#name").val())){  
		                    alert("用户信息输入非法字符，请重新输入"); 
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
						
						divSearch($('#subsidyRecordList #pagerForm'),
								'subsidyRecordList');
					}
				}
				else{
				divSearch($('#subsidyRecordList #pagerForm'),
						'subsidyRecordList');
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
			
		<div class="searchBar" id="searchBar">
			<table border="0">
				<tr align="right">
					<td width="60">用户信息：</td>
					<td><input type="text" name="nameStr" size="7"
						value="${nameStr }" id="name" maxlength="32"/></td>
					<td width="60">卡种类：</td>
					<td align="left"><select class="combox" outerw="45"
						innerw="65" name="cardTypeId">
							<option value="-1" width="50">全部</option>
							<c:forEach var="i" begin="0" end="15" step="1">
								<option value="${i}" width="70"
									<c:if test="${i==cardTypeId}">selected</c:if>>${i}类卡</option>
							</c:forEach>
					</select></td>

					<td align="left"><input type="checkbox" name="consumeGain" value="9" 
						<c:if test="${consumeGain=='9'}">checked</c:if> />消费机补助领取</td>
					<td align="left"><input type="checkbox" name="consumeToZero" value="39"
						<c:if test="${consumeToZero=='39'}">checked</c:if> />消费机补助清零</td>
					<td align="left"><input type="checkbox" name="waterGain" value="109"
						<c:if test="${waterGain=='109'}">checked</c:if> />水控机补助领取</td>
					<td align="left"><input type="checkbox" name="waterToZero" value="139"
						<c:if test="${waterToZero=='139'}">checked</c:if> />水控机补助清零</td>

					<td align="right">部门：</td>
					<td><input type="hidden" name="deptId" value="${deptId}" /> <input
						type="text" name="deptName" value="${deptName}" size="10"
						readonly="readonly" /></td>
					<td><a class="btnLook" width="300" maxable="false"
						resizable="true" title="选择部门" href="/selectUserDeptTree.do"
						lookupgroup="district"></a></td>
					<td><input type="checkbox" name="includeSub"
						style="width: 13px; top: 2px;"
						<c:if test="${includeSub=='on'}">checked</c:if> />不包含下级</td>
				</tr>
				<tr align="left">
					<td width="50">终端：</td>
					<td><input type="hidden" name="deviceId" value="${deviceId}" />
						<input type="text" name="deviceName" value="${deviceName}"
						size="10" readonly="readonly" /></td>
					<td><a class="btnLook" width="800" height="500"
						maxable="false" resizable="false" title="选择终端"
						href="/selectPosIndex.do" lookupgroup="district"></a></td>
					<td id="selectDate" width="8">选择日期:</td>
					<td align="left"><input type="text" name="beginDate"
						value="${beginDate}" id="beginDate" readonly="readonly"
						datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="15" /><a
						id="inputDateButton" class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left" id="td">&nbsp;- <input type="text"
						id="endDate" name="endDate" value="${endDate}" readonly="readonly"
						class="date" datefmt="yyyy-MM-dd HH:mm:ss" size="15" /><a
						id="inputDateButton" class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left"><input type="checkbox" name="includeOff"
						<c:if test="${includeOff=='on'}">checked</c:if> /> 包含注销用户</td>
						
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
	
		<table class="list" width="112%" layoutH="160" id="tablesOrder" >
			<thead>
				<tr>
					<th width="auto;">序号</th>
					<th width="auto;">设备名</th>
					<th width="auto;">学号</th>
					<th width="auto;">姓名</th>
					<th width="auto;">卡号</th>
					<th width="auto;">卡类型</th>
					<th width="auto;">卡状态</th>
					<th width="auto;">卡总额</th>
					<th width="auto;">卡余额</th>
					<th width="auto;">补助操作额</th>
					<th width="auto;">补助余额</th>
					<th width="auto;">补助操作计数器</th>
					<th width="auto;">操作类型</th>
					<th width="auto;">操作时间</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="d" items="${list}" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td>${d.deviceName}</td>
						<td>${d.userNO}</td>
						<td>${d.username}</td>
						<td>${d.cardNO}</td>

						<td>${d.cardTypeId}</td>
						<td>${d.status}</td>
						<td>${d.totalFare}</td>
						<td>${d.oddFare}</td>
						<td>${d.subsidyOpFare}</td>
						<td>${d.subsidyOddFare}</td>
						<td>${d.subsidyOpCount}</td>
						<td>${d.consumeType}</td>
						<td>${d.opTime}</td>

					</tr>
				</c:forEach>
			</tbody>
		</table>


	
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'subsidyRecordList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="subsidyRecordList"
			totalCount="${totalCount }" numPerPage="${numPerPage }"
			pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>

