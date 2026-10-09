package com.kinnarastudio.kecakplugins.pivottable.userview;

import com.kinnarastudio.commons.Declutter;
import com.kinnarastudio.commons.Try;
import com.kinnarastudio.commons.jsonstream.JSONCollectors;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.*;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataListPivotTable extends UserviewMenu implements Declutter {
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
        return getRenderPage("/templates/pivotTable.ftl");
    }

    @Override
    public boolean isHomePageSupported() {
        return true;
    }

    @Override
    public String getDecoratedMenu() {
        return null;
    }

    @Override
    public String getName() {
        return getLabel();
    }

    @Override
    public String getVersion() {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        ResourceBundle resourceBundle = pluginManager.getPluginMessageBundle(getClassName(), "/messages/BuildNumber");
        String buildNumber = resourceBundle.getString("buildNumber");
        return buildNumber;
    }

    @Override
    public String getDescription() {
        return getClass().getPackage().getImplementationTitle();
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
        return AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", null, true, "/messages/pivotTable");
    }

    protected DataList getDataList(String datalistId) {
        ApplicationContext ac = AppUtil.getApplicationContext();
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();

        DataListService dataListService = (DataListService) ac.getBean("dataListService");
        DatalistDefinitionDao datalistDefinitionDao = (DatalistDefinitionDao) ac.getBean("datalistDefinitionDao");
        DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(datalistId, appDef);
        if (datalistDefinition != null) {
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            return dataList;
        }
        return null;
    }

    protected void getCollectFilters(DataList dataList, Map<String, Object> requestParameters) {
        DataListColumn[] columns = dataList.getColumns();
        if (columns == null || requestParameters == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            String paramName = entry.getKey();
            if (paramName == null) {
                continue;
            }

            boolean isColumnMatch = Arrays.stream(columns)
                    .filter(Objects::nonNull)
                    .map(DataListColumn::getName)
                    .filter(Objects::nonNull)
                    .anyMatch(paramName::equals);

            if (isColumnMatch) {
                try {
                    // parameter is one of the filter
                    DataListFilterQueryObject filter = new DataListFilterQueryObject();
                    filter.setOperator("AND");
                    // this is the default pattern of datalist filter query is "lower([field]) like lower(?)"
                    filter.setQuery("lower(" + paramName + ") like lower(?)");
                    if (entry.getValue() instanceof String[]) {
                        String[] parameterValues = (String[]) entry.getValue();
                        String[] values = new String[parameterValues.length];
                        for (int i = 0, size = parameterValues.length; i < size; i++) {
                            // this is the default pattern of datalist filter value is %[value]%
                            values[i] = "%" + parameterValues[i] + "%";
                        }
                        filter.setValues(values);
                    } else {
                        filter.setValues(new String[]{"%" + String.valueOf(entry.getValue()) + "%"});
                    }
                    dataList.addFilterQueryObject(filter);
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Error creating filter [" + paramName + "]");
                }
            }
        }
    }

    /**
     * Render page using template
     *
     * @param templatePath Path to FTL template file
     * @return
     */
    protected String getRenderPage(String templatePath) {
        Map<String, Object> dataModel = new HashMap<>();

        ApplicationContext appContext = AppUtil.getApplicationContext();
        PluginManager pluginManager = (PluginManager) appContext.getBean("pluginManager");

        dataModel.put("className", getClassName());

        String elementName = getPropertyString("id");
        dataModel.put("elementName", elementName);

        // Put default values to prevent FreeMarker exceptions
        dataModel.put("dataListId", "");
        dataModel.put("data", new JSONArray());
        dataModel.put("showDataListFilter", false);
        dataModel.put("filterTemplates", new String[0]);
        dataModel.put("isDataEmpty", true);

        DataList dataList = getDataList(getPropertyString("dataListId"));
        if (dataList != null) {
            getCollectFilters(dataList, ((Map<String, Object>) getRequestParameters()));
            
            // Check if collection is empty BEFORE generating rows
            DataListCollection<?> dataListCollection = dataList.getRows();
            boolean isEmpty = dataListCollection == null || dataListCollection.isEmpty();
            dataModel.put("isDataEmpty", isEmpty);

            JSONArray data = getRowsAsJson(dataList);

            dataModel.put("data", data);
            dataModel.put("dataListId", dataList.getId());

            // filter template
            List<String> filterTemplates = new ArrayList<>();
            Pattern pagePattern = Pattern.compile("id='d-[0-9]+-p'|id='d-[0-9]+-ps'");
            
            String[] templates = dataList.getFilterTemplates();
            if (templates != null) {
                for (String filterTemplate : templates) {
                    if (filterTemplate != null) {
                        Matcher m = pagePattern.matcher(filterTemplate);
                        if (!m.find()) {
                            filterTemplates.add(filterTemplate);
                        }
                    }
                }
            }

            dataModel.put("filterTemplates", filterTemplates.toArray(new String[0]));
            
            DataListFilter[] filters = dataList.getFilters();
            dataModel.put("showDataListFilter", filters != null && filters.length > 0);
        }

        String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), templatePath, null);
        return htmlContent;
    }

    @Nonnull
    protected Map<String, Object> formatRow(@Nonnull DataList dataList, @Nonnull Map<String, Object> row) {
        Map<String, Object> formattedRow = new HashMap<>();

        Optional.of(dataList)
                .map(DataList::getColumns)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .filter(Try.toNegate(DataListColumn::isHidden))
                .forEach(c -> {
                    String label = c.getLabel();
                    if (label != null) {
                        formattedRow.put(label, formatValue(dataList, row, c.getName()));
                    }
                });

        String primaryKeyColumn = getPrimaryKeyColumn(dataList);
        Object primaryKeyValue = row.get(primaryKeyColumn);
        if (primaryKeyValue != null) {
            formattedRow.putIfAbsent("_" + FormUtil.PROPERTY_ID, primaryKeyValue);
        }

        return formattedRow;
    }

    /**
     * Get Primary Key
     *
     * @param dataList
     * @return
     */
    @Nonnull
    protected String getPrimaryKeyColumn(@Nonnull final DataList dataList) {
        return Optional.of(dataList)
                .map(DataList::getBinder)
                .map(DataListBinder::getPrimaryKeyColumnName)
                .orElse("id");
    }

    /**
     * Format
     *
     * @param dataList DataList
     * @param row      Row
     * @param field    Field
     * @return
     */
    @Nonnull
    protected String formatValue(@Nonnull final DataList dataList, @Nonnull final Map<String, Object> row, String field) {
        String value = Optional.ofNullable(field)
                .map(row::get)
                .map(String::valueOf)
                .orElse("");

        if (field == null) {
            return value;
        }

        return Optional.of(dataList)
                .map(DataList::getColumns)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .filter(c -> field.equals(c.getName()))
                .findFirst()
                .map(column -> Optional.of(column)
                        .map(DataListColumn::getFormats)
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(f -> f.format(dataList, column, row, value))
                        .map(s -> s.replaceAll("<[^>]*>", ""))
                        .orElse(value))
                .orElse(value);
    }

    protected JSONArray getRowsAsJson(DataList dataList) {
        final DataListCollection<Map<String, Object>> dataListCollection = Optional.of(dataList)
                .map(DataList::getRows)
                .map(collection -> (DataListCollection<Map<String, Object>>) collection)
                .orElseGet(DataListCollection::new);

        if (dataListCollection.isEmpty()) {
            JSONObject dummyRow = new JSONObject();
            Optional.of(dataList)
                    .map(DataList::getColumns)
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .map(DataListColumn::getLabel)
                    .filter(Objects::nonNull)
                    .forEach(label -> {
                        try {
                            dummyRow.put(label, "");
                        } catch (Exception ignored) {
                        }
                    });

            JSONArray jsonArray = new JSONArray();
            if (dummyRow.length() > 0) {
                jsonArray.put(dummyRow);
            }
            return jsonArray;
        } else {
            return dataListCollection.stream()
                    .filter(Objects::nonNull)
                    // reformat content value
                    .map(row -> formatRow(dataList, row))

                    // collect as JSON
                    .collect(JSONCollectors.toJSONArray());
        }
    }

}
