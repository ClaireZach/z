<!-- 进出门时间段列表 -->
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript">
	function entranceTimeClick(tr) {
		$('#entranceTimeForm input').eq(0).val(tr.attr('entranceTimeId'));
		var tds = tr.find('td');
		$('#entranceTimeForm input').eq(1).val(tds.eq(1).find('div').html());
		$('#entranceTimeForm input').eq(2).val(tds.eq(2).find('div').html());
		$('#entranceTimeForm input').eq(3).val(tds.eq(4).find('div').html());
		var status = tr.attr('status');
		$('#entranceTimeForm input[name=status]').attr('checked', false);
		$('#entranceTimeForm input[name=status][value='+status+']').attr('checked', true);
	}

	$(function() {
		$('#entranceTimeList .item[enable=false]').addClass('red');
	});
</script>
<style type="text/css">
.red {
	color: red;
}

.hide {
	display: none;
}
</style>
<div id="entranceTimeList">
	<table class="table" width="99%" rel="jbsxBox" layoutH="90">
		<thead>
			<tr>
				<th width="10">序号</th>
				<th width="100">开始时间</th>
				<th width="100">结束时间</th>
				<th width="100">状态</th>
				<th width="100">备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list }" var="e" varStatus="status">
				<tr class="item" target="entranceTime" entranceTimeId="${e.id }" status="${e.status }">
					<td>${status.index+1}</td>
					<td>${e.beginTime }</td>
					<td>${e.endTime }</td>
					<td><c:if test="${e.status==1}">进门</c:if> <c:if test="${e.status==2}">出门</c:if></td>
					<td>${e.remark }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>