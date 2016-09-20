<!-- 结算记录  陈梦  -->
<%@page import="javax.sound.midi.Patch"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript">
	$(function() {
		$("#tablesOrder").tablesorter();
		$('.exportExcel').click(
				function() {
					$('#balanceRecordList #pagerForm input[name=export]')
							.val(1);
					$('#balanceRecordList #pagerForm input[name=exportType]')
							.val($(this).attr('exportType'));
					$('#balanceRecordList #pagerForm').submit();
					$('#balanceRecordList #pagerForm input[name=export]').val(
							'');
					$('#balanceRecordList #pagerForm input[name=exportType]')
							.val('');
				});//exportExcel Click END
		$('.search').click(function() {
			divSearch($('#balanceRecordList #pagerForm'), 'balanceRecordList');
		});//search Click END
		//结算
		$('#balance :button')
				.click(
						function() {
							var date = new Date();
							var nowDate = date.pattern("yyyy-MM-dd HH:mm:ss");
							var endDate = $('#endDate').val();
							var beginDate = $('#beginDate').val();
							if (endDate == null || endDate == '') {
								alertMsg.error("请先选择结算日期");
							} else if (beginDate != null && endDate != null) {
								if (beginDate >= endDate) {
									alertMsg.error('结算时间必须大于起始时间');
								} else if (endDate >= nowDate) {
									alertMsg.error('结算时间不能大于当前时间');
								} else {
									var form = $('form');
									$
											.post(
													'${base}/insert.do?beginDate=${beginDate}',
													form.serialize(),
													function(e) {
														if (e == 1) {
														} else {
															divSearch(
																	$('#balanceRecordList #pagerForm'),
																	'balanceRecordList');
															alertMsg
																	.correct('结算完成');
														}
													});
								}
							}
						});
	});
	//******************************
	Date.prototype.pattern = function(fmt) {
		var o = {
			"M+" : this.getMonth() + 1, //月份
			"d+" : this.getDate(), //日
			"h+" : this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
			"H+" : this.getHours(), //小时
			"m+" : this.getMinutes(), //分
			"s+" : this.getSeconds(), //秒
			"q+" : Math.floor((this.getMonth() + 3) / 3), //季度
			"S" : this.getMilliseconds()
		};//毫秒
		var week = {
			"0" : "/u65e5",
			"1" : "/u4e00",
			"2" : "/u4e8c",
			"3" : "/u4e09",
			"4" : "/u56db",
			"5" : "/u4e94",
			"6" : "/u516d"
		};
		if (/(y+)/.test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		}
		if (/(E+)/.test(fmt)) {
			fmt = fmt
					.replace(
							RegExp.$1,
							((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "/u661f/u671f"
									: "/u5468")
									: "")
									+ week[this.getDay() + ""]);
		}
		for ( var k in o) {
			if (new RegExp("(" + k + ")").test(fmt)) {
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
						: (("00" + o[k]).substr(("" + o[k]).length)));
			}
		}
		return fmt;
	}
	//******************************
</script>
<div style="border: 0px #B8D0D6 solid" id="balance">
	<form method="post">
		<table border="0">
			<tr>
				<td><p>选择结算日期：</p></td>
				<td align="left" width="120"><input type="text"
					name="beginDate" id="beginDate" value="${beginDate}"
					readonly="readonly" datefmt="yyyy-MM-dd HH:mm:ss" class="date"
					size="15" disabled="disabled" /> <!-- <a class="inputDateButton" href="javascript:;" style="float: right; margin-left: 0" readonly="readonly">选择</a> -->
				</td>
				<td><p>-</p></td>
				<td align="left" width="120">
					<!-- 选择结算日期 : --> <input type="text" name="endDate" id="endDate"
					value="${endDate}" readonly="readonly" class="date"
					datefmt="yyyy-MM-dd HH:mm:ss" size="15" placeholder="点击选择日期" /> <!-- <a class="inputDateButton" href="javascript:;" style="float: right; margin-left: 0">选择</a> -->
				</td>
				<td>
					<!-- <input type="button" id="submit" value="结算" /> -->
					<div class="buttonActive" style="margin-left: 5px">
						<div class="buttonContent">
							<button type="button" id="submit">点击结算</button>
						</div>
					</div>
				</td>

				<td>
					<%-- <div class="buttonActive" style="margin-left: 5px">
						<div class="buttonContent">
							<button type="button"><a title="自动结算" href="${base}/autoBalance.do" lookupgroup="district">自动结算</a></button>
						</div>
					</div> --%>
				</td>
				<td><p style="margin-left: 30px">默认显示最近一次结算结果<p></td>
				
			</tr>
		</table>
	</form>
