$("#unackbtn").on("click",function()
{
	var doctorDashboard="/opdetiicos/home";
	window.location.replace(doctorDashboard);
})

$("#recipientlogbtn").on("click",function()
{
	var doctorDashboard="/opdetiicos/log";
	window.location.replace(doctorDashboard);
})


var databaseID = $("#hospitalid").text();
 
 $.post("innerprocess/recipientlog/",{databaseName:databaseID},function(callLogResults){
	 
 //console.log(callLogResults)
 var DashboardDetails="";
 let sno=0;
 
	 if (callLogResults.status == 200 && callLogResults.message == "success") {
		
		
		$.each(callLogResults.content.recipientlogcallno,function(logIndex,logResult)
		{
		   sno=sno+1;
		   DashboardDetails+="<tr>";
           DashboardDetails+="<th scope='row'><a href='#'>"+sno+"</a></th>";
           
           
           $.each(callLogResults.content.recipientlogcallno,function(logCallnoIndex,logCallnoResult)
           {
           if(logIndex == logCallnoIndex)
           {
	         
	         DashboardDetails+="<td>"+logCallnoResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientlogdata,function(logContentIndex,logContentResult)
           {
           if(logIndex == logContentIndex)
           {
	         
	         DashboardDetails+="<td>"+logContentResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientlogstarttime,function(startTimeIndex,startTimeResult)
           {
           if(logIndex == startTimeIndex)
           {
	         DashboardDetails+="<td>"+startTimeResult+"</td>";
             } 
              })
              
          $.each(callLogResults.content.recipientlogendtime,function(endTimeIndex,endTimeResult)
           {
           if(logIndex == endTimeIndex)
           {
	         DashboardDetails+="<td>"+endTimeResult+"</td>";
             } 
              })
              
           $.each(callLogResults.content.recipientlogduration,function(durationIndex,durationResult)
           {
           if(logIndex == durationIndex)
           {
	         DashboardDetails+="<td>"+durationResult+"</td>"
             } 
              })
         
   
        	$.each(callLogResults.content.recipientlogendview,function(endviewIndex,endviewResult)
           {
           if(logIndex == endviewIndex)
           {
	         DashboardDetails+="<td>"+endviewResult+"</td>"
             } 
              })
            DashboardDetails+="</tr>";
      	
      	})
      	$("#recipientlogdata").html(DashboardDetails);
		
		 $('.mytable').DataTable();
      	};
      	})