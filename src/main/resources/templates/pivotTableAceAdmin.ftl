<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot.min.js"></script>



<div class="row">
    <div class="col-md-12">
        <div id="output"></div>
    </div>
</div>
<script>
 $(function(){
        $("#output").pivotUI(
            [
                {color: "blue", shape: "circle",type:"Taram"},
                {color: "red", shape: "triangle",type:"Bla Bla"}
            ],
            {
                rows: ["color"],
                cols: ["shape","type"],
            }
        );
 });

    console.log('${dataListId}');
    console.log(${data});

</script>