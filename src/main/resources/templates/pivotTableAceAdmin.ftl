<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot.min.js"></script>

<#-- Sample Data -->
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/dist/tips_data.min.js"></script>
<#-- End of sample data -->
<#--<div class="row">
    <div class="col-md-12">
		<table class="table table-striped">
			<thead>
			    <tr>
			        <th colspan= "1" rowspan="1"></th>
			        <th>${coloum}</th>
                        <#list dataColoumn as dat>
                            <#if dat.coloumn?? && dat.coloumn !=''>
                        		<th colspan="1" rowspan="2">${dat.coloumn}</th>
                        	</#if>
                        </#list>
				        <th rowspan ="2">Total</th>
				</tr>
				<tr>
				    <th>${row}</th>
				    <th></th>
				</tr>
			</thead>

			<tbody>
			    <#list dataRow as dt>
                    <tr>
                        <td rowspan = "1" colspan ="2">${dt.rows}</td>
                                <#list dataValue as dv>
                                    <td>${dv.rowManis}</td>
            		             </#list>
            		    <td>${totalBTC}</td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
</div>-->

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