</div>
<div style="border: 0px #B8D0D6 solid">
	<form action="${base}/list.do" id="pagerForm" method="post">
		<input type="hidden" name="export" /> <input type="hidden"
			name="exportType" /> <input type="hidden" name="pageNum"
			value="${pageNum}" /> <input type="hidden" name="numPerPage"
			value="${numPerPage}" /> <input type="hidden" name="totalCount"
			value="${totalCount}" /> <input type="hidden" name="orderField"
			value="${param.orderField}" /> <input type="hidden"
			name="orderDirection" value="${param.orderDirection}" />
		<table border="0">
			<tr>
				<td><p>查询结算历史：</p></td>
				<td align="left" width="120">
					<!-- <select id="beginTime" outerw="80" innerw="100" name="beginTime"> -->
					<select id="beginTime" name="beginTime" style="width: 122px">
						<option value="-1">全部</option>
						<c:forEach items="${beginDateList }" var="m" varStatus="status">
							<option value="${m.beginTime}" id="beginOption"
								<c:if test="${m.beginTime==beginTime}">selected</c:if>>${m.beginTime }</option>
						</c:forEach>
				</select>
				</td>
				<td><p>-</p></td>
				<td align="left" width="120">
					<!-- <select outerw="80" innerw="100" name="endTime"> --> <select
					name="endTime" style="width: 122px">
						<option value="-1">全部</option>
						<c:forEach items="${endDateList }" var="m">
							<option value="${m.endTime}"
								<c:if test="${m.endTime==endTime}">selected</c:if>>${m.endTime }</option>
						</c:forEach>
				</select>
				</td>
				<td>
					<div class="buttonActive" style="margin-left: 5px">
						<div class="buttonContent">
							<button type="button" class="search">结算历史</button>
						</div>
					</div>
				</td>
				<td>
					<div class="buttonActive" style="margin: 0 5px;">
						<div class="buttonContent">
							<button type="button" class="exportExcel" exportType="0">导出当前</button>
						</div>
					</div>
				</td>
				<td>
					<div class="buttonActive">
						<div class="buttonContent">
							<button type="button" class="exportExcel" exportType="1">导出全部</button>
						</div>
					</div>
				</td>

				<!-- <td>
				</td> -->
			</tr>
		</table>
	</form>
</div>
<div class="pageContent"
	style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid">
	<table class="list" width="99%" layoutH="148" id="tablesOrder">
		<thead>
			<tr>
				<th width="auto;">序号</th>
				<th width="auto;" orderFiled="deptName">部门或操作员</th>
				<th width="auto;" orderFiled="totalOpCount">操作次数</th>
				<th width="auto;" orderFiled="totalOpFare">操作金额</th>
				<th width="auto;" orderFiled="honchonCount">宏冲次数</th>
				<th width="auto;" orderFiled="honchonFare">宏冲金额</th>
				<th width="auto;" orderFiled="recordTypes">操作类型</th>
				<th width="auto;" orderFiled="beginDate">开始时间</th>
				<th width="auto;" orderFiled="endDate">结束时间</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach var="d" items="${list}" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${d.deptName}</td>
					<td>${d.totalOpCount}</td>
					<td>${d.totalOpFare}</td>
					<td>${d.honchonCount}</td>
					<td>${d.honchonFare}</td>
					<td>${d.recordTypes}</td>
					<td>${d.beginDate}</td>
					<td>${d.endDate}</td>

				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="panelBar">
		<div class="pages">
			<span>每页显示</span> <select class="combox" name="numPerPage"
				onchange="navTabPageBreak({numPerPage:this.value}, 'balanceRecordList')">
				<c:forEach begin="1" end="20" var="i" step="1">
					<option value='${i*10 }'
						<c:if test="${i*10==numPerPage }">selected</c:if>>${i*10 }</option>
				</c:forEach>
			</select> <span>条，共${totalCount}条</span>
		</div>

		<div class="pagination" rel="balanceRecordList"
			totalCount="${totalCount }" numPerPage="${numPerPage }"
			pageNumShown="10" currentPage="${pageNum }"></div>
	</div>
</div>



