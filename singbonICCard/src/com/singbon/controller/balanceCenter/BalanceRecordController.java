package com.singbon.controller.balanceCenter;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMethod;

import com.singbon.controller.BaseController;
import com.singbon.device.ConsumeType;
import com.singbon.entity.CardRecord;
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.service.BalanceRecordService;
import com.singbon.service.CommonService;
import com.singbon.util.DesUtil;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 
 * 结算控制类
 * 
 * @author 陈梦
 *
 */
@Controller
@RequestMapping(value = "/balanceCenter/balanceRecord")
public class BalanceRecordController extends BaseController {

	@Autowired
	public CommonService commonService;

	@Autowired
	public BalanceRecordService balanceRecordService;

	/**
	 * s 结算表数据插入
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/insert.do", method = RequestMethod.POST)
	public void insert(@ModelAttribute String beginDate, String endDate, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Company company = (Company) request.getSession().getAttribute("company");
		beginDate = request.getParameter("beginDate");

		String whereSql1 = "u.companyId=" + company.getId() + " " + "and cb.companyId =  " + company.getId();
		String groupBy1 = "dp.deptName,consumeType";
		String fromSql1 = "consumerecord cb LEFT JOIN user u ON cb.userId=u.userId LEFT JOIN device d ON cb.deviceId = d.id LEFT JOIN dept dp ON d.deptId = dp.id";
		// String whereSql2 = "u.companyId=" + company.getId() + " "+ "and
		// ca.companyId = " + company.getId()+ " and ca.recordType!=10 and
		// ca.recordType!=11 and ca.recordType!=12 and ca.recordType!=13";

		String whereSql2 = "u.companyId=" + company.getId() + " " + "and ca.companyId =  " + company.getId()
				+ " and ca.recordType!=10 and ca.recordType!=11 and ca.recordType!=12 and ca.recordType!=13";

		String groupBy2 = "o.loginName,recordType";
		String fromSql2 = "cardRecord ca LEFT JOIN `user` u ON ca.userId=u.userId LEFT JOIN sysuser o ON o.operId = ca.operId";
		String dateColumn = "opTime";

		if (!StringUtils.isEmpty(beginDate)) {
			whereSql1 += String.format(" and %s >= '%s'", dateColumn, beginDate);
			whereSql2 += String.format(" and %s >= '%s'", dateColumn, beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql1 += String.format(" and %s < '%s'", dateColumn, endDate);
			whereSql2 += String.format(" and %s < '%s'", dateColumn, endDate);
		}

		String sql1 = String.format(
				"select u.companyId as companyId,dp.id as deptId,-1 operId,dp.deptName as deptName,cb.consumeType as recordTypes," + "SUM(cb.opFare+cb.subsidyOpFare) as totalOpFare,"
						+ "COUNT(consumeType) as totalOpCount,sum(cb.discountFare) as discountFare,sum(CASE WHEN consumeType=39 OR consumeType=139 THEN cb.subsidyOpFare ELSE 0 END) as honchonFare,sum(CASE WHEN consumeType=39 OR consumeType=139 THEN 1 ELSE 0 END) as honchonCount,cb.opTime,0 froms from %s where %s group by %s union select u.companyId,-1 deptId,o.operId,o.loginName as deptName,ca.recordType,SUM(ca.opFare),COUNT(recordType),0 discountFare,sum(CASE WHEN recordType=11 OR recordType=13 THEN ca.opFare ELSE 0 END),sum(CASE WHEN recordType=11 OR recordType=13 THEN 1 ELSE 0 END), ca.opTime,1 froms from %s where %s group by %s",
				fromSql1, whereSql1, groupBy1, fromSql2, whereSql2, groupBy2);
		List<Map> list1 = this.commonService.selectListBySql(sql1);

		if (list1.isEmpty()) {
			Map map = new HashMap<>();

			map.put("deptId", -1);
			map.put("companyId", company.getId());
			map.put("deptName", null);
			map.put("totalOpCount", 0);
			map.put("totalOpFare", 0.0);
			map.put("honchonCount", 0);
			map.put("honchonFare", 0.0);
			map.put("recordTypes", -1);
			map.put("beginDate", beginDate);
			map.put("endDate", endDate);
			map.put("froms", -1);
			list1.add(map);
			this.balanceRecordService.insert(map);

		} else {
			for (Map map : list1) {

				map.put("beginDate", beginDate);
				map.put("endDate", endDate);
				this.balanceRecordService.insert(map);
			}

		}
		endDate = StringUtil.getDateStr(endDate, 1);
		model.addAttribute("beginDate", endDate);

	}

	/**
	 * 列表
	 * 
	 * @param pagination
	 * @param export
	 * @param exportType
	 * @param beginDate
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, String beginTime, String endTime, Model model, HttpServletRequest request,
			HttpServletResponse response, String orderField, String orderDirection) {
		Company company = (Company) request.getSession().getAttribute("company");
		String sql = "select max(endDate) as endDate from balancerecord where companyId=" + company.getId();

		String beginDate = "";
		List<Map> list = this.commonService.selectListBySql(sql);
		if (list.get(0) != null) {
			for (Map map : list) {
				beginDate = (String) map.get("endDate");
				beginDate = StringUtil.getDateStr(beginDate, 1);
			}
		} else {
			sql = String.format(
					"select min(endDate) endDate from (select min(cb.opTime) as endDate from consumerecord  cb left join user u on cb.userId=u.userId where u.companyId= %s  union select min(c.opTime) as endDate from cardrecord c left join user u on c.userId=u.userId where u.companyId=%s) t",
					company.getId(), company.getId());
			list = this.commonService.selectListBySql(sql);
			if (list.get(0) != null) {
				for (Map map : list) {
					beginDate = (String) map.get("endDate");
				}
			}

		}

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}

		String[] columns = { "b.deptName", "b.totalOpCount", "b.totalOpFare", "b.honchonCount", "b.honchonFare", "b.recordTypes", "b.beginDate", "b.endDate", "b.froms" };
		String fromSql = "balancerecord b";
		String whereSql = "b.companyId=" + company.getId();
		if (StringUtils.isEmpty(beginTime)) {
			String beginDateSql = "select max(beginDate) as beginTime from balancerecord where companyId=" + company.getId();
			List<Map> beginDateList = this.commonService.selectListBySql(beginDateSql);
			if (beginDateList.get(0) != null) {
				for (Map map : beginDateList) {
					whereSql += String.format(" and b.beginDate >= '%s'", map.get("beginTime"));
				}
			}
		} else if (!beginTime.equals("-1")) {
			whereSql += String.format(" and b.beginDate >= '%s'", beginTime);
		}
		if (StringUtils.isEmpty(endTime)) {
			String endDateSql = "select max(endDate) as endTime from balancerecord where companyId=" + company.getId();
			List<Map> endDateList = this.commonService.selectListBySql(endDateSql);
			if (endDateList.get(0) != null) {
				for (Map map : endDateList) {
					whereSql += String.format(" and b.endDate >= '%s'", map.get("endTime"));
				}
			}
		} else if (!endTime.equals("-1")) {
			whereSql += String.format(" and b.endDate <= '%s'", endTime);
		}

		String beginDateSql = "select distinct beginDate as beginTime from balancerecord where companyId=" + company.getId();
		String endDateSql = "select distinct endDate as endTime from balancerecord where companyId=" + company.getId();

		list = this.commonService.selectByPage(columns, null, fromSql, whereSql, pagination);
		List<Map> beginDateList = this.commonService.selectListBySql(beginDateSql);
		List<Map> endDateList = this.commonService.selectListBySql(endDateSql);

		int totalCount = Integer.valueOf(list.get(0).get("deptName").toString());
		list.remove(0);

		for (Map m : list) {
			m.put("totalOpFare", Float.valueOf(m.get("totalOpFare").toString()) / 100);
			m.put("honchonFare", Float.valueOf(m.get("honchonFare").toString()) / 100);
			if ((Integer) m.get("froms") != -1) {
				if ((Integer) m.get("froms") == 0) {
					m.put("recordTypes", ConsumeType.getTypeDes(Integer.valueOf(m.get("recordTypes").toString())));
				} else {
					m.put("recordTypes", CardRecord.recordTypes[StringUtil.objToInt(m.get("recordTypes"))]);
				}
				if ((Integer) m.get("froms") == 1) {
					m.put("deptName", DesUtil.decrypt(StringUtil.objToString(m.get("deptName"))));
				}
			}
		}

		if (export != null && 1 == export) {

			String[] expColumns = { "部门或操作员名称", "总操作次数", "总操作额", "宏冲次数", "宏冲金额", "开始时间", "结算时间", "操作类型" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("deptName")));
				list2.add(StringUtil.objToString(m.get("totalOpCount")));
				list2.add(StringUtil.objToString(m.get("totalOpFare")));
				list2.add(StringUtil.objToString(m.get("honchonCount")));
				list2.add(StringUtil.objToString(m.get("honchonFare")));
				list2.add(StringUtil.objToString(m.get("beginDate")));
				list2.add(StringUtil.objToString(m.get("endDate")));
				list2.add(StringUtil.objToString(m.get("recordTypes")));

				exportList.add(list2);
			}

			ExportUtil.exportExcel("结算表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;

		}

		model.addAttribute("list", list);

		model.addAttribute("beginDateList", beginDateList);
		model.addAttribute("endDateList", endDateList);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("beginTime", beginTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("base", StringUtil.requestBase(request));
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		return StringUtil.requestPath(request, "list");
	}

	/**
	 * 时间段设置
	 */
	@RequestMapping(value = "/autoBalance.do")
	public String timeSet(Integer balanceMode, HttpServletRequest request, HttpServletResponse response, Model model) {
		// id = selectGroupId;
		// Company company = (Company)
		// request.getSession().getAttribute("company");
		// SysUser sysUser = (SysUser)
		// request.getSession().getAttribute("sysUser");
		// List<EntranceTimeSet> entranceTimeSetList = (List<EntranceTimeSet>)
		// this.entranceTimeService
		// .selectListByCompanyIdgroupId(company.getId(), id);//
		// entranceTimeSet.xml
		// // 里面是groupId
		// if (entranceTimeSetList.size() == 0) {
		// this.entranceTimeService.insert(company.getId(), id,
		// sysUser.getOperId());
		// } else {
		// }
		String url = request.getRequestURI();
		model.addAttribute("base", url.replace("/autoBalance.do", ""));
		return StringUtil.requestPath(request, "autoBalance");
		// return "systemManager/systemSetting/entranceGuard/timeSet";
	}

}
