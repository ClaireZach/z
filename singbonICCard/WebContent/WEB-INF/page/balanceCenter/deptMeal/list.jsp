<!-- 部门就餐统计    by梁敏-->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	$(function(){
		$("#tablesOrder").tablesorter();
		$('.exportExcel').click(function(){
			$('#deptMealList #pagerForm input[name=export]').val(1);
			$('#deptMealList #pagerForm input[name=exportType]').val($(this).attr('exportType'));
			$('#deptMealList #pagerForm').submit();
			$('#deptMealList #pagerForm input[name=export]').val('');
			$('#deptMealList #pagerForm input[name=exportType]').val('');
		});
	});
	$(function(){
		$('#deptMealList .search').click(function(){
			var endDate = $('#endDate').val();
			var beginDate = $('#beginDate').val();
			if (beginDate != "" && endDate != "") {
				if (beginDate >= endDate) {
					alertMsg.error('查询开始时间不能大于查询结束时间');
				}
				else{
					divSearch($('#deptMealList #pagerForm'),'deptMealList');
						}
			}
			else{
				divSearch($('#deptMealList #pagerForm'),'deptMealList');
					}
		});			
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
			<br>
			<table border="0">
				<tr align="right">
					<td width="70">营业部门：</td>
					<td><input type="hidden" name="deptId" value="${deptId}" /> <input
						type="text" name="deptName" value="${deptName}" size="10"
						readonly="readonly" /></td>
					<td><a class="btnLook" width="300" maxable="false"
						resizable="true" title="请选择营业部门" href="/selectDeptTree.do"
						lookupgroup="district"></a></td>
					<td><input type="checkbox" name="includeSub"
						<c:if test="${includeSub=='on'}">checked</c:if> /> 不包含下级部门</td>
					<td width="70">消费日期：</td>
					<td align="left"><input type="text" name="beginDate"
						id="beginDate" value="${beginDate}" readonly="readonly"
						datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="18" /><a
						class="inputDateButton" href="javascript:;">选择</a></td>
					<td align="left">&nbsp;- <input type="text" name="endDate"
						id="endDate" value="${endDate}" readonly="readonly" class="date"
						datefmt="yyyy-MM-dd HH:mm:ss" size="18" /><a
						class="inputDateButton" href="javascript:;">选择</a>
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
					<td>(提示:查询时间间隔不足一天时,天均消费额和天均次数不计)</td>
				</tr>
			</table>
			<br>
		</div>
	</form>
</div>
<div class="pageContent"
	style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid">
	<table class="list" width="99%" layoutH="148" id="tablesOrder">
		<thead>
			<tr>
				<th width="50">序号</th>
				<th width="80">营业部门</th>
				<th width="70">消费总额</th>
				<th width="70">消费次数</th>
				<th width="70">宏冲总额</th>
				<th width="70">宏冲次数</th>
				<th width="70">早餐次数</th>
				<th width="70">午餐次数</th>
				<th width="70">晚餐次数</th>
				<th width="70">夜宵次数</th>
				<th width="70">加班次数</th>
				<th width="70">其他次数</th>
				<th width="70">总次数</th>
				<th width="70">总天数</th>
				<th width="70">天均消费额</th>
				<th width="80">天均消费次数</th>
				<th width="70">就餐率</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="ds" items="${limitList}" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${ds.deptName}</td>
					<td>${ds.totalFares}</td>
					<td>${ds.totalCounts}</td>
					<td>${ds.honChonFares}</td>
					<td>${ds.honChonCounts}</td>
					<td>${ds.breakfast}</td>
					<td>${ds.lunch}</td>
					<td>${ds.dinner}</td>
					<td>${ds.night}</td>
					<td>${ds.stayup}</td>
					<td>${ds.others}</td>
					<td>${ds.sumOpCounts}</td>
					<td>${ds.days}</td>
					<td>${ds.avgFare}</td>
					<td>${ds.avgCount}</td>
					<td>${ds.mealRate}</td>
				</tr>
			</c:forEach>
		<tfoot bgcolor="red">
			<td></td>
			<td>汇总</td>
			<td>${totalSum}</td>
			<td>${opCounts}</td>

			<td>${honchonFareSum}</td>
			<td>${honchonCount}</td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			

		</tfoot>

		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'deptMealList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="deptMealList" totalCount="${totalCount }"
			numPerPage="${numPerPage }" pageNumShown="10"
			currentPage="${pageNum }"></div>
	</div>
</div>


