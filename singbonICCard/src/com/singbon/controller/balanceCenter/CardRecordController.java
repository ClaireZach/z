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
 * 卡操作记录查询控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/balanceCenter/cardRecord")
public class CardRecordController extends BaseController {

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
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, Integer queryType, Integer operId, Integer recordType, String beginDate, String endDate,
			String includeOff, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		if (queryType == null)
			queryType = 0;

		List<Map> list = null;

		String fromSql = "cardRecord c left join user u on c.userId=u.userId left join sysUser o on c.operId=o.operId";
		String whereSql = "u.companyId=" + company.getId();

		if (!StringUtils.isEmpty(operId) && operId != 0) {
			whereSql += String.format(" and c.operId = %s", operId);
		}
		if (queryType == 1 && !StringUtils.isEmpty(recordType) && recordType != -1) {
			whereSql += String.format(" and c.recordType = %s", recordType);
		}

		if (!StringUtils.isEmpty(beginDate)) {
			whereSql += String.format(" and c.opTime >= '%s'", beginDate);
		}else{
			beginDate =StringUtil.getNowTimeForDataCenterBg();
			whereSql += String.format(" and c.opTime >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql += String.format(" and c.opTime <= '%s'", endDate);
		}else{
			endDate = StringUtil.getNowTimeForDataCenterEd();
			whereSql += String.format(" and c.opTime <= '%s'",endDate);
		}
		if (StringUtils.isEmpty(includeOff)) {
			whereSql += " and u.status != 244";
		}

		//明细本页汇总
		BigDecimal sumopFare = new BigDecimal(0);
		BigDecimal sumoddFare =new BigDecimal(0);
		BigDecimal sumsubsidyOddFare = new BigDecimal(0);
		BigDecimal sumcardOddFare = new BigDecimal(0);
		BigDecimal sumcardSubsidyOddFare =new BigDecimal(0);
		//明细全部汇总
		BigDecimal sumopFareAll = new BigDecimal(0);
		BigDecimal sumoddFareAll = new BigDecimal(0);
		BigDecimal sumsubsidyOddFareAll =new BigDecimal(0);
		BigDecimal sumcardOddFareAll =new BigDecimal(0);
		BigDecimal sumcardSubsidyOddFareAll = new BigDecimal(0);
		
		//统计全部汇总
		float totalmakeCardFare = 0.00f;
		float totalPCSaving = 0.00f;
		float totalmakeCardGiveFare = 0.00f;
		float totalPCSavingGiveFare = 0.00f;
		float totalgetCardDeposit = 0.00f;
		float totalposSubsidySaving = 0.00f;
		float totalwaterSubsidySaving = 0.00f;
		float totalPCTake = 0.00f;
		float totalbackCardDepostFare = 0.00f;
		float totalposSubsidyClear = 0.00f;
		float totalwaterSubsidyClear = 0.00f;
		float totalmakeCardCount = 0.00f;
		float totalremakeCard = 0.00f;
		float totalloss = 0.00f;
		float totalunloss = 0.00f;
		float totalcardOff = 0.00f;
		float totalupdateByCard = 0.00f;
		float totalupdateByUserInfo = 0.00f;
		
		
		// 统计查询
		if (queryType == 0) {
			String sql = String.format(
					"select o.loginName,sum(case recordType when 0 then opFare else 0 end) makeCardFare,sum(case recordType when 0 then 1 else 0 end) makeCardCount,"
							+ "sum(case recordType when 1 then opFare else 0 end) getCardDeposit,sum(case recordType when 2 then opFare else 0 end) makeCardGiveFare,"
							+ "sum(case recordType when 3 then 1 else 0 end) remakeCard,sum(case recordType when 4 then 1 else 0 end) loss,"
							+ "sum(case recordType when 5 then 1 else 0 end) unloss,sum(case recordType when 6 then opFare else 0 end) PCSaving,"
							+ "sum(case recordType when 7 then opFare else 0 end) PCSavingGiveFare,sum(case recordType when 8 then opFare else 0 end) PCTake,"
							+ "sum(case recordType when 9 then opFare else 0 end) backCardDepostFare,sum(case recordType when 10 then opFare else 0 end) posSubsidySaving,"
							+ "sum(case recordType when 11 then opFare else 0 end) posSubsidyClear,sum(case recordType when 12 then opFare else 0 end) waterSubsidySaving,"
							+ "sum(case recordType when 13 then opFare else 0 end) waterSubsidyClear,sum(case recordType when 14 then 1 else 0 end) cardOff,"
							+ "sum(case recordType when 15 then 1 else 0 end) updateByCard,sum(case recordType when 16 then 1 else 0 end) updateByUserInfo from %s where %s group by c.operId",
					fromSql, whereSql);
			list = this.commonService.selectListBySql(sql);
			StringUtil.println("Tongji   " + list);
			for (Map map : list) {
				map.put("loginName", DesUtil.decrypt(StringUtil.objToString(map.get("loginName"))));
				map.put("makeCardFare", ((float) StringUtil.objToInt(map.get("makeCardFare")) /100));
				map.put("getCardDeposit", ((float) StringUtil.objToInt(map.get("getCardDeposit")) /100));
				map.put("makeCardGiveFare", ((float) StringUtil.objToInt(map.get("makeCardGiveFare")) /100));
				map.put("PCSaving", ((float) StringUtil.objToInt(map.get("PCSaving")) /100));
				map.put("PCSavingGiveFare", ((float) StringUtil.objToInt(map.get("PCSavingGiveFare")) /100));
				map.put("PCTake", ((float) StringUtil.objToInt(map.get("PCTake")) /100));
				map.put("backCardDepostFare", ((float) StringUtil.objToInt(map.get("backCardDepostFare")) /100));
				map.put("posSubsidySaving", ((float) StringUtil.objToInt(map.get("posSubsidySaving")) /100));
				map.put("posSubsidyClear", ((float) StringUtil.objToInt(map.get("posSubsidyClear")) /100));
				map.put("waterSubsidySaving", ((float) StringUtil.objToInt(map.get("waterSubsidySaving")) /100));
				map.put("waterSubsidyClear", ((float) StringUtil.objToInt(map.get("waterSubsidyClear")) /100));
				
				totalmakeCardFare +=  StringUtil.objToFloat(map.get("makeCardFare"));
				totalPCSaving +=  StringUtil.objToFloat(map.get("PCSaving"));
				totalmakeCardGiveFare +=  StringUtil.objToFloat(map.get("makeCardGiveFare"));
				totalPCSavingGiveFare +=  StringUtil.objToFloat(map.get("PCSavingGiveFare"));
				totalgetCardDeposit +=  StringUtil.objToFloat(map.get("getCardDeposit"));
				totalposSubsidySaving +=  StringUtil.objToFloat(map.get("posSubsidySaving"));
				totalwaterSubsidySaving +=  StringUtil.objToFloat(map.get("waterSubsidySaving"));
				totalPCTake +=  StringUtil.objToFloat(map.get("PCTake"));
				totalbackCardDepostFare +=  StringUtil.objToFloat(map.get("backCardDepostFare"));
				totalposSubsidyClear +=  StringUtil.objToFloat(map.get("posSubsidyClear"));
				totalwaterSubsidyClear +=  StringUtil.objToFloat(map.get("waterSubsidyClear"));
				totalmakeCardCount +=  StringUtil.objToFloat(map.get("makeCardCount"));
				totalremakeCard +=  StringUtil.objToFloat(map.get("remakeCard"));
				totalloss +=  StringUtil.objToFloat(map.get("loss"));
				totalunloss +=  StringUtil.objToFloat(map.get("unloss"));
				totalcardOff +=  StringUtil.objToFloat(map.get("cardOff"));
				totalupdateByCard +=  StringUtil.objToFloat(map.get("updateByCard"));
				totalupdateByUserInfo +=  StringUtil.objToFloat(map.get("updateByUserInfo"));
			}
			if (export != null && 1 == export) {
				String[] expColumns = { "出纳员", "发卡金额", "PC存款", "发卡赠送金额", "存款赠送金额", "收取卡押金", "消费机补助存款", "水控补助存款", "PC取款", "退还卡押金", "消费机补助清零", "水控补助清零", "发卡", "补卡", "挂失", "解挂", "卡注销", "按卡修正", "按库修正" };

				List<List<String>> exportList = new ArrayList<List<String>>();
				for (Map m : list) {
					List<String> list2 = new ArrayList<String>();
					list2.add(StringUtil.objToString(m.get("loginName")));
					list2.add(StringUtil.objToString(m.get("makeCardFare")));
					list2.add(StringUtil.objToString(m.get("PCSaving")));
					list2.add(StringUtil.objToString(m.get("makeCardGiveFare")));
					list2.add(StringUtil.objToString(m.get("PCSavingGiveFare")));
					list2.add(StringUtil.objToString(m.get("getCardDeposit")));
					list2.add(StringUtil.objToString(m.get("posSubsidySaving")));
					list2.add(StringUtil.objToString(m.get("waterSubsidySaving")));
					list2.add(StringUtil.objToString(m.get("PCTake")));
					list2.add(StringUtil.objToString(m.get("backCardDepostFare")));
					list2.add(StringUtil.objToString(m.get("posSubsidyClear")));
					list2.add(StringUtil.objToString(m.get("waterSubsidyClear")));
					list2.add(StringUtil.objToString(m.get("makeCardCount")));
					list2.add(StringUtil.objToString(m.get("remakeCard")));
					list2.add(StringUtil.objToString(m.get("loss")));
					list2.add(StringUtil.objToString(m.get("unloss")));
					list2.add(StringUtil.objToString(m.get("cardOff")));
					list2.add(StringUtil.objToString(m.get("updateByCard")));
					list2.add(StringUtil.objToString(m.get("updateByUserInfo")));

					exportList.add(list2);
				}

				ExportUtil.exportExcel("卡操作统计表", expColumns, exportList, response);
				return null;
			}
			// 明细
		} else {
			if (exportType != null && 1 == exportType) {
				pagination.setPageNum(1);
				pagination.setNumPerPage(pagination.getTotalCount());
			}
			String[] columns = { "o.loginName", "u.username", "u.userNO", "c.cardNO", "c.cardSN", "c.recordType", "c.opFare", "c.oddFare", "c.subsidyOddFare", "c.cardOddFare", "c.cardSubsidyOddFare",	"c.opCount", "c.subsidyOpCount", "c.cardOpCount", "c.cardSubsidyOpCount", "c.opTime" };

			list = this.commonService.selectByPage(columns, null, fromSql, whereSql, pagination);
			
			List<Map> listAll = this.commonService.selectNoPage(columns, null, fromSql, whereSql);
			int totalCount = Integer.valueOf(list.get(0).get("loginName").toString());
			list.remove(0);
			listAll.remove(0);
			
			for (Map map : listAll) {
				map.put("opFare", (float) StringUtil.objToInt(map.get("opFare")) / 100);
				map.put("oddFare", (float) StringUtil.objToLong(map.get("oddFare")) / 100);
				map.put("subsidyOddFare", (float) StringUtil.objToInt(map.get("subsidyOddFare")) / 100);
				map.put("cardOddFare", (float) StringUtil.objToLong(map.get("cardOddFare")) / 100);
				map.put("cardSubsidyOddFare", (float) StringUtil.objToInt(map.get("cardSubsidyOddFare")) / 100);
				
				//sumopFareAll += StringUtil.objToFloat(map.get("opFare"));
				sumopFareAll = sumopFareAll.add(new BigDecimal(map.get("opFare").toString()));
				
				
				//sumoddFareAll += StringUtil.objToFloat(map.get("oddFare"));
				sumoddFareAll = sumoddFareAll.add(new BigDecimal(map.get("oddFare").toString()));
				
				//sumsubsidyOddFareAll += StringUtil.objToFloat(map.get("subsidyOddFare"));
				sumsubsidyOddFareAll = sumsubsidyOddFareAll.add(new BigDecimal(map.get("subsidyOddFare").toString()));
				
				//sumcardOddFareAll += StringUtil.objToFloat(map.get("cardOddFare"));
				sumcardOddFareAll = sumcardOddFareAll.add(new BigDecimal(map.get("cardOddFare").toString()));
				
				//sumcardSubsidyOddFareAll += StringUtil.objToFloat(map.get("cardSubsidyOddFare"));
				sumcardSubsidyOddFareAll = sumcardSubsidyOddFareAll.add(new BigDecimal(map.get("cardSubsidyOddFare").toString()));
			}
			
			
			for (Map map : list) {
				map.put("loginName", DesUtil.decrypt(StringUtil.objToString(map.get("loginName"))));
				map.put("opFare", (float) StringUtil.objToInt(map.get("opFare")) / 100);
				map.put("oddFare", (float) StringUtil.objToLong(map.get("oddFare")) / 100);
				map.put("subsidyOddFare", (float) StringUtil.objToInt(map.get("subsidyOddFare")) / 100);
				map.put("cardOddFare", (float) StringUtil.objToLong(map.get("cardOddFare")) / 100);
				map.put("cardSubsidyOddFare", (float) StringUtil.objToInt(map.get("cardSubsidyOddFare")) / 100);
				
				//sumopFare += StringUtil.objToFloat(map.get("opFare"));
				sumopFare = sumopFare.add(new BigDecimal(map.get("opFare").toString()));
				
				//sumoddFare += StringUtil.objToFloat(map.get("oddFare"));
				sumoddFare = sumoddFare.add(new BigDecimal(map.get("oddFare").toString()));
				
				//sumsubsidyOddFare += StringUtil.objToFloat(map.get("subsidyOddFare"));
				sumsubsidyOddFare = sumsubsidyOddFare.add(new BigDecimal(map.get("subsidyOddFare").toString()));
				
				//sumcardOddFare += StringUtil.objToFloat(map.get("cardOddFare"));
				sumcardOddFare = sumcardOddFare.add(new BigDecimal(map.get("cardOddFare").toString()));
				
				//sumcardSubsidyOddFare += StringUtil.objToFloat(map.get("cardSubsidyOddFare"));
				sumcardSubsidyOddFare = sumcardSubsidyOddFare.add(new BigDecimal(map.get("cardSubsidyOddFare").toString()));
				
				map.put("recordTypeDes", CardRecord.recordTypes[StringUtil.objToInt(map.get("recordType"))]);
			}

			if (export != null && 1 == export) {
				String[] expColumns = { "出纳员姓名", "姓名", "编号", "卡号", "物理卡号", "操作类型", "操作额", "库大钱包", "库补助钱包", "卡大钱包", "卡补助钱包", "库计数器", "库补助计数器", "卡计数器", "卡补助计数器", "操作时间" };

				List<List<String>> exportList = new ArrayList<List<String>>();
				for (Map m : list) {
					List<String> list2 = new ArrayList<String>();

					list2.add(StringUtil.objToString(m.get("loginName")));
					list2.add(StringUtil.objToString(m.get("username")));
					list2.add(StringUtil.objToString(m.get("userNO")));
					list2.add(StringUtil.objToString(m.get("cardNO")));
					list2.add(StringUtil.objToString(m.get("cardSN")));
					list2.add(StringUtil.objToString(m.get("recordTypeDes")));
					list2.add(StringUtil.objToString(m.get("opFare")));
					list2.add(StringUtil.objToString(m.get("oddFare")));
					list2.add(StringUtil.objToString(m.get("subsidyOddFare")));
					list2.add(StringUtil.objToString(m.get("cardOddFare")));
					list2.add(StringUtil.objToString(m.get("cardSubsidyOddFare")));
					list2.add(StringUtil.objToString(m.get("opCount")));
					list2.add(StringUtil.objToString(m.get("subsidyOpCount")));
					list2.add(StringUtil.objToString(m.get("cardOpCount")));
					list2.add(StringUtil.objToString(m.get("cardSubsidyOpCount")));

					list2.add(StringUtil.objToString(m.get("opTime")));
					exportList.add(list2);
				}

				ExportUtil.exportExcel("卡操作明细表", expColumns, exportList, response);
				return null;
			}
			model.addAttribute("pageNum", pagination.getPageNum());
			model.addAttribute("numPerPage", pagination.getNumPerPage());
			model.addAttribute("totalCount", totalCount);
		}

		model.addAttribute("list", list);
		model.addAttribute("queryType", queryType);
		model.addAttribute("operId", operId);
		model.addAttribute("recordType", recordType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);
		model.addAttribute("sysUserList", sysUserList);
		model.addAttribute("recordTypes", CardRecord.recordTypes);
		//明细本页
		model.addAttribute("sumopFare", sumopFare);
		model.addAttribute("sumoddFare",sumoddFare );
		model.addAttribute("sumsubsidyOddFare",sumsubsidyOddFare );
		model.addAttribute("sumcardOddFare", sumcardOddFare);
		model.addAttribute("sumcardSubsidyOddFare",sumcardSubsidyOddFare );
		//明细全部
		model.addAttribute("sumopFareAll", sumopFareAll);
		model.addAttribute("sumoddFareAll",sumoddFareAll );
		model.addAttribute("sumsubsidyOddFareAll",sumsubsidyOddFareAll );
		model.addAttribute("sumcardOddFareAll", sumcardOddFareAll);
		model.addAttribute("sumcardSubsidyOddFareAll",sumcardSubsidyOddFareAll );
		//统计全部汇总
		model.addAttribute("totalmakeCardFare", totalmakeCardFare);
		model.addAttribute("totalPCSaving", totalPCSaving);
		model.addAttribute("totalmakeCardGiveFare", totalmakeCardGiveFare);
		model.addAttribute("totalPCSavingGiveFare", totalPCSavingGiveFare);
		model.addAttribute("totalgetCardDeposit", totalgetCardDeposit);
		model.addAttribute("totalposSubsidySaving", totalposSubsidySaving);
		model.addAttribute("totalwaterSubsidySaving", totalwaterSubsidySaving);
		model.addAttribute("totalPCTake", totalPCTake);
		model.addAttribute("totalbackCardDepostFare", totalbackCardDepostFare);
		model.addAttribute("totalposSubsidyClear", totalposSubsidyClear);
		model.addAttribute("totalwaterSubsidyClear", totalwaterSubsidyClear);
		model.addAttribute("totalmakeCardCount", totalmakeCardCount);
		model.addAttribute("totalremakeCard", totalremakeCard);
		model.addAttribute("totalloss", totalloss);
		model.addAttribute("totalunloss", totalunloss);
		model.addAttribute("totalcardOff", totalcardOff);
		model.addAttribute("totalupdateByCard", totalupdateByCard);
		model.addAttribute("totalupdateByUserInfo", totalupdateByUserInfo);
		
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
