<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">

	$(function() {
		$("#tablesOrder").tablesorter();
		$('.exportExcel').click(
				function() {
					$('#subsidyStatList #pagerForm input[name=export]')
							.val(1);
					$('#subsidyStatList #pagerForm input[name=exportType]')
							.val($(this).attr('exportType'));
					$('#subsidyStatList #pagerForm').submit();
					$('#subsidyStatList #pagerForm input[name=export]').val(
							'');
					$('#subsidyStatList #pagerForm input[name=exportType]')
							.val('');
				});
	});
	$('#subsidyStatList .search').click(
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
				
				divSearch($('#subsidyStatList #pagerForm'),
						'subsidyStatList');
				
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
					<td width="80">补助版本号：</td>
					
					<td><input type="text" name="subsidyVersion" size="7"
						value="${subsidyVersion }" id="name" maxlength="32"/></td>
					
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
	
		<table class="list" width="112%" layoutH="160" id="tablesOrder">
			<thead>
				<tr>
					<th width="auto;">序号</th>
					<th width="auto;">补助版本号</th>
					<th width="auto;">已领人数</th>
					<th width="auto;">已领金额</th>
					<th width="auto;">未领人数</th>
					<th width="auto;">未领金额</th>
					<th width="auto;">宏冲日期</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="d" items="${list}" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td>${d.subsidyVersion}</td>
						<td>${d.readyGainCount}</td>
						<td>${d.readyGainFare}</td>
						<td>${d.noGainCount}</td>
						<td>${d.noGainFare}</td>
							<td>${d.invalidDate}</td>
					</tr>
				</c:forEach>
				<tfoot bgcolor="red">
					<td></td>
					<td>总计</td>
					<td>${totalGainCount}</td>
					<td>${totalGainFare}</td>
					<td>${totalNoGainCount}</td>
					<td>${totalNoGainFare}</td>
					<td></td>
				</tfoot>
			</tbody>
		</table>

	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'subsidyStatList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="subsidyStatList"
			totalCount="${totalCount }" numPerPage="${numPerPage }"
			pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>

