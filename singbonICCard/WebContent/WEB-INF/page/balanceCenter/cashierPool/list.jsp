<!--出纳汇总  根据结算结果 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">

$(function() {

	$("#tablesOrder").tablesorter();
});
	$(function(){
		$('.exportExcel').click(function(){
			$('#cashierpool #pagerForm input[name=export]').val(1);
			$('#cashierpool #pagerForm input[name=exportType]').val($(this).attr('exportType'));
			$('#cashierpool #pagerForm').submit();
			$('#cashierpool #pagerForm input[name=export]').val('');
			$('#cashierpool #pagerForm input[name=exportType]').val('');
		});
	});
	/*  $(function(){
		$('#cashierpool .search').click(function(){
			divSearch($('#cashierpool #pagerForm'),'cashierpool');
		});
	}); */
	 $(function(){
		$('#cashierpool .search').click(function(){
			
			var beginDate = $('#beginDate').val();
			var endDate = $('#endDate').val();
			if (beginDate != "" && endDate != ""&& beginDate !=-1 && endDate !=-1) {
				if (beginDate >= endDate) {
					alertMsg.error('查询开始时间不能大于查询结束时间');
				}
				else{
					divSearch($('#cashierpool #pagerForm'),'cashierpool');
						}
			}
			else{
				divSearch($('#cashierpool #pagerForm'),'cashierpool');
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
								<option value="${u.operId}" width="70" <c:if test="${u.operId==operId}">selected</c:if> >${u.loginName}</option>
							</c:forEach>
						</select>
					</td>
					<td width="70" class="recordType">记录类型：</td>
					<td align="left" class="recordType">
						<select class="" outerw="80" innerw="120" name="recordType">
						<%--<option value="-1" width="120" <c:if test="${recordType==-1}">selected</c:if>>全部</option>
							<c:forEach var="r" items="${recordTypes}" varStatus="status">
								<option value="${status.index}" width="120" <c:if test="${status.index==recordType}">selected</c:if>>${r}</option>
							</c:forEach> --%>
							<%-- <option value="-1" width="100" <c:if test="${recordType==-1}">selected</c:if>>全部</option>
							<option value="0" width="100" <c:if test="${recordType==0}">selected</c:if>>发卡</option>
							<option value="1" width="100" <c:if test="${recordType==1}">selected</c:if>>收取卡押金</option>
							<option value="2" width="100" <c:if test="${recordType==2}">selected</c:if>>发卡赠送金额</option>
							<option value="3" width="100" <c:if test="${recordType==3}">selected</c:if>>补卡</option>
							<option value="16" width="100" <c:if test="${recordType==16}">selected</c:if>>按库修正</option>
							<option value="4" width="100" <c:if test="${recordType==4}">selected</c:if>>挂失</option>
							<option value="5" width="100" <c:if test="${recordType==5}">selected</c:if>>解挂</option>
							<option value="6" width="100" <c:if test="${recordType==6}">selected</c:if>>PC存款</option>
							<option value="8" width="100" <c:if test="${recordType==8}">selected</c:if>>PC取款</option>
							<option value="7" width="100" <c:if test="${recordType==7}">selected</c:if>>存款赠送金额</option>
							
							<option value="9" width="100" <c:if test="${recordType==8}">selected</c:if>>退还卡押金</option>
							<option value="10" width="100" <c:if test="${recordType==10}">selected</c:if>>消费机补助存款</option>
							<option value="11" width="100" <c:if test="${recordType==11}">selected</c:if>>消费机补助清零</option>
							<option value="12" width="100" <c:if test="${recordType==12}">selected</c:if>>水控补助存款</option>
							<option value="13" width="100" <c:if test="${recordType==13}">selected</c:if>>水控补助清零</option>
							<option value="14" width="100" <c:if test="${recordType==14}">selected</c:if>>卡注销</option>
							<option value="15" width="100" <c:if test="${recordType==15}">selected</c:if>>按卡修正</option> --%>
							
							<option value="-1" width="100" <c:if test="${recordType==-1}">selected</c:if>>全部</option>
							<option value="0" width="100" <c:if test="${recordType==0}">selected</c:if>>发卡</option>
							<option value="6" width="100" <c:if test="${recordType==6}">selected</c:if>>PC存款</option>
							<option value="8" width="100" <c:if test="${recordType==8}">selected</c:if>>PC取款</option>
							<option value="1" width="100" <c:if test="${recordType==1}">selected</c:if>>收取卡押金</option>
							<option value="9" width="100" <c:if test="${recordType==8}">selected</c:if>>退还卡押金</option>
							<option value="2" width="100" <c:if test="${recordType==2}">selected</c:if>>发卡赠送金额</option>
							<option value="7" width="100" <c:if test="${recordType==7}">selected</c:if>>存款赠送金额</option>
							
							
						</select>
					</td>	
				
					<td style="width: auto;">
						结算历史：
					</td>
					
					<td align="left" style="padding-right: 10px">
					
						<!-- <select class="combox" outerw="140" innerw="140" name="beginDate"> -->
						<select name="beginDate" id="beginDate">
							<option value="-1" width="150">全部</option>
							<c:forEach var="u" items="${TiemList}" varStatus="status">
								<option value="${u.beginDate}" width="70" <c:if test="${u.beginDate==beginDate}">selected</c:if> >${u.beginDate}</option>
							</c:forEach>
						</select>
						<!-- </td>
						<td>--</td>
						<td> -->
						<!-- <select class="combox" outerw="140" innerw="140" name="endDate"> -->
						<select  name="endDate" id="endDate">
							<option value="-1" width="150">全部</option>
							<c:forEach var="u" items="${TiemList}" varStatus="status">
								<option value="${u.endDate}" width="70" <c:if test="${u.endDate==endDate}">selected</c:if> >${u.endDate}</option>
							</c:forEach>
						</select>
						</div>
					</td>
					<td  style="padding-right: 10px">
						<input type="checkbox" name="offgiveFare" <c:if test="${offgiveFare=='on'}">checked</c:if> />
						不计算赠送金额
					</td>
					<!-- </tr><tr>	 -->			
					<td>
						<div class="buttonActive">
							<div class="buttonContent">
								<!-- <button type="button" class="search"> &nbsp; &nbsp; 查&nbsp;&nbsp;询 &nbsp; &nbsp; </button> -->
								<button type="button" class="search">点击查询</button>
							</div>
						</div>
					</td>
					<td>
						<div class="buttonActive"  style="margin: 0 10px;">
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
					<!-- <td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td> -->
					<%-- <td align="left">
						<input type="checkbox" name="includeOff" <c:if test="${includeOff=='on'}">checked</c:if> />
						包含注销用户
					</td> --%>
				</tr>
			</table>
		</div>
	</form>
</div>
<div class="pageContent"
	style="border-left: 1px #B8D0D6 solid; border-right: 1px #B8D0D6 solid">
		<table class="list" id="tablesOrder" width="105%" layoutH="148"> <!--  asc=”asc” desc=”desc” -->
			<thead>
				<tr>
					<th style="width: auto;">序号</th>
					<th style="width: auto;">出纳员姓名</th>
					<th style="width: auto;">出纳员编号</th>
					<th style="width: auto;">使用的读卡机</th>
					<th style="width: auto;">操作类型</th>
					<th style="width: auto;">操作次数</th>
					<th style="width: auto;">操作金额</th>
					<th style="width: auto;">结算开始时间</th>
					<th style="width: auto;">结算结束时间</th>
					<!-- <th style="width: auto;">查询开始时间</th>
					<th style="width: auto;">查询结束时间</th> -->
				</tr>
			</thead>
			<tbody>
				<c:forEach var="c" items="${list}" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td>${c.loginName}</td> 
						<td>${c.operId}</td>
						<td>${c.deviceName}</td>
						<td>${c.recordTypeDes}</td>
						<td>${c.opCount}</td>
						<td>${c.opFare}</td>
						<td>${c.beginDate}</td> 
						<td>${c.endDate}</td>
						<%-- <td>${beginDate}</td> 
						<td>${endDate}</td> --%>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
			<tr  bgcolor="#DFDFDF">
				<td></td>
				<td colspan="2" align="center"><b>&nbsp;&nbsp;汇&nbsp;&nbsp;&nbsp;&nbsp;总&nbsp;&nbsp;</b></td>
				<td></td>
				<td></td>
				
				<td>${totalOpCount}</td>
				<td>${totalOpFare1}</td>
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
					onchange="navTabPageBreak({numPerPage:this.value}, 'cashierpool')">
					<c:forEach begin="1" end="20" var="i" step="1">
						<option value='${i*10 }' <c:if test="${i*10==numPerPage }">selected</c:if> >${i*10 }</option>
					</c:forEach>
				</select>
				<span>条，共${totalCount}条</span>
			</div>
	
			<div class="pagination" rel="cashierpool" totalCount="${totalCount }" numPerPage="${numPerPage }"
				pageNumShown="10" currentPage="${pageNum }"></div>
		</div>
</div>
