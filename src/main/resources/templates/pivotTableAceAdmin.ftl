<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot.min.js"></script>



<div class="row">
    <div class="col-md-12">
        <div id="output"></div>
    </div>
</div>

<script>
    let arrData = [];
    <#list data as dt>
    <#if myOptionalVar??>
        let label = ${dt.label}
     </#if>
    </#list>
    arrData.push(label)
    $.pivotUtilities.tipsData = arrData;
</script>

<script type="text/javascript">
$("#output").pivotUI(
  $.pivotUtilities.tipsData, {
    rows: [],
    cols: [],
    vals: [],
    aggregatorName: "",
    rendererName: ""
  });
</script>