<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot.min.js"></script>

<#-- Sample Data -->
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/dist/tips_data.min.js"></script>
<#-- End of sample data -->

<div class="row">
    <div class="col-md-12">
        <div id="output"></div>
    </div>
</div>

<script type="text/javascript">
$("#output").pivotUI(
  $.pivotUtilities.tipsData, {
    rows: ["sex", "smoker"],
    cols: ["day", "time"],
    vals: ["tip", "total_bill"],
    aggregatorName: "Sum over Sum",
    rendererName: "Heatmap"
  });
</script>