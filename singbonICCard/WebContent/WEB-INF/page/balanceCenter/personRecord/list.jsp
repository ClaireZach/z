<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">

	$(function(){
		  /* if($("#name").val() == "" || $("#name").val() == null){
			  alert("请输入要查询的用户信息");
			  $("#name").focus();  
		  } */
		
	$("#tablesOrder").tablesorter();
		$('.exportExcel').click(	function() {
			$('#personRecordList #pagerForm input[name=export]')
			.val(1);
	$('#personRecordList #pagerForm input[name=exportType]')
			.val($(this).attr('exportType'));
	$('#personRecordList #pagerForm').submit();
	$('#personRecordList #pagerForm input[name=export]').val(
			'');
	$('#personRecordList #pagerForm input[name=exportType]')
			.val('');
});
		});
	
	
	
		$('#personRecordList .search').click(function(){
			  var pattern = new RegExp("[~'‘’!@#$%^&*.。()-+_=:]");  
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
					}else{
			divSearch($('#personRecordList #pagerForm'),'personRecordList');
					}
			 }else{
			 
			 divSearch($('#personRecordList #pagerForm'),'personRecordList');
			 }
		});
	

</script>
<style type="text/css">
	.inputDateButton{
		float: right;
	}
	.recordType{
/* 		display: none; */
	}
</style>
<div class="pageHeader" style="border: 1px #B8D0D6 solid">
	<form action="${base}/list.do" id="pagerForm">
		<input type="hidden" name="export"/>
		<input type="hidden" name="exportType"/>
		<input type="hidden" name="pageNum" value="${pageNum}" />
		<input type="hidden" name="numPerPage" value="${numPerPage}" />
 	<input type="hidden" name="totalCount" value="${totalCount}" />
		<div class="searchBar">
			<table border="0">
				<tr align="right">
					<td>用户信息：</td>
					<td><input type="text" name="nameStr" size="10" id="name"
						value="${nameStr }" maxlength="32" />
					</td>
				
					<td>&nbsp;&nbsp;选择日期：</td>
					<td align="left">
						<input type="text" name="beginDate" id="beginDate" value="${beginDate}" id="beginDate" readonly="readonly" datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="18"/><a class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left">
						&nbsp;- 
						<input type="text" name="endDate" id="endDate" value="${endDate}" id="endDate" readonly="readonly" class="date" datefmt="yyyy-MM-dd HH:mm:ss"  size="18"/><a class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left">
						<input type="checkbox" name="includeOff" <c:if test="${includeOff=='on'}">checked</c:if> />
						包含注销用户
					</td>
				</tr>
				<tr>
					<td width="70" class="recordType">记录类型：</td>
					<td align="left" class="recordType">
						<select class="combox" outerw="80" innerw="120" name="resultTypes">
							<option value="-1" width="100" <c:if test="${recordType==-1}">selected</c:if>>全部</option>
							<option value="0" width="100" <c:if test="${recordType==0}">selected</c:if>>发卡</option>
							<option value="1" width="100" <c:if test="${recordType==1}">selected</c:if>>收取卡押金</option>
							<option value="2" width="100" <c:if test="${recordType==2}">selected</c:if>>发卡赠送金额</option>
							<option value="6" width="100" <c:if test="${recordType==6}">selected</c:if>>PC存款</option>
							<option value="7" width="100" <c:if test="${recordType==7}">selected</c:if>>存款赠送金额</option>
							<option value="8" width="100" <c:if test="${recordType==8}">selected</c:if>>PC取款</option>
							<option value="9" width="100" <c:if test="${recordType==9}">selected</c:if>>退还卡押金</option>
							<%-- <option value="10" width="100" <c:if test="${recordType==10}">selected</c:if>>消费机补助存款</option>
							<option value="11" width="100" <c:if test="${recordType==11}">selected</c:if>>消费机补助清零</option>
							<option value="12" width="100" <c:if test="${recordType==12}">selected</c:if>>水控补助存款</option>
							<option value="13" width="100" <c:if test="${recordType==13}">selected</c:if>>水控补助清零</option> --%>
							<option value="3" width="100" <c:if test="${recordType==3}">selected</c:if>>补卡</option>
							
							 <option value="4" width="100" <c:if test="${recordType==4}">selected</c:if>>挂失</option>
							<option value="5" width="100" <c:if test="${recordType==5}">selected</c:if>>解挂</option>
							
							<option value="14" width="100" <c:if test="${recordType==14}">selected</c:if>>卡注销</option>
							<option value="15" width="100" <c:if test="${recordType==15}">selected</c:if>>按卡修正</option>
							<option value="16" width="100" <c:if test="${recordType==16}">selected</c:if>>按库修正</option>
							
							<option value="2821" width="100" <c:if test="${consumeType==1}">selected</c:if>>消费机普通消费</option>
							<option value="2822" width="100" <c:if test="${consumeType==2}">selected</c:if>>消费机补助消费</option>
							<option value="2823" width="100" <c:if test="${consumeType==3}">selected</c:if>>消费机混合消费</option>
 							<option value="2829" width="100" <c:if test="${consumeType==9}">selected</c:if>>消费机补助领取</option> 
 							<option value="28239" width="100" <c:if test="${consumeType==39}">selected</c:if>>消费机补助清零</option> 
							
							<option value="282101" width="100" <c:if test="${consumeType==101}">selected</c:if>>水控普通消费</option>
							<option value="282102" width="100" <c:if test="${consumeType==102}">selected</c:if>>水控补助消费</option>
							<option value="282103" width="100" <c:if test="${consumeType==103}">selected</c:if>>水控混合消费</option>
							<option value="282105" width="100" <c:if test="${consumeType==105}">selected</c:if>>水控大钱包找零</option>
							<option value="282106" width="100" <c:if test="${consumeType==106}">selected</c:if>>水控补助找零</option>
							<option value="282107" width="100" <c:if test="${consumeType==107}">selected</c:if>>水控混合找零</option>
							<option value="282109" width="100" <c:if test="${consumeType==109}">selected</c:if>>水控补助领取</option> 
							<option value="282139" width="100" <c:if test="${consumeType==139}">selected</c:if>>水控补助清零</option>
						</select>
					</td>		
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
				<th width="auto;">学号</th>
				<th width="auto;">姓名</th>
				<th width="auto;">卡号</th>
				<th width="auto;">记录类型</th>
				<th width="auto;">卡操作金额</th>
				<th width="auto;">卡总额</th>

				<th width="auto;">卡余额</th>

				<th width="auto;">补助操作额</th>
				<th width="auto;">补助余额</th>

				<th width="auto;">卡操作计数器</th>
				<th width="auto;">补助操作计数器</th>
				<th width="auto;">操作时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="d" items="${list}" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${d.userNO}</td>
					<td>${d.username}</td>
					<td>${d.cardNO}</td>
					<td>${d.consumeType}</td>
					<td>${d.opFare}</td>
					<td>${d.sumFare}</td>

					<td>${d.oddFare}</td>
					<td>${d.subsidyOpFare}</td>
					<td>${d.subsidyOddFare}</td>

					<td>${d.opCount}</td>
					<td>${d.subsidyOpCount}</td>

					<td>${d.opTime}</td>
			</c:forEach>

			</tr>

		</tbody>
	</table>
 	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'personRecordList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }' <c:if test="${i*10==numPerPage }">selected</c:if> >${i*10 }</option>
				</c:forEach>
			</select>
			<span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="personRecordList" totalCount="${totalCount }" numPerPage="${numPerPage }"
			pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>
