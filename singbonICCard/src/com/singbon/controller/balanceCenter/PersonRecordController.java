package com.singbon.controller.balanceCenter;

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
 * 
 * 
 * 个人记录查询控制类
 * 
 * @author 陈梦
 *
 */
@Controller
@RequestMapping(value = "/balanceCenter/personRecord")
public class PersonRecordController extends BaseController {

	@Autowired
	public CommonService commonService;

	@Autowired
	public SysUserService sysUserService;
	private List<SysUser> sysUserList = null;

	/**
	 * 通用导航到各模块首页
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
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
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, String nameStr, Integer consumeType, Integer recordType, Integer resultTypes, String beginDate,
			String endDate, String includeOff, Long froms, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Company company = (Company) request.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());

		}
		if (resultTypes != null) {
			String result = Integer.toString(resultTypes);
			if (result.contains("282")) {
				consumeType = Integer.valueOf(result.substring(3, result.length()));
			} else {
				recordType = resultTypes;
			}
		}

		String orderSql = "";
		String fromSql1 = "consumerecord c LEFT JOIN USER u ON c.userId = u.userId";
		String fromSql2 = "cardrecord c LEFT JOIN USER u ON c.userId = u.userId";
		String whereSql1 = "u.companyId=" + company.getId();
		String whereSql2 = "u.companyId=" + company.getId() + " and c.recordType!=10 and c.recordType!=11 and c.recordType!=12 and c.recordType!=13";
		if (!StringUtils.isEmpty(nameStr)) {
			whereSql1 += String.format(" and (u.userNO like '%%%s%%' or username like '%%%s%%')", nameStr, nameStr);
			whereSql2 += String.format(" and (u.userNO like '%%%s%%' or username like '%%%s%%')", nameStr, nameStr);
		}
		if (!StringUtils.isEmpty(consumeType) && consumeType != -1) {
			whereSql1 += String.format(" and c.consumeType = %s", consumeType);
			whereSql2 += String.format(" and c.recordType = %s", -1);

		}

		String dateColumn = "opTime";

		if (!StringUtils.isEmpty(beginDate)) {
			whereSql1 += String.format(" and %s >= '%s'", dateColumn, beginDate);
			whereSql2 += String.format(" and %s >= '%s'", dateColumn, beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql1 += String.format(" and %s <= '%s'", dateColumn, endDate);
			whereSql2 += String.format(" and %s <= '%s'", dateColumn, endDate);
		}

		if (StringUtils.isEmpty(includeOff)) {
			whereSql1 += " and u.status != 244";
			whereSql2 += " and u.status != 244";
		}
		if (!StringUtils.isEmpty(recordType) && recordType != -1) {
			whereSql2 += String.format(" and c.recordType = %s", recordType);
			whereSql1 += String.format(" and c.consumeType = %s", -1);

		}

		String sql = String.format(
				"select t.userNO,t.cardNO,t.sumFare,t.oddFare,t.username,t.opFare,t.subsidyOpFare,t.consumeType,t.subsidyOddFare,t.opCount,t.subsidyOpCount,t.opTime,t.froms from (select u.userId,u.userNO,u.username,c.cardNO,c.sumFare,"
						+ "c.oddFare as oddFare,c.opFare,c.subsidyOpFare,c.consumeType,c.subsidyOddFare,c.opCount,c.subsidyOpCount,c.opTime,0 froms from %s where %s"
						+ " union all select u.userId,u.userNO,u.username,c.cardNO,(CASE WHEN c.recordType = 0 THEN c.oddFare+c.opFare+c.subsidyOddFare "
						+ " WHEN c.recordType = 7 AND (SELECT count(userId) > 1 or count(userId) =1 FROM cardrecord ca WHERE (ca.opTime < c.opTime or ca.opTime = c.opTime) AND (ca.recordType = 0 OR ca.recordType = 2 OR ca.recordType = 6 OR ca.recordType = 7 OR ca.recordType = 10 OR ca.recordType = 12) and ca.id!=c.id AND ca.userId = c.userId) THEN c.opFare +( SELECT sum(ca.opFare+ca.subsidyOddFare) FROM cardrecord ca WHERE (ca.recordType = 0 OR ca.recordType = 2 OR ca.recordType = 6 OR ca.recordType = 7 OR ca.recordType = 10 OR ca.recordType = 12) AND (ca.opTime < c.opTime or ca.opTime = c.opTime) and ca.id!=c.id AND ca.userId = c.userId) "
						+ "WHEN c.recordType = 6 AND (SELECT count(userId) > 1 or count(userId) =1 FROM cardrecord ca WHERE ca.opTime < c.opTime AND (ca.recordType = 0 OR ca.recordType = 2 OR ca.recordType = 6 OR ca.recordType = 7 OR ca.recordType = 10 OR ca.recordType = 12) AND ca.userId = c.userId) THEN c.opFare +( SELECT sum(ca.opFare+ca.subsidyOddFare) FROM cardrecord ca WHERE (ca.recordType = 0 OR ca.recordType = 2 OR ca.recordType = 6 OR ca.recordType = 7 OR ca.recordType = 10 OR ca.recordType = 12) AND ca.opTime < c.opTime AND ca.userId = c.userId)	 WHEN c.recordType = 2 THEN ("
						+ "SELECT(A.cardOddFare + A.opFare + A.subsidyOddFare + C.cardOddFare + C.opFare + C.subsidyOddFare) FROM cardrecord A WHERE a.recordType=0 AND c.userId=a.userId GROUP BY userId ) WHEN c.recordType = 1 THEN(SELECT sum(A.cardOddFare + A.opFare + A.subsidyOddFare + C.cardOddFare + C.subsidyOddFare) FROM cardrecord A WHERE A.opTime = C.opTime AND A.id <> c.id GROUP BY userId)"
						+ " WHEN c.recordType = 7  THEN(SELECT(A.cardOddFare + A.opFare + A.subsidyOddFare + C.opFare + C.subsidyOddFare) FROM cardrecord A WHERE A.opTime = C.opTime AND A.id <> c.id GROUP BY userId)  WHEN c.recordType = 8 AND (SELECT count(userId) > 1 or count(userId)=1 FROM cardrecord ca WHERE ca.opTime < c.opTime AND ca.recordType = 8 AND ca.userId = c.userId) THEN c.oddFare + ( SELECT sum(ca.opFare) FROM cardrecord ca WHERE ca.recordType = 8 AND ca.opTime < c.opTime AND ca.userId = c.userId)"
						+ " 	ELSE (SELECT sum(IF (ca.opTime < c.opTime,ca.opFare+ca.subsidyOddFare,0))FROM cardrecord ca WHERE ca.userId = c.userId AND( ca.recordType=0 or ca.recordType=2 OR ca.recordType = 7 or ca.recordType=6 or ca.recordType=10 or ca.recordType=12) GROUP By c.userId) END	) AS sumFare,"
						+ "(CASE WHEN c.recordType = 0 or c.recordType =6 THEN c.oddFare+c.opFare when c.recordType = 8 then c.oddFare-c.opFare WHEN c.recordType = 2 THEN"
						+ " (SELECT(A.oddFare + A.opFare +C.oddFare + C.opFare) FROM cardrecord A WHERE a.recordType=0 AND c.userId=a.userId GROUP BY userId) 	WHEN c.recordType = 1 THEN(SELECT sum(A.oddFare + A.opFare +C.oddFare) FROM cardrecord A WHERE A.opTime = C.opTime AND A.id <> c.id GROUP BY userId)"
						+ "WHEN c.recordType = 7  THEN(SELECT(A.oddFare + A.opFare + C.opFare) FROM cardrecord A WHERE A.opTime = C.opTime AND A.id <> c.id GROUP BY userId) when c.recordType= 9 then 0 ELSE c.oddFare END) AS oddFare,c.opFare,(case when c.recordType =10 or c.recordType =12 then c.subsidyOddFare else 0 end) subsidyOpFare,c.recordType as consumeType,c.subsidyOddFare,c.cardOpCount AS opCount,c.subsidyOpCount,c.opTime,1 froms from %s where %s) t %s limit %s,%s",
				fromSql1, whereSql1, fromSql2, whereSql2, orderSql, pagination.getOffset(), pagination.getNumPerPage());

		String sql1 = String.format("select count(t.userId) as total from (select u.userId from %s where %s union all select u.userId from %s where %s) t", fromSql1, whereSql1, fromSql2, whereSql2);

		List<Map> list = this.commonService.selectListBySql(sql);
		List<Map> totalList = this.commonService.selectListBySql(sql1);
		Integer totalCount = 0;
		for (Map map : totalList) {
			totalCount = Integer.valueOf(map.get("total").toString());
		}

		for (Map m : list) {
			if (m.get("oddFare") == null) {
				m.put("oddFare", 0);
			}
			if (m.get("opFare") == null) {
				m.put("opFare", 0);
			}
			if (m.get("subsidyOddFare") == null) {
				m.put("subsidyOddFare", 0);
			}
			if (m.get("sumFare") == null) {
				m.put("sumFare", 0);
			}

			m.put("oddFare", Float.valueOf(m.get("oddFare").toString()) / 100);
			m.put("opFare", Float.valueOf(m.get("opFare").toString()) / 100);
			m.put("subsidyOddFare", Float.valueOf(m.get("subsidyOddFare").toString()) / 100);
			m.put("subsidyOpFare", Float.valueOf(m.get("subsidyOpFare").toString()) / 100);
			m.put("sumFare", Float.valueOf(m.get("sumFare").toString()) / 100);

			if ((Long) m.get("froms") == 0) {
				m.put("consumeType", ConsumeType.getTypeDes(Integer.valueOf(m.get("consumeType").toString())));
			} else {
				m.put("consumeType", CardRecord.recordTypes[StringUtil.objToInt(m.get("consumeType"))]);
			}
		}

		if (export != null && 1 == export) {

			String[] expColumns = { "学号", "姓名", "卡号", "记录类型", "卡操作额", "卡总额", "卡余额", "补助操作额", "补助余额", "卡操作计数器", "补助操作计数器", "操作时间" };

			List<List<String>> exportList = new ArrayList<List<String>>();

			for (Map m : list) {
				List<String> list1 = new ArrayList<String>();
				list1.add(StringUtil.objToString(m.get("userNO")));
				list1.add(StringUtil.objToString(m.get("username")));
				list1.add(StringUtil.objToString(m.get("cardNO")));
				list1.add(StringUtil.objToString(m.get("consumeType")));
				list1.add(StringUtil.objToString(m.get("opFare")));
				list1.add(StringUtil.objToString(m.get("sumFare")));
				list1.add(StringUtil.objToString(m.get("oddFare")));

				list1.add(StringUtil.objToString(m.get("subsidyOpFare")));
				list1.add(StringUtil.objToString(m.get("subsidyOddFare")));

				list1.add(StringUtil.objToString(m.get("opCount")));
				list1.add(StringUtil.objToString(m.get("subsidyOpCount")));
				list1.add(StringUtil.objToString(m.get("opTime")));
				exportList.add(list1);
			}

			ExportUtil.exportExcel("个人记录表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}

		model.addAttribute("list", list);
		model.addAttribute("froms", froms);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("nameStr", nameStr);

		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);

		model.addAttribute("recordType", recordType);
		model.addAttribute("consumeType", consumeType);
		model.addAttribute("recordTypes", CardRecord.recordTypes);
		model.addAttribute("base", StringUtil.requestBase(request));

		return StringUtil.requestPath(request, "list");
	}
}
