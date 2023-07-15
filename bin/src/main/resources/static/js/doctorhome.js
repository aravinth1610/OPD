$.get("innerprocess/doctordataid/",function(resultDoctorID){
//	console.log(resultDoctorID);
	$("#hospitalid").html(resultDoctorID.content)
	
})