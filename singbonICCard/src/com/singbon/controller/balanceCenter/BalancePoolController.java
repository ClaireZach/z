package com.singbon.controller.balanceCenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.singbon.controller.BaseController;
import com.singbon.device.ConsumeType;
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 结算汇总查询控制类
 * 
 * @author 赵春辉
 * 
 */
@Controller
@RequestMapping(value = "/balanceCenter/balancePool")
public class BalancePoolController extends BaseController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public DeviceService deviceService;

	/**
	 * 列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, String includeSub, Integer export, Integer exportType, Integer deptId, String disCountFare,
			/* String deptId, */ String deptName, Integer consumeType, String beginDate, String endDate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}
		List<Map> list = null;
		String selectSql = "br.deptName as dpName,br.deptId,br.recordTypes,SUM(br.totalOpCount) as OpCount,SUM(br.totalOpFare) as opFare,SUM(br.honchonCount) as hcCount,SUM(br.honchonFare) as hcFare,br.beginDate,br.endDate";
		String fromSql = "balancerecord br ";
		String whereSql = "br.recordTypes!=9 and br.recordTypes!=109 and  br.froms = 0 AND br.companyId=" + company.getId();
		/*
		 * String whereSql = "br.froms = 0 AND br.companyId=" + company.getId();
		 */

		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			if (StringUtils.isEmpty(includeSub)) {
				whereSql += String.format(" and find_in_set(br.deptId,getSubIds(%s,1))>0", deptId);
			} else {
				whereSql += String.format(" and br.deptId = %s", deptId);
			}
		}

		if (!StringUtils.isEmpty(consumeType) && consumeType != -1) {
			whereSql += String.format(" and br. recordTypes= %s", consumeType);
		}
		// 显示最近一次结算
		/*
		 * List<Map> maxbalance = commonService.selectListBySql(
		 * "select max(br.beginDate) beginDate ,max(br.endDate) endDate,min(br.beginDate) mbeginDate,min(br.endDate) mendDate  from balancerecord br where br.companyId =  "
		 * + company.getId()); String maxbg=""; String maxed=""; String
		 * minbg="";
		 * 
		 * if(maxbalance.get(0)!=null){ maxbg = (String)
		 * maxbalance.get(0).get("beginDate"); maxed = (String)
		 * maxbalance.get(0).get("endDate"); minbg = (String)
		 * maxbalance.get(0).get("mbeginDate"); }
		 * if(maxbalance.get(0)!=null&&StringUtils.isEmpty(beginDate)){ whereSql
		 * += String.format(" and br.beginDate >= '%s'", maxbg); }
		 * if(maxbalance.get(0)!=null&&StringUtils.isEmpty(endDate)){ whereSql
		 * += String.format(" and br.endDate  <= '%s'", maxed); } if
		 * (!StringUtils.isEmpty(beginDate)&&!beginDate.equals("-1")) { whereSql
		 * += String.format(" and br.beginDate >= '%s'", beginDate); } if
		 * (!StringUtils.isEmpty(endDate)&& !endDate.equals("-1")) {
		 * 
		 * if (!StringUtils.isEmpty(endDate)&&!endDate.equals("-1")) { whereSql
		 * += String.format(" and br.endDate  <= '%s'", endDate); }
		 * 
		 * if (!StringUtils.isEmpty(beginDate)&&beginDate.equals("-1")) {
		 * whereSql += String.format(" and br.beginDate >= '%s'", minbg);
		 * beginDate = minbg; } if (!StringUtils.isEmpty(endDate)&&
		 * !endDate.equals("-1")) {
		 * 
		 * if (!StringUtils.isEmpty(endDate)&&endDate.equals("-1")) { whereSql
		 * += String.format(" and br.endDate  <= '%s'", maxed); endDate = maxed;
		 * }
		 */
		if (!StringUtils.isEmpty(beginDate) && !beginDate.equals("-1")) {
			whereSql += String.format(" and br.beginDate >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate) && !endDate.equals("-1")) {
			whereSql += String.format(" and br.endDate <= '%s'", endDate);
		}

		if (!StringUtils.isEmpty(disCountFare)) {
			selectSql = "br.deptName as dpName,br.deptId,br.recordTypes,SUM(br.totalOpCount) as OpCount,SUM(br.totalOpFare+br.discountFare) as opFare,SUM(br.honchonCount) as hcCount,SUM(br.honchonFare) as hcFare,br.beginDate,br.endDate";
		}

		String sql = String.format("select %s from %s where %s group by br.recordTypes,br.deptId", selectSql, fromSql, whereSql);
		String sql1 = String.format("select %s from %s where %s group by br.recordTypes,br.deptId limit %s,%s", selectSql, fromSql, whereSql, pagination.getOffset(), pagination.getNumPerPage());
		list = this.commonService.selectListBySql(sql1);
		// 结算历史列表
		String balancetl = String.format("select br.beginDate,br.endDate from balancerecord br where br.companyId= %s group by br.beginDate,br.endDate", company.getId());
		List<Map> TiemList = commonService.selectListBySql(balancetl);
		List<Map> totalList = this.commonService.selectListBySql(sql);
		Integer totalCount = totalList.size();
		Integer totalFareCount = 0;
		Integer totalHc = 0;
		double totalFare1 = 0.0;
		double totalHcFare1 = 0.0;
		for (Map map : list) {
			if (StringUtil.objToInt(map.get("recordTypes")) == 139 || StringUtil.objToInt(map.get("recordTypes")) == 39) {
				map.put("opFare", 0);
			}
			map.put("opFare", (float) StringUtil.objToInt(map.get("opFare")) / 100);
			map.put("hcFare", (float) StringUtil.objToInt(map.get("hcFare")) / 100);
			map.put("recordTypes", ConsumeType.getTypeDes(Integer.valueOf(map.get("recordTypes").toString())));
			totalFareCount += StringUtil.objToInt(map.get("OpCount"));
			totalHc += StringUtil.objToInt(map.get("hcCount"));
			totalFare1 += (float) map.get("opFare");
			totalHcFare1 += (float) map.get("hcFare");
		}
		BigDecimal b1 = new BigDecimal(totalFare1);
		BigDecimal b2 = new BigDecimal(totalHcFare1);
		double totalFare = b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		double totalHcFare = b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		/*****************************************************************************************************************/
		if (export != null && 1 == export) {

			String[] expColumns = { "营业部门", "消费类型", "消费次数", "消费金额", "宏冲次数", "宏冲金额", "开始时间", "结束时间" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("dpName")));
				list2.add(StringUtil.objToString(m.get("recordTypes")));
				list2.add(StringUtil.objToString(m.get("OpCount")));
				list2.add(StringUtil.objToString(m.get("opFare")));
				list2.add(StringUtil.objToString(m.get("hcCount")));
				list2.add(StringUtil.objToString(m.get("hcFare")));
				// list2.add(StringUtil.objToString(m.get("beginDate")));
				// list2.add(StringUtil.objToString(m.get("hcFare")));
				/*
				 * list2.add(beginDate); list2.add(endDate);
				 */
				list2.add(StringUtil.objToString(m.get("beginDate")));
				list2.add(StringUtil.objToString(m.get("endDate")));
				exportList.add(list2);
			}
			ExportUtil.exportExcel("结算汇总表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}

		/*****************************************************************************************************************/
		model.addAttribute("list", list);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("consumeType", consumeType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("totalFare", totalFare);
		model.addAttribute("includeSub", includeSub);
		model.addAttribute("totalFareCount", totalFareCount);
		model.addAttribute("disCountFare", disCountFare);
		model.addAttribute("totalHc", totalHc);
		model.addAttribute("totalHcFare", totalHcFare);
		model.addAttribute("TiemList", TiemList);
		// List<Device> deviceList =
		// this.deviceService.selectDeviceListByCompanyId(company.getId(), new
		// String[] { "2", "3" }, 0);
		// model.addAttribute("deviceList", deviceList);
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
