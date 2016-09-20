<!-- 结算汇总 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">

$(function() {

	$("#tablesOrder").tablesorter();
});
	$(function(){
		$('.exportExcel').click(function(){
			$('#balancePoolList #pagerForm input[name=export]').val(1);
			$('#balancePoolList #pagerForm input[name=exportType]').val($(this).attr('exportType'));
			$('#balancePoolList #pagerForm').submit();
			$('#balancePoolList #pagerForm input[name=export]').val('');
			$('#balancePoolList #pagerForm input[name=exportType]').val('');
		});
	});
	/* $(function(){
		$('#balancePoolList .search').click(function(){
			divSearch($('#balancePoolList #pagerForm'),'balancePoolList');
		});
	}); */
 	$(function(){
		$('#balancePoolList .search').click(function(){
			
			var beginDate = $('#beginDate').val();
			var endDate = $('#endDate').val();
			if (beginDate != "" && endDate != "" && beginDate !=-1 && endDate !=-1) {
				if (beginDate >= endDate) {
					alertMsg.error('查询开始时间不能大于查询结束时间');
				}
				else{
					divSearch($('#balancePoolList #pagerForm'),'balancePoolList');
						}
			}
			else{
				divSearch($('#balancePoolList #pagerForm'),'balancePoolList');
					}
		});			
	}); 
</script>

<style type="text/css">
	.inputDateButton{
		float: right;
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
					<%-- <td>用户信息：</td>
					<td><input type="text" name="nameStr" size="10"
						value="${nameStr }" />
					</td> --%>
					<td style="width: auto;">营业部门：</td>
					<td style="width: auto;">
						<input type="hidden" name="deptId" value="${deptId}"/>
						<input type="text" name="deptName" value="${deptName}" size="10" readonly="readonly"/>
					</td>
					<td style="width: auto;">
						<a class="btnLook" width="300" maxable="false" resizable="true" title="选择部门" href="/selectDeptTree.do" lookupgroup="district"></a>					
					</td>
					<td  style="padding-right: 10px">
						<input type="checkbox" name="includeSub" <c:if test="${includeSub=='on'}">checked</c:if> />
						不包含下级部门
					</td>
					<%-- <td align="left">
						<input type="checkbox" name="includeOff" <c:if test="${includeOff=='on'}">checked</c:if> />
						包含注销用户
					</td> --%>
					<td style="width: auto;margin: 0 10px;">消费类型：</td>
					<td align="left" style="width: auto;">
						<!-- <select class="combox" outerw="80" innerw="100" name="consumeType"> -->
						<select outerw="80" innerw="100" name="consumeType">
							<option value="-1" width="100">全部</option>
							<option value="1" width="100" <c:if test="${consumeType==1}">selected</c:if>>消费机普通消费</option>
							<option value="2" width="100" <c:if test="${consumeType==2}">selected</c:if>>消费机补助消费</option>
							<option value="3" width="100" <c:if test="${consumeType==3}">selected</c:if>>消费机混合消费</option>
 						<%-- 	<option value="9" width="100" <c:if test="${consumeType==9}">selected</c:if>>消费机补助领取</option>  --%>
							<option value="39" width="100" <c:if test="${consumeType==39}">selected</c:if>>消费机补助清零</option>
							<option value="101" width="100" <c:if test="${consumeType==101}">selected</c:if>>水控普通消费</option>
							<option value="102" width="100" <c:if test="${consumeType==102}">selected</c:if>>水控补助消费</option>
							<option value="103" width="100" <c:if test="${consumeType==103}">selected</c:if>>水控混合消费</option>
							<option value="105" width="100" <c:if test="${consumeType==105}">selected</c:if>>水控大钱包找零</option>
							<option value="106" width="100" <c:if test="${consumeType==106}">selected</c:if>>水控补助找零</option>
							<option value="107" width="100" <c:if test="${consumeType==107}">selected</c:if>>水控混合找零</option>
 							<%-- <option value="109" width="100" <c:if test="${consumeType==109}">selected</c:if>>水控补助领取</option>  --%>
 							<option value="139" width="100" <c:if test="${consumeType==139}">selected</c:if>>水控补助清零</option> 
						</select></td>
					<td style="width: auto;">
						结算历史：
					</td>
					<td align="left" style="padding-right: 10px">
						<div id="showtime" style="display: block">
						<select  name="beginDate" id="beginDate">
							<option value="-1" width="150">全部</option>
							<c:forEach var="u" items="${TiemList}" varStatus="status">
								<option value="${u.beginDate}" width="70" <c:if test="${u.beginDate==beginDate}">selected</c:if> >${u.beginDate}</option>
							</c:forEach>
						</select>
						<select name="endDate" id="endDate">
							<option value="-1" width="150">全部</option>
							<c:forEach var="u" items="${TiemList}" varStatus="status">
								<option value="${u.endDate}" width="70" <c:if test="${u.endDate==endDate}">selected</c:if> >${u.endDate}</option>
							</c:forEach>
						</select>
						</div>
					</td>
					<td >管理归商户<input type="checkbox" disabled="disabled" name="shanghuCountFare" <c:if test="${shanghuCountFare=='on'}">checked</c:if> /></td>
					<td >优惠费商户承担<input type="checkbox" name="disCountFare" <c:if test="${disCountFare=='on'}">checked</c:if> /></td>
					</tr><tr>
					<%-- <td >管理归商户<input type="checkbox" name="shanghuCountFare" <c:if test="${shanghuCountFare=='on'}">checked</c:if> /></td>
					<td >优惠费商户承担<input type="checkbox" name="disCountFare" <c:if test="${disCountFare=='on'}">checked</c:if> /></td>		 --%>	
					<td colspan="9">
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
	<table class="list" id="tablesOrder" width="99%" layoutH="148">
		<thead>
			<tr>
				<th style="width: auto;">序号</th>
				<th  style="width: auto;">营业部门</th>
				<th  style="width: auto;">消费类型</th>
				<th  style="width: auto;">消费次数</th>
				<th  style="width: auto;">消费金额</th>
				<th  style="width: auto;" >宏冲次数</th>
				<th  style="width: auto;" >宏冲金额</th>
				<th  style="width: auto;">结算开始日期</th>
				<th  style="width: auto;">结算结束日期</th>
				<!-- <th  style="width: auto;">查询开始时间</th>
				<th  style="width: auto;">查询结束时间</th> -->
			</tr>
		</thead>
		<tbody>
			<c:forEach var="c" items="${list}" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td>${c.dpName}</td>
						<td>${c.recordTypes}</td>
						<td>${c.OpCount}</td>
						<td>${c.opFare}</td>
						<td>${c.hcCount}</td>
						<td>${c.hcFare}</td>
						<td>${c.beginDate}</td>
						<td>${c.endDate}</td>
						<%-- <td>${beginDate}</td>
						<td>${endDate}</td> --%>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr bgcolor="#DFDFDF">
				<td></td>
				<td colspan="2" align="center"><b>&nbsp;&nbsp;汇&nbsp;&nbsp;&nbsp;&nbsp;总&nbsp;&nbsp;</b></td>
				<td>${totalFareCount}</td>
				<td>${totalFare }</td>
				<td>${totalHc}</td>
				<td>${totalHcFare}</td>
				<td></td>
				<td></td>
				<!-- <td></td>
				<td></td> -->
			</tr>
			</tfoot>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'balancePoolList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }' <c:if test="${i*10==numPerPage }">selected</c:if> >${i*10 }</option>
				</c:forEach>
			</select>
			<span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="balancePoolList" totalCount="${totalCount }" numPerPage="${numPerPage }"
			pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>
