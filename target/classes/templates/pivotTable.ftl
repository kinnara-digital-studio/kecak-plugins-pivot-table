<!DOCTYPE html>
<html lang="en">
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js"></script>
</head>
<body>

	<div class="container">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>kolom1</th>
					<th>kolom2</th>
				</tr>
			</thead>
			<tbody>
				<#list data as dt>
				<tr>
					<td>${dt.kolom1}</td>
					<td>${dt.kolom2}</td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>

</body>
</html>