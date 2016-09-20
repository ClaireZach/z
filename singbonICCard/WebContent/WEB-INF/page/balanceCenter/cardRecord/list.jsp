<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
$(function() {

	$("#tablesOrder").tablesorter();
	$("#cardRecordDetailList").tablesorter();
});
	$(function(){
		$('.exportExcel').click(function(){
			$('#cardRecordList #pagerForm input[name=export]').val(1);
			$('#cardRecordList #pagerForm input[name=exportType]').val($(this).attr('exportType'));
			$('#cardRecordList #pagerForm').submit();
			$('#cardRecordList #pagerForm input[name=export]').val('');
			$('#cardRecordList #pagerForm input[name=exportType]').val('');
		});
	});
	/* $(function(){
		$('#cardRecordList .search').click(function(){
			divSearch($('#cardRecordList #pagerForm'),'cardRecordList');
		});
	}); */
	$('#cardRecordList .search').click(
			function() {

				var endDate = $('#endDate').val();
				var beginDate = $('#beginDate').val();
				if (beginDate != "" && endDate != "") {
					if (beginDate >= endDate) {
						alertMsg.error('结束时间必须大于起始时间');
					} else {
						divSearch($('#cardRecordList #pagerForm'),
								'cardRecordList');
					}
				}
				else{
				divSearch($('#cardRecordList #pagerForm'),
						'cardRecordList');
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
					<td width="60">查询类型：</td>
					<td align="left">
						<select class="combox" outerw="80" innerw="120" name="queryType">
							<option value="0" width="120" <c:if test="${queryType==0}">selected="selected"</c:if>>统计查询</option>
							<option value="1" width="120" <c:if test="${queryType==1}">selected="selected"</c:if>>明细查询</option>
						</select>
					</td>
					<td width="60">出纳员：</td>
					<td align="left">
						<select class="combox" outerw="80" innerw="100" name="operId">
							<option value="0" width="100">全部</option>
							<c:forEach var="u" items="${sysUserList}" varStatus="status">
								<option value="${u.operId}" width="70" <c:if test="${u.operId==operId}">selected</c:if> >${u.loginName}</option>
							</c:forEach>
						</select>
					</td>
					<td width="70" class="recordType">记录类型：</td>
					<td align="left" class="recordType">
						<select class="combox" outerw="80" innerw="120" name="recordType">
							<option value="-1" width="120" <c:if test="${status.index==-1}">selected</c:if>>全部</option>
							<c:forEach var="r" items="${recordTypes}" varStatus="status">
								<option value="${status.index}" width="120" <c:if test="${status.index==recordType}">selected</c:if>>${r}</option>
							</c:forEach>
						</select>
					</td>	
					<td width="70">操作日期：</td>
					<td align="left">
						<input type="text" name="beginDate"  id="beginDate" value="${beginDate}" readonly="readonly" datefmt="yyyy-MM-dd HH:mm:ss" class="date" size="18"/><a class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left">
						&nbsp;- 
						<input type="text" name="endDate"  id="endDate" value="${endDate}" readonly="readonly" class="date" datefmt="yyyy-MM-dd HH:mm:ss"  size="18"/><a class="inputDateButton" href="javascript:;">选择</a>
					</td>
					<td align="left">
						<input type="checkbox" name="includeOff" <c:if test="${includeOff=='on'}">checked</c:if> />
						包含注销用户
					</td>
				</tr>
				<tr>
					<td colspan="12">
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
	<c:if test="${queryType==0}">
		<table class="list" width="112%" layoutH="120"  id="tablesOrder">
			<thead>
				<tr>
					<th width="40">序号</th>
					<th width="80">出纳员</th>
					<th width="80">发卡金额</th>
					<th width="60">PC存款</th>
					<th width="100">发卡赠送金额</th>
					<th width="100">存款赠送金额</th>
					<th width="80">收取卡押金</th>
					<th width="120">消费机补助存款</th>
					<th width="100">水控补助存款</th>
					<th width="60">PC取款</th>
					<th width="100">退还卡押金</th>
					<th width="120">消费机补助清零</th>
					<th width="100">水控补助清零</th>
					<th width="80">发卡</th>
					<th width="60">补卡</th>
					<th width="60">挂失</th>
					<th width="60">解挂</th>
					<th width="60">卡注销</th>
					<th width="80">按卡修正</th>
					<th width="80">按库修正</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="c" items="${list}" varStatus="status">
					<tr>
						<td>${status.index+1}</td>  	     
						<td>${c.loginName}</td>		<!-- 出纳员 -->
						<td>${c.makeCardFare}</td>	<!-- 发卡金额 -->
						<td>${c.PCSaving}</td>		<!-- PC存款 -->
						<td>${c.makeCardGiveFare}</td>	<!-- 发卡赠送金额	 -->
						<td>${c.PCSavingGiveFare}</td>	<!-- 存款赠送金额	 -->
						<td>${c.getCardDeposit}</td>	<!-- 收取卡押金 -->
						<td>${c.posSubsidySaving}</td>		<!-- 消费机补助存款 -->
						<td>${c.waterSubsidySaving}</td>	<!-- 水控补助存款 -->
						<td>${c.PCTake}</td>	<!-- PC取款 -->
						<td>${c.backCardDepostFare}</td>	<!-- 退还卡押金 -->	
						<td>${c.posSubsidyClear}</td>	<!-- 消费机补助清零 -->
						<td>${c.waterSubsidyClear}</td>	<!-- 水控补助清零 -->
						<td>${c.makeCardCount}</td>		<!-- 发卡 -->
						<td>${c.remakeCard}</td>   <!-- 补卡 -->
						<td>${c.loss}</td>     <!-- 挂失 -->
						<td>${c.unloss}</td>     <!-- 解挂 -->
						<td>${c.cardOff}</td>        <!-- 卡注销 -->
						<td>${c.updateByCard}</td>     <!-- 按卡修正 -->
						<td>${c.updateByUserInfo}</td>  <!-- 按库修正 -->
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr bgcolor="#DFDFDF">
				<td></td>
				<td align="center"><b>全部汇总</b></td>
				<td>${totalmakeCardFare}</td>
				<td>${totalPCSaving}</td>
				<td>${totalmakeCardGiveFare}</td>
				<td>${totalPCSavingGiveFare}</td>
				<td>${totalgetCardDeposit}</td>
				<td>${totalposSubsidySaving}</td>
				<td>${totalwaterSubsidySaving}</td>
				<td>${totalPCTake}</td>
				<td>${totalbackCardDepostFare}</td>
				<td>${totalposSubsidyClear}</td>
				<td>${totalwaterSubsidyClear}</td>
				<td>${totalmakeCardCount}</td>
				<td>${totalremakeCard}</td>
				<td>${totalloss}</td>
				<td>${totalunloss}</td>
				<td>${totalcardOff}</td>
				<td>${totalupdateByCard}</td>
				<td>${totalupdateByUserInfo}</td>
			</tr>
			</tfoot>
		</table>
	</c:if>
	<c:if test="${queryType==1}">
		<table class="list" id="cardRecordDetailList" width="105%" layoutH="148">
			<thead>
				<tr>
					<th width="40">序号</th>
					<th width="100">出纳员姓名</th>
					<th width="100">姓名</th>
					<th width="100">编号</th>
					<th width="100">卡号</th>
					<th width="100">物理卡号</th>
					<th width="140">操作类型</th>
					<th width="100">操作额</th>
					<th width="100">库大钱包</th>
					<th width="110">库补助钱包</th>
					<th width="100">卡大钱包</th>
					<th width="110">卡补助钱包</th>
					<th width="100">库计数器</th>
					<th width="130">库补助计数器</th>
					<th width="100">卡计数器</th>
					<th width="130">卡补助计数器</th>
					<th width="180">操作时间</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="c" items="${list}" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td>${c.loginName}</td> 
						<td>${c.username}</td>
						<td>${c.userNO}</td>
						<td>${c.cardNO}</td>
						<td>${c.cardSN}</td>
						<td>${c.recordTypeDes}</td> 
						<td>${c.opFare}</td>
						<td>${c.oddFare}</td>
						<td>${c.subsidyOddFare}</td>
						<td>${c.cardOddFare}</td>
						<td>${c.cardSubsidyOddFare}</td>
						<td>${c.opCount}</td>
						<td>${c.subsidyOpCount}</td>
						<td>${c.cardOpCount}</td>
						<td>${c.cardSubsidyOpCount}</td>
						<td>${c.opTime}</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr bgcolor="#DFDFDF">
				<td></td>
				<td colspan="2" align="center"><b>本页汇总</b></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td>${sumopFare}</td>
				<td>${sumoddFare}</td>
				<td>${sumsubsidyOddFare}</td>
				<td>${sumcardOddFare }</td>
				<td>${sumcardSubsidyOddFare}</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			
			<tr bgcolor="#DFDFDF">
				<td></td>
				<td colspan="2" align="center"><b>全部汇总</b></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td>${sumopFareAll}</td>
				<td>${sumoddFareAll}</td>
				<td>${sumsubsidyOddFareAll}</td>
				<td>${sumcardOddFareAll }</td>
				<td>${sumcardSubsidyOddFareAll}</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			
		</tfoot>
		</table>
		<div class="panelBar">
			<div class="pages">
				<span>每页显示</span> <select class="combox" name="numPerPage"
					onchange="navTabPageBreak({numPerPage:this.value}, 'cardRecordList')">
					<c:forEach begin="1" end="20" var="i" step="1">
						<option value='${i*10}' <c:if test="${i*10==numPerPage }">selected</c:if> >${i*10}</option>
					</c:forEach>
				</select>
				<span>条，共${totalCount}条</span>
			</div>
	
			<div class="pagination" rel="cardRecordList" totalCount="${totalCount }" numPerPage="${numPerPage }"
				pageNumShown="10" currentPage="${pageNum }"></div>
		</div>
	</c:if>
</div>
