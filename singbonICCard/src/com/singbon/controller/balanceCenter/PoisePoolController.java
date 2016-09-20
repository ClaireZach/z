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
import com.singbon.entity.CardRecord;
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.entity.SysUser;
import com.singbon.service.CommonService;
import com.singbon.service.SysUserService;
import com.singbon.util.DesUtil;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 平衡汇总
 * 
 * @author 赵春辉
 */
@Controller
@RequestMapping(value = "/balanceCenter/poisePool")
public class PoisePoolController extends BaseController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public SysUserService sysUserService;
	private List<SysUser> sysUserList = null;

	/**
	 * 通用导航到各模块首页
	 */
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		model.addAttribute("company", company);
		sysUserList = (List<SysUser>) this.sysUserService.selectCashierList(company.getId());
		for (SysUser sysUser : sysUserList) {
			sysUser.setLoginName(DesUtil.decrypt(sysUser.getLoginName()));
		}
		String url = request.getRequestURI();
		model.addAttribute("base", url.replace("/index.do", ""));
		return url.replace(".do", "");
	}

	/**
	 * 列表
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination,
			Integer deptId/* String deptId */, Integer export, Integer exportType, String deptName, Integer consumeType, Integer queryType, Integer operId, Integer recordType, String beginDate,
			String endDate, String includeOff, String otime, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}

		String whensql1 = "case  when br.recordTypes=0 and br.froms=1 then SUM(br.totalOpCount) " + "when br.recordTypes=1 and br.froms=1 then SUM(br.totalOpCount) "
				+ "when br.recordTypes=2 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=6 and br.froms=1 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=7 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=10 and br.froms=1 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=12 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=9 and br.froms=0 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=105 and br.froms=0 then SUM(br.totalOpCount)" + "when br.recordTypes=106 and br.froms=0 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=107 and br.froms=0 then SUM(br.totalOpCount)" + "when br.recordTypes=15 and br.froms=1 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=16 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=109 and br.froms=0 then SUM(br.totalOpCount)" + "else 0 end as  countIn";
		String whensql2 = "case  when br.recordTypes=0 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=1 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=2 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=6 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=7 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=10 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=12 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=9 and br.froms=0 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=105 and br.froms=0 then SUM(br.totalOpFare)" + "when br.recordTypes=106 and br.froms=0 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=107 and br.froms=0 then SUM(br.totalOpFare)" + "when br.recordTypes=15 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=16 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=109 and br.froms=0 then SUM(br.totalOpFare)" + "else 0 end as  countInFare";
		String whensql3 = "case  when br.recordTypes=8 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=9 and br.froms=1 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=11 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=13 and br.froms=1 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=14 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=1 and br.froms=0 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=2 and br.froms=0 then SUM(br.totalOpCount)" + "when br.recordTypes=3 and br.froms=0 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=39 and br.froms=0 then SUM(br.totalOpCount)" + "when br.recordTypes=101 and br.froms=0 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=102 and br.froms=0 then SUM(br.totalOpCount)" + "when br.recordTypes=103 and br.froms=0 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=15 and br.froms=1 then SUM(br.totalOpCount)" + "when br.recordTypes=16 and br.froms=1 then SUM(br.totalOpCount)"
				+ "when br.recordTypes=139 and br.froms=0 then SUM(br.totalOpCount)" + "else 0 end as  countOut";
		String whensql4 = "case  when br.recordTypes=8 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=9 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=11 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=13 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=14 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=1 and br.froms=0 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=2 and br.froms=0 then SUM(br.totalOpFare)" + "when br.recordTypes=3 and br.froms=0 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=39 and br.froms=0 then SUM(br.totalOpFare)" + "when br.recordTypes=101 and br.froms=0 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=102 and br.froms=0 then SUM(br.totalOpFare)" + "when br.recordTypes=103 and br.froms=0 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=15 and br.froms=1 then SUM(br.totalOpFare)" + "when br.recordTypes=16 and br.froms=1 then SUM(br.totalOpFare)"
				+ "when br.recordTypes=139 and br.froms=0 then SUM(br.totalOpFare)" + "else 0 end as  countOutFare";

		String selectsql = String.format("br.deptName as login,br.recordTypes,br.froms,br.beginDate,br.endDate,%s,%s,%s,%s", whensql1, whensql2, whensql3, whensql4);

		String whereSql = "(br.recordTypes+br.froms!=4) and (br.recordTypes+br.froms!=5) and (br.recordTypes+br.froms!=6)  and (br.recordTypes+br.froms!=12) and (br.recordTypes+br.froms!=14) and (br.recordTypes+br.froms!=15)  and (br.recordTypes+br.froms!=16) and (br.recordTypes+br.froms!=17)  and  br.froms !=-1 and br.companyId="
				+ company.getId();
		/* and br.recordTypes !=15 and br.recordTypes !=16 */

		if (!StringUtils.isEmpty(operId) && operId != 0) {
			whereSql += String.format(" and br.operId = %s", operId);
		}

		if (!StringUtils.isEmpty(recordType) && recordType != -1) {
			whereSql += String.format(" and br.recordTypes = %s", recordType);
		}
		// 显示最近一次结算
		/*
		 * List<Map> maxbalance = commonService.selectListBySql(
		 * "select max(br.beginDate) beginDate ,max(br.endDate) endDate,min(br.beginDate) mbeginDate,min(br.endDate) mendDate  from balancerecord br where br.companyId =  "
		 * + company.getId()); String maxbg=""; String maxed=""; String
		 * minbg=""; if(maxbalance.get(0)!=null){ maxbg = (String)
		 * maxbalance.get(0).get("beginDate"); maxed = (String)
		 * maxbalance.get(0).get("endDate"); minbg = (String)
		 * maxbalance.get(0).get("mbeginDate"); }
		 * if(maxbalance.get(0)!=null&&StringUtils.isEmpty(beginDate)){ whereSql
		 * += String.format(" and br.beginDate >= '%s'", maxbg); }
		 * if(maxbalance.get(0)!=null&&StringUtils.isEmpty(endDate)){ whereSql
		 * += String.format(" and br.endDate  <= '%s'", maxed); } if
		 * (!StringUtils.isEmpty(beginDate)&&!beginDate.equals("-1")) { whereSql
		 * += String.format(" and br.beginDate >= '%s'", beginDate); }
		 * 
		 * if (!StringUtils.isEmpty(endDate)&&!endDate.equals("-1")) { whereSql
		 * += String.format(" and br.endDate  <= '%s'", endDate); }
		 * 
		 * if (!StringUtils.isEmpty(beginDate)&&beginDate.equals("-1")) {
		 * whereSql += String.format(" and br.beginDate >= '%s'", minbg); }
		 * 
		 * if (!StringUtils.isEmpty(endDate)&&endDate.equals("-1")) { whereSql
		 * += String.format(" and br.endDate  <= '%s'", maxed); }
		 */

		if (!StringUtils.isEmpty(beginDate) && !beginDate.equals("-1")) {
			whereSql += String.format(" and br.beginDate >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate) && !endDate.equals("-1")) {
			whereSql += String.format(" and br.endDate  <= '%s'", endDate);
		}

		/*
		 * if (!StringUtils.isEmpty(deptId)) { whereSql += String.format(
		 * " and br.deptId in ( %s ) ", deptId.substring(0,deptId.length()-1));
		 * }
		 */
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			whereSql += String.format(" and find_in_set(br.deptId,getSubIds(%s,1))>0", deptId);
		}
		if (!StringUtils.isEmpty(consumeType) && consumeType != -1) {
			whereSql += String.format(" and br.recordTypes = %s", consumeType);
		}

		String sql = String.format("select %s from balancerecord br where %s group by br.recordTypes,br.deptName limit %s,%s ", selectsql, whereSql, pagination.getOffset(),
				pagination.getNumPerPage());
		String sql1 = String.format("select %s from balancerecord br where %s group by br.recordTypes,br.deptName", selectsql, whereSql);

		// 结算历史列表
		// String balancetl = String.format("select br.beginDate,br.endDate from
		// balancerecord br where br.companyId= %s and br.froms !=-1 group by
		// br.beginDate,br.endDate",company.getId());
		String balancetl = String.format("select br.beginDate,br.endDate from balancerecord br where br.companyId= %s group by br.beginDate,br.endDate", company.getId());
		List<Map> TiemList = commonService.selectListBySql(balancetl);
		List<Map> list = this.commonService.selectListBySql(sql);
		List<Map> totalList = this.commonService.selectListBySql(sql1);
		Integer totalCount = totalList.size();

		Integer totalCountIn = 0;
		Integer totalCountOut = 0;
		float totalCountInFare = 0;
		float totalCountOutFare = 0;
		float poisFare;
		for (Map map : list) {
			if (StringUtil.objToInt(map.get("froms")) == 0) {
				map.put("recordTypeDes", ConsumeType.getTypeDes(Integer.valueOf(map.get("recordTypes").toString())));
			} else if (StringUtil.objToInt(map.get("froms")) == 1) {
				map.put("login", DesUtil.decrypt(StringUtil.objToString(map.get("login"))));
				map.put("recordTypeDes", CardRecord.recordTypes[StringUtil.objToInt(map.get("recordTypes"))]);
			} else {
				map.put("recordTypeDes", StringUtil.objToInt(map.get("recordTypes")));
			}
			map.put("countInFare", (float) StringUtil.objToInt(map.get("countInFare")) / 100);
			map.put("countOutFare", (float) StringUtil.objToInt(map.get("countOutFare")) / 100);
			totalCountIn += StringUtil.objToInt(map.get("countIn"));
			totalCountOut += StringUtil.objToInt(map.get("countOut"));
			totalCountInFare += (float) map.get("countInFare");
			totalCountOutFare += (float) map.get("countOutFare");
		}
		/*
		 * if( totalCountInFare > totalCountOutFare){ poisFare =
		 * totalCountInFare - totalCountOutFare; }else{ poisFare =
		 * totalCountOutFare - totalCountInFare; }
		 */
		poisFare = totalCountInFare - totalCountOutFare;
		BigDecimal b1 = new BigDecimal(totalCountInFare);
		BigDecimal b2 = new BigDecimal(totalCountOutFare);
		BigDecimal b3 = new BigDecimal(poisFare);
		totalCountInFare = (float) b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		totalCountOutFare = (float) b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		poisFare = (float) b3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		/**********************************************************************************************************************/
		if (export != null && 1 == export) {
			String[] expColumns = { "营业分区或操作员", "操作类型", "收入计次", "收入金额", "支出计次", "支出金额" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("login")));
				list2.add(StringUtil.objToString(m.get("recordTypeDes")));
				list2.add(StringUtil.objToString(m.get("countIn")));
				list2.add(StringUtil.objToString(m.get("countInFare")));
				list2.add(StringUtil.objToString(m.get("countOut")));
				list2.add(StringUtil.objToString(m.get("countOutFare")));
				exportList.add(list2);
			}
			ExportUtil.exportExcel("平衡汇总表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}
		/**********************************************************************************************************************/

		model.addAttribute("poisFare", poisFare);
		model.addAttribute("otime", otime);
		model.addAttribute("totalCountIn", totalCountIn);
		model.addAttribute("totalCountOut", totalCountOut);
		model.addAttribute("totalCountInFare", totalCountInFare);
		model.addAttribute("totalCountOutFare", totalCountOutFare);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("list", list);
		model.addAttribute("queryType", queryType);
		model.addAttribute("operId", operId);
		model.addAttribute("recordType", recordType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("consumeType", consumeType);
		model.addAttribute("TiemList", TiemList);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);
		model.addAttribute("sysUserList", sysUserList);
		model.addAttribute("recordTypes", CardRecord.recordTypes);
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
