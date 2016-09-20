<!-- 平衡汇总 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script src="js/jquery.tablesorter.js" type="text/javascript"></script>
<script src="js/jquery.tablesorter.widgets.js" type="text/javascript"></script>
<script type="text/javascript">



	$(function() {

		$("#tablesOrder").tablesorter();
	});
	$(function() {
		$('.exportExcel').click(
				function() {
					$('#poisePool #pagerForm input[name=export]').val(1);
					$('#poisePool #pagerForm input[name=exportType]').val($(this).attr('exportType'));
					$('#poisePool #pagerForm').submit();
					$('#poisePool #pagerForm input[name=export]').val('');
					$('#poisePool #pagerForm input[name=exportType]').val('');
				});
	});
	/* 	$(function(){
	 $('#poisePool .search').click(function(){
	 divSearch($('#poisePool #pagerForm'),'poisePool');
	 });
	 }); */

	$(function() {
		$('#poisePool .search').click(function() {

			var beginDate = $('#beginDate').val();
			var endDate = $('#endDate').val();
			if (beginDate != "" && endDate != ""&& beginDate !=-1 && endDate !=-1) {
				if (beginDate >= endDate) {
					alertMsg.error('查询开始时间不能大于查询结束时间');
				} else {
					divSearch($('#poisePool #pagerForm'), 'poisePool');
				}
			} else {
				divSearch($('#poisePool #pagerForm'), 'poisePool');
			}
		});
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
				
					<td width="60">出纳员：</td>
					<td align="left">
						<select class="" outerw="80" innerw="100" name="operId">
							<option value="0" width="100">全部</option>
							<c:forEach var="u" items="${sysUserList}" varStatus="status">
								<option value="${u.operId}"  width="70" <c:if test="${u.operId==operId}">selected</c:if> >${u.loginName}</option>
							</c:forEach>
						</select>
					</td>
					
					<%-- <td width="70" class="recordType">记录类型：</td>
					<td align="left" class="recordType">
						<select class="" outerw="80" innerw="120" name="recordType">
							<option value="-1" width="120" <c:if test="${status.index==-1}">selected</c:if>>全部</option>
							<c:forEach var="r" items="${recordTypes}" varStatus="status">
								<option value="${status.index}" width="120" <c:if test="${status.index==recordType}">selected</c:if>>${r}</option>
							</c:forEach>
						</select>
					</td> --%>	
					<%-- <td style="width: auto;">营业部门：</td>
					<td style="width: auto;">
						<input type="hidden" name="deptId" value="${deptId}"/>
						<input type="text" name="deptName" value="${deptName}" size="10" readonly="readonly"/>
					</td>
					<td style="width: auto;">
						<a class="btnLook" width="300" maxable="false" resizable="true" title="选择部门" href="/selectDeptTree.do" lookupgroup="district"></a>					
					</td>
					<td style="width: auto;">消费类型：</td>
					<td align="left" style="width: auto;">
						<select class="" outerw="80" innerw="100" name="consumeType">
							<option value="-1" width="100">全部</option>
							<option value="1" width="100" <c:if test="${consumeType==1}">selected</c:if>>消费机普通消费</option>
							<option value="2" width="100" <c:if test="${consumeType==2}">selected</c:if>>消费机补助消费</option>
							<option value="3" width="100" <c:if test="${consumeType==3}">selected</c:if>>消费机混合消费</option>
 							<option value="9" width="100" <c:if test="${consumeType==9}">selected</c:if>>消费机补助领取</option> 
							<option value="39" width="100" <c:if test="${consumeType==39}">selected</c:if>>消费机补助清零</option>
							<option value="101" width="100" <c:if test="${consumeType==101}">selected</c:if>>水控普通消费</option>
							<option value="102" width="100" <c:if test="${consumeType==102}">selected</c:if>>水控补助消费</option>
							<option value="103" width="100" <c:if test="${consumeType==103}">selected</c:if>>水控混合消费</option>
							<option value="105" width="100" <c:if test="${consumeType==105}">selected</c:if>>水控大钱包找零</option>
							<option value="106" width="100" <c:if test="${consumeType==106}">selected</c:if>>水控补助找零</option>
							<option value="107" width="100" <c:if test="${consumeType==107}">selected</c:if>>水控混合找零</option>
 							<option value="109" width="100" <c:if test="${consumeType==109}">selected</c:if>>水控补助领取</option> 
 							<option value="139" width="100" <c:if test="${consumeType==139}">selected</c:if>>水控补助清零</option> 
						</select></td> --%>
					
					<td style="width: auto;padding-left: 10px">
						结算历史：
					</td>
					<td align="left" style="padding-right: 10px">
						<div id="showtime" style="display: block">
						<select name="beginDate" id="beginDate">
							<option value="-1" width="150">全部</option>
							<c:forEach var="u" items="${TiemList}" varStatus="status">
								<option value="${u.beginDate}" width="70" <c:if test="${u.beginDate==beginDate}">selected</c:if> >${u.beginDate}</option>
							</c:forEach>
						</select>
						<select  name="endDate" id ="endDate">
							<option value="-1" width="150">全部</option>
							<c:forEach var="u" items="${TiemList}" varStatus="status">
								<option value="${u.endDate}" width="70" <c:if test="${u.endDate==endDate}">selected</c:if> >${u.endDate}</option>
							</c:forEach>
						</select>
						</div>
					</td>			
					<!-- </tr>
					<tr> -->
					<td>
						<div class="buttonActive">
							<div class="buttonContent">
								<button type="button" class="search">点击查询</button>
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
		<table class="list" id="tablesOrder" width="105%" layoutH="148">
			<thead>
				<tr>
					<th style="width: auto;" align="center">序号</th>
					<th style="width: auto;" align="center">营业分区或操作员</th>
					<th style="width: auto;" align="center">操作类型</th>
					<th style="width: auto;" align="center">收入计次</th>
					<th style="width: auto;" align="center">收入金额</th>
					<th style="width: auto;" align="center">支出计次</th>
					<th style="width: auto;" align="center">支出金额</th>
					<th style="width: auto;" align="center">收支平衡额</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="c" items="${list}" varStatus="status">
					<tr>
						<td align="">${status.index+1}</td> <!-- center -->
						<td align="">${c.login}</td>
						<td align="">${c.recordTypeDes}</td>
						<td align="">${c.countIn}</td>
						<td align="">${c.countInFare}</td>
						<td align="">${c.countOut}</td>
						<td align="">${c.countOutFare}</td>
						<td align=""></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr bgcolor="#DFDFDF">
				<td></td>
				<td align=""><b>&nbsp;&nbsp;操&nbsp;&nbsp;作&nbsp;&nbsp;汇&nbsp;&nbsp;总</b></td>
				<td></td>
				<td  align="">${totalCountIn}</td>
				<td  align="">${totalCountInFare}</td>
				<td align="">${totalCountOut}</td>
				<td align="">${totalCountOutFare}</td>
				<td align="">累计收入:${poisFare}</td>
			</tr>
		</tfoot>
		</table>
		
		<div class="panelBar">
			<div class="pages">
				<span>每页显示</span> <select class="" name="numPerPage"
					onchange="navTabPageBreak({numPerPage:this.value}, 'poisePool')">
					<c:forEach begin="1" end="20" var="i" step="1">
						<option value='${i*10 }' <c:if test="${i*10==numPerPage }">selected</c:if> >${i*10 }</option>
					</c:forEach>
				</select>
				<span>条，共${totalCount}条</span>
			</div>
	
			<div class="pagination" rel="poisePool" totalCount="${totalCount }" numPerPage="${numPerPage }"
				pageNumShown="10" currentPage="${pageNum }"></div>
		</div>
</div>
