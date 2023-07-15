$("#doctorlogout").on("click",function(){
	$.get("innerprocess/doctorlogout/",function(doctorlogoutresult){
		console.log(doctorlogoutresult)
		if(doctorlogoutresult.status == 200 && doctorlogoutresult.message == "success")
		{
			const dashboardnavbtnform="/opdetiicos/doctorlogin";
	         window.location.replace(dashboardnavbtnform)
		}
		
	})
})
