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
 * 出纳汇总控制类 根据结算查询结果
 * 
 * @author 赵春辉
 */
@Controller
@RequestMapping(value = "/balanceCenter/cashierPool")
public class CashierPoolController extends BaseController {

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
	public String list(@ModelAttribute Pagination pagination, String offgiveFare, Integer export, Integer exportType, Integer queryType, Integer operId, Integer recordType, String beginDate,
			String endDate, String includeOff, String otime, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		/***************************************** 验证出纳员 ***********************************************************/
		/*
		 * List<Map> su = commonService.selectListBySql(
		 * "select su.loginName from sysuser su  where su.companyId=9 ");
		 * for(Map m:su){
		 * System.out.println(m.get("loginName")+"--"+DesUtil.decrypt(StringUtil
		 * .objToString(m.get("loginName")))); }
		 */
		/*******************************************************************************************************/
		String selectSql = "br.operId,br.deptName,br.recordTypes,	SUM(br.totalOpCount) as  opCount ,SUM(br.totalOpFare) as opFare ,dc.deviceName,br.beginDate,br.endDate";
		String fromSql = "balancerecord br LEFT JOIN sysuser o ON o.operId=br.operId LEFT JOIN device dc ON dc.id = o.deviceId";
		String whereSql = "br.recordTypes !=3 and br.recordTypes !=4 and br.recordTypes !=5 and br.recordTypes !=10  and br.recordTypes !=11 and br.recordTypes !=12   and br.recordTypes !=13 and br.recordTypes !=14  and br.recordTypes !=15 and br.recordTypes !=16 and br.froms = 1 AND br.companyId="
				+ company.getId();
		// String whereSql = "br.froms = 1 AND br.companyId=" + company.getId();
		if (!StringUtils.isEmpty(operId) && operId != 0) {
			whereSql += String.format(" and br.operId = %s", operId);
		}

		if (!StringUtils.isEmpty(recordType) && recordType != -1) {
			whereSql += String.format(" and br.recordTypes = %s", recordType);
		}

		if (!StringUtils.isEmpty(offgiveFare)) {
			whereSql += String.format(" and br.recordTypes != %s  and br.recordTypes != %s ", 2, 7);
		}
		if (!StringUtils.isEmpty(beginDate) && !beginDate.equals("-1")) {
			whereSql += String.format(" and br.beginDate >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate) && !endDate.equals("-1")) {
			whereSql += String.format(" and br.endDate  <= '%s'", endDate);
		}

		// 显示最近一次结算
		/*
		 * List<Map> maxbalance = commonService.selectListBySql(
		 * "select max(br.beginDate) beginDate ,max(br.endDate) endDate,min(br.beginDate) mbeginDate,min(br.endDate) mendDate  from balancerecord br where br.companyId =  "
		 * + company.getId()); String maxbg=""; String maxed=""; String
		 * minbg=""; String mined=""; if(maxbalance.get(0)!=null){ maxbg =
		 * (String) maxbalance.get(0).get("beginDate"); maxed = (String)
		 * maxbalance.get(0).get("endDate"); minbg = (String)
		 * maxbalance.get(0).get("mbeginDate"); mined = (String)
		 * maxbalance.get(0).get("mendDate"); } String maxbg = (String)
		 * maxbalance.get(0).get("beginDate"); String maxed = (String)
		 * maxbalance.get(0).get("endDate");
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
		 * whereSql += String.format(" and br.beginDate >= '%s'", minbg); } if
		 * (!StringUtils.isEmpty(endDate)&& !endDate.equals("-1")) {
		 * 
		 * if (!StringUtils.isEmpty(endDate)&&endDate.equals("-1")) { whereSql
		 * += String.format(" and br.endDate  <= '%s'", maxed); }
		 */
		// 结算历史列表
		String balancetl = String.format("select br.beginDate,br.endDate from balancerecord br where br.companyId= %s group by br.beginDate,br.endDate", company.getId());
		List<Map> TiemList = commonService.selectListBySql(balancetl);

		String sql = String.format("select %s from %s where %s group by br.recordTypes,operId order by  br.beginDate  limit %s,%s", selectSql, fromSql, whereSql, pagination.getOffset(),
				pagination.getNumPerPage());
		String sql1 = String.format("select %s from %s where %s group by br.recordTypes,operId ", selectSql, fromSql, whereSql);
		List<Map> list = this.commonService.selectListBySql(sql);
		List<Map> totalList = this.commonService.selectListBySql(sql1);
		Integer totalCount = totalList.size();
		/* Integer totalCount=totalList.size(); */
		/* System.out.println("sql"+sql); */
		/* System.out.println(list); */
		/*
		 * for(int i=0;i<list.size();i++){
		 * if(StringUtil.objToInt(list.get(i).get("recordTypes"))==4&&StringUtil
		 * .objToInt(list.get(i).get("recordTypes"))==5){ list.remove(i); i=i-1;
		 * } }
		 */
		/*
		 * for(int i=0;i<totalList.size();i++){
		 * if(StringUtil.objToInt(totalList.get(i).get("recordTypes"))==4||
		 * StringUtil.objToInt(totalList.get(i).get("recordTypes"))==5){
		 * totalList.remove(i); i=i-1; } }
		 */
		/* Integer totalCount=totalList.size(); */
		Integer totalOpCount = 0;
		double totalOpFare1 = 0.0;
		for (Map map : list) {
			map.put("loginName", DesUtil.decrypt(StringUtil.objToString(map.get("deptName"))));
			/*
			 * map.put("opFare", (float) StringUtil.objToInt(map.get("opFare"))
			 * / 100);
			 */
			if (StringUtil.objToInt(map.get("recordTypes")) == 8 || StringUtil.objToInt(map.get("recordTypes")) == 9 || StringUtil.objToInt(map.get("recordTypes")) == 14) {
				// map.put("opFare", (float)
				// StringUtil.objToInt(map.get("opFare")) / 100-((float)
				// StringUtil.objToInt(map.get("opFare")) / 100)*2);
				map.put("opFare", -(float) StringUtil.objToInt(map.get("opFare")) / 100);
			} else {
				map.put("opFare", (float) StringUtil.objToInt(map.get("opFare")) / 100);
			}
			map.put("recordTypeDes", CardRecord.recordTypes[StringUtil.objToInt(map.get("recordTypes"))]);
			totalOpCount += StringUtil.objToInt(map.get("opCount"));
			totalOpFare1 += (float) map.get("opFare");
		}
		BigDecimal b2 = new BigDecimal(totalOpFare1);
		double totalOpFare = b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		/**********************************************************************************************************************/

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}
		if (export != null && 1 == export) {
			String[] expColumns = { "出纳员姓名", "出纳员编号", "操作类型", "操作金额", "操作次数", "开始时间", "结束时间" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("loginName")));
				list2.add(StringUtil.objToString(m.get("operId")));
				list2.add(StringUtil.objToString(m.get("recordTypeDes")));
				list2.add(StringUtil.objToString(m.get("opFare")));
				list2.add(StringUtil.objToString(m.get("opCount")));
				list2.add(StringUtil.objToString(m.get("beginDate")));
				list2.add(StringUtil.objToString(m.get("endDate")));
				/*
				 * list2.add(beginDate); list2.add(endDate);
				 */
				exportList.add(list2);
			}
			ExportUtil.exportExcel("出纳汇总表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}
		/***********************************************************************************************************************/

		// 去除 清零的操作类型
		/*
		 * Map m = new HashMap(); for(int i=0;i<
		 * CardRecord.recordTypes.length;i++){ if(i==11||i==13) i=i+1;
		 * m.put(i,CardRecord.recordTypes[i]); } Collection s = m.values();
		 */
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalOpCount", totalOpCount);
		model.addAttribute("totalOpFare1", totalOpFare);
		model.addAttribute("list", list);
		model.addAttribute("otime", otime);
		model.addAttribute("TiemList", TiemList);
		model.addAttribute("queryType", queryType);
		model.addAttribute("operId", operId);
		model.addAttribute("offgiveFare", offgiveFare);
		model.addAttribute("recordType", recordType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);
		model.addAttribute("sysUserList", sysUserList);
		model.addAttribute("recordTypes", CardRecord.recordTypes);
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
