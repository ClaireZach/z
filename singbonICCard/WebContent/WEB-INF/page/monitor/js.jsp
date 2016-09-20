<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="/js/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="/js/jquery.validate.js" type="text/javascript"></script>
<script src="/js/dwz.core.js" type="text/javascript"></script>
<script src="/js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="/js/dwz.drag.js" type="text/javascript"></script>
<script src="/js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="/js/dwz.checkbox.js" type="text/javascript"></script>
<script src="/js/dwz.core.js" type="text/javascript"></script>
<script src="/js/dwz.barDrag.js" type="text/javascript"></script>
<script src="/js/dwz.tree.js" type="text/javascript"></script>
<script src="/js/dwz.ui.js" type="text/javascript"></script>
<script src="/js/dwz.navTab.js" type="text/javascript"></script>
<script src="/js/dwz.tab.js" type="text/javascript"></script>
<script src="/js/dwz.cssTable.js" type="text/javascript"></script>
<script src="/js/dwz.stable.js" type="text/javascript"></script>
<script src="/js/dwz.ajax.js" type="text/javascript"></script>
<script src="/js/dwz.pagination.js" type="text/javascript"></script>
<script src="/js/dwz.panel.js" type="text/javascript"></script>
<script src="/js/comet4j.js" type="text/javascript"></script>
<script src="/js/jquery.contextmenu.r2.js" type="text/javascript"></script>
<script src="/js/jquery.timers.js" type="text/javascript"></script>
<script src="/js/map.js" type="text/javascript"></script>
<script type="text/javascript">
	var mode=0;
	var maxRow=100;
	$(function() {
	// window.moveTo(0, 0);
	// window.resizeTo(screen.availWidth, screen.availHeight);
		DWZ.init('/dwz.frag.xml', {
			loginUrl : 'login_dialog.html',
			loginTitle : '登录',
			statusCode : {
				ok : 200,
				error : 300,
				timeout : 301
			},
			pageInfo : {
				pageNum : 'pageNum',
				numPerPage : 'numPerPage',
				orderField : 'orderField',
				orderDirection : 'orderDirection'
			}, // 【可选】
			debug : false, // 调试模式 【true|false】
			callback : function() {
				initEnv();
			}
		});
		DWZ.ui.sbar=false;
		init();
		
		// 初始添加在线主动udp消费机设备和中转通信器
		<c:forEach items="${posUdpDeviceList }" var="d">
			<c:if test="${d.isOnline==1}">
				map.put('${d.sn}',new Date());
			</c:if>
		</c:forEach>
		heart();
		
		<c:if test="${mode==0}">
			$('#deviceList img').contextMenu('menu',monitorOps);
			$('#deptTreeLi a').contextMenu('menu',monitorOps);
			$('.deptTree a').contextMenu('menu',monitorOps);
		</c:if>
		<c:if test="${mode==1}">
		   $('#deviceList img').contextMenu('upgradeMenu',upgradeOps);
		   $('#deptTreeLi a').contextMenu('upgradeMenu',upgradeOps);
		   $('.deptTree a').contextMenu('upgradeMenu',upgradeOps);
		   mode=1;
		</c:if>
		var deptTree=$('.deptTree');
		var list = $('#deptTreeLi li');
		if(list.length!=0){
			$('li', deptTree).append("<ul class='expand'></ul>");
		}
		list.each(function() {
			var $this = $(this);
			var parentId = $this.attr('parentId');		
			if (parentId == '0') {
				$('.expand', deptTree).append($this);
			} else {
				var $li = $('li[deptId=' + parentId + ']', deptTree);
				if ($('>ul', $li).size() == 0) {
					$('<ul></ul>', deptTree).appendTo($li);
				}
				$('>ul', $li).append($(this));
			}
		});
	});
	
	var map = new Map();
	
	var logIndex=1;
	var consumeRecordIndex=1;
	var cookbookconsumeRecordIndex=1;
	function init() {
		JS.Engine.stop();
		JS.Engine.start('/conn');
		JS.Engine.on({
			'Co${company.id}' : function(e) {// 侦听一个channel
				var e2 = eval('(' + e + ')');
				var sn=e2.sn;
				var transferId=e2.transferId;
				// 非中转消费机
				if(transferId==0){
					map.put(sn,new Date());
				}
			    // 状态
				if(e2.type=='s'){
					$('#deviceList .device[name='+sn+'] img').attr('alt','在线').attr('src','/img/online.png');
					var statusTr=$('#deviceStatusList tr.deviceStatus[id='+sn+']');
					if(statusTr.length!=0){
	 					$('td[recordCount] div',statusTr).html(e2.a);
	// $('td[batchCount] div',statusTr).html(e2.batchCount);
	// $('td[blackCount] div',statusTr).html(e2.blackCount);
						$('td[subsidyVersion] div',statusTr).html(e2.b);
						$('td[subsidyAuth] div',statusTr).html(e2.c==1?'是':'否');
					}
				// 消费记录
				}else if(e2.type=='cr'){
					$('#deviceList .device[id='+sn+'] img').attr('alt','在线').attr('src','/img/online.png');
					if(consumeRecordIndex>=maxRow){
						consumeRecordIndex=1;
						$('#consumeRecord tbody tr td div').empty();
					}
					var tr=$('#consumeRecord tbody tr[index='+consumeRecordIndex+']');					
					$('td[index] div',tr).html(consumeRecordIndex);
					$('td[deviceName] div',tr).html(e2.a);
					$('td[userNO] div',tr).html(e2.b);
					$('td[cardNO] div',tr).html(e2.c);
					$('td[username] div',tr).html(e2.d);
					$('td[oddFare] div',tr).html(e2.e);
					$('td[subsidyOddFare] div',tr).html(e2.f);
					$('td[discountFare] div',tr).html(e2.g);
					$('td[opFare] div',tr).html(e2.h);
					$('td[mealName] div',tr).html(e2.i);
					$('td[opTime] div',tr).html(e2.j);
					$('td[opCount] div',tr).html(e2.k);
					$('td[subsidyOpCount] div',tr).html(e2.l);
					$('td[recordNO] div',tr).html(e2.m);
					$('td[consumeTypeDes] div',tr).html(e2.n);
					$('td[cookbookName] div',tr).html(e2.o);
					$('td[cookbookNum] div',tr).html(e2.p);
					consumeRecordIndex++;
					
					var statusTr=$('#deviceStatusList tr.deviceStatus[id='+sn+']');
					$('td[recordCount] div',statusTr).html(e2.q);
	// $('td[subsidyVersion] div',statusTr).html(e2.r);
					$('td[subsidyAuth] div',statusTr).html(e2.s==1?'是':'否');
				// 订餐取餐记录
				}else if(e2.type=='cbr'){
					$('#deviceList .device[id='+sn+'] img').attr('alt','在线').attr('src','/img/online.png');
					if(cookbookRecordIndex>=maxRow){
						cookbookRecordIndex=1;
						$('#cookbookRecord tbody tr td div').empty();
					}
					var tr=$('#cookbookRecord tbody tr[index='+consumeRecordIndex+']');	
					$('td[index] div',tr).html(cookbookRecordIndex);
					$('td[deviceName] div',tr).html(e2.record.deviceName);
					$('td[userNO] div',tr).html(e2.record.userNO);
					$('td[cardNO] div',tr).html(e2.record.cardNO);
					$('td[username] div',tr).html(e2.record.username);
					$('td[opFare] div',tr).html(e2.record.cookbookNum*price);
					$('td[mealName] div',tr).html(e2.record.mealName);
					$('td[mealTime] div',tr).html(e2.record.mealTime);
					$('td[orderTime] div',tr).html(e2.record.orderTime);
					$('td[consumeTypeDes] div',tr).html(e2.record.consumeTypeDes);
					$('td[cookbookName] div',tr).html(e2.record.cookbookName);
					$('td[cookbookNum] div',tr).html(e2.record.cookbookNum);
					$('td[recordNO] div',tr).html(e2.record.recordNO);
					$('td[typeName] div',tr).html(e2.record.typeName);
					cookbookRecordIndex++;
					
					var statusTr=$('#deviceStatusList tr.deviceStatus[id='+sn+']');
					$('td[recordCount] div',statusTr).html(e2.consumeRecord.recordCount);
					$('td[subsidyVersion] div',statusTr).html(e2.consumeRecord.subsidyVersion);
					$('td[subsidyAuth] div',statusTr).html(e2.consumeRecord.subsidyAuth==1?'是':'否');
					
				// 日志
				}else if(e2.type=='log'){
					if(logIndex>=maxRow){
						logIndex=0;
						$('#log tbody tr td div').empty();
					}
					var tr=$('#log tbody tr[index='+logIndex+']');					
					$('td[index] div',tr).html(logIndex);
					$('td[time] div',tr).html(e2.time);
					$('td[from] div',tr).html(e2.from);
					$('td[des] div',tr).html(e2.des);
					logIndex++;
				// 升级进度
				}else if(e2.type=='schedule'){
					if(mode==1){
						$('.schedule[name='+e2.sn+']').html(e2.s+'%');					
					}
				}
			}
		});
	}
	
	function getStatus(sn){
		var recordCount=$('#status'+sn+' div').html();
		if(recordCount==null || recordCount=='' || recordCount=='0'){
			$.post('${base }/command.do?cmd=getStatus&sn='+sn);
		}else{
			$('#status'+sn+' div').html(parseInt(recordCount)-1);
		}
	}
	
	function heart(){
		var heartInterval=parseInt('${company.heartInterval}')*35;
		$('body').everyTime('5s','getDeviceStatus', function() {
			var d=new Date();
			var array = map.keySet();
			for(var i in array) {
				var t=(d.getTime()-map.get(array[i]).getTime())/1000;
				if(t>heartInterval){
					$('#deviceList .device[name='+array[i]+'] img').attr('alt','离线').attr('src','/img/offline.png');
					$.post('${base }/removeInetSocketAddress.do?sn='+array[i]);
					map.remove(array[i]);
				}
			}
		},0,true);
	}
	
	function showDevice(deptId){
		if(deptId==0){
			$('#deviceList .device').show();
		}else{
			$('#deviceList .device').hide();
			$('#deviceList .device[deptId='+deptId+']').show();
		}
	}
	
	var monitorOps = {
		menuStyle: {
			width: '120px'
		},
		bindings : {
			'sysTime' : function(t, target) {
				executeCmd(t,'sysTime');
			},
			'baseConsumeParam' : function(t, target) {
				executeCmd(t,'baseConsumeParam');
			},
			'deviceParam' : function(t, target) {
				executeCmd(t,'deviceParam');
			},
			'grantSubsidy' : function(t, target) {
				executeCmd(t,'grantSubsidy');
			},
			'disableSubsidy' : function(t, target) {
				executeCmd(t,'disableSubsidy');
			},
			'allCookbook' : function(t, target) {
				executeCmd(t,'allCookbook');
			},
			'appendCookbook' : function(t, target) {
				executeCmd(t,'appendCookbook');
			},
			'singelCookbook' : function(t, target) {
				targetSN=$(t).parent().attr('id');
				$('.modifyCookbook').show();
			},
			'clear' : function(t, target) {
				executeCmd(t,'clear');
			},
			'sysInit' : function(t, target) {
				executeCmd(t,'sysInit');
			},
			'batchUpdate' : function(t, target) {
				executeCmd(t,'batchUpdate');
			},
			'blackUpdate' : function(t, target) {
				executeCmd(t,'blackUpdate');
			}
		},
		onShowMenu : function(e, menu) {
			return menu;
		}
	};
	
	var upgradeOps = {
		bindings : {
			'upgrade' : function(t, target) {
				executeCmd(t,'upgrade');
			}
		},
		onShowMenu : function(e, menu) {
			return menu;
		}
	};
	
	var clearOps = {
		bindings : {
			'clear' : function(t, target) {
				if($(t).hasClass('log')){
					logIndex=0;
				}else if($(t).hasClass('consumeRecord')){
					consumeRecordIndex=1;
				}
				$('tr td div', t).empty();
			}
		},
		onShowMenu : function(e, menu) {
			return menu;
		}
	};
	
	// 执行命令
	function executeCmd(t,cmd){
		var target=t.tagName;
		if(target=='A'){
			var deptId=$(t).attr('deptId');
			$.post('${base }/command.do?cmd='+cmd+'&deptId='+deptId);
		}else{
			var online=$(t).attr('src').indexOf('online');
			if(online>0){
				var sn=$(t).parent().attr('id');
				$.post('${base }/command.do?cmd='+cmd+'&sn='+sn);
			}			
		}
	}
	function setCodeNull(){
		var code=$('#jqContextMenu .modifyCookbook #code').val();
		if(code=='编号'){
			$('#jqContextMenu .modifyCookbook #code').val('')
		}
	}
	
	var targetSN=null;
	function modifyCookbook(){
		var online=$('#'+targetSN).find('img').attr('src').indexOf('online');
	// if(online>0){
			var code=$('#jqContextMenu .modifyCookbook #code').val();
			var ex=/^\d+$/;
			if(ex.test(code)){
				if(code>2000){
					alert('菜单编号不能大于2000！');
					return;					
				}
				$.post('${base }/command.do?cmd=modifyCookbook&sn='+targetSN+'&cookbookCode='+code);
				$('.modifyCookbook').hide();
			}else{
				alert('请输入正确的菜单编号！');
			}
	// }
	}
	$(function(){
		 var src_posi_Y = 0, dest_posi_Y = 0, move_Y = 0, is_mouse_down = false, destHeight = 30;
	    $('.tabsHeaderContent').mousedown(function(e){
			src_posi_Y = e.pageY;
	        is_mouse_down = true;
	    });
	    $(document).bind('click mouseup',function(e){
	        if(is_mouse_down){
	        	is_mouse_down = false;
	        }
	    }).mousemove(function(e){
	    	if(!is_mouse_down)
	    		return;
	        dest_posi_Y = e.pageY;
	        move_Y = dest_posi_Y-src_posi_Y;
	        src_posi_Y = dest_posi_Y;
	        $('#deviceList').css('height',$('#deviceList').height()+move_Y);
	        $('#msgContent').attr('layoutH',parseInt($('#msgContent').attr('layoutH'))+move_Y).layoutH();
	    });

	    <c:if test="${sessionScope.sysUser.loginName=='admin'}">
		    $(document).keydown(function(event){ 
		        if((event.altKey && event.keyCode == 85)) {
				   // 进入升级模式
		           if(mode==0){
		        	   mode=1;
		        	   $('.transfer').show();
		        	   $('.schedule').show().html('0.00%');
		        	   alertMsg.warn('升级过程中请勿随意切换模式以免造成升级失败！');
		        	   $('#deviceList img').contextMenu('upgradeMenu',upgradeOps);
		       		   $('.deptTree a').contextMenu('upgradeMenu',upgradeOps);
		           }else{
		        	   mode=0;
		        	   $('.transfer').hide();
		        	   $('.schedule').hide();
		        	   $('#deviceList img').contextMenu('menu',monitorOps);
		       		   $('.deptTree a').contextMenu('menu',monitorOps);
		           }
		           $.post('${base }/mode.do?mode='+mode);
		        }  
		  	}); 
		</c:if>
	    
	});
</script>

</head>
</html>