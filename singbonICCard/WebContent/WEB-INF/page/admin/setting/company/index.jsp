<!-- 单位设置 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<script type="text/javascript">
	$(function() {
		$('#companyForm .add').click(function() {
			$('#companyForm input').eq(0).val('');
			var form = $(this).parents('form');
			validateCallback(form, function(e) {
				if (e == 1) {
					alertMsg.correct('添加成功！');					
					form.clearForm();
					refreshcompanyList();
				}else if (e == 2) {
					alertMsg.correct('该单位名称已存在！');
				} else {
					alertMsg.error('添加失败！');
				}
			}, null);
		});

		$('#companyForm .edit').click(function() {
			var form = $(this).parents('form');
			validateCallback(form, function(e) {
				if (e == 1) {
					alertMsg.correct('修改成功！');					
					refreshcompanyList();
				}else if (e == 2) {
					alertMsg.correct('该单位名称已存在！');
				} else {
					alertMsg.error('修改失败！');
				}
			}, null, false);
		});
		$('#companyForm .delete').click(function(){
			var form = $(this).parents('form');
			var id = $('#companyForm input[name=id]').val();
			if(id==''){
				alertMsg.error('请选择一个单位！');				
			}else{
				var companyName = $('#companyForm input[name=companyName]').val();
				alertMsg.confirm('确定要删除'+companyName+'吗？', {
					okCall : function() {
						$.post('${base }/delete.do?id=' + id,function(e){
							if (e == 1) {
								alertMsg.correct('删除成功！');
								refreshcompanyList();
							} else {
								alertMsg.warn('删除失败！');
							} 
						});
					}
				});
			}
		});
	});

	function refreshcompanyList() {
		divSearch($('#companyList #pagerForm'), 'companyList');
	}
	//选择单位
	function companyClick(tr) {
		$('#companyForm input').eq(0).val(tr.attr('id'));
		$('#companyForm input').eq(1).val(tr.attr('companyName'));
		$('#companyForm input').eq(2).val(tr.attr('serialNumber'));
		$('#companyForm input').eq(3).val(tr.attr('authNumber'));
		$('#companyForm input').eq(4).val(tr.attr('invalidDate'));
		$('#companyForm input').eq(5).val(tr.attr('appId'));
		$('#companyForm input').eq(6).val(tr.attr('mchId'));
		$('#companyForm input').eq(7).val(tr.attr('mchKey'));
		$('#companyForm input').eq(14).val(tr.attr('weixinLimit'));
		$('#companyForm input').eq(15).val(tr.attr('remainingWeixin'));
		$('#companyForm input').eq(16).val(tr.attr('shortLimit'));
		$('#companyForm input').eq(17).val(tr.attr('remainingShort'));
		
		$('#companyForm input:radio').attr('checked', false);
		var allowEntranceMessage=tr.attr('allowEntranceMessage');
		$('#companyForm input[name=allowEntranceMessage][value='+allowEntranceMessage+']').attr('checked', true);
		var allowPCSavingMessage=tr.attr('allowPCSavingMessage');
		$('#companyForm input[name=allowPCSavingMessage][value='+allowPCSavingMessage+']').attr('checked', true);
		var allowWeixinSavingMessage=tr.attr('allowWeixinSavingMessage');
		$('#companyForm input[name=allowWeixinSavingMessage][value='+allowWeixinSavingMessage+']').attr('checked', true);
		
		var select = $('#companyForm select');
		select.prev().val(tr.attr('baseSection')).html($('option[value=' + tr.attr('baseSection') + ']', select).html());
		
		var enable = tr.attr('enable');
		if(enable=='false'){
			$('#companyForm input[name=status]').attr('checked',false);
		}else{
			$('#companyForm input[name=status]').attr('checked',true);			
		}
	};
</script>
<link href="/themes/css/custom.css" rel="stylesheet" type="text/css"/>
<style type="text/css">
	#companyForm input {
		width: 150px;
	}
	#companyForm dl{
		width: 260px;
	}
	#companyForm dt{
		width: 80px;
	}
	#companyForm .radio{
		width: 15px;
	}
</style>
<div class="form" layoutH="30"
	style="float: left; display: block; overflow: auto; width: 280px; border: solid 1px #CCC; line-height: 21px; background: #fff">
<!-- 	<div layoutH="1019"></div> -->
	<form id="companyForm" method="post" action="${base}/addEdit.do"
		class="pageForm required-validate">
		<div class="pageFormContent">
			<dl style="margin: 10px 0;">
				<dt>单位名称：</dt>
				<dd>
					<input type="hidden" name="id" /> 
					<input type="text" name="companyName" maxlength="20" class="required" />
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>序列号：</dt>
				<dd>
					<input type="text" name="serialNumber" maxlength="20" class="required digits"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>授权号：</dt>
				<dd>
					<input type="text" name="authNumber" maxlength="20" class="required digits"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>扇区号：</dt>
				<select class="combox" name=baseSection outerw="128" innerw="145">
					<c:forEach var="i" begin="1" end="13" step="1">
						<option value="${i}">${i}</option>
					</c:forEach>
				</select>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>试用日期：</dt>
				<dd>
					<input type="text" name="invalidDate" maxlength="20" class="date"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>公众号appId：</dt>
				<dd>
					<input type="text" name="appId"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>支付商户号：</dt>
				<dd>
					<input type="text" name="mchId"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>支付秘钥：</dt>
				<dd>
					<input type="text" name="mchKey"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>进出门提醒：</dt>
				<dd>
					<input type="radio" name="allowEntranceMessage" class="radio" checked="checked" value="1"/>是
					<input type="radio" name="allowEntranceMessage" class="radio" checked="checked" value="0"/>否
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>PC存款提醒：</dt>
				<dd>
					<input type="radio" name="allowPCSavingMessage" class="radio" checked="checked" value="1"/>是
					<input type="radio" name="allowPCSavingMessage" class="radio" checked="checked" value="0"/>否
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>微信充值提醒：</dt>
				<dd>
					<input type="radio" name="allowWeixinSavingMessage" class="radio" checked="checked" value="1"/>是
					<input type="radio" name="allowWeixinSavingMessage" class="radio" checked="checked" value="0"/>否
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>天微信限次：</dt>
				<dd>
					<input type="text" name="weixinLimit" class="required digits" value="0"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>天微信剩余：</dt>
				<dd>
					<input type="text" name="remainingWeixin" class="required digits" value="0"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>天短信限次：</dt>
				<dd>
					<input type="text" name="shortLimit" class="required digits" value="0"/>
				</dd>
			</dl>
			<dl style="margin: 10px 0;">
				<dt>天短信剩余：</dt>
				<dd>
					<input type="text" name="remainingShort" class="required digits" value="0"/>
				</dd>
			</dl>
			<dl style="margin: 0 0 8px 0;">
				<dt>&nbsp;</dt>
				<dd>
					<input type="checkbox" name="status" checked="checked" style="width: 13px;" />是否启用
				</dd>
			</dl>
		</div>
		<div class="formBar">
			<div class="panelBar" style="border-style: none;">
				<ul class="toolBar">
					<li><a class="add" href="javascript:;"><span>添加</span></a></li>
					<li><a class="edit" href="javascript:;"><span>修改</span></a></li>
<!-- 					<li><a class="delete" href="javascript:;"><span>删除</span></a></li> -->
				</ul>
			</div>
		</div>
	</form>
</div>

<div id="companyList" class="unitBox" style="margin-left: 280px;">
	<jsp:include page="${base}/list.do?pageNum=1&numPerPage=50" />
</div>
