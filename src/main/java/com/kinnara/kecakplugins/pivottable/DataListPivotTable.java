package com.kinnara.kecakplugins.pivottable;

import org.joget.plugin.base.PluginManager;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionResult;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.userview.model.Userview;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.joget.apps.datalist.model.DataListCollection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataListPivotTable extends UserviewMenu {
    @Override
    public String getCategory() {
        return "Kecak";
    }

    @Override
    public String getIcon() {
        return "/plugin/org.joget.apps.userview.lib.HtmlPage/images/grid_icon.gif";
    }

    @Override
    public String getRenderPage() {

        Map<String, Object> dataModel = new HashMap<String, Object>();

        ApplicationContext appContext    = AppUtil.getApplicationContext();
        PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");

        /*DataList dataList = getDataList();
        if (dataList != null) {
            dataModel.put("data",dataList);
        }*/

        List<Test> data = Stream
                .of(new Test(1, "ramba"), new Test(2, "Pitter"), new Test(3, "Muslim"))
                .collect(Collectors.toList());

        dataModel.put("data", data);

        //masih error ngerender page
        String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/pivotTable.ftl",null);
        return htmlContent;
    }

    @Override
    public boolean isHomePageSupported() {
        return true;
    }


    @Override
    public String getDecoratedMenu() {
        return null;
    }


    /*protected void viewList() {
        try {
            DataList dataList = getDataList();
            if (dataList != null) {
                LogUtil.info(getClass().getName(),"datalist: "+dataList);
                this.setProperty("dataList", dataList);
            } else {
                this.setProperty("error", ("Data List \"" + getPropertyString("datalistId") + "\" not exist."));
            }
        }
        catch (Exception ex) {
            StringWriter out = new StringWriter();
            ex.printStackTrace(new PrintWriter(out));
            String message = ex.toString();
            message = message + "\r\n<pre class=\"stacktrace\">" + out.getBuffer() + "</pre>";
            this.setProperty("error", message);
        }
    }*/
    @Override
    public String getName() {
        return "Pivot Table";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Show Datalist Pivot Table";
    }

    @Override
    public String getLabel() {
        return "Pivot Table";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String appId = appDef.getId();
        String appVersion = appDef.getVersion().toString();
        Object[] arguments = new Object[]{appId, appVersion, appId, appVersion, appId, appVersion};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", arguments, true,null);
        return json;
        //return AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", null, true, null);
    }

    /*protected DataList getDataList() throws BeansException {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String formId = getPropertyString("formId");
        String tableName="";
        if (appDef != null &&  formId != null) {
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            tableName = appService.getFormTableName(appDef, formId);
        }
        DataList dataList = new DataList();
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        FormRowSet rowSetData = formDataDao.find(formId,tableName, null, null, null, false, null, null);
        LogUtil.info(getClass().getName(),"tableName:"+tableName);
        if(rowSetData!=null){
            for(FormRow rowData : rowSetData){
                dataList.addBinderProperty("Kolom1",rowData.getProperty(getPropertyString("pivotColoumnId")));
                dataList.addBinderProperty("Kolom2",rowData.getProperty(getPropertyString("pivotRowId")));
            }
            LogUtil.info(getClass().getName(),dataList.toString());
            return  dataList;
        }
        return null;

    }*/
}
