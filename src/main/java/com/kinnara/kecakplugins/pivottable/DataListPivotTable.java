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
import org.kecak.apps.userview.model.AceUserviewMenu;
import org.kecak.apps.userview.model.AdminLteUserviewMenu;
import org.kecak.apps.userview.model.BootstrapUserviewTheme;
import org.mockito.internal.matchers.Null;
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
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataListPivotTable extends UserviewMenu implements AceUserviewMenu, AdminLteUserviewMenu {
    //private WeakHashMap<String, DataList> datalistCache = new WeakHashMap<>();

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
        try {
            Map<String, Object> dataModel = new HashMap<String, Object>();

            ApplicationContext appContext    = AppUtil.getApplicationContext();
            PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");
            String formDefId    =  getPropertyString("formDefId");
            String tableName    =   "";
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            if (appDef != null && formDefId != null) {
                AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
                tableName = appService.getFormTableName(appDef, formDefId);
            }

            //DataList dataList = getDataList();
            DataListCollection<Map<String, Object>> resultListRow = new DataListCollection<>();
            DataListCollection<Map<String, Object>> resultListColoumn = new DataListCollection<>();
            DataListCollection<Map<String, Object>> resultListValue = new DataListCollection<>();
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            FormRowSet rowSetData = formDataDao.find(formDefId,tableName, null, null, null, false, null, null);

            String rowId = getPropertyString("pivotRowId");
            String coloumnId = getPropertyString("pivotColoumnId");
            if(rowSetData!=null) {
                String rows = "";
                String coloumn = "";
                for (FormRow rowDataC : rowSetData) {
                /*if (!rows.contains(rowDataC.getProperty("namaBuah"))) {
                    dataTable.put("rows", rowDataC.getProperty("namaBuah"));
                    //dataList.
                    rows += rowDataC.getProperty("namaBuah");
                    rows += ";";
                }*/
                    if (!coloumn.contains(rowDataC.getProperty(coloumnId))) {
                        if(rowDataC.getProperty(coloumnId)!=null && rowDataC.getProperty(coloumnId)!="") {
                            HashMap<String, Object> dataTableC = new HashMap<String, Object>();
                            dataTableC.put("coloumn", rowDataC.getProperty(coloumnId));
                            coloumn += rowDataC.getProperty(coloumnId);
                            coloumn += ";";
                            resultListColoumn.add(dataTableC);
                        }
                    }
                    /*for(FormRow rows : rowSetData) {
                        dataList.addBinderProperty("coloumn", rows.getProperty(getPropertyString("pivotColoumnId")));

                    }*/

                }
                int total = 0;
                for (FormRow rowData : rowSetData) {
                    if (!rows.contains(rowData.getProperty(rowId)) && rowData.getProperty(rowId)!=null && rowData.getProperty(rowId)!="")
                    {
                        HashMap<String, Object> dataTable = new HashMap<String, Object>();
                        dataTable.put("rows", rowData.getProperty(rowId));
                        String[] coloum = coloumn.split(";");
                        //int n =1;
                        for(String coloumnData : coloum){
                            HashMap<String, Object> dataValue = new HashMap<String, Object>();
                            int value = 0;
                            for(FormRow rowValue : rowSetData){
                                if(rowValue.getProperty(coloumnId).equalsIgnoreCase(coloumnData) && rowValue.getProperty(rowId).equalsIgnoreCase(rowData.getProperty(rowId))){
                                    value++;
                                    if(rowData.getProperty(rowId).equalsIgnoreCase(coloumnData)) {
                                        total++;
                                    }
                                    LogUtil.info(getClass().getName(),"Total"+total);
                                    /*LogUtil.info(getClass().getName(),"Total"+total);*/
                                }
                            }
                            //if(rowData.getProperty("rasaBuah").equalsIgnoreCase("manis")) {
                            dataValue.put("row" + rowData.getProperty(rowId), value);
                            resultListValue.add(dataValue);
                            LogUtil.info(getClass().getName(),"Total"+total);
                        }

                        dataModel.put("totalBTC",total);
                        rows += rowData.getProperty(rowId);
                        rows += ";";
                        resultListRow.add(dataTable);
                    }
                }
            }
            dataModel.put("dataRow", resultListRow.getList());
            dataModel.put("dataColoumn", resultListColoumn.getList());
            dataModel.put("dataValue",resultListValue.getList());
            LogUtil.info(getClassName(), "dataValue: "+resultListValue.getList());
            //LogUtil.info(getClassName(), "dataList: "+resultListColoumn.getList());
            //dataModel.put("total",dataList.getSize());
            dataModel.put("row",rowId);
            dataModel.put("coloum",coloumnId);
            dataModel.put("total",resultListRow.getList().size());
            String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/pivotTable.ftl",null);
            return htmlContent;
        }catch (Exception e) {
            LogUtil.error(getClassName(), e, "------" + e.getMessage());
            return null;
        }
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
        /*AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String appId = appDef.getId();
        String appVersion = appDef.getVersion().toString();
        Object[] arguments = new Object[]{appId, appVersion, appId, appVersion, appId, appVersion};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", arguments, true,null);
        return json;*/
        return AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", null, true, null);
    }

    @Override
    public String getAceJspPage(BootstrapUserviewTheme bootstrapUserviewTheme) {
        return null;
    }

    @Override
    public String getAceRenderPage() {
        try {
            Map<String, Object> dataModel = new HashMap<String, Object>();
            String contextPath = AppUtil.getRequestContextPath();
            ApplicationContext appContext    = AppUtil.getApplicationContext();
            PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");
            String formDefId    =  getPropertyString("formDefId");
            String tableName    =   "";
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            if (appDef != null && formDefId != null) {
                AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
                tableName = appService.getFormTableName(appDef, formDefId);
            }

            //DataList dataList = getDataList();
            DataListCollection<Map<String, Object>> resultListRow = new DataListCollection<>();
            DataListCollection<Map<String, Object>> resultListColoumn = new DataListCollection<>();
            DataListCollection<Map<String, Object>> resultListValue = new DataListCollection<>();
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            FormRowSet rowSetData = formDataDao.find(formDefId,tableName, null, null, null, false, null, null);

            String rowId = getPropertyString("pivotRowId");
            String coloumnId = getPropertyString("pivotColoumnId");
            if(rowSetData!=null) {
                String rows = "";
                String coloumn = "";
                for (FormRow rowDataC : rowSetData) {
                /*if (!rows.contains(rowDataC.getProperty("namaBuah"))) {
                    dataTable.put("rows", rowDataC.getProperty("namaBuah"));
                    //dataList.
                    rows += rowDataC.getProperty("namaBuah");
                    rows += ";";
                }*/
                if (!coloumn.contains(rowDataC.getProperty(coloumnId))) {
                    if(rowDataC.getProperty(coloumnId)!=null && rowDataC.getProperty(coloumnId)!="") {
                        HashMap<String, Object> dataTableC = new HashMap<String, Object>();
                        dataTableC.put("coloumn", rowDataC.getProperty(coloumnId));
                        coloumn += rowDataC.getProperty(coloumnId);
                        coloumn += ";";
                        resultListColoumn.add(dataTableC);
                    }
                }
                    /*for(FormRow rows : rowSetData) {
                        dataList.addBinderProperty("coloumn", rows.getProperty(getPropertyString("pivotColoumnId")));

                    }*/

            }
            int total = 0;
            for (FormRow rowData : rowSetData) {
                if (!rows.contains(rowData.getProperty(rowId)) && rowData.getProperty(rowId)!=null && rowData.getProperty(rowId)!="")
                {
                    HashMap<String, Object> dataTable = new HashMap<String, Object>();
                    dataTable.put("rows", rowData.getProperty(rowId));
                    String[] coloum = coloumn.split(";");
                    //int n =1;

                    for(String coloumnData : coloum){
                        //String var = "dataValue"+rowData.getProperty("rasaBuah");
                        HashMap<String, Object> dataValue = new HashMap<String, Object>();
                        int value = 0;
                        for(FormRow rowValue : rowSetData){
                            if(rowValue.getProperty(coloumnId).equalsIgnoreCase(coloumnData) && rowValue.getProperty(rowId).equalsIgnoreCase(rowData.getProperty(rowId))){
                                value++;
                                /*if(rowData.getProperty("rasaBuah").equalsIgnoreCase("manis")) {
                                    total++;
                                }*/
                                //LogUtil.info(getClass().getName(),"Total"+total);
                            }
                        }
                        //if(rowData.getProperty("rasaBuah").equalsIgnoreCase("manis")) {
                            dataValue.put("row" + rowData.getProperty(rowId), value);
                            resultListValue.add(dataValue);
                       // }
                    }
                    dataModel.put("totalManis",total);
                    rows += rowData.getProperty(rowId);
                    rows += ";";
                    resultListRow.add(dataTable);
                }
                /*if (!coloumn.contains(rowData.getProperty("rasaBuah"))) {
                    dataTable.put("coloumn", rowData.getProperty("rasaBuah"));
                    coloumn += rowData.getProperty("rasaBuah");
                    coloumn += ";";
                }*/
                    /*for(FormRow rows : rowSetData) {
                        dataList.addBinderProperty("coloumn", rows.getProperty(getPropertyString("pivotColoumnId")));

                    }*/
            }

        }

        //dataList.setRows();
        //if (dataList != null) {
            //dataModel.put("data",dataList);
        //}

            /*List<Test> data = Stream
                    .of(new Test(1, "ramba"), new Test(2, "Pitter"), new Test(3, "Muslim"))
                    .collect(Collectors.toList());*/

           /* List<DataList> data = Stream
                    .of(resultList)
                    .collect(Collectors.toList());*/

            dataModel.put("dataRow", resultListRow.getList());
            dataModel.put("dataColoumn", resultListColoumn.getList());
            dataModel.put("dataValue",resultListValue.getList());
            LogUtil.info(getClassName(), "dataValue: "+resultListValue.getList());
            //LogUtil.info(getClassName(), "dataList: "+resultListColoumn.getList());
            //dataModel.put("total",dataList.getSize());
            dataModel.put("row",rowId);
            dataModel.put("coloum",coloumnId);
            dataModel.put("total",resultListRow.getList().size());
            String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/pivotTableAceAdmin.ftl",null);
            return htmlContent;
        }catch (Exception e) {
            LogUtil.error(getClassName(), e, "------" + e.getMessage());
            return null;
        }
    }

    @Override
    public String getAceDecoratedMenu() {
        return null;
    }

    @Override
    public String getAdminLteJspPage(BootstrapUserviewTheme bootstrapUserviewTheme) {
        return null;
    }

    @Override
    public String getAdminLteRenderPage() {
        return null;
    }

    @Override
    public String getAdminLteDecoratedMenu() {
        return null;
    }

    /*protected DataList getDataList()  {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
       *//* String formId = getPropertyString("formId");
        String tableName="";
        if (appDef != null &&  formId != null) {
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            tableName = appService.getFormTableName(appDef, formId);
        }*//*
        DataList dataList = new DataList();
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        *//*FormRowSet rowSetData = formDataDao.find(formId,tableName, null, null, null, false, null, null);
        LogUtil.warn(getClass().getName(),"tableName:"+tableName);*//*
        //FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        FormRowSet rowSetData = formDataDao.find("daftarBuah","m_buah", null, null, null, false, null, null);
        if(rowSetData!=null){
            String rows ="";
            String coloumn ="";
            for(FormRow rowData : rowSetData){
                if(!rows.contains(rowData.getProperty("namaBuah"))) {
                    dataList.addBinderProperty("rows", rowData.getProperty("namaBuah"));
                    //dataList.
                    rows+=rowData.getProperty("namaBuah");
                    rows+=";";
                }
                if(!coloumn.contains(rowData.getProperty("rasaBuah"))) {
                    dataList.addBinderProperty("coloumn", rowData.getProperty("rasaBuah"));
                    coloumn+=rowData.getProperty("rasaBuah");
                    coloumn+=";";
                }
                    *//*for(FormRow rows : rowSetData) {
                        dataList.addBinderProperty("coloumn", rows.getProperty(getPropertyString("pivotColoumnId")));

                    }*//*
            }
            //LogUtil.warn(getClass().getName(),dataList.toString());
            return  dataList;
        }
        return null;
    }*/

   /* private DataList getDataList(String datalistId) {
        ApplicationContext ac     = AppUtil.getApplicationContext();
        AppDefinition      appDef = AppUtil.getCurrentAppDefinition();

        if (datalistCache.containsKey(datalistId))
            return datalistCache.get(datalistId);

        DataListService       dataListService       = (DataListService) ac.getBean("dataListService");
        DatalistDefinitionDao datalistDefinitionDao = (DatalistDefinitionDao) ac.getBean("datalistDefinitionDao");
        DatalistDefinition    datalistDefinition    = (DatalistDefinition) datalistDefinitionDao.loadById(datalistId, appDef);
        if (datalistDefinition != null) {
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            datalistCache.put(datalistId, dataList);
            return dataList;
        }
        return null;
    }*/
}